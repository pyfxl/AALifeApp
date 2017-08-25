package com.aalife.android;

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
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity {
	private EditText etTitleKey = null;
	private ImageButton btnTitleSearch = null;
	private String key = "";
    private String curDate = "";
	private TextView tvTotalZhiChu = null;
	private TextView tvTotalShouRu = null;
	private TextView tvTotalJieCun = null;
	private double totalZhiChu = 0;
	private double totalShouRu = 0;
	private LinearLayout layDayTotal = null;
	private final int FIRST_REQUEST_CODE = 1;

	private ListView listSearch = null;
	private List<Map<String, String>> list = null;
	private SimpleAdapter adapter = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ItemTableAccess itemAccess = null;
	private LinearLayout layNoItem = null;

	private View myView = null;
	private DatePicker datePicker = null;
	private RadioButton radioAll = null;
	private RadioButton radioYear = null;
	private RadioButton radioMonth = null;
	private String type = "all";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleItemType = (TextView) super.findViewById(R.id.tv_title_itemtype);
		textPaint = tvTitleItemType.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleSelect = (TextView) super.findViewById(R.id.tv_title_select);
		textPaint = tvTitleSelect.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleItemName = (TextView) super.findViewById(R.id.tv_title_itemname);
		textPaint = tvTitleItemName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleItemBuyDate = (TextView) super.findViewById(R.id.tv_title_itembuydate);
		textPaint = tvTitleItemBuyDate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleItemPrice = (TextView) super.findViewById(R.id.tv_title_itemprice);
		textPaint = tvTitleItemPrice.getPaint();
		textPaint.setFakeBoldText(true);

		tvTotalZhiChu = (TextView) super.findViewById(R.id.tv_total_zhichu);
		textPaint = tvTotalZhiChu.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalShouRu = (TextView) super.findViewById(R.id.tv_total_shouru);
		textPaint = tvTotalShouRu.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalJieCun = (TextView) super.findViewById(R.id.tv_total_jiecun);
		textPaint = tvTotalJieCun.getPaint();
		textPaint.setFakeBoldText(true);
		
		//初始化
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);
		layDayTotal = (LinearLayout) super.findViewById(R.id.lay_day_total);
		layDayTotal.setVisibility(View.GONE);
		etTitleKey = (EditText) super.findViewById(R.id.et_title_key);
		listSearch = (ListView) super.findViewById(R.id.list_search);

		//当前日期
		curDate = UtilityHelper.getCurDate();
		
        //初始值
		Intent intent = super.getIntent();
		key = intent.getStringExtra("key");
		if(!key.equals("")) {
			etTitleKey.setText(key);
		}
		setSearchData(key, curDate, type);
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SearchActivity.this.finish();
			}			
		});
				
		//搜索按钮
		btnTitleSearch = (ImageButton) super.findViewById(R.id.btn_title_search);
		btnTitleSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				key = etTitleKey.getText().toString().trim();
				key = UtilityHelper.replaceKey(key);
				if(key.equals("")) {
					etTitleKey.setText("");
					Toast.makeText(SearchActivity.this, getString(R.string.txt_search_keyempty), Toast.LENGTH_SHORT).show();
					return;
				}
				
				setSearchData(key, curDate, type);
			}			
		});

		//列表点击
		listSearch.setDivider(null);		
		listSearch.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String date = map.get("datevalue");
		        
		        LinearLayout laySelect = (LinearLayout) view.findViewById(R.id.lay_rank_select);
		        laySelect.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemType = (TextView) view.findViewById(R.id.tv_rank_itemtype);
		        tvItemType.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemName = (TextView) view.findViewById(R.id.tv_rank_itemname);
		        tvItemName.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemBuyDate = (TextView) view.findViewById(R.id.tv_rank_itembuydate);
		        tvItemBuyDate.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemPrice = (TextView) view.findViewById(R.id.tv_rank_itemprice);
		        tvItemPrice.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(SearchActivity.this, DayDetailActivity.class);
		        intent.putExtra("date", date);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//日期按钮
		ImageButton btnTitleDate = (ImageButton) super.findViewById(R.id.btn_title_date);
		btnTitleDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				myView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_zhuanti, new LinearLayout(SearchActivity.this), false);
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
				
				Dialog dialog = new AlertDialog.Builder(SearchActivity.this)
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
							
							setSearchData(key, curDate, type);
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
		
	//设置搜索Data	
	protected void setSearchData(String key, String date, String type) {		
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findItemByKey(key, date, type);
		itemAccess.close();
		
		final boolean[] listCheck = new boolean[list.size()];
		adapter = new SimpleAdapter(this, list, R.layout.list_search, new String[] { "itemtype", "itemtypevalue", "itemname", "itembuydate", "itemprice", "pricevalue" }, new int[] { R.id.tv_rank_itemtype, R.id.tv_rank_itemtypevalue, R.id.tv_rank_itemname, R.id.tv_rank_itembuydate, R.id.tv_rank_itemprice, R.id.tv_rank_pricevalue }){
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				final TextView priceValue = (TextView) view.findViewById(R.id.tv_rank_pricevalue);
				final TextView typeValue = (TextView) view.findViewById(R.id.tv_rank_itemtypevalue);
				//选择
				final CheckBox sel = (CheckBox) view.findViewById(R.id.cb_rank_select);
				sel.setChecked(!listCheck[position]);
				sel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						listCheck[position] = sel.isChecked() ? false : true;
						if(sel.isChecked()) {
						    updateTotal(Double.parseDouble(priceValue.getText().toString()), typeValue.getText().toString());
						} else {
							updateTotal(-Double.parseDouble(priceValue.getText().toString()), typeValue.getText().toString());
						}
					}					
				});
				return view;
			}
		};
		listSearch.setAdapter(adapter);

		//设置empty
		if(list.size() > 0) {
			layNoItem.setVisibility(View.GONE);
			layDayTotal.setVisibility(View.VISIBLE);
		} else {
			layNoItem.setVisibility(View.VISIBLE);
			layDayTotal.setVisibility(View.GONE);
		}
		
		if(list.size() > 0) {
			//统计总价
			totalZhiChu = 0;
			totalShouRu = 0;
			Iterator<Map<String, String>> it = list.iterator();
			while(it.hasNext()) {
				Map<String, String> map = (Map<String, String>) it.next();
				String itemType = map.get("itemtypevalue");
				if(itemType.equals("zc") || itemType.equals("jc") || itemType.equals("hc")) {
				    totalZhiChu += Double.parseDouble(map.get("pricevalue"));
				} else {
					totalShouRu += Double.parseDouble(map.get("pricevalue"));
				}
			}
			tvTotalZhiChu.setText(getString(R.string.txt_month_zhi) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(totalZhiChu, "0.0##"));
			tvTotalShouRu.setText(getString(R.string.txt_month_shou) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(totalShouRu, "0.0##"));
			tvTotalJieCun.setText(getString(R.string.txt_month_cun) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(totalShouRu - totalZhiChu, "0.0##"));
		}
	}

	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SearchActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}

	//关闭this
	protected void close() {
		this.finish();
	}

	//更新总价
	protected void updateTotal(double price, String type) {
		if(type.equals("zc") || type.equals("jc") || type.equals("hc")) {
		    totalZhiChu += price;
		} else if(type.equals("sr") || type.equals("jr") || type.equals("hr")) {
			totalShouRu += price;
		}
		tvTotalZhiChu.setText(getString(R.string.txt_month_zhi) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(totalZhiChu, "0.0##"));
		tvTotalShouRu.setText(getString(R.string.txt_month_shou) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(totalShouRu, "0.0##"));
		tvTotalJieCun.setText(getString(R.string.txt_month_cun) + " " + getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(totalShouRu - totalZhiChu, "0.0##"));
	}
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			setSearchData(key, curDate, type);
		}
	}
	
}
