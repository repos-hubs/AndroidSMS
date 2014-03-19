/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.theme;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.util.ArrayList;
import java.util.List;

public class ThemeUtils {
	public static final int THEME_VERSION_CONTROL = 0;//主题版本和主应用版本匹配
	public static final int THEME_VERSION_LOW = 1;//主题版本低于主应用的版本要求
	public static final int THEME_VERSION_HIGHT = 2;//主题版本高于主应用的版本要求

	public static int checkThemeVersion(ThemeMeta themeMeta, AppMeta appMeta){
		if(themeMeta == null){
			return 1;
		}
		if(appMeta == null){
			return 2;
		}
		int requiredThemeVersion = appMeta.getRequiredThemeVersionCode();
		int requiredAppVersion = themeMeta.getRequiredAppVersionCode();
		int themeVersion = themeMeta.getVersionCode();
		int appVersion = appMeta.getVersionCode();
		
		if(themeVersion < requiredThemeVersion){
			return THEME_VERSION_LOW;
		}else if(appVersion < requiredAppVersion){
			return THEME_VERSION_HIGHT;
		}else{
			return THEME_VERSION_CONTROL;
		}
	}
	public static List<PackageInfo> getInstalledThemePackages(Context context){
		List<PackageInfo> installedThemePkgs = new ArrayList<PackageInfo>();
		List<PackageInfo> installedPkgs = context.getPackageManager().getInstalledPackages(0);
		for(PackageInfo packageInfo : installedPkgs){
			String pkgName = packageInfo.packageName;
			if(pkgName != null && pkgName.startsWith(ThemeRegistry.THEME_SCHEME_PREFIX)){
				installedThemePkgs.add(packageInfo);
			}
		}
		return installedThemePkgs;
	}
}
