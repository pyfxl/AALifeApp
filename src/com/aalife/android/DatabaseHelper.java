package com.aalife.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASENAME = "aalife.db";
	private static final int DATABASEVERSION = 22;
	private static final String ITEMTABNAME = "ItemTable";
	private static final String CATTABNAME = "CategoryTable";
	private static final String DELTABNAME = "DeleteTable";
	private static final String ZTTABNAME = "ZhuanTiTable";
	private static final String CARDTABNAME = "CardTable";
	private static final String ZZTABNAME = "ZhuanZhangTable";

	public DatabaseHelper(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//消费表
		db.execSQL("CREATE TABLE " + ITEMTABNAME + " ("
				+ "ItemID           INTEGER          PRIMARY KEY, "
				+ "ItemWebID        INTEGER          DEFAULT 0, "
				+ "ItemType         VARCHAR(10)      DEFAULT 'zc', "
				+ "ItemName         VARCHAR(20)      NOT NULL, "
				+ "ItemPrice        REAL             NOT NULL, "
				+ "ItemBuyDate      DATE             NOT NULL, "
				+ "CategoryID       INTEGER          NOT NULL, "
				+ "Synchronize      INTEGER          DEFAULT 1, "
				+ "Recommend        INTEGER          DEFAULT 0, "
				+ "RegionID         INTEGER          DEFAULT 0, "
				+ "RegionType       VARCHAR(10), "
				+ "Remark           VARCHAR(1000), "
				+ "ZhuanTiID        INTEGER          DEFAULT 0, "
				+ "CardID           INTEGER          DEFAULT 0)");
		//类别表
		db.execSQL("CREATE TABLE " + CATTABNAME + " ("
				+ "CategoryID       INTEGER          NOT NULL, "
				+ "CategoryName     VARCHAR(20)      NOT NULL, "
				+ "CategoryPrice    REAL             DEFAULT 0, "
				+ "Synchronize      INTEGER          DEFAULT 0, "
				+ "CategoryRank     INTEGER, "
				+ "CategoryDisplay  INTEGER          DEFAULT 1, "
				+ "CategoryLive     INTEGER          DEFAULT 1, "
				+ "IsDefault        INTEGER          DEFAULT 0)");
		//专题表
		db.execSQL("CREATE TABLE " + ZTTABNAME + " ("
				+ "ZhuanTiID        INTEGER          PRIMARY KEY, "
				+ "ZTID             INTEGER          NOT NULL, "
				+ "ZhuanTiName      VARCHAR(20)      NOT NULL, "
				+ "Synchronize      INTEGER          DEFAULT 1, "
				+ "ZhuanTiImage     VARCHAR(200), "
				+ "ZhuanTiLive      INTEGER          DEFAULT 1)");
		//钱包表
		db.execSQL("CREATE TABLE " + CARDTABNAME + " ("
				+ "CardID           INTEGER          PRIMARY KEY, "
				+ "CDID             INTEGER          NOT NULL, "
				+ "CardName         VARCHAR(20)      NOT NULL, "
				+ "CardMoney        REAL             NOT NULL, "
				+ "Synchronize      INTEGER          DEFAULT 1, "
				+ "CardNumber       VARCHAR(50), "
				+ "CardImage        VARCHAR(50), "
				+ "CardLive         INTEGER          DEFAULT 1)");
		//转账表
		db.execSQL("CREATE TABLE " + ZZTABNAME + " ("
				+ "ZhangID          INTEGER          PRIMARY KEY, "
				+ "ZZID             INTEGER          NOT NULL, "
				+ "ZhangFrom        INTEGER          NOT NULL, "
				+ "ZhangTo          INTEGER          NOT NULL, "
				+ "Synchronize      INTEGER          DEFAULT 1, "
				+ "ZhangMoney       REAL             NOT NULL, "
				+ "ZhangDate        DATE             NOT NULL, "
				+ "ZhangNote        VARCHAR(100), "
				+ "ZhangLive        INTEGER          DEFAULT 1)");
		//插入类别
		db.execSQL("INSERT INTO " + CATTABNAME + "(CategoryID, CategoryName, CategoryPrice, CategoryRank, IsDefault) "
				+ "SELECT '1', '菜米油盐酱醋', '0', '1', '1' UNION ALL " 
				+ "SELECT '2', '外就餐', '0', '2', '1' UNION ALL "
				+ "SELECT '3', '烟酒水', '0', '3', '1' UNION ALL " 
				+ "SELECT '4', '零食', '0', '4', '1' UNION ALL "
				+ "SELECT '5', '水果', '0', '5', '1' UNION ALL " 
				+ "SELECT '6', '日用品', '0', '6', '1' UNION ALL "
				+ "SELECT '7', '电子产品', '0', '7', '1' UNION ALL " 
				+ "SELECT '8', '衣裤鞋袜帽', '0', '8', '1' UNION ALL "
				+ "SELECT '9', '杂费用', '0', '9', '1' UNION ALL " 
				+ "SELECT '10', '娱乐', '0', '10', '1'");
		//删除表
		db.execSQL("CREATE TABLE " + DELTABNAME + " ("
				+ "DeleteID         INTEGER          PRIMARY KEY, "
				+ "ItemID           INTEGER, "
				+ "ItemWebID        INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 15) {
			db.execSQL("DROP TABLE IF EXISTS " + CATTABNAME);
			db.execSQL("DROP TABLE IF EXISTS " + ITEMTABNAME);
			db.execSQL("DROP TABLE IF EXISTS " + DELTABNAME);
			onCreate(db);
		} else {
			for(int i = oldVersion; i < newVersion; i++) {
				switch(i) {
				case 17:
					db.execSQL("ALTER TABLE " + ITEMTABNAME + " ADD ItemType VARCHAR(10) DEFAULT 'zc'");
					db.execSQL("UPDATE " + CATTABNAME + " SET CategoryName='衣裤鞋袜帽' WHERE CategoryName='衣裤鞋'");
					db.execSQL("UPDATE " + CATTABNAME + " SET CategoryName='费用' WHERE CategoryName='交费'");			
					break;
				case 18://4.4.1
					db.execSQL("ALTER TABLE " + ITEMTABNAME + " ADD ZhuanTiID INTEGER DEFAULT 0");
					db.execSQL("CREATE TABLE " + ZTTABNAME + " ("
						    + "ZhuanTiID        INTEGER          PRIMARY KEY, "
						    + "ZTID             INTEGER          NOT NULL, "
						    + "ZhuanTiName      VARCHAR(20)      NOT NULL, "
						    + "Synchronize      INTEGER          DEFAULT 1, "
						    + "ZhuanTiImage     VARCHAR(200), "
						    + "ZhuanTiLive      INTEGER          DEFAULT 1)");			
					break;
				case 19://4.5.1
					db.execSQL("ALTER TABLE " + CATTABNAME + " ADD CategoryPrice REAL DEFAULT 0");
					db.execSQL("UPDATE " + CATTABNAME + " SET CategoryName='杂费用' WHERE CategoryName='费用'");			
					break;
				case 20://4.6.1
					db.execSQL("ALTER TABLE " + ITEMTABNAME + " ADD CardID INTEGER DEFAULT 0");
					db.execSQL("CREATE TABLE " + CARDTABNAME + " ("
							+ "CardID           INTEGER          PRIMARY KEY, "
							+ "CDID             INTEGER          NOT NULL, "
							+ "CardName         VARCHAR(20)      NOT NULL, "
							+ "CardMoney        REAL             NOT NULL, "
							+ "Synchronize      INTEGER          DEFAULT 1, "
							+ "CardNumber       VARCHAR(50), "
							+ "CardImage        VARCHAR(50), "
							+ "CardLive         INTEGER          DEFAULT 1)");
					break;
				case 21://4.9.0
					db.execSQL("CREATE TABLE " + ZZTABNAME + " ("
							+ "ZhangID          INTEGER          PRIMARY KEY, "
							+ "ZZID             INTEGER          NOT NULL, "
							+ "ZhangFrom        INTEGER          NOT NULL, "
							+ "ZhangTo          INTEGER          NOT NULL, "
							+ "Synchronize      INTEGER          DEFAULT 1, "
							+ "ZhangMoney       REAL             NOT NULL, "
							+ "ZhangDate        DATE             NOT NULL, "
							+ "ZhangNote        VARCHAR(100), "
							+ "ZhangLive        INTEGER          DEFAULT 1)");
					break;
				}
			}
		}
	}
	
}
