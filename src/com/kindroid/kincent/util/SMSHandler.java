/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kindroid.android.provider.Telephony;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

public class SMSHandler extends Handler {
	public static final String TAG = "SMSHandler";
	private Context mContext;

	public static final Uri SMS_URI = Telephony.Sms.CONTENT_URI;
	private static final String[] SMS_PROJECTION = new String[] { "date",
			"read", "type", "subject", "body" };

	public SMSHandler(Context context) {
		super();
		this.mContext = context;
	}
	@Override
	public void handleMessage(Message message) {
		Log.i(TAG, "handleMessage: " + message);
		if(message.what != SMSObserver.SMS_DATABASE_CHANGED){
			return;
		}
		int sum = PrivacyContactsActivity.sPrivacyContactsListItems.size();
		for (int i = 0; i < sum; i++) {
			PrivacyContactDataItem item = PrivacyContactsActivity.sPrivacyContactsListItems
					.get(i);
			PhoneType pt = ConvertUtils.getPhonetype(item.getPhoneNumber());
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

			sum = PrivacyContactsActivity.sPrivacyContactsListItems.size();
		}
	}
}
