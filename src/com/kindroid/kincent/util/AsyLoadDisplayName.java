package com.kindroid.kincent.util;


import java.lang.ref.SoftReference;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;



public class AsyLoadDisplayName {
	
	private HashMap<String, SoftReference<String>> displaynamessmaps;
	private static AsyLoadDisplayName addAddress;

	public static AsyLoadDisplayName GetAddressBody() {
		if (addAddress == null) {
			addAddress = new AsyLoadDisplayName();
		}
		return addAddress;
	}

	public AsyLoadDisplayName() {
		displaynamessmaps = new HashMap<String, SoftReference<String>>();
	}
	
	public void remove(String key){
		if(displaynamessmaps.containsKey(key)){
			displaynamessmaps.remove(key);
		}
	}

	
	
	public String AsyDisplayName(final String id,final String address, final DisplayNameCallback addressCallback,final Context context) {
		if(address==null){
			return null;
		}
		if (displaynamessmaps.containsKey(address)) {
			SoftReference<String> softReference = displaynamessmaps.get(address);
			String str = softReference.get();
			if (str != null) {
				return str;
			}
		}
		
		final Handler handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if ((String) msg.obj != null) {
					displaynamessmaps.put(address, new SoftReference<String>((String) msg.obj));
					addressCallback.displayNameLoad((String) msg.obj);
				}
				return false;
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String str=MessengerUtils.getPersonName(context, id, address);
				Message ms = handler.obtainMessage(0, str);
				handler.sendMessage(ms);
			}
		}).start();
		return null;
	}
	
	
	

	
	public interface DisplayNameCallback {
		public void displayNameLoad(String name);
	}
	

}
