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

public class ZhuanTiActivity extends Activity {
	private ListView listZhuanTi = null;
	private SimpleAdapter adapter = null;
	private List<Map<String, String>> list = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ZhuanTiTableAccess zhuanTiAccess = null;
	private EditText etZhuanTiName = null;
	private int saveId = 0;
	private SharedHelper sharedHelper = null;
	private LinearLayout layNoItem = null;
	private int position = 0;
	private boolean isClick = false;
	private final int FIRST_REQUEST_CODE = 1;
	private ItemTableAccess itemAccess = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhuanti);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleName = (TextView) super.findViewById(R.id.tv_title_name);
		textPaint = tvTitleName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleDate = (TextView) super.findViewById(R.id.tv_title_date);
		textPaint = tvTitleDate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleJieCun = (TextView) super.findViewById(R.id.tv_title_jiecun);
		textPaint = tvTitleJieCun.getPaint();
		textPaint.setFakeBoldText(true);

		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//初始化
		sharedHelper = new SharedHelper(this);	
		etZhuanTiName = (EditText) super.findViewById(R.id.et_zhuanti_name);
		listZhuanTi = (ListView) super.findViewById(R.id.list_zhuanti);
		listZhuanTi.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	
		
		//绑定类别列表
		zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
		list = zhuanTiAccess.findAllZhuanTiList();
		zhuanTiAccess.close();
		
		//列表为空
		if(list.size() <= 0) {
			layNoItem.setVisibility(View.VISIBLE);
		}

		//列表数据源
		adapter = new SimpleAdapter(this, list, R.layout.list_zhuanti, new String[] { "ztname", "ztdate", "ztjiecun" }, new int[] { R.id.tv_zhuanti_name, R.id.tv_zhuanti_date, R.id.tv_zhuanti_jiecun }) {
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView tvName = (TextView) view.findViewById(R.id.tv_zhuanti_name);
		        TextView tvDate = (TextView) view.findViewById(R.id.tv_zhuanti_date);
		        TextView tvJieCun = (TextView) view.findViewById(R.id.tv_zhuanti_jiecun);
				if(isClick && ZhuanTiActivity.this.position == position) {
					tvName.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_tran_main));
					tvDate.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_tran_main));
					tvJieCun.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_tran_main));
				} else {
					tvName.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_item_bg));
					tvDate.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_item_bg));
					tvJieCun.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_item_bg));
				}

				return view;
			}			
		};
		listZhuanTi.setAdapter(adapter);
		
		//列表点击
		listZhuanTi.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
				String ztName = map.get("ztname");
				int ztId = Integer.parseInt(map.get("ztid"));
				
				TextView tvName = (TextView) view.findViewById(R.id.tv_zhuanti_name);
				tvName.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvDate = (TextView) view.findViewById(R.id.tv_zhuanti_date);
				tvDate.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_tran_main));
		        TextView tvJieCun = (TextView) view.findViewById(R.id.tv_zhuanti_jiecun);
		        tvJieCun.setBackgroundColor(ZhuanTiActivity.this.getResources().getColor(R.color.color_tran_main));
		        
				Intent intent = new Intent(ZhuanTiActivity.this, ZhuanTiShowActivity.class);
				intent.putExtra("ztname", ztName);
				intent.putExtra("ztid", ztId);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//列表长按
		listZhuanTi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
				final String ztName = map.get("ztname");
				final int ztId = Integer.parseInt(map.get("ztid"));
            	ZhuanTiActivity.this.position = position;
            	        	
                Dialog dialog = new AlertDialog.Builder(ZhuanTiActivity.this)
                    .setTitle(R.string.txt_tips)
                    .setMessage(R.string.txt_zhuanti_message)
                    .setPositiveButton(R.string.txt_day_edit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	ZhuanTiActivity.this.etZhuanTiName.setText(ztName);
                        	isClick = true;
                        	saveId = ztId;
                        	
                        	adapter.notifyDataSetChanged();
                        	
                        	sharedHelper.setLocalSync(true);
    			        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
                        }
                    }).setNegativeButton(R.string.txt_day_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            
                            itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
                            boolean success = itemAccess.deleteZhuanTi(ztId);
                            itemAccess.close();
                            
                            if(success) {	                                
                                zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
                        		zhuanTiAccess.updateZhuanTi(ztId);
                        		zhuanTiAccess.close();
                            }
                            
                            sharedHelper.setLocalSync(true);
    			        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
                            ZhuanTiActivity.this.onCreate(null);
                        }
                    }).create();
                dialog.show();
                
                //振动
                UtilityHelper.setVibrator(ZhuanTiActivity.this);
                
                return true;
            }
        });
        
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ZhuanTiActivity.this.setResult(Activity.RESULT_OK);
				ZhuanTiActivity.this.close();
			}			
		});
				
		//保存按钮
		Button btnZhuanTiSave = (Button) super.findViewById(R.id.btn_zhuanti_save);
		btnZhuanTiSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String ztName = ZhuanTiActivity.this.etZhuanTiName.getText().toString().trim();
				if (ztName.equals("")) {
					Toast.makeText(ZhuanTiActivity.this, getString(R.string.txt_zhuanti_name) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}
				zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
				Boolean result = zhuanTiAccess.saveZhuanTi(saveId, ztName, "none.gif");
				zhuanTiAccess.close();
		        if(result) {
		        	sharedHelper.setLocalSync(true);
		        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
		        	
		        	saveId = 0;
		        	Toast.makeText(ZhuanTiActivity.this, getString(R.string.txt_add_addsuccess), Toast.LENGTH_SHORT).show();
		        } else {
		        	Toast.makeText(ZhuanTiActivity.this, getString(R.string.txt_add_adderror), Toast.LENGTH_SHORT).show();
		        }

		        isClick = false;
                ZhuanTiActivity.this.onCreate(null);
			}			
		});
	}

	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ZhuanTiActivity.this.setResult(Activity.RESULT_OK);
			ZhuanTiActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//关闭this
	protected void close() {
		this.finish();
	}
		
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			isClick = false;
			this.onCreate(null);
		}
	}
	
}
