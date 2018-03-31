package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ZhuanTiTableAccess {
	private static final String ITEMTABNAME = "ItemTable";
	private static final String ZTTABNAME = "ZhuanTiTable";
	private SQLiteDatabase db = null;

	public ZhuanTiTableAccess(SQLiteDatabase db) {
		this.db = db;
	}

	//保存专题
	public boolean saveZhuanTi(int saveId, String ztName, String ztImage) {
		if(findZhuanTiId(ztName) > 0) {
			return false;
		}
		
		String sql = "";
		try {
			if(saveId == 0) {
				int ztId = getMaxZhuanTiId();
				sql = "INSERT INTO " + ZTTABNAME + "(ZTID, ZhuanTiName, ZhuanTiImage, Synchronize, ZhuanTiLive) "
				   	+ "VALUES ('" + ztId + "', '" + UtilityHelper.replaceLine(ztName) + "', '" + ztImage + "', '1', '1')";
			} else {
				sql = "UPDATE " + ZTTABNAME + " SET ZhuanTiName = '" + UtilityHelper.replaceLine(ztName) + "', Synchronize = '1', ZhuanTiLive = '1' WHERE ZTID = " + saveId;
			}
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	//查所有专题下拉
	public List<CharSequence> findAllZhuanTi() {
		List<CharSequence> list = new ArrayList<CharSequence>();
		list.add("请选择");
		
		String sql = "SELECT ZhuanTiName FROM " + ZTTABNAME + " WHERE ZhuanTiLive = '1' ORDER BY ZTID ASC";
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

	//查所有专题列表
	public List<Map<String, String>> findAllZhuanTiList() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT zt.ZTID, zt.ZhuanTiName, zt.ZhuanTiImage, IFNULL(t1.ShouRu,0), IFNULL(t2.ZhiChu,0), t3.MinDate, t3.MaxDate FROM " + ZTTABNAME + " AS zt"
				   + " LEFT JOIN (SELECT ZhuanTiID, SUM(ItemPrice) AS ShouRu FROM " + ITEMTABNAME + " WHERE ItemType IN ('sr', 'jr', 'hr') AND IFNULL(ZhuanTiID,0) > 0 GROUP BY ZhuanTiID) t1 ON zt.ZTID = t1.ZhuanTiID"
				   + " LEFT JOIN (SELECT ZhuanTiID, SUM(ItemPrice) AS ZhiChu FROM " + ITEMTABNAME + " WHERE IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc') AND IFNULL(ZhuanTiID,0) > 0 GROUP BY ZhuanTiID) t2 ON zt.ZTID = t2.ZhuanTiID"
				   + " LEFT JOIN (SELECT ZhuanTiID, MIN(ItemBuyDate) AS MinDate, MAX(ItemBuyDate) AS MaxDate FROM " + ITEMTABNAME + " WHERE IFNULL(ZhuanTiID,0) > 0 GROUP BY ZhuanTiID) t3 ON zt.ZTID = t3.ZhuanTiID"
				   + " WHERE zt.ZhuanTiLive = 1 ORDER BY ZTID ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ztid", result.getString(0));
				map.put("ztname", result.getString(1));
				map.put("ztimage", result.getString(2));
				map.put("ztjiecun", "￥ " + UtilityHelper.formatDouble(result.getDouble(3) - result.getDouble(4), "0.0##"));
				if(result.getString(5) != null) {
				    map.put("ztdate", UtilityHelper.formatDate(result.getString(5), "ys-m-d") + "~" + UtilityHelper.formatDate(result.getString(6), "ys-m-d"));
				} else {
					map.put("ztdate", "~");
				}
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

	//查专题根据ID
	public Map<String, String> findZhuanTiShowById(int ztId, String curDate, String type) {
		Map<String, String> map = new HashMap<String, String>();

		String query = "";
		if(type.equals("all")) {
			query = "1=1";
		} else if (type.equals("year")) {
			query = "STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')";
		} else if (type.equals("month")) {
			query = "STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')";
		}
		
		String sql = "SELECT zt.ZTID, zt.ZhuanTiName, zt.ZhuanTiImage, IFNULL(t1.ShouRu,0), IFNULL(t2.ZhiChu,0), t3.MinDate, t3.MaxDate FROM " + ZTTABNAME + " AS zt"
				   + " LEFT JOIN (SELECT ZhuanTiID, SUM(ItemPrice) AS ShouRu FROM " + ITEMTABNAME + " WHERE ZhuanTiID = " + ztId + " AND " + query + " AND ItemType IN ('sr', 'jr', 'hr') GROUP BY ZhuanTiID) t1 ON zt.ZTID = t1.ZhuanTiID"
				   + " LEFT JOIN (SELECT ZhuanTiID, SUM(ItemPrice) AS ZhiChu FROM " + ITEMTABNAME + " WHERE ZhuanTiID = " + ztId + " AND " + query + " AND IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc') GROUP BY ZhuanTiID) t2 ON zt.ZTID = t2.ZhuanTiID"
				   + " LEFT JOIN (SELECT ZhuanTiID, MIN(ItemBuyDate) AS MinDate, MAX(ItemBuyDate) AS MaxDate FROM " + ITEMTABNAME + " WHERE ZhuanTiID = " + ztId + " AND " + query + " GROUP BY ZhuanTiID) t3 ON zt.ZTID = t3.ZhuanTiID"
				   + " WHERE zt.ZTID = " + ztId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToNext()) {
				map.put("ztid", result.getString(0));
				map.put("ztname", result.getString(1));
				map.put("ztimage", result.getString(2));
				map.put("ztshouru", "收 ￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.0##"));
				map.put("ztzhichu", "支 ￥ " + UtilityHelper.formatDouble(result.getDouble(4), "0.0##"));
				map.put("ztjiecun", "存 ￥ " + UtilityHelper.formatDouble(result.getDouble(3) - result.getDouble(4), "0.0##"));
				map.put("ztdate", UtilityHelper.formatDate(result.getString(5), "ys-m-d") + "~" + UtilityHelper.formatDate(result.getString(6), "ys-m-d"));
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
	
	//查找最大专题ID
	public int getMaxZhuanTiId() {
		int ztId = 0;
		
		String sql = "SELECT IFNULL(MAX(ZTID), 0) + 1 FROM " + ZTTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				ztId = result.getInt(0);
				ztId = ((ztId+1) % 2 == 0 ? ztId+1 : ztId+2);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return ztId;
	}
	
	//查找专题ID
	public int findZhuanTiId(int zhuanTiId) {
		int ztId = 0;
		
		String sql = "SELECT ZTID FROM " + ZTTABNAME + " WHERE ZTID = " + zhuanTiId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				ztId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return ztId;
	}

	//查找专题ID
	public int findZhuanTiId(String ztName) {
		int ztId = 0;
		
		String sql = "SELECT ZTID FROM " + ZTTABNAME + " WHERE ZhuanTiName = '" + ztName + "' AND ZhuanTiLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				ztId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return ztId;
	}
	
	//查找专题名称
	public String findZhuanTiName(int ztId) {
		String ztName = "";
		
		String sql = "SELECT ZhuanTiName FROM " + ZTTABNAME + " WHERE ZTID = " + ztId + " AND ZhuanTiLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				ztName = result.getString(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return ztName;
	}

	//删除专题
	public void updateZhuanTi(int ztId) {
		String sql = "UPDATE " + ZTTABNAME + " SET Synchronize = '1', ZhuanTiLive = '0' WHERE ZTID = " + ztId;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//查所有同步专题
	public List<Map<String, String>> findAllSyncZhuanTi() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT ZTID, ZhuanTiName, ZhuanTiImage, ZhuanTiLive FROM " + ZTTABNAME + " WHERE Synchronize = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("ztid", result.getString(0));
				map.put("ztname", result.getString(1));
				map.put("ztimage", result.getString(2));
				map.put("ztlive", result.getString(3));
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
		String sql = "UPDATE " + ZTTABNAME + " SET Synchronize = '0' WHERE ZTID = " + id;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//保存网络专题
	public void saveWebZhuanTi(int ztId, String ztName, String ztImage, int ztLive) throws Exception {
		boolean hasZhuanTi = findZhuanTiId(ztId) > 0;
		
		String sql = "";
		if (hasZhuanTi) {
			sql = "UPDATE " + ZTTABNAME + " SET ZhuanTiName = '" + ztName + "', ZhuanTiImage = '" + ztImage + "', ZhuanTiLive = '" + ztLive + "', Synchronize = '0' WHERE ZTID = " + ztId;
		} else {
			sql = "INSERT INTO " + ZTTABNAME + "(ZTID, ZhuanTiName, ZhuanTiImage, ZhuanTiLive, Synchronize) "
			   	+ "VALUES ('" + ztId + "', '" + ztName + "', '" + ztImage + "', '" + ztLive + "', '0')";
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
