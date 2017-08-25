package com.aalife.android;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class AboutsActivity extends Activity {
	private MyHandler myHandler = new MyHandler(this);
	private EditText etAboutText = null;
	private Button btnSend = null;
	private SharedHelper sharedHelper = null;

	private QzoneShare mQzoneShare = null;
	private QQShare mQQShare = null;
	private QQAuth mQQAuth = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abouts);
		
		//标题变粗
		TextPaint textPaint = null;
		TextView tvAboutEmail = (TextView) super.findViewById(R.id.tv_about_email);
		textPaint = tvAboutEmail.getPaint();
		textPaint.setFakeBoldText(true);
							
		//初始化		
		sharedHelper = new SharedHelper(this);
		etAboutText= (EditText) super.findViewById(R.id.et_about_text);
		mQQAuth = QQAuth.createInstance("100761541", this.getApplicationContext());
	    mQzoneShare = new QzoneShare(this, mQQAuth.getQQToken());
	    mQQShare = new QQShare(this, mQQAuth.getQQToken());

		//QQ空间分享
		TextView tvLabQzone = (TextView) super.findViewById(R.id.tv_lab_qzone);
		tvLabQzone.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				shareToWeb("qzone");
			}
		});

		TextView tvLabQQ = (TextView) super.findViewById(R.id.tv_lab_qq);
		tvLabQQ.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				shareToWeb("qq");
			}
		});
		
		TextView tvLabQun = (TextView) super.findViewById(R.id.tv_lab_qun);
		tvLabQun.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				joinQQGroup("xjRXudQxnyQ_lXghKauoKtDss2d2CZje");
			}
		});
			
		//返回按钮
		ImageButton btnTitleBack = (ImageButton) super.findViewById(R.id.btn_title_back);
		btnTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AboutsActivity.this.close();
			}			
		});
				
		//设置
		ImageButton btnSetting = (ImageButton) super.findViewById(R.id.btn_setting);
		btnSetting.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutsActivity.this, SettingsActivity.class);
				startActivity(intent);
			}			
		});
		
		//发送邮件
		btnSend = (Button) super.findViewById(R.id.btn_about_send);
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String userEmail = sharedHelper.getUserEmail();
				if(userEmail.equals("")) {
					Toast.makeText(AboutsActivity.this, getString(R.string.txt_about_useremail), Toast.LENGTH_SHORT).show();
					return;
				}
				
				final String text = etAboutText.getText().toString().trim();
				if(text.length() < 10) {
					Toast.makeText(AboutsActivity.this, getString(R.string.txt_about_empty), Toast.LENGTH_SHORT).show();
					return;
				}

				btnSend.setEnabled(false);	
				new Thread(new Runnable(){
					@Override
					public void run() {
						String userImage = sharedHelper.getUserImage();
						String userNickName = sharedHelper.getUserNickName();
						String userName = sharedHelper.getUserName();
						String userEmail = sharedHelper.getUserEmail();
						
						String name = userNickName.equals("") ? userName : userNickName;
						boolean result = UtilityHelper.sendEmail(name, userImage, text, userEmail);
						
						Bundle bundle = new Bundle();
						bundle.putBoolean("result", result);	
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (mQzoneShare != null) {
            mQzoneShare.releaseResource();
            mQzoneShare = null;
        }
		if (mQQShare != null) {
            mQQShare.releaseResource();
            mQQShare = null;
        }
		if (mQQAuth != null) {
			mQQAuth = null;
        }
	}

	//多线程处理
	static class MyHandler extends Handler {
		WeakReference<AboutsActivity> myActivity = null;
		MyHandler(AboutsActivity activity) {
			myActivity = new WeakReference<AboutsActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			boolean result = false;
			final AboutsActivity activity = myActivity.get();
			switch(msg.what) {
			case 1:
				activity.btnSend.setEnabled(true);
				
				result = msg.getData().getBoolean("result");
				if(result) {
					activity.etAboutText.setText("");
					Toast.makeText(activity, activity.getString(R.string.txt_about_emailsuccess), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, activity.getString(R.string.txt_about_emailerror), Toast.LENGTH_SHORT).show();					
				}
				
				break;
			case 3:
				result = msg.getData().getBoolean("result");
				if(result) {
					activity.close();
				} else {
					Toast.makeText(activity, activity.getString(R.string.txt_new_updateerror), Toast.LENGTH_SHORT).show();						
				}

				break;
			}
		}			
	};	
	
	//分享到QQ空间
	protected void shareToWeb(String from) {
		String logoUrl = "http://www.fxlweb.com/AALifeWeb/Logo100.png";
		String keyType = "";
		if(from.equals("qzone")) {
			keyType = QzoneShare.SHARE_TO_QZONE_KEY_TYPE;
		} else {
			keyType = QQShare.SHARE_TO_QQ_KEY_TYPE;
		}
		
    	Bundle params = new Bundle();
    	params.putInt(keyType, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
    	params.putString(QQShare.SHARE_TO_QQ_TITLE, getString(R.string.app_name));
    	params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getString(R.string.app_description));
    	params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.fxlweb.com/app/AALifeNew.apk");
    	if(from.equals("qzone")) {
	    	ArrayList<String> imageUrls = new ArrayList<String>();
	 		imageUrls.add(logoUrl);
	 	    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
    	} else {
    		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, logoUrl);
    	}
    	doShareToWeb(from, params);
    }    
	
    protected void doShareToWeb(final String from, final Bundle params) {
        final Activity activity = AboutsActivity.this;
        new Thread(new Runnable() {            
            @Override
            public void run() {
            	if(from.equals("qzone")) {
	            	mQzoneShare.shareToQzone(activity, params, new IUiListener() {
	                    @Override
	                    public void onCancel() {                        
	                    }
	                    @Override
	                    public void onError(UiError e) {                        
	                    }
						@Override
						public void onComplete(Object response) {						
						}
	                });
            	} else {
            		mQQShare.shareToQQ(activity, params, new IUiListener() {    				
        				@Override
        				public void onCancel() {
        				}    				
        				@Override
        				public void onError(UiError e) {
        				}    				
        				@Override
        				public void onComplete(Object response) {
        				}    				
        			});
            	}
            }
        }).start();
    }
    
    /****************
    *
    * 发起添加群流程。群号：AA生活记账(5656929) 的 key 为： xjRXudQxnyQ_lXghKauoKtDss2d2CZje
    * 调用 joinQQGroup(xjRXudQxnyQ_lXghKauoKtDss2d2CZje) 即可发起手Q客户端申请加群 AA生活记账(5656929)
    *
    * @param key 由官网生成的key
    * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
    ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}
