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

public class AnalyzeJieHuanDetailActivity extends Activity {
	private ListView listAnalyzeJieHuanDetail = null;
	private List<Map<String, String>> list = null;
	private SimpleAdapter adapter = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ItemTableAccess itemAccess = null;
	private String curDate = "";
	private LinearLayout layNoItem = null;
	private final int FIRST_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analyze_jiehuan_detail);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleItemBuyDate = (TextView) super.findViewById(R.id.tv_title_itembuydate);
		textPaint = tvTitleItemBuyDate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleJieChu = (TextView) super.findViewById(R.id.tv_title_jiechu);
		textPaint = tvTitleJieChu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleHuanRu = (TextView) super.findViewById(R.id.tv_title_huanru);
		textPaint = tvTitleHuanRu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleJieRu = (TextView) super.findViewById(R.id.tv_title_jieru);
		textPaint = tvTitleJieRu.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleHuanChu = (TextView) super.findViewById(R.id.tv_title_huanchu);
		textPaint = tvTitleHuanChu.getPaint();
		textPaint.setFakeBoldText(true);	
				
		//数据库
		sqlHelper = new DatabaseHelper(this);

		//取传入的值
		Intent intent = super.getIntent();
		curDate = intent.getStringExtra("date");
		
		//初始化
		listAnalyzeJieHuanDetail = (ListView) super.findViewById(R.id.list_analyzejiehuandetail);
		listAnalyzeJieHuanDetail.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		
		//列表点击
		listAnalyzeJieHuanDetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
				String date = map.get("datevalue");
				
		        TextView tvItemBuyDate = (TextView) view.findViewById(R.id.tv_analyze_itembuydate);
		        tvItemBuyDate.setBackgroundColor(AnalyzeJieHuanDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvJieChuPrice = (TextView) view.findViewById(R.id.tv_analyze_jiechu);
		        tvJieChuPrice.setBackgroundColor(AnalyzeJieHuanDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvHuanRuPrice = (TextView) view.findViewById(R.id.tv_analyze_huanru);
		        tvHuanRuPrice.setBackgroundColor(AnalyzeJieHuanDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvJieRuPrice = (TextView) view.findViewById(R.id.tv_analyze_jieru);
		        tvJieRuPrice.setBackgroundColor(AnalyzeJieHuanDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvHuanChuPrice = (TextView) view.findViewById(R.id.tv_analyze_huanchu);
		        tvHuanChuPrice.setBackgroundColor(AnalyzeJieHuanDetailActivity.this.getResources().getColor(R.color.color_tran_main));
		        
		        Intent intent = new Intent(AnalyzeJieHuanDetailActivity.this, DayDetailActivity.class);
		        intent.putExtra("date", date);
		        startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AnalyzeJieHuanDetailActivity.this.setResult(Activity.RESULT_OK);
				AnalyzeJieHuanDetailActivity.this.close();
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
			AnalyzeJieHuanDetailActivity.this.setResult(Activity.RESULT_OK);
			AnalyzeJieHuanDetailActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//设置ListView	
	protected void setListData(String date) {		
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findAnalyzeJieHuanDetail(date);
		itemAccess.close();
		adapter = new SimpleAdapter(this, list, R.layout.list_analyzejiehuan, new String[] { "itembuydate", "jiechuprice", "huanruprice", "jieruprice", "huanchuprice" }, new int[] { R.id.tv_analyze_itembuydate, R.id.tv_analyze_jiechu, R.id.tv_analyze_huanru, R.id.tv_analyze_jieru, R.id.tv_analyze_huanchu });
		listAnalyzeJieHuanDetail.setAdapter(adapter);

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
