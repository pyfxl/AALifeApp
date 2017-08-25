package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CardTableAccess {
	private static final String ITEMTABNAME = "ItemTable";
	private static final String CARDTABNAME = "CardTable";
	private SQLiteDatabase db = null;
	private SharedHelper sharedHelper = null;
	private ItemTableAccess itemAccess = null;

	public CardTableAccess(SQLiteDatabase db) {
		this.db = db;
	}

	public CardTableAccess(SQLiteDatabase db, Context context) {
		this.db = db;
		this.sharedHelper = new SharedHelper(context);
	}
	
	//查所有下拉
	public List<CharSequence> findAllCard() {
		List<CharSequence> list = new ArrayList<CharSequence>();
		list.add("我的钱包");
		
		String sql = "SELECT CardName FROM " + CARDTABNAME + " WHERE CardLive = '1' ORDER BY CDID ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				list.add(result.getString(0));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return list;
	}

	//查所有列表
	public List<Map<String, String>> findAllCardList() {	
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();		
		list.add(getUserMoneyFull());
		
		String sql = "SELECT CDID, CardName, CardMoney FROM " + CARDTABNAME + " WHERE CardLive = '1' ORDER BY CDID ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				double cardMoney = getCardMoney(result.getInt(0), result.getDouble(2));
				map.put("detail", "查看");
				map.put("cardid", result.getString(0));
				map.put("cardname", result.getString(1));
				map.put("cardmoney", "￥ " + UtilityHelper.formatDouble(cardMoney, "0.0##"));
				map.put("cardmoneyvalue", UtilityHelper.formatDouble(cardMoney, "0.###"));
				map.put("cardmoneystart", UtilityHelper.formatDouble(result.getDouble(2), "0.###"));
				map.put("delete", "删除");
				list.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return list;
	}

	//取完整用户钱包
	private Map<String, String> getUserMoneyFull() {
		Map<String, String> map = new HashMap<String, String>();
		double startMoney = Double.parseDouble(sharedHelper.getUserMoney());
		double cardMoney = getCardMoney(0, startMoney);
		map.put("detail", "查看");
		map.put("cardid", "0");
		map.put("cardname", "我的钱包");
		map.put("cardmoney", "￥ " + UtilityHelper.formatDouble(cardMoney, "0.0##"));
		map.put("cardmoneyvalue", UtilityHelper.formatDouble(cardMoney, "0.###"));
		map.put("cardmoneystart", UtilityHelper.formatDouble(startMoney, "0.###"));
		map.put("delete", "删除");
		
		return map;
	}
	
	//查所有列表
	public List<Map<String, String>> findAllCardListMap() {		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();		
		list.add(getUserMoney());
		
		String sql = "SELECT CDID, CardName, CardMoney FROM " + CARDTABNAME + " WHERE CardLive = '1' ORDER BY CDID ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("cardid", result.getString(0));
				map.put("cardname", result.getString(1));
				map.put("cardmoneyvalue", UtilityHelper.formatDouble(result.getDouble(2), "0.###"));
				//用于AddSmart
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("name", result.getString(1));
				map.put("value", result.getString(0));
				list.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return list;
	}
	
	//取用户钱包
	private Map<String, String> getUserMoney() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("cardid", "0");
		map.put("cardname", "我的钱包");
		map.put("cardmoneyvalue", UtilityHelper.formatDouble(Double.parseDouble(sharedHelper.getUserMoney()), "0.###"));
		//用于AddSmart
		map.put("id", "0");
		map.put("name", "我的钱包");
		map.put("value", "0");
		
		return map;
	}
	
	//取余额
	private double getCardMoney(int cardId, double cardMoney) {
		itemAccess = new ItemTableAccess(this.db);
		Map<String, String> map = itemAccess.findAllShouZhi(cardId);
		double shouPrice = Double.parseDouble(map.get("shouprice"));
		double zhiPrice = Double.parseDouble(map.get("zhiprice"));
		
		return cardMoney + shouPrice - zhiPrice;
	}
	
	//保存
	public boolean saveCard(int saveId, String cardName, String cardMoney) {
		if(findCardId(cardName, cardMoney) > 0) {
			return false;
		}
		
		String sql = "";
		Cursor result = null;
		try {
			if(saveId == -1) {
				int cdId = getMaxCardId();
				sql = "INSERT INTO " + CARDTABNAME + "(CDID, CardName, CardMoney, Synchronize, CardLive) "
				   	+ "VALUES ('" + cdId + "', '" + cardName + "', '" + cardMoney + "', '1', '1')";
			} else if(saveId == 0) {
				sharedHelper.setUserMoney(cardMoney);
			} else {
				sql = "UPDATE " + CARDTABNAME + " SET CardName = '" + cardName + "', CardMoney = '" + cardMoney + "', Synchronize = '1', CardLive = '1' WHERE CDID = " + saveId;
			}
			if(!sql.equals("")) {
		        this.db.execSQL(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return true;
	}

	//保存
	public int saveCard(String cardName, String cardMoney) {
		if(findCardId(cardName, cardMoney) > 0) {
			return 0;
		}
		
		int cdId = getMaxCardId();
		try {
			String sql = "INSERT INTO " + CARDTABNAME + "(CDID, CardName, CardMoney, Synchronize, CardLive) "
			   	+ "VALUES ('" + cdId + "', '" + cardName + "', '" + cardMoney + "', '1', '1')";
	        this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		return cdId;
	}

	//根据名称查找余额
	public double findCardMoney(String cardName) {
		if(cardName.equals("我的钱包")) {
			Map<String, String> map = getUserMoneyFull();
			return Double.parseDouble(map.get("cardmoneyvalue"));
		}
		
		double cardMoney = 0;		
		String sql = "SELECT CDID, CardName, CardMoney FROM " + CARDTABNAME + " WHERE CardName = '" + cardName + "' AND CardLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				cardMoney = getCardMoney(result.getInt(0), result.getDouble(2));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return cardMoney;
	}

	//根据ID查找余额
	public double findCardMoney(int cardId) {		
		if(cardId == 0) {
			Map<String, String> map = getUserMoney();
			return Double.parseDouble(map.get("cardmoneyvalue"));
		}
		
		double cardMoney = 0;		
		String sql = "SELECT CardMoney FROM " + CARDTABNAME + " WHERE CDID = " + cardId + " AND CardLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				cardMoney = result.getDouble(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return cardMoney;
	}

	//查钱包根据ID
	public Map<String, String> findCardById(int cdId, String curDate, String type) {
		Map<String, String> map = new HashMap<String, String>();

		String query = "";
		if(type.equals("all")) {
			query = "1=1";
		} else if (type.equals("year")) {
			query = "STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')";
		} else if (type.equals("month")) {
			query = "STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')";
		}
		
		String sql = "SELECT cd.CDID, cd.CardName, cd.CardImage, IFNULL(t1.ShouRu,0), IFNULL(t2.ZhiChu,0) FROM (SELECT CDID, CardName, CardImage FROM " + CARDTABNAME + " UNION ALL SELECT 0, '我的钱包', '') AS cd"
				   + " LEFT JOIN (SELECT CardID, SUM(ItemPrice) AS ShouRu FROM " + ITEMTABNAME + " WHERE (CASE IFNULL(CardID,'') WHEN '' THEN 0 ELSE CardID END) = " + cdId + " AND " + query + " AND ItemType IN ('sr', 'jr', 'hr') GROUP BY CardID) t1 ON cd.CDID = (CASE IFNULL(t1.CardID,'') WHEN '' THEN 0 ELSE t1.CardID END)"
				   + " LEFT JOIN (SELECT CardID, SUM(ItemPrice) AS ZhiChu FROM " + ITEMTABNAME + " WHERE (CASE IFNULL(CardID,'') WHEN '' THEN 0 ELSE CardID END) = " + cdId + " AND " + query + " AND IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc') GROUP BY CardID) t2 ON cd.CDID = (CASE IFNULL(t2.CardID,'') WHEN '' THEN 0 ELSE t2.CardID END)"
				   + " WHERE cd.CDID = " + cdId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToNext()) {
				map.put("cdid", result.getString(0));
				map.put("cdname", result.getString(1));
				map.put("cdimage", result.getString(2));
				map.put("cdshouru", "收 ￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.0##"));
				map.put("cdzhichu", "支 ￥ " + UtilityHelper.formatDouble(result.getDouble(4), "0.0##"));
				map.put("cdjiecun", "存 ￥ " + UtilityHelper.formatDouble(result.getDouble(3) - result.getDouble(4), "0.0##"));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return map;
	}
	
	//查找最大钱包ID
	public int getMaxCardId() {
		int cdId = 0;
		
		String sql = "SELECT IFNULL(MAX(CDID), 0) + 1 FROM " + CARDTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				cdId = result.getInt(0);
				cdId = ((cdId+1) % 2 == 0 ? cdId+1 : cdId+2);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return cdId;
	}
	
	//查找CardID
	public int findCardId(String cardName, String cardMoney) {
		int cardId = 0;
		
		String sql = "SELECT CDID FROM " + CARDTABNAME + " WHERE CardName = '" + cardName + "' AND CardMoney = '" + cardMoney + "' AND CardLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				cardId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return cardId;
	}

	//查找CardID
	public int findCardId(String cardName) {
		int cardId = 0;
		
		String sql = "SELECT CDID FROM " + CARDTABNAME + " WHERE CardName = '" + cardName + "' AND CardLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				cardId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return cardId;
	}

	//查找CardID
	public int findCardId(int cdId) {
		int cardId = 0;
		
		String sql = "SELECT CDID FROM " + CARDTABNAME + " WHERE CDID = " + cdId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				cardId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return cardId;
	}

	//查找CardName
	public String findCardName(int cardId) {
		String cardName = "";
		
		String sql = "SELECT CardName FROM " + CARDTABNAME + " WHERE CDID = " + cardId + " AND CardLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				cardName = result.getString(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return cardName;
	}

	//检查ID是否在使用
	public boolean findItemByCardId(int cardId) {
		boolean bool = false; 
		
		String sql = "SELECT CardID FROM " + ITEMTABNAME + " WHERE CardID = " + cardId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				bool = true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return bool;
	}

	//删除
	public int deleteCard(int cardId) {
		boolean hasItem = findItemByCardId(cardId);
		if (hasItem) {
			return 2;
		}
		
		if (cardId == 0) {
			return 3;
		}
		
		try {			
			String sql = "UPDATE " + CARDTABNAME + " SET CardLive = '0', Synchronize = '1' WHERE CDID = " + cardId;
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		return 1;
	}

	//查所有同步钱包
	public List<Map<String, String>> findAllSyncCard() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT CDID, CardName, CardMoney, CardLive FROM " + CARDTABNAME + " WHERE Synchronize = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("cardid", result.getString(0));
				map.put("cardname", result.getString(1));
				map.put("cardmoney", result.getString(2));
				map.put("cardlive", result.getString(3));
				list.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return list;
	}

	//更改同步状态
	public void updateSyncStatus(int id) {
		String sql = "UPDATE " + CARDTABNAME + " SET Synchronize = '0' WHERE CDID = " + id;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//保存网络钱包
	public void saveWebCard(int cardId, String cardName, double cardMoney, int cardLive) throws Exception {
		boolean hasCard = findCardId(cardId) > 0;
		
		String sql = "";
		if (hasCard) {
			sql = "UPDATE " + CARDTABNAME + " SET CardName = '" + cardName + "', CardMoney = '" + cardMoney + "', CardLive = '" + cardLive + "', Synchronize = '0' WHERE CDID = " + cardId;
		} else {
			sql = "INSERT INTO " + CARDTABNAME + "(CDID, CardName, CardMoney, CardLive, Synchronize) "
			   	+ "VALUES ('" + cardId + "', '" + cardName + "', '" + cardMoney + "', '" + cardLive + "', '0')";
		}
		
		try {
	        this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//关闭数据库
	public void close() {
		this.db.close();
	}
}
