package com.kindroid.kincent.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.service.OnStartService;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

import java.lang.reflect.Method;
import java.util.List;

public class PhoneReceiver extends BroadcastReceiver {
	private Context mContext;
	private TelephonyManager mTelephonyManager;
	
	private static final Uri CALLOG_URI = CallLog.Calls.CONTENT_URI;
	private static final String[] CALLOG_PROJECTION = new String[]{
		"date", "type"
 	};

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.mContext = context;
		if (intent.getAction()
				.equals("android.intent.action.NEW_OUTGOING_CALL")) {
			try {
				String number = intent
						.getStringExtra("android.intent.extra.PHONE_NUMBER");
				if (number == null) {
					return;
				}
				checkPrivacyCall(number);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else if (intent.getAction().equals(
				TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
			try {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				switch (tm.getCallState()) {
				case TelephonyManager.CALL_STATE_IDLE:
					deletePrivacyCallogs();
				
					break;
				
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}
	private void deletePrivacyCallogs(){
		int sum = PrivacyContactsActivity.sPrivacyContactsListItems.size();
		for (int i = 0; i < sum; i++) {
			PrivacyContactDataItem item = PrivacyContactsActivity.sPrivacyContactsListItems
					.get(i);
			PhoneType pt = ConvertUtils.getPhonetype(item.getPhoneNumber());
			Cursor localCursor = mContext.getContentResolver().query(CALLOG_URI, CALLOG_PROJECTION, "number LIKE '%" + pt.getPhoneNo() + "%'",
					null, null);
			if(localCursor != null && localCursor.getCount() > 0){
				mContext.getContentResolver().delete(CALLOG_URI, "number LIKE '%" + pt.getPhoneNo() + "%'", null);
			}
		}
	}
	private void checkPrivacyCall(String phoneNumber){
		List<PrivacyContactDataItem> mItems = PrivacyContactsActivity.sPrivacyContactsListItems;
		int sum = mItems.size();
		for(int i = 0; i < sum; i++){
			PrivacyContactDataItem item = mItems.get(i);
			PhoneType pt = ConvertUtils.getPhonetype(phoneNumber);
			if(item.getPhoneNumber().contains(pt.getPhoneNo())){
				ContentValues cv = new ContentValues();
				cv.put(PrivateDBHelper.PrivateCallOut.NAME, SafeUtils.getEncStr(mContext, item.getContactName()));
				cv.put(PrivateDBHelper.PrivateCallOut.NUMBER, SafeUtils.getEncStr(mContext, item.getPhoneNumber()));
				cv.put(PrivateDBHelper.PrivateCallOut.DATE, System.currentTimeMillis());
				cv.put(PrivateDBHelper.PrivateCallOut.BLOCKED_TYPE, item.getBlockedType());
				PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
				pdbUtils.addCallOut(mContext, cv);	
				break;
			}
			
		}
	}
	private void updateNotification(){
		PrivacyNotification mPrivacyNotification = KindroidMessengerApplication.getPrivacyNotification(mContext);
		mPrivacyNotification.updateCallNotification();
	}
	private void endCall(String incomingNumber){
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
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		boolean autoSendMsg = sh.getBoolean(Constant.SHARE_PREFS_PRIVACY_AUTO_SEND_MSG, false);
		if(autoSendMsg){
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
			String autoMsg = pdbUtils.getAutoMsg();
			if(autoMsg != null){
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(incomingNumber, null, autoMsg, null, null);
			}
		}
	}

}
