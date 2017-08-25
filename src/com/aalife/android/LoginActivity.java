package com.aalife.android;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class LoginActivity extends Activity {
	private EditText etUserName = null;
	private EditText etUserPass = null;
	private SharedHelper sharedHelper = null;
	private MyHandler myHandler = new MyHandler(this);
	private UserEntity user = null;
	private ProgressBar pbUserLoading = null;
	private Button btnUserLogin = null;
	private Button btnQQLogin = null;
	private String isFirst = "1";
	private QQAuth mQQAuth = null;
	private MyUserInfo myInfo = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvUserName = (TextView) super.findViewById(R.id.tv_user_name);
		textPaint = tvUserName.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvUserPass = (TextView) super.findViewById(R.id.tv_user_pass);
		textPaint = tvUserPass.getPaint();
		textPaint.setFakeBoldText(true);
		
		//初始化
		sharedHelper = new SharedHelper(this);
		etUserName = (EditText) super.findViewById(R.id.et_user_name);
		etUserPass = (EditText) super.findViewById(R.id.et_user_pass);
		pbUserLoading = (ProgressBar) super.findViewById(R.id.pb_user_loading);
		mQQAuth = QQAuth.createInstance("100761541", this.getApplicationContext());
		myInfo = new MyUserInfo();
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LoginActivity.this.close();
			}			
		});
		
		//登录按钮
		btnUserLogin = (Button) super.findViewById(R.id.btn_user_login);
		btnUserLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String userName = etUserName.getText().toString().trim();
				if (userName.equals("")) {
					Toast.makeText(LoginActivity.this, getString(R.string.txt_user_username) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}

				final String userPass = etUserPass.getText().toString().trim();
				if (userPass.equals("")) {
					Toast.makeText(LoginActivity.this, getString(R.string.txt_user_userpass) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}
				
				pbUserLoading.setVisibility(View.VISIBLE);
				btnUserLogin.setEnabled(false);
				
				new Thread(new Runnable(){
					@Override
					public void run() {
						user = UtilityHelper.loginUser(userName, userPass, isFirst);
												
						Message message = new Message();
						message.what = 1;
						myHandler.sendMessage(message);
					}
				}).start();
			}			
		});	
		
		//注册按钮
		ImageButton btnUserAdd = (ImageButton) super.findViewById(R.id.btn_user_add);
		btnUserAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}			
		});	
		
		//QQ登录按钮
		btnQQLogin = (Button) super.findViewById(R.id.btn_user_qqlogin);
		btnQQLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doLogin();
			}		
		});
	}
	
	//QQ登录方法
	private void doLogin() {
	 	IUiListener listener = new BaseUIListener() {
	 		@Override
	 		protected void doComplete(JSONObject values) {
	 			updateUserInfo(values);
	 		}
	 	};
	 	mQQAuth.login(this, "get_simple_userinfo", listener);
	}
	
	//QQ登录回调
	private class BaseUIListener implements IUiListener {	
		@Override
		public void onComplete(Object response) {
			doComplete((JSONObject)response);
		}
		
		protected void doComplete(JSONObject values) {
		}
		
		@Override
		public void onError(UiError e) {
		}
		
		@Override
		public void onCancel() {
		}
	}
		
	protected void updateUserInfo(JSONObject values) {
		try {
			myInfo.openId = values.getString("openid");
			myInfo.accessToken = values.getString("access_token");
			myInfo.oAuthFrom = "sjqq" + getString(R.string.app_client);
			myInfo.userFrom = getString(R.string.app_version) + getString(R.string.app_client);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		UserInfo info = new UserInfo(this, mQQAuth.getQQToken());
		info.getUserInfo(new BaseUIListener(){
			@Override
	 		protected void doComplete(JSONObject arg0) {
				Message msg = new Message();
				msg.obj = arg0;
				msg.what = 2;
				myHandler.sendMessage(msg);
	 		}
		});
	}
	
	//QQ信息
	class MyUserInfo {
		String openId;
		String accessToken;
		String oAuthFrom;
		String nickName;
		String userImage;
		String userFrom;
	}
	
	//关闭this
	protected void close() {
		this.finish();
	}
		
	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<LoginActivity> myActivity = null;
		MyHandler(LoginActivity activity) {
			myActivity = new WeakReference<LoginActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			final LoginActivity activity = myActivity.get();
			switch(msg.what) {
			case 1:
				activity.pbUserLoading.setVisibility(View.GONE);
				activity.btnUserLogin.setEnabled(true);
				
				if(activity.user.userId != 0) {
					activity.sharedHelper.setUserId(activity.user.userId);
					activity.sharedHelper.setUserName(activity.user.userName);
					activity.sharedHelper.setUserPass(activity.user.userPassword);
					activity.sharedHelper.setUserNickName(activity.user.userNickName);
					activity.sharedHelper.setJoinDate(activity.user.createDate);
					activity.sharedHelper.setUserEmail(activity.user.userEmail);
					activity.sharedHelper.setUserPhone(activity.user.userPhone);
					activity.sharedHelper.setUserBound(activity.user.userBound == 1);

					//用线程更新登录后的头像
					new Thread(new Runnable(){
						@Override
						public void run() {
							String userImage = activity.user.userImage;
							if(!userImage.equals("")) {
								String imageName = "tu_" + activity.user.userId + ".jpg";
								Boolean success = UtilityHelper.loadBitmap(activity, userImage, imageName);
								if(success) {
									if(userImage.startsWith("http")) {
										activity.sharedHelper.setUserQQImage(userImage);
										userImage = imageName;
									}
									activity.sharedHelper.setUserImage(userImage);
								}
							}
						}
					}).start();

					if(activity.user.hasSync == 1) {
						activity.sharedHelper.setWebSync(true);
						activity.sharedHelper.setSyncStatus(activity.getString(R.string.txt_home_haswebsync));
					} else if(!activity.sharedHelper.getLocalSync()) {
						activity.sharedHelper.setSyncStatus(activity.getString(R.string.txt_home_nosync));
					}

					activity.sharedHelper.setLogin(true);
					Toast.makeText(activity, activity.getString(R.string.txt_user_loginsuccess), Toast.LENGTH_SHORT).show();
					activity.setResult(Activity.RESULT_OK);
					activity.close();
				} else {
					Toast.makeText(activity, activity.getString(R.string.txt_user_loginerror), Toast.LENGTH_SHORT).show();
				}
				
				break;
			case 2:
				JSONObject values = (JSONObject) msg.obj;
				try {
					activity.myInfo.nickName = values.getString("nickname");
					activity.myInfo.userImage = values.getString("figureurl_qq_2");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				activity.mQQAuth.logout(activity);
				activity.sharedHelper.setUserBound(true);
				activity.qqLogin();
				break;
			}
		}			
	};
	
	//QQ登录
	private void qqLogin() {
		pbUserLoading.setVisibility(View.VISIBLE);
		btnQQLogin.setEnabled(false);
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				String userName = sharedHelper.getUserName();
				user = UtilityHelper.loginQQUser(userName, myInfo.openId, myInfo.accessToken, myInfo.oAuthFrom, myInfo.nickName, myInfo.userImage, myInfo.userFrom, isFirst);
										
				Message message = new Message();
				message.what = 1;
				myHandler.sendMessage(message);
			}
		}).start();
	}
}
