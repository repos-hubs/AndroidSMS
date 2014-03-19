/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import android.database.ContentObserver;
import android.os.Handler;

public class CallogObserver extends ContentObserver {
	private Handler mHandler;
	public static final int CALLOG_CHANGED = 9;

	public CallogObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
		this.mHandler = handler;
	}
	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);		
		mHandler.sendEmptyMessage(CALLOG_CHANGED);
		
	}
	@Override
	public boolean deliverSelfNotifications() {
		// TODO Auto-generated method stub
		return super.deliverSelfNotifications();
	}
	

}
