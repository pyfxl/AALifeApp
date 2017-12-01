package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

public class AddActivity extends Activity {
	private ArrayAdapter<CharSequence> adapter = null;
	private List<CharSequence> list = null;
	private Spinner spinner = null;
	private SQLiteOpenHelper sqlHelper = null;
	private CategoryTableAccess categoryAccess = null;
	private ItemTableAccess itemAccess = null;
	private SharedHelper sharedHelper = null;
	private String curDate = "";
	private String curTime = "";
	private String fromDate = "";
	private int recommend = 0;
	private int itemId = 0;
	private String itemName = "";
	private String itemPrice = "";
	private int saveType = 1;
	
	private String ztName = "";
	private Spinner spZhuanTi = null;
	private ArrayAdapter<CharSequence> ztAdapter = null;
	private ZhuanTiTableAccess zhuanTiAccess = null;
	private List<CharSequence> ztList = null;
	
	private String cardName = "";
	private Spinner spinerCard = null;
	private ArrayAdapter<CharSequence> cardAdapter = null;
	private CardTableAccess cardAccess = null;
	private List<CharSequence> cardList = null;
	
	private AutoCompleteTextView etAddItemName = null;
	private ArrayAdapter<CharSequence> nameAdapter = null;
	private List<CharSequence> nameList = null;
	private EditText etAddItemPrice = null;
	private EditText etAddItemBuyDate = null;
	private final int FIRST_REQUEST_CODE = 1;

	private String curDate2 = "";
	private String curDate0 = "";
	private Spinner spRegionType = null;
	private ArrayAdapter<CharSequence> regionAdapter = null;
	private EditText etAddItemBuyDate2 = null;
	private TextView tvRegionTo = null;
	private int regionId = 0;
	private int monthRegion = 0;
	private String regionType = "";
	private String regionFirst = "";
	
	private Spinner spItemType = null;
	private ArrayAdapter<CharSequence> itemAdapter = null;
	private String itemType = "zc";
	private String[] itemTypeArr;
	private RadioGroup rgItemType = null;
	private RadioButton radioZhiChu = null;
	private RadioButton radioJieChu = null;
	private RadioButton radioHuanChu = null;
	
	private SimpleAdapter adapterSmart = null;
	private List<Map<String, String>> listCat = null;
	private List<Map<String, String>> listItem = null;
	private List<Map<String, String>> listPrice = null;
	private List<Map<String, String>> listDate = null;
	private List<Map<String, String>> listCard = null;
	private ListView listAddSmart = null;
	private int smartFlag = 1;
	private SlidingDrawer sdAddSmart = null;
	private LinearLayout laySmartBack = null;
	private int screenWidth = 0;
	private int screenHeight = 0;
	
	//百度语音识别对话框  
    private BaiduASRDigitalDialog mDialog = null;   
    //应用授权信息   
    private String API_KEY="tdW7auX1OxkPwu7BWdQ06RnY";  
    private String SECRET_KEY="XmjVFoFxN6B3gR9lLxOc9k1iEimFms6b"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvCatType = (TextView) super.findViewById(R.id.tv_cattype);
		textPaint = tvCatType.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvItemName = (TextView) super.findViewById(R.id.tv_itemname);
		textPaint = tvItemName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvItemType = (TextView) super.findViewById(R.id.tv_itemtype);
		textPaint = tvItemType.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvItemPrice = (TextView) super.findViewById(R.id.tv_itemprice);
		textPaint = tvItemPrice.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvItemBuyDate = (TextView) super.findViewById(R.id.tv_itembuydate);
		textPaint = tvItemBuyDate.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvRegion = (TextView) super.findViewById(R.id.tv_region);
		textPaint = tvRegion.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvZhuanTi = (TextView) super.findViewById(R.id.tv_zhuanti);
		textPaint = tvZhuanTi.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvMoney = (TextView) super.findViewById(R.id.tv_money);
		textPaint = tvMoney.getPaint();
		textPaint.setFakeBoldText(true);
				
		//数据库
		sqlHelper = new DatabaseHelper(this);

		//初始化
		sharedHelper = new SharedHelper(this);				
		spinner = (Spinner) super.findViewById(R.id.sp_add_cattype);
		etAddItemName = (AutoCompleteTextView) super.findViewById(R.id.et_add_itemname);
		etAddItemPrice = (EditText) super.findViewById(R.id.et_add_itemprice);
		listAddSmart = (ListView) super.findViewById(R.id.list_add_smart);
		sdAddSmart = (SlidingDrawer) super.findViewById(R.id.slidingDrawer1);
		laySmartBack = (LinearLayout) super.findViewById(R.id.lay_smart_back);
		laySmartBack.setVisibility(View.GONE);
		etAddItemBuyDate = (EditText) super.findViewById(R.id.et_add_itembuydate);
		etAddItemBuyDate2 = (EditText) super.findViewById(R.id.et_add_itembuydate2);
		spZhuanTi = (Spinner) super.findViewById(R.id.sp_zhuanti);
		spinerCard = (Spinner) super.findViewById(R.id.spinner_card);
		
