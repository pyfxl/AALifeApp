package com.aalife.android;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DayActivity extends Activity {
	private ListView lvDayList = null;
	private List<Map<String, String>> list = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ItemTableAccess itemAccess = null;
	private DayAdapter dayAdapter = null;
	private MyHandler myHandler = new MyHandler(this);
	private ProgressBar pbDay = null;
	private LinearLayout layNoItem = null;
	private String curDate = "";
	private int visibleLastIndex = 0;
	private boolean loading = false;
	private List<Map<String, String>> newList = null;
	private TextView tvTotalLabel = null;
	private TextView tvTotalShouRu = null;
	private TextView tvTotalZhiChu = null;
	private TextView tvTotalJieCun = null;
	private LinearLayout layDayTotal = null;
	private final int FIRST_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day);

		//标题变粗
		TextPaint textPaint = null;
		tvTotalLabel = (TextView) super.findViewById(R.id.tv_total_label);
		textPaint = tvTotalLabel.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalShouRu = (TextView) super.findViewById(R.id.tv_total_shouru);
		textPaint = tvTotalShouRu.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalZhiChu = (TextView) super.findViewById(R.id.tv_total_zhichu);
		textPaint = tvTotalZhiChu.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalJieCun = (TextView) super.findViewById(R.id.tv_total_jiecun);
		textPaint = tvTotalJieCun.getPaint();
		textPaint.setFakeBoldText(true);
		
		//数据库
		sqlHelper = new DatabaseHelper(this);

		//初始化
		lvDayList = (ListView) super.findViewById(R.id.list_day);
		lvDayList.setDivider(null);
		lvDayList.setFocusable(false);
		pbDay = (ProgressBar) super.findViewById(R.id.pb_day);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	
		layDayTotal = (LinearLayout) super.findViewById(R.id.lay_day_total);
		layDayTotal.setVisibility(View.GONE);	
		
		//初始列表
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		String lastDate = itemAccess.findLastDate();
		list = itemAccess.findAllDayFirstBuyDate(lastDate);
		if(lastDate.equals("")) {
			curDate = UtilityHelper.getCurDate();
		} else {
			curDate = lastDate;
		}
		itemAccess.close();
		dayAdapter = new DayAdapter(this, list);
		lvDayList.setAdapter(dayAdapter);
		if(list.size() <= 0) {
			layNoItem.setVisibility(View.VISIBLE);
		} else {
			layDayTotal.setVisibility(View.VISIBLE);
		}
				
		//列表滑动事件
		lvDayList.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int itemsLastIndex = dayAdapter.getCount() - 1;
			    if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == itemsLastIndex) { 
					if(!loading) {
				    	pbDay.setVisibility(View.VISIBLE);
						loading = true;
						
						new Thread(new Runnable(){
							@Override
							public void run() {
								itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
								String nextDate = itemAccess.findNextDate(curDate);
								newList = itemAccess.findAllDayBuyDate(nextDate);
								itemAccess.close();

								if(newList.size() > 0) {
									curDate = nextDate;
									Message message = new Message();
									message.what = 2;
									myHandler.sendMessage(message);
								} else {
									Message message = new Message();
									message.what = 3;
									myHandler.sendMessage(message);									
								}
							}
						}).start(); 
				    } 
				}
			}					
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DayActivity.this.close();
			}			
		});

		//添加按钮
		ImageButton btnTitleAdd = (ImageButton) super.findViewById(R.id.btn_title_add);
		btnTitleAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(DayActivity.this, AddActivity.class);
				intent.putExtra("date", UtilityHelper.getCurDate());
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
	}

	//关闭this
	protected void close() {
		this.finish();
	}
	
	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<DayActivity> myActivity = null;
		MyHandler(DayActivity activity) {
			myActivity = new WeakReference<DayActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			final DayActivity activity = myActivity.get();
			switch(msg.what) {
			case 2:
				Iterator<Map<String, String>> it = activity.newList.iterator();
				while(it.hasNext()) {
					activity.list.add(it.next());
				}
				activity.dayAdapter.notifyDataSetChanged();
				
				activity.pbDay.setVisibility(View.GONE);
				activity.loading = false;
				
				break;
			case 3:				
				activity.pbDay.setVisibility(View.GONE);
				
				break;
			}
		}			
	};	
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			ItemTableAccess itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
			list = itemAccess.findAllDayFirstBuyDate(curDate);
			itemAccess.close();
			
			//设置empty
			if(list.size() <= 0) {
				layNoItem.setVisibility(View.VISIBLE);
				layDayTotal.setVisibility(View.GONE);	
			} else {
				layNoItem.setVisibility(View.GONE);
				layDayTotal.setVisibility(View.VISIBLE);
			}
			
			if(dayAdapter != null) {
			    dayAdapter.updateData(list);
			}
		}
	}
	
	//Adapter调用刷新
	public void refreshData() {
		dayAdapter.notifyDataSetChanged();
	}
	
	//设置总计
	public void setTotalData(String date, String shouruPrice, String zhichuPrice, String jiecunPrice) {
		tvTotalLabel.setText(UtilityHelper.formatDate(date, "y2-m2"));
		tvTotalShouRu.setText(getString(R.string.txt_month_shou) + " " + shouruPrice);
		tvTotalZhiChu.setText(getString(R.string.txt_month_zhi) + " " + zhichuPrice);
		tvTotalJieCun.setText(getString(R.string.txt_month_cun) + " " + jiecunPrice);
	}
	
}
