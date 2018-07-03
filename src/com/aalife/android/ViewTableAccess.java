package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ViewTableAccess {
	private static final String VIEWTABNAME = "ViewTable";
	private SQLiteDatabase db = null;

	public ViewTableAccess(SQLiteDatabase db) {
		this.db = db;
	}

	//添加
	public boolean addView(int pageId, String dateStart, String portal, String version, String model, int width, int height, String IP, String network, String remark) {
		String sql = " INSERT INTO " + VIEWTABNAME + "(PageID, DateStart, Portal, Version, Browser, Width, Height, IP, Network, Remark)"
			   	   + " VALUES ('" + pageId + "', '" + dateStart + "', '" + portal + "', '" + version + "', '" + model + "', '" + width + "', '" + height + "', '" + IP + "', '" + network + "', '" + remark + "')";
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	//更改同步状态
	public void updateSyncStatus(int id) {
		String sql = "UPDATE " + VIEWTABNAME + " SET Synchronize = '0' WHERE ViewID = " + id;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//查所有同步数据
	public List<Map<String, String>> findAllSyncView() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT ViewID, PageID, DateStart, DateEnd, Portal, Version, Browser, Width, Height, IP, Network, Remark FROM " + VIEWTABNAME + " WHERE Synchronize = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("viewid", result.getString(0));
				map.put("pageid", result.getString(1));
				map.put("datestart", result.getString(2));
				map.put("dateend", result.getString(3));
				map.put("portal", result.getString(4));
				map.put("version", result.getString(5));
				map.put("browser", result.getString(6));
				map.put("width", result.getString(7));
				map.put("height", result.getString(8));
				map.put("ip", result.getString(9));
				map.put("network", result.getString(10));
				map.put("remark", result.getString(11).replace("null", ""));
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

	//关闭数据库
	public void close() {
		this.db.close();
	}
	
}
