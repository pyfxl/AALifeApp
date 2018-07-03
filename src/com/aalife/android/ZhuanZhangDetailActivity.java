package com.aalife.android;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.Toast;

public class ZhuanZhangDetailActivity extends Activity {
	private ListView listZhuanZhangDetail = null;
	private SimpleAdapter adapter = null;
	private List<Map<String, String>> list = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ZhuanZhangTableAccess zhangAccess = null;
	private LinearLayout layNoItem = null;
    private String curDate = "";
	private SharedHelper sharedHelper = null;
	private CardTableAccess cardAccess = null;
	private int position = 0;
	private boolean isClick = false;
	private Handler handler = new Handler();

	private View myView = null;
	private DatePicker datePicker = null;
	private RadioButton radioAll = null;
	private RadioButton radioYear = null;
	private RadioButton radioMonth = null;
	private String type = "all";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhuanzhang_detail);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvZhangFrom = (TextView) super.findViewById(R.id.tv_title_zhangfrom);
		textPaint = tvZhangFrom.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvZhangTo = (TextView) super.findViewById(R.id.tv_title_zhangto);
		textPaint = tvZhangTo.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvZhangMoney = (TextView) super.findViewById(R.id.tv_title_zhangmoney);
		textPaint = tvZhangMoney.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvZhangDate = (TextView) super.findViewById(R.id.tv_title_zhangdate);
		textPaint = tvZhangDate.getPaint();
		textPaint.setFakeBoldText(true);
		
		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//初始化
		sharedHelper = new SharedHelper(this);
		listZhuanZhangDetail = (ListView) super.findViewById(R.id.list_zhuanzhang_detail);
		listZhuanZhangDetail.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	

		//当前日期
		curDate = UtilityHelper.getCurDate();
		
		//绑定列表
		setListData(curDate, type);
		
		//提示长按操作
		Toast.makeText(ZhuanZhangDetailActivity.this, getString(R.string.txt_longedittext), Toast.LENGTH_SHORT).show();
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ZhuanZhangDetailActivity.this.setResult(Activity.RESULT_OK);
				ZhuanZhangDetailActivity.this.close();
			}			
		});
		
		//日期按钮
		ImageButton btnTitleDate = (ImageButton) super.findViewById(R.id.btn_title_date);
		btnTitleDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				myView = LayoutInflater.from(ZhuanZhangDetailActivity.this).inflate(R.layout.layout_zhuanti, new LinearLayout(ZhuanZhangDetailActivity.this), false);
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
				
				Dialog dialog = new AlertDialog.Builder(ZhuanZhangDetailActivity.this)
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

		//列表单击
		listZhuanZhangDetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
				final String fCardNote = map.get("zhangnote");
				
				//点击选中取消选择
				/*if(isClick && ZhuanZhangDetailActivity.this.position == position) {
					isClick = false;
					adapter.notifyDataSetChanged();
					return;
				}*/
				
		        isClick = true;
		        ZhuanZhangDetailActivity.this.position = position;
		        adapter.notifyDataSetChanged();
				
				if(!fCardNote.equals("")) {
					Dialog dialog = new AlertDialog.Builder(ZhuanZhangDetailActivity.this)
					    .setTitle(R.string.txt_card_note)
		                .setMessage(fCardNote + "\n")
		                .setNegativeButton(R.string.txt_day_cancel, new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                            dialog.cancel();
	                            isClick = false;
	        					adapter.notifyDataSetChanged();
	                        }
	                    }).create();
			        dialog.show();
				} else {
					handler.postDelayed(new Runnable() {
				        @Override
				        public void run() {
				        	isClick = false;
        					adapter.notifyDataSetChanged();
				        }
				    }, 1000);
				}
			}
		});
		
		//列表长按
		listZhuanZhangDetail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
				final String tCardName = map.get("zhangto");
				final String fCardName = map.get("zhangfrom");
				//final String fCardNote = map.get("zhangnote");
				final String cardMoney = map.get("zhangmoneyvalue");
				final int zzId = Integer.parseInt(map.get("zzid"));

		        isClick = true;
		        ZhuanZhangDetailActivity.this.position = position;
		        adapter.notifyDataSetChanged();
				
				Dialog dialog = new AlertDialog.Builder(ZhuanZhangDetailActivity.this)
				    .setTitle(R.string.txt_tips)
	                .setMessage(R.string.txt_zhuanti_message)
	                .setPositiveButton(R.string.txt_day_delete, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	//转出
							cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), ZhuanZhangDetailActivity.this);
							int fCardId = cardAccess.findCardId(tCardName);
							double dCardMoney = cardAccess.findCardMoney(fCardId) - Double.parseDouble(cardMoney);
							cardAccess.saveCard(fCardId, tCardName, String.valueOf(dCardMoney));
							//转入
							int tCardId = cardAccess.findCardId(fCardName);
							dCardMoney = cardAccess.findCardMoney(tCardId) + Double.parseDouble(cardMoney);
							cardAccess.saveCard(tCardId, fCardName, String.valueOf(dCardMoney));
							cardAccess.close();
                                
                        	zhangAccess = new ZhuanZhangTableAccess(sqlHelper.getReadableDatabase());
                        	zhangAccess.updateZhuanZhang(zzId);
                        	zhangAccess.close();
                            
                            sharedHelper.setLocalSync(true);
    			        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
    			        	ZhuanZhangDetailActivity.this.onCreate(null);
	                    }
	                }).setNegativeButton(R.string.txt_day_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            isClick = false;
        					adapter.notifyDataSetChanged();
                        }
                    }).create();
		        dialog.show();

                //振动
                UtilityHelper.setVibrator(ZhuanZhangDetailActivity.this);
                
		        return true;
			}			
		});
	}

	//绑定列表
	protected void setListData(String date, String type) {
		zhangAccess = new ZhuanZhangTableAccess(sqlHelper.getReadableDatabase());
		list = zhangAccess.findZhuanZhangDetail(date, type);
		zhangAccess.close();
		if(list.size() > 0) {
			layNoItem.setVisibility(View.GONE);
		} else {
			layNoItem.setVisibility(View.VISIBLE);
		}
		adapter = new SimpleAdapter(this, list, R.layout.list_zhuanzhang_detail, new String[] { "zzid", "zhangfrom", "zhangto", "zhangmoney", "zhangmoneyvalue", "zhangdate", "zhangnote" }, new int[] { R.id.tv_zhang_id, R.id.tv_zhang_from, R.id.tv_zhang_to, R.id.tv_zhang_money, R.id.tv_zhang_moneyvalue, R.id.tv_zhang_date, R.id.tv_zhang_note }){
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				//System.out.println("position:" + position + ", convertView:" + convertView);
				View view = super.getView(position, convertView, parent);
				TextView zhangFrom = (TextView) view.findViewById(R.id.tv_zhang_from);
		        TextView zhangTo = (TextView) view.findViewById(R.id.tv_zhang_to);
		        TextView zhangMoney = (TextView) view.findViewById(R.id.tv_zhang_money);
		        TextView zhangDate = (TextView) view.findViewById(R.id.tv_zhang_date);
				if(isClick && ZhuanZhangDetailActivity.this.position == position) {
					zhangFrom.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_tran_main));
					zhangTo.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_tran_main));
					zhangMoney.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_tran_main));
					zhangDate.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_tran_main));
				} else {
					zhangFrom.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_item_bg));
					zhangTo.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_item_bg));
					zhangMoney.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_item_bg));
					zhangDate.setBackgroundColor(ZhuanZhangDetailActivity.this.getResources().getColor(R.color.color_item_bg));
				}

				//如果有备注显示颜色
				TextView tvItemRemark = (TextView) view.findViewById(R.id.tv_zhang_note);
				if(!tvItemRemark.getText().equals("")) {
					zhangFrom.setTextColor(getResources().getColor(R.color.color_back_main));
					zhangTo.setTextColor(getResources().getColor(R.color.color_back_main));
					zhangMoney.setTextColor(getResources().getColor(R.color.color_back_main));
					zhangDate.setTextColor(getResources().getColor(R.color.color_back_main));
				} else {
					zhangFrom.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
					zhangTo.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
					zhangMoney.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
					zhangDate.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
				}
				
				return view;
			}
		};
		listZhuanZhangDetail.setAdapter(adapter);
		//UtilityHelper.setListViewHeight(this, listZhuanZhangDetail, adapter.getCount());
	}
	
	//关闭this
	protected void close() {
		this.finish();
	}

	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ZhuanZhangDetailActivity.this.setResult(Activity.RESULT_OK);
			ZhuanZhangDetailActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
		
}
