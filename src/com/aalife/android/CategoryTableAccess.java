package com.aalife.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoryTableAccess {
	private static final String ITEMTABNAME = "ItemTable";
	private static final String CATTABNAME = "CategoryTable";
	private SQLiteDatabase db = null;

	public CategoryTableAccess(SQLiteDatabase db) {
		this.db = db;
	}

	//查所有分类下拉
	public List<CharSequence> findAllCategory() {
		List<CharSequence> all = new ArrayList<CharSequence>();
		
		String sql = "SELECT CategoryName FROM " + CATTABNAME + " WHERE CategoryLive = '1' AND CategoryDisplay = '1'";
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
	
	//查所有分类下拉--AddSmart
	public List<Map<String, String>> findAllCategorySmart() {
		List<Map<String, String>> all = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT CategoryID, CategoryName FROM " + CATTABNAME + " WHERE CategoryLive = '1' AND CategoryDisplay = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", result.getString(0));
				map.put("name", result.getString(1));
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

	//查所有分类管理
	public List<Map<String, String>> findAllCatEdit() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT CategoryID, CategoryName, CategoryDisplay, CategoryPrice FROM " + CATTABNAME + " WHERE CategoryLive = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("catid", result.getString(0));
				map.put("catname", result.getString(1));
				map.put("catdisplay", result.getString(2));
				map.put("catprice", UtilityHelper.formatDouble(result.getDouble(3), "0.###"));
				map.put("catrate", "0");
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
	
	//查所有同步分类管理
	public List<Map<String, String>> findAllSyncCat() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		String sql = "SELECT CategoryID, CategoryName, CategoryPrice, CategoryLive FROM " + CATTABNAME + " WHERE Synchronize = '1'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			while (result.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(result.getPosition() + 1));
				map.put("catid", result.getString(0));
				map.put("catname", result.getString(1));
				map.put("catprice", result.getString(2));
				map.put("catlive", result.getString(3));
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

	//根据名称返回ID
	public int findCatIdByName(String catName) {
		int catId = 0; 
		
		String sql = "SELECT CategoryID FROM " + CATTABNAME + " WHERE CategoryName = '" + catName + "' AND CategoryLive = 1";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				catId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return catId;
	}

	//根据名称返回ID
	public int findCategoryId(String catName, String catPrice) {
		int catId = 0; 
		
		String sql = "SELECT CategoryID FROM " + CATTABNAME + " WHERE CategoryName = '" + catName + "' AND CategoryPrice = '" + catPrice + "' AND CategoryLive = 1";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				catId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return catId;
	}

	//同步时检查ID是否存在
	public int findCategoryId(int catId) {
		int categoryId = 0; 
		
		String sql = "SELECT CategoryID FROM " + CATTABNAME + " WHERE CategoryID = " + catId;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				categoryId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return categoryId;
	}

	//检查ID是否在使用
	public boolean findItemByCatId(int categoryId) {
		boolean bool = false; 
		
		String sql = "SELECT CategoryID FROM " + ITEMTABNAME + " WHERE CategoryID = " + categoryId;
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

	//查找类别数量
	public int findCategoryCount() {
		int count = 0; 
		
		String sql = "SELECT COUNT(0) FROM " + CATTABNAME + " WHERE CategoryLive = 1";
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

	//查找最大类别ID
	public int getMaxCategoryId() {
		int catId = 0;
		
		String sql = "SELECT IFNULL(MAX(CategoryID), 0) + 1 FROM " + CATTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				catId = result.getInt(0);
				catId = ((catId+1) % 2 == 0 ? catId+1 : catId+2);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return catId;
	}

	//查找最大可用类别ID
	public int getMaxUseCategoryId() {
		int catId = 0;
		
		String sql = "SELECT MAX(CategoryID) FROM " + CATTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				catId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return catId;
	}
	
	//保存类别
	public int saveCategory(int saveId, String catName, String catPrice) {
		int catId = findCategoryId(catName, catPrice);
		if(catId > 0) {
			return catId;
		}
		
		String sql = "";
		Cursor result = null;
		try {
			if(saveId == 0) {
				catId = getMaxCategoryId();
				sql = "INSERT INTO " + CATTABNAME + "(CategoryID, CategoryName, CategoryPrice, Synchronize, CategoryRank) "
				   	+ "VALUES ('" + catId + "', '" + catName + "', '" + catPrice + "', '1', '" + catId + "')";
			} else {
				catId = saveId;
				sql = "UPDATE " + CATTABNAME + " SET CategoryName = '" + catName + "', CategoryPrice = '" + catPrice + "', Synchronize = '1', IsDefault = '0' WHERE CategoryID = " + catId;
			}
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}
		
		return catId;
	}

	//保存网络类别
	public void saveWebCategory(int catId, String catName, double catPrice, int catLive) throws Exception {
		boolean hasCategory = findCategoryId(catId) > 0;
		
		String sql = "";
		if (hasCategory) {
			sql = "UPDATE " + CATTABNAME + " SET CategoryName = '" + catName + "', CategoryPrice = '" + catPrice + "', CategoryLive = '" + catLive + "', Synchronize = '0', IsDefault = '0' WHERE CategoryID = " + catId;
		} else {
			sql = "INSERT INTO " + CATTABNAME + "(CategoryID, CategoryName, CategoryPrice, CategoryLive, Synchronize, CategoryRank) "
			   	+ "VALUES ('" + catId + "', '" + catName + "', '" + catPrice + "', '" + catLive + "', '0', '" + catId + "')";
		}
		
		try {
			this.db.execSQL(sql);
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}

	//更改同步状态
	public void updateSyncStatus(int id) {
		String sql = "UPDATE " + CATTABNAME + " SET Synchronize = '0' WHERE CategoryID = " + id;
		try {
		    this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//删除类别
	public int delCategory(int catId) {
		boolean hasItem = findItemByCatId(catId);
		if (hasItem) {
			return 2;
		}
		
		if(findCategoryCount() == 1) {
			return 3;
		}
		
		String sql = "UPDATE " + CATTABNAME + " SET CategoryLive = '0', Synchronize = '1' WHERE CategoryID = " + catId;
		try {
			this.db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
		return 1;
	}
	
	//根据ID返回名称
	public String findCatNameById(int id) {
		String catName = ""; 
		
		String sql = "SELECT CategoryName FROM " + CATTABNAME + " WHERE CategoryID = '" + id + "'";
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				catName = result.getString(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return catName;
	}

	//返回最小CatID
	public int findMinCategoryId() {
		int catId = 0; 
		
		String sql = "SELECT IFNULL(MIN(CategoryID), 0) FROM " + CATTABNAME;
		Cursor result = null;
		try {
			result = this.db.rawQuery(sql, null);
			if (result.moveToFirst()) {
				catId = result.getInt(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) {
				result.close();
			}
		}

		return catId;
	}
	
	//关闭数据库
	public void close() {
		this.db.close();
	}
	
}
