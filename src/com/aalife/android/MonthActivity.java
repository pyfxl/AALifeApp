package com.aalife.android;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MonthActivity extends Activity {
	private ViewPager viewPager = null;
	private ViewPagerAdapter viewPagerAdapter = null;
	private Handler handler = new Handler();
	
	private HorizontalScrollView hsvMonthTitle = null;
	private GridView gvMonthTitle = null;
	private SimpleAdapter simpleAdapter = null;
	private int titleCount = 4;
	private int titleWidth = 0;
	private int screenWidth = 0;

	private List<Map<String, String>> listAll = null;
	private SQLiteOpenHelper sqlHelper = null;
	private SharedHelper sharedHelper = null;
	private String curDate = "";
	private int pagerPosition = 0;
	private TextView tvTotalZhiChuPrice = null;
	private TextView tvTotalShouRuPrice = null;
	private TextView tvTotalJieCunPrice = null;
	private TextPaint textPaint = null;
	private LinearLayout layMonthTotal = null;
	private LinearLayout layNoItem = null;
	private final int FIRST_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_month);

		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//定义ViewPager
		viewPager = (ViewPager) super.findViewById(R.id.viewPager);
		
        //水平ListView
        hsvMonthTitle = (HorizontalScrollView) super.findViewById(R.id.hsv_month_title);
        gvMonthTitle = (GridView) super.findViewById(R.id.gv_month_title);
        gvMonthTitle.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				viewPager.setCurrentItem(position);
			}        	
        });
		
		//初始化
		sharedHelper = new SharedHelper(this);			
		Intent intent = super.getIntent();
		curDate = intent.getStringExtra("date");
		if(curDate==null || curDate.equals("")) {
			curDate = sharedHelper.getCurDate();
		}		
		layMonthTotal = (LinearLayout) super.findViewById(R.id.lay_month_total);
		layMonthTotal.setVisibility(View.GONE);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	

		//设置TitleWidth
		DisplayMetrics dm = getResources().getDisplayMetrics();  
		screenWidth = dm.widthPixels;
        titleWidth = screenWidth / titleCount;
		
		//标题变粗
        tvTotalZhiChuPrice = (TextView) super.findViewById(R.id.tv_total_zhichuprice);
		textPaint = tvTotalZhiChuPrice.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalShouRuPrice = (TextView) super.findViewById(R.id.tv_total_shouruprice);
		textPaint = tvTotalShouRuPrice.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalJieCunPrice = (TextView) super.findViewById(R.id.tv_total_jiecunprice);
		textPaint = tvTotalJieCunPrice.getPaint();
		textPaint.setFakeBoldText(true);

		//左右分页
		ItemTableAccess itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		listAll = itemAccess.findAllMonthTitle();
		itemAccess.close();	
		pagerPosition = listAll.size() - 1;
        for(int i=0; i<listAll.size(); i++) {
            Map<String, String> map = listAll.get(i);
            String date = map.get("datevalue");
            if(UtilityHelper.formatDate(curDate, "y-m").equals(UtilityHelper.formatDate(date, "y-m"))) {
            	pagerPosition = i;
            }
        }
		if(listAll.size() > 0) {
			layMonthTotal.setVisibility(View.VISIBLE);
	        viewPagerAdapter = new ViewPagerAdapter(this, listAll);
	        viewPager.setAdapter(viewPagerAdapter);			
	        viewPager.setCurrentItem(pagerPosition);
	        setTotalPrice(listAll.get(pagerPosition));
		} else {
			layNoItem.setVisibility(View.VISIBLE);
		}
        
		//GridView数据源
		simpleAdapter = new SimpleAdapter(this, listAll, R.layout.list_month_title, new String[] { "date" }, new int[] { R.id.tv_nav_main }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				if(parent.getChildCount() == position) {
					TextView tv = (TextView) view.findViewById(R.id.tv_nav_main);
					if(position == pagerPosition) {
						tv.setTextColor(getResources().getColor(R.color.color_back_main));
						tv.setBackgroundResource(R.drawable.nav_border_cur);
					} else {
						tv.setTextColor(getResources().getColor(android.R.color.black));
						tv.setBackgroundResource(R.drawable.nav_border_sub);
					}
				}
				
				return view;
			}						
		};
		gvMonthTitle.setAdapter(simpleAdapter);
		setTitleSize();
		
		//页面滑动事件
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {				
			}

			@Override
			public void onPageSelected(int arg0) {
				Map<String, String> map = listAll.get(arg0);
				String date = map.get("datevalue");
				setTotalPrice(map);
				curDate = date;
				sharedHelper.setCurDate(date);
				
				pagerPosition = viewPager.getCurrentItem();
				
				setTitlePosition();				
				simpleAdapter.notifyDataSetChanged();
			}			
		});
                
		//返回按钮
        ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MonthActivity.this.setResult(Activity.RESULT_OK);
				MonthActivity.this.close();
			}			
		});
		
		//添加按钮
		ImageButton btnTitleAdd = (ImageButton) super.findViewById(R.id.btn_title_add);
		btnTitleAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MonthActivity.this, AddActivity.class);
				intent.putExtra("date", curDate);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//自动定位
		handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            setTitlePosition();
	        }
	    }, 100); 
	}

	//关闭this
	protected void close() {
		this.finish();
	}

	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MonthActivity.this.setResult(Activity.RESULT_OK);
			MonthActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//设置标题尺寸
	protected void setTitleSize() {
		int size = listAll.size();  
		LayoutParams params = gvMonthTitle.getLayoutParams();
		params.width = size * titleWidth;
		params.height = this.getResources().getDimensionPixelSize(R.dimen.list_item_height);
		if(params.width < screenWidth) {
			params.width = screenWidth;
		}
		gvMonthTitle.setLayoutParams(params);
		gvMonthTitle.setNumColumns(size);
	}
	
	//设置总价
	protected void setTotalPrice(Map<String, String> map) {
		double zhichuPrice = Double.parseDouble(map.get("zhichuvalue"));
		double shouruPrice = Double.parseDouble(map.get("shouruvalue"));
		double jiecunPrice = shouruPrice - zhichuPrice;
		tvTotalZhiChuPrice.setText(getString(R.string.txt_month_zhi) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(zhichuPrice, "0.0##"));
		tvTotalShouRuPrice.setText(getString(R.string.txt_month_shou) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(shouruPrice, "0.0##"));
		tvTotalJieCunPrice.setText(getString(R.string.txt_month_cun) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(jiecunPrice, "0.0##"));
	}
	
	//设置当前
	protected void setTitlePosition() {
		int hsvX = (pagerPosition / titleCount) * screenWidth;
		hsvMonthTitle.smoothScrollTo(hsvX, 0);
	}
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if(data != null) {
				curDate = data.getStringExtra("date");
				sharedHelper.setCurDate(curDate);
			}
			this.onCreate(null);
		}
	}
	
}
