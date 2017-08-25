package com.aalife.android;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CardActivity extends Activity {
	private ListView listCard = null;
	private SimpleAdapter adapter = null;
	private List<Map<String, String>> list = null;
	private SQLiteOpenHelper sqlHelper = null;
	private CardTableAccess cardAccess = null;
	private CategoryTableAccess categoryAccess = null;
	private ItemTableAccess itemAccess = null;
	private ZhuanZhangTableAccess zhangAccess = null;
	private EditText etCardName = null;
	private EditText etCardMoney = null;
	private TextView tvCardMoneyStart = null;
	private TextView tvCardMoneyBalance = null;
	private int saveId = -1; //-1是新增，0是我的钱包，数值是对应的钱包
	private SharedHelper sharedHelper = null;
	private LinearLayout layNoItem = null;
	private LinearLayout cardTotal = null;
	private int position = 0;
	private boolean isClick = false;
	private final int FIRST_REQUEST_CODE = 1;
	
	private View transView = null;
	private Spinner spinerCardIn = null;
	private Spinner spinerCardOut = null;
	private ArrayAdapter<CharSequence> cardAdapter = null;
	private List<CharSequence> cardList = null;
	private EditText cardMoneyEdit = null;
	private EditText cardDateEdit = null;
	private String curDate = "";

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);
	
		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleDetail = (TextView) super.findViewById(R.id.tv_title_detail);
		textPaint = tvTitleDetail.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleCardName = (TextView) super.findViewById(R.id.tv_title_cardname);
		textPaint = tvTitleCardName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleCardMoney = (TextView) super.findViewById(R.id.tv_title_cardmoney);
		textPaint = tvTitleCardMoney.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleOperate = (TextView) super.findViewById(R.id.tv_title_operate);
		textPaint = tvTitleOperate.getPaint();
		textPaint.setFakeBoldText(true);

		//数据库
		sqlHelper = new DatabaseHelper(this);
		
		//初始化
		sharedHelper = new SharedHelper(this);	
		etCardName = (EditText) super.findViewById(R.id.et_card_name);
		etCardMoney = (EditText) super.findViewById(R.id.et_card_money);
		tvCardMoneyStart = (TextView) super.findViewById(R.id.tv_card_moneystart);
		tvCardMoneyBalance = (TextView) super.findViewById(R.id.tv_card_moneybalance);
		listCard = (ListView) super.findViewById(R.id.list_card);
		listCard.setDivider(null);
		layNoItem = (LinearLayout) super.findViewById(R.id.lay_noitem);
		layNoItem.setVisibility(View.GONE);	
        cardTotal = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_card_total, null, false);
        listCard.addFooterView(cardTotal);
        curDate = UtilityHelper.getCurDate();
        
        //转账下拉
        cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase());
		cardList = cardAccess.findAllCard();
		cardAccess.close();
		cardAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, cardList);
		cardAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
        
        //总计标题变粗
		TextView tvTotalPrice = (TextView) cardTotal.findViewById(R.id.tv_total_price);
		textPaint = tvTotalPrice.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTotalLabel = (TextView) cardTotal.findViewById(R.id.tv_total_label);
		textPaint = tvTotalLabel.getPaint();
		textPaint.setFakeBoldText(true);

		//绑定列表
		cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), this);
		list = cardAccess.findAllCardList();
		cardAccess.close();
		
		//列表为空
		if(list.size() <= 0) {
			layNoItem.setVisibility(View.VISIBLE);
		}
		
		//计算总价
		double totalMoney = 0;
		for(Map<String, String> map : list) {
			totalMoney += Double.parseDouble(map.get("cardmoneyvalue"));
		}
		tvTotalPrice.setText(getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(totalMoney, "0.0##"));

		//列表数据源
		adapter = new SimpleAdapter(this, list, R.layout.list_card, new String[] { "detail", "cardname", "cardmoney", "cardmoneyvalue", "cardmoneystart", "delete", "cardid" }, new int[] { R.id.tv_card_detail, R.id.tv_card_name, R.id.tv_card_money, R.id.tv_card_moneyvalue, R.id.tv_card_moneystart, R.id.tv_card_delete, R.id.tv_card_cardid }) {
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView tvCardId = (TextView) view.findViewById(R.id.tv_card_cardid);
				TextView tvCardName = (TextView) view.findViewById(R.id.tv_card_name);
				final int cardId = Integer.parseInt(tvCardId.getText().toString());
				final String cardName = tvCardName.getText().toString();
				//明细
				TextView tvDetail = (TextView) view.findViewById(R.id.tv_card_detail);
				tvDetail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(CardActivity.this, CardDetailActivity.class);
						intent.putExtra("cdid", cardId);
						intent.putExtra("cdname", cardName);
						startActivityForResult(intent, FIRST_REQUEST_CODE);
					}
				});
				//删除
				TextView tvDel = (TextView) view.findViewById(R.id.tv_card_delete);
				tvDel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase());
						int result = cardAccess.deleteCard(cardId);
						sharedHelper.setCardId(0);
						cardAccess.close();
						if(result == 1) {
				        	CardActivity.this.onCreate(null);
							Toast.makeText(CardActivity.this, getString(R.string.txt_day_deletesuccess), Toast.LENGTH_SHORT).show();
						} else if(result == 2) {
							Toast.makeText(CardActivity.this, getString(R.string.txt_day_deleteuse), Toast.LENGTH_SHORT).show();
						} else if(result == 3) {
							Toast.makeText(CardActivity.this, getString(R.string.txt_day_deleteonly), Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(CardActivity.this, getString(R.string.txt_day_deleteerror), Toast.LENGTH_SHORT).show();
						}
						
						sharedHelper.setLocalSync(true);
			        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
					}					
				});

		        TextView tvCardMoney = (TextView) view.findViewById(R.id.tv_card_money);
				if(isClick && CardActivity.this.position == position) {
					tvDetail.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_tran_main));
					tvCardName.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_tran_main));
					tvCardMoney.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_tran_main));
					tvDel.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_tran_main));
				} else {
					tvDetail.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_item_bg));
					tvCardName.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_item_bg));
					tvCardMoney.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_item_bg));
					tvDel.setBackgroundColor(CardActivity.this.getResources().getColor(R.color.color_item_bg));
				}

				return view;
			}			
		};
		listCard.setAdapter(adapter);
		
		//钱包余额设置
		tvTitleCardMoney.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CardActivity.this, SettingsActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
				
		//列表点击
		listCard.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//让总计不可点击
				if(position == list.size()) return;
				
				//点击选中取消选择
				if(isClick && CardActivity.this.position == position) {
					isClick = false;
			        CardActivity.this.saveId = -1;
			        CardActivity.this.etCardName.setText("");
			        CardActivity.this.etCardMoney.setText("");
			        CardActivity.this.tvCardMoneyStart.setText("");
			        CardActivity.this.tvCardMoneyBalance.setText("");
					adapter.notifyDataSetChanged();
					return;
				}
				
				ListView lv = (ListView) parent;
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) lv.getItemAtPosition(position);
		        int cardId = Integer.parseInt(map.get("cardid"));
		        CardActivity.this.saveId = cardId;
		        String cardName = map.get("cardname");
		        String cardMoney = map.get("cardmoneyvalue");
		        String cardMoneyStart = map.get("cardmoneystart");
		        String cardMoneyBalance = map.get("cardmoneyvalue");
		        CardActivity.this.etCardName.setText(cardName);
		        CardActivity.this.etCardMoney.setText(cardMoney);
		        CardActivity.this.tvCardMoneyStart.setText(cardMoneyStart);
		        CardActivity.this.tvCardMoneyBalance.setText(cardMoneyBalance);
		        
		        if(cardId == 0) {
		        	CardActivity.this.etCardName.setEnabled(false);
		        	CardActivity.this.etCardName.setTextColor(getResources().getColor(R.color.color_text_disabled));
		        } else {
		        	CardActivity.this.etCardName.setEnabled(true);
		        	CardActivity.this.etCardName.setTextColor(getResources().getColor(R.color.color_text));
		        }
		        
		        isClick = true;
		        CardActivity.this.position = position;
		        adapter.notifyDataSetChanged();		        
			}		
		});

		//转账按钮
		tvTitleCardName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				transView = LayoutInflater.from(CardActivity.this).inflate(R.layout.layout_card_translate, new LinearLayout(CardActivity.this), false);
				spinerCardIn = (Spinner) transView.findViewById(R.id.sp_card_in);
				spinerCardIn.setAdapter(cardAdapter);
				spinerCardOut = (Spinner) transView.findViewById(R.id.sp_card_out);
				spinerCardOut.setAdapter(cardAdapter);
				cardMoneyEdit = (EditText) transView.findViewById(R.id.et_card_money);
				cardDateEdit = (EditText) transView.findViewById(R.id.et_card_date);
				cardDateEdit.setText(UtilityHelper.formatDate(curDate, "y-m-d-w"));
				cardDateEdit.setOnClickListener(new DateClickListenerImpl());
				
				Dialog dialog = new AlertDialog.Builder(CardActivity.this)
				    .setTitle(R.string.txt_trans)
					.setView(transView)
					.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String tCardMoney = cardMoneyEdit.getText().toString();
							if (!UtilityHelper.checkDouble(tCardMoney)) {
								Toast.makeText(CardActivity.this, getString(R.string.txt_card_jinetext), Toast.LENGTH_SHORT).show();
								return;
							}
							//转出
							cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), CardActivity.this);
							String tCardName = spinerCardOut.getSelectedItem().toString();
							int fCardId = cardAccess.findCardId(tCardName);
							double dCardMoney = cardAccess.findCardMoney(fCardId) - Double.parseDouble(tCardMoney);
							cardAccess.saveCard(fCardId, tCardName, String.valueOf(dCardMoney));
							//转入
							tCardName = spinerCardIn.getSelectedItem().toString();
							int tCardId = cardAccess.findCardId(tCardName);
							dCardMoney = cardAccess.findCardMoney(tCardId) + Double.parseDouble(tCardMoney);
							cardAccess.saveCard(tCardId, tCardName, String.valueOf(dCardMoney));
							cardAccess.close();
							//存转账							
							zhangAccess = new ZhuanZhangTableAccess(sqlHelper.getReadableDatabase());
							zhangAccess.addZhuanZhang(fCardId, tCardId, tCardMoney, curDate, "");
							zhangAccess.close();
							
							sharedHelper.setLocalSync(true);
				        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
				        	
							CardActivity.this.onCreate(null);
						}
					}).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					}).create();
				dialog.show();
			}			
		});
			
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CardActivity.this.setResult(Activity.RESULT_OK);
				CardActivity.this.close();
			}			
		});
		
		//转账明细
		ImageButton btnTitleTrans = (ImageButton) super.findViewById(R.id.btn_title_trans);
		btnTitleTrans.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CardActivity.this, ZhuanZhangDetailActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//保存按钮
		Button btnCardSave = (Button) super.findViewById(R.id.btn_card_save);
		btnCardSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String cardName = CardActivity.this.etCardName.getText().toString().trim();
				if (cardName.equals("")) {
					Toast.makeText(CardActivity.this, getString(R.string.txt_card_name) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}
				String cardMoney = CardActivity.this.etCardMoney.getText().toString().trim();
				if (cardMoney.equals("")) {
					cardMoney = "0";
				}
				
				double newMoney = 0;
				try {
					newMoney = Double.parseDouble(cardMoney);
				} catch(Exception e) {
					e.printStackTrace();
					cardMoney = "0";
				}
			
				String cardMoneyStart = CardActivity.this.tvCardMoneyStart.getText().toString().trim();
				String cardMoneyBalance = CardActivity.this.tvCardMoneyBalance.getText().toString().trim();
				if(saveId != -1) {
					//卡金额等于新的加上初始减去当前
					newMoney = Double.parseDouble(cardMoney) + Double.parseDouble(cardMoneyStart) - Double.parseDouble(cardMoneyBalance);
				}

				Boolean result = false;
				String fixMoneyText = sharedHelper.getFixMoneyType();				

				if(fixMoneyText.equals("")) {
					cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), CardActivity.this);
					result = cardAccess.saveCard(saveId, cardName, String.valueOf(newMoney));
					cardAccess.close();
				}
				
				//设置了差账
				if(!fixMoneyText.equals("")) {
					//新增要添加卡
					if(saveId == -1) {
						cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), CardActivity.this);
						saveId = cardAccess.saveCard(cardName, "0");
						cardAccess.close();
					}
					
					//差账金额等于新的减去当前
					newMoney = Math.abs(Double.parseDouble(cardMoney) - Double.parseDouble(cardMoneyBalance));
					if(newMoney > 0) {
						String itemType = Double.parseDouble(cardMoney) > Double.parseDouble(cardMoneyBalance) ? "sr" : "zc";
						String itemName = fixMoneyText;
						String itemPrice = String.valueOf(newMoney);
						String itemBuyDate = UtilityHelper.getCurDateTime();
		
						categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
						int catId = categoryAccess.getMaxUseCategoryId();
						categoryAccess.close();
					
						itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
						result = itemAccess.addItem(itemType, itemName, itemPrice, itemBuyDate, catId, 0, 0, "", 0, saveId);
						itemAccess.close();
					}
				}				
				
		        if(result) {
		        	sharedHelper.setLocalSync(true);
		        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
		        	
		        	saveId = -1;
		        	Toast.makeText(CardActivity.this, getString(R.string.txt_add_addsuccess), Toast.LENGTH_SHORT).show();
		        } else {
		        	Toast.makeText(CardActivity.this, getString(R.string.txt_add_adderror), Toast.LENGTH_SHORT).show();
		        }

		        isClick = false;
                CardActivity.this.onCreate(null);
			}			
		});
	}
	
	//日期选择事件
	class DateClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View view) {
			String[] array = curDate.split("-");
			DatePickerDialog dateDialog = new MyDatePickerDialog(CardActivity.this, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month, int day) {
					String date = UtilityHelper.formatDate(year + "-" + (month + 1) + "-" + day, "");
					curDate = date;
					cardDateEdit.setText(UtilityHelper.formatDate(curDate, "y-m-d-w"));
				}					
			}, Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]));
			dateDialog.show();
		}
	}
	
	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CardActivity.this.setResult(Activity.RESULT_OK);
			CardActivity.this.close();
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
			//isClick = false;
			this.onCreate(null);
		}
	}
	
}
