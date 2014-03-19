/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.exception.CrashHandler;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.receiver.CallReceiver;
import com.kindroid.kincent.receiver.KindroidRemoteSecurityReceiver;
import com.kindroid.kincent.receiver.PhoneReceiver;
import com.kindroid.kincent.receiver.SmsReceiver;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.kincent.util.CallogHandler;
import com.kindroid.kincent.util.CallogObserver;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SMSHandler;
import com.kindroid.kincent.util.SMSObserver;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

import java.lang.reflect.Method;
import java.util.List;

public class OnStartService extends Service {
	private ContentObserver mSmsObserver;
	private ContentObserver mCallogObserver;
	
	private BroadcastReceiver mSmsReceiver;
	private BroadcastReceiver mPhoneReceiver;
	private CallReceiver mCallReceiver;
	private BroadcastReceiver mRemoteSecurityReceiver;
	
	private static final Uri CALLOG_URI = CallLog.Calls.CONTENT_URI;
	private static final String[] CALLOG_PROJECTION = new String[]{
		"date", "type", "new"
 	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initPrivacyContactsList();
		addReceiver();
		addContentObserver();
//		addTelStateListener();
		registerCrashHandler();
//		SmsReceiver.spamProbProps = SmsUtils.loadProbProps(this);		
	}
	private void addTelStateListener() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		TelStateListener tsl = new TelStateListener();
		tm.listen(tsl, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	private void addContentObserver(){
		 ContentResolver resolver = getContentResolver();
		 Handler handler = new SMSHandler(this);
		 mSmsObserver = new SMSObserver(handler);
		 resolver.registerContentObserver(SMSHandler.SMS_URI, true, mSmsObserver);
		 
//		 handler = new CallogHandler(this);
//		 mCallogObserver = new CallogObserver(handler);
//		 resolver.registerContentObserver(CallogHandler.CALLOG_URI, true, mCallogObserver);
		 
	}
	private void initPrivacyContactsList(){
		if(PrivacyContactsActivity.sPrivacyContactsListItems == null){
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(this);
			PrivacyContactsActivity.sPrivacyContactsListItems = pdbUtils.getAllContacts(this);
		}
	}
	private void addReceiver(){
		IntentFilter it = new IntentFilter();
		it.setPriority(2147483647);
		it.addAction("android.provider.Telephony.SMS_RECEIVED");
		it.addAction("android.intent.action.DATA_SMS_RECEIVED");
		it.addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
		it.addAction("android.provider.Telephony.SMS_REJECTED");
		it.addAction(Constant.KINDROID_SECURITY_INTERCEPT_BROADCAST);
		mSmsReceiver = new SmsReceiver();
		registerReceiver(mSmsReceiver, it);
		
		IntentFilter phone_it = new IntentFilter();
		phone_it.setPriority(2147483647);
		phone_it.addAction("android.intent.action.SIM_STATE_CHANGED");
		phone_it.addAction("android.intent.action.ANY_DATA_STATE");
		phone_it.addAction("android.intent.action.RADIO_TECHNOLOGY");
		phone_it.addAction(TelephonyManager.EXTRA_STATE_RINGING);
		phone_it.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		mCallReceiver = new CallReceiver();
		registerReceiver(mCallReceiver, phone_it);
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		localIntentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		localIntentFilter.setPriority(2147483647);
		mPhoneReceiver = new PhoneReceiver();
		registerReceiver(mPhoneReceiver, localIntentFilter);
		IntentFilter remote_it = new IntentFilter();
		remote_it.setPriority(2147483647);
		remote_it.addAction(Constant.KINDROID_REMOTE_SECURITY_BROADCAST);
		mRemoteSecurityReceiver = new KindroidRemoteSecurityReceiver();
		registerReceiver(mRemoteSecurityReceiver, remote_it);
	}
	private void registerCrashHandler() {
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		crashHandler.sendPreviousReportsToServer();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mSmsObserver);		
//		getContentResolver().unregisterContentObserver(mCallogObserver);
		this.unregisterReceiver(mPhoneReceiver);
		this.unregisterReceiver(mSmsReceiver);
		this.unregisterReceiver(mCallReceiver);
	}
	private void endCall(String incomingNumber){
		TelephonyManager mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		boolean result = false;
		try {
			PhoneUtils.getITelephony(mTelephonyManager).endCall();// 静铃
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				Method getITelephonyMethod = TelephonyManager.class
						.getDeclaredMethod("getITelephony", (Class[]) null);
				getITelephonyMethod.setAccessible(true);
				
				Object mITelephony = getITelephonyMethod.invoke(
						mTelephonyManager, (Object[]) null);
				// 挂断 - mITelephony.endCall();
				mITelephony.getClass().getMethod("endCall", new Class[] {})
						.invoke(mITelephony, new Object[] {});
				// 取消通知 - mITelephony.cancelMissedCallsNotification()
				mITelephony
						.getClass()
						.getMethod("cancelMissedCallsNotification",
								new Class[] {})
						.invoke(mITelephony, new Object[] {});
				result = true;
			} catch (Exception ne) {
				e.printStackTrace();
			}
			e.printStackTrace();
		}
		if(result){
			sendAutoMsg(incomingNumber);
			//update notification
			
		}
	}
	private void sendAutoMsg(String incomingNumber){
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(this);
		boolean autoSendMsg = sh.getBoolean(Constant.SHARE_PREFS_PRIVACY_AUTO_SEND_MSG, false);
		if(autoSendMsg){
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(this);
			String autoMsg = pdbUtils.getAutoMsg();
			if(autoMsg != null){
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(incomingNumber, null, autoMsg, null, null);
			}
		}
	}
	private void updateNotification(){
		PrivacyNotification mPrivacyNotification = KindroidMessengerApplication.getPrivacyNotification(this);
		mPrivacyNotification.updateCallNotification();
	}
	private void checkPrivacy(String incomingNumber){
		List<PrivacyContactDataItem> mItems = PrivacyContactsActivity.sPrivacyContactsListItems;
		int sum = mItems.size();
		for(int i = 0; i < sum; i++){
			PrivacyContactDataItem item = mItems.get(i);
			PhoneType pt = ConvertUtils.getPhonetype(incomingNumber);
			if(item.getPhoneNumber().contains(pt.getPhoneNo())){
				if(item.getBlockedType() == 1){
					endCall(incomingNumber);
					SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(OnStartService.this);
					boolean notiforNew = sh.getBoolean(com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_NOTIFY_FOR_NEW, true);
					if(notiforNew){
						updateNotification();
//						boolean vibrate = sh.getBoolean(Constant.SHARE_PREFS_PRIVACY_VIBRATE_NOTIFY, false);
//						if(vibrate){
//							Vibrator  vibrator = (Vibrator)getApplication().getSystemService(Service .VIBRATOR_SERVICE);
//							vibrator.vibrate( new long[]{100,10,100,1000},-1);
//							vibrator.cancel();
//						}
					}
				}
				ContentValues cv = new ContentValues();
				cv.put(PrivateDBHelper.PrivateCllIn.NAME, SafeUtils.getEncStr(this, item.getContactName()));
				cv.put(PrivateDBHelper.PrivateCllIn.NUMBER, SafeUtils.getEncStr(this, item.getPhoneNumber()));
				cv.put(PrivateDBHelper.PrivateCllIn.DATE, System.currentTimeMillis());
				cv.put(PrivateDBHelper.PrivateCllIn.BLOCKED_TYPE, item.getBlockedType());
				PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(this);
				pdbUtils.addCallIn(this, cv);	
				break;
			}
			
		}
	}
	private void deletePrivacyCallogs(){
		int sum = PrivacyContactsActivity.sPrivacyContactsListItems.size();
		for (int i = 0; i < sum; i++) {
			PrivacyContactDataItem item = PrivacyContactsActivity.sPrivacyContactsListItems
					.get(i);
			PhoneType pt = ConvertUtils.getPhonetype(item.getPhoneNumber());
			Cursor localCursor = getContentResolver().query(CALLOG_URI, CALLOG_PROJECTION, "number LIKE '%" + pt.getPhoneNo() + "%'",
					null, null);			
			if(localCursor != null && localCursor.getCount() > 0){				
				while(localCursor.moveToNext()){
					int isNew = localCursor.getInt(localCursor.getColumnIndex(CallLog.Calls.NEW));
					int type = localCursor.getInt(localCursor.getColumnIndex(CallLog.Calls.TYPE));
					System.out.println("isNew :" + isNew);
					if(isNew == 1 || type == CallLog.Calls.MISSED_TYPE){
						updateNotification();
					}
				}
				getContentResolver().delete(CALLOG_URI, "number LIKE '%" + pt.getPhoneNo() + "%'", null);
			}
		}
	}
	private class TelStateListener extends PhoneStateListener {		

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch(state){
			case TelephonyManager.CALL_STATE_RINGING:
				checkPrivacy(incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				deletePrivacyCallogs();
				break;
				
			}
			
		}

	}

}
