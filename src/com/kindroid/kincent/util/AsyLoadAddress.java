package com.kindroid.kincent.util;


import java.lang.ref.SoftReference;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;



public class AsyLoadAddress {
	
	private HashMap<String, SoftReference<String>> addressmaps;
	private static AsyLoadAddress addAddress;

	public static AsyLoadAddress GetAddressBody() {
		if (addAddress == null) {
			addAddress = new AsyLoadAddress();
		}
		return addAddress;
	}

	public AsyLoadAddress() {
		addressmaps = new HashMap<String, SoftReference<String>>();
	}
	
	public void remove(String key){
		if(addressmaps.containsKey(key)){
			addressmaps.remove(key);
		}
	}
	public String AsyAddress(final String address, final AddressCallback addressCallback,final Context context) {
		if(address==null){
			return null;
		}
		if (addressmaps.containsKey(address)) {
			SoftReference<String> softReference = addressmaps.get(address);
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
					addressmaps.put(address, new SoftReference<String>((String) msg.obj));
					addressCallback.addressLoad((String) msg.obj);
				}
				return false;
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String str=PhoneUtils.getAddressLocation(address, context);
				if(str==null){
					return;
				}
				Message ms = handler.obtainMessage(0, str);
				handler.sendMessage(ms);
			}
		}).start();
		return null;
	}

	
	

	
	public interface AddressCallback {
		public void addressLoad(String address);
	}
	

	

}
