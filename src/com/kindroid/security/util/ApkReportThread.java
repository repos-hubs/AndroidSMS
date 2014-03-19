/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:	heli.zhao
 * Date: 2011-12
 * Description:
 */

package com.kindroid.security.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.kindroid.kincent.util.SafeUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;

public class ApkReportThread extends Thread {
	private static final int MARKET_ID = Config.MARKET_ID;
	private Context mContext;
	
	public ApkReportThread(Context context){
		this.mContext = context;
	}
	
	public void run(){
		if(!SafeUtils.checkNetwork(mContext)){
			return;
		}
		TelephonyManager telephonyManager=(TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imei=telephonyManager.getDeviceId();
		PackageManager pm = this.mContext.getPackageManager();
		try{			
			JSONObject param = new JSONObject();
			param.put("market_id", MARKET_ID);
			param.put("product_id", Constant.MESSENGER_PRODUCT_ID);
			param.put("manuer", Build.MANUFACTURER);
			param.put("model", Build.MODEL);
			param.put("build", Build.DISPLAY);
			param.put("fingerprint", Build.FINGERPRINT);
			param.put("imei", imei);
			param.put("host", Build.HOST);
			param.put("device", Build.DEVICE);
			param.put("brand", Build.BRAND);			
			param.put("board", Build.BOARD);
			param.put("osversion", Build.VERSION.RELEASE);
			param.put("sdkversion", Build.VERSION.SDK_INT);
			if(Build.VERSION.SDK_INT >= 8){
				param.put("hardware", Build.HARDWARE);
			}else{
				param.put("hardware", "unknown");
			}
			
			try{
				PackageInfo packageInfo = pm.getPackageInfo("com.kindroid.kincent", PackageManager.GET_SIGNATURES);
				param.put("version", packageInfo.versionName);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
			if(tm.getSubscriberId() != null){
				param.put("sim", tm.getSubscriberId());
			}else{
				param.put("sim", "");
			}
			
			Base64Handler base64 = new Base64Handler();
			HashMap<String, String> postParam = new HashMap<String, String>();
			System.out.println("request :" + param.toString());
			postParam.put("info", param.toString());
			HttpRequestUtil.postData(
					com.kindroid.security.util.Constant.REPORT_URL, postParam, 10000);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
