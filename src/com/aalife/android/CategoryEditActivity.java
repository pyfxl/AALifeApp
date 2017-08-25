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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryEditActivity extends Activity {
	private ListView listCategory = null;
	private SimpleAdapter adapter = null;
	private List<Map<String, String>> list = null;
	private SQLiteOpenHelper sqlHelper = null;
	private CategoryTableAccess categoryAccess = null;
	private EditText etCatName = null;
	private EditText etCatPrice = null;
	private int saveId = 0;
	private int catId = 0;
	private SharedHelper sharedHelper = null;
	private LinearLayout layNoItem = null;
	private int position = 0;
	private boolean isClick = false;
	private final int FIRST_REQUEST_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_edit);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleCatName = (TextView) super.findViewById(R.id.tv_title_catname);
		textPaint = tvTitleCatName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleCatPrice = (TextView) super.findViewById(R.id.tv_title_catprice);
		textPaint = tvTitleCatPrice.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleCatRate = (TextView) super.findViewById(R.id.tv_title_catrate);
		textPaint = tvTitleCatRate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleOperate = (TextView) super.findViewById(R.id.tv_title_operate);
		textPaint = tvTitleOperate.getPaint();
		textPaint.setFakeBoldText(true);
		
		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//初始化
		sharedHelper = new SharedHelper(this);		
		etCatName = (EditText) super.findViewById(R.id.et_cat_name);
		etCatPrice = (EditText) super.findViewById(R.id.et_cat_price);
		listCategory = (ListView) super.findViewById(R.id.list_category);
		listCategory.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	
		
		//绑定类别列表
		categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
		list = categoryAccess.findAllCatEdit();
		categoryAccess.close();
		
		//列表为空
		if(list.size() <= 0) {
			layNoItem.setVisibility(View.VISIBLE);
		}
		
		//预警范围
		double catRate = Double.parseDouble(sharedHelper.getCategoryRate());
		for(int i=0; i < list.size(); i++) {
			Map<String, String> map = list.get(i);
			double catPrice = Double.parseDouble(map.get("catprice"));
			double[] rateArr = getRateArray(catPrice, catRate);
			if(catPrice > 0) {
				map.put("catrate", UtilityHelper.formatDouble(rateArr[0], "0.###") + "~" + UtilityHelper.formatDouble(rateArr[1], "0.###"));
			}
		}
		
		//列表数据源
		adapter = new SimpleAdapter(this, list, R.layout.list_category, new String[] { "catname", "delete", "catid", "catprice", "catrate" }, new int[] { R.id.tv_cat_name, R.id.tv_cat_delete, R.id.tv_cat_catid, R.id.tv_cat_price, R.id.tv_cat_rate }) {
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView tvCatId = (TextView) view.findViewById(R.id.tv_cat_catid);
				final int catId = Integer.parseInt(tvCatId.getText().toString());
				//删除
				TextView tv = (TextView) view.findViewById(R.id.tv_cat_delete);
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
						int result = categoryAccess.delCategory(catId);
						sharedHelper.setCategory(0);
						categoryAccess.close();
						if(result == 1) {
				        	CategoryEditActivity.this.onCreate(null);
							Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_day_deletesuccess), Toast.LENGTH_SHORT).show();
						} else if(result == 2) {
							Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_day_deleteuse), Toast.LENGTH_SHORT).show();
						} else if(result == 3) {
							Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_day_deleteonly), Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_day_deleteerror), Toast.LENGTH_SHORT).show();
						}
						
						sharedHelper.setLocalSync(true);
			        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
					}					
				});

		        TextView tvCatName = (TextView) view.findViewById(R.id.tv_cat_name);
		        TextView tvCatPrice = (TextView) view.findViewById(R.id.tv_cat_price);
		        TextView tvRate = (TextView) view.findViewById(R.id.tv_cat_rate);
		        LinearLayout layOperate = (LinearLayout) view.findViewById(R.id.lay_cat_operate);
				if(isClick && CategoryEditActivity.this.position == position) {
			        tvCatName.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_tran_main));
			        tvCatPrice.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_tran_main));
					tvRate.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_tran_main));
			        layOperate.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_tran_main));
				} else {
			        tvCatName.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_item_bg));
			        tvCatPrice.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_item_bg));
					tvRate.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_item_bg));
			        layOperate.setBackgroundColor(CategoryEditActivity.this.getResources().getColor(R.color.color_item_bg));
				}

				return view;
			}			
		};
		listCategory.setAdapter(adapter);			
		
		//列表点击
		listCategory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//点击选中取消选择
				if(isClick && CategoryEditActivity.this.position == position) {
					isClick = false;
					CategoryEditActivity.this.saveId = -1;
					CategoryEditActivity.this.etCatName.setText("");
					CategoryEditActivity.this.etCatPrice.setText("");
					adapter.notifyDataSetChanged();
					return;
				}
				
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        CategoryEditActivity.this.saveId = Integer.parseInt(map.get("catid"));
		        String catName = map.get("catname");
		        String catPrice = map.get("catprice");
		        CategoryEditActivity.this.etCatName.setText(catName);
		        CategoryEditActivity.this.etCatPrice.setText(catPrice);
		        
		        isClick = true;
		        CategoryEditActivity.this.position = position;
		        adapter.notifyDataSetChanged();		        
			}			
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.putExtra("catid", catId);
				CategoryEditActivity.this.setResult(Activity.RESULT_OK, intent);
				CategoryEditActivity.this.close();
			}			
		});
		
		//预算率设置
		ImageButton btnCatRate = (ImageButton) super.findViewById(R.id.btn_catrate);
		btnCatRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showRateDialog();
			}			
		});
				
		//保存按钮
		Button btnCatSave = (Button) super.findViewById(R.id.btn_cat_save);
		btnCatSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String catName = CategoryEditActivity.this.etCatName.getText().toString().trim();
				if (catName.equals("")) {
					Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_cat_name) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}
				String catPrice = CategoryEditActivity.this.etCatPrice.getText().toString().trim();
				if (catPrice.equals("")) {
					catPrice = "0";
				}
				categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
				catId = categoryAccess.saveCategory(saveId, catName, catPrice);
				categoryAccess.close();
		        if(catId > 0) {
		        	sharedHelper.setLocalSync(true);
		        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
		        	
		        	saveId = 0;
		        	Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_add_addsuccess), Toast.LENGTH_SHORT).show();
		        } else {
		        	Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_add_adderror), Toast.LENGTH_SHORT).show();
		        }
		        
				isClick = false;
				CategoryEditActivity.this.onCreate(null);	
			}	
        		
		});
	}

	//关闭this
	protected void close() {
		this.finish();
	}

	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra("catid", catId);
			CategoryEditActivity.this.setResult(Activity.RESULT_OK, intent);
			CategoryEditActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE) {	
			isClick = false;
			position = 0;
			saveId = 0;
			this.onCreate(null);
		}
	}
	
	//预警范围
	private double[] getRateArray(double catPrice, double catRate)
    {
        double num = catPrice - catPrice * (catRate / 100);
        double[] result = new double[2];
        result[0] = catPrice - num;
        result[1] = catPrice + num;

        return result;
    }
	
	//显示预算率设置窗口
	private void showRateDialog() {
		View catRateView = LayoutInflater.from(CategoryEditActivity.this).inflate(R.layout.layout_category_rate, new LinearLayout(CategoryEditActivity.this), false);
		final EditText catRateEdit = (EditText) catRateView.findViewById(R.id.et_category_rate);
		final String categoryRate = sharedHelper.getCategoryRate();
		catRateEdit.setText(categoryRate);
		
		Dialog dialog = new AlertDialog.Builder(CategoryEditActivity.this)
		    .setTitle(R.string.txt_title_catrate)
			.setView(catRateView)
			.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String catRateValue = catRateEdit.getText().toString();
					if(!UtilityHelper.checkDouble(catRateValue)) {
						Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_set_categorytext), Toast.LENGTH_SHORT).show();
						return;
					}
					if(Double.parseDouble(catRateValue)>100) {
						Toast.makeText(CategoryEditActivity.this, getString(R.string.txt_set_categoryerror), Toast.LENGTH_SHORT).show();
						return;
					}
					
					double oldCatRate = Double.parseDouble(categoryRate);
					double newCatRate = Double.parseDouble(catRateValue);
					if(newCatRate != oldCatRate) {
						sharedHelper.setCategoryRate(catRateValue);
						sharedHelper.setLocalSync(true);
						sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
					}
					
					CategoryEditActivity.this.onCreate(null);
				}
			}).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			}).create();
		dialog.show();
	}
	
}
