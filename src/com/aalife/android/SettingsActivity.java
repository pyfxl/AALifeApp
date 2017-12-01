package com.aalife.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	private SharedHelper sharedHelper = null;
	private EditText etSetLock = null;
	private EditText etSetWelcome = null;
	private CheckBox cbSetSync = null;
	private CheckBox cbSetAutoSync = null;
	private CheckBox cbSetReadTips = null;
	private Spinner spWorkDay = null;
	private ArrayAdapter<CharSequence> workDayAdapter = null;

	private RadioGroup radioGroup = null;
	private RadioButton radioYue = null;
	private RadioButton radioBujide = null;
	private RadioButton radioChazhang = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		//标题变粗
		TextPaint textPaint = null;
		TextView tvSetLock = (TextView) super.findViewById(R.id.tv_set_lock);
		textPaint = tvSetLock.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvSetWelcome = (TextView) super.findViewById(R.id.tv_set_welcome);
		textPaint = tvSetWelcome.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvSetSync = (TextView) super.findViewById(R.id.tv_set_sync);
		textPaint = tvSetSync.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvSetReadTips = (TextView) super.findViewById(R.id.tv_set_readtips);
		textPaint = tvSetReadTips.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvUserWorkDay = (TextView) super.findViewById(R.id.tv_user_workday);
		textPaint = tvUserWorkDay.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvSetFixMoney = (TextView) super.findViewById(R.id.tv_set_fixmoney);
		textPaint = tvSetFixMoney.getPaint();
		textPaint.setFakeBoldText(true);
				
		//初始化
		sharedHelper = new SharedHelper(this);
		etSetLock = (EditText) super.findViewById(R.id.et_set_lock);
		etSetWelcome = (EditText) super.findViewById(R.id.et_set_welcome);
		cbSetSync = (CheckBox) super.findViewById(R.id.cb_set_sync);
		cbSetAutoSync = (CheckBox) super.findViewById(R.id.cb_set_autosync);
		cbSetReadTips = (CheckBox) super.findViewById(R.id.cb_set_readtips);
		
		//初始Radio
		radioGroup = (RadioGroup) super.findViewById(R.id.rg_fixmoney);
		radioYue = (RadioButton) super.findViewById(R.id.radio_yue);
		radioBujide = (RadioButton) super.findViewById(R.id.radio_bujide);
		radioChazhang = (RadioButton) super.findViewById(R.id.radio_chazhang);
				
		//设置默认值
		String lock = sharedHelper.getLockText();
		etSetLock.setText(lock);
		String welcome = sharedHelper.getWelcomeText();
		etSetWelcome.setText(welcome);

		//checkbox值
		if(!sharedHelper.getAllowSync()) {
			cbSetSync.setChecked(false);
		}
		if(!sharedHelper.getAutoSync()) {
			cbSetAutoSync.setChecked(false);
		}
		if(sharedHelper.getReadTips()) {
			cbSetReadTips.setChecked(true);
		}

		//工作日
		String[] workDayArr = getResources().getStringArray(R.array.workday);
		spWorkDay = (Spinner) super.findViewById(R.id.sp_workday);
		workDayAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, workDayArr);
		workDayAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spWorkDay.setAdapter(workDayAdapter);
		int userWorkDay = Integer.parseInt(sharedHelper.getUserWorkDay());
		spWorkDay.setSelection(userWorkDay - 1);
		
		//余额修改
		String fixMoneyText = sharedHelper.getFixMoneyType();
		if(fixMoneyText.equals(getString(R.string.txt_set_fixmoney_bujide_val))) {
			radioBujide.setChecked(true);
		} else if(fixMoneyText.equals(getString(R.string.txt_set_fixmoney_chazhang_val))) {
			radioChazhang.setChecked(true);
		} else {
			radioYue.setChecked(true);
		}
		
		//设置提醒闹钟
		ImageButton btnTitleClock = (ImageButton) super.findViewById(R.id.btn_title_clock);
		btnTitleClock.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
				boolean flag = false;

				String clockImpls[][] = {
				    { "Android 4.4 Alarm Clock", "com.android.deskclock", "com.android.deskclock.DeskClock" },
				    { "Standard Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock" },
			        { "Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock" },
			        { "Android Alarm Clock", "com.android.alarmclock", "com.android.alarmclock.AlarmClock" },
			        { "Moto Blur Alarm Clock", "com.motorola.blur.alarmclock", "com.motorola.blur.alarmclock.AlarmClock" },
			        { "XiaoMi Alarm Clock", "com.android.deskclock", "com.android.deskclock.DeskClockTabActivity" },
			        { "Lenovo Alarm Clock", "com.lenovomobile.deskclock", "com.lenovomobile.deskclock.AlarmClock" },
			        { "HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
			        { "Sony Alarm Clock", "com.sonyericsson.alarm", "com.sonyericsson.alarm.Alarm" },
			        { "Asus Alarm Clock", "com.asus.asusclock", "com.asus.asusclock.deskclock.DeskClock" },
			        { "Samsung Galaxy Clock", "com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage" },
			        { "Huawei Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmsMainActivity" }
				};

				for(int i=0; i<clockImpls.length; i++) {
				    String packageName = clockImpls[i][1];
				    String className = clockImpls[i][2];
				    ComponentName cn = new ComponentName(packageName, className);
				    intent.setComponent(cn);
				    try {
				        startActivity(intent);
				        flag = true;
				        break;
				    } catch (Exception e) {
				    	continue;
				    }
				}
				
				if(!flag) {
					Toast.makeText(SettingsActivity.this, getString(R.string.txt_set_clockerror), Toast.LENGTH_SHORT).show();
				}
			}			
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SettingsActivity.this.close();
			}			
		});
		
		//设置
		Button btnSetSure = (Button) super.findViewById(R.id.btn_set_sure);
		btnSetSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String lock = etSetLock.getText().toString().trim();
				sharedHelper.setLockText(lock);				
				String welcome = etSetWelcome.getText().toString().trim();
				sharedHelper.setWelcomeText(welcome);

				//允许同步
				if(cbSetSync.isChecked()) {
					sharedHelper.setAllowSync(true);
				} else {
					sharedHelper.setAllowSync(false);
				}

				//自动同步
				if(cbSetAutoSync.isChecked()) {
					sharedHelper.setAutoSync(true);
				} else {
					sharedHelper.setAutoSync(false);
				}

				//显示tips
				if(cbSetReadTips.isChecked()) {
					sharedHelper.setReadTips(true);
				} else {
					sharedHelper.setReadTips(false);
				}
				
				String userWorkDay = String.valueOf(spWorkDay.getSelectedItemPosition() + 1);
				int oldWorkDay = Integer.parseInt(sharedHelper.getUserWorkDay());
				int newWorkDay = Integer.parseInt(userWorkDay);
				boolean workDayFlag = false;
				if(newWorkDay != oldWorkDay) {
					workDayFlag = true;
					sharedHelper.setUserWorkDay(userWorkDay);
				}
				
				if(workDayFlag) {
					sharedHelper.setLocalSync(true);
					sharedHelper.setSyncStatus(getString(R.string.txt_home_haslocalsync));
				}
				
				int radioId = radioGroup.getCheckedRadioButtonId();
				if(radioBujide.getId() == radioId) {
					sharedHelper.setFixMoneyType(getString(R.string.txt_set_fixmoney_bujide_val));
				} else if(radioChazhang.getId() == radioId) {					
					sharedHelper.setFixMoneyType(getString(R.string.txt_set_fixmoney_chazhang_val));
				} else {
					sharedHelper.setFixMoneyType("");
				}
				
				SettingsActivity.this.close();
			}			
		});
	}
		
	//关闭this
	protected void close() {
		this.finish();
	}
}
