/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;

import com.kindroid.android.provider.Telephony;
import com.kindroid.kincent.adapter.PrivacyContactImportAdapter;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

public class PrivacyContactImportDataThread extends Thread {
	private Context mContext;
	private Handler mHandler;
	private PrivacyContactImportAdapter mAdapter;
	private boolean mImportData;
	
	private static final Uri SMS_URI = Telephony.Sms.CONTENT_URI;
	private static final String[] SMS_PROJECTION = new String[]{
		"date", "read", "type", "subject", "body"
	};
	
	private static final Uri CALLOG_URI = CallLog.Calls.CONTENT_URI;
	private static final String[] CALLOG_PROJECTION = new String[]{
		"date", "type"
 	};
	
	public static final int PRIVACY_CONTACT_IMPORT_FINISH = 9;
	public static final int PRIVACY_CONTACT_ADD_EXISTED = 10;

	public PrivacyContactImportDataThread(Context context, Handler handler, PrivacyContactImportAdapter mAdapter
			, boolean importData){
		this.mContext = context;
		this.mHandler = handler;
		this.mAdapter = mAdapter;
		this.mImportData = importData;
	}
	private void importData(PrivacyContactDataItem item){
		PhoneType pt = ConvertUtils.getPhonetype(item.getPhoneNumber());		
		//import sms
		Cursor localCursor = mContext.getContentResolver().query(SMS_URI, SMS_PROJECTION, "address LIKE '%" + pt.getPhoneNo() + "%'",
				null, null);
		if(localCursor != null){
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
			while(localCursor.moveToNext()){
				ContentValues cv = new ContentValues();
				cv.put(PrivateDBHelper.PrivateMsg.NAME, SafeUtils.getEncStr(mContext, item.getContactName()));
				cv.put(PrivateDBHelper.PrivateMsg.ADDRESS, SafeUtils.getEncStr(mContext, item.getPhoneNumber()));
				long date = localCursor.getLong(localCursor.getColumnIndex("date"));
				cv.put(PrivateDBHelper.PrivateMsg.DATE, date);				
				int read = localCursor.getInt(localCursor.getColumnIndex("read"));
				cv.put(PrivateDBHelper.PrivateMsg.READ, read);
				int type = localCursor.getInt(localCursor.getColumnIndex("type"));
				cv.put(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE, type);
				String subject = localCursor.getString(localCursor.getColumnIndex("subject"));
				if(subject != null){
					cv.put(PrivateDBHelper.PrivateMsg.SUBJECT, SafeUtils.getEncStr(mContext, subject));
				}
				String body = localCursor.getString(localCursor.getColumnIndex("body"));
				if(body != null){
					cv.put(PrivateDBHelper.PrivateMsg.BODY, SafeUtils.getEncStr(mContext, body));
				}
				pdbUtils.addMessage(mContext, cv);
			}
			try{
				localCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			mContext.getContentResolver().delete(SMS_URI, "address LIKE '%" + pt.getPhoneNo() + "%'", null);
		}
		//import callog
		localCursor = mContext.getContentResolver().query(CALLOG_URI, CALLOG_PROJECTION, "number LIKE '%" + pt.getPhoneNo() + "%'",
				null, null);
		if(localCursor != null){
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
			while(localCursor.moveToNext()){
				long date = localCursor.getLong(localCursor.getColumnIndex("date"));
				int type = localCursor.getInt(localCursor.getColumnIndex("type"));
				ContentValues cv = new ContentValues();
				if(type == CallLog.Calls.INCOMING_TYPE){
					cv.put(PrivateDBHelper.PrivateCllIn.NAME, SafeUtils.getEncStr(mContext, item.getContactName()));
					cv.put(PrivateDBHelper.PrivateCllIn.NUMBER, SafeUtils.getEncStr(mContext, item.getPhoneNumber()));
					cv.put(PrivateDBHelper.PrivateCllIn.DATE, date);
					cv.put(PrivateDBHelper.PrivateCallOut.BLOCKED_TYPE, 0);
					pdbUtils.addCallIn(mContext, cv);
				}else if(type == CallLog.Calls.OUTGOING_TYPE){
					cv.put(PrivateDBHelper.PrivateCallOut.NAME, SafeUtils.getEncStr(mContext, item.getContactName()));
					cv.put(PrivateDBHelper.PrivateCallOut.NUMBER, SafeUtils.getEncStr(mContext, item.getPhoneNumber()));
					cv.put(PrivateDBHelper.PrivateCallOut.DATE, date);					
					pdbUtils.addCallOut(mContext, cv);
				}
			}
			mContext.getContentResolver().delete(CALLOG_URI, "number LIKE '%" + pt.getPhoneNo() + "%'", null);
		}
	}
	public void run(){
		PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);		
		for (int i = 0; i < mAdapter.getCount(); i++) {
			PrivacyContactDataItem item = (PrivacyContactDataItem) mAdapter
					.getItem(i);
			if (item.isSelected()) {
				int addResult = pdbUtils.addContact(mContext, item);
				if (addResult > 0) {
					item.setSelected(false);
					PrivacyContactsActivity.sPrivacyContactsListItems
							.add(item);
					if(mImportData){
						importData(item);
					}
				}else if(mAdapter.getCount() == 1 && addResult == 0){
					this.mHandler.sendEmptyMessage(PRIVACY_CONTACT_ADD_EXISTED);
					return;
				}
			}
			
		}
		this.mHandler.sendEmptyMessage(PRIVACY_CONTACT_IMPORT_FINISH);
	}
}
