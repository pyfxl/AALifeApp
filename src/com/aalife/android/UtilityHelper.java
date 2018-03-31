package com.aalife.android;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;


public class UtilityHelper {
	//private static final String WEBURL = "http://192.168.1.105:81";
	//private static final String WEBURL = "http://10.0.2.2:81";
	private static final String WEBURL = "http://www.fxlweb.com";
	
	public UtilityHelper() {
		
	}
	
	//取当前日期
	public static String getCurDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Calendar c = Calendar.getInstance();
		return sdf.format(c.getTime());
	}
	
	//取当前时间
	public static String getCurTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
		Calendar c = Calendar.getInstance();
		return sdf.format(c.getTime());
	}
	
	//取当前月第一天
	public static String getMonthFirst() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return sdf.format(c.getTime());
	}

	//取当前日期日期
	public static String getCurDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Calendar c = Calendar.getInstance();
		return sdf.format(c.getTime());
	}
	
	//取同步日期
	public static String getSyncDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
		Calendar c = Calendar.getInstance();
		return sdf.format(c.getTime());
	}
	
	//取每日消费导航日期
	public static String getNavDate(String date, int value, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		if(type.equals("d")) {
			c.add(Calendar.DATE, value);
		} else if(type.equals("m")) {
			c.add(Calendar.MONTH, value);
		} else if(type.equals("y")) {
			c.add(Calendar.YEAR, value);
		}
		
		return sdf.format(c.getTime());
	}
	
	//格式化导航日期
	public static String formatDate(String date, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = formatFull(c.get(Calendar.MONTH) + 1);
		String day = formatFull(c.get(Calendar.DAY_OF_MONTH));	
		String week = formatWeek(c.get(Calendar.DAY_OF_WEEK) - 1);
		
		if(type.equals("m-d-w"))
			return month + "-" + day + "  " + week;
		else if(type.equals("y"))
			return year;
		else if(type.equals("d"))
			return day;
		else if(type.equals("m"))
			return month;
		else if(type.equals("y-m"))
			return year + "-" + month;
		else if(type.equals("y-m-w"))
			return year + "-" + month + "  " + week;
		else if(type.equals("y2-m2"))
			return year + "年" + month + "月";
		else if(type.equals("ys-m"))
			return year.substring(2) + "-" + month;
		else if(type.equals("ys-m-d"))
			return year.substring(2) + "-" + month + "-" + day;
		else if(type.equals("m-d"))
			return month + "-" + day;
		else if(type.equals("m2"))
			return month + "月";
		else if(type.equals("y-m-d-w"))
			return year + "-" + month + "-" + day + "  " + week;
		else if(type.equals("y-m-d-w2"))
			return year + "-" + month + "-" + day + "  周" + week;
		else
			return year + "-" + month + "-" + day;
	}
	
	//格式化导航日期
	public static int[] getDateArray(String date) {
		int[] dates = new int[3];
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		dates[0] = c.get(Calendar.YEAR);
		dates[1] = c.get(Calendar.MONTH);
		dates[2] = c.get(Calendar.DAY_OF_MONTH);
		
		return dates;
	}
	
	//日期补0
	protected static String formatFull(int x) {
		String s = "" + x;
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}

	//取星期
	protected static String formatWeek(int x) {
		String s = "";
		switch(x) {
		case 0:
			s = "日";
			break;
		case 1:
			s = "一";
			break;
		case 2:
			s = "二";
			break;
		case 3:
			s = "三";
			break;
		case 4:
			s = "四";
			break;
		case 5:
			s = "五";
			break;
		case 6:
			s = "六";
			break;
		}
		return s;
	}
	
	//比较日期
	public static boolean compareDate(String date){
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);		
		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return d.before(now);
	}

	//比较日期
	public static boolean compareDate(String date1, String date2) {
		boolean result = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		
		try {
			Date d1 = sdf.parse(date1);
			Date d2 = sdf.parse(date2);
			if(d1.before(d2) || d1.equals(d2)) {
				result = true;
			} else {
				result = false;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//创建随机用户名
	public static String createUserName() {
		Random r = new Random();
		int max = 99999;
		int min = 10000;
		return "aa" + String.valueOf(r.nextInt(max)%(max-min+1) + min);
	}
	
	//恢复数据
	public static int startRestore(Context context, String name) {
		int result = 0;
		try {
			File file = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + name);
			} else {
				file = context.getFileStreamPath(name);
			}
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if(!file.exists()) {
				file.createNewFile();
			}
			
			FileInputStream input = new FileInputStream(file);			
			List<CharSequence> list = new ArrayList<CharSequence>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String str = "";
			while((str = reader.readLine()) != null) {
				//恢复用户JSON
				if(str.indexOf('{') >= 0) {
					restoreUserJson(context, str);
					continue;
				}
				list.add(str);
			}
			
			//更改类别名称
			//list.add("UPDATE CategoryTable SET CategoryName='杂费用' WHERE CategoryName='费用'");
			
			reader.close();
			input.close();
			
			if(list.size() > 0) {
				ItemTableAccess itemAccess = new ItemTableAccess(new DatabaseHelper(context).getReadableDatabase());
				Boolean flag = itemAccess.restoreDataBase(list);
				itemAccess.close();
				if(flag) {
					result = 1;
				} else { 
					result = 0;
				}
			} else {
				result = 2;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		//0失败1成功2无数据
		return result;
	}
	
	//备份数据
	public static boolean startBackup(Context context, String name) {
		ItemTableAccess itemAccess = new ItemTableAccess(new DatabaseHelper(context).getReadableDatabase());
		List<CharSequence> list = itemAccess.bakDataBase();
		itemAccess.close();
		
		try {
			FileOutputStream output = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + name);
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				output = new FileOutputStream(file, false);				
				
				File nomedia = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + ".nomedia");
				if(!nomedia.exists()) nomedia.createNewFile();
			} else {
				output = context.openFileOutput(name, Activity.MODE_PRIVATE);
				
				File nomedia = context.getFileStreamPath(".nomedia");
				if(!nomedia.exists()) nomedia.createNewFile();
			}

			PrintStream out = new PrintStream(output);
			
			//备份用户JSON
			out.println(backupUserJson(context));
			out.println("");
			
			Iterator<CharSequence> it = list.iterator();
			while(it.hasNext()) {
				out.println(it.next());
			}

			out.close();
			output.close();				
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}	
		
		return true;
	}

	//导出数据
	public static boolean exportData(Context context, String beginDate, String endDate, String name) {
		ItemTableAccess itemAccess = new ItemTableAccess(new DatabaseHelper(context).getReadableDatabase());
		List<CharSequence> list = itemAccess.exportDataByDate(beginDate, endDate);
		itemAccess.close();
		
		try {
			FileOutputStream output = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + name);
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				output = new FileOutputStream(file, false);
			} else {
				output = context.openFileOutput(name, Activity.MODE_PRIVATE);
			}
			//添加头,使之成为utf-8+bom
			output.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
			PrintWriter out = new PrintWriter(output);
			Iterator<CharSequence> it = list.iterator();
			while(it.hasNext()) {
				out.println(it.next());
			}

			out.close();
			output.close();				
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}	
		
		return true;
	}

	//复制备份数据
	public static void copyBackup(Context context) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File filein = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + "aalife.bak");
				if(!filein.getParentFile().exists()) {
					filein.getParentFile().mkdirs();
				}
				File fileout = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + "aalife.copy");
				if(!fileout.getParentFile().exists()) {
					fileout.getParentFile().mkdirs();
				}

				fis = new FileInputStream(filein);
				fos = new FileOutputStream(fileout, false);
			} else {
				fis = context.openFileInput("aalife.bak");
				fos = context.openFileOutput("aalife.copy", Activity.MODE_PRIVATE);
			}
			
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(fos);
			byte[] b = new byte[1024 * 2];
			int len = 0;
			while((len=bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}	
			
			bos.flush();
			fis.close();
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bis != null) {
					bis.close();
				}
				if(bos != null) {
					bos.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	//检查是否有备份
	public static boolean hasBackupFile(Context context) {
		try {
			File file = null;
		    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + "aalife.bak");
			} else {
				file = context.getFileStreamPath("aalife.bak");
			}
		    if(file.exists()) {
		    	return true;
		    }
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	//备份用户数据JSON
	protected static String backupUserJson(Context context) {
		SharedHelper sharedHelper = new SharedHelper(context);
		
		String result = "{";
		result += "\"userid\":\"" + sharedHelper.getUserId() + "\",";
		result += "\"username\":\"" + sharedHelper.getUserName() + "\",";
		result += "\"userpass\":\"" + sharedHelper.getUserPass() + "\",";
		result += "\"usernickname\":\"" + sharedHelper.getUserNickName() + "\",";
		result += "\"createdate\":\"" + sharedHelper.getJoinDate() + "\",";
		result += "\"useremail\":\"" + sharedHelper.getUserEmail() + "\",";
		result += "\"userphone\":\"" + sharedHelper.getUserPhone() + "\",";
		result += "\"userworkday\":\"" + sharedHelper.getUserWorkDay() + "\",";
		result += "\"usermoney\":\"" + sharedHelper.getUserMoney() + "\",";
		result += "\"categoryrate\":\"" + sharedHelper.getCategoryRate() + "\",";
		result += "\"userbound\":\"" + (sharedHelper.getUserBound() ? 1 : 0) + "\",";
		
		String userImage = sharedHelper.getUserImage();
		if(userImage.startsWith("http")) {
		    result += "\"userimage\":\"" + sharedHelper.getUserQQImage() + "\",";
		} else {
			result += "\"userimage\":\"" + sharedHelper.getUserImage() + "\",";
		}		
		
		//其它
		result += "\"login\":\"" + sharedHelper.getLogin() + "\",";
		result += "\"category\":\"" + sharedHelper.getCategory() + "\",";
		result += "\"cardid\":\"" + sharedHelper.getCardId() + "\",";
		result += "\"typeid\":\"" + sharedHelper.getTypeId() + "\",";
		result += "\"homeview\":\"" + sharedHelper.getHomeView() + "\",";
		result += "\"localsync\":\"" + sharedHelper.getLocalSync() + "\",";
		result += "\"websync\":\"" + sharedHelper.getWebSync() + "\",";
		result += "\"sync\":\"" + sharedHelper.getSyncStatus() + "\",";
		result += "\"bakdate\":\"" + sharedHelper.getBakDate() + "\",";
		result += "\"lock\":\"" + sharedHelper.getLockText() + "\",";
		result += "\"welcome\":\"" + sharedHelper.getWelcomeText() + "\"";
		result += "}";
		
		return result;
	}
	
	//恢复用户数据JSON
	protected static void restoreUserJson(Context context, String str) {
		SharedHelper sharedHelper = new SharedHelper(context);
		try {
			JSONObject jsonObject = new JSONObject(str);
			if(jsonObject.length() > 0) {
				sharedHelper.setUserId(jsonObject.getInt("userid"));
				sharedHelper.setUserName(jsonObject.getString("username"));
				sharedHelper.setUserPass(jsonObject.getString("userpass"));
				sharedHelper.setUserNickName(jsonObject.getString("usernickname"));
				sharedHelper.setJoinDate(jsonObject.getString("createdate"));
				sharedHelper.setUserEmail(jsonObject.getString("useremail"));
				sharedHelper.setUserPhone(jsonObject.getString("userphone"));
				sharedHelper.setUserWorkDay(jsonObject.getString("userworkday"));
				sharedHelper.setUserMoney(String.valueOf(jsonObject.getDouble("usermoney")));
				sharedHelper.setCategoryRate(UtilityHelper.formatDouble(jsonObject.getDouble("categoryrate"), "0.###"));
				sharedHelper.setUserBound(jsonObject.getInt("userbound") == 1);
				
				String userImage = jsonObject.getString("userimage");
				if(userImage.startsWith("http")) {
					sharedHelper.setUserQQImage(userImage);
				} else {
					userImage = "tu_" + jsonObject.getInt("userid") + ".jpg";
					sharedHelper.setUserImage(userImage);
				}

				//其它
				if(jsonObject.has("login")) {
					sharedHelper.setLogin(jsonObject.getBoolean("login"));
				}
				if(jsonObject.has("category")) {
					sharedHelper.setCategory(jsonObject.getInt("category"));
				}
				if(jsonObject.has("cardid")) {
					sharedHelper.setCardId(jsonObject.getInt("cardid"));
				}
				if(jsonObject.has("typeid")) {
					sharedHelper.setTypeId(jsonObject.getInt("typeid"));
				}
				if(jsonObject.has("homeview")) {
					sharedHelper.setHomeView(jsonObject.getString("homeview"));
				}
				if(jsonObject.has("localsync")) {
					sharedHelper.setLocalSync(jsonObject.getBoolean("localsync"));
				}
				if(jsonObject.has("websync")) {
					sharedHelper.setWebSync(jsonObject.getBoolean("websync"));
				}
				if(jsonObject.has("sync")) {
					sharedHelper.setSyncStatus(jsonObject.getString("sync"));
				}
				if(jsonObject.has("bakdate")) {
					sharedHelper.setBakDate(jsonObject.getString("bakdate"));
				}
				if(jsonObject.has("lock")) {
					sharedHelper.setLockText(jsonObject.getString("lock"));
				}
				if(jsonObject.has("welcome")) {
					sharedHelper.setWelcomeText(jsonObject.getString("welcome"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	//备份用户
	protected static void backupUser(Context context, String name) {
		SharedHelper sharedHelper = new SharedHelper(context);
		try {
			FileOutputStream output = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + name);
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				
				output = new FileOutputStream(file, false);
			} else {
				output = context.openFileOutput(name, Activity.MODE_PRIVATE);
			}
			
			PrintStream out = new PrintStream(output);
			out.println("usermoney:" + sharedHelper.getUserMoney());
			out.println("workday:" + sharedHelper.getUserWorkDay());
			out.println("categeryrate:" + sharedHelper.getCategoryRate());
			out.println("category:" + sharedHelper.getCategory());
			out.println("cardid:" + sharedHelper.getCardId());
			out.println("typeid:" + sharedHelper.getTypeId());
			out.println("homeview:" + sharedHelper.getHomeView());
			out.println("localsync:" + sharedHelper.getLocalSync());
			out.println("sync:" + sharedHelper.getSyncStatus());
			out.println("joindate:" + sharedHelper.getJoinDate());
			out.println("bakdate:" + sharedHelper.getBakDate());
			out.println("lock:" + sharedHelper.getLockText());
			out.println("welcome:" + sharedHelper.getWelcomeText());
			
			output.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//恢复用户
	protected static void restoreUser(Context context, String name) {
		SharedHelper sharedHelper = new SharedHelper(context);
		try {
			File file = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + name);
			} else {
				file = context.getFileStreamPath(name);
			}
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if(!file.exists()) {
				file.createNewFile();
			}
			
			FileInputStream input = new FileInputStream(file);			
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String str = "";
			int num = 0;
			while((str = reader.readLine()) != null) {
				String s = str.substring(str.indexOf(":") + 1);
				num++;
				switch(num) {
					case 1:
						sharedHelper.setUserMoney(s);
						break;
					case 2:
						sharedHelper.setUserWorkDay(s);
						break;
					case 3:
						sharedHelper.setCategoryRate(s);
						break;
					case 4:
						sharedHelper.setCategory(Integer.parseInt(s));
						break;
					case 5:
						sharedHelper.setCardId(Integer.parseInt(s));
						break;
					case 6:
						sharedHelper.setTypeId(Integer.parseInt(s));
						break;
					case 7:
						sharedHelper.setHomeView(s);
						break;
					case 8:
						sharedHelper.setLocalSync(s.equals("true"));
						break;
					case 9:
						sharedHelper.setSyncStatus(s);
						break;
					case 10:
						sharedHelper.setJoinDate(s);
						break;
					case 11:
						sharedHelper.setBakDate(s);
						break;
					case 12:
						sharedHelper.setLockText(s);
						break;
					case 13:
						sharedHelper.setWelcomeText(s);
						break;
				}
			}
			
			reader.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	//清表数据
	public static void clearTableData(Context context) {
		ItemTableAccess itemAccess = new ItemTableAccess(new DatabaseHelper(context).getReadableDatabase());
		itemAccess.clearItemTable();
		itemAccess.close();
	}
	
	//修改资料方法
	public static int editUser(int userId, String userName, String userPass, String userNickName, String userImage, String userEmail, String userFrom, String userWorkDay, String categoryRate) {
		int result = 0;
		String url = WEBURL +  "/AALifeWeb/SyncUserEdit.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("userpass", userPass));
		params.add(new BasicNameValuePair("nickname", userNickName));
		params.add(new BasicNameValuePair("userimage", userImage));
		params.add(new BasicNameValuePair("useremail", userEmail));
		params.add(new BasicNameValuePair("userid", String.valueOf(userId)));
		params.add(new BasicNameValuePair("userfrom", userFrom));
		params.add(new BasicNameValuePair("userworkday", userWorkDay));
		params.add(new BasicNameValuePair("categoryrate", categoryRate));
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getInt("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return result;
	}
	
	//登录用户
	public static UserEntity loginUser(String userName, String userPass, String type) {
		UserEntity user = new UserEntity();
		String url = WEBURL +  "/AALifeWeb/SyncLoginNew.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("userpass", userPass));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("isupdate", "1"));
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				user.userId = jsonObject.getInt("userid");
				user.userName = jsonObject.getString("username");
				user.userPassword = jsonObject.getString("userpass");
				user.userNickName = jsonObject.getString("usernickname");
				user.createDate = jsonObject.getString("createdate");
				user.userEmail = jsonObject.getString("useremail");
				user.userPhone = jsonObject.getString("userphone");
				user.userImage = jsonObject.getString("userimage");
				user.userWorkDay = jsonObject.getString("userworkday");
				user.hasSync = jsonObject.getInt("hassync");
				user.userMoney = jsonObject.getDouble("usermoney");
				user.userBound = jsonObject.getInt("userbound");
				user.categoryRate = jsonObject.getDouble("categoryrate");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return user;
	}

	//登录QQ用户
	public static UserEntity loginQQUser(String userName, String openId, String accessToken, String oAuthFrom, String nickName, String userImage, String userFrom, String type) {
		UserEntity user = new UserEntity();
		String url = WEBURL +  "/AALifeWeb/SyncLoginQQNew.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("openid", openId));
		params.add(new BasicNameValuePair("accesstoken", accessToken));
		params.add(new BasicNameValuePair("oauthfrom", oAuthFrom));
		params.add(new BasicNameValuePair("nickname", nickName));
		params.add(new BasicNameValuePair("userimage", userImage));
		params.add(new BasicNameValuePair("userfrom", userFrom));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("isupdate", "1"));
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				user.userId = jsonObject.getInt("userid");
				user.userName = jsonObject.getString("username");
				user.userPassword = jsonObject.getString("userpass");
				user.userNickName = jsonObject.getString("usernickname");
				user.createDate = jsonObject.getString("createdate");
				user.userEmail = jsonObject.getString("useremail");
				user.userPhone = jsonObject.getString("userphone");
				user.userImage = jsonObject.getString("userimage");
				user.userQQImage = jsonObject.getString("userimage");
				user.userWorkDay = jsonObject.getString("userworkday");
				user.hasSync = jsonObject.getInt("hassync");
				user.userMoney = jsonObject.getDouble("usermoney");
				user.userBound = jsonObject.getInt("userbound");
				user.categoryRate = jsonObject.getDouble("categoryrate");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return user;
	}
	
	//绑定用户
	public static int boundUser(String openId, String accessToken, String oAuthFrom, int userId) {
		int result = 0;
		String url = WEBURL +  "/AALifeWeb/SyncBoundNew.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("openid", openId));
		params.add(new BasicNameValuePair("accesstoken", accessToken));
		params.add(new BasicNameValuePair("oauthfrom", oAuthFrom));
		params.add(new BasicNameValuePair("userid", String.valueOf(userId)));
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getInt("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//注册用户
	public static int addUser(String userName, String userPass, String userNickName, String userEmail, String userFrom, String userWorkDay, String userMoney, String categoryRate) {
		int result = 0;
		String url = WEBURL +  "/AALifeWeb/SyncNewUser.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("userpass", userPass));
		params.add(new BasicNameValuePair("usernickname", userNickName));
		params.add(new BasicNameValuePair("useremail", userEmail));
		params.add(new BasicNameValuePair("userfrom", userFrom));
		params.add(new BasicNameValuePair("userworkday", userWorkDay));
		params.add(new BasicNameValuePair("usermoney", userMoney));
		params.add(new BasicNameValuePair("categoryRate", categoryRate));
		params.add(new BasicNameValuePair("isupdate", "1"));
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getInt("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return result;
	}
	
	//发送邮件
	public static boolean sendEmail(String name, String userImage, String content, String userEmail) {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncSendEmail.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", name));
		params.add(new BasicNameValuePair("userimage", userImage));
		params.add(new BasicNameValuePair("content", content));
		params.add(new BasicNameValuePair("useremail", userEmail));
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post2(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getString("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return result.equals("ok");
	}
	
	//登录同步消费
	public static void syncItemLogin(List<Map<String, String>> list, String userId, String userGroupId) {
		String url = WEBURL +  "/AALifeWeb/SyncItemLogin.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", userId));
			params.add(new BasicNameValuePair("usergroupid", userGroupId));
			params.add(new BasicNameValuePair("itemappid", map.get("itemid")));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					
				}
			} catch(Exception e) {
				continue;
			}
		}
	}
	
	//删除同步修复
	public static boolean deleteSyncFix(int userGroupId) {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/DelSyncFix.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("usergroupid", String.valueOf(userGroupId)));
	
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getString("result");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result.equals("ok");
	}

	//同步公告
	public static String[] getPhoneMessage() {
		String[] result = new String[2];
		String url = WEBURL +  "/AALifeWeb/GetPhoneMessage.aspx";
	
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url));
			if(jsonObject.length() > 0) {
				result[0] = jsonObject.getString("messagecode");
				result[1] = jsonObject.getString("message");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	//登录后保存头像
	public static boolean loadBitmap(Context context, String fileName, String imageName) {
		String url = WEBURL + "/Images/Users/" + fileName;
		if(fileName.startsWith("http")) {
			url = fileName;
			fileName = imageName;
		}
	
	    return downloadFile(context, fileName, url);
	}
	
	//下载云备份
	public static boolean cloudDataDown(Context context, String fileName, String dataName) {
		try {
			String url = WEBURL + "/Backup/Cloud/" + URLEncoder.encode(fileName, "utf-8");
		    return downloadFile(context, dataName, url);
		} catch (Exception e) {
			return false;
		}
	}
	
	//下载jpg,txt方法
	public static boolean downloadFile(Context context, String fileName, String url) {
		FileOutputStream output = null;
		try {
			URL myUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
			if(conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + fileName);
					if(!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					
					output = new FileOutputStream(file, false);
				} else {
					output = context.openFileOutput(fileName, Activity.MODE_PRIVATE);
				}
				
		        byte[] buffer = new byte[1024];
		        int len = -1;
		        while((len = is.read(buffer)) != -1 ){
		            output.write(buffer, 0, len);
		        }

				output.close();
				is.close();
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//选择头像选择图片保存
	public static boolean saveBitmap(Context context, Bitmap bitmap, String fileName) {
		FileOutputStream output = null;
		try {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + fileName);
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				
				output = new FileOutputStream(file, false);
			} else {
				output = context.openFileOutput(fileName, Activity.MODE_PRIVATE);
			}
			
			if(bitmap.compress(Bitmap.CompressFormat.PNG, 80, output)) {
				output.flush();
			}

			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//取文件扩展名
	public static String getFileExtName(String fileName) {
		int start = fileName.lastIndexOf(".");
		return fileName.substring(start);
	}
	
	//显示用户头像
	public static Bitmap getUserImage(Context context, String fileName) {
		File file = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + fileName);
		} else {
			file = context.getFileStreamPath(fileName);
		}
		
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
		if(bitmap == null) {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_user_image);
		}
		
		return bitmap;
	}

	//改变头像大小
	public static Bitmap resizeBitmap(Bitmap bitmap, int newSize) {
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = (float)newSize / width;
		float scaleHeight = (float)newSize / height;
		if(width >= height) {
		    matrix.postScale(scaleWidth, scaleWidth);
		} else {
			matrix.postScale(scaleHeight, scaleHeight);
		}
		return Bitmap.createBitmap(bitmap, 0, 0, (int)width, (int)height, matrix, true);
	}
	
	//同步用户头像
	public static boolean postBitmap(Context context, String fileName) {
		String url = WEBURL + "/AALifeWeb/SyncUserImage.ashx";		
		return uploadFile(context, fileName, fileName, url);
	}
	
	//同步云备份
	public static boolean cloudDataUp(Context context, String fileName, String dataName) {
		String url = WEBURL + "/AALifeWeb/SyncCloudData.ashx";		
		return uploadFile(context, fileName, dataName, url);
	}
	
	//上传jpg,txt文件方法
	public static boolean uploadFile(Context context, String fileName, String dataName,String url) {
        try {
			String lineEnd = "\r\n";  
	        String twoHyphens = "--";  
	        String boundary = "*****";
	        
			URL myUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.writeBytes(twoHyphens + boundary + lineEnd);
	        dos.writeBytes("Content-Disposition:form-data; " + "name=\"file1\"; filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"" + lineEnd);
	        dos.writeBytes(lineEnd);  
	        
	        File file = null;
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + dataName);
			} else {
				file = context.getFileStreamPath(dataName);
			}
			
	        FileInputStream fis = new FileInputStream(file.getPath());
	        byte[] buffer = new byte[1024];
	        int len = -1;
	        while((len = fis.read(buffer)) != -1) {
	            dos.write(buffer, 0, len);
	        }
	        dos.writeBytes(lineEnd);
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

	        fis.close();
	        dos.flush();
            dos.close();
            
            InputStream is = conn.getInputStream();
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }
            
            return sb.toString().equals("1");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//修改用户头像
	public static int editUserImage(String userImage, String userId) {
		int result = 0;
		String url = WEBURL +  "/AALifeWeb/SyncUserImage.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userimage", userImage));
		params.add(new BasicNameValuePair("userid", userId));
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getInt("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return result;
	}
	
	//格式化金额
	public static String formatDouble(double d, String format){
		DecimalFormat df = null;
		df = new DecimalFormat(format);
		return df.format(d);
	}

	//检查网络
	public static boolean checkInternet(Context context, int type) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info != null && info.isConnected()) {
			//type: 1 所有，0 不包括2g
			if(type == 1) {
				return info.isAvailable();
			}
			if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE || info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
				return false;
			} else {
				return info.isAvailable();
			}
		}
		
		return false;
	}
	
	//检查新版本
	public static boolean checkNewVersion(Context context) {
		String versionWeb = getVersionFromWeb();
		String versionApp = getVersionFromApp(context);

		if(versionWeb.equals("") || versionApp.equals("")) {
			return false;
		}
		
		if(Integer.parseInt(versionWeb.replace(".", "")) > Integer.parseInt(versionApp.replace(".", ""))) {
			return true;
		} else {
			return false;
		}
	}
	
	//取APP版本
	public static String getVersionFromApp(Context context) {
		String version = "";		
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			version = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return version;
	}
	
	//取网络版本
	public static String getVersionFromWeb() {
		String version = "";
		String url = WEBURL + "/AALifeWeb/GetWebVersion.aspx";
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url));
			if(jsonObject.length() > 0) {
				version = jsonObject.getString("version");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return version;
	}
	
	//取安装文件
	public static File getInstallFile(Context context) throws Exception {
		String url = WEBURL +  "/app/AALifeNew.apk";
		File file = null;
		try {
			URL myUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
			InputStream is = conn.getInputStream();
						
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "aalife" + File.separator + "AALifeNew.apk");
			} else {
				file = context.getFileStreamPath("AALifeNew.apk");
			}
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			
			fos.close();
			bis.close();
			is.close();
		} catch (Exception e) {
			throw new Exception();
		}
		
		return file;
	}

	//取APP安装日期
	public static String getJoinDateFromApp(Context context) {
		long date = 0L;
		Calendar c = Calendar.getInstance();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			date = pi.firstInstallTime;
			
			c.setTimeInMillis(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH);
	}

	//取APP使用天数
	public static int getJoinDayFromApp(String joinDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date d = new Date();
		Date n = new Date();
		try {
			d = sdf.parse(joinDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (int)((n.getTime()-d.getTime())/(24*60*60*1000)) + 1;
	}
	
	//取月区间
	public static int getMonthRegion(String date1, String date2) {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(sdf.parse(date1));
			c2.setTime(sdf.parse(date2));
			
			result = (c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR)) * 12 + (c2.get(Calendar.MONTH)-c1.get(Calendar.MONTH));
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		
		return result;
	}
	
	//取月区间
	public static int getMonthRegion(String date1, String date2, String regionType) {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(sdf.parse(date1));
			c2.setTime(sdf.parse(date2));
			
			if(regionType.equals("d") || regionType.equals("b")) {
				result = (int) ((c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24));
			} else if(regionType.equals("w")) {
				result = (int) Math.floor((double)((c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24)/7));
			} else if(regionType.equals("m")) {
				result = (c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR)) * 12 + (c2.get(Calendar.MONTH)-c1.get(Calendar.MONTH));
			} else if(regionType.equals("j")) {
				result = (4 * (c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR))) + (int) Math.floor(c2.get(Calendar.MONTH)/3) - (int) Math.floor(c1.get(Calendar.MONTH)/3);
			} else if(regionType.equals("y")) {
				result = (c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		
		return result;
	}
	
	//取最大RegionID
	public static int getMaxRegionID(String userGroupId) {
		int result = 0;
		String url = WEBURL +  "/AALifeWeb/GetMaxRegionID.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("usergroupid", userGroupId));
	
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = Integer.parseInt(jsonObject.getString("result"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//取统计URL
	public static String getTongJiURL(int type) {
		return WEBURL + "/QuWeiTongJiNew.aspx?flag=" + type;
	}
	
	//取每日消费导航日期
	public static String getRegionDate(String date, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		if(type.equals("d") || type.equals("w") || type.equals("b")) {
			c.add(Calendar.MONTH, 1);
		    c.add(Calendar.DATE, -1);
		} else if(type.equals("m")) {
			c.add(Calendar.MONTH, 11);
		} else if(type.equals("y") || type.equals("j")) {
			c.add(Calendar.YEAR, 2);
		}
		
		return sdf.format(c.getTime());
	}
	
	//取商品名称包括区间
	public static String getRegionName(String regionType, int type) {
		String result = "";
		if(regionType.equals("d")) {
			result = (type==0 ? "天" : "每天");
		} else if(regionType.equals("w")) {
			result = (type==0 ? "周" : "每周");
		} else if(regionType.equals("m")) {
			result = (type==0 ? "月" : "每月");
		} else if(regionType.equals("j")) {
			result = (type==0 ? "季" : "每季");
		} else if(regionType.equals("y")) {
			result = (type==0 ? "年" : "每年");
		} else if(regionType.equals("b")) {
			result = (type==0 ? "班" : "工作日");
		}
		
		return result;
	}
	
	//验证邮箱
	public static boolean isEmailAddress(String email) {
		String str = "^([A-Za-z0-9]+[_|\\-]?)+@([A-Za-z0-9]+([_|\\-]?[A-Za-z0-9]+)*)+\\.(([A-Za-z]{2,4})|([A-Za-z]{3}\\.[A-Za-z]{2}))$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}
	
	//取是否工作日
	public static boolean getWorkDayFinal(String date, int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
        switch (day)
        {
            case 1:
                if (week != 1) return false;
                break;
            case 2:
                if (week > 2 || week == 0) return false;
                break;
            case 3:
                if (week > 3 || week == 0) return false;
                break;
            case 4:
                if (week > 4 || week == 0) return false;
                break;
            case 5:
                if (week > 5 || week == 0) return false;
                break;
            case 6:
                if (week == 0) return false;
                break;
        }
        
        return true;
	}
	
	//取分类List用于右边智能添加
	public static List<Map<String, String>> getItemTypeList(String[] arr) {
	    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    for(int i=0; i<arr.length; i++) {
	    	Map<String, String> map = new HashMap<String, String>();
	    	map.put("id", String.valueOf(i));
	    	map.put("name", arr[i]);
	    	list.add(map);
	    }
	    
	    return list;
	}
	
	//替换关键字
	public static String replaceKey(String key) {
		key = key.replace("%", "");
		key = key.replace("'", "");
		
		return key;
	}
	
	//替换行
	public static String replaceLine(String str) {
		return str.replace("\n", "");
	}
	
	//预警灯
	public static double[] getCategoryDen(double catPrice, double catRate) {
		double num = catPrice - catPrice * (catRate / 100);
		double[] result = new double[2];
		result[0] = catPrice - num;
		result[1] = catPrice + num;
		
		return result;
	}
	
	//验证金额
	public static Boolean checkDouble(String s) {
		if(s.length() > 0) {
			Pattern pattern = Pattern.compile("^\\-?\\d+\\.?\\d*$");
			return pattern.matcher(s).matches();
		}
		return false;
	}
	
	//显示虚拟菜单
	public static void showMenuButton(Context context) {
		try {
			if (Build.VERSION.SDK_INT < 22) {  
				((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));  
	        } else {  
	            Method m = Window.class.getDeclaredMethod("setNeedsMenuKey", int.class);  
	            m.setAccessible(true);  
	            m.invoke(((Activity) context).getWindow(), new Object[] { WindowManager.LayoutParams.class.getField("NEEDS_MENU_SET_TRUE").getInt(null) });  
	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//设置振动
	public static void setVibrator(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{ 0, 100, 1300 }, 1);
        vibrator.cancel();
    }
	
	//添加查看
	public static void addView(Context context, int pageId, String dateStart, String remark) {
		try {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();  
			int screenWidth = dm.widthPixels;
			int screenHeight = dm.heightPixels;
			String version = android.os.Build.VERSION.RELEASE;
			String model = android.os.Build.MODEL;
			String brand = android.os.Build.BRAND;
			String network = getNetwork(context);
			
			if(remark.indexOf(",")==0) {
				remark = remark.substring(1, remark.length());
			}
			
			ViewTableAccess viewAccess = new ViewTableAccess(new DatabaseHelper(context).getReadableDatabase());
			viewAccess.addView(pageId, dateStart, brand, version, model, screenWidth, screenHeight, "", network, remark);
			viewAccess.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//取网络类型
	public static String getNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		String type = "";
		if(info != null && info.isConnected()) {
			if(info.getType() == ConnectivityManager.TYPE_WIFI) {
				type = "WIFI";
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE || info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
					type = "2G";
				} else if(info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE) {
					type = "4G";
				} else {
					type = "3G";
				}
			}
		}
		return type;
	}
	
}
