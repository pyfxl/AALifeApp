package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class SyncHelper {
	private SharedHelper sharedHelper = null;
	private Context context = null;
	private SQLiteOpenHelper sqlHelper = null;
	//private static final String WEBURL = "http://192.168.1.105:81";
	//private static final String WEBURL = "http://10.0.2.2:81";
	private static final String WEBURL = "http://www.fxlweb.com";
	
	public SyncHelper(Context context) {
		this.context = context;
		sharedHelper = new SharedHelper(this.context);
		sqlHelper = new DatabaseHelper(this.context);
		sqlHelper.close();
	}
	
	//开始同步
	public void Start() throws Exception {		
		//同步本地
		if(sharedHelper.getLocalSync()) {
			
			//类别
			CategoryTableAccess categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());			
			List<Map<String, String>> list = categoryAccess.findAllSyncCat();
			categoryAccess.close();
			if(list.size() > 0) {
				try {
					syncCategory(list);
				} catch(Exception e) {
					throw new Exception();
				}
			}

			//专题
			ZhuanTiTableAccess zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());			
			list = zhuanTiAccess.findAllSyncZhuanTi();
			zhuanTiAccess.close();
			if(list.size() > 0) {
				try {
					syncZhuanTi(list);
				} catch(Exception e) {
					throw new Exception();
				}
			}

			//转账
			ZhuanZhangTableAccess zhangAccess = new ZhuanZhangTableAccess(sqlHelper.getReadableDatabase());			
			list = zhangAccess.findSyncZhuanZhang();
			zhangAccess.close();
			if(list.size() > 0) {
				try {
					syncZhuanZhang(list);
				} catch(Exception e) {
					throw new Exception();
				}
			}

			//钱包
			CardTableAccess cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase());			
			list = cardAccess.findAllSyncCard();
			cardAccess.close();
			if(list.size() > 0) {
				try {
					syncCard(list);
				} catch(Exception e) {
					throw new Exception();
				}
			}

			//查看
			ViewTableAccess viewAccess = new ViewTableAccess(sqlHelper.getReadableDatabase());			
			list = viewAccess.findAllSyncView();
			viewAccess.close();
			if(list.size() > 0) {
				try {
					syncView(list);
				} catch(Exception e) {
					throw new Exception();
				}
			}

			//删除
			ItemTableAccess itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
			list = itemAccess.findDelSyncItem();
			itemAccess.close();
			if(list.size() > 0) {				
				try {
					syncDeleteList(list);
				} catch(Exception e) {
					throw new Exception();
				}
			}
			
			//消费
			itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());
			list = itemAccess.findSyncItem();	
			itemAccess.close();	
			if(list.size() > 0) {
				try {
					syncItemList(list);
				} catch(Exception e) {
					throw new Exception();
				}
			}

			//同步用户
			syncUserInfo();
			
			sharedHelper.setLocalSync(false);
			sharedHelper.setSyncStatus(this.context.getString(R.string.txt_home_syncat) + " " + UtilityHelper.getSyncDate());
			
			//检查网络同步数据
			int userId = sharedHelper.getUserId();
			if(checkSyncWeb(userId) == 1) {
				sharedHelper.setWebSync(true);
			}
		}
		
		//同步网络
		if(sharedHelper.getWebSync()) {
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			try {
				//删除
				list = syncGetDeleteListWeb();
				if(list.size() > 0) {
					syncDeleteListWeb(list);
					syncDeleteListWebBack();
				}
				
				//1为第一次同步消费
				if(sharedHelper.getFirstSync()) {
					list = syncGetItemListWeb(1);
					sharedHelper.setFirstSync(false);
				} else {
					list = syncGetItemListWeb(0);
				}

				while(list.size() > 0) {
					if(!sharedHelper.getSyncing()) return;
					
					syncItemListWeb(list);
					
					list = syncGetItemListWeb(0);
				}
				
				//类别
				list = syncGetCategoryWeb();
				if(list.size() > 0) {
					syncCategoryWeb(list);
					syncCategoryWebBack();
				}

				//专题
				list = syncGetZhuanTiWeb();
				if(list.size() > 0) {
					syncZhuanTiWeb(list);
					syncZhuanTiWebBack();
				}

				//转账
				list = syncGetZhuanZhangWeb();
				if(list.size() > 0) {
					syncZhuanZhangWeb(list);
					syncZhuanZhangWebBack();
				}
				
				//钱包
				list = syncGetCardWeb();
				if(list.size() > 0) {
					syncCardWeb(list);
					syncCardWebBack();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			}
			
			//同步网络用户
			syncUserInfoWeb();
			syncUserInfoWebBack();
			
			sharedHelper.setWebSync(false);
			sharedHelper.setSyncStatus(this.context.getString(R.string.txt_home_syncat) + " " + UtilityHelper.getSyncDate());
		}
	}
	
	//同步用户
	public boolean syncUserInfo() throws Exception {
		String result = "";
		String userId = String.valueOf(sharedHelper.getUserId());
		String userMoney = sharedHelper.getUserMoney();
		String userWorkDay = sharedHelper.getUserWorkDay();
		String categoryRate = sharedHelper.getCategoryRate();
		String url = WEBURL +  "/AALifeWeb/SyncUserInfo.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		params.add(new BasicNameValuePair("userfrom", context.getString(R.string.app_client)));
		params.add(new BasicNameValuePair("usermoney", userMoney));
		params.add(new BasicNameValuePair("userworkday", userWorkDay));
		params.add(new BasicNameValuePair("categoryrate", categoryRate));
		params.add(new BasicNameValuePair("isupdate", "1"));

		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getString("result");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result.equals("1");
	}
		
	//同步消费
	public void syncItemList(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		boolean syncing = sharedHelper.getSyncing();
		ItemTableAccess itemAccess = new ItemTableAccess(this.sqlHelper.getReadableDatabase());
		String userId = String.valueOf(sharedHelper.getUserId());
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncItemList.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext() && syncing) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("itemid", map.get("itemid")));
			params.add(new BasicNameValuePair("itemname", map.get("itemname")));
			params.add(new BasicNameValuePair("catid", map.get("catid")));
			params.add(new BasicNameValuePair("itemprice", map.get("itemprice")));
			params.add(new BasicNameValuePair("itembuydate", map.get("itembuydate")));
			params.add(new BasicNameValuePair("userid", userId));
			params.add(new BasicNameValuePair("itemwebid", map.get("itemwebid")));
			params.add(new BasicNameValuePair("recommend", map.get("recommend")));
			params.add(new BasicNameValuePair("regionid", map.get("regionid")));
			params.add(new BasicNameValuePair("regiontype", map.get("regiontype")));
			params.add(new BasicNameValuePair("itemtype", map.get("itemtype")));
			params.add(new BasicNameValuePair("ztid", map.get("ztid")));
			params.add(new BasicNameValuePair("cardid", map.get("cardid")));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					result = jsonObject.getString("result");
				}
			} catch(Exception e) {
				syncFlag = false;
				continue;
			}

			int itemId = Integer.parseInt(map.get("itemid"));
			int itemWebId = 0;
			if(!result.equals("0") || !result.equals("no")) {
				itemWebId = Integer.parseInt(result);
				itemAccess.updateSyncStatus(itemId, itemWebId);
			} else {
				syncFlag = true;
			}
		}
		itemAccess.close();
		
		if(syncFlag || !syncing) {
			throw new Exception();
		}
	}

	//同步类别
	public void syncCategory(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		CategoryTableAccess categoryAccess = new CategoryTableAccess(sqlHelper.getReadableDatabase());
		String userId = String.valueOf(sharedHelper.getUserId());
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncCategory.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("catid", map.get("catid")));
			params.add(new BasicNameValuePair("catname", map.get("catname")));
			params.add(new BasicNameValuePair("catprice", map.get("catprice")));
			params.add(new BasicNameValuePair("catlive", map.get("catlive")));
			params.add(new BasicNameValuePair("userid", userId));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					result = jsonObject.getString("result");
				}
			} catch(Exception e) {
				syncFlag = true;
				continue;
			}
			
			if(result.equals("ok")) {
				int catId = Integer.parseInt(map.get("catid"));
				categoryAccess.updateSyncStatus(catId);
			} else {
				syncFlag = true;
			}
		}
		categoryAccess.close();
		
		if(syncFlag) {
			throw new Exception();
		}
	}

	//同步专题
	public void syncZhuanTi(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		ZhuanTiTableAccess zhuanTiAccess = new ZhuanTiTableAccess(sqlHelper.getReadableDatabase());
		String userId = String.valueOf(sharedHelper.getUserId());
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncZhuanTi.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ztid", map.get("ztid")));
			params.add(new BasicNameValuePair("ztname", map.get("ztname")));
			params.add(new BasicNameValuePair("ztimage", map.get("ztimage")));
			params.add(new BasicNameValuePair("ztlive", map.get("ztlive")));
			params.add(new BasicNameValuePair("userid", userId));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					result = jsonObject.getString("result");
				}
			} catch(Exception e) {
				syncFlag = true;
				continue;
			}
			
			if(result.equals("ok")) {
				int ztId = Integer.parseInt(map.get("ztid"));
				zhuanTiAccess.updateSyncStatus(ztId);
			} else {
				syncFlag = true;
			}
		}
		zhuanTiAccess.close();
		
		if(syncFlag) {
			throw new Exception();
		}
	}

	//同步转账
	public void syncZhuanZhang(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		ZhuanZhangTableAccess zhangAccess = new ZhuanZhangTableAccess(sqlHelper.getReadableDatabase());
		String userId = String.valueOf(sharedHelper.getUserId());
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncZhuanZhang.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("zhangfrom", map.get("zhangfrom")));
			params.add(new BasicNameValuePair("zhangto", map.get("zhangto")));
			params.add(new BasicNameValuePair("zhangmoney", map.get("zhangmoney")));
			params.add(new BasicNameValuePair("zhangdate", map.get("zhangdate")));
			params.add(new BasicNameValuePair("zhangnote", map.get("zhangnote")));
			params.add(new BasicNameValuePair("zhanglive", map.get("zhanglive")));
			params.add(new BasicNameValuePair("userid", userId));
			params.add(new BasicNameValuePair("zzid", map.get("zzid")));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					result = jsonObject.getString("result");
				}
			} catch(Exception e) {
				syncFlag = true;
				continue;
			}
			
			if(result.equals("ok")) {
				int zzId = Integer.parseInt(map.get("zzid"));
				zhangAccess.updateSyncStatus(zzId);
			} else {
				syncFlag = true;
			}
		}
		zhangAccess.close();
		
		if(syncFlag) {
			throw new Exception();
		}
	}

	//同步钱包
	public void syncCard(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		CardTableAccess cardAccess = new CardTableAccess(sqlHelper.getReadableDatabase());
		String userId = String.valueOf(sharedHelper.getUserId());
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncCard.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cardid", map.get("cardid")));
			params.add(new BasicNameValuePair("cardname", map.get("cardname")));
			params.add(new BasicNameValuePair("cardmoney", map.get("cardmoney")));
			params.add(new BasicNameValuePair("cardlive", map.get("cardlive")));
			params.add(new BasicNameValuePair("userid", userId));
			params.add(new BasicNameValuePair("isupdate", "1"));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					result = jsonObject.getString("result");
				}
			} catch(Exception e) {
				syncFlag = true;
				continue;
			}
			
			if(result.equals("ok")) {
				int cdId = Integer.parseInt(map.get("cardid"));
				cardAccess.updateSyncStatus(cdId);
			} else {
				syncFlag = true;
			}
		}
		cardAccess.close();
		
		if(syncFlag) {
			throw new Exception();
		}
	}

	//同步查看
	public void syncView(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		ViewTableAccess viewAccess = new ViewTableAccess(sqlHelper.getReadableDatabase());
		String userId = String.valueOf(sharedHelper.getUserId());
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncView.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pageid", map.get("pageid")));
			params.add(new BasicNameValuePair("datestart", map.get("datestart")));
			params.add(new BasicNameValuePair("dateend", map.get("dateend")));
			params.add(new BasicNameValuePair("portal", map.get("portal")));
			params.add(new BasicNameValuePair("version", map.get("version")));
			params.add(new BasicNameValuePair("browser", map.get("browser")));
			params.add(new BasicNameValuePair("width", map.get("width")));
			params.add(new BasicNameValuePair("height", map.get("height")));
			params.add(new BasicNameValuePair("remark", map.get("remark")));
			params.add(new BasicNameValuePair("userid", userId));
			params.add(new BasicNameValuePair("network", map.get("network")));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					result = jsonObject.getString("result");
				}
			} catch(Exception e) {
				syncFlag = true;
				continue;
			}
			
			if(result.equals("ok")) {
				int viewId = Integer.parseInt(map.get("viewid"));
				viewAccess.updateSyncStatus(viewId);
			} else {
				syncFlag = true;
			}
		}
		viewAccess.close();
		
		if(syncFlag) {
			throw new Exception();
		}
	}
	
	//同步删除
	public void syncDeleteList(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		String userId = String.valueOf(sharedHelper.getUserId());
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncDeleteList.aspx";
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = it.next();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("itemid", map.get("itemid")));
			params.add(new BasicNameValuePair("itemwebid", map.get("itemwebid")));
			params.add(new BasicNameValuePair("userid", userId));
	
			try {
				JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
				if(jsonObject.length() > 0) {
					result = jsonObject.getString("result");
				}
			} catch(Exception e) {
				syncFlag = true;
				continue;
			}
			
			if(!result.equals("ok")) {
				syncFlag = true;
			}
		}	
		
		if(syncFlag) {
			throw new Exception();
		} else {
			ItemTableAccess itemAccess = new ItemTableAccess(sqlHelper.getReadableDatabase());	
			itemAccess.clearDelTable();	
			itemAccess.close();				
		}
	}

	//同步网络用户
	private void syncUserInfoWeb() {
		String userName = sharedHelper.getUserName();
		String userPass = sharedHelper.getUserPass();
		String type = "0";
		UserEntity user = UtilityHelper.loginUser(userName, userPass, type);
		if(user.userId != 0) {
			sharedHelper.setUserNickName(user.userNickName);
			sharedHelper.setJoinDate(user.createDate);
			sharedHelper.setUserEmail(user.userEmail);
			sharedHelper.setUserPhone(user.userPhone);
			sharedHelper.setUserWorkDay(user.userWorkDay);
			sharedHelper.setUserMoney(String.valueOf(user.userMoney));
			sharedHelper.setCategoryRate(UtilityHelper.formatDouble(user.categoryRate, "0.###"));
		}
	}

	//同步网络用户返回
	public void syncUserInfoWebBack() throws Exception {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncUserInfoWebBack.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		
		JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
		if(jsonObject.length() > 0) {
			result = jsonObject.getString("result");
		}
		
		if(!result.equals("ok")) {
			throw new Exception();
		}
	}

	//取同步网络消费
	public List<Map<String, String>> syncGetItemListWeb(int type) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String url = WEBURL + "/AALifeWeb/SyncGetItemListWeb.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		params.add(new BasicNameValuePair("type", String.valueOf(type)));

		JSONObject jsonAll = new JSONObject(HttpHelper.post(url, params));
		JSONArray jsonArray = jsonAll.getJSONArray("itemlist");
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("itemid", jsonObject.getString("itemid"));
			map.put("itemappid", jsonObject.getString("itemappid"));
			map.put("itemname", jsonObject.getString("itemname"));
			map.put("catid", jsonObject.getString("catid"));
			map.put("itemprice", jsonObject.getString("itemprice"));
			map.put("itembuydate", jsonObject.getString("itembuydate"));
			map.put("recommend", jsonObject.getString("recommend"));
			map.put("regionid", jsonObject.getString("regionid"));
			map.put("regiontype", jsonObject.getString("regiontype"));
			map.put("itemtype", jsonObject.getString("itemtype"));
			map.put("ztid", jsonObject.getString("ztid"));
			map.put("cardid", jsonObject.getString("cardid"));
			list.add(map);
		}
		
		return list;
	}

	//同步网络消费
	public void syncItemListWeb(List<Map<String, String>> list) throws Exception {
		boolean syncFlag = false;
		ItemTableAccess itemAccess = new ItemTableAccess(this.sqlHelper.getReadableDatabase());
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			if(!sharedHelper.getSyncing()) return;
			
			Map<String, String> map = (Map<String, String>) it.next();
			int itemId = Integer.parseInt(map.get("itemid"));
			int itemAppId = Integer.parseInt(map.get("itemappid"));
			String itemName = map.get("itemname");
			String itemPrice = map.get("itemprice");
			String itemBuyDate = map.get("itembuydate");
			int catId = Integer.parseInt(map.get("catid"));
			int recommend = Integer.parseInt(map.get("recommend"));
			int regionId = Integer.parseInt(map.get("regionid"));
			String regionType = map.get("regiontype");
			String itemType = map.get("itemtype");
			int ztId = Integer.parseInt(map.get("ztid"));
			int cardId = Integer.parseInt(map.get("cardid"));

			//用于首页实时更新
			if(UtilityHelper.compareDate(itemBuyDate)) {
				sharedHelper.setCurDate(UtilityHelper.formatDate(itemBuyDate, "y-m-d"));
			}
			
			boolean success = itemAccess.addWebItem(itemId, itemAppId, itemName, itemPrice, itemBuyDate, catId, recommend, regionId, regionType, itemType, ztId, cardId);
			if(!success) {
				syncFlag = true;
				continue;
			}
						
			//根据ItemWebID取ItemID
			if(itemAppId == 0) itemAppId = itemAccess.getItemId(itemId);
			
			if(!syncItemListWebBack(itemId, itemAppId)) {
				syncFlag = true;
			} 
		}
		itemAccess.close();
				
		if(syncFlag) {
			throw new Exception();
		}
	}

	//同步网络消费返回
	public boolean syncItemListWebBack(int itemId, int itemAppId) {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncItemListWebBack.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("itemid", String.valueOf(itemId)));
		params.add(new BasicNameValuePair("itemappid", String.valueOf(itemAppId)));
		
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getString("result");
			}
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return result.equals("ok");
	}

	//取同步网络类别
	public List<Map<String, String>> syncGetCategoryWeb() throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String url = WEBURL +  "/AALifeWeb/SyncGetCategoryWeb.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));

		JSONObject jsonAll = new JSONObject(HttpHelper.post(url, params));
		JSONArray jsonArray = jsonAll.getJSONArray("catlist");
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("catid", jsonObject.getString("catid"));
			map.put("catname", jsonObject.getString("catname"));
			map.put("catprice", jsonObject.getString("catprice"));
			map.put("catlive", jsonObject.getString("catlive"));
			list.add(map);
		}
		
		return list;
	}

	//取同步网络专题
	public List<Map<String, String>> syncGetZhuanTiWeb() throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String url = WEBURL +  "/AALifeWeb/SyncGetZhuanTiWeb.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));

		JSONObject jsonAll = new JSONObject(HttpHelper.post(url, params));
		JSONArray jsonArray = jsonAll.getJSONArray("ztlist");
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("ztid", jsonObject.getString("ztid"));
			map.put("ztname", jsonObject.getString("ztname"));
			map.put("ztimage", jsonObject.getString("ztimage"));
			map.put("ztlive", jsonObject.getString("ztlive"));
			list.add(map);
		}
		
		return list;
	}

	//取同步网络转账
	public List<Map<String, String>> syncGetZhuanZhangWeb() throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String url = WEBURL +  "/AALifeWeb/SyncGetZhuanZhangWeb.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));

		JSONObject jsonAll = new JSONObject(HttpHelper.post(url, params));
		JSONArray jsonArray = jsonAll.getJSONArray("zzlist");
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("zhangfrom", jsonObject.getString("zhangfrom"));
			map.put("zhangto", jsonObject.getString("zhangto"));
			map.put("zhangmoney", jsonObject.getString("zhangmoney"));
			map.put("zhangdate", jsonObject.getString("zhangdate"));
			map.put("zhangnote", jsonObject.getString("zhangnote"));
			map.put("zhanglive", jsonObject.getString("zhanglive"));
			map.put("zzid", jsonObject.getString("zzid"));
			list.add(map);
		}
		
		return list;
	}

	//取同步网络钱包
	public List<Map<String, String>> syncGetCardWeb() throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String url = WEBURL +  "/AALifeWeb/SyncGetCardWeb.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		params.add(new BasicNameValuePair("isupdate", "1"));

		JSONObject jsonAll = new JSONObject(HttpHelper.post(url, params));
		JSONArray jsonArray = jsonAll.getJSONArray("cardlist");
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("cardid", jsonObject.getString("cardid"));
			map.put("cardname", jsonObject.getString("cardname"));
			map.put("cardmoney", jsonObject.getString("cardmoney"));
			map.put("cardlive", jsonObject.getString("cardlive"));
			list.add(map);
		}
		
		return list;
	}

	//同步网络类别
	public void syncCategoryWeb(List<Map<String, String>> list) throws Exception {
		CategoryTableAccess categoryAccess = new CategoryTableAccess(this.sqlHelper.getReadableDatabase());
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = (Map<String, String>) it.next();
			int catId = Integer.parseInt(map.get("catid"));
			String catName = map.get("catname");
			double catPrice = Double.parseDouble(map.get("catprice"));
			int catLive = Integer.parseInt(map.get("catlive"));
			
			categoryAccess.saveWebCategory(catId, catName, catPrice, catLive);
		}
		categoryAccess.close();
	}

	//同步网络专题
	public void syncZhuanTiWeb(List<Map<String, String>> list) throws Exception {
		ZhuanTiTableAccess zhuanTiAccess = new ZhuanTiTableAccess(this.sqlHelper.getReadableDatabase());
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = (Map<String, String>) it.next();
			int ztId = Integer.parseInt(map.get("ztid"));
			String ztName = map.get("ztname");
			String ztImage = map.get("ztimage");
			int ztLive = Integer.parseInt(map.get("ztlive"));
			
			zhuanTiAccess.saveWebZhuanTi(ztId, ztName, ztImage, ztLive);
		}
		zhuanTiAccess.close();
	}

	//同步网络转账
	public void syncZhuanZhangWeb(List<Map<String, String>> list) throws Exception {
		ZhuanZhangTableAccess zhangAccess = new ZhuanZhangTableAccess(this.sqlHelper.getReadableDatabase());
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = (Map<String, String>) it.next();
			int zzId = Integer.parseInt(map.get("zzid"));
			String zhangFrom = map.get("zhangfrom");
			String zhangTo = map.get("zhangto");
			String zhangMoney = map.get("zhangmoney");
			String zhangDate = map.get("zhangdate");
			String zhangNote = map.get("zhangnote");
			int zhangLive = Integer.parseInt(map.get("zhanglive"));
			
			zhangAccess.saveWebZhuanZhang(zzId, zhangFrom, zhangTo, zhangMoney, zhangDate, zhangNote, zhangLive);
		}
		zhangAccess.close();
	}

	//同步网络钱包
	public void syncCardWeb(List<Map<String, String>> list) throws Exception {
		CardTableAccess cardAccess = new CardTableAccess(this.sqlHelper.getReadableDatabase());
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = (Map<String, String>) it.next();
			int cardId = Integer.parseInt(map.get("cardid"));
			String cardName = map.get("cardname");
			double cardMoney = Double.parseDouble(map.get("cardmoney"));
			int cardLive = Integer.parseInt(map.get("cardlive"));
			
			cardAccess.saveWebCard(cardId, cardName, cardMoney, cardLive);
		}
		cardAccess.close();
	}

	//同步网络类别返回
	public void syncCategoryWebBack() throws Exception {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncCategoryWebBack.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		
		JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
		if(jsonObject.length() > 0) {
			result = jsonObject.getString("result");
		}
		
		if(!result.equals("ok")) {
			throw new Exception();
		}
	}

	//同步网络专题返回
	public void syncZhuanTiWebBack() throws Exception {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncZhuanTiWebBack.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		
		JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
		if(jsonObject.length() > 0) {
			result = jsonObject.getString("result");
		}
		
		if(!result.equals("ok")) {
			throw new Exception();
		}
	}

	//同步网络专题返回
	public void syncZhuanZhangWebBack() throws Exception {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncZhuanZhangWebBack.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		
		JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
		if(jsonObject.length() > 0) {
			result = jsonObject.getString("result");
		}
		
		if(!result.equals("ok")) {
			throw new Exception();
		}
	}

	//同步网络钱包返回
	public void syncCardWebBack() throws Exception {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncCardWebBack.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		
		JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
		if(jsonObject.length() > 0) {
			result = jsonObject.getString("result");
		}
		
		if(!result.equals("ok")) {
			throw new Exception();
		}
	}

	//取同步网络删除
	public List<Map<String, String>> syncGetDeleteListWeb() throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String url = WEBURL +  "/AALifeWeb/SyncGetDeleteListWeb.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		
		JSONObject jsonAll = new JSONObject(HttpHelper.post(url, params));
		JSONArray jsonArray = jsonAll.getJSONArray("deletelist");
		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("itemid", jsonObject.getString("itemid"));
			map.put("itemappid", jsonObject.getString("itemappid"));
			list.add(map);
		}
		
		return list;
	}
	
	//同步网络删除
	public void syncDeleteListWeb(List<Map<String, String>> list) throws Exception {
		ItemTableAccess itemAccess = new ItemTableAccess(this.sqlHelper.getReadableDatabase());
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, String> map = (Map<String, String>) it.next();
			int itemId = Integer.parseInt(map.get("itemid"));
			int itemAppId = Integer.parseInt(map.get("itemappid"));
			
			itemAccess.delWebItem(itemId, itemAppId);
		}
		itemAccess.close();
	}

	//同步网络删除返回
	public void syncDeleteListWebBack() throws Exception {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncDeleteListWebBack.aspx";
		String userId = String.valueOf(sharedHelper.getUserId());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", userId));
		
		JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
		if(jsonObject.length() > 0) {
			result = jsonObject.getString("result");
		}
		
		if(!result.equals("ok")) {
			throw new Exception();
		}
	}

	//检查同步网络消费
	public static int checkSyncWeb(int userId) {
		String result = "";
		String url = WEBURL +  "/AALifeWeb/SyncCheckWebData.aspx";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", String.valueOf(userId)));
		
		try {
			JSONObject jsonObject = new JSONObject(HttpHelper.post(url, params));
			if(jsonObject.length() > 0) {
				result = jsonObject.getString("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result.equals("ok") ? 1 : result.equals("error") ? 0 : 2;
	}

}
