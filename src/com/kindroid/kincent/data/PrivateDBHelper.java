/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.kindroid.android.provider.Telephony.BaseMmsColumns;
import com.kindroid.kincent.R;

public class PrivateDBHelper extends SQLiteOpenHelper {
	private Context mContext;
	//privacy database name cant contain privacy word
	private static final String DATABASE_NAME = "messengersms";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_PRIVATE_MESSAGE = "pri_message";
	public static final String TABLE_PRIVATE_MMS_IN = "pri_mms_in";
	public static final String TABLE_PRIVATE_MMS_OUT = "pri_mms_out";
	public static final String TABLE_PRIVATE_CONTACTS = "pri_contacts";
	public static final String TABLE_PRIVATE_CALL_IN = "pri_call_in";
	public static final String TABLE_PRIVATE_CALL_OUT = "pri_call_out";
	public static final String TABLE_PRIVATE_AUTO_MSG = "pri_auto_message";
	
	private static PrivateDBHelper mInstance = null;
	
	public synchronized static PrivateDBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new PrivateDBHelper(context);
		}
		return mInstance;
	}
	
	public PrivateDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	private PrivateDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createPrivateCallOutTable(db);
		createPrivateCallInTable(db);
		createPrivateContactsTable(db);
		createPrivateMmsOutTable(db);			
		createPrivateMmsInTable(db);			
		createPrivateMsgTable(db);
		createPrivateAutoMsgTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	private void createPrivateCallInTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_PRIVATE_CALL_IN + " (_id INTEGER PRIMARY KEY,name TEXT,pre_number TEXT,number TEXT,date INTEGER,blocked_type INTEGER,new INTEGER, type INTEGER);");
	}
	private void createPrivateCallOutTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_PRIVATE_CALL_OUT + " (_id INTEGER PRIMARY KEY,name TEXT,pre_number TEXT,number TEXT,date INTEGER,blocked_type INTEGER);");
	}
	private void createPrivateContactsTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_PRIVATE_CONTACTS + 
					" (_id INTEGER PRIMARY KEY,contact_name TEXT,pre_number TEXT,phone_number TEXT,blocked_type INTEGER);");
	}
	private void createPrivateMmsOutTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_PRIVATE_MMS_OUT + " (_id INTEGER PRIMARY KEY,name TEXT,pre_address TEXT,address TEXT,date INTEGER,subject TEXT,body TEXT);");
	}
	private void createPrivateMmsInTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_PRIVATE_MMS_IN + " (_id INTEGER PRIMARY KEY,name TEXT,pre_address TEXT,address TEXT,date INTEGER,subject TEXT,body TEXT);");
	}
	private void createPrivateMsgTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_PRIVATE_MESSAGE + " (_id INTEGER PRIMARY KEY,name TEXT,pre_address TEXT,address TEXT,date INTEGER,subject TEXT,body TEXT,mms_recv_type INTEGER,mms_type INTEGER,read INTEGER);");
	}
	private void createPrivateAutoMsgTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + TABLE_PRIVATE_AUTO_MSG + " (_id INTEGER PRIMARY KEY,msg TEXT, used INTEGER);");
		initAutoMsgTable(db);
	}
	private void initAutoMsgTable(SQLiteDatabase db){
		ContentValues cv = new ContentValues();
		DatabaseUtils.InsertHelper helper = new DatabaseUtils.InsertHelper(db, TABLE_PRIVATE_AUTO_MSG);
		String[] defaultAutoMsg = mContext.getResources().getStringArray(R.array.default_auto_msg);
		for(int i = 0; i < defaultAutoMsg.length; i++){
			cv.put(PrivateAutoMsg.MESSAGE, defaultAutoMsg[i]);
			if(i == 0){
				cv.put(PrivateAutoMsg.USED, 1);	
			}else{
				cv.put(PrivateAutoMsg.USED, 0);	
			}
			helper.insert(cv);
		}

	}
	public static final class PrivateMsg implements BaseColumns{
		public static final String NAME = "name";
		public static final String PRE_ADDRESS = "pre_address";
		public static final String ADDRESS = "address";
		public static final String DATE = "date";
		public static final String SUBJECT = "subject";
		public static final String BODY = "body";
		public static final String MMS_RECV_TYPE = "mms_recv_type";
		public static final String MMS_TYPE = "mms_type";
		public static final String READ = "read";
	}
	public static final class PrivateMmsIn implements BaseColumns{
		public static final String NAME = "name";
		public static final String PRE_ADDRESS = "pre_address";
		public static final String ADDRESS = "address";
		public static final String DATE = "date";
		public static final String SUBJECT = "subject";
		public static final String BODY = "body";
	}
	public static final class PrivateMmsOut implements BaseColumns{
		public static final String NAME = "name";
		public static final String PRE_ADDRESS = "pre_address";
		public static final String ADDRESS = "address";
		public static final String DATE = "date";
		public static final String SUBJECT = "subject";
		public static final String BODY = "body";
	}
	public static final class PrivateCllIn implements BaseColumns{
		public static final String NAME = "name";
		public static final String PRE_NUMBER = "pre_number";
		public static final String NUMBER = "number";
		public static final String DATE = "date";
		public static final String BLOCKED_TYPE = "blocked_type";
		public static final String NEW = "new";
		public static final String TYPE = "type";
	}
	public static final class PrivateCallOut implements BaseColumns{
		public static final String NAME = "name";
		public static final String PRENUMBER = "pre_number";
		public static final String NUMBER = "number";
		public static final String DATE = "date";
		public static final String BLOCKED_TYPE = "blocked_type";
	}
	public static final class PrivateContact implements BaseColumns {
		public static final String CONTACT_NAME = "contact_name";
		public static final String PRE_NUMBER = "pre_number";
		public static final String PHONE_NUMBER = "phone_number";
		public static final String BLOCKED_TYPE = "blocked_type";
		
	}
	public static final class PrivateAutoMsg implements BaseColumns {
		public static final String MESSAGE = "msg";
		public static final String USED = "used";
	}
	
}
