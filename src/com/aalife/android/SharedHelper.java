package com.aalife.android;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {
    private SharedPreferences setting = null;
    
	public SharedHelper(Context context) {
		setting = context.getSharedPreferences("setting", 0);
	}

	//用户ID
	public int getUserId() {
		return setting.getInt("userid", 0);
	}

	public void setUserId(int userId) {
		setting.edit().putInt("userid", userId).commit();
	}
	
	//用户名
	public String getUserName() {
		return setting.getString("username", "");
	}

	public void setUserName(String userName) {
		setting.edit().putString("username", userName).commit();
	}
	
	//用户密码
	public String getUserPass() {
		return setting.getString("password", "");
	}

	public void setUserPass(String userPass) {
		setting.edit().putString("password", userPass).commit();
	}

	//用户昵称
	public String getUserNickName() {
		return setting.getString("nickname", "");
	}

	public void setUserNickName(String userNickName) {
		setting.edit().putString("nickname", userNickName).commit();
	}

	//用户邮箱
	public String getUserEmail() {
		return setting.getString("useremail", "");
	}

	public void setUserEmail(String userEmail) {
		setting.edit().putString("useremail", userEmail).commit();
	}

	//用户头像
	public String getUserImage() {
		return setting.getString("userimage", "");
	}

	public void setUserImage(String userImage) {
		setting.edit().putString("userimage", userImage).commit();
	}

	//用户QQ头像
	public String getUserQQImage() {
		return setting.getString("userqqimage", "");
	}

	public void setUserQQImage(String userQQImage) {
		setting.edit().putString("userqqimage", userQQImage).commit();
	}

	//电话
	public String getUserPhone() {
		return setting.getString("userphone", "");
	}

	public void setUserPhone(String userPhone) {
		setting.edit().putString("userphone", userPhone).commit();
	}

	//工作日
	public String getUserWorkDay() {
		return setting.getString("workday", "5");
	}

	public void setUserWorkDay(String workDay) {
		setting.edit().putString("workday", workDay).commit();
	}
	
	//用户钱包
	public String getUserMoney() {
		return setting.getString("usermoney", "0");
	}

	public void setUserMoney(String userMoney) {
		setting.edit().putString("usermoney", userMoney).commit();
	}

	//用户绑定
	public boolean getUserBound() {
		return setting.getBoolean("userbound", false);
	}

	public void setUserBound(Boolean flag) {
		setting.edit().putBoolean("userbound", flag).commit();
	}

	//预算比率
	public String getCategoryRate() {
		return setting.getString("categeryrate", "90");
	}

	public void setCategoryRate(String categoryRate) {
		setting.edit().putString("categeryrate", categoryRate).commit();
	}
	
	//用户登录
	public boolean getLogin() {
		return setting.getBoolean("login", false);
	}

	public void setLogin(Boolean flag) {
		setting.edit().putBoolean("login", flag).commit();
	}

	//当前日期
	public String getCurDate() {
		return setting.getString("curdate", "");
	}

	public void setCurDate(String curDate) {
		setting.edit().putString("curdate", curDate).commit();
	}

	//注册日期
	public String getJoinDate() {
		return setting.getString("joindate", "");
	}

	public void setJoinDate(String joinDate) {
		setting.edit().putString("joindate", joinDate).commit();
	}

	//备份日期
	public String getBakDate() {
		return setting.getString("bakdate", "");
	}

	public void setBakDate(String bakDate) {
		setting.edit().putString("bakdate", bakDate).commit();
	}

	//当前Category
	public int getCategory() {
		return setting.getInt("category", 0);
	}

	public void setCategory(int category) {
		setting.edit().putInt("category", category).commit();
	}

	//当前CardId
	public int getCardId() {
		return setting.getInt("cardid", 0);
	}

	public void setCardId(int cardId) {
		setting.edit().putInt("cardid", cardId).commit();
	}

	//当前TypeId
	public int getTypeId() {
		return setting.getInt("typeid", 0);
	}

	public void setTypeId(int typeId) {
		setting.edit().putInt("typeid", typeId).commit();
	}

	//同步状态
	public String getSyncStatus() {
		return setting.getString("sync", "");
	}

	public void setSyncStatus(String status) {
		setting.edit().putString("sync", status).commit();
	}
	
	//登录密锁
	public String getLockText() {
		return setting.getString("lock", "");
	}

	public void setLockText(String text) {
		setting.edit().putString("lock", text).commit();
	}
	
	//首页文字
	public String getWelcomeText() {
		return setting.getString("welcome", "");
	}

	public void setWelcomeText(String text) {
		setting.edit().putString("welcome", text).commit();
	}

	//检查更新2g
	public boolean getUpdate() {
		return setting.getBoolean("update", false);
	}

	public void setUpdate(Boolean flag) {
		setting.edit().putBoolean("update", flag).commit();
	}

	//自动备份
	public boolean getAutoBak() {
		return setting.getBoolean("autobak", true);
	}

	public void setAutoBak(Boolean flag) {
		setting.edit().putBoolean("autobak", flag).commit();
	}

	//自动检查更新
	public boolean getAutoNew() {
		return setting.getBoolean("autonew", true);
	}

	public void setAutoNew(Boolean flag) {
		setting.edit().putBoolean("autonew", flag).commit();
	}

	//允许同步
	public boolean getAllowSync() {
		return setting.getBoolean("allowsync", true);
	}

	public void setAllowSync(Boolean flag) {
		setting.edit().putBoolean("allowsync", flag).commit();
	}

	//自动同步
	public boolean getAutoSync() {
		return setting.getBoolean("autosync", true);
	}

	public void setAutoSync(Boolean flag) {
		setting.edit().putBoolean("autosync", flag).commit();
	}

	//本地同步
	public boolean getLocalSync() {
		return setting.getBoolean("localsync", false);
	}

	public void setLocalSync(Boolean flag) {
		setting.edit().putBoolean("localsync", flag).commit();
	}

	//有否同步
	public boolean getHasSync() {
		return setting.getBoolean("hassync", false);
	}

	public void setHasSync(Boolean flag) {
		setting.edit().putBoolean("hassync", flag).commit();
	}

	//首页同步
	public boolean getHomeSync() {
		return setting.getBoolean("homesync", false);
	}

	public void setHomeSync(Boolean flag) {
		setting.edit().putBoolean("homesync", flag).commit();
	}

	//修复同步
	public boolean getFixSync() {
		return setting.getBoolean("fixsync2", false);
	}

	public void setFixSync(Boolean flag) {
		setting.edit().putBoolean("fixsync2", flag).commit();
	}
	
	//网络同步
	public boolean getWebSync() {
		return setting.getBoolean("websync", false);
	}

	public void setWebSync(Boolean flag) {
		setting.edit().putBoolean("websync", flag).commit();
	}

	//备份恢复
	public boolean getRestore() {
		return setting.getBoolean("restore2", false);
	}

	public void setRestore(Boolean flag) {
		setting.edit().putBoolean("restore2", flag).commit();
	}

	//是否备份恢复
	public boolean getHasRestore() {
		return setting.getBoolean("hasrestore", false);
	}

	public void setHasRestore(Boolean flag) {
		setting.edit().putBoolean("hasrestore", flag).commit();
	}
	
	//首次同步网络
	public boolean getFirstSync() {
		return setting.getBoolean("firstsync", false);
	}

	public void setFirstSync(Boolean flag) {
		setting.edit().putBoolean("firstsync", flag).commit();
	}

	//同步中
	public boolean getSyncing() {
		return setting.getBoolean("syncing", false);
	}

	public void setSyncing(Boolean flag) {
		setting.edit().putBoolean("syncing", flag).commit();
	}

	//是否发送
	public boolean getIsSend() {
		return setting.getBoolean("issend", false);
	}

	public void setIsSend(Boolean flag) {
		setting.edit().putBoolean("issend", flag).commit();
	}

	//查看首页年月日
	public String getHomeView() {
		return setting.getString("homeview", "month");
	}

	public void setHomeView(String view) {
		setting.edit().putString("homeview", view).commit();
	}

	//公告版本
	public int getMessageCode() {
		return setting.getInt("messagecode", 1);
	}

	public void setMessageCode(int code) {
		setting.edit().putInt("messagecode", code).commit();
	}

	//是否从钱包计算
	public boolean getIsMoney() {
		return setting.getBoolean("ismoney", true);
	}

	public void setIsMoney(Boolean flag) {
		setting.edit().putBoolean("ismoney", flag).commit();
	}

	//是否已读公告
	public boolean getIsRead() {
		return setting.getBoolean("isread", false);
	}

	public void setIsRead(Boolean flag) {
		setting.edit().putBoolean("isread", flag).commit();
	}

	//是否读贴士
	public boolean getReadTips() {
		return setting.getBoolean("readtips", true);
	}

	public void setReadTips(Boolean flag) {
		setting.edit().putBoolean("readtips", flag).commit();
	}

	//修复钱包余额
	public boolean getFixMoney() {
		return setting.getBoolean("fixmoney", false);
	}

	public void setFixMoney(Boolean flag) {
		setting.edit().putBoolean("fixmoney", flag).commit();
	}

	//修改钱包金额类型
	public String getFixMoneyType() {
		return setting.getString("fixmoneytype", "");
	}

	public void setFixMoneyType(String type) {
		setting.edit().putString("fixmoneytype", type).commit();
	}
	
}
