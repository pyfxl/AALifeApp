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

public class RankCatDetailActivity extends Activity {
	private ListView listRankCatDetail = null;
	private List<Map<String, String>> list = null;
	private SimpleAdapter adapter = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ItemTableAccess itemAccess = null;
	private String curDate = "";
	private LinearLayout layNoItem = null;
	private int catId = 0;
	private final int FIRST_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank_cat_detail);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleItemType = (TextView) super.findViewById(R.id.tv_title_itemtype);
		textPaint = tvTitleItemType.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleItemName = (TextView) super.findViewById(R.id.tv_title_itemname);
		textPaint = tvTitleItemName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleCount = (TextView) super.findViewById(R.id.tv_title_count);
		textPaint = tvTitleCount.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitlePrice = (TextView) super.findViewById(R.id.tv_title_price);
		textPaint = tvTitlePrice.getPaint();
		textPaint.setFakeBoldText(true);	
				
		//数据库
		sqlHelper = new DatabaseHelper(this);

		//取传入的值
		Intent intent = super.getIntent();
		catId = intent.getIntExtra("catid", 1);	
		curDate = intent.getStringExtra("date");
		
		//初始化
		listRankCatDetail = (ListView) super.findViewById(R.id.list_rankcatdetail);
		listRankCatDetail.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		
		//列表点击
		listRankCatDetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String itemName = map.get("itemname");
		        int catId = Integer.parseInt(map.get("catid"));
		        
		        TextView tvItemType = (TextView) view.findViewById(R.id.tv_rank_itemtype);
		        tvItemType.setBackgroundColor(RankCatDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemName = (TextView) view.findViewById(R.id.tv_rank_itemname);
		        tvItemName.setBackgroundColor(RankCatDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvCount = (TextView) view.findViewById(R.id.tv_rank_count);
		        tvCount.setBackgroundColor(RankCatDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvPrice = (TextView) view.findViewById(R.id.tv_rank_price);
		        tvPrice.setBackgroundColor(RankCatDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(RankCatDetailActivity.this, RankCountDetailActivity.class);
		        intent.putExtra("itemname", itemName);
		        intent.putExtra("catid", catId);
		        intent.putExtra("date", curDate);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				RankCatDetailActivity.this.setResult(Activity.RESULT_OK);
				RankCatDetailActivity.this.close();
			}			
		});
		
		setListData(curDate);
	}

	//关闭this
	protected void close() {
		this.finish();
	}
	
	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			RankCatDetailActivity.this.setResult(Activity.RESULT_OK);
			RankCatDetailActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//设置ListView	
	protected void setListData(String date) {		
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findRankCountByDate(date, catId);
		itemAccess.close();
		adapter = new SimpleAdapter(this, list, R.layout.list_rankcount, new String[] { "itemtype", "catid", "itemname", "count", "price" }, new int[] { R.id.tv_rank_itemtype, R.id.tv_rank_catid, R.id.tv_rank_itemname, R.id.tv_rank_count, R.id.tv_rank_price });
		listRankCatDetail.setAdapter(adapter);

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
			setListData(curDate);
		}
	}

}
