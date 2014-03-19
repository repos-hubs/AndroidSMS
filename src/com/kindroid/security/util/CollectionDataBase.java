/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:
 * Date:
 * Description:
 */

package com.kindroid.security.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.android.model.CollectCategory;
import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.data.PrivateDBHelper.PrivateAutoMsg;
import com.kindroid.kincent.R;

public class CollectionDataBase {

	private final String DATABASE_NAME = "collection.db";
	private final int DATABASE_VERSION = 1;
	// table name for category
	private final String TABLE_COLLECT_CATEGORY = "collect_category";
	// table name for sms
	private final String TABLE_COLLECT_SMS = "collect_sms";
	private static SQLiteDatabase mDb;

	private static CollectionDataBase mDataBase;

	private CollectionDataBase(Context context) {
		mDb = new DatabaseHelper(context).getWritableDatabase();
	}

	/** instance of InterceptDataBase,the object is single */
	public synchronized static CollectionDataBase get(Context context) {
		if (mDataBase == null) {
			mDataBase = new CollectionDataBase(context);

		}
		return mDataBase;
	}

	/** close database */
	public synchronized static void close() {
		try {
			mDb.close();
			mDataBase = null;
		} catch (Exception e) {
			e.printStackTrace();
			mDataBase = null;
		}
	}

