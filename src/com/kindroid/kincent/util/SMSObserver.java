/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;

public class SMSObserver extends ContentObserver {
	public static final String TAG = "SMSObserver";

	private ContentResolver mResolver;
	private Handler mHandler;
	
	public static final int SMS_DATABASE_CHANGED = 9;

	public SMSObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
		mHandler = handler;
	}

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		mHandler.sendEmptyMessage(SMS_DATABASE_CHANGED);
	}

}
