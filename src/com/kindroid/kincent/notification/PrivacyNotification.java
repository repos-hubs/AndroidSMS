/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.ui.PrivacyChangeCallInNotifyIconDialog;
import com.kindroid.kincent.ui.PrivacyChangeMsgNotifyIconDialog;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;

public class PrivacyNotification {
	Context mContext;
	private NotificationManager mNotificationMgr;
	private boolean showCallNotification;
	private boolean showMmsNotification;
	
	public static final int MMS_NOTIFICATION = 1099;
	public static final int CALL_NOTIFICATION = 2099;
	
	public PrivacyNotification(Context context){
		this.mContext = context;
		mNotificationMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void updateMmsNotification(){
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		int notifIconIndex = sh.getInt(Constant.SHARE_PREFS_PRIVACY_MSG_NOTIFY_ICON, 0);
		String notifText = sh.getString(Constant.SHARE_PREFS_PRIVACY_NOTIFY_TEXT, mContext.getString(R.string.privacy_default_notify_text));
		int notifIconId = mContext.getResources().getIdentifier(
				PrivacyChangeMsgNotifyIconDialog.PRIVACY_MSG_NOTIFY_ICON_NAME_PREF + notifIconIndex, "drawable",
				mContext.getPackageName());	
		if(notifIconId == 0){
			notifIconId = R.drawable.privacy_msg_notify_icon0;
		}
		Notification notification = new Notification(
				notifIconId,
				notifText,
				System.currentTimeMillis());
		
		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
		
		notification.contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.privacy_mms_notification);
		
		notification.contentView.setImageViewResource(R.id.privacy_mms_notification_iv, notifIconId);
		notification.contentView.setTextViewText(R.id.privacy_mms_notification_tv, notifText);
		Intent intent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		
		mNotificationMgr.notify(MMS_NOTIFICATION, notification);
	}
	public void cancelMmsNotification(){
		mNotificationMgr.cancel(MMS_NOTIFICATION);
	}
	public void updateCallNotification(){
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		int notifIconIndex = sh.getInt(Constant.SHARE_PREFS_PRIVACY_CALL_IN_NOTIFY_ICON, 0);
		int notifIconId = mContext.getResources().getIdentifier(
				PrivacyChangeCallInNotifyIconDialog.PRIVACY_CALL_IN_NOTIFY_ICON_NAME_PREF + notifIconIndex, "drawable",
				mContext.getPackageName());
		if(notifIconId == 0){
			notifIconId = R.drawable.privacy_call_in_notify_icon0;
		}
		Notification notification = new Notification(
				notifIconId,
				"",
				System.currentTimeMillis());
		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
		
		notification.contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.privacy_mms_notification);
		
		notification.contentView.setImageViewResource(R.id.privacy_mms_notification_iv, notifIconId);
		Intent intent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		
		mNotificationMgr.notify(CALL_NOTIFICATION, notification);
	}
	public void cancelCallNotifcation(){
		mNotificationMgr.cancel(CALL_NOTIFICATION);
	}
	
}
