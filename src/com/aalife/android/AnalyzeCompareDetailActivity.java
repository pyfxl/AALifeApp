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

public class AnalyzeCompareDetailActivity extends Activity {
	private ListView listAnalyzeCompareDetail = null;
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
		setContentView(R.layout.activity_analyze_compare_detail);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleItemType = (TextView) super.findViewById(R.id.tv_title_itemtype);
		textPaint = tvTitleItemType.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleItemName = (TextView) super.findViewById(R.id.tv_title_itemname);
		textPaint = tvTitleItemName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleCountCur = (TextView) super.findViewById(R.id.tv_title_countcur);
		textPaint = tvTitleCountCur.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitlePriceCur = (TextView) super.findViewById(R.id.tv_title_pricecur);
		textPaint = tvTitlePriceCur.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleCountPrev = (TextView) super.findViewById(R.id.tv_title_countprev);
		textPaint = tvTitleCountPrev.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitlePricePrev = (TextView) super.findViewById(R.id.tv_title_priceprev);
		textPaint = tvTitlePricePrev.getPaint();
		textPaint.setFakeBoldText(true);	
				
		//数据库
		sqlHelper = new DatabaseHelper(this);

		//取传入的值
		Intent intent = super.getIntent();
		catId = intent.getIntExtra("catid", 1);	
		curDate = intent.getStringExtra("date");
		
		//初始化
		listAnalyzeCompareDetail = (ListView) super.findViewById(R.id.list_analyzecomparedetail);
		listAnalyzeCompareDetail.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		
		//列表点击
		listAnalyzeCompareDetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        String itemName = map.get("itemname");
		        String countValue = map.get("countvalue");
		        String tmpDate = !countValue.equals("0") ? curDate : UtilityHelper.getNavDate(curDate, -1, "m");
		        
		        TextView tvItemType = (TextView) view.findViewById(R.id.tv_analyze_itemtype);
		        tvItemType.setBackgroundColor(AnalyzeCompareDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvItemName = (TextView) view.findViewById(R.id.tv_analyze_itemname);
		        tvItemName.setBackgroundColor(AnalyzeCompareDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvCountCur = (TextView) view.findViewById(R.id.tv_analyze_countcur);
		        tvCountCur.setBackgroundColor(AnalyzeCompareDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvPriceCur = (TextView) view.findViewById(R.id.tv_analyze_pricecur);
		        tvPriceCur.setBackgroundColor(AnalyzeCompareDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvCountPrev = (TextView) view.findViewById(R.id.tv_analyze_countprev);
		        tvCountPrev.setBackgroundColor(AnalyzeCompareDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvPricePrev = (TextView) view.findViewById(R.id.tv_analyze_priceprev);
		        tvPricePrev.setBackgroundColor(AnalyzeCompareDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(AnalyzeCompareDetailActivity.this, RankCountDetailActivity.class);
		        intent.putExtra("itemname", itemName);
		        intent.putExtra("date", tmpDate);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AnalyzeCompareDetailActivity.this.setResult(Activity.RESULT_OK);
				AnalyzeCompareDetailActivity.this.close();
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
			AnalyzeCompareDetailActivity.this.setResult(Activity.RESULT_OK);
			AnalyzeCompareDetailActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//设置ListView	
	protected void setListData(String date) {		
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findAnalyzeCompareDetailByDate(date, catId);
		itemAccess.close();
		adapter = new SimpleAdapter(this, list, R.layout.list_analyzecompare_detail, new String[] { "itemtype", "itemname", "countcur", "pricecur", "countprev", "priceprev" }, new int[] { R.id.tv_analyze_itemtype, R.id.tv_analyze_itemname, R.id.tv_analyze_countcur, R.id.tv_analyze_pricecur, R.id.tv_analyze_countprev, R.id.tv_analyze_priceprev });
		listAnalyzeCompareDetail.setAdapter(adapter);

		//设置empty
		if(list.size() == 0) {
			layNoItem.setVisibility(View.VISIBLE);
			listAnalyzeCompareDetail.setVisibility(View.GONE);
		} else {
			layNoItem.setVisibility(View.GONE);
			listAnalyzeCompareDetail.setVisibility(View.VISIBLE);
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
