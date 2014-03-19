/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:
 * Date:
 * Description:
 */

package com.kindroid.kincent.notification;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.List;


import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.service.KindroidMmsService;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;


public class InterceptNotification {

	Context mContext;
	public NotificationManager mNotificationMgr;

	private KindroidMessengerApplication mApp;
	

	public InterceptNotification(Application application, Context ctx) {
		mContext = ctx;
		mNotificationMgr = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mApp = (KindroidMessengerApplication) application;
		
	}


	public void showInterceptNotification() {

		HistoryNativeCursor hnc = new HistoryNativeCursor();
	
		hnc.setmRequestType(3);
		int sms = InterceptDataBase.get(mContext).getHistoryNum(hnc);
		int num=MessengerUtils.getUnreadMessagesCount(mContext);
		
		if (sms == 0&&num==0) {
			mNotificationMgr.cancel(1212);
			return;
		}
		int drawableId =0;
		if(sms>0){
			
			drawableId=R.drawable.intercept_sms_icon;
		}else{
			drawableId=R.drawable.privacy_msg_notify_icon1;
		}
		
		
		Notification notification = new Notification(
				drawableId,
				mContext.getString(R.string.app_name),
				System.currentTimeMillis());
		notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.net_notification_intercept_layout);
		notification.contentView.setTextViewText(R.id.intercept_sum_tv, sms
				+ "");
		
		
		notification.contentView.setTextViewText(R.id.no_read_sum_tv, num
				+ "");
		

//		notification.contentView.setTextViewText(R.id.progressDescription,
//				mContext.getString(R.string.safe_your_mobile));

		Intent i = new Intent();
		ComponentName comp = new ComponentName(mContext.getPackageName(),
				mContext.getPackageName() + ".ui.AppEngBlockActivity");

		i.setComponent(comp);
		i.setAction("android.intent.action.VIEW");
		i.putExtra("type", 2);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pending = PendingIntent.getActivity(mContext, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = pending;
		mNotificationMgr.notify(1212, notification);
	}
	
	
	public void notifyInstallation(Context mContext, String content){
		NotificationManager localNotificationManager = (NotificationManager)mContext.getSystemService("notification");
	    Notification localNotification = new Notification(R.drawable.icon, content, System.currentTimeMillis());
//	    Intent localIntent1 = new Intent("android.intent.action.MAIN");
//	    localIntent1.addCategory("android.intent.category.HOME");
//	    Uri localUri = Uri.parse(PKG_INSTALL_NOTIFY_ID + "");
//	    localIntent1.setData(localUri);
//	    PendingIntent localPendingIntent = PendingIntent.getActivity(mContext, 0, localIntent1, PendingIntent.FLAG_CANCEL_CURRENT);
//	    Object[] arrayOfObject = new Object[1];
//	    arrayOfObject[0] = pkgName;
	    
	    
	    Intent i = new Intent();
		ComponentName comp = new ComponentName(mContext.getPackageName(),
				mContext.getPackageName() + ".ui.AppEngBlockActivity");

		i.setComponent(comp);
		i.setAction("android.intent.action.VIEW");
		i.putExtra("type", 2);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pending = PendingIntent.getActivity(mContext, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
	    
	    
	    
	    String str2 = mContext.getString(R.string.sms_content);
	    
	    localNotification.setLatestEventInfo(mContext, str2, content, pending);
	    localNotification.flags = localNotification.flags | Notification.FLAG_AUTO_CANCEL;
	    localNotificationManager.notify(1013, localNotification);
	}
	
	public void disNotification(){
		
		NotificationManager localNotificationManager = (NotificationManager)mContext.getSystemService("notification");
		localNotificationManager.cancel(1013);
		
	}
	
	
	
	
	


}