	/**
	 * 返回1 为已存在 返回2 为异常
	 * */
	public synchronized int insertCategory(String name) {

		try {
			Cursor c = mDb.query(TABLE_COLLECT_CATEGORY,
					new String[] { Category._ID }, Category.NAME + "=?",
					new String[] { name }, null, null, null);
			if (c.getCount() > 0) {
				return 1;
			} else {
				ContentValues cv = new ContentValues();
				cv.put(Category.NAME, name);
				mDb.insert(TABLE_COLLECT_CATEGORY, null, cv);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		}

		return 0;
	}
	
	
	
	public synchronized int delCategory(int id) {

		try {
			
			mDb.delete(TABLE_COLLECT_CATEGORY, Category._ID+"=?", new String[]{id+""});
			
			mDb.delete(TABLE_COLLECT_SMS, CollectSms.CATEGORYID+"=?", new String[]{id+""});

		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		}

		return 0;
	}
	
	
	
	/**
	 * 返回1 为已存在 返回2 为异常
	 * */
	public synchronized int updateCategory(String name,int id) {

		try {
			ContentValues cv = new ContentValues();
			cv.put(Category.NAME, name);
			mDb.update(TABLE_COLLECT_CATEGORY, cv, Category._ID+"=?", new String[]{id+""});
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		}

		return 0;
	}
	
	

	public List<CollectCategory> getCategorys() {
		List<CollectCategory> mCollectCategorys = new ArrayList<CollectCategory>();
		try {
			Cursor c = mDb
					.query(TABLE_COLLECT_CATEGORY, new String[] { Category._ID,
							Category.NAME }, null, null, null, null, null);
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex(Category._ID));
				String name = c.getString(c.getColumnIndex(Category.NAME));
				CollectCategory category = new CollectCategory();
				category.setmId(id);
				category.setmName(name);
				mCollectCategorys.add(category);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCollectCategorys;
	}

	public synchronized boolean insertCategorySmsItem(CategorySmsListItem item) {
		boolean ret = false;
		try {

			ContentValues cv = new ContentValues();
			cv.put(CollectSms.ADDRESS, item.getmAddress()==null?"":item.getmAddress());
			cv.put(CollectSms.BODY, item.getmBody());
			cv.put(CollectSms.DATE, item.getInsertTime()==null?"":item.getInsertTime());
			cv.put(CollectSms.CATEGORYID, item.getmCategoryId());
			mDb.insert(TABLE_COLLECT_SMS, null, cv);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	public List<CategorySmsListItem> getCategorySmsListItems(int id) {
		List<CategorySmsListItem> mCollectCategorys = new ArrayList<CategorySmsListItem>();
		try {
			Cursor c = mDb.query(TABLE_COLLECT_SMS, new String[] {
					CollectSms._ID, CollectSms.BODY, CollectSms.ADDRESS,
					CollectSms.DATE, CollectSms.CATEGORYID },

			CollectSms.CATEGORYID + "=?", new String[] { id + "" }, null, null,
					null);
			while (c.moveToNext()) {
				int lid = c.getInt(c.getColumnIndex(CollectSms._ID));
				String body = c.getString(c.getColumnIndex(CollectSms.BODY));
				int categoryid = c.getInt(c
						.getColumnIndex(CollectSms.CATEGORYID));
				String address = c.getString(c
						.getColumnIndex(CollectSms.ADDRESS));

				CategorySmsListItem item = new CategorySmsListItem();
				item.setmId(lid);
				item.setmBody(body);
				item.setmCategoryId(categoryid);
				item.setmAddress(address);
				String date = c.getString(c
						.getColumnIndex(InterceptDataBase.DATE));
				item.setmDate(date);
				mCollectCategorys.add(item);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCollectCategorys;
	}
	
	
	public void delCollectSms(int id) {
		
		try {
			System.out.println(id+"id");
			mDb.delete(TABLE_COLLECT_SMS, CollectSms._ID+"=?", new String[]{id+""});
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	

	class DatabaseHelper extends SQLiteOpenHelper {
		private Context context;

		public DatabaseHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTable(db);
		}

		private void createTable(SQLiteDatabase db) {
			createCollectCategoryTable(db);
			createCollectSmsTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

		private void createCollectCategoryTable(SQLiteDatabase db) {

			String tableStr = "CREATE TABLE " + TABLE_COLLECT_CATEGORY + " ("
					+ Category._ID + " INTEGER PRIMARY KEY," + Category.NAME
					+ " TEXT NOT NULL)";
			db.execSQL(tableStr);

		}

		private void createCollectSmsTable(SQLiteDatabase db) {
			String tableStr = "CREATE TABLE " + TABLE_COLLECT_SMS + " ("
					+ CollectSms._ID + " INTEGER PRIMARY KEY,"
					+ CollectSms.BODY + " TEXT NOT NULL," + CollectSms.ADDRESS
					+ " TEXT," + CollectSms.SUBJECT + " TEXT,"
					+ CollectSms.DATE + " DATE," + CollectSms.CATEGORYID
					+ " INTEGER)";
			db.execSQL(tableStr);
			
//			CategorySmsListItem item = new CategorySmsListItem();
//			
//			item.setmBody("dajiadfsfsafasfasdfasdfasdfasdhao");
//			item.setmCategoryId(1);
//			item.setmAddress("15000028317");
//			item.setInsertTime(InterceptDataBase.DF_DATE_MD.format(new Date()));
//	
//			
//			insertCategorySmsItem(item);
//			insertCategorySmsItem(item);
			
		}

		private void initAutoMsgTable(SQLiteDatabase db) {
			// ContentValues cv = new ContentValues();
			// DatabaseUtils.InsertHelper helper = new
			// DatabaseUtils.InsertHelper(db, TABLE_PRIVATE_AUTO_MSG);
			// String[] defaultAutoMsg =
			// mContext.getResources().getStringArray(R.array.default_auto_msg);
			// for(int i = 0; i < defaultAutoMsg.length; i++){
			// cv.put(PrivateAutoMsg.MESSAGE, defaultAutoMsg[i]);
			// if(i == 0){
			// cv.put(PrivateAutoMsg.USED, 1);
			// }else{
			// cv.put(PrivateAutoMsg.USED, 0);
			// }
			// helper.insert(cv);
			// }

		}

	}

	public static final class Category implements BaseColumns {
		public static final String NAME = "name";
	}

	public static final class CollectSms implements BaseColumns {
		public static final String BODY = "body";
		public static final String ADDRESS = "address";
		public static final String DATE = "date";
		public static final String SUBJECT = "subject";

		public static final String CATEGORYID = "category_id";

	}

}
