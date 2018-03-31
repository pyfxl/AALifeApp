package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemTableAccess {
	private static final String ITEMTABNAME = "ItemTable";
	private static final String DELTABNAME = "DeleteTable";
	private static final String CATTABNAME = "CategoryTable";
	private static final String ZTTABNAME = "ZhuanTiTable";
	private static final String ZZTABNAME = "ZhuanZhangTable";
	private static final String CARDTABNAME = "CardTable";
	private SQLiteDatabase db = null;

	public ItemTableAccess(SQLiteDatabase db) {
		this.db = db;
	}
	
	//查所有消费数量
	public int findAllItemCount() {
		int count = 0;
		
		String sql = "SELECT Count(0) FROM " + ITEMTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				count = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return count;
	}
		
	//查消费根据ID
	public Map<String, String> findItemById(int itemId) {
		Map<String, String> map = new HashMap<String, String>();
		
		String sql = " SELECT ItemID, ItemName, ItemPrice, ItemBuyDate, CategoryID, ItemWebID, Recommend, RegionID, RegionType, IFNULL(ItemType,'zc'), ZhuanTiID, IFNULL(CardID,'0') FROM " + ITEMTABNAME 
				   + " WHERE itemId = " + itemId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToNext()) {
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("itemid", result.getString(0));
				map.put("itemname", result.getString(1));
				map.put("itemprice", UtilityHelper.formatDouble(result.getDouble(2), "0.###"));
				map.put("itembuydate", result.getString(3));
				map.put("catid", result.getString(4));
				map.put("itemwebid", result.getString(5));
				map.put("recommend", result.getString(6));
				map.put("regionid", result.getString(7));
				map.put("regiontype", result.getString(8));
				map.put("itemtype", result.getString(9));
				map.put("ztid", result.getString(10).equals("") ? "0" : result.getString(10));
				map.put("cardid", result.getString(11));
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
		
	//查所有名称
	public List<CharSequence> findAllItemName() {
		List<CharSequence> all = new ArrayList<CharSequence>();
		
		String sql = "SELECT ItemName FROM " + ITEMTABNAME + " GROUP BY ItemName ORDER BY ItemName ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				all.add(result.getString(0));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return all;
	}
		
	//根据类别ID查所有消费名称--AddSmart
	public List<Map<String, String>> findAllItemByCatId(String catId) {
		List<Map<String, String>> all = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT ItemName, COUNT(0) AS Count FROM " + ITEMTABNAME + " WHERE CategoryID = " + catId + " GROUP BY ItemName ORDER BY Count DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("name", result.getString(0));
				all.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return all;
	}

	//根据名称查所有消费单价--AddSmart
	public List<Map<String, String>> findAllPriceByName(String name) {
		List<Map<String, String>> all = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT ItemPrice, COUNT(0) AS Count FROM " + ITEMTABNAME + " WHERE ItemName LIKE '%" + name + "%' GROUP BY ItemPrice ORDER BY Count DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);		
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("name", UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("value", result.getString(0));
				all.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return all;
	}
	
	//根据日期查消费
	public List<Map<String, String>> findItemByDate(String date, int type) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemID, ItemName, ItemPrice, ItemBuyDate, CategoryID, Recommend, RegionID, CASE WHEN RegionID<>0 AND IFNULL(RegionType,'')='' THEN 'm' ELSE IFNULL(RegionType,'') END, IFNULL(ItemType,'zc'), IFNULL(ZhuanTiID,0) FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) = '" + date + "' ORDER BY ItemID ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				//map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("itemid", result.getString(0));
				map.put("itemname", result.getString(1));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("zhiprice", getItemPrice("zc", result.getString(8), UtilityHelper.formatDouble(result.getDouble(2), "0.0##")));
				map.put("shouprice", getItemPrice("sr", result.getString(8), UtilityHelper.formatDouble(result.getDouble(2), "0.0##")));
				map.put("pricevalue", UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itembuydate", result.getString(3));
				map.put("catid", result.getString(4));
				map.put("recommend", result.getString(5));
				map.put("regionid", result.getString(6));
				map.put("regiontype", result.getString(7));
				map.put("regionname", UtilityHelper.getRegionName(result.getString(7), 0));
				map.put("itemtype", getItemTypeName(result.getString(8), type));
				map.put("itemtypevalue", result.getString(8));
				map.put("ztid", result.getString(9).equals("") ? "0" : result.getString(9));
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

	//根据日期导出消费
	public List<CharSequence> exportDataByDate(String beginDate, String endDate) {
		List<CharSequence> list = new ArrayList<CharSequence>();
		
		String sql = " SELECT IFNULL(ItemType,'zc'), CategoryName, ItemName, ItemPrice, ItemBuyDate, IFNULL(Recommend, 0), IFNULL(zt.ZhuanTiName, ''), IFNULL(cd.CardName, '我的钱包') FROM " + ITEMTABNAME + " it"
				    + " INNER JOIN " + CATTABNAME + " ct ON it.CategoryID = ct.CategoryID AND ct.CategoryLive = 1"
				    + " LEFT JOIN " + ZTTABNAME + " zt ON it.ZhuanTiID = zt.ZTID AND zt.ZhuanTiLive = 1"
				    + " LEFT JOIN " + CARDTABNAME + " cd ON it.CardID = cd.CDID AND cd.CardLive = 1"
				    + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) BETWEEN '" + beginDate + "' AND '" + endDate + "' ORDER BY ItemBuyDate, ItemID ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			//标题
			list.add(getExportString(1, "编号", "") +
					getExportString(2, "分类", "") +
					getExportString(3, "商品类别", "") +
					getExportString(4, "商品名称", "") +
					getExportString(5, "支出", "") +
					getExportString(6, "收入", "") +
					getExportString(7, "日期", "") +
					getExportString(8, "推荐否", "") +
					getExportString(9, "专题", "") +
					getExportString(10, "钱包", ""));
			//分割线
			list.add(getExportString(1, "-", "-") +
					getExportString(2, "-", "-") +
					getExportString(3, "-", "-") +
					getExportString(4, "-", "-") +
					getExportString(5, "-", "-") +
					getExportString(6, "-", "-") +
					getExportString(7, "-", "-") +
					getExportString(8, "-", "-") +
					getExportString(9, "-", "-") +
					getExportString(10, "-", "-"));
			
			String tempDate = "";
			double shouRuTotal = 0d;
			double zhiChuTotal = 0d;
			while (result.moveToNext()) {
				String curDate = UtilityHelper.formatDate(result.getString(4), "y-m-d");
				if(!curDate.equals(tempDate)) {
					tempDate = curDate;
					list.add("");
				}
				String itemType = result.getString(0);
				if(itemType.equals("zc") || itemType.equals("hc") || itemType.equals("jc")) {
					zhiChuTotal += result.getDouble(3);
				}else{
					shouRuTotal += result.getDouble(3);
				}
				list.add(getExportString(1, String.valueOf(result.getPosition() + 1), "") +
						getExportString(2, getItemTypeName(result.getString(0), 1), "") +
						getExportString(3, result.getString(1), "") +
						getExportString(4, result.getString(2), "") +
						getExportString(5, getItemPrice2("zc", result.getString(0), UtilityHelper.formatDouble(result.getDouble(3), "0.0##")), "") +
						getExportString(6, getItemPrice2("sr", result.getString(0), UtilityHelper.formatDouble(result.getDouble(3), "0.0##")), "") +
						getExportString(7, UtilityHelper.formatDate(result.getString(4), "y-m-d"), "") +
						getExportString(8, result.getString(5).equals("1") ? "是" : "", "") +
						getExportString(9, result.getString(6), "") +
						getExportString(10, result.getString(7), ""));
			}
			
			//分割线
			list.add("");
			list.add(getExportString(1, "-", "-") +
					getExportString(2, "-", "-") +
					getExportString(3, "-", "-") +
					getExportString(4, "-", "-") +
					getExportString(5, "-", "-") +
					getExportString(6, "-", "-") +
					getExportString(7, "-", "-") +
					getExportString(8, "-", "-") +
					getExportString(9, "-", "-") +
					getExportString(10, "-", "-"));
			//合计
			list.add(getExportString(1, "总计", "") +
					getExportString(2, "", "") +
					getExportString(3, "", "") +
					getExportString(4, "", "") +
					getExportString(5, "支出", "") +
					getExportString(6, "收入", "") +
					getExportString(7, "结存", "") +
					getExportString(8, "", "") +
					getExportString(9, "", "") +
					getExportString(10, "", ""));
			list.add(getExportString(1, "", "") +
					getExportString(2, "", "") +
					getExportString(3, "", "") +
					getExportString(4, "", "") +
					getExportString(5, UtilityHelper.formatDouble(zhiChuTotal, "0.0##"), "") +
					getExportString(6, UtilityHelper.formatDouble(shouRuTotal, "0.0##"), "") +
					getExportString(7, UtilityHelper.formatDouble(shouRuTotal-zhiChuTotal, "0.0##"), "") +
					getExportString(8, "", "") +
					getExportString(9, "", "") +
					getExportString(10, "", ""));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
			
		return list;
	}
	
	//根据日期查月消费
	public List<Map<String, String>> findMonthByDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(ZhiChuPrice), SUM(ShouRuPrice), SUM(JieHuanPrice), SUM(HuanJiePrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiChuPrice, 0 AS ShouRuPrice, 0 AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND (IFNULL(ItemType,'zc')='zc' OR ItemType='jc' OR ItemType='hc')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, SUM(ItemPrice) AS ShouRuPrice, 0 AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND (ItemType='sr' OR ItemType='jr' OR ItemType='hr')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, 0 AS ShouRuPrice, SUM(ItemPrice) AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND (ItemType='jc' OR ItemType='hr')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, 0 AS ShouRuPrice, 0 AS JieHuanPrice, SUM(ItemPrice) AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND (ItemType='jr' OR ItemType='hc')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhichuprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shouruprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("jiehuanprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.##"));
				map.put("huanjieprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.##"));
				map.put("date", UtilityHelper.formatDate(result.getString(4), "m-d-w"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(4), "y-m-d"));
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

	//查所有月消费
	public List<Map<String, String>> findAllMonthTitle() {		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(ZhiChuPrice), SUM(ShouRuPrice), SUM(JieHuanPrice), SUM(HuanJiePrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiChuPrice, 0 AS ShouRuPrice, 0 AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE (IFNULL(ItemType,'zc')='zc' OR ItemType='jc' OR ItemType='hc')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, SUM(ItemPrice) AS ShouRuPrice, 0 AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE (ItemType='sr' OR ItemType='jr' OR ItemType='hr')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, 0 AS ShouRuPrice, SUM(ItemPrice) AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE (ItemType='jc' OR ItemType='hr')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, 0 AS ShouRuPrice, 0 AS JieHuanPrice, SUM(ItemPrice) AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE (ItemType='jr' OR ItemType='hc')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhichuvalue", result.getString(0));
				map.put("zhichuprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shouruvalue", result.getString(1));
				map.put("shouruprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("jiehuanvalue", result.getString(2));
				map.put("jiehuanprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.##"));
				map.put("huanjievalue", result.getString(3));
				map.put("huanjieprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.##"));
				map.put("date", UtilityHelper.formatDate(result.getString(4), "y-m"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(4), "y-m-d"));
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

	//根据日期查月总计
	public Map<String, String> findHomeMonthTotal() {		
		Map<String, String> map = new HashMap<String, String>();
		
		String sql = " SELECT SUM(ZhiChuPrice), SUM(ShouRuPrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiChuPrice, 0 AS ShouRuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', datetime('now', '+8 hour')) AND (IFNULL(ItemType,'zc')='zc' OR ItemType='jc' OR ItemType='hc')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, SUM(ItemPrice) AS ShouRuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', datetime('now', '+8 hour')) AND (ItemType='sr' OR ItemType='jr' OR ItemType='hr')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToNext()) {
				map.put("zhichuvalue", result.getString(0));
				map.put("zhichuprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shouruvalue", result.getString(1));
				map.put("shouruprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));				
				map.put("date", UtilityHelper.formatDate(result.getString(2), "y-m"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(2), "y-m-d"));
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

	//查所有专题消费
	public List<Map<String, String>> findZhuanTiShowList(int ztId, String curDate, String type) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String query = "";
		if(type.equals("all")) {
			query = "1=1";
		} else if (type.equals("year")) {
			query = "STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')";
		} else if (type.equals("month")) {
			query = "STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')";
		}
		
		String sql = " SELECT ItemID, ItemName, ItemPrice, ItemBuyDate, CategoryID, Recommend, RegionID, CASE WHEN RegionID<>0 AND IFNULL(RegionType,'')='' THEN 'm' ELSE IFNULL(RegionType,'') END, IFNULL(ItemType,'zc'), IFNULL(ZhuanTiID,0) FROM " + ITEMTABNAME
				   + " WHERE ZhuanTiID = '" + ztId + "' AND " + query + " ORDER BY ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				//map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("itemid", result.getString(0));
				map.put("itemname", result.getString(1));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("pricevalue", UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(3), "y-m-d"));
				map.put("itembuydatetext", UtilityHelper.formatDate(result.getString(3), "ys-m-d"));
				map.put("catid", result.getString(4));
				map.put("recommend", result.getString(5));
				map.put("regionid", result.getString(6));
				map.put("regiontype", result.getString(7));
				map.put("regionname", UtilityHelper.getRegionName(result.getString(7), 0));
				map.put("itemtype", getItemTypeName(result.getString(8), 1));
				map.put("itemtypevalue", result.getString(8));
				map.put("ztid", result.getString(9).equals("") ? "0" : result.getString(9));
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

	//查所有钱包消费
	public List<Map<String, String>> findCardDetailList(int cdId, int num, int start, String curDate, String type) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String query = "";
		if(type.equals("all")) {
			query = "1=1";
		} else if (type.equals("year")) {
			query = "STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')";
		} else if (type.equals("month")) {
			query = "STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')";
		}
		
		String sql = " SELECT ItemID, ItemName, ItemPrice, ItemBuyDate, IFNULL(ItemType,'zc') FROM " + ITEMTABNAME
				   + " WHERE (CASE IFNULL(CardID,'') WHEN '' THEN 0 ELSE CardID END) = " + cdId + " AND " + query 
				   + " ORDER BY ItemBuyDate DESC LIMIT " + num + " OFFSET " + start;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemid", result.getString(0));
				map.put("itemname", result.getString(1));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("pricevalue", UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(3), "y-m-d"));
				map.put("itembuydatetext", UtilityHelper.formatDate(result.getString(3), "ys-m-d"));
				map.put("itemtype", getItemTypeName(result.getString(4), 1));
				map.put("itemtypevalue", result.getString(4));
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
	
	//查所有月消费
	public List<Map<String, String>> findAllMonthTitle(String curDate) {
		String fromDate = UtilityHelper.getNavDate(curDate, -3, "m");		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(ZhiChuPrice), SUM(ShouRuPrice), SUM(JieHuanPrice), SUM(HuanJiePrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiChuPrice, 0 AS ShouRuPrice, 0 AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE IFNULL(ItemType,'zc')='zc' AND STRFTIME('%Y-%m', ItemBuyDate) BETWEEN STRFTIME('%Y-%m', '" + fromDate + "') AND STRFTIME('%Y-%m', '" + curDate + "')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, SUM(ItemPrice) AS ShouRuPrice, 0 AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE ItemType='sr' AND STRFTIME('%Y-%m', ItemBuyDate) BETWEEN STRFTIME('%Y-%m', '" + fromDate + "') AND STRFTIME('%Y-%m', '" + curDate + "')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, 0 AS ShouRuPrice, SUM(ItemPrice) AS JieHuanPrice, 0 AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE (ItemType='jc' OR ItemType='hr') AND STRFTIME('%Y-%m', ItemBuyDate) BETWEEN STRFTIME('%Y-%m', '" + fromDate + "') AND STRFTIME('%Y-%m', '" + curDate + "')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, 0 AS ShouRuPrice, 0 AS JieHuanPrice, SUM(ItemPrice) AS HuanJiePrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE (ItemType='jr' OR ItemType='hc') AND STRFTIME('%Y-%m', ItemBuyDate) BETWEEN STRFTIME('%Y-%m', '" + fromDate + "') AND STRFTIME('%Y-%m', '" + curDate + "')"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhichuprice", UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shouruprice", UtilityHelper.formatDouble(result.getDouble(1), "0.###"));
				map.put("jiehuanprice", UtilityHelper.formatDouble(result.getDouble(2), "0"));
				map.put("huanjieprice", UtilityHelper.formatDouble(result.getDouble(3), "0"));
				map.put("date", UtilityHelper.formatDate(result.getString(4), "y-m"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(4), "y-m-d"));
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
	
	//查所有月消费
	public Map<String, String> findAllMonth(String curDate) {
		Map<String, String> map = new HashMap<String, String>();
		
		String sql = " SELECT SUM(ZhiChuPrice), SUM(ShouRuPrice), ItemBuyDate, SUM(ShouRuPrice)-SUM(ZhiChuPrice) FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiChuPrice, 0 AS ShouRuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc') AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + curDate + "')"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, SUM(ItemPrice) AS ShouRuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE ItemType IN ('sr', 'jr', 'hr') AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + curDate + "')"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToNext()) {
				map.put("zhichuprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("zhichupricevalue", UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shouruprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("shourupricevalue", UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("date", UtilityHelper.formatDate(result.getString(2), "y-m"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(2), "y-m-d"));
				map.put("jiecunprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.0##"));
				map.put("jiecunpricevalue", UtilityHelper.formatDouble(result.getDouble(3), "0.0##"));
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

	//查下一个有数据日期
	public String findLastDate() {
		String value = "";
		
		String sql = " SELECT ItemBuyDate FROM " + ITEMTABNAME 
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) ORDER BY ItemBuyDate DESC LIMIT 30";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToLast()) {
				value = result.getString(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
			
		return value;
	}
	
	//根据日期查所有月消费
	public List<Map<String, String>> findAllDayBuyDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(ZhiPrice), SUM(ShouPrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiPrice, 0 AS ShouPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc') AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiPrice, SUM(ItemPrice) AS ShouPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE ItemType IN ('sr', 'jr', 'hr') AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate) ORDER BY ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhipricevalue", UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shoupricevalue", UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(2), "y-m-d"));
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
	
	//根据日期查首次所有月消费
	public List<Map<String, String>> findAllDayFirstBuyDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(ZhiPrice), SUM(ShouPrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiPrice, 0 AS ShouPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc') AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) >= STRFTIME('%Y-%m', '" + date + "')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiPrice, SUM(ItemPrice) AS ShouPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE ItemType IN ('sr', 'jr', 'hr') AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) >= STRFTIME('%Y-%m', '" + date + "')"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate) ORDER BY ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhipricevalue", UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shoupricevalue", UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(2), "y-m-d"));
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
	
	//根据日期取下个有数据日期
	public String findNextDate(String date) {
		String value = "";
		
		String sql = " SELECT ItemBuyDate FROM " + ITEMTABNAME
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " HAVING STRFTIME('%Y-%m', ItemBuyDate) < STRFTIME('%Y-%m', '" + date + "') ORDER BY ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				value = UtilityHelper.formatDate(result.getString(0), "y-m-d");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
			
		return value;
	}

	//查第一个有数据日期
	public String findFirstDate() {
		String value = "";
		
		String sql = "SELECT ItemBuyDate FROM " + ITEMTABNAME + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate) ORDER BY ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				value = UtilityHelper.formatDate(result.getString(0), "y-m-d");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
			
		return value;
	}

	//查月消费数量
	public int findMonthCountByDate(String date) {
		int count = 0;
		
		String sql = "SELECT COUNT(0) FROM " + ITEMTABNAME + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "')";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				count = result.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return count;
	}
	
	//根据日期查首页统计
	public List<Map<String, String>> findHomeTotalByDate(String curDate, String type) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String query = "";
		if(type.equals("all")) {
			query = "1=1";
		} else if (type.equals("year")) {
			query = "STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')";
		} else if (type.equals("month")) {
			query = "STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')";
		} else if (type.equals("day")) {
			query = "STRFTIME('%Y-%m-%d',ItemBuyDate)=STRFTIME('%Y-%m-%d','" + curDate + "')";
		}
		
		String sql = " SELECT a1.Price, a2.Price, a1.Label, a2.Label, a1.Flag FROM ("
				   + " SELECT SUM(ItemPrice) AS Price, '收入' AS Label, 1 AS Flag FROM " + ITEMTABNAME 
				   + " WHERE ItemType='sr' AND " + query
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " UNION "
				   + " SELECT SUM(ItemPrice) AS Price, '还入' AS Label, 2 AS Flag FROM " + ITEMTABNAME 
				   + " WHERE ItemType='hr' AND " + query
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " UNION "
				   + " SELECT SUM(ItemPrice) AS Price, '借入' AS Label, 3 AS Flag FROM " + ITEMTABNAME 
				   + " WHERE ItemType='jr' AND " + query
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
		           + " a1 INNER JOIN ("
		           + " SELECT SUM(ItemPrice) AS Price, '支出' AS Label, 1 AS Flag FROM " + ITEMTABNAME 
				   + " WHERE IFNULL(ItemType,'zc')='zc' AND " + query
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " UNION "
				   + " SELECT SUM(ItemPrice) AS Price, '借出' AS Label, 2 AS Flag FROM " + ITEMTABNAME 
				   + " WHERE ItemType='jc' AND " + query
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " UNION "
				   + " SELECT SUM(ItemPrice) AS Price, '还出' AS Label, 3 AS Flag FROM " + ITEMTABNAME 
				   + " WHERE ItemType='hc' AND " + query
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
                   + " a2 ON a1.Flag=a2.Flag ORDER BY a1.Flag ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				double shouru = result.getDouble(0);
				double zhichu = result.getDouble(1);
				Map<String, String> map = new HashMap<String, String>();
				map.put("shouruprice", "￥ " + UtilityHelper.formatDouble(shouru, "0.0##"));
				map.put("shouruvalue", String.valueOf(shouru));
				map.put("zhichuprice", "￥ " + UtilityHelper.formatDouble(zhichu, "0.0##"));
				map.put("zhichuvalue", String.valueOf(zhichu));
				map.put("shourulabel", result.getString(2));
				map.put("zhichulabel", result.getString(3));
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
	
	
	//根据日期查首页统计
	public List<Map<String, String>> findHomeTotalByDate(String curDate) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = 
				" SELECT a1.Price, a2.Price, a1.Label, a2.Label, a1.Flag FROM ("
				+ " SELECT SUM(ItemPrice) AS Price, '收入' AS Label, 1 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType='sr' AND STRFTIME('%Y-%m-%d',ItemBuyDate)=STRFTIME('%Y-%m-%d','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				+ " UNION "
				+ " SELECT SUM(ItemPrice) AS Price, '月入' AS Label, 2 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType='sr' AND STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				+ " UNION "
				+ " SELECT SUM(ItemPrice) AS Price, '年入' AS Label, 3 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType='sr' AND STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
		        + " a1 INNER JOIN ("
		        + " SELECT SUM(ItemPrice) AS Price, '支出' AS Label, 1 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE IFNULL(ItemType,'zc')='zc' AND STRFTIME('%Y-%m-%d',ItemBuyDate)=STRFTIME('%Y-%m-%d','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				+ " UNION "
				+ " SELECT SUM(ItemPrice) AS Price, '月出' AS Label, 2 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE IFNULL(ItemType,'zc')='zc' AND STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				+ " UNION "
				+ " SELECT SUM(ItemPrice) AS Price, '年出' AS Label, 3 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE IFNULL(ItemType,'zc')='zc' AND STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
                + " a2 ON a1.Flag=a2.Flag"
                + " UNION " +
                " SELECT a1.Price, a2.Price, a1.Label, a2.Label, a1.Flag FROM ("
                + " SELECT SUM(ItemPrice) AS Price, '借出' AS Label, 4 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType='jc' AND STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				+ " UNION "
                + " SELECT SUM(ItemPrice) AS Price, '借入' AS Label, 5 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType='jr' AND STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
				+ " a1 INNER JOIN ("
                + " SELECT SUM(ItemPrice) AS Price, '还入' AS Label, 4 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType='hr' AND STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				+ " UNION "
                + " SELECT SUM(ItemPrice) AS Price, '还出' AS Label, 5 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType='hc' AND STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')"
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
				+ " a2 ON a1.Flag=a2.Flag ORDER BY a1.Flag ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				double shouru = result.getDouble(0);
				double zhichu = result.getDouble(1);
				Map<String, String> map = new HashMap<String, String>();
				map.put("shouruprice", "￥ " + UtilityHelper.formatDouble(shouru, "0.0##"));
				map.put("zhichuprice", "￥ " + UtilityHelper.formatDouble(zhichu, "0.0##"));
				map.put("shourulabel", result.getString(2));
				map.put("zhichulabel", result.getString(3));
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

	//查所有收入支出
	public Map<String, String> findAllShouZhi(int cardId) {
		Map<String, String> map = new HashMap<String, String>();
		
		String sql = 
				" SELECT IFNULL(a1.Price,0), IFNULL(a2.Price,0) FROM ("
				+ " SELECT SUM(ItemPrice) AS Price, 1 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE ItemType IN ('sr', 'hr', 'jr') AND (CASE IFNULL(CardID,'') WHEN '' THEN 0 ELSE CardID END)=" + cardId
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
		        + " a1 INNER JOIN ("
		        + " SELECT SUM(ItemPrice) AS Price, 1 AS Flag FROM " + ITEMTABNAME 
				+ " WHERE IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc') AND (CASE IFNULL(CardID,'') WHEN '' THEN 0 ELSE CardID END)=" + cardId
				+ " AND STRFTIME('%Y-%m-%d', ItemBuyDate)<=STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')))"
                + " a2 ON a1.Flag=a2.Flag";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				map.put("shouprice", UtilityHelper.formatDouble(result.getDouble(0), "0.###"));
				map.put("zhiprice", UtilityHelper.formatDouble(result.getDouble(1), "0.###"));
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
	
	//查同步消费
	public List<Map<String, String>> findSyncItem() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemID, ItemName, ItemPrice, ItemBuyDate, CategoryID, ItemWebID, Recommend, RegionID, RegionType, IFNULL(ItemType,'zc'), ZhuanTiID, IFNULL(CardID,0) FROM " + ITEMTABNAME 
				   + " WHERE Synchronize = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("itemid", result.getString(0));
				map.put("itemname", result.getString(1));
				map.put("itemprice", String.valueOf(result.getDouble(2)));
				map.put("itembuydate", result.getString(3));
				map.put("catid", result.getString(4));
				map.put("itemwebid", result.getString(5));
				map.put("recommend", result.getString(6));
				map.put("regionid", result.getString(7));
				map.put("regiontype", result.getString(8));
				map.put("itemtype", result.getString(9));
				map.put("ztid", result.getString(10).equals("") ? "0" : result.getString(10));
				map.put("cardid", result.getString(11).equals("") ? "0" : result.getString(11));
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

	//查是否有同步消费
	public boolean hasSyncItem() {
		boolean bool = false;
		
		String sql = "SELECT COUNT(0) FROM " + ITEMTABNAME + " WHERE Synchronize = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				bool = result.getInt(0) > 0;
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
	
	//查是否有消费
	public boolean hasItem() {
		boolean bool = false;
		
		String sql = "SELECT COUNT(0) FROM " + ITEMTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				bool = result.getInt(0) > 0;
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
	
	//修复同步状态 3.1.4
	public void fixSyncStatus() {
		String sql = "UPDATE " + ITEMTABNAME + " SET Synchronize = '1'";
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//查同步删除消费
	public List<Map<String, String>> findDelSyncItem() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT ItemID, ItemWebID FROM " + DELTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemid", result.getString(0));
				map.put("itemwebid", result.getString(1));
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

	//添加消费
	public boolean addItem(String itemType, String itemName, String itemPrice, String itemBuyDate, int catId, int recommend, int regionId, String regionType, int zhuanTiId, int cardId) {
		String sql = " INSERT INTO " + ITEMTABNAME + "(ItemType, ItemName, ItemPrice, ItemBuyDate, CategoryID, Synchronize, Recommend, RegionId, RegionType, ZhuanTiID, CardID)"
			   	   + " VALUES ('" + itemType + "', '" + UtilityHelper.replaceLine(itemName) + "', '" + itemPrice + "', '" + itemBuyDate + "', '" + catId + "', '1', '" + recommend + "', '" + regionId + "', '" + regionType + "', '" + zhuanTiId + "', '" + cardId + "')";
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
		
	//取最大区间ID
	public int getMaxRegionId() {
		int regionId = 0;
		
		Cursor result = null;
		try {
		    result = this.db.rawQuery("SELECT IFNULL(MAX(RegionID),0) FROM " + ITEMTABNAME, null);
		    if(result.moveToFirst()) {
		    	regionId = result.getInt(0);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return (regionId+1) % 2 == 0 ? regionId+1 : regionId+2;
	}
	
	//取区间ID日期
	public String[] getRegionDate(int regionId) {
		String[] date = new String[2];
		
		Cursor result = null;
		try {
		    result = this.db.rawQuery("SELECT STRFTIME('%Y-%m-%d', MIN(ItemBuyDate)), STRFTIME('%Y-%m-%d', MAX(ItemBuyDate)) FROM " + ITEMTABNAME + " WHERE RegionID = " + regionId, null);
		    if(result.moveToFirst()) {
		    	date[0] = result.getString(0);
		    	date[1] = result.getString(1);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return date;
	}

	//添加网络消费
	public boolean addWebItem(int itemId, int itemAppId, String itemName, String itemPrice, String itemBuyDate, int catId, int recommend, int regionId, String regionType, String itemType, int ztId, int cardId) {
		String sql = "SELECT ItemID FROM " + ITEMTABNAME + " WHERE ItemWebID = " + itemId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if(result.moveToFirst()) {
				sql = " UPDATE " + ITEMTABNAME + " SET ItemName = '" + itemName + "', ItemPrice = '" + itemPrice + "', ItemBuyDate = '" + itemBuyDate + "', CategoryID = '" + catId + "', Synchronize = '0', Recommend = '" + recommend + "', RegionID = '" + regionId + "', RegionType = '" + regionType + "', ItemType = '" + itemType + "', ZhuanTiID = '" + ztId + "', CardID = '" + cardId + "'"
				    + " WHERE ItemID = " + result.getString(0);
			} else if(itemAppId > 0) {
				sql = " UPDATE " + ITEMTABNAME + " SET ItemName = '" + itemName + "', ItemPrice = '" + itemPrice + "', ItemBuyDate = '" + itemBuyDate + "', CategoryID = '" + catId + "', Synchronize = '0', Recommend = '" + recommend + "', RegionID = '" + regionId + "', RegionType = '" + regionType + "', ItemType = '" + itemType + "', ZhuanTiID = '" + ztId + "', CardID = '" + cardId + "'"
					+ " WHERE ItemID = " + itemAppId;
			} else {
				sql = " INSERT INTO " + ITEMTABNAME + "(ItemWebID, ItemName, ItemPrice, ItemBuyDate, CategoryID, Synchronize, Recommend, RegionID, RegionType, ItemType, ZhuanTiID, CardID)"
					+ " VALUES('" + itemId + "', '" + itemName + "', '" + itemPrice + "', '" + itemBuyDate + "', '" + catId + "', '0', '" + recommend + "', '" + regionId + "', '" + regionType + "', '" + itemType + "', '" + ztId + "', '" + cardId + "')";
			}
		    this.db.execSQL(sql);
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
	
	//根据ItemWebID取ItemID
	public int getItemId(int itemWebId) {
		int itemId = 0;
		
		Cursor result = null;
		try {
		    result = this.db.rawQuery("SELECT ItemID FROM " + ITEMTABNAME + " WHERE ItemWebID = " + itemWebId, null);
		    if(result.moveToFirst()) {
		    	itemId = result.getInt(0);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return itemId;
	}

	//删除网络消费
	public void delWebItem(int itemId, int itemAppId) {
		String sql = "DELETE FROM " + ITEMTABNAME + " WHERE ItemID = " + itemAppId + " OR ItemWebID = " + itemId;
		try {
		    this.db.execSQL(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	

	//删除DeleteTable
	public void clearDelTable() {
		String sql = "DELETE FROM " + DELTABNAME;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//删除ItemTable
	public void clearItemTable() {
		String sql = "DELETE FROM " + ITEMTABNAME;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//删除最后消费
	public boolean deleteLastItem() {
		String sql = "DELETE FROM " + ITEMTABNAME + " WHERE ItemID = (SELECT MAX(ItemID) FROM " + ITEMTABNAME + ")";
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//编辑消费
	public boolean updateItem(int id, String itemType, String itemName, String itemPrice, String itemBuyDate, int catId, int ztId, int cardId) {
		String sql = " UPDATE " + ITEMTABNAME + " SET ItemType = '" + itemType + "', ItemName = '" + itemName + "', ItemPrice = '" + itemPrice + "', ItemBuyDate = '" + itemBuyDate + "', CategoryID = '" + catId + "', ZhuanTiID = '" + ztId + "', CardID = '" + cardId + "', Synchronize = '1'"
				   + " WHERE ItemID = " + id;
		Cursor result = null;
		try {			
			this.db.execSQL(sql);
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
	
	//删除消费
	public boolean deleteItem(int itemId) {
		this.db.beginTransaction();
		Cursor result = null;
		try {
			int itemWebId = 0;
		    result = this.db.rawQuery("SELECT ItemWebID FROM " + ITEMTABNAME + " WHERE ItemID = " + itemId, null);
		    if(result.moveToFirst()) {
		    	itemWebId = result.getInt(0);
		    }
		    
		    this.db.execSQL("DELETE FROM " + ITEMTABNAME + " WHERE ItemID = " + itemId);
		    this.db.execSQL("INSERT INTO " + DELTABNAME + " (ItemID, ItemWebID) VALUES (" + itemId + ", " + itemWebId + ")");
		    this.db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			this.db.endTransaction();
			if(result != null) {
				result.close();
			}
		}
		
		return true;
	}
	
	//删除专题
	public boolean deleteZhuanTi(int ztId) {
		String sql = "SELECT ItemID FROM " + ITEMTABNAME + " WHERE ZhuanTiID = " + ztId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while(result.moveToNext()) {
				int itemId = result.getInt(0);
				deleteItem(itemId);
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

	//删除消费区间
	public boolean deleteRegion(int regionId) {
		String sql = "SELECT ItemID FROM " + ITEMTABNAME + " WHERE RegionID = " + regionId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while(result.moveToNext()) {
				int itemId = result.getInt(0);
				deleteItem(itemId);
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
	
	//删除钱包
	public double deleteUserMoney(int id, double userMoney, String type) {
		String sql = "";
		if(type.equals("itemId")) {
			sql = "SELECT IFNULL(ItemType,'zc'), ItemPrice FROM " + ITEMTABNAME + " WHERE ItemID = " + id;
		} else {
			sql = "SELECT IFNULL(ItemType,'zc'), ItemPrice FROM " + ITEMTABNAME + " WHERE RegionID = " + id;
		}
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while(result.moveToNext()) {
				String itemType = result.getString(0);
				double itemPrice = result.getDouble(1);
				if(itemType.equals("zc") || itemType.equals("jc") || itemType.equals("hc")) {
					userMoney += itemPrice;
				} else {
					userMoney -= itemPrice;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return userMoney;
	}

	//反删除消费区间
	public boolean deleteRegionBack(int regionId) {
		String sql = "SELECT ItemID FROM " + ITEMTABNAME + " WHERE RegionID = " + regionId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while(result.moveToNext()) {
				int itemId = result.getInt(0);
				this.db.execSQL("DELETE FROM " + DELTABNAME + " WHERE ItemID = " + itemId);
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
		
	//更改同步状态
	public void updateSyncStatus(int id, int itemWebId) {
		String sql = "UPDATE " + ITEMTABNAME + " SET Synchronize = '0', ItemWebID = '" + itemWebId + "' WHERE ItemID = " + id;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//更改同步状态
	public void updateSyncStatus() {
		String sql = "UPDATE " + ITEMTABNAME + " SET Synchronize = '0'";
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//更新类别显示
	public boolean updateItemRecommend(int itemId, int recommend) {
		String sql = "UPDATE " + ITEMTABNAME + " SET Recommend = '" + recommend + "', Synchronize = '1' WHERE ItemID = " + itemId;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	//查消费分类排行
	public List<Map<String, String>> findRankCatByDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT a.CategoryID, a.CategoryName, a.ZhiItemPrice, b.ShouItemPrice, a.CategoryPrice FROM ("
				   + "SELECT ct.CategoryID, ct.CategoryName, SUM(ItemPrice) AS ZhiItemPrice, MAX(ct.CategoryPrice) AS CategoryPrice FROM " + CATTABNAME + " AS ct "
				   + "LEFT JOIN (SELECT * FROM " + ITEMTABNAME + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y%m', ItemBuyDate) = STRFTIME('%Y%m', '" + date + "') AND IFNULL(ItemType, 'zc') IN ('zc', 'jc', 'hc')) AS it "
				   + "ON ct.CategoryID = it.CategoryID WHERE ct.CategoryLive = '1' AND ct.CategoryDisplay = '1' "
				   + "GROUP BY ct.CategoryID, ct.CategoryName) a INNER JOIN ("
		           + "SELECT ct.CategoryID, ct.CategoryName, SUM(ItemPrice) AS ShouItemPrice, MAX(ct.CategoryPrice) AS CategoryPrice FROM " + CATTABNAME + " AS ct "
				   + "LEFT JOIN (SELECT * FROM " + ITEMTABNAME + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y%m', ItemBuyDate) = STRFTIME('%Y%m', '" + date + "') AND IFNULL(ItemType, 'zc') IN ('sr', 'jr', 'hr')) AS it "
				   + "ON ct.CategoryID = it.CategoryID WHERE ct.CategoryLive = '1' AND ct.CategoryDisplay = '1' "
				   + "GROUP BY ct.CategoryID, ct.CategoryName) b on a.CategoryID = b.CategoryID "
				   + "ORDER BY ZhiItemPrice DESC, ShouItemPrice DESC, a.CategoryID ASC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("catid", result.getString(0));
				map.put("catname", result.getString(1));
				map.put("zhiprice", result.getDouble(2) > 0 ? "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##") : "0");
				map.put("zhipricevalue", result.getDouble(2) > 0 ? result.getString(2) : "0");
				map.put("shouprice", result.getDouble(3) > 0 ? "￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.0##") : "0");
				map.put("shoupricevalue", result.getDouble(3) > 0 ? result.getString(3) : "0");
				map.put("catprice", result.getString(4));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return list;
	}
	
	//查消费分类排行
	public List<Map<String, String>> findCompareCatByDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String sql = "SELECT a.CategoryID, a.CategoryName, a.ZhiPrice AS ZhiPriceCur, a.ShouPrice AS ShouPriceCur, b.ZhiPrice AS ZhiPricePrev, b.ShouPrice AS ShouPricePrev FROM ("
				   + "SELECT a.CategoryID, a.CategoryName, a.ItemPrice AS ZhiPrice, b.ItemPrice AS ShouPrice FROM ("
				   + "SELECT ct.CategoryID, ct.CategoryName, SUM(ItemPrice) AS ItemPrice FROM " + CATTABNAME + " AS ct "
				   + "LEFT JOIN " + ITEMTABNAME + " AS it ON STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) "
				   + "AND STRFTIME('%Y%m', ItemBuyDate) = STRFTIME('%Y%m', '" + date + "') AND ct.CategoryID = it.CategoryID "
				   + "AND IFNULL(ItemType, 'zc') IN ('zc', 'jc', 'hc') WHERE ct.CategoryLive = '1' AND ct.CategoryDisplay = '1' "
				   + "GROUP BY ct.CategoryID, ct.CategoryName ORDER BY ItemPrice DESC, ct.CategoryID ASC) a INNER JOIN ("
				   + "SELECT ct.CategoryID, ct.CategoryName, SUM(ItemPrice) AS ItemPrice FROM " + CATTABNAME + " AS ct "
				   + "LEFT JOIN " + ITEMTABNAME + " AS it ON STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) "
				   + "AND STRFTIME('%Y%m', ItemBuyDate) = STRFTIME('%Y%m', '" + date + "') AND ct.CategoryID = it.CategoryID "
				   + "AND IFNULL(ItemType, 'zc') IN ('sr', 'jr', 'hr') WHERE ct.CategoryLive = '1' AND ct.CategoryDisplay = '1' "
				   + "GROUP BY ct.CategoryID, ct.CategoryName ORDER BY ItemPrice DESC, ct.CategoryID ASC) b ON a.CategoryID = b.CategoryID"
				   + ") AS a INNER JOIN ("
				   + "SELECT a.CategoryID, a.CategoryName, a.ItemPrice AS ZhiPrice, b.ItemPrice AS ShouPrice FROM ("
		           + "SELECT ct.CategoryID, ct.CategoryName, SUM(ItemPrice) AS ItemPrice FROM " + CATTABNAME + " AS ct "
				   + "LEFT JOIN " + ITEMTABNAME + " AS it ON STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) "
		           + "AND STRFTIME('%Y%m', ItemBuyDate) = STRFTIME('%Y%m', DATETIME('" + date + "', 'START OF MONTH', '-1 MONTH')) AND ct.CategoryID = it.CategoryID "
				   + "AND IFNULL(ItemType, 'zc') IN ('zc', 'jc', 'hc') WHERE ct.CategoryLive = '1' AND ct.CategoryDisplay = '1' "
				   + "GROUP BY ct.CategoryID, ct.CategoryName ORDER BY ItemPrice DESC, ct.CategoryID ASC) a INNER JOIN ("
		           + "SELECT ct.CategoryID, ct.CategoryName, SUM(ItemPrice) AS ItemPrice FROM " + CATTABNAME + " AS ct "
				   + "LEFT JOIN " + ITEMTABNAME + " AS it ON STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) "
		           + "AND STRFTIME('%Y%m', ItemBuyDate) = STRFTIME('%Y%m', DATETIME('" + date + "', 'START OF MONTH', '-1 MONTH')) AND ct.CategoryID = it.CategoryID "
				   + "AND IFNULL(ItemType, 'zc') IN ('sr', 'jr', 'hr') WHERE ct.CategoryLive = '1' AND ct.CategoryDisplay = '1' "
				   + "GROUP BY ct.CategoryID, ct.CategoryName ORDER BY ItemPrice DESC, ct.CategoryID ASC) b ON a.CategoryID = b.CategoryID"
				   + ") AS b ON a.CategoryID = b.CategoryID "
				   + "ORDER BY a.ZhiPrice DESC, b.ZhiPrice DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("catid", result.getString(0));
				map.put("catname", result.getString(1));
				map.put("zhipricecur", result.getDouble(2) > 0 ? UtilityHelper.formatDouble(result.getDouble(2), "0.0##") : "0");
				map.put("shoupricecur", result.getDouble(3) > 0 ? UtilityHelper.formatDouble(result.getDouble(3), "0.0##") : "0");
				map.put("zhipriceprev", result.getDouble(4) > 0 ? UtilityHelper.formatDouble(result.getDouble(4), "0.0##") : "0");
				map.put("shoupriceprev", result.getDouble(5) > 0 ? UtilityHelper.formatDouble(result.getDouble(5), "0.0##") : "0");
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return list;
	}

	//查消费次数排名
	public List<Map<String, String>> findRankCountByDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemName, COUNT(ItemName) AS Count, SUM(ItemPrice) AS Price, IFNULL(ItemType,'zc') FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') GROUP BY ItemName, IFNULL(ItemType,'zc')"
				   + " ORDER BY Count DESC, Price DESC, ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("count", result.getString(1) + " 次");
				map.put("price", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itemtype", getItemTypeName(result.getString(3), 1));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}

	//查消费分类明细
	public List<Map<String, String>> findRankCountByDate(String date, int catId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemName, COUNT(ItemName) AS Count, SUM(ItemPrice) AS Price, IFNULL(ItemType,'zc'), CategoryID FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND CategoryID = '" + catId + "'"
				   + " GROUP BY ItemName, IFNULL(ItemType,'zc'), CategoryID ORDER BY Count DESC, Price DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("count", result.getString(1) + " 次");
				map.put("price", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itemtype", getItemTypeName(result.getString(3), 1));
				map.put("catid", result.getString(4));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}
	
	//查消费分类明细
	public List<Map<String, String>> findAnalyzeCompareDetailByDate(String date, int catId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String sql = "SELECT ItemName, ItemType, CountCur, PriceCur, CountPrev, PricePrev FROM ("
				   + "SELECT a.ItemName, a.ItemType, IFNULL(a.Count, 0) AS CountCur, a.Price AS PriceCur, IFNULL(b.Count, 0) AS CountPrev, b.Price AS PricePrev FROM ("
				   + "SELECT ItemName, IFNULL(ItemType,'zc') AS ItemType, COUNT(ItemName) AS Count, SUM(ItemPrice) AS Price FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND CategoryID = '" + catId + "'"
				   + " GROUP BY ItemName, IFNULL(ItemType,'zc') ORDER BY ItemID ASC) AS a LEFT JOIN ("
				   + "SELECT ItemName, IFNULL(ItemType,'zc') AS ItemType, COUNT(ItemName) AS Count, SUM(ItemPrice) AS Price FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', DATETIME('" + date + "', 'START OF MONTH', '-1 MONTH')) AND CategoryID = '" + catId + "'"
				   + " GROUP BY ItemName, IFNULL(ItemType,'zc') ORDER BY ItemID ASC) AS b ON a.ItemName = b.ItemName AND IFNULL(a.ItemType,'zc') = IFNULL(b.ItemType,'zc')"
				   + " UNION ALL "
				   + "SELECT b.ItemName, b.ItemType, IFNULL(a.Count, 0) AS CountCur, a.Price AS PriceCur, IFNULL(b.Count, 0) AS CountPrev, b.Price AS PricePrev FROM ("
				   + "SELECT ItemName, IFNULL(ItemType,'zc') AS ItemType, COUNT(ItemName) AS Count, SUM(ItemPrice) AS Price FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', DATETIME('" + date + "', 'START OF MONTH', '-1 MONTH')) AND CategoryID = '" + catId + "'"
				   + " GROUP BY ItemName, IFNULL(ItemType,'zc') ORDER BY ItemID ASC) AS b LEFT JOIN ("
				   + "SELECT ItemName, IFNULL(ItemType,'zc') AS ItemType, COUNT(ItemName) AS Count, SUM(ItemPrice) AS Price FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND CategoryID = '" + catId + "'"
				   + " GROUP BY ItemName, IFNULL(ItemType,'zc') ORDER BY ItemID ASC) AS a ON a.ItemName = b.ItemName AND IFNULL(a.ItemType,'zc') = IFNULL(b.ItemType,'zc')"
				   + " ) t GROUP BY ItemName, ItemType, CountCur, PriceCur, CountPrev, PricePrev ORDER BY CountCur DESC, PriceCur DESC, CountPrev DESC, PricePrev DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("itemtype", getItemTypeName(result.getString(1), 1));
				map.put("countvalue", result.getString(2));
				map.put("countcur", result.getInt(2) > 0 ? result.getInt(2) + "次" : "0");
				map.put("pricecur", result.getDouble(3) > 0 ? UtilityHelper.formatDouble(result.getDouble(3), "0.0##") : "0");
				map.put("countprev", result.getInt(4) > 0 ? result.getInt(4) + "次" : "0");
				map.put("priceprev", result.getDouble(5) > 0 ? UtilityHelper.formatDouble(result.getDouble(5), "0.0##") : "0");
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}

	//查消费单价排名
	public List<Map<String, String>> findRankPriceByDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemName, ItemBuyDate, ItemPrice, IFNULL(ItemType,'zc') FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') ORDER BY ItemPrice DESC, ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(1), "m-d"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(1), "y-m-d"));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itemtype", getItemTypeName(result.getString(3), 1));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}

	//查区间统计
	public List<Map<String, String>> findRankRegion() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemName, MIN(ItemBuyDate), MAX(ItemBuyDate), ItemPrice, RegionType FROM " + ITEMTABNAME
				   + " WHERE RegionID <> 0"
				   + " GROUP BY ItemName, ItemPrice, RegionType"
				   + " ORDER BY RegionID DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(1), "ys-m-d")+"~"+UtilityHelper.formatDate(result.getString(2), "ys-m-d"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(1), "y-m-d"));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.0##"));
				map.put("regiontype", UtilityHelper.getRegionName(result.getString(4), 1));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}

	//查消费推荐分析
	public List<Map<String, String>> findRankRecommend() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemName, ItemBuyDate, ItemPrice, IFNULL(ItemType,'zc') FROM " + ITEMTABNAME 
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) AND Recommend = '1' ORDER BY ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(1), "ys-m-d"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(1), "y-m-d"));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itemtype", getItemTypeName(result.getString(3), 1));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}

	//查收入支出分析
	public List<Map<String, String>> findAnalyzeShouZhi(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(ZhiChuPrice), SUM(ShouRuPrice), ItemBuyDate, SUM(ShouRuPrice)-SUM(ZhiChuPrice) FROM ("
				   + " SELECT SUM(ItemPrice) AS ZhiChuPrice, 0 AS ShouRuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y', ItemBuyDate) = STRFTIME('%Y', '" + date + "') AND IFNULL(ItemType,'zc') IN ('zc', 'jc', 'hc')"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS ZhiChuPrice, SUM(ItemPrice) AS ShouRuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y', ItemBuyDate) = STRFTIME('%Y', '" + date + "') AND ItemType IN ('sr', 'jr', 'hr')"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("zhichuprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("zhichuvalue", UtilityHelper.formatDouble(result.getDouble(0), "0.0##"));
				map.put("shouruprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("shouruvalue", UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(2), "y-m"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(2), "y-m-d"));
				map.put("jiecunprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(3), "0.0##"));
				map.put("jiecunvalue", UtilityHelper.formatDouble(result.getDouble(3), "0.0##"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}

	//查借还分析
	public List<Map<String, String>> findAnalyzeJieHuan(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(JieChuPrice), SUM(HuanRuPrice), SUM(JieRuPrice), SUM(HuanChuPrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS JieChuPrice, 0 AS HuanRuPrice, 0 AS JieRuPrice, 0 AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y', ItemBuyDate) = STRFTIME('%Y', '" + date + "') AND ItemType='jc'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS JieChuPrice, SUM(ItemPrice) AS HuanRuPrice, 0 AS JieRuPrice, 0 AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y', ItemBuyDate) = STRFTIME('%Y', '" + date + "') AND ItemType='hr'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS JieChuPrice, 0 AS HuanRuPrice, SUM(ItemPrice) AS JieRuPrice, 0 AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y', ItemBuyDate) = STRFTIME('%Y', '" + date + "') AND ItemType='jr'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS JieChuPrice, 0 AS HuanRuPrice, 0 AS JieRuPrice, SUM(ItemPrice) AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y', ItemBuyDate) = STRFTIME('%Y', '" + date + "') AND ItemType='hc'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("jiechuprice", UtilityHelper.formatDouble(result.getDouble(0), "0.##"));
				map.put("huanruprice", UtilityHelper.formatDouble(result.getDouble(1), "0.##"));
				map.put("jieruprice", UtilityHelper.formatDouble(result.getDouble(2), "0.##"));
				map.put("huanchuprice", UtilityHelper.formatDouble(result.getDouble(3), "0.##"));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(4), "ys-m"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(4), "y-m-d"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}
	
	//查借还分析明细
	public List<Map<String, String>> findAnalyzeJieHuanDetail(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT SUM(JieChuPrice), SUM(HuanRuPrice), SUM(JieRuPrice), SUM(HuanChuPrice), ItemBuyDate FROM ("
				   + " SELECT SUM(ItemPrice) AS JieChuPrice, 0 AS HuanRuPrice, 0 AS JieRuPrice, 0 AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND ItemType='jc'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS JieChuPrice, SUM(ItemPrice) AS HuanRuPrice, 0 AS JieRuPrice, 0 AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND ItemType='hr'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS JieChuPrice, 0 AS HuanRuPrice, SUM(ItemPrice) AS JieRuPrice, 0 AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND ItemType='jr'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)"
				   + " UNION "
				   + " SELECT 0 AS JieChuPrice, 0 AS HuanRuPrice, 0 AS JieRuPrice, SUM(ItemPrice) AS HuanChuPrice, ItemBuyDate FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND ItemType='hc'"
				   + " AND STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour')) GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate))"
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate)";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("jiechuprice", UtilityHelper.formatDouble(result.getDouble(0), "0.##"));
				map.put("huanruprice", UtilityHelper.formatDouble(result.getDouble(1), "0.##"));
				map.put("jieruprice", UtilityHelper.formatDouble(result.getDouble(2), "0.##"));
				map.put("huanchuprice", UtilityHelper.formatDouble(result.getDouble(3), "0.##"));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(4), "m-d"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(4), "y-m-d"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}
	
	//根据关键字查消费
	public List<Map<String, String>> findItemByKey(String key, String curDate, String type) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String query = "";
		if(type.equals("all")) {
			query = "1=1";
		} else if (type.equals("year")) {
			query = "STRFTIME('%Y',ItemBuyDate)=STRFTIME('%Y','" + curDate + "')";
		} else if (type.equals("month")) {
			query = "STRFTIME('%Y-%m',ItemBuyDate)=STRFTIME('%Y-%m','" + curDate + "')";
		}
		
		String sql = " SELECT ItemName, ItemBuyDate, ItemPrice, IFNULL(ItemType,'zc') FROM " + ITEMTABNAME 
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND ItemName LIKE '%" + key + "%' AND " + query + " ORDER BY ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(1), "ys-m-d"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(1), "y-m-d"));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("pricevalue", UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itemtype", getItemTypeName(result.getString(3), 1));
				map.put("itemtypevalue", result.getString(3));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}
	
	//查消费次数明细
	public List<Map<String, String>> findRankPriceByDate(String date, String itemName, int catId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String catSql = catId > 0 ? "CategoryID = " + catId : "1=1";
		String sql = " SELECT ItemName, ItemBuyDate, ItemPrice, IFNULL(ItemType,'zc') FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "') AND ItemName = '" + itemName + "' AND " + catSql
				   + " ORDER BY ItemPrice DESC, ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("itemname", result.getString(0));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(1), "m-d"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(1), "y-m-d"));
				map.put("itemprice", "￥ " + UtilityHelper.formatDouble(result.getDouble(2), "0.0##"));
				map.put("itemtype", getItemTypeName(result.getString(3), 1));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}
	
	//查消费日期排名
	public List<Map<String, String>> findRankDateByDate(String date) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = " SELECT ItemBuyDate, SUM(ItemPrice) AS Price FROM " + ITEMTABNAME
				   + " WHERE STRFTIME('%Y-%m-%d', ItemBuyDate) <= STRFTIME('%Y-%m-%d', datetime('now', '+8 hour'))"
				   + " AND STRFTIME('%Y-%m', ItemBuyDate) = STRFTIME('%Y-%m', '" + date + "')" 
				   + " GROUP BY STRFTIME('%Y-%m-%d', ItemBuyDate) ORDER BY Price DESC, ItemBuyDate DESC";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("itembuydate", UtilityHelper.formatDate(result.getString(0), "m-d-w"));
				map.put("datevalue", UtilityHelper.formatDate(result.getString(0), "y-m-d"));
				map.put("price", "￥ " + UtilityHelper.formatDouble(result.getDouble(1), "0.0##"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return list;
	}

	//恢复数据
	public boolean restoreDataBase(List<CharSequence> list) {
		this.db.beginTransaction();
		try {
			Iterator<CharSequence> it = list.iterator();
			while(it.hasNext()) {
				String sql = it.next().toString();
				if(sql.equals("")) continue;
				this.db.execSQL(sql);
			}
			this.db.setTransactionSuccessful();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			this.db.endTransaction();
		}
		
		return true;
	}
	
	//备份数据
	public List<CharSequence> bakDataBase() {
		List<CharSequence> list = new ArrayList<CharSequence>();
		
		//消费
		String sql = "SELECT ItemID, ItemName, ItemPrice, ItemBuyDate, CategoryID, Recommend, Synchronize, RegionID, RegionType, ItemType, ZhuanTiID, CardID, ItemWebID FROM " + ITEMTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			list.add("DELETE FROM " + ITEMTABNAME + ";");
			while (result.moveToNext()) {
				list.add("INSERT INTO " + ITEMTABNAME + " (ItemID, ItemName, ItemPrice, ItemBuyDate, CategoryID, Recommend, Synchronize, RegionID, RegionType, ItemType, ZhuanTiID, CardID, ItemWebID) VALUES ('" 
			             + result.getString(0)+ "', '" 
						 + UtilityHelper.replaceLine(result.getString(1)) + "', '"
					     + result.getString(2) + "', '" 
						 + result.getString(3) + "', '" 
					     + result.getString(4) + "', '" 
						 + result.getString(5) + "', '" 
						 + result.getString(6) + "', '" 
						 + result.getString(7) + "', '" 
						 + result.getString(8) + "', '" 
						 + result.getString(9) + "', '" 
					     + result.getString(10) + "', '" 
					     + result.getString(11) + "', '"
					     + result.getString(12) + "');");
			}
			result.close();
			list.add("");
			
			//类别
			sql = "SELECT CategoryID, CategoryName, CategoryPrice, CategoryRank, CategoryDisplay, CategoryLive, Synchronize FROM " + CATTABNAME;
			result = this.db.rawQuery(sql, null);
			list.add("DELETE FROM " + CATTABNAME + ";");	
			while (result.moveToNext()) {
				list.add("INSERT INTO " + CATTABNAME + " (CategoryID, CategoryName, CategoryPrice, CategoryRank, CategoryDisplay, CategoryLive, Synchronize) VALUES ('" 
			             + result.getString(0)+ "', '" 
						 + UtilityHelper.replaceLine(result.getString(1)) + "', '"
					     + result.getString(2) + "', '" 
						 + result.getString(3) + "', '" 
						 + result.getString(4) + "', '" 
					     + result.getString(5) + "', '"
					     + result.getString(6) + "');");
			}
			result.close();
			list.add("");

			//专题 
			sql = "SELECT ZTID, ZhuanTiName, ZhuanTiImage, ZhuanTiLive, Synchronize FROM " + ZTTABNAME;
			result = this.db.rawQuery(sql, null);
			list.add("DELETE FROM " + ZTTABNAME + ";");	
			while (result.moveToNext()) {
				list.add("INSERT INTO " + ZTTABNAME + " (ZTID, ZhuanTiName, ZhuanTiImage, ZhuanTiLive, Synchronize) VALUES ('" 
			             + result.getString(0)+ "', '" 
						 + UtilityHelper.replaceLine(result.getString(1)) + "', '"
					     + result.getString(2) + "', '" 
						 + result.getString(3) + "', '" 
					     + result.getString(4) + "');");
			}
			result.close();
			list.add("");

			//转账
			sql = "SELECT ZZID, ZhangFrom, ZhangTo, ZhangMoney, ZhangDate, ZhangNote, ZhangLive, Synchronize FROM " + ZZTABNAME;
			result = this.db.rawQuery(sql, null);
			list.add("DELETE FROM " + ZZTABNAME + ";");	
			while (result.moveToNext()) {
				list.add("INSERT INTO " + ZZTABNAME + " (ZZID, ZhangFrom, ZhangTo, ZhangMoney, ZhangDate, ZhangNote, ZhangLive, Synchronize) VALUES ('" 
			             + result.getString(0)+ "', '" 
						 + result.getString(1) + "', '"
					     + result.getString(2) + "', '" 
						 + result.getString(3) + "', '" 
						 + result.getString(4) + "', '" 
						 + result.getString(5) + "', '" 
						 + result.getString(6) + "', '" 
					     + result.getString(7) + "');");
			}
			result.close();
			list.add("");
			
			//钱包
			sql = "SELECT CDID, CardName, CardMoney, CardLive, Synchronize, CardNumber, CardImage FROM " + CARDTABNAME;
			result = this.db.rawQuery(sql, null);
			list.add("DELETE FROM " + CARDTABNAME + ";");	
			while (result.moveToNext()) {
				list.add("INSERT INTO " + CARDTABNAME + " (CDID, CardName, CardMoney, CardLive, Synchronize, CardNumber, CardImage) VALUES ('" 
			             + result.getString(0)+ "', '" 
						 + UtilityHelper.replaceLine(result.getString(1)) + "', '"
					     + result.getString(2) + "', '" 
						 + result.getString(3) + "', '" 
					     + result.getString(4) + "', '" 
					     + result.getString(5) + "', '" 
					     + result.getString(6) + "');");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return list;
	}
		
	//关闭数据库
	public void close() {
		this.db.close();
	}

	//取分类文字
	protected String getItemTypeName(String itemType, int type) {
		String result = "";
		if(itemType.equals("zc")) {
			result = (type==0 ? "-" : "支出");
		} else if(itemType.equals("sr")) {
			result = (type==0 ? "+" : "收入");
		} else if(itemType.equals("jr")) {
			result = (type==0 ? "借" : "借入");
		} else if(itemType.equals("hc")) {
			result = (type==0 ? "还" : "还出");
		} else if(itemType.equals("jc")) {
			result = (type==0 ? "借" : "借出");
		} else if(itemType.equals("hr")) {
			result = (type==0 ? "还" : "还入");
		}
		
		return result;
	}
	
	//取价格
	protected String getItemPrice(String type, String itemType, String itemPrice) {
		String result = "";
		if(type.equals("zc")) {
			if(itemType.equals("zc") || itemType.equals("hc") || itemType.equals("jc")) {
				result = getItemTypeName(itemType, 0) + " " + itemPrice;
			}
		} else {
			if(itemType.equals("sr") || itemType.equals("hr") || itemType.equals("jr")) {
				result = getItemTypeName(itemType, 0) + " " + itemPrice;
			}
		}
		
		return result;
	}
	
	//取价格
	protected String getItemPrice2(String type, String itemType, String itemPrice) {
		String result = "";
		if(type.equals("zc")) {
			if(itemType.equals("zc") || itemType.equals("hc") || itemType.equals("jc")) {
				result = itemPrice;
			}
		} else {
			if(itemType.equals("sr") || itemType.equals("hr") || itemType.equals("jr")) {
				result = itemPrice;
			}
		}
		
		return result;
	}
	
	//取加了空格的导出数据
	protected String getExportString(int type, String str, String flag) {
		String result = "";		
		switch(type) {
			case 1:
			case 2:
			case 8:
				result = str + getExportEmpty(8, str, flag);
				break;
			case 3:
				result = str + getExportEmpty(20, str, flag);
				break;
			case 4:
				result = str + getExportEmpty(40, str, flag);
				break;
			case 5:
			case 6:
			case 7:
			case 9:
			case 10:
				result = str + getExportEmpty(14, str, flag);
				break;
			
		}
		
		return result;
	}
	
	//取空格
	protected String getExportEmpty(int num, String str, String dot) {
		String empty = "";
		dot = dot.equals("") ? " " : dot;
		try {
			int len = str.getBytes("GBK").length;//getStringLength(str);//
			int count = num - len;
			for(int i=0; i<count; i++) {
				empty += dot;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return empty;
	}
	
	//取长度
	protected static int getStringLength(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
		    String temp = value.substring(i, i + 1);
		    if (temp.matches(chinese)) {
			    valueLength += 2;
			} else {
			    valueLength += 1;
			}
		}
		return valueLength;
	}
}
