package com.aalife.android;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText etUserName = null;
	private EditText etUserPass = null;
	private EditText etUserNickName = null;
	private EditText etUserEmail = null;
	private MyHandler myHandler = new MyHandler(this);
	private int result = 0;
	private ProgressBar pbUserLoading = null;
	private Button btnUserAdd = null;
	private Spinner spWorkDay = null;
	private ArrayAdapter<CharSequence> workDayAdapter = null;
	private SharedHelper sharedHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvUserName = (TextView) super.findViewById(R.id.tv_user_name);
		textPaint = tvUserName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvUserPass = (TextView) super.findViewById(R.id.tv_user_pass);
		textPaint = tvUserPass.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvNickName = (TextView) super.findViewById(R.id.tv_user_nickname);
		textPaint = tvNickName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvUserEmail = (TextView) super.findViewById(R.id.tv_user_email);
		textPaint = tvUserEmail.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvUserWorkDay = (TextView) super.findViewById(R.id.tv_user_workday);
		textPaint = tvUserWorkDay.getPaint();
		textPaint.setFakeBoldText(true);
		
		//初始化
		sharedHelper = new SharedHelper(this);
		etUserName = (EditText) super.findViewById(R.id.et_user_name);
		etUserPass = (EditText) super.findViewById(R.id.et_user_pass);
		etUserNickName = (EditText) super.findViewById(R.id.et_user_nickname);
		etUserEmail = (EditText) super.findViewById(R.id.et_user_email);
		pbUserLoading = (ProgressBar) super.findViewById(R.id.pb_user_loading);

		//工作日
		String[] workDayArr = getResources().getStringArray(R.array.workday);
		spWorkDay = (Spinner) super.findViewById(R.id.sp_workday);
		workDayAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, workDayArr);
		workDayAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spWorkDay.setAdapter(workDayAdapter);
		spWorkDay.setSelection(4);
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				RegisterActivity.this.close();
			}			
		});
				
		//注册按钮
		btnUserAdd = (Button) super.findViewById(R.id.btn_user_add);
		btnUserAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String userName = etUserName.getText().toString().trim();
				if (userName.equals("")) {
					Toast.makeText(RegisterActivity.this, getString(R.string.txt_user_username) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}

				final String userPass = etUserPass.getText().toString().trim();
				if (userPass.equals("")) {
					Toast.makeText(RegisterActivity.this, getString(R.string.txt_user_userpass) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}
				
				final String userEmail = etUserEmail.getText().toString().trim();
				if(!userEmail.equals("") && !UtilityHelper.isEmailAddress(userEmail)) {
					Toast.makeText(RegisterActivity.this, getString(R.string.txt_user_emailerror), Toast.LENGTH_SHORT).show();
					return;
				}
				
				final String userNickName = etUserNickName.getText().toString().trim();				
				final String userWorkDay = String.valueOf(spWorkDay.getSelectedItemPosition() + 1);				
				final String userFrom = getString(R.string.app_version) + getString(R.string.app_client);
				final String userMoney = sharedHelper.getUserMoney();
				final String categoryRate = sharedHelper.getCategoryRate();
				
				pbUserLoading.setVisibility(View.VISIBLE);
				btnUserAdd.setEnabled(false);
				
				new Thread(new Runnable(){
					@Override
					public void run() {						
						result = UtilityHelper.addUser(userName, userPass, userNickName, userEmail, userFrom, userWorkDay, userMoney, categoryRate);
						
						Bundle bundle = new Bundle();
						bundle.putString("userName", userName);
						bundle.putString("userPass", userPass);
						Message message = new Message();
						message.what = 1;
						message.setData(bundle);
						myHandler.sendMessage(message);
					}
				}).start();
			}			
		});
	}

	//关闭this
	protected void close() {
		this.finish();
	}
		
	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<RegisterActivity> myActivity = null;
		MyHandler(RegisterActivity activity) {
			myActivity = new WeakReference<RegisterActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			RegisterActivity activity = myActivity.get();
			switch(msg.what) {
			case 1:
			    String userName = msg.getData().getString("userName");
			    String userPass = msg.getData().getString("userPass");
			    
				activity.pbUserLoading.setVisibility(View.GONE);
				activity.btnUserAdd.setEnabled(true);
				
				if(activity.result == 1) {
					activity.sharedHelper.setUserName(userName);
					activity.sharedHelper.setUserPass(userPass);
					
					Toast.makeText(activity, activity.getString(R.string.txt_user_registersuccess), Toast.LENGTH_SHORT).show();
					activity.close();
				} else if(activity.result == 2) {
					Toast.makeText(activity, activity.getString(R.string.txt_user_userrepeat), Toast.LENGTH_SHORT).show();
				} else {					
					Toast.makeText(activity, activity.getString(R.string.txt_user_registererror), Toast.LENGTH_SHORT).show();
				}
				
				break;
			}
		}			
	};
}
