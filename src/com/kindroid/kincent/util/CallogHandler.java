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
import android.os.Message;
import android.provider.CallLog;
import android.util.Log;

import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

public class CallogHandler extends Handler {
	public static final String TAG = "CallogHandler";
	private Context mContext;
	public static final Uri CALLOG_URI = CallLog.Calls.CONTENT_URI;
	private static final String[] CALLOG_PROJECTION = new String[]{
		"date", "type"
 	};
	
	public CallogHandler(Context context){
		super();
		this.mContext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Log.d(TAG, "msg :" + msg);
		if(msg.what != CallogObserver.CALLOG_CHANGED){
			return;
		}
		int sum = PrivacyContactsActivity.sPrivacyContactsListItems.size();
		for (int i = 0; i < sum; i++) {
			PrivacyContactDataItem item = PrivacyContactsActivity.sPrivacyContactsListItems
					.get(i);
			PhoneType pt = ConvertUtils.getPhonetype(item.getPhoneNumber());
			Cursor localCursor = mContext.getContentResolver().query(CALLOG_URI, CALLOG_PROJECTION, "number LIKE '%" + pt.getPhoneNo() + "%'",
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
	}
	

}
