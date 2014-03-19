/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.theme;

import android.content.Context;

import com.kindroid.theme.AppMeta;

public class MessengerAppMeta implements AppMeta {
	private String mName;
	private String mVersionName;
	private int mVersionCode;
	private String mPackageName;
	private Context mAppContext;
	private int mRequiredThemeVersionCode;
	
	public void setName(String name){
		this.mName = name;
	}
	public void setVersionName(String versionName){
		this.mVersionName = versionName;
	}
	public void setVersionCode(int versionCode){
		this.mVersionCode = versionCode;
	}
	public void setPackageName(String packageName){
		this.mPackageName = packageName;
	}
	public void setAppContext(Context context){
		this.mAppContext = context;
	}
	public void setRequiredThemeVersionCode(int requiredThemeVersionCode){
		this.mRequiredThemeVersionCode = requiredThemeVersionCode;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.mName;
	}

	@Override
	public String getVersionName() {
		// TODO Auto-generated method stub
		return this.mVersionName;
	}

	@Override
	public int getVersionCode() {
		// TODO Auto-generated method stub
		return this.mVersionCode;
	}

	@Override
	public String getPackageName() {
		// TODO Auto-generated method stub
		return this.mPackageName;
	}

	@Override
	public Context getAppContext() {
		// TODO Auto-generated method stub
		return this.mAppContext;
	}

	@Override
	public int getRequiredThemeVersionCode() {
		// TODO Auto-generated method stub
		return this.mRequiredThemeVersionCode;
	}

}
