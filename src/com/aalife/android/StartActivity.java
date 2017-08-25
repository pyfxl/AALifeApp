package com.aalife.android;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StartActivity extends Activity {
	private SQLiteOpenHelper sqlHelper = null;
	private SharedHelper sharedHelper = null;
	private MyHandler myHandler = new MyHandler(this);
	private TextView tvStartLabel = null;
	private LinearLayout layLock = null;
	private EditText etLockText = null;
	private ImageButton btnLockGo = null;
	private ProgressBar pbStart = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		//虚拟按键菜单
		UtilityHelper.showMenuButton(this);
				
		//数据库
		sqlHelper = new DatabaseHelper(this);
		sqlHelper.getWritableDatabase();
		sqlHelper.close();
		
		//初始化
		sharedHelper = new SharedHelper(this);
		tvStartLabel = (TextView) super.findViewById(R.id.tv_start_lable);
		layLock = (LinearLayout) super.findViewById(R.id.lay_lock);
		etLockText = (EditText) super.findViewById(R.id.et_lock_text);
		btnLockGo = (ImageButton) super.findViewById(R.id.btn_lock_go);
		pbStart = (ProgressBar) super.findViewById(R.id.pb_start);
		pbStart.setVisibility(View.VISIBLE);
		
		//设置欢迎文字
		String welcome = sharedHelper.getWelcomeText();
		if(welcome.equals("")) {
			welcome = getString(R.string.app_website);
			sharedHelper.setWelcomeText(welcome);
		}		
		tvStartLabel.setText(welcome);
		
		//获取应用安装日期
		String joinDate = sharedHelper.getJoinDate();
		if(joinDate.equals("")) {
			joinDate = UtilityHelper.getJoinDateFromApp(this);
		    sharedHelper.setJoinDate(joinDate);
		}
		
		//第3备份五天一次
		String bakDate = sharedHelper.getBakDate();
		String curDate = UtilityHelper.getCurDate();
		if(bakDate.equals("") || UtilityHelper.compareDate(bakDate, curDate)) {
			String date = UtilityHelper.getNavDate(curDate, 5, "d");
			sharedHelper.setBakDate(date);
			UtilityHelper.startBackup(this, "aalife3.bak");
		}
		
		//检查新版本
		if(UtilityHelper.checkInternet(this, 0)) {
			if(!sharedHelper.getSyncing()) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						boolean result = false;
						try {
							result = UtilityHelper.checkNewVersion(StartActivity.this);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						Bundle bundle = new Bundle();
						bundle.putBoolean("result", result);	
						Message message = new Message();
						message.what = 3;
						message.setData(bundle);
						myHandler.sendMessage(message);
					}
				}).start();
			} else {
				jumpActivity();
			}
		} else {
			myHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					jumpActivity();
				}
			}, 500);
		}
	}

	//关闭this
	protected void close() {
		this.finish();
	}
	
	//返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.close();
		return true;
	}
		
	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<StartActivity> myActivity = null;
		MyHandler(StartActivity activity) {
			myActivity = new WeakReference<StartActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			boolean bool = false;
			final StartActivity activity = myActivity.get();
			switch(msg.what) {
			case 3:
				bool = msg.getData().getBoolean("result");
				if(bool) {					
					Dialog dialog = new AlertDialog.Builder(activity)
				        .setCancelable(false)
						.setTitle(R.string.txt_tips)
						.setMessage(R.string.txt_new_version)
						.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								activity.tvStartLabel.setText(R.string.txt_new_downing);
								activity.downNewFile();
							}
						}).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
								activity.jumpActivity();
							}
						}).create();
					dialog.show();
				} else {
					activity.jumpActivity();
				}

				break;
			case 4:
				bool = msg.getData().getBoolean("result");
				if(bool) {
					activity.close();
				} else {
					activity.tvStartLabel.setText(R.string.txt_new_updateerror);
					activity.jumpActivity();
				}
				
				activity.pbStart.setVisibility(View.GONE);

				break;
			}				
		}			
	};
	
	//跳转
	protected void jumpActivity() {		
		final String lock = sharedHelper.getLockText();
		if(!lock.equals("")) {
			tvStartLabel.setText(R.string.txt_lock);
			layLock.setVisibility(View.VISIBLE);
			pbStart.setVisibility(View.GONE);
			
			btnLockGo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {			        
					String text = etLockText.getText().toString().trim();
					if(text.equals(lock)) {
						jumpActivityClose();
					} else {
						tvStartLabel.setText(R.string.txt_lock_error);
					}
				}				
			});
		} else {		
			jumpActivityClose();
		}
	}
	
	//跳转关闭
	protected void jumpActivityClose() {	
		Intent intent = new Intent(StartActivity.this, MainActivity.class);
		startActivity(intent);  
		this.close();
	}
	
	//升级
	protected void downNewFile() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				boolean result = false;
				try {
					Uri uri = Uri.fromFile(UtilityHelper.getInstallFile(StartActivity.this));
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					startActivity(intent);
					
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Bundle bundle = new Bundle();
				bundle.putBoolean("result", result);	
				Message message = new Message();
				message.what = 4;
				message.setData(bundle);
				myHandler.sendMessage(message);
			}
		}).start();
	}

}
