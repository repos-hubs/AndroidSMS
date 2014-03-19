/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.theme;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.List;

public interface ThemeMeta{
	public static final String REQUIRED_APP_VERSION_CODE = "requiredAppVersionCode";
	public static final String DESP = "desp";
	public static final String NAME = "name";
	public static final String VERSION_CODE = "versionCode";
	public static final String VERSION_NAME = "versionName";
	
	public static final String ASSETS_ROOT_PATH = "assets";
	public static final String THEME_ROOT_PATH = "theme";
	public static final String THEME_META_FILE = "theme";
	public static final String THEME_SKIN_PATH = "skin";
	public static final String THEME_ICONS_PATH = "icons";
	public static final String THEME_ICON_FILE = "icon";
	public static final String XML_FILE_SUFFIX = ".xml";
	
	public String getName();
	public String getId();
	public String getPackageName();
	public String getVersionName();
	public int getVersionCode();
	public int getRequiredAppVersionCode();
	public String getDesp();
	public Context getThemeContext();
	public Drawable getThemeIcon();
	public boolean equals(Object o);
	public boolean isDefaultTheme();
}
