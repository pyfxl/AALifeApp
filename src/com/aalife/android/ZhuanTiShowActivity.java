package com.aalife.android;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ZhuanTiShowActivity extends Activity {
	private ListView listZhuanTiShow = null;
	private SimpleAdapter adapter = null;
	private List<Map<String, String>> list = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ZhuanTiTableAccess zhuanTiAccess = null;
	private LinearLayout layNoItem = null;
	private final int FIRST_REQUEST_CODE = 1;
	private int ztId = 0;
	private String ztName = "";
	private String curDate = "";
	private ItemTableAccess itemAccess = null;
	private Map<String, String> ztTotal = null;
	private TextView tvTotalZhiChuPrice = null;
	private TextView tvTotalShouRuPrice = null;
	private TextView tvTotalJieCunPrice = null;
	private LinearLayout layZhuanTiTotal = null;
	private TextView tvTitleZhuanTiShow = null;
	
	private View myView = null;
	private DatePicker datePicker = null;
	private RadioButton radioAll = null;
	private RadioButton radioYear = null;
	private RadioButton radioMonth = null;
	private String type = "all";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhuanti_show);
		
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
		listZhuanTiShow = (ListView) super.findViewById(R.id.list_zhuanti_show);
		listZhuanTiShow.setDivider(null);
		layZhuanTiTotal = (LinearLayout) super.findViewById(R.id.lay_zhuanti_total);
		layZhuanTiTotal.setVisibility(View.GONE);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	
		tvTitleZhuanTiShow = (TextView) super.findViewById(R.id.tv_title_zhuanti_show);
		
		//初始值
		Intent intent = super.getIntent();
		ztId = intent.getIntExtra("ztid", 0);
		ztName = intent.getStringExtra("ztname");

		//当前日期
		curDate = UtilityHelper.getCurDate();
				
		//绑定类别列表
		setListData(curDate, type);	

		//列表点击
		listZhuanTiShow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String itemBuyDate = map.get("itembuydate");

		        TextView tvItemBuyDate = (TextView) view.findViewById(R.id.tv_day_itembuydate);
		        tvItemBuyDate.setBackgroundColor(ZhuanTiShowActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemType = (TextView) view.findViewById(R.id.tv_day_itemtype);
		        tvItemType.setBackgroundColor(ZhuanTiShowActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemName = (TextView) view.findViewById(R.id.tv_day_itemname);
		        tvItemName.setBackgroundColor(ZhuanTiShowActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemPrice = (TextView) view.findViewById(R.id.tv_day_itemprice);
		        tvItemPrice.setBackgroundColor(ZhuanTiShowActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(ZhuanTiShowActivity.this, DayDetailActivity.class);
				intent.putExtra("date", itemBuyDate);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ZhuanTiShowActivity.this.setResult(Activity.RESULT_OK);
				ZhuanTiShowActivity.this.close();
			}			
		});

		//日期按钮
		ImageButton btnTitleDate = (ImageButton) super.findViewById(R.id.btn_title_date);
		btnTitleDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				myView = LayoutInflater.from(ZhuanTiShowActivity.this).inflate(R.layout.layout_zhuanti, new LinearLayout(ZhuanTiShowActivity.this), false);
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
				
				Dialog dialog = new AlertDialog.Builder(ZhuanTiShowActivity.this)
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
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findZhuanTiShowList(ztId, date, type);
		itemAccess.close();
		if(list.size() > 0) {
			layZhuanTiTotal.setVisibility(View.VISIBLE);
			layNoItem.setVisibility(View.GONE);
		} else {
			layZhuanTiTotal.setVisibility(View.GONE);
			layNoItem.setVisibility(View.VISIBLE);
		}
		
		adapter = new SimpleAdapter(this, list, R.layout.list_zhuanti_show, new String[] { "itembuydatetext", "itemname", "itemtype", "itemprice" }, new int[] { R.id.tv_day_itembuydate, R.id.tv_day_itemname, R.id.tv_day_itemtype, R.id.tv_day_itemprice });
		listZhuanTiShow.setAdapter(adapter);
		
		if(list.size() > 0) {
			//绑定总计
			zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
			ztTotal = zhuanTiAccess.findZhuanTiShowById(ztId, date, type);
			zhuanTiAccess.close();
			tvTotalShouRuPrice.setText(ztTotal.get("ztshouru"));
			tvTotalZhiChuPrice.setText(ztTotal.get("ztzhichu"));
			tvTotalJieCunPrice.setText(ztTotal.get("ztjiecun"));
			tvTitleZhuanTiShow.setText(ztName);	
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
			ZhuanTiShowActivity.this.setResult(Activity.RESULT_OK);
			ZhuanTiShowActivity.this.close();
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
	
}
