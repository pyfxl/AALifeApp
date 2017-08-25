package com.aalife.android;

import java.lang.ref.WeakReference;
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
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SQLiteOpenHelper sqlHelper = null;
	private SharedHelper sharedHelper = null;
	private MyHandler myHandler = new MyHandler(this);
	private ItemTableAccess itemAccess = null;
	private ListView listTotal = null;
	private List<Map<String, String>> list = null;
	private SimpleAdapter adapter = null;
	private TextView tvDateChoose = null;
	private String curDate = "";
	private String fromDate = "";
	private final int FIRST_REQUEST_CODE = 1;
	private Runnable runnable = null;
	private final int DELAY_TIME = 5 * 1000;
	private TextView tvUserMoney = null;
	private TextView tvDian = null;
	private ImageButton btnNotes = null;
	private String noteMessage = "";
	private boolean hasBackup = false;

	private Spinner spinerCard = null;
	private ArrayAdapter<CharSequence> cardAdapter = null;
	private CardTableAccess cardAccess = null;
	private List<CharSequence> cardList = null;
	private Spinner spinerType = null;
	private ArrayAdapter<CharSequence> typeAdapter = null;
	
	private RadioGroup radioGroup = null;
	private RadioButton radioAll = null;
	private RadioButton radioYear = null;
	private RadioButton radioMonth = null;
	private RadioButton radioDay = null;
	private TextView tvTotalJieCun = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		//虚拟按键菜单
		UtilityHelper.showMenuButton(this);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvTitleTotal = (TextView) super.findViewById(R.id.tv_title_total);
		textPaint = tvTitleTotal.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvTitleJieCun = (TextView) super.findViewById(R.id.tv_title_jiecun);
		textPaint = tvTitleJieCun.getPaint();
		textPaint.setFakeBoldText(true);
		tvTotalJieCun = (TextView) super.findViewById(R.id.tv_total_jiecun);
		textPaint = tvTotalJieCun.getPaint();
		textPaint.setFakeBoldText(true);
		
		//初始化
		sharedHelper = new SharedHelper(this);
		listTotal = (ListView) super.findViewById(R.id.list_total);
		tvUserMoney = (TextView) super.findViewById(R.id.tv_usermoney);
		tvDian = (TextView) super.findViewById(R.id.tv_dian);
		btnNotes = (ImageButton) super.findViewById(R.id.btn_about_notes);
		spinerCard = (Spinner) super.findViewById(R.id.spinner_card);
		spinerType = (Spinner) super.findViewById(R.id.spinner_type);
		
		//初始Radio
		radioGroup = (RadioGroup) super.findViewById(R.id.radioGroup1);
		radioAll = (RadioButton) super.findViewById(R.id.radio_all);
		radioYear = (RadioButton) super.findViewById(R.id.radio_year);
		radioMonth = (RadioButton) super.findViewById(R.id.radio_month);
		radioDay = (RadioButton) super.findViewById(R.id.radio_day);
		setRadioDefault();
	
		//数据库
		sqlHelper = new DatabaseHelper(this);

		//当前日期
		curDate = UtilityHelper.getCurDate();
					
		//显示公告
		if(UtilityHelper.checkInternet(this, 1)) {
			new Thread(new Runnable(){
				@Override
				public void run() {
					String[] result = UtilityHelper.getPhoneMessage();
					
					Bundle bundle = new Bundle();
					bundle.putStringArray("result", result);
					Message message = new Message();
					message.what = 6;
					message.setData(bundle);
					myHandler.sendMessage(message);
				}
			}).start();
		}
		
		//显示公告按钮
		btnNotes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				showNotes();
			}			
		});
		
		//钱包选择
		spinerCard.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position > 0) {
					spinerType.setSelection(0);
					sharedHelper.setTypeId(0);
					
					sharedHelper.setCardId(position-1);
					updateUserMoney(position, "right");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {	
				
			}			
		});	

		//分类选择
		spinerType.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position > 0) {
					spinerCard.setSelection(0);
					sharedHelper.setCardId(0);
					
					sharedHelper.setTypeId(position);
					updateUserMoney(position, "left");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {	
				
			}			
		});	
							
		//首页日期
		tvDateChoose = (TextView) super.findViewById(R.id.tv_date_choose);
		tvDateChoose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] array = fromDate.split("-");
				DatePickerDialog dateDialog = new MyDatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						String date = UtilityHelper.formatDate(year + "-" + (month + 1) + "-" + day, "");
						setListData(date);
					}					
				}, Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]));
				dateDialog.show();
			}
		});
						
		//每日消费菜单
		ImageButton btnTabDay = (ImageButton)super.findViewById(R.id.btn_tab_day);
		btnTabDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, DayActivity.class);
				startActivity(intent);				
			}
		});
		
		//每月消费菜单
		ImageButton btnTabMonth = (ImageButton)super.findViewById(R.id.btn_tab_month);
		btnTabMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, MonthActivity.class);
				startActivity(intent);				
			}
		});
		
		//消费排行菜单
		ImageButton btnTabRank = (ImageButton)super.findViewById(R.id.btn_tab_rank);
		btnTabRank.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, RankActivity.class);
				startActivity(intent);				
			}
		});

		//消费分析菜单
		ImageButton btnTabAnalyze = (ImageButton)super.findViewById(R.id.btn_tab_analyze);
		btnTabAnalyze.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, AnalyzeActivity.class);
				startActivity(intent);				
			}
		});

		//高级按钮
		ImageButton btnTitleSync = (ImageButton) super.findViewById(R.id.btn_title_sync);
		btnTitleSync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, SynchronizeActivity.class);
				startActivity(intent);
			}			
		});

		//关于按钮
		ImageButton btnTitleAboutUs = (ImageButton) super.findViewById(R.id.btn_title_aboutus);
		btnTitleAboutUs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, AboutsActivity.class);
				startActivity(intent);
			}			
		});
		
		//收入按钮
        Button btnHomeShouRu = (Button) super.findViewById(R.id.btn_home_shouru);
        btnHomeShouRu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				intent.putExtra("type", "sr");
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
        
        //支出按钮
        Button btnHomeZhiChu = (Button) super.findViewById(R.id.btn_home_zhichu);
        btnHomeZhiChu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				intent.putExtra("type", "zc");
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
        
        //管理钱包
  		ImageButton moneyEdit = (ImageButton) super.findViewById(R.id.btn_moneyedit);
  		moneyEdit.setOnClickListener(new OnClickListener(){
  			@Override
  			public void onClick(View v) {
  				Intent intent = new Intent(MainActivity.this, CardActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
  			}			
  		});
      		
		//定时刷新
		runnable = new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 3;
				myHandler.sendMessage(message);
				
				myHandler.postDelayed(this, DELAY_TIME);
			}			
		};
		
		//统计列表Radio
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int radioId) {
				if(radioAll.getId() == radioId) {
					setListData(fromDate, "all");
				} else if(radioYear.getId() == radioId) {
					setListData(fromDate, "year");
				} else if(radioMonth.getId() == radioId) {
					setListData(fromDate, "month");
				} else if(radioDay.getId() == radioId) {
					setListData(fromDate, "day");
				}
			}			
		});
				
		//检查网络同步
		if(sharedHelper.getLogin() && !sharedHelper.getSyncing() && UtilityHelper.checkInternet(this, 0)) {
			new Thread(new Runnable(){
				@Override
				public void run() {
					int userId = sharedHelper.getUserId();
					int result = SyncHelper.checkSyncWeb(userId);
					
					Bundle bundle = new Bundle();
					bundle.putInt("result", result);	
					Message message = new Message();
					message.what = 2;
					message.setData(bundle);
					myHandler.sendMessage(message);
				}
			}).start();
		}
	}
	
	//显示公告窗口
	protected void showNotes() {
		//没有公告不显示
		if(noteMessage.trim().equals("")) return;
		
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.txt_about_notes)
			.setMessage(noteMessage.trim().equals("") ? getString(R.string.txt_about_notesempty) : noteMessage)
			.setPositiveButton(R.string.txt_about_notesread, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					sharedHelper.setIsRead(true);
				}
			}).create();
		dialog.show();
	}
	
	//设置钱包
	protected void updateUserMoney(int position, String type) {
		double userMoney = 0;
		if(type.equals("right")) {
			String cardName = spinerCard.getSelectedItem().toString();
			cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase(), this);
			userMoney = cardAccess.findCardMoney(cardName);
			cardAccess.close();
		} else {
			itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
			Map<String, String> map = itemAccess.findHomeMonthTotal();
			itemAccess.close();
			
			try {
				switch(sharedHelper.getTypeId()) {
					case 1:
						userMoney = Double.parseDouble(map.get("zhichuvalue"));
						break;
					case 2:
						userMoney = Double.parseDouble(map.get("shouruvalue"));
						break;
					case 3:
						userMoney = Double.parseDouble(map.get("shouruvalue")) - Double.parseDouble(map.get("zhichuvalue"));
						break;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		if(userMoney < 0 || sharedHelper.getTypeId() == 1) {
			tvUserMoney.setTextColor(getResources().getColor(R.color.color_tran3_main));
		} else {
			tvUserMoney.setTextColor(getResources().getColor(android.R.color.black));
		}
		
		if(type.equals("right")) {
			tvUserMoney.setText(getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(userMoney, "0.0##"));
		} else {
			tvUserMoney.setText(getString(R.string.txt_price) + " " + (sharedHelper.getTypeId() == 1 ? "-" : "") + UtilityHelper.formatDouble(userMoney, "0.0##"));
		}
	}
			
	//设置ListView	
	protected void setListData(String date, String type) {
		fromDate = date;
		sharedHelper.setCurDate(date);
		tvDateChoose.setText(UtilityHelper.formatDate(date, "y-m-d-w2"));
		
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		list = itemAccess.findHomeTotalByDate(date, type);
		itemAccess.close();
		adapter = new SimpleAdapter(this, list, R.layout.list_home_total, new String[] { "shourulabel", "shouruprice", "zhichulabel", "zhichuprice" }, new int[] { R.id.tv_shourulabel, R.id.tv_shouruprice, R.id.tv_zhichulabel, R.id.tv_zhichuprice });
		listTotal.setAdapter(adapter);
		
		//结存
		Map<String, String> map = list.get(0);
		double shouRu = Double.parseDouble(map.get("shouruvalue"));
		double zhiChu = Double.parseDouble(map.get("zhichuvalue"));
		tvTotalJieCun.setText(getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(shouRu - zhiChu, "0.0##"));
		
		//未还=借出-还入
		map = list.get(1);
		double huanRu = Double.parseDouble(map.get("shouruvalue"));
		double jieChu = Double.parseDouble(map.get("zhichuvalue"));
		TextView tvHoneWeiHuan = (TextView) super.findViewById(R.id.tv_home_weihuan);
		tvHoneWeiHuan.setText(getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(jieChu - huanRu, "0.0##"));
		
		//欠还=借入-还出
		map = list.get(2);
		double jieRu = Double.parseDouble(map.get("shouruvalue"));
		double huanChu = Double.parseDouble(map.get("zhichuvalue"));
		TextView tvHoneQianHuan = (TextView) super.findViewById(R.id.tv_home_qianhuan);
		tvHoneQianHuan.setText(getString(R.string.txt_price) + " " + UtilityHelper.formatDouble(jieRu - huanChu, "0.0##"));
		
		//钱包
		cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase());
		cardList = cardAccess.findAllCard();
		cardAccess.close();
		cardList.add(0, "请选择");
		cardAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, cardList);
		cardAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spinerCard.setAdapter(cardAdapter);

		//首页分类
		String[] typeArr = getResources().getStringArray(R.array.hometype);
		typeAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, typeArr);
		typeAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spinerType.setAdapter(typeAdapter);
		
		if(hasTypeId()) {
			spinerType.setSelection(sharedHelper.getTypeId());
		} else {
			spinerCard.setSelection(sharedHelper.getCardId()+1);
		}
		
		sharedHelper.setHomeView(type);
	}
	
	//设置ListView	
	protected void setListData(String date) {
		int radioId = radioGroup.getCheckedRadioButtonId();
		if(radioAll.getId() == radioId) {
			setListData(date, "all");
		} else if(radioYear.getId() == radioId) {
			setListData(date, "year");
		} else if(radioMonth.getId() == radioId) {
			setListData(date, "month");
		} else if(radioDay.getId() == radioId) {
			setListData(date, "day");
		}
	}
	
	//关闭this
	protected void close() {
		this.finish();
		new Thread(new Runnable(){
			@Override
			public void run() {
				UtilityHelper.startBackup(MainActivity.this, "aalife.bak");
			}
		}).start();
	}
	
	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(sharedHelper.getSyncing()) {
				Toast.makeText(this, getString(R.string.txt_home_syncexit), Toast.LENGTH_SHORT).show();
				return false;
			} else {
				this.close();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	//菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_exits:
			sharedHelper.setSyncing(false);
			this.close();
			
			break;
		case R.id.action_settings:
			intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivity(intent);
			
			break;
		case R.id.action_abouts:
			intent = new Intent(MainActivity.this, AboutsActivity.class);
			startActivity(intent);
			
			break;
		}
		
		return false;
	}
	
	//页面返回
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		//数据填充
		setListData(curDate);
					
		//设置同步时文本
		if(sharedHelper.getSyncing()) {
			myHandler.postDelayed(runnable, DELAY_TIME);
		} else {
			myHandler.removeCallbacks(runnable);
		}	
		
		//首页主位置
		if(hasTypeId()) {
			updateUserMoney(sharedHelper.getTypeId(), "left");
		} else {
		    updateUserMoney(sharedHelper.getCardId()+1, "right");
		}
		
		checkDian();		
	}
	
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if(data != null) {
				curDate = data.getStringExtra("date");
			}
		}
	}
	
	//销毁处理
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		myHandler.removeCallbacks(runnable);
	}
		
	//检查点
	protected void checkDian() {
		if(sharedHelper.getLocalSync() || sharedHelper.getWebSync()) {
			tvDian.setVisibility(View.VISIBLE);
		} else {
			tvDian.setVisibility(View.GONE);
		}
	}
	
	//选择统计
	protected void setRadioDefault() {
		String type = sharedHelper.getHomeView();
		if(type.equals("all")) {
			radioAll.setChecked(true);
		} else if(type.equals("year")) {
			radioYear.setChecked(true);
		} else if(type.equals("month")) {
			radioMonth.setChecked(true);
		} else if(type.equals("day")) {
			radioDay.setChecked(true);
		}
	}
	
	//是否有TypeId
	protected boolean hasTypeId() {
		return sharedHelper.getTypeId() > 0;
	}
	
	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<MainActivity> myActivity = null;
		MyHandler(MainActivity activity) {
			myActivity = new WeakReference<MainActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			MainActivity activity = myActivity.get();
			switch(msg.what) {
			case 2:
				boolean syncFlag = activity.sharedHelper.getSyncing();
				int result = msg.getData().getInt("result");
				if(result == 1 && !syncFlag) {
					String syncStatus = activity.getString(R.string.txt_home_haswebsync);
					activity.sharedHelper.setSyncStatus(syncStatus);
					activity.sharedHelper.setWebSync(true);
					
					activity.checkDian();
				}
							
				break;
			case 3:
				String date = activity.sharedHelper.getCurDate();
				if(!date.equals("")) {
				    activity.curDate = date;
				    activity.setListData(activity.curDate);
				}
				
				if(!activity.sharedHelper.getSyncing()) {
					activity.myHandler.removeCallbacks(activity.runnable);
				}
				
				break;
			case 6:
				String[] array = msg.getData().getStringArray("result");
				try {
					int messageCode = Integer.parseInt(array[0]);
					String message = array[1];
					if(!message.equals("")) {
						activity.noteMessage = message;
						activity.btnNotes.setVisibility(View.VISIBLE);
						
						if(messageCode > activity.sharedHelper.getMessageCode()) {
							activity.sharedHelper.setIsRead(false);
							activity.sharedHelper.setMessageCode(messageCode);
						}
						
						//双数是帮助贴士
						if(messageCode % 2 == 0 && !activity.sharedHelper.getIsRead()) {
							if(!activity.sharedHelper.getReadTips()) {
								activity.sharedHelper.setIsRead(true);
							}
						}
						
						//首次显示
						if(!activity.hasBackup && !activity.sharedHelper.getIsRead()) {
							activity.showNotes();
						}
					} else {
						activity.sharedHelper.setIsRead(true);
						activity.noteMessage = activity.getString(R.string.txt_about_notesempty);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				break;
			}
		}
	}

}
