package com.aalife.android;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RankCountDetailActivity extends Activity {
	private ListView listRankCountDetail = null;
	private List<Map<String, String>> list = null;
	private SimpleAdapter adapter = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ItemTableAccess itemAccess = null;
	private String curDate = "";
	private LinearLayout layNoItem = null;
	private String itemName = "";
	private int catId = 0;
	private final int FIRST_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank_count_detail);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleItemType = (TextView) super.findViewById(R.id.tv_title_itemtype);
		textPaint = tvTitleItemType.getPaint();
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
				
		//数据库
		sqlHelper = new DatabaseHelper(this);

		//取传入的值
		Intent intent = super.getIntent();
		itemName = intent.getStringExtra("itemname");
		catId = intent.getIntExtra("catid", 0);
		curDate = intent.getStringExtra("date");
		
		//初始化
		listRankCountDetail = (ListView) super.findViewById(R.id.list_rankcountdetail);
		listRankCountDetail.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		
		//列表点击
		listRankCountDetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String date = map.get("datevalue");
		        
		        TextView tvItemType = (TextView) view.findViewById(R.id.tv_rank_itemtype);
		        tvItemType.setBackgroundColor(RankCountDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemName = (TextView) view.findViewById(R.id.tv_rank_itemname);
		        tvItemName.setBackgroundColor(RankCountDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemBuyDate = (TextView) view.findViewById(R.id.tv_rank_itembuydate);
		        tvItemBuyDate.setBackgroundColor(RankCountDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemPrice = (TextView) view.findViewById(R.id.tv_rank_itemprice);
		        tvItemPrice.setBackgroundColor(RankCountDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(RankCountDetailActivity.this, DayDetailActivity.class);
		        intent.putExtra("date", date);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				RankCountDetailActivity.this.setResult(Activity.RESULT_OK);
				RankCountDetailActivity.this.close();
			}			
		});
		
		setListData(curDate, itemName);
	}

	//关闭this
	protected void close() {
		this.finish();
	}
	
	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			RankCountDetailActivity.this.setResult(Activity.RESULT_OK);
			RankCountDetailActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//设置ListView	
	protected void setListData(String date, String itemName) {		
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findRankPriceByDate(date, itemName, catId);
		itemAccess.close();
		adapter = new SimpleAdapter(this, list, R.layout.list_rankprice, new String[] { "itemtype", "itemname", "itembuydate", "itemprice" }, new int[] { R.id.tv_rank_itemtype, R.id.tv_rank_itemname, R.id.tv_rank_itembuydate, R.id.tv_rank_itemprice });
		listRankCountDetail.setAdapter(adapter);
		
		//设置empty
		if(list.size() == 0) {
			layNoItem.setVisibility(View.VISIBLE);
		} else {
			layNoItem.setVisibility(View.GONE);
		}
	}
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if(data != null) {
				String itemName = data.getStringExtra("itemname");
				if(!itemName.equals(""))
					this.itemName = itemName;
			}
			
			setListData(curDate, this.itemName);
		}
	}

}
