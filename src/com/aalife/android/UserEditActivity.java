package com.aalife.android;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UserEditActivity extends Activity {
	private EditText etUserName = null;
	private EditText etUserPass = null;
	private EditText etUserNickName = null;
	private EditText etUserEmail = null;
	private EditText etSetCategoryRate = null;
	private Button btnUserEdit = null;
	private SharedHelper sharedHelper = null;
	private MyHandler myHandler = new MyHandler(this);
	private UserEntity user = null;
	private ProgressBar pbUserLoading = null;
	private ImageView ivUserImage = null;
	private int userImageSize = 0;
	private static int RESULT_LOAD_IMAGE = 1;
	private Spinner spWorkDay = null;
	private ArrayAdapter<CharSequence> workDayAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_edit);

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
		TextView tvSetCategoryRate = (TextView) super.findViewById(R.id.tv_set_categoryrate);
		textPaint = tvSetCategoryRate.getPaint();
		textPaint.setFakeBoldText(true);
		
		//初始化
		sharedHelper = new SharedHelper(this);
		etUserName = (EditText) super.findViewById(R.id.et_user_name);
		etUserPass = (EditText) super.findViewById(R.id.et_user_pass);
		etUserNickName = (EditText) super.findViewById(R.id.et_user_nickname);
		etUserEmail = (EditText) super.findViewById(R.id.et_user_email);
		etSetCategoryRate = (EditText) super.findViewById(R.id.et_set_categoryrate);
		userImageSize = this.getResources().getDimensionPixelSize(R.dimen.user_image_size);
		pbUserLoading = (ProgressBar) super.findViewById(R.id.pb_user_loading);
		pbUserLoading.setVisibility(View.VISIBLE);
		
		//工作日
		String[] workDayArr = getResources().getStringArray(R.array.workday);
		spWorkDay = (Spinner) super.findViewById(R.id.sp_workday);
		workDayAdapter = new ArrayAdapter<CharSequence>(this, R.layout.layout_spinner, workDayArr);
		workDayAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown);
		spWorkDay.setAdapter(workDayAdapter);
		
		//头像按钮
		ivUserImage = (ImageView) super.findViewById(R.id.iv_userimage);
		ivUserImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			    startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}			
		});
		
		//头像修改按钮
		ImageView ivImageEdit = (ImageView) super.findViewById(R.id.iv_imageedit);
		ivImageEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			    startActivityForResult(intent, RESULT_LOAD_IMAGE);
			}			
		});
		
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				UserEditActivity.this.setResult(Activity.RESULT_OK);
				UserEditActivity.this.close();
			}			
		});
		
		//修改资料按钮
		btnUserEdit = (Button) super.findViewById(R.id.btn_user_edit);
		btnUserEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String userName = etUserName.getText().toString().trim();
				if (userName.equals("")) {
					Toast.makeText(UserEditActivity.this, getString(R.string.txt_user_username) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}

				final String userPass = etUserPass.getText().toString().trim();
				if (userPass.equals("")) {
					Toast.makeText(UserEditActivity.this, getString(R.string.txt_user_userpass) + getString(R.string.txt_nonull), Toast.LENGTH_SHORT).show();
					return;
				}
				
				final String userEmail = etUserEmail.getText().toString().trim();
				if(!userEmail.equals("") && !UtilityHelper.isEmailAddress(userEmail)) {
					Toast.makeText(UserEditActivity.this, getString(R.string.txt_user_emailerror), Toast.LENGTH_SHORT).show();
					return;
				}

				final String categoryRate = etSetCategoryRate.getText().toString().trim();
				if(!UtilityHelper.checkDouble(categoryRate)) {
					Toast.makeText(UserEditActivity.this, getString(R.string.txt_set_categorytext), Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				final int userId = sharedHelper.getUserId();		
				final String userNickName = etUserNickName.getText().toString().trim();				
				final String userImage = (!sharedHelper.getUserQQImage().equals("") ? sharedHelper.getUserQQImage() : sharedHelper.getUserImage());
				final String userFrom = getString(R.string.app_client);		
				final String userWorkDay = String.valueOf(spWorkDay.getSelectedItemPosition() + 1);
				
				pbUserLoading.setVisibility(View.VISIBLE);
				btnUserEdit.setEnabled(false);
				
				new Thread(new Runnable(){
					@Override
					public void run() {						
						int result = UtilityHelper.editUser(userId, userName, userPass, userNickName, userImage, userEmail, userFrom, userWorkDay, categoryRate);
						
						Bundle bundle = new Bundle();
						bundle.putInt("result", result);
						bundle.putString("username", userName);
						bundle.putString("userpass", userPass);
						bundle.putString("nickname", userNickName);
						bundle.putString("useremail", userEmail);
						bundle.putString("userworkday", userWorkDay);
						bundle.putString("categoryrate", categoryRate);
						Message message = new Message();
						message.what = 1;
						message.setData(bundle);
						myHandler.sendMessage(message);
					}
				}).start();
			}			
		});
		
		//获取用户资料
		new Thread(new Runnable(){
			@Override
			public void run() {							
				String userName = sharedHelper.getUserName();
				String userPass = sharedHelper.getUserPass();
				user = UtilityHelper.loginUser(userName, userPass, "0");
				
				Message message = new Message();
				message.what = 2;
				myHandler.sendMessage(message);
			}
		}).start();
		
		//显示头像
		String userImage = sharedHelper.getUserImage();
		if(!userImage.equals("")) {
			ivUserImage.setImageBitmap(UtilityHelper.getUserImage(this, userImage));
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
			UserEditActivity.this.setResult(Activity.RESULT_OK);
			UserEditActivity.this.close();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<UserEditActivity> myActivity = null;
		MyHandler(UserEditActivity activity) {
			myActivity = new WeakReference<UserEditActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			UserEditActivity activity = myActivity.get();
			switch(msg.what) {
			case 1:
				activity.pbUserLoading.setVisibility(View.GONE);
				activity.btnUserEdit.setEnabled(true);
				
				int result = msg.getData().getInt("result");
				
				if(result == 1) {					
					String userName = msg.getData().getString("username");
					activity.sharedHelper.setUserName(userName);	
					String userPass = msg.getData().getString("userpass");
					activity.sharedHelper.setUserPass(userPass);				
					String userNickName = msg.getData().getString("nickname");
					activity.sharedHelper.setUserNickName(userNickName);
					String userEmail = msg.getData().getString("useremail");
					activity.sharedHelper.setUserEmail(userEmail);
					String userWorkDay = msg.getData().getString("userworkday");
					activity.sharedHelper.setUserWorkDay(userWorkDay);
					String categoryRate = msg.getData().getString("categoryrate");
					activity.sharedHelper.setCategoryRate(categoryRate);
					
					Toast.makeText(activity, activity.getString(R.string.txt_user_editsuccess), Toast.LENGTH_SHORT).show();
					activity.setResult(Activity.RESULT_OK);
					activity.close();
				} else if(result == 2) {
					Toast.makeText(activity, activity.getString(R.string.txt_user_userrepeat), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, activity.getString(R.string.txt_user_editerror), Toast.LENGTH_SHORT).show();
				}
				
				break;
			case 2:
				activity.pbUserLoading.setVisibility(View.GONE);
				
				if(activity.user.userId != 0) {
					activity.etUserName.setText(activity.user.userName);
					activity.etUserPass.setText(activity.user.userPassword);
					activity.etUserNickName.setText(activity.user.userNickName);
					activity.etUserEmail.setText(activity.user.userEmail);
					activity.spWorkDay.setSelection(Integer.parseInt(activity.user.userWorkDay) - 1);
					activity.etSetCategoryRate.setText(UtilityHelper.formatDouble(activity.user.categoryRate, "0.###"));
					
					String userImage = activity.user.userImage;
					if(!userImage.equals("")) {
						if(userImage.startsWith("http")) {
							userImage = activity.sharedHelper.getUserImage();
						}
						activity.ivUserImage.setImageBitmap(UtilityHelper.getUserImage(activity, userImage));
					}
				} else {
					activity.etUserName.setText(activity.sharedHelper.getUserName());
					activity.etUserPass.setText(activity.sharedHelper.getUserPass());
					activity.etUserNickName.setText(activity.sharedHelper.getUserNickName());
					activity.etUserEmail.setText(activity.sharedHelper.getUserEmail());
					activity.spWorkDay.setSelection(Integer.parseInt(activity.sharedHelper.getUserWorkDay()) - 1);
					activity.etSetCategoryRate.setText(activity.sharedHelper.getCategoryRate());					
				}
				
				break;
			case 3:
				activity.pbUserLoading.setVisibility(View.GONE);
				
				boolean success = msg.getData().getBoolean("success");
				if(success) {
					String newUserImage = msg.getData().getString("userimage");
					activity.sharedHelper.setUserImage(newUserImage);
					
					Toast.makeText(activity, activity.getString(R.string.txt_user_imagesuccess), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, activity.getString(R.string.txt_user_imageerror), Toast.LENGTH_SHORT).show();
				}
				
				break;
			}
		}			
	};
	
	//图片选择返回处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
			Uri originalUri = data.getData();
			ContentResolver resolver = getContentResolver();
			
			Cursor cursor = null;
			String imgExtName = "";
			try {
				cursor = resolver.query(originalUri, null, null, null, null); 
				cursor.moveToFirst(); 
				String imgName = cursor.getString(3);
				imgExtName = UtilityHelper.getFileExtName(imgName);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				cursor.close(); 
			}
					
			Bitmap bitmap = null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			bitmap = UtilityHelper.resizeBitmap(bitmap, userImageSize);			
			final String newUserImage = "tu_" + sharedHelper.getUserId() + imgExtName;
			
			boolean success = UtilityHelper.saveBitmap(this, bitmap, newUserImage);
			if(success) {
				pbUserLoading.setVisibility(View.VISIBLE);
				ivUserImage.setImageBitmap(UtilityHelper.getUserImage(this, newUserImage));
				
				new Thread(new Runnable(){
					@Override
					public void run() {		
						boolean success = UtilityHelper.postBitmap(UserEditActivity.this, newUserImage);
						
						Bundle bundle = new Bundle();
						bundle.putBoolean("success", success);
						bundle.putString("userimage", newUserImage);
						Message message = new Message();
						message.what = 3;
						message.setData(bundle);
						myHandler.sendMessage(message);
					}
				}).start();
			}
		}
	}
	
}
