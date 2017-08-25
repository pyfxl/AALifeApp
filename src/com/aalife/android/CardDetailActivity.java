package com.aalife.android;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CardDetailActivity extends Activity {
	private ListView listCardDetail = null;
	private SimpleAdapter adapter = null;
	private List<Map<String, String>> list = null;
	private SQLiteOpenHelper sqlHelper = null;
	private CardTableAccess cardAccess = null;
	private LinearLayout layNoItem = null;
	private final int FIRST_REQUEST_CODE = 1;
	private int cdId = 0;
	private String cdName = "";
    private String curDate = "";
	private ItemTableAccess itemAccess = null;
	private Map<String, String> cdTotal = null;
	private TextView tvTotalZhiChuPrice = null;
	private TextView tvTotalShouRuPrice = null;
	private TextView tvTotalJieCunPrice = null;
	private LinearLayout layCardTotal = null;
	private TextView tvTitleCardDetail = null;
	private int num = 50;
	private int start = 0;
	private int visibleLastIndex = 0;
	private boolean loading = false;
	private ProgressBar pbDay = null;
	private MyHandler myHandler = new MyHandler(this);
	private List<Map<String, String>> newList = null;

	private View myView = null;
	private DatePicker datePicker = null;
	private RadioButton radioAll = null;
	private RadioButton radioYear = null;
	private RadioButton radioMonth = null;
	private String type = "all";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_detail);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvItemName = (TextView) super.findViewById(R.id.tv_title_itemname);
		textPaint = tvItemName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvItemBuyDate = (TextView) super.findViewById(R.id.tv_title_itembuydate);
		textPaint = tvItemBuyDate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvItemPrice = (TextView) super.findViewById(R.id.tv_title_itemprice);
		textPaint = tvItemPrice.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvItemType = (TextView) super.findViewById(R.id.tv_title_itemtype);
		textPaint = tvItemType.getPaint();
		textPaint.setFakeBoldText(true);
		
		tvTotalZhiChuPrice = (TextView) super.findViewById(R.id.tv_total_zhichuprice);
		textPaint = tvTotalZhiChuPrice.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalShouRuPrice = (TextView) super.findViewById(R.id.tv_total_shouruprice);
		textPaint = tvTotalShouRuPrice.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalJieCunPrice = (TextView) super.findViewById(R.id.tv_total_jiecunprice);
		textPaint = tvTotalJieCunPrice.getPaint();
		textPaint.setFakeBoldText(true);

		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//初始化
		listCardDetail = (ListView) super.findViewById(R.id.list_card_detail);
		listCardDetail.setDivider(null);
		layCardTotal = (LinearLayout) super.findViewById(R.id.lay_card_total);
		layCardTotal.setVisibility(View.GONE);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	
		tvTitleCardDetail = (TextView) super.findViewById(R.id.tv_title_card_detail);
		pbDay = (ProgressBar) super.findViewById(R.id.pb_day);
		
		//初始值
		Intent intent = super.getIntent();
		cdId = intent.getIntExtra("cdid", 0);
		cdName = intent.getStringExtra("cdname");
		tvTitleCardDetail.setText(cdName + getString(R.string.txt_tab_card_detail));

		//当前日期
		curDate = UtilityHelper.getCurDate();
		
		//绑定列表
		setListData(curDate, type);

		//列表点击
		listCardDetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String itemBuyDate = map.get("itembuydate");

		        TextView tvItemBuyDate = (TextView) view.findViewById(R.id.tv_day_itembuydate);
		        tvItemBuyDate.setBackgroundColor(CardDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemType = (TextView) view.findViewById(R.id.tv_day_itemtype);
		        tvItemType.setBackgroundColor(CardDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemName = (TextView) view.findViewById(R.id.tv_day_itemname);
		        tvItemName.setBackgroundColor(CardDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemPrice = (TextView) view.findViewById(R.id.tv_day_itemprice);
		        tvItemPrice.setBackgroundColor(CardDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(CardDetailActivity.this, DayDetailActivity.class);
				intent.putExtra("date", itemBuyDate);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//列表滑动事件
		listCardDetail.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int itemsLastIndex = adapter.getCount() - 1;
			    if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == itemsLastIndex) { 
					System.out.println("scrool end.");
					if(!loading) {
				    	pbDay.setVisibility(View.VISIBLE);
						loading = true;
						start += num;
						
						new Thread(new Runnable(){
							@Override
							public void run() {
								itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
								newList = itemAccess.findCardDetailList(cdId, num, start, curDate, type);
								itemAccess.close();

								if(newList.size() > 0) {
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
				CardDetailActivity.this.setResult(Activity.RESULT_OK);
				CardDetailActivity.this.close();
			}			
		});

		//日期按钮
		ImageButton btnTitleDate = (ImageButton) super.findViewById(R.id.btn_title_date);
		btnTitleDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				myView = LayoutInflater.from(CardDetailActivity.this).inflate(R.layout.layout_zhuanti, new LinearLayout(CardDetailActivity.this), false);
				datePicker = (DatePicker) myView.findViewById(R.id.zhuanti_datepicker);
				int[] dates = UtilityHelper.getDateArray(curDate);
				datePicker.updateDate(dates[0], dates[1], dates[2]);

				radioAll = (RadioButton) myView.findViewById(R.id.radio_all);
				radioYear = (RadioButton) myView.findViewById(R.id.radio_year);
				radioMonth = (RadioButton) myView.findViewById(R.id.radio_month);
				if(type.equals("year")) {
					radioYear.setChecked(true);
				} else if(type.equals("month")) {
					radioMonth.setChecked(true);
				} else {
					radioAll.setChecked(true);
				}
				
				Dialog dialog = new AlertDialog.Builder(CardDetailActivity.this)
				    .setTitle(R.string.txt_zhuanti_search)
					.setView(myView)
					.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {							
							curDate = UtilityHelper.formatDate(datePicker.getYear() + "-" + (datePicker.getMonth()+1) + "-" + datePicker.getDayOfMonth(), "yyyy-MM-dd");

							if(radioYear.isChecked()) {
							    type = "year";
							} else if (radioMonth.isChecked()) {
								type = "month";
							} else {
								type = "all";
							}
							
							setListData(curDate, type);
						}
					}).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					}).create();
				dialog.show();
			}			
		});
	}

	//绑定列表
	protected void setListData(String date, String type) {
		start = 0;
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findCardDetailList(cdId, num, start, date, type);
		itemAccess.close();
		if(list.size() > 0) {
			layCardTotal.setVisibility(View.VISIBLE);
			layNoItem.setVisibility(View.GONE);
		} else {
			layCardTotal.setVisibility(View.GONE);
			layNoItem.setVisibility(View.VISIBLE);
		}
		adapter = new SimpleAdapter(this, list, R.layout.list_zhuanti_show, new String[] { "itembuydatetext", "itemname", "itemtype", "itemprice" }, new int[] { R.id.tv_day_itembuydate, R.id.tv_day_itemname, R.id.tv_day_itemtype, R.id.tv_day_itemprice });
		listCardDetail.setAdapter(adapter);
		
		if(list.size() > 0) {
			//绑定总计
			cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase());
			cdTotal = cardAccess.findCardById(cdId, date, type);
			cardAccess.close();
			tvTotalShouRuPrice.setText(cdTotal.get("cdshouru"));
			tvTotalZhiChuPrice.setText(cdTotal.get("cdzhichu"));
			tvTotalJieCunPrice.setText(cdTotal.get("cdjiecun"));
		}
	}
	
	//关闭this
	protected void close() {
		this.finish();
	}

	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CardDetailActivity.this.setResult(Activity.RESULT_OK);
			CardDetailActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			this.onCreate(null);
		}
	}

	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<CardDetailActivity> myActivity = null;
		MyHandler(CardDetailActivity activity) {
			myActivity = new WeakReference<CardDetailActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			final CardDetailActivity activity = myActivity.get();
			switch(msg.what) {
			case 2:
				Iterator<Map<String, String>> it = activity.newList.iterator();
				while(it.hasNext()) {
					activity.list.add(it.next());
				}
				activity.adapter.notifyDataSetChanged();
				
				activity.pbDay.setVisibility(View.GONE);
				activity.loading = false;
				
				break;
			case 3:				
				activity.pbDay.setVisibility(View.GONE);
				
				break;
			}
		}			
	};	
}
