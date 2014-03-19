/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.theme;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.kindroid.theme.ThemeMeta;

import java.util.List;

public class MessengerThemeMeta implements ThemeMeta {
	private String mName;
	//id is the theme folder name
	private String mId;
	private String mPackageName;
	private String mVersionName;
	private int mVersionCode;
	private int mRequiredAppVersionCode;
	private String mDesp;
	private Context mThemeContext;
	private Drawable mThemeIcon;
	private boolean isDefaultTheme;
	
	public void setDefaultTheme(boolean flag){
		this.isDefaultTheme = flag;
	}
	public boolean isDefaultTheme(){
		return this.isDefaultTheme;
	}
	
	public void setThemeIcon(Drawable icon){
		this.mThemeIcon = icon;
	}
	public Drawable getThemeIcon(){
		return this.mThemeIcon;
	}
	
	public void setName(String name){
		this.mName = name;
	}
	public void setId(String id){
		this.mId = id;
	}
	public void setPackageName(String packageName){
		this.mPackageName = packageName;
	}
	public void setVersionName(String versionName){
		this.mVersionName = versionName;
	}
	public void setVersionCode(int versionCode){
		this.mVersionCode = versionCode;
	}
	public void setRequiredAppVersionCode(int requiredAppVersionCode){
		this.mRequiredAppVersionCode = requiredAppVersionCode;
	}
	public void setDesp(String desp){
		this.mDesp = desp;
	}
	public void setThemeContext(Context context){
		this.mThemeContext = context;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.mName;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.mId;
	}

	@Override
	public String getPackageName() {
		// TODO Auto-generated method stub
		return this.mPackageName;
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
	public int getRequiredAppVersionCode() {
		// TODO Auto-generated method stub
		return this.mRequiredAppVersionCode;
	}

	@Override
	public String getDesp() {
		// TODO Auto-generated method stub
		return this.mDesp;
	}

	@Override
	public Context getThemeContext() {
		// TODO Auto-generated method stub
		return this.mThemeContext;
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof ThemeMeta){
			ThemeMeta other = (ThemeMeta)o;
			try{
				String otherPackageName = other.getPackageName();
				String otherId = other.getId();
				if(otherPackageName.equals(getPackageName()) && otherId.equals(getId())){
					return true;
				}else{
					return false;
				}
			}catch(Exception e){
				return false;
			}
			
		}else{
			return false;
		}		
	}
	
}
