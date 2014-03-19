/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.theme;

import android.content.Context;

public interface AppMeta {
	public static final String THEME_VERSION_FILE = "themeVersion";
	
	public static final String REQUIRED_THEME_VERSION_CODE = "requiredThemeVersionCode";
	
	public String getName();
	public String getVersionName();
	public int getVersionCode();
	public String getPackageName();
	public Context getAppContext();
	public int getRequiredThemeVersionCode();
	
}