		//获取数据
		Intent intent = super.getIntent();
		ztName = intent.getStringExtra("ztname");	
		cardName = intent.getStringExtra("cardname");
		
		//固定消费
		String[] regionTypeArr = getResources().getStringArray(R.array.regiontype);
		tvRegionTo = (TextView) super.findViewById(R.id.tv_region_to);
		spRegionType = (Spinner) super.findViewById(R.id.sp_regiontype);
		regionAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, regionTypeArr);
		regionAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spRegionType.setAdapter(regionAdapter);
		
		//收支分类
		itemTypeArr = getResources().getStringArray(R.array.itemtype);
		spItemType = (Spinner) super.findViewById(R.id.sp_itemtype);
		itemAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, itemTypeArr);
		itemAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spItemType.setAdapter(itemAdapter);
		rgItemType = (RadioGroup) super.findViewById(R.id.rg_itemtype);
		radioZhiChu = (RadioButton) super.findViewById(R.id.radio_zhichu);
		radioJieChu = (RadioButton) super.findViewById(R.id.radio_jiechu);
		radioHuanChu = (RadioButton) super.findViewById(R.id.radio_huanchu);
		
		//百度语音
		if (mDialog == null) {
			Bundle bundle = new Bundle(); 
			bundle.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);  
			bundle.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);
			bundle.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG); 
	        mDialog = new BaiduASRDigitalDialog(this, bundle); 
	        mDialog.setDialogRecognitionListener(new DialogRecognitionListener() {  
	            @Override  
	            public void onResults(Bundle results) {  
	                ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION) : null;      
	                if (rs != null && rs.size() > 0) {  	                	
	                	Toast.makeText(AddActivity.this, rs.get(0).replace("吧", "八"), Toast.LENGTH_SHORT).show();
	                	
	                	VoiceHelper voice = new VoiceHelper();
	                	String[] result = voice.splitWords(rs.get(0));
	                	
	                	etAddItemName.setText(result[0]);
	                	etAddItemName.dismissDropDown();
	                	
	                	etAddItemPrice.setText(result[1]);
	                }
	            }
	        }); 
		}
        
		//语音输入
		ImageButton btnAddVoice = (ImageButton) super.findViewById(R.id.btn_add_voice);
		btnAddVoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mDialog != null) {
					mDialog.show();
				}
			}			
		});
				
		//文本框点击关闭Smart
		etAddItemName.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(sdAddSmart.isOpened()) {
					sdAddSmart.animateClose();
				}
			}			
		});
		etAddItemName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(sdAddSmart.isOpened()) {
					sdAddSmart.animateClose();
				}
			}			
		});
		etAddItemPrice.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(sdAddSmart.isOpened()) {
					sdAddSmart.animateClose();
				}
			}			
		});
		etAddItemPrice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(sdAddSmart.isOpened()) {
					sdAddSmart.animateClose();
				}
			}			
		});
		
		//商品名称List
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		nameList = itemAccess.findAllItemName();
		itemAccess.close();
		nameAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner_dropdown, nameList);
		etAddItemName.setAdapter(nameAdapter);
		
		//设置SmartLeft
		MarginLayoutParams mp = (MarginLayoutParams) sdAddSmart.getLayoutParams();
		int marginTop = mp.topMargin;
		int handleWidth = getResources().getDimensionPixelSize(R.dimen.smart_handle_width);
		DisplayMetrics dm = getResources().getDisplayMetrics();  
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.setMargins(screenWidth/2 - handleWidth, marginTop, 0, 0);
        sdAddSmart.setLayoutParams(params);
		
		//获取数据
		recommend = intent.getIntExtra("recommend", 0);
		fromDate = intent.getStringExtra("date");
		if(fromDate != null && !fromDate.equals("")) {
			curDate = fromDate;
		} else if(curDate.equals("")) {
			curDate = UtilityHelper.getCurDate();
		}
		
		//设置分类RadioGroup
		final String type = intent.getStringExtra("type");
		setItemType(type);
		rgItemType.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int radioId) {
				if(radioZhiChu.getId() == radioId) {
					itemType = (type.equals("zc") ? "zc" : "sr");
				} else if(radioJieChu.getId() == radioId) {
					itemType = (type.equals("zc") ? "jc" : "hr");
				} else if(radioHuanChu.getId() == radioId) {
					itemType = (type.equals("zc") ? "hc" : "jr");
				}
			}
		});

		//AddSmart关闭
		sdAddSmart.setOnDrawerCloseListener(new OnDrawerCloseListener(){
			@Override
			public void onDrawerClosed() {
			}			
		});
		
		//ListSmart点击
		listAddSmart.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, String> map = new HashMap<String, String>();
				itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
				try {
					switch(smartFlag) {
					case 0:
						laySmartBack.setVisibility(View.VISIBLE);
						spItemType.setSelection(position);
						setCurItemType(position);
						setListSmartData(listCat);
						smartFlag = 1;
						break;
					case 1:
						map = (Map<String, String>) listCat.get(position);
						smartFlag = 2;
						setCurCategory(map.get("name"));
						listItem = itemAccess.findAllItemByCatId(map.get("id"));
						setListSmartData(listItem);
						break;
					case 2:
						map = (Map<String, String>) listItem.get(position);
						smartFlag = 3;
						etAddItemName.setText(map.get("name"));
						etAddItemName.dismissDropDown();
						listPrice = itemAccess.findAllPriceByName(map.get("name"));
						setListSmartData(listPrice);
						break;
					case 3:
						map = (Map<String, String>) listPrice.get(position);
						smartFlag = 4;
						etAddItemPrice.setText(map.get("value"));
						listDate = getListDateData(curDate);
						setListSmartData(listDate);
						//etAddItemPrice.requestFocus();
						break;
					case 4:
						map = (Map<String, String>) listDate.get(position);
						smartFlag = 5;
						curDate = map.get("id");
						etAddItemBuyDate.setText(map.get("name"));
						//sdAddSmart.animateClose();
						cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), AddActivity.this);
						listCard = cardAccess.findAllCardListMap();
						cardAccess.close();
						setListSmartData(listCard);
						break;
					case 5:
						map = (Map<String, String>) listCard.get(position);
						setCurCard(map.get("name"));
						sdAddSmart.animateClose();
						break;
					}		
				} catch (Exception e) {
					e.printStackTrace();
				}
				itemAccess.close();
			}			
		});

		//智能返回点击
		Button btnSmartBack = (Button) super.findViewById(R.id.btn_smart_back);
		btnSmartBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				switch(smartFlag) {
				case 1:
					laySmartBack.setVisibility(View.GONE);
					smartFlag = 0;
					setListSmartData(UtilityHelper.getItemTypeList(itemTypeArr));
					break;
				case 2:
					smartFlag = 1;
					setListSmartData(listCat);
					break;
				case 3:
					smartFlag = 2;
					setListSmartData(listItem);
					break;
				case 4:
					smartFlag = 3;
					setListSmartData(listPrice);
					break;
				case 5:
					smartFlag = 4;
					setListSmartData(listDate);
					break;
				}
			}			
		});

		//添加继续按钮
		Button btnAddContinue = (Button) super.findViewById(R.id.btn_add_continue);
		btnAddContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				saveType = 1;
				
				if(!checkSave()) {
					return;
				}				
				
				if(!regionType.equals("")) {
					if(!checkRegion()) {
						return;
					}
					
					showRegionView();
				} else {
					submitContinue();
				}
			}			
		});

		//添加返回按钮
		Button btnAddBack = (Button) super.findViewById(R.id.btn_add_back);
		btnAddBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				saveType = 0;
				
				if(!checkSave()) {
					return;
				}				
				
				if(!regionType.equals("")) {
					if(!checkRegion()) {
						return;
					}	
					
					showRegionView();
				} else {
					submitBack();
				}
			}			
		});	

		//X2添加按钮
		Button btnAddSame = (Button) super.findViewById(R.id.btn_add_same);
		btnAddSame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!checkSave()) {
					return;
				}	
				
				if(!regionType.equals("")) {
					Toast.makeText(AddActivity.this, getString(R.string.txt_add_region_x2), Toast.LENGTH_SHORT).show();
					return;
				}
				
				int result = saveItem();
				if(result == 1) {
					Toast.makeText(AddActivity.this, getString(R.string.txt_add_addsuccess), Toast.LENGTH_SHORT).show();
					
					setListSmartData(listCat);
					smartFlag = 1;
					laySmartBack.setVisibility(View.VISIBLE);
				} else if (result == 0) {
					Toast.makeText(AddActivity.this, getString(R.string.txt_add_adderror), Toast.LENGTH_SHORT).show();
				}
			}			
		});	
		
		//编辑按钮
		Button btnDayEdit = (Button) super.findViewById(R.id.btn_day_edit);
		btnDayEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				saveType = 2;
				
				if(!checkSave()) {
					return;
				}				
				
				if(!regionType.equals("")) {
					if(!checkRegion()) {
						return;
					}	
					
					showRegionView();
				} else {
					submitEdit();
				}
				
			}			
		});
		
		//删除按钮
		Button btnDayDelete = (Button) super.findViewById(R.id.btn_day_delete);
		btnDayDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Dialog dialog = new AlertDialog.Builder(AddActivity.this)
					.setTitle(R.string.txt_tips)
					.setMessage(R.string.txt_day_deletenote)
					.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
							Boolean result = false;
							if(regionId > 0) {
								result = itemAccess.deleteRegion(regionId);
							} else {
							    result = itemAccess.deleteItem(itemId);
							}
							itemAccess.close();
							
					        if(result) {					
								sharedHelper.setLocalSync(true);
					        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
					        	
					        	Toast.makeText(AddActivity.this, R.string.txt_day_deletesuccess, Toast.LENGTH_SHORT).show();
					        	
					        	AddActivity.this.setResult(Activity.RESULT_OK);
					        	AddActivity.this.close();
					        } else {
					        	Toast.makeText(AddActivity.this, R.string.txt_day_deleteerror, Toast.LENGTH_SHORT).show();
					        }
						}
					}).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					}).create();
				dialog.show();		
			}
		});
		
		//区间选择
		spRegionType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0) {
					etAddItemBuyDate2.setVisibility(View.GONE);
					tvRegionTo.setVisibility(View.GONE);
					regionType = "";
				    regionId = 0;
	        	    monthRegion = 0;
				} else {
					etAddItemBuyDate2.setVisibility(View.VISIBLE);
					tvRegionTo.setVisibility(View.VISIBLE);
					switch(position) {
						case 1:
							regionType = "d";
							break;
						case 2:
							regionType = "w";
							break;
						case 3:
							regionType = "m";
							break;
						case 4:
							regionType = "j";
							break;
						case 5:
							regionType = "y";
							break;
						case 6:
							regionType = "b";
							break;
					}
					if(regionFirst.equals(regionType)) {
						curDate2 = curDate0;
					} else {
					    curDate2 = UtilityHelper.getRegionDate(curDate, regionType);
					}
					etAddItemBuyDate2.setText(UtilityHelper.formatDate(curDate2, "y-m-d-w"));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}			
		});	
		
		//分类选择
		spItemType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setCurItemType(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}			
		});		
				
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.putExtra("date", curDate);
				AddActivity.this.setResult(Activity.RESULT_OK, intent);
				AddActivity.this.close();
			}			
		});
		
		//计算器按钮
		ImageButton btnTitleCalc = (ImageButton) super.findViewById(R.id.btn_title_calc);
		btnTitleCalc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final MyCalcView calcView = new MyCalcView(AddActivity.this);
				calcView.setGravity(Gravity.CENTER);
				
				final AlertDialog calcDialog = new AlertDialog.Builder(AddActivity.this).create();
				calcDialog.setTitle(R.string.txt_calculator);
				calcDialog.setView(calcView, 0, 0, 0, 0);
				calcDialog.show();
				android.view.WindowManager.LayoutParams lp = calcDialog.getWindow().getAttributes();
				lp.width = (int) (screenWidth * 0.95);
				calcDialog.getWindow().setAttributes(lp);
				
				Button btnOk = (Button) calcView.findViewById(R.id.buttonok);
				btnOk.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String result = calcView.resultText;
						if(!result.equals("")) {
							calcDialog.cancel();
							etAddItemPrice.setText(result);
						} else {
							Toast.makeText(AddActivity.this, R.string.txt_calcfirst, Toast.LENGTH_SHORT).show();
						}
					}					
				});
			}		
		});
				
		//类别编辑
		ImageButton btnAddCatEdit = (ImageButton) super.findViewById(R.id.btn_add_catedit);
		btnAddCatEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AddActivity.this, CategoryEditActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//专题按钮
		ImageButton btnAddZhuanTi = (ImageButton) super.findViewById(R.id.btn_add_zhuanti);
		btnAddZhuanTi.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AddActivity.this, ZhuanTiActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}
		});
		
		//固定消费按钮
		ImageButton btnAddRegion = (ImageButton) super.findViewById(R.id.btn_add_region);
		btnAddRegion.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Dialog dialog = new AlertDialog.Builder(AddActivity.this)
					.setTitle(R.string.txt_tips)
					.setMessage(R.string.txt_add_regionhelp)
					.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
						}
					}).create();
				dialog.show();
			}
		});
		
		//钱包按钮
		ImageButton btnAddMoney = (ImageButton) super.findViewById(R.id.btn_add_money);
		btnAddMoney.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AddActivity.this, CardActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}
		});
		
		//数据填充
		setListData();
		
		//编辑初始
		itemId = intent.getIntExtra("itemid", 0);
		if(itemId > 0) {
			itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
			Map<String, String> items = itemAccess.findItemById(itemId);
			itemAccess.close();
			
			TextView tvAddTitle = (TextView) super.findViewById(R.id.tv_add_title);
			tvAddTitle.setText(R.string.txt_tab_edit);

			LinearLayout layAdd = (LinearLayout) super.findViewById(R.id.lay_add);
			LinearLayout layEdit = (LinearLayout) super.findViewById(R.id.lay_edit);
			layAdd.setVisibility(View.GONE);
			layEdit.setVisibility(View.VISIBLE);
			
			//小米检测到不明bug
			try {
				setEditValue(items);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		//选择日期
		etAddItemBuyDate.setText(UtilityHelper.formatDate(curDate, "y-m-d-w"));
		etAddItemBuyDate.setOnClickListener(new DateClickListenerImpl());
		ImageButton btnAddDate = (ImageButton) super.findViewById(R.id.btn_add_date);
		btnAddDate.setOnClickListener(new DateClickListenerImpl());
		
		//区间日期
		etAddItemBuyDate2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] array = curDate2.split("-");
				DatePickerDialog dateDialog = new MyDatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						String date = UtilityHelper.formatDate(year + "-" + (month + 1) + "-" + day, "");
						curDate2 = date;
						etAddItemBuyDate2.setText(UtilityHelper.formatDate(curDate2, "y-m-d-w"));
					}					
				}, Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]));
				dateDialog.show();
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
			intent.putExtra("date", curDate);
			AddActivity.this.setResult(Activity.RESULT_OK, intent);
			AddActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (mDialog != null) {  
            mDialog.dismiss();  
        }
	}

	//设置编辑默认值
	protected void setEditValue(Map<String, String> items) {
		String catName = getCategoryName(Integer.parseInt(items.get("catid")));
		setCurCategory(catName);
		setCurItemType(items.get("itemtype"));
		
		itemName = items.get("itemname");
		etAddItemName.setText(itemName);
		etAddItemName.dismissDropDown();
		
		itemPrice = items.get("itemprice");
		etAddItemPrice.setText(itemPrice);
		
		String[] date = items.get("itembuydate").split(" ");
		curDate = date[0];
		curTime = date.length > 1 ? date[1] : UtilityHelper.getCurTime();
		
		regionId = Integer.parseInt(items.get("regionid"));
		regionType = regionFirst = items.get("regiontype");
		//初始区间
		if(!regionType.equals("")) {
			itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
			String[] regionDate = itemAccess.getRegionDate(regionId);
			itemAccess.close();
			
			curDate = regionDate[0];
			curDate2 = curDate0 = regionDate[1];
			etAddItemBuyDate.setText(UtilityHelper.formatDate(curDate, "y-m-d-w"));
			etAddItemBuyDate2.setText(UtilityHelper.formatDate(curDate2, "y-m-d-w"));
			
			etAddItemBuyDate2.setVisibility(View.VISIBLE);
			tvRegionTo.setVisibility(View.VISIBLE);
			
			if(regionType.equals("d")) {
				spRegionType.setSelection(1);
			} else if(regionType.equals("w")) {
				spRegionType.setSelection(2);
			} else if(regionType.equals("m")) {
				spRegionType.setSelection(3);
			} else if(regionType.equals("j")) {
				spRegionType.setSelection(4);
			} else if(regionType.equals("y")) {
				spRegionType.setSelection(5);
			} else if(regionType.equals("b")) {
				spRegionType.setSelection(6);
			} else {
				spRegionType.setSelection(0);
			}
		}
		
		String ztName = getZhuanTi(Integer.parseInt(items.get("ztid")));
		setCurZhuanTi(ztName);
		
		int cardId = Integer.parseInt(items.get("cardid"));
		if(cardId > 0) {
			cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), this);
			String cardName = cardAccess.findCardName(cardId);
			cardAccess.close();
			setCurCard(cardName);
		} else {
			spinerCard.setSelection(0);
		}
	}
	
	//保存方法
	protected int saveItem() {
		try {
			String catType = spinner.getSelectedItem().toString();
			categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
			int catId = categoryAccess.findCatIdByName(catType);
			sharedHelper.setCategory(spinner.getSelectedItemPosition());
			categoryAccess.close();	
			
			String itemName = etAddItemName.getText().toString().trim();
			String itemPrice = etAddItemPrice.getText().toString().trim();

			int ztId = 0;
			if(spZhuanTi.getSelectedItemPosition() > 0) {
				String ztName = spZhuanTi.getSelectedItem().toString();
				zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
				ztId = zhuanTiAccess.findZhuanTiId(ztName);
				zhuanTiAccess.close();
			}

			int cardId = 0;
			if(spinerCard.getSelectedItemPosition() > 0) {
				String cardName = spinerCard.getSelectedItem().toString();
				cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), this);
				cardId = cardAccess.findCardId(cardName);
				cardAccess.close();
			}
			sharedHelper.setCardId(spinerCard.getSelectedItemPosition());
			
			curTime = UtilityHelper.getCurTime();
			
			if(!regionType.equals("")) {
				if(!checkRegion()) {
					return 2;
				}
				
				if(regionId == 0) {
					itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
					regionId = itemAccess.getMaxRegionId();
					itemAccess.close();
				}				
			}
			
			String itemBuyDate = "";
			itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
			Boolean result = false;
			//用或是因为可能有两个日期相同的情况
			if(regionId > 0 || monthRegion > 0) {
				result = itemAccess.deleteRegion(regionId);
				
				for(int i=0; i <= monthRegion; i++) {					
					itemBuyDate = getRegionDate(i);
					if(itemBuyDate.equals("")) {
						continue;
					}
					
				    result = itemAccess.addItem(itemType, itemName, itemPrice, itemBuyDate, catId, recommend, regionId, regionType, ztId, cardId);
				}
				
				//删除DeleteTable记录
				result = itemAccess.deleteRegionBack(regionId);
			} else {
				itemBuyDate = curDate + " " + curTime;
				if(itemId > 0) {
					result = itemAccess.updateItem(itemId, itemType, itemName, itemPrice, itemBuyDate, catId, ztId, cardId);
				} else {
					result = itemAccess.addItem(itemType, itemName, itemPrice, itemBuyDate, catId, recommend, regionId, regionType, ztId, cardId);
				}
			}
			itemAccess.close();
			
	        if(result) {
	        	sharedHelper.setLocalSync(true);
	        	sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
	        	
	        	//成功添加后将id设为0，防止继续添加时不会获取新id
	        	regionId = 0;
	        	monthRegion = 0;
	        	
	        	return 1;
	        } else {
	        	return 0;
	        } 
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, getString(R.string.txt_add_cattype) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
			return 2;
		}
	}

	//检查保存
	protected boolean checkSave() {
		String itemName = etAddItemName.getText().toString().trim();
		if (itemName.equals("")) {
			Toast.makeText(this, getString(R.string.txt_add_itemname) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
			return false;
		}

		String itemPrice = etAddItemPrice.getText().toString().trim();
		if (!UtilityHelper.checkDouble(itemPrice)) {
			Toast.makeText(this, getString(R.string.txt_add_itemprice) + getString(R.string.txt_nogood), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	//检查区间
	protected boolean checkRegion() {
		int maxRegion = 0;		
		monthRegion = UtilityHelper.getMonthRegion(curDate, curDate2, regionType);
		String regionStr = "";
		
        if (regionType.equals("d") || regionType.equals("b")) {
            maxRegion = 92;
            regionStr = getString(R.string.txt_add_regionday);
        } else if (regionType.equals("w")) {
            maxRegion = (int)Math.floor((double)92 / 7);
            regionStr = getString(R.string.txt_add_regionday);
        } else if (regionType.equals("m")) {
            maxRegion = 36;
            regionStr = getString(R.string.txt_add_regionmonth);
        } else if (regionType.equals("j")) {
            maxRegion = 12;
            regionStr = getString(R.string.txt_add_regionyear);
        } else if (regionType.equals("y")) {
            maxRegion = 15;
            regionStr = getString(R.string.txt_add_regionyear);
        }
		
		if(monthRegion <= 0 || monthRegion >= maxRegion) {
			Toast.makeText(this, getString(R.string.txt_add_regionerr) + regionStr, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	//显示固定消费预览
	protected void showRegionView() {
		ListView regionView = new ListView(AddActivity.this);
		regionView.setDivider(null);
		List<Map<String, String>> list = getRegionList();
		
		SimpleAdapter adapter = new SimpleAdapter(AddActivity.this, list, R.layout.list_regionview, new String[] { "itemname", "itembuydate", "itemprice" }, new int[] { R.id.tv_rank_itemname, R.id.tv_rank_itembuydate, R.id.tv_rank_itemprice });
		regionView.setAdapter(adapter);
		
		Dialog dialog = new AlertDialog.Builder(AddActivity.this)
		    .setTitle(getString(R.string.txt_add_region_tips) + "（" + list.size() + "）")
			.setView(regionView)
			.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					switch(saveType) {
						case 0:
							submitBack();
							break;
						case 1:
							submitContinue();
							break;
						case 2:
							submitEdit();
							break;
					}
				}
			}).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}
			}).create();
		dialog.show();	
		
		android.view.WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.height = (int) (screenHeight * 0.75);
		dialog.getWindow().setAttributes(lp);
	}
	
	//保存继续事件
	protected void submitContinue() {
		int result = saveItem();
		if(result == 1) {
			Toast.makeText(AddActivity.this, getString(R.string.txt_add_addsuccess), Toast.LENGTH_SHORT).show();
			etAddItemName.setText("");
			etAddItemName.requestFocus();
			etAddItemPrice.setText("");
			
			setListSmartData(listCat);
			smartFlag = 1;
			laySmartBack.setVisibility(View.VISIBLE);
		} else if (result == 0) {
			Toast.makeText(AddActivity.this, getString(R.string.txt_add_adderror), Toast.LENGTH_SHORT).show();
		}
	}
	
	//保存返回事件
	protected void submitBack() {
		int result = saveItem();
		if(result == 1) {
			Toast.makeText(AddActivity.this, getString(R.string.txt_add_addsuccess), Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent();
			intent.putExtra("date", curDate);
			AddActivity.this.setResult(Activity.RESULT_OK, intent);
			AddActivity.this.close();
		} else if (result == 0) {
			Toast.makeText(AddActivity.this, getString(R.string.txt_add_adderror), Toast.LENGTH_SHORT).show();
		}
	}
	
	//编辑保存事件
	protected void submitEdit() {
		int result = saveItem();
		if(result == 1) {
			Toast.makeText(AddActivity.this, getString(R.string.txt_edit_editsuccess), Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent();
			intent.putExtra("itemname", itemName);
			intent.putExtra("date", curDate);
			AddActivity.this.setResult(Activity.RESULT_OK, intent);
			AddActivity.this.close();
		} else if (result == 0) {
			Toast.makeText(AddActivity.this, getString(R.string.txt_edit_editerror), Toast.LENGTH_SHORT).show();
		}
	}
	
	//取固定消费预览
	protected List<Map<String, String>> getRegionList() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String itemName = etAddItemName.getText().toString().trim();
		String itemPrice = etAddItemPrice.getText().toString().trim();
		String itemBuyDate = "";
		
		for(int i=0; i <= monthRegion; i++) {
			if(!regionType.equals("")) {
				itemBuyDate = getRegionDate(i);
				if(itemBuyDate.equals("")) {
					continue;
				}
			} else {
				itemBuyDate = curDate + " " + curTime;
				//regionId = 0;
			}
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("itemname", itemName);
			map.put("itemprice", "￥ " + UtilityHelper.formatDouble(Double.parseDouble(itemPrice), "0.0##"));
			map.put("itembuydate", UtilityHelper.formatDate(itemBuyDate, "y-m-d"));
			list.add(map);
		}
		
		return list;
	}
	
	//取区间第二日期
	protected String getRegionDate(int i) {
		String itemBuyDate = "";
		int workDay = Integer.parseInt(sharedHelper.getUserWorkDay());
		
		if(regionType.equals("d")) {
		    itemBuyDate = UtilityHelper.getNavDate(curDate, i, "d") + " " + curTime;
		} else if(regionType.equals("w")) {
		    itemBuyDate = UtilityHelper.getNavDate(curDate, i*7, "d") + " " + curTime;
		} else if(regionType.equals("m")) {
		    itemBuyDate = UtilityHelper.getNavDate(curDate, i, "m") + " " + curTime;
		} else if(regionType.equals("j")) {
		    itemBuyDate = UtilityHelper.getNavDate(curDate, i*3, "m") + " " + curTime;
		} else if(regionType.equals("y")) {
		    itemBuyDate = UtilityHelper.getNavDate(curDate, i, "y") + " " + curTime;
		} else if(regionType.equals("b")) {
		    String tempDate = UtilityHelper.getNavDate(curDate, i, "d");
		    if (UtilityHelper.getWorkDayFinal(tempDate, workDay)) {
                itemBuyDate = tempDate + " " + curTime;
            } else {
                return "";
            }
		}
		
		return itemBuyDate;
	}
	
	//设置钱包
	protected void setUserMoney(String price) {
		double userMoney = Double.parseDouble(sharedHelper.getUserMoney());
		if(itemType.equals("zc") || itemType.equals("jc") || itemType.equals("hc")) {
			userMoney -= Double.parseDouble(price);
		} else {
			userMoney += Double.parseDouble(price);
		}
		sharedHelper.setUserMoney(UtilityHelper.formatDouble(userMoney, "0.0##"));
	}
	
	//删除钱包
	protected double delUserMoney(String price) {
		double userMoney = Double.parseDouble(sharedHelper.getUserMoney());
		if(itemType.equals("zc") || itemType.equals("jc") || itemType.equals("hc")) {
			userMoney += Double.parseDouble(price);
		} else {
			userMoney -= Double.parseDouble(price);
		}
		
		return userMoney;
	}

	//绑定下拉数据
	protected void setListData() {
		//商品类别
		categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
		list = categoryAccess.findAllCategory();
		listCat = categoryAccess.findAllCategorySmart();
		categoryAccess.close();
		adapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, list);
		adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spinner.setAdapter(adapter);
		int category = sharedHelper.getCategory();
		if(category > 0) {
			spinner.setSelection(category);
		}
		
		//专题
		zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
		ztList = zhuanTiAccess.findAllZhuanTi();
		zhuanTiAccess.close();
		ztAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, ztList);
		ztAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spZhuanTi.setAdapter(ztAdapter);
		setCurZhuanTi(ztName);
		
		//钱包
		cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase());
		cardList = cardAccess.findAllCard();
		cardAccess.close();
		cardAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, cardList);
		cardAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spinerCard.setAdapter(cardAdapter);
		if(cardName==null || cardName.equals("")) {
			spinerCard.setSelection(sharedHelper.getCardId());
		} else {
			setCurCard(cardName);
		}
		
		//右边列表
		//setListSmartData(UtilityHelper.getItemTypeList(itemTypeArr));
		setListSmartData(listCat);
		laySmartBack.setVisibility(View.VISIBLE);
	}
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			setListData();
			if(data != null) {
				int catId = data.getIntExtra("catid", 0);
				String catName = getCategoryName(catId);
				setCurCategory(catName);
			}
		}
	}
	
	//设置ListSmart
	protected void setListSmartData(List<Map<String, String>> listSmart) {
		adapterSmart = new SimpleAdapter(this, listSmart, R.layout.list_addsmart, new String[] { "id", "name", "value" }, new int[] { R.id.tv_add_id, R.id.tv_add_name, R.id.tv_add_value });
		listAddSmart.setAdapter(adapterSmart);
	}

	//设置默认类别
	protected void setCurCategory(String catName) {
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i).toString();
			if (s.equals(catName)) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	//取类别名称
	protected String getCategoryName(int catId) {
		categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
		String catName = categoryAccess.findCatNameById(catId);
		categoryAccess.close();
		
		return catName;
	}

	//设置默认专题
	protected void setCurZhuanTi(String ztName) {
		for (int i = 0; i < ztList.size(); i++) {
			String s = ztList.get(i).toString();
			if (s.equals(ztName)) {
				spZhuanTi.setSelection(i);
				break;
			}
		}
	}
	
	//设置默认钱包
	protected void setCurCard(String cardName) {
		for (int i = 0; i < cardList.size(); i++) {
			String s = cardList.get(i).toString();
			if (s.equals(cardName)) {
				spinerCard.setSelection(i);
				break;
			}
		}
	}
	
	//取专题名称
	protected String getZhuanTi(int ztId) {
		zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
		String name = zhuanTiAccess.findZhuanTiName(ztId);
		zhuanTiAccess.close();
		
		return name;
	}
	
	//设置分类值	
	protected void setItemType(String type) {
		if (type == null) {
			itemType = "zc";
			spItemType.setVisibility(View.VISIBLE);
			rgItemType.setVisibility(View.GONE);
		} else if (type.equals("sr")) {
			itemType = "sr";
			radioZhiChu.setText(R.string.txt_type_shouru);
			radioJieChu.setText(R.string.txt_type_huanru);
			radioHuanChu.setText(R.string.txt_type_jieru);
		}
	}
	protected void setCurItemType(String type) {
		int position = 0;
		if(type.equals("zc")) {
			position = 0;
		} else if(type.equals("sr")) {
			position = 1;
		} else if(type.equals("jc")) {
			position = 2;
		} else if(type.equals("hr")) {
			position = 3;
		} else if(type.equals("jr")) {
			position = 4;
		} else if(type.equals("hc")) {
			position = 5;
		}
		
		spItemType.setSelection(position);
	}
	protected void setCurItemType(int position) {
		switch(position) {
			case 0:
				itemType = "zc";
				break;
			case 1:
				itemType = "sr";
				break;
			case 2:
				itemType = "jc";
				break;
			case 3:
				itemType = "hr";
				break;
			case 4:
				itemType = "jr";
				break;
			case 5:
				itemType = "hc";
				break;
		}
	}
	
	//取日期
	protected List<Map<String, String>> getListDateData(String date) {
		List<Map<String, String>> all = new ArrayList<Map<String, String>>();

		all.add(getListDateMap(date, -4));
		all.add(getListDateMap(date, -3));
		all.add(getListDateMap(date, -2));
		all.add(getListDateMap(date, -1));
		all.add(getListDateMap(date, 0));		
		all.add(getListDateMap(date, 1));
		all.add(getListDateMap(date, 2));
		all.add(getListDateMap(date, 3));
		all.add(getListDateMap(date, 4));
		
		return all;
	}

	//取日期
	protected Map<String, String> getListDateMap(String date, int value) {
		String tempDate = UtilityHelper.getNavDate(date, value, "d");
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", tempDate);
		map.put("name", UtilityHelper.formatDate(tempDate, "y-m-d-w"));
		
		return map;
	}
	
	//日期选择事件
	class DateClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View view) {
			String[] array = curDate.split("-");
			DatePickerDialog dateDialog = new MyDatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month, int day) {
					String date = UtilityHelper.formatDate(year + "-" + (month + 1) + "-" + day, "");
					curDate = date;
					etAddItemBuyDate.setText(UtilityHelper.formatDate(curDate, "y-m-d-w"));
				}					
			}, Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]));
			dateDialog.show();
		}
	}
	
}
