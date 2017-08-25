package com.aalife.android;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class SynchronizeActivity extends Activity {
	private Button btnBackup = null;
	private Button btnRestore = null;
	private ImageButton btnUserLogin = null;
	private ImageButton btnUserEdit = null;
	private final int FIRST_REQUEST_CODE = 1;
	private SharedHelper sharedHelper = null;
	private ImageView ivUserImage = null;
	private Button btnUserBound = null;
	private TextView tvUserText = null;
	private TextView tvUserHonor = null;
	private MyHandler myHandler = new MyHandler(this);
	private SyncHelper syncHelper = null;
	private ProgressBar pbHomeSync = null;
	private ProgressBar pbCloudSync = null;
	private TextView tvLabStatus = null;
	private Button btnHomeSync = null;
	private Button btnCloudUp = null;
	private Button btnCloudDown = null;
	private Button btnZhuanTi = null;
	private Handler handler = new Handler();
	private EditText etSearchKey = null;
	private QQAuth mQQAuth = null;
	private SQLiteOpenHelper sqlHelper = null;
	private ItemTableAccess itemAccess = null;
	private Runnable runnable = null;
	private final int DELAY_TIME = 5 * 1000;
	private boolean isBackup = false;
	private EditText etDataDate1 = null;
	private EditText etDataDate2 = null;
	private String beginDate = "";
	private String endDate = "";
	private TextView tvUserLogout = null;
	private TextView tvSyncCancel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronize);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvRestoreTitle = (TextView) super.findViewById(R.id.tv_restore_title);
		textPaint = tvRestoreTitle.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvSyncUser = (TextView) super.findViewById(R.id.tv_sync_user);
		textPaint = tvSyncUser.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvSyncTitle = (TextView) super.findViewById(R.id.tv_sync_title);
		textPaint = tvSyncTitle.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvZhuanTi = (TextView) super.findViewById(R.id.tv_zhuanti);
		textPaint = tvZhuanTi.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvSearch = (TextView) super.findViewById(R.id.tv_search);
		textPaint = tvSearch.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvCloudTitle = (TextView) super.findViewById(R.id.tv_cloud_title);
		textPaint = tvCloudTitle.getPaint();
		textPaint.setFakeBoldText(true);
		TextView tvDataTitle = (TextView) super.findViewById(R.id.tv_data_title);
		textPaint = tvDataTitle.getPaint();
		textPaint.setFakeBoldText(true);

		//数据库
		sqlHelper = new DatabaseHelper(this);
	
		//初始化
		sharedHelper = new SharedHelper(this);
		syncHelper = new SyncHelper(this);
		btnUserLogin = (ImageButton) super.findViewById(R.id.btn_user_login);
		btnUserEdit = (ImageButton) super.findViewById(R.id.btn_user_edit);
		ivUserImage = (ImageView) super.findViewById(R.id.iv_userimage);
		btnUserBound = (Button) super.findViewById(R.id.btn_user_bound);
		tvUserText = (TextView) super.findViewById(R.id.tv_sync_user_text);
		tvUserHonor = (TextView) super.findViewById(R.id.tv_sync_user_honor);
		pbHomeSync = (ProgressBar) super.findViewById(R.id.pb_home_sync);
		pbCloudSync = (ProgressBar) super.findViewById(R.id.pb_cloud_sync);
		tvLabStatus = (TextView) super.findViewById(R.id.tv_lab_status);
		btnHomeSync = (Button) super.findViewById(R.id.btn_home_sync);
		btnCloudUp = (Button) super.findViewById(R.id.btn_cloud_up);
		btnCloudDown = (Button) super.findViewById(R.id.btn_cloud_down);
		etSearchKey = (EditText) super.findViewById(R.id.et_search_key);
		mQQAuth = QQAuth.createInstance("100761541", this.getApplicationContext());
		beginDate = UtilityHelper.getMonthFirst();
		endDate = UtilityHelper.getCurDate();
		tvUserLogout = (TextView) super.findViewById(R.id.tv_user_logout);
		tvSyncCancel = (TextView) super.findViewById(R.id.tv_sync_cancel);

		//检查登录
		if(sharedHelper.getLogin()) {
			btnUserLogin.setVisibility(View.GONE);
			btnUserEdit.setVisibility(View.VISIBLE);

			tvUserText.setText(sharedHelper.getUserName() + getString(R.string.txt_sync_user_login));
			
			setUserImage();
			
			if(!sharedHelper.getUserBound()) {
				btnUserBound.setVisibility(View.VISIBLE);
			}
			
			tvUserLogout.setVisibility(View.VISIBLE);
		} else {
			tvUserLogout.setVisibility(View.GONE);
		}
		
		//显示荣耀
		showUserHonor();
		
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
		
		//设置同步时文本
		if(sharedHelper.getSyncing()) {
			myHandler.postDelayed(runnable, DELAY_TIME);
			
			pbHomeSync.setVisibility(View.VISIBLE);
			btnHomeSync.setEnabled(false);
			sharedHelper.setSyncStatus(getString(R.string.txt_home_syncing));
			
			tvSyncCancel.setVisibility(View.VISIBLE);
		} else {
			myHandler.removeCallbacks(runnable);

			pbHomeSync.setVisibility(View.GONE);
			btnHomeSync.setEnabled(true);
			tvLabStatus.setText(sharedHelper.getSyncStatus());
			
			tvSyncCancel.setVisibility(View.GONE);
		}
		
		//设置同步状态文本
		String syncStatus = sharedHelper.getSyncStatus();
		if(syncStatus.equals("")) {
			syncStatus = getString(R.string.txt_home_nosync);
			sharedHelper.setSyncStatus(syncStatus);
		}
		tvLabStatus.setText(syncStatus);

		//开始数据备份
		btnBackup = (Button) super.findViewById(R.id.btn_backup_go);
		btnBackup.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				boolean success = UtilityHelper.startBackup(SynchronizeActivity.this, "aalife2.bak");
				if(success) {
					isBackup = true;
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_backup_success), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_backup_error), Toast.LENGTH_SHORT).show();
				}
			}			
		});
		
		//开始数据恢复
		btnRestore = (Button) super.findViewById(R.id.btn_restore_go);
		btnRestore.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Dialog dialog = new AlertDialog.Builder(SynchronizeActivity.this)
					.setTitle(R.string.txt_tips)
					.setMessage(R.string.txt_restore_note)
					.setPositiveButton(R.string.txt_sure, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							int result = UtilityHelper.startRestore(SynchronizeActivity.this, "aalife2.bak");
							if(result == 1) {
								Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_restore_success), Toast.LENGTH_SHORT).show();
								onCreate(null);
							} else if(result == 2) {
								Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_restore_nodata), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_restore_error), Toast.LENGTH_SHORT).show();
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

        //用户专题
		btnZhuanTi = (Button) super.findViewById(R.id.btn_zhuanti_go);
		btnZhuanTi.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(SynchronizeActivity.this, ZhuanTiActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//登录按钮
		btnUserLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(SynchronizeActivity.this, LoginActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});

		//修改按钮
		btnUserEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(SynchronizeActivity.this, UserEditActivity.class);
				startActivityForResult(intent, FIRST_REQUEST_CODE);
			}			
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SynchronizeActivity.this.close();
			}			
		});	
		
		//搜索按钮
		Button btnSearchGo = (Button) super.findViewById(R.id.btn_search_go);
		btnSearchGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String key = etSearchKey.getText().toString().trim();
				key = UtilityHelper.replaceKey(key);
				if(key.equals("")) {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_search_keyempty), Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(SynchronizeActivity.this, SearchActivity.class);
				intent.putExtra("key", key);
				startActivity(intent);
			}			
		});
				
		//同步按钮
		btnHomeSync.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if(!sharedHelper.getLogin()) {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_home_loginsync), Toast.LENGTH_SHORT).show();
					return;
				}
				if(!sharedHelper.getAllowSync()) {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_home_allowsync), Toast.LENGTH_SHORT).show();
					return;
				}
				if(sharedHelper.getLocalSync() || sharedHelper.getWebSync()) {
					pbHomeSync.setVisibility(View.VISIBLE);
					btnHomeSync.setEnabled(false);
					tvLabStatus.setText(getString(R.string.txt_home_syncing));
					tvSyncCancel.setVisibility(View.VISIBLE);

					new Thread(new Runnable(){
						@Override
						public void run() {
							try {								
								sharedHelper.setSyncing(true);
								syncHelper.Start();
							} catch (Exception e) {
								sharedHelper.setSyncing(false);
								e.printStackTrace();
							}
							
							Message message = new Message();
							message.what = 1;
							myHandler.sendMessage(message);
						}
					}).start();
				} else {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_home_nosync), Toast.LENGTH_SHORT).show();
				}
			}
		});
				
		//绑定QQ
		btnUserBound.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				doLogin();
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

		//上传按钮
		btnCloudUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!sharedHelper.getLogin()) {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_home_cloudsync), Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!isBackup) {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_backup_isbackup), Toast.LENGTH_SHORT).show();
					return;
				}
				
				pbCloudSync.setVisibility(View.VISIBLE);
				btnCloudUp.setEnabled(false);
				
				new Thread(new Runnable(){
					@Override
					public void run() {
                        String fileName = getCloudFileName();
						boolean success = UtilityHelper.cloudDataUp(SynchronizeActivity.this, fileName, "aalife2.bak");
						
						Bundle bundle = new Bundle();
						bundle.putBoolean("success", success);
						Message message = new Message();
						message.what = 5;
						message.setData(bundle);
						myHandler.sendMessage(message);
					}
				}).start();
			}			
		});
		
		//下载按钮
		btnCloudDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!sharedHelper.getLogin()) {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_home_cloudsync), Toast.LENGTH_SHORT).show();
					return;
				}
				
				pbCloudSync.setVisibility(View.VISIBLE);
				btnCloudDown.setEnabled(false);
				
				new Thread(new Runnable(){
					@Override
					public void run() {
						String fileName = getCloudFileName();
						Boolean success = UtilityHelper.cloudDataDown(SynchronizeActivity.this, fileName, "aalife2.bak");
						
						Bundle bundle = new Bundle();
						bundle.putBoolean("success", success);
						Message message = new Message();
						message.what = 6;
						message.setData(bundle);
						myHandler.sendMessage(message);
					}
				}).start();
			}			
		});
		
		//数据导出选择日期
		etDataDate1 = (EditText) super.findViewById(R.id.et_data_date1);
		etDataDate1.setText(UtilityHelper.formatDate(beginDate, "y-m-d-w"));
		etDataDate1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] array = beginDate.split("-");
				DatePickerDialog dateDialog = new MyDatePickerDialog(SynchronizeActivity.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						String date = UtilityHelper.formatDate(year + "-" + (month + 1) + "-" + day, "");
						beginDate = date;
						etDataDate1.setText(UtilityHelper.formatDate(beginDate, "y-m-d-w"));
					}					
				}, Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]));
				dateDialog.show();
			}
		});
		etDataDate2 = (EditText) super.findViewById(R.id.et_data_date2);
		etDataDate2.setText(UtilityHelper.formatDate(endDate, "y-m-d-w"));
		etDataDate2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] array = endDate.split("-");
				DatePickerDialog dateDialog = new MyDatePickerDialog(SynchronizeActivity.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						String date = UtilityHelper.formatDate(year + "-" + (month + 1) + "-" + day, "");
						endDate = date;
						etDataDate2.setText(UtilityHelper.formatDate(endDate, "y-m-d-w"));
					}					
				}, Integer.parseInt(array[0]), Integer.parseInt(array[1]) - 1, Integer.parseInt(array[2]));
				dateDialog.show();
			}
		});
		
		//数据导出按钮
		Button btnDataExport = (Button) super.findViewById(R.id.btn_data_export);
		btnDataExport.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				String name = beginDate + "~" + endDate + ".txt";
				
				boolean success = UtilityHelper.exportData(SynchronizeActivity.this, beginDate, endDate, name);
				if(success) {
					Toast.makeText(SynchronizeActivity.this, String.format(getString(R.string.txt_data_success), name), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SynchronizeActivity.this, getString(R.string.txt_data_error), Toast.LENGTH_SHORT).show();
				}
			}			
		});

		//用户登出
		tvUserLogout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				sharedHelper.setLogin(false);
				SynchronizeActivity.this.onCreate(null);
			}
		});
		
		//取消同步
		tvSyncCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				sharedHelper.setSyncing(false);
				sharedHelper.setSyncStatus(getString(R.string.txt_sync_cancelnote));
				
				Message message = new Message();
				message.what = 3;
				myHandler.sendMessage(message);				
			}
		});
		
	}
	
	//取云备份文件名
	private String getCloudFileName() {
		return "aalife2(" + sharedHelper.getUserId() + sharedHelper.getUserName() + ").bak";
	}
	
	//显示荣耀
	private void showUserHonor() {		
		itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
		int count = itemAccess.findAllItemCount();
		itemAccess.close();
		
		String joinDate = sharedHelper.getJoinDate();
		int day = UtilityHelper.getJoinDayFromApp(joinDate);
		tvUserHonor.setText(String.format(getString(R.string.txt_sync_user_honor), day, count));			
	}
	
	//QQ登录方法
	private void doLogin() {
	 	IUiListener listener = new BaseUIListener() {
	 		@Override
	 		protected void doComplete(final JSONObject values) {
	 			new Thread(new Runnable(){
					@Override
					public void run() {
						int result = 0;
						try {
							result = UtilityHelper.boundUser(values.getString("openid"), values.getString("access_token"), "sjqq", sharedHelper.getUserId());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						Message message = new Message();
						message.obj = result;
						message.what = 4;
						myHandler.sendMessage(message);
					}	 				
	 			}).start();
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
		
	//返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FIRST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			handler.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	if(sharedHelper.getLogin()) {
			        	tvUserText.setText(sharedHelper.getUserName() + getString(R.string.txt_sync_user_login));
					
			        	setUserImage();
		        	}
		        }
		    }, 1500); 
			
			this.onCreate(null);
		}
	}

	//设置用户头像
	protected void setUserImage() {
		String userImage = sharedHelper.getUserImage();
		if(!userImage.equals("")) {
			ivUserImage.setImageBitmap(UtilityHelper.getUserImage(SynchronizeActivity.this, userImage));
			ivUserImage.setVisibility(View.VISIBLE);
		}	
	}
	
	//关闭this
	protected void close() {
		this.finish();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (mQQAuth != null) {
			mQQAuth = null;
        }
	}

	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<SynchronizeActivity> myActivity = null;
		MyHandler(SynchronizeActivity activity) {
			myActivity = new WeakReference<SynchronizeActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			SynchronizeActivity activity = myActivity.get();
			try {
				boolean syncFlag = activity.sharedHelper.getSyncing();
				String syncStatus = "";
				switch(msg.what) {
				case 1:
					activity.pbHomeSync.setVisibility(View.GONE);
					activity.btnHomeSync.setEnabled(true);
					activity.tvSyncCancel.setVisibility(View.GONE);
					
					syncStatus = activity.sharedHelper.getSyncStatus();				
					if(!syncFlag) {
						syncStatus = activity.getString(R.string.txt_home_syncerror);
						activity.sharedHelper.setSyncStatus(syncStatus);
						Toast.makeText(activity, syncStatus, Toast.LENGTH_SHORT).show();
					} else {
	                    activity.sharedHelper.setSyncing(false);
					}
					
					activity.tvLabStatus.setText(syncStatus);

					//显示荣耀
					activity.showUserHonor();
					
					break;
				case 2:
					int result = msg.getData().getInt("result");
					if(result == 1 && !syncFlag) {
						syncStatus = activity.getString(R.string.txt_home_haswebsync);
						activity.sharedHelper.setSyncStatus(syncStatus);
						activity.sharedHelper.setWebSync(true);
						
						activity.tvLabStatus.setText(syncStatus);
					}
								
					break;
				case 3:
					if(activity.sharedHelper.getSyncing()) {
						activity.pbHomeSync.setVisibility(View.VISIBLE);
						activity.btnHomeSync.setEnabled(false);
						activity.sharedHelper.setSyncStatus(activity.getString(R.string.txt_home_syncing));
						activity.tvSyncCancel.setVisibility(View.VISIBLE);
					} else {
						activity.myHandler.removeCallbacks(activity.runnable);
						
						activity.pbHomeSync.setVisibility(View.GONE);
						activity.btnHomeSync.setEnabled(true);
						activity.tvLabStatus.setText(activity.sharedHelper.getSyncStatus());
						activity.tvSyncCancel.setVisibility(View.GONE);
					}
					
					break;
				case 4:
					int bound = (Integer) msg.obj;
					if(bound == 1) {
						activity.sharedHelper.setUserBound(true);
						activity.btnUserBound.setVisibility(View.GONE);
						Toast.makeText(activity, R.string.txt_sync_user_bound_ok, Toast.LENGTH_SHORT).show();
					} else if(bound == 2){
						Toast.makeText(activity, R.string.txt_sync_user_bound_ready, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(activity, R.string.txt_sync_user_bound_error, Toast.LENGTH_SHORT).show();
					}
					
					activity.mQQAuth.logout(activity);
					break;
				case 5:
					activity.pbCloudSync.setVisibility(View.GONE);
					activity.btnCloudUp.setEnabled(true);
					
					boolean successUp = msg.getData().getBoolean("success");
					if(successUp) {
						Toast.makeText(activity, activity.getString(R.string.txt_cloud_up_success), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(activity, activity.getString(R.string.txt_cloud_up_error), Toast.LENGTH_SHORT).show();
					}
					
					break;
				case 6:
					activity.pbCloudSync.setVisibility(View.GONE);
					activity.btnCloudDown.setEnabled(true);
					
					boolean successDown = msg.getData().getBoolean("success");
					if(successDown) {
						Toast.makeText(activity, activity.getString(R.string.txt_cloud_down_success), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(activity, activity.getString(R.string.txt_cloud_down_error), Toast.LENGTH_SHORT).show();
					}
					
					break;
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
