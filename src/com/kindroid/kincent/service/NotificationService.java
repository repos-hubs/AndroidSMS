/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:
 * Date:
 * Description:
 */

package com.kindroid.kincent.service;

import com.kindroid.kincent.notification.InterceptNotification;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class NotificationService extends Service {
	
	public static int showType=0;
	public static String content;
	
	
//	NetTrafficNotification mNetTrafficNotification;
	
    private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
//				mNetTrafficNotification.showNetTrafficNotification();
				break;
			case 1:
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				InterceptNotification notification=new InterceptNotification(getApplication(), NotificationService.this);
				notification.showInterceptNotification();
				break;
				
			case 2:
				String str=content;
				if(str!=null){
					content=null;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 notification=new InterceptNotification(getApplication(), NotificationService.this);
				 if(str!=null){
					 notification.notifyInstallation(NotificationService.this, str);

				 }
				 notification.showInterceptNotification();
				break;
				
			default:
				break;
			}
		}
    	
    };

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		handler.sendEmptyMessageDelayed(showType, 1);
		if(showType!=0){
			showType=0;
		}
	}
}
