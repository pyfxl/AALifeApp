package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ZhuanZhangTableAccess {
	private static final String ZZTABNAME = "ZhuanZhangTable";
	private static final String CARDTABNAME = "CardTable";
	private SQLiteDatabase db = null;

	public ZhuanZhangTableAccess(SQLiteDatabase db) {
		this.db = db;
	}

	//保存转账
	public void addZhuanZhang(int fromId, int toId, String zzMoney, String zzDate, String zzNote) {
		int zzId = getMaxZhuanZhangId();
		String sql = " INSERT INTO " + ZZTABNAME + "(ZZID, ZhangFrom, ZhangTo, ZhangMoney, ZhangDate, ZhangNote, Synchronize, ZhangLive)"
			   	   + " VALUES ('" + zzId + "', '" + fromId + "', '" + toId + "', '" + zzMoney + "', '" + zzDate + "', '" + zzNote + "', '1', '1')";
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//删除转账
	public void updateZhuanZhang(int zzId) {
		String sql = "UPDATE " + ZZTABNAME + " SET Synchronize = '1', ZhangLive = '0' WHERE ZZID = " + zzId;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//查所有转账列表
	public List<Map<String, String>> findZhuanZhangDetail(String curDate, String type) {	
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();	

		String query = "";
		if(type.equals("all")) {
			query = "1=1";
		} else if (type.equals("year")) {
			query = "STRFTIME('%Y',ZhangDate)=STRFTIME('%Y','" + curDate + "')";
		} else if (type.equals("month")) {
			query = "STRFTIME('%Y-%m',ZhangDate)=STRFTIME('%Y-%m','" + curDate + "')";
		}
		
		String sql = " SELECT CASE a.ZhangFrom WHEN 0 THEN '我的钱包' ELSE b.CardName END AS ZhangFrom, "
				   + " CASE a.ZhangTo WHEN 0 THEN '我的钱包' ELSE c.CardName END AS ZhangTo, ZhangMoney, ZhangDate, ZZID, ZhangNote FROM " + ZZTABNAME + " a "
				   + " LEFT JOIN " + CARDTABNAME + " b ON a.ZhangFrom = b.CDID "
				   + " LEFT JOIN " + CARDTABNAME + " c ON a.ZhangTo = c.CDID "
				   + " WHERE ZhangLive = '1' AND " + query + " ORDER BY a.ZhangDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhangfrom", result.getString(0));
				map.put("zhangto", result.getString(1));
				map.put("zhangmoney", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("zhangmoneyvalue", result.getString(2));
				map.put("zhangdate", UtilityHelper.formatDate(result.getString(3), "ys-m-d"));
				map.put("zzid", result.getString(4));
				map.put("zhangnote", result.getString(5));
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

	//查找最大转账ID
	public int getMaxZhuanZhangId() {
		int zzId = 0;
		
		String sql = "SELECT IFNULL(MAX(ZZID), 0) + 1 FROM " + ZZTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				zzId = result.getInt(0);
				zzId = ((zzId+1) % 2 == 0 ? zzId+1 : zzId+2);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return zzId;
	}
	
	//查同步转账
	public List<Map<String, String>> findSyncZhuanZhang() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ZhangFrom, ZhangTo, ZhangMoney, ZhangDate, ZhangNote, ZhangLive, ZZID FROM " + ZZTABNAME 
				   + " WHERE Synchronize = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhangfrom", result.getString(0));
				map.put("zhangto", result.getString(1));
				map.put("zhangmoney", result.getString(2));
				map.put("zhangdate", result.getString(3));
				map.put("zhangnote", result.getString(4));
				map.put("zhanglive", result.getString(5));
				map.put("zzid", result.getString(6));
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
	public void updateSyncStatus(int zzId) {
		String sql = "UPDATE " + ZZTABNAME + " SET Synchronize = '0' WHERE ZZID = " + zzId;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//保存网络转账
	public void saveWebZhuanZhang(int zzId, String zhangFrom, String zhangTo, String zhangMoney, String zhangDate, String zhangNote, int zhangLive) throws Exception {
		boolean hasZhuanZhang = findZhuanZhangId(zzId) > 0;
		
		String sql = "";
		if (hasZhuanZhang) {
			sql = "UPDATE " + ZZTABNAME + " SET ZhangFrom = '" + zhangFrom + "', ZhangTo = '" + zhangTo + "', ZhangMoney = '" + zhangMoney + "', "
		        + "ZhangDate = '" + zhangDate + "', ZhangNote = '" + zhangNote + "', ZhangLive = '" + zhangLive + "', Synchronize = '0' WHERE ZZID = " + zzId;
		} else {
			sql = "INSERT INTO " + ZZTABNAME + "(ZZID, ZhangFrom, ZhangTo, ZhangMoney, ZhangDate, ZhangNote, ZhangLive, Synchronize) "
			   	+ "VALUES ('" + zzId + "', '" + zhangFrom + "', '" + zhangTo + "', '" + zhangMoney + "', '" + zhangDate + "', '" + zhangNote + "', '" + zhangLive + "', '0')";
		}
		
		try {
	        this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//查找转账ID
	public int findZhuanZhangId(int zhangId) {
		int zzId = 0;
		
		String sql = "SELECT ZZID FROM " + ZZTABNAME + " WHERE ZZID = " + zhangId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				zzId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return zzId;
	}
	
	//关闭数据库
	public void close() {
		this.db.close();
	}
	
}
