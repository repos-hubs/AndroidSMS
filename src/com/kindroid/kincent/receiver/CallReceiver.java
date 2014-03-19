package com.kindroid.kincent.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivacyMessageDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.service.OnStartService;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.security.util.PhoneType;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

public class CallReceiver extends BroadcastReceiver {
	private Context context;
	private TelephonyManager tm;
	private long beginTime = 0;
	
	private static final Uri CALLOG_URI = CallLog.Calls.CONTENT_URI;
	private static final String[] CALLOG_PROJECTION = new String[]{
		"date", "type", "new"
 	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context;
		
		try {
			// String state =
			// intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			// 呼入的号码
			String phoneNumber = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			if(phoneNumber == null){
				return;
			}

			tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			System.out.println("tm state :" + tm.getCallState());
			System.out.println("phoneNumber :" + phoneNumber);
			switch (tm.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电响铃
				checkPrivacy(phoneNumber);
				// 挂断
				break;// 响铃
			case TelephonyManager.CALL_STATE_OFFHOOK: // 来电接通 去电拨出
				break;// 摘机
			case TelephonyManager.CALL_STATE_IDLE: // 来去电电话挂断
				deletePrivacyCallogs();
				break;// 挂机
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void endCall(String incomingNumber){
		TelephonyManager mTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
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
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(context);
		boolean autoSendMsg = sh.getBoolean(com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_AUTO_SEND_MSG, false);
		if(autoSendMsg){
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(context);
			String autoMsg = pdbUtils.getAutoMsg();
			if(autoMsg != null){
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(incomingNumber, null, autoMsg, null, null);
			}
		}
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
					SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(context);
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
				long currentTime = System.currentTimeMillis();
				cv.put(PrivateDBHelper.PrivateCllIn.NAME, SafeUtils.getEncStr(context, item.getContactName()));
				cv.put(PrivateDBHelper.PrivateCllIn.NUMBER, SafeUtils.getEncStr(context, item.getPhoneNumber()));
				cv.put(PrivateDBHelper.PrivateCllIn.DATE, currentTime);
				cv.put(PrivateDBHelper.PrivateCllIn.BLOCKED_TYPE, item.getBlockedType());
				PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(context);
				pdbUtils.addCallIn(context, cv);
				
				Intent mIntent = new Intent(com.kindroid.kincent.util.Constant.PRIVACY_CALL_BROADCAST_ACTION);
				mIntent.putExtra(PrivateDBHelper.PrivateCllIn.NAME, item.getContactName());
				mIntent.putExtra(PrivateDBHelper.PrivateCllIn.NUMBER, item.getPhoneNumber());
				mIntent.putExtra(PrivateDBHelper.PrivateCllIn.DATE, currentTime);
				mIntent.putExtra(PrivateDBHelper.PrivateCllIn.BLOCKED_TYPE, item.getBlockedType());		
				
				context.sendBroadcast(mIntent);
				break;
			}
			
		}
	}
	private void updateNotification(){
		PrivacyNotification mPrivacyNotification = KindroidMessengerApplication.getPrivacyNotification(context);
		mPrivacyNotification.updateCallNotification();
	}
	private void deletePrivacyCallogs(){
		int sum = PrivacyContactsActivity.sPrivacyContactsListItems.size();
		for (int i = 0; i < sum; i++) {
			PrivacyContactDataItem item = PrivacyContactsActivity.sPrivacyContactsListItems
					.get(i);
			PhoneType pt = ConvertUtils.getPhonetype(item.getPhoneNumber());
			Cursor localCursor = context.getContentResolver().query(CALLOG_URI, CALLOG_PROJECTION, "number LIKE '%" + pt.getPhoneNo() + "%'",
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
				context.getContentResolver().delete(CALLOG_URI, "number LIKE '%" + pt.getPhoneNo() + "%'", null);
			}
		}
	}

}
