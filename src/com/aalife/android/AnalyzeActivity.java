package com.aalife.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AnalyzeActivity extends Activity {
	private ViewPager viewPager = null;
	private ViewPagerAdapter viewPagerAdapter = null;
	private LayoutInflater mInflater = null;
	private List<View> viewPagerList = null;
	private View layAnalyzeCompare = null;
	private View layAnalyzeShouZhi = null;
	private View layAnalyzeJieHuan = null;
	private View layAnalyzeTongji = null;
	private ImageButton btnTitleDate = null;
	private ImageButton btnTitleAdd = null;
	private ImageButton btnTitleRefresh = null;
	private String strTitle = "";
	private int curView = 0;

	private WebView webViewTongji = null;
	private ProgressBar webViewLoading = null;
	private int viewType = 1;
	
	private ListView listAnalyzeCompare = null;
	private ListView listAnalyzeShouZhi = null;
	private ListView listAnalyzeJieHuan = null;
	private List<Map<String, String>> list = null;
	private SimpleAdapter adapter = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ItemTableAccess itemAccess = null;
	private String curDate = "";
	private TextView tvNavCompare = null;
	private TextView tvNavShouZhi = null;
	private TextView tvNavJieHuan = null;
	private TextView tvNavTongji = null;
	private TextView tvTitleAnalyze = null;
	private LinearLayout layNoItemCompare = null;
	private LinearLayout layCompareTotal = null;
	private LinearLayout layNoItemShouZhi = null;
	private LinearLayout layShouZhiTotal = null;
	private LinearLayout layNoItemJieHuan = null;
	private LinearLayout layJieHuanTotal = null;
	private final int FIRST_REQUEST_CODE = 1;

	private TextView tvTotalShouRu = null;
	private TextView tvTotalZhiChu = null;
	private TextView tvTotalJieCun = null;
	private TextView tvTotalJie = null;
	private TextView tvTotalHuan = null;
	private TextView tvTotalCurPrice = null;
	private TextView tvTotalPrevPrice = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze);

		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//定义ViewPager
		viewPagerList = new ArrayList<View>();
		mInflater = getLayoutInflater();
		layAnalyzeCompare = mInflater.inflate(R.layout.analyze_compare, new LinearLayout(this), false);
		layAnalyzeShouZhi = mInflater.inflate(R.layout.analyze_shouzhi, new LinearLayout(this), false);
		layAnalyzeJieHuan = mInflater.inflate(R.layout.analyze_jiehuan, new LinearLayout(this), false);
		layAnalyzeTongji = mInflater.inflate(R.layout.analyze_tongji, new LinearLayout(this), false);	
		
        viewPagerList.add(layAnalyzeCompare);
        viewPagerList.add(layAnalyzeShouZhi);
        viewPagerList.add(layAnalyzeJieHuan);
        viewPagerList.add(layAnalyzeTongji);
		viewPager = (ViewPager) super.findViewById(R.id.viewPager);
        
		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleCatName = (TextView) layAnalyzeCompare.findViewById(R.id.tv_title_catname);
		textPaint = tvTitleCatName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleShouPriceCur = (TextView) layAnalyzeCompare.findViewById(R.id.tv_title_shoupricecur);
		textPaint = tvTitleShouPriceCur.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleZhiPriceCur = (TextView) layAnalyzeCompare.findViewById(R.id.tv_title_zhipricecur);
		textPaint = tvTitleZhiPriceCur.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleShouPricePrev = (TextView) layAnalyzeCompare.findViewById(R.id.tv_title_shoupriceprev);
		textPaint = tvTitleShouPricePrev.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleZhiPricePrev = (TextView) layAnalyzeCompare.findViewById(R.id.tv_title_zhipriceprev);
		textPaint = tvTitleZhiPricePrev.getPaint();
		textPaint.setFakeBoldText(true);

		TextView tvTitleItemBuyDate = (TextView) layAnalyzeShouZhi.findViewById(R.id.tv_title_itembuydate);
		textPaint = tvTitleItemBuyDate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleShouRu = (TextView) layAnalyzeShouZhi.findViewById(R.id.tv_title_shouru);
		textPaint = tvTitleShouRu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleZhiChu = (TextView) layAnalyzeShouZhi.findViewById(R.id.tv_title_zhichu);
		textPaint = tvTitleZhiChu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleJieCun = (TextView) layAnalyzeShouZhi.findViewById(R.id.tv_title_jiecun);
		textPaint = tvTitleJieCun.getPaint();
		textPaint.setFakeBoldText(true);
		
		tvTitleItemBuyDate = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_title_itembuydate);
		textPaint = tvTitleItemBuyDate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleJieChu = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_title_jiechu);
		textPaint = tvTitleJieChu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleHuanRu = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_title_huanru);
		textPaint = tvTitleHuanRu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleJieRu = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_title_jieru);
		textPaint = tvTitleJieRu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleHuanChu = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_title_huanchu);
		textPaint = tvTitleHuanChu.getPaint();
		textPaint.setFakeBoldText(true);
		
        //初始化
		curDate = UtilityHelper.getCurDate();
		tvTitleAnalyze = (TextView) super.findViewById(R.id.tv_title_analyze);
		strTitle = getString(R.string.txt_tab_analyzecompare);
		layNoItemCompare = (LinearLayout) layAnalyzeCompare.findViewById(R.id.lay_noitem);
		layCompareTotal = (LinearLayout) layAnalyzeCompare.findViewById(R.id.lay_analyze_total);
		layNoItemShouZhi = (LinearLayout) layAnalyzeShouZhi.findViewById(R.id.lay_noitem);
		layShouZhiTotal = (LinearLayout) layAnalyzeShouZhi.findViewById(R.id.lay_analyze_total);
		layNoItemJieHuan = (LinearLayout) layAnalyzeJieHuan.findViewById(R.id.lay_noitem);
		layJieHuanTotal = (LinearLayout) layAnalyzeJieHuan.findViewById(R.id.lay_analyze_total);
		layNoItemCompare.setVisibility(View.GONE);
		layCompareTotal.setVisibility(View.GONE);
		layNoItemShouZhi.setVisibility(View.GONE);
		layShouZhiTotal.setVisibility(View.GONE);
		layNoItemJieHuan.setVisibility(View.GONE);
		layJieHuanTotal.setVisibility(View.GONE);

		tvTotalShouRu = (TextView) layAnalyzeShouZhi.findViewById(R.id.tv_total_shouruprice);
		tvTotalZhiChu = (TextView) layAnalyzeShouZhi.findViewById(R.id.tv_total_zhichuprice);
		tvTotalJieCun = (TextView) layAnalyzeShouZhi.findViewById(R.id.tv_total_jiecunprice);
		tvTotalJie = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_total_jieprice);
		tvTotalHuan = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_total_huanprice);
		tvTotalCurPrice = (TextView) layAnalyzeCompare.findViewById(R.id.tv_total_curprice);
		tvTotalPrevPrice = (TextView) layAnalyzeCompare.findViewById(R.id.tv_total_prevprice);
		textPaint = tvTotalShouRu.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = tvTotalZhiChu.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = tvTotalJieCun.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = tvTotalJie.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = tvTotalHuan.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = tvTotalCurPrice.getPaint();
		textPaint.setFakeBoldText(true);
		textPaint = tvTotalPrevPrice.getPaint();
		textPaint.setFakeBoldText(true);

		TextView tvTotalLabel = (TextView) layAnalyzeJieHuan.findViewById(R.id.tv_total_label);
		textPaint = tvTotalLabel.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalLabel = (TextView) layAnalyzeCompare.findViewById(R.id.tv_total_label);
		textPaint = tvTotalLabel.getPaint();
		textPaint.setFakeBoldText(true);
		
		//WebView
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int screenDpi = dm.densityDpi;
		int screenWidth = dm.widthPixels;		
		if(screenDpi == 160 && screenWidth == 360) {
			viewType = 1;
		} else if(screenDpi <= 160 && screenWidth > 360) {
			viewType = 2;
		}
		webViewLoading = (ProgressBar) layAnalyzeTongji.findViewById(R.id.webViewLoading);
		webViewTongji = (WebView) layAnalyzeTongji.findViewById(R.id.webViewTongji);
		setWebView();
		
        //定义分类比较ListView
		listAnalyzeCompare = (ListView) layAnalyzeCompare.findViewById(R.id.list_analyzecompare);
		listAnalyzeCompare.setDivider(null);		
		listAnalyzeCompare.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        int catId = Integer.parseInt(map.get("catid"));
		        
		        TextView tvCatName = (TextView) view.findViewById(R.id.tv_analyze_catname);
		        tvCatName.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvShouPriceCur = (TextView) view.findViewById(R.id.tv_analyze_shoupricecur);
		        tvShouPriceCur.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvZhiPriceCur = (TextView) view.findViewById(R.id.tv_analyze_zhipricecur);
		        tvZhiPriceCur.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvShouPricePrev = (TextView) view.findViewById(R.id.tv_analyze_shoupriceprev);
		        tvShouPricePrev.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvZhiPricePrev = (TextView) view.findViewById(R.id.tv_analyze_zhipriceprev);
		        tvZhiPricePrev.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(AnalyzeActivity.this, AnalyzeCompareDetailActivity.class);
		        intent.putExtra("catid", catId);
		        intent.putExtra("date", curDate);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//定义收支分析ListView
		listAnalyzeShouZhi = (ListView) layAnalyzeShouZhi.findViewById(R.id.list_analyzeshouzhi);
		listAnalyzeShouZhi.setDivider(null);		
		listAnalyzeShouZhi.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String date = map.get("datevalue");
		        
		        TextView tvItemBuyDate = (TextView) view.findViewById(R.id.tv_analyze_itembuydate);
		        tvItemBuyDate.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvShouRuPrice = (TextView) view.findViewById(R.id.tv_analyze_shouru);
		        tvShouRuPrice.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvZhiChuPrice = (TextView) view.findViewById(R.id.tv_analyze_zhichu);
		        tvZhiChuPrice.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvJieCunPrice = (TextView) view.findViewById(R.id.tv_analyze_jiecun);
		        tvJieCunPrice.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(AnalyzeActivity.this, MonthActivity.class);
		        intent.putExtra("date", date);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//定义借还分析ListView
		listAnalyzeJieHuan = (ListView) layAnalyzeJieHuan.findViewById(R.id.list_analyzejiehuan);
		listAnalyzeJieHuan.setDivider(null);		
		listAnalyzeJieHuan.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String date = map.get("datevalue");

		        TextView tvItemBuyDate = (TextView) view.findViewById(R.id.tv_analyze_itembuydate);
		        tvItemBuyDate.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvJieChuPrice = (TextView) view.findViewById(R.id.tv_analyze_jiechu);
		        tvJieChuPrice.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvHuanRuPrice = (TextView) view.findViewById(R.id.tv_analyze_huanru);
		        tvHuanRuPrice.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvJieRuPrice = (TextView) view.findViewById(R.id.tv_analyze_jieru);
		        tvJieRuPrice.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvHuanChuPrice = (TextView) view.findViewById(R.id.tv_analyze_huanchu);
		        tvHuanChuPrice.setBackgroundColor(AnalyzeActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(AnalyzeActivity.this, AnalyzeJieHuanDetailActivity.class);
		        intent.putExtra("date", date);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
						
		//比较导航
		tvNavCompare = (TextView) super.findViewById(R.id.tv_nav_compare);
		tvNavCompare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				viewPager.setCurrentItem(0);
			}
		});

		//收支导航
		tvNavShouZhi = (TextView) super.findViewById(R.id.tv_nav_shouzhi);
		tvNavShouZhi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				viewPager.setCurrentItem(1);
			}
		});

		//借还导航
		tvNavJieHuan = (TextView) super.findViewById(R.id.tv_nav_jiehuan);
		tvNavJieHuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				viewPager.setCurrentItem(2);
			}
		});
		
		//统计导航
		tvNavTongji = (TextView) super.findViewById(R.id.tv_nav_tongji);
		tvNavTongji.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				viewPager.setCurrentItem(3);
			}
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AnalyzeActivity.this.close();
			}			
		});
		
		//日期按钮
		btnTitleDate = (ImageButton) super.findViewById(R.id.btn_title_date);
		btnTitleDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] array = curDate.split("-");
				DatePickerDialog dateDialog = new MyDatePickerDialog(AnalyzeActivity.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						String date = UtilityHelper.formatDate(year + "-" + (month + 1) + "-" + day, "");
						setListData(date);
					}					
				}, Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]));
				dateDialog.show();
			}		
		});
		
		//添加按钮
		btnTitleAdd = (ImageButton) super.findViewById(R.id.btn_title_add);
		btnTitleAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AnalyzeActivity.this, AddActivity.class);
				intent.putExtra("recommend", 1);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//刷新按钮
		btnTitleRefresh = (ImageButton) super.findViewById(R.id.btn_title_refresh);
		btnTitleRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setWebView();
			}			
		});
				
		//页面滑动事件
        viewPagerAdapter = new ViewPagerAdapter();
		viewPager.setAdapter(viewPagerAdapter);	
        viewPager.setCurrentItem(0);        
        tvNavCompare.setTextColor(this.getResources().getColor(R.color.color_back_main));
        tvNavCompare.setBackgroundResource(R.drawable.nav_border_cur);
		int padding = AnalyzeActivity.this.getResources().getDimensionPixelSize(R.dimen.title_text_padding);
		tvNavCompare.setPadding(padding, padding, padding, padding);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				int pad = AnalyzeActivity.this.getResources().getDimensionPixelSize(R.dimen.title_text_padding);
				tvNavCompare.setTextColor(AnalyzeActivity.this.getResources().getColor(android.R.color.black));
				tvNavCompare.setBackgroundResource(R.drawable.nav_border_sub);
				tvNavCompare.setPadding(pad, pad, pad, pad);
				tvNavShouZhi.setTextColor(AnalyzeActivity.this.getResources().getColor(android.R.color.black));
				tvNavShouZhi.setBackgroundResource(R.drawable.nav_border_sub);
				tvNavShouZhi.setPadding(pad, pad, pad, pad);
				tvNavJieHuan.setTextColor(AnalyzeActivity.this.getResources().getColor(android.R.color.black));
				tvNavJieHuan.setBackgroundResource(R.drawable.nav_border_sub);
				tvNavJieHuan.setPadding(pad, pad, pad, pad);
				tvNavTongji.setTextColor(AnalyzeActivity.this.getResources().getColor(android.R.color.black));
				tvNavTongji.setBackgroundResource(R.drawable.nav_border_sub);
				tvNavTongji.setPadding(pad, pad, pad, pad);
				switch(arg0) {
					case 0:
						tvNavCompare.setTextColor(AnalyzeActivity.this.getResources().getColor(R.color.color_back_main));
						tvNavCompare.setBackgroundResource(R.drawable.nav_border_cur);
						tvNavCompare.setPadding(pad, pad, pad, pad);
						
						tvTitleAnalyze.setText(getString(R.string.txt_tab_analyzecompare) + "(" + UtilityHelper.formatDate(curDate, "ys-m") + ")");
						strTitle = getString(R.string.txt_tab_analyzecompare);
						curView = 0;
						
						btnTitleDate.setVisibility(View.VISIBLE);
						btnTitleAdd.setVisibility(View.GONE);
						btnTitleRefresh.setVisibility(View.GONE);
						tvTitleAnalyze.setVisibility(View.VISIBLE);
						
						break;
					case 1:
						tvNavShouZhi.setTextColor(AnalyzeActivity.this.getResources().getColor(R.color.color_back_main));
						tvNavShouZhi.setBackgroundResource(R.drawable.nav_border_cur);
						tvNavShouZhi.setPadding(pad, pad, pad, pad);
						
						tvTitleAnalyze.setText(getString(R.string.txt_tab_analyzeshouzhi) + "(" + UtilityHelper.formatDate(curDate, "y") + ")");
						strTitle = getString(R.string.txt_tab_analyzeshouzhi);
						curView = 1;
						
						btnTitleDate.setVisibility(View.VISIBLE);
						btnTitleAdd.setVisibility(View.GONE);
						btnTitleRefresh.setVisibility(View.GONE);
						tvTitleAnalyze.setVisibility(View.VISIBLE);
						
						break;
					case 2:
						tvNavJieHuan.setTextColor(AnalyzeActivity.this.getResources().getColor(R.color.color_back_main));
						tvNavJieHuan.setBackgroundResource(R.drawable.nav_border_cur);
						tvNavJieHuan.setPadding(pad, pad, pad, pad);
						
						tvTitleAnalyze.setText(getString(R.string.txt_tab_analyzejiehuan) + "(" + UtilityHelper.formatDate(curDate, "y") + ")");
						strTitle = getString(R.string.txt_tab_analyzejiehuan);
						curView = 2;
						
						btnTitleDate.setVisibility(View.VISIBLE);
						btnTitleAdd.setVisibility(View.GONE);
						btnTitleRefresh.setVisibility(View.GONE);
						tvTitleAnalyze.setVisibility(View.VISIBLE);
											
						break;
					case 3:
						tvNavTongji.setTextColor(AnalyzeActivity.this.getResources().getColor(R.color.color_back_main));
						tvNavTongji.setBackgroundResource(R.drawable.nav_border_cur);
						tvNavTongji.setPadding(pad, pad, pad, pad);
						
						tvTitleAnalyze.setText(getString(R.string.txt_tab_analyzetongji));
						strTitle = getString(R.string.txt_tab_analyzetongji);
						curView = 3;
						
						btnTitleDate.setVisibility(View.GONE);
						btnTitleAdd.setVisibility(View.GONE);
						btnTitleRefresh.setVisibility(View.VISIBLE);
						tvTitleAnalyze.setVisibility(View.VISIBLE);
						
						break;
				}
			}			
		});
		
		setListData(curDate);
		//setSearchData(key);
	}
	
	//加载WebView
	protected void setWebView() {
		webViewTongji.loadUrl(UtilityHelper.getTongJiURL(viewType));
		webViewTongji.setWebViewClient(new WebViewClient(){
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				view.loadDataWithBaseURL(null, getString(R.string.txt_home_neterror), "text/html", "UTF-8", null);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				webViewLoading.setVisibility(ProgressBar.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webViewLoading.setVisibility(ProgressBar.VISIBLE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}	
		});
	}

	//关闭this
	protected void close() {
		this.finish();
	}

	//设置ListView	
	protected void setListData(String date) {
		curDate = date;
		String strDate = "(" + UtilityHelper.formatDate(curDate, "ys-m") + ")";
		if(curView == 1 || curView == 2) {
			strDate = "(" + UtilityHelper.formatDate(curDate, "y") + ")";
		}
		tvTitleAnalyze.setText(strTitle + strDate);
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		
		//比较
		list = itemAccess.findCompareCatByDate(date);
		adapter = new SimpleAdapter(this, list, R.layout.list_analyzecompare, new String[] { "catid", "catname", "shoupricecur", "zhipricecur", "shoupriceprev", "zhipriceprev" }, new int[] { R.id.tv_analyze_catid, R.id.tv_analyze_catname, R.id.tv_analyze_shoupricecur, R.id.tv_analyze_zhipricecur, R.id.tv_analyze_shoupriceprev, R.id.tv_analyze_zhipriceprev });
		listAnalyzeCompare.setAdapter(adapter);
		if(list.size() <= 0) {
			layNoItemCompare.setVisibility(View.VISIBLE);
			layCompareTotal.setVisibility(View.GONE);
		} else {
			layNoItemCompare.setVisibility(View.GONE);
			layCompareTotal.setVisibility(View.VISIBLE);

			double curshou = 0;
			double curzhi = 0;
			double curprice = 0;
			double prevshou = 0;
			double prevzhi = 0;
			double prevprice = 0;
			for(int i=0; i<list.size(); i++) {
				Map<String, String> map = list.get(i);
				curshou += Double.parseDouble(map.get("shoupricecur"));
				curzhi += Double.parseDouble(map.get("zhipricecur"));
				prevshou += Double.parseDouble(map.get("shoupriceprev"));
				prevzhi += Double.parseDouble(map.get("zhipriceprev"));
			}
			curprice = curshou - curzhi;
			prevprice = prevshou - prevzhi;
			
			tvTotalCurPrice.setText(getString(R.string.txt_analyze_curprice) + "  " + UtilityHelper.formatDouble(curprice, "0.0##"));
			tvTotalPrevPrice.setText(getString(R.string.txt_analyze_prevprice) + "  " + UtilityHelper.formatDouble(prevprice, "0.0##"));
		}

		//收支
		list = itemAccess.findAnalyzeShouZhi(date);
		adapter = new SimpleAdapter(this, list, R.layout.list_analyzeshouzhi, new String[] { "itembuydate", "shouruvalue", "shouruvalue", "zhichuvalue", "zhichuvalue", "jiecunvalue", "jiecunvalue" }, new int[] { R.id.tv_analyze_itembuydate, R.id.tv_analyze_shouru, R.id.tv_analyze_shouruvalue, R.id.tv_analyze_zhichu, R.id.tv_analyze_zhichuvalue, R.id.tv_analyze_jiecun, R.id.tv_analyze_jiecunvalue });
		listAnalyzeShouZhi.setAdapter(adapter);
		if(list.size() <= 0) {
			layNoItemShouZhi.setVisibility(View.VISIBLE);
			layShouZhiTotal.setVisibility(View.GONE);
		} else {
			layNoItemShouZhi.setVisibility(View.GONE);
			layShouZhiTotal.setVisibility(View.VISIBLE);
			
			double shouru = 0;
			double zhichu = 0;
			double jiecun = 0;
			for(int i=0; i<list.size(); i++) {
				Map<String, String> map = list.get(i);
				shouru += Double.parseDouble(map.get("shouruvalue"));
				zhichu += Double.parseDouble(map.get("zhichuvalue"));
			}
			jiecun = shouru - zhichu;
			
			tvTotalShouRu.setText(getString(R.string.txt_month_shou) + "  " + UtilityHelper.formatDouble(shouru, "0.0##"));
			tvTotalZhiChu.setText(getString(R.string.txt_month_zhi) + "  " + UtilityHelper.formatDouble(zhichu, "0.0##"));
			tvTotalJieCun.setText(getString(R.string.txt_month_cun) + "  " + UtilityHelper.formatDouble(jiecun, "0.0##"));
		}
		
		//借还
		list = itemAccess.findAnalyzeJieHuan(date);
		adapter = new SimpleAdapter(this, list, R.layout.list_analyzejiehuan, new String[] { "itembuydate", "jiechuprice", "huanruprice", "jieruprice", "huanchuprice" }, new int[] { R.id.tv_analyze_itembuydate, R.id.tv_analyze_jiechu, R.id.tv_analyze_huanru, R.id.tv_analyze_jieru, R.id.tv_analyze_huanchu });
		listAnalyzeJieHuan.setAdapter(adapter);
		if(list.size() <= 0) {
			layNoItemJieHuan.setVisibility(View.VISIBLE);
			layJieHuanTotal.setVisibility(View.GONE);
		} else {
			layNoItemJieHuan.setVisibility(View.GONE);
			layJieHuanTotal.setVisibility(View.VISIBLE);
				
			double jiechu = 0;
			double huanru = 0;
			double jieru = 0;
			double huanchu = 0;
			for(int i=0; i<list.size(); i++) {
				Map<String, String> map = list.get(i);
				jiechu += Double.parseDouble(map.get("jiechuprice"));
				huanru += Double.parseDouble(map.get("huanruprice"));
				jieru += Double.parseDouble(map.get("jieruprice"));
				huanchu += Double.parseDouble(map.get("huanchuprice"));
			}
			
			tvTotalJie.setText(getString(R.string.txt_analyze_weihuan) + "  " + UtilityHelper.formatDouble(jiechu-huanru, "0.##"));
			tvTotalHuan.setText(getString(R.string.txt_analyze_qianhuan) + "  " + UtilityHelper.formatDouble(jieru-huanchu, "0.##"));
		}
		itemAccess.close();
	}
			
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			setListData(curDate);
		}
	}
	
	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(viewPagerList.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public Object instantiateItem(ViewGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(viewPagerList.get(arg1),0);
			return viewPagerList.get(arg1);
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==(arg1);
		}
		
	}
}
