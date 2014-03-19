package com.kindroid.kincent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.security.util.Constant;

public class KindroidRemoteSecurityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub		
		boolean enableRemoteSecurity = intent.getBooleanExtra(com.kindroid.kincent.util.Constant.KINDROID_REMOTE_SECURITY_ENABLE, false);
		
		if(enableRemoteSecurity){
			String secNumber = intent.getStringExtra(com.kindroid.kincent.util.Constant.KINDROID_REMOTE_SECURITY_NUMBER);
			if(TextUtils.isEmpty(secNumber)){
				return;
			}
			SharedPreferences prefs = KindroidMessengerApplication.getSharedPrefs(context);
			Editor editor = prefs.edit();
			editor.putString(Constant.SHAREDPREFERENCES_SAFEMOBILENUMBER, secNumber);
			editor.putBoolean(Constant.SHAREDPREFERENCES_REMOTESECURITY, true);
			editor.commit();
		}else{
			SharedPreferences prefs = KindroidMessengerApplication.getSharedPrefs(context);
			Editor editor = prefs.edit();
			editor.putBoolean(Constant.SHAREDPREFERENCES_REMOTESECURITY, false);
			editor.commit();
		}
	}

}
