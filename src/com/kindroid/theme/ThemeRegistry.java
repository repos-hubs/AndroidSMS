/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.theme;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.theme.MessengerAppMeta;
import com.kindroid.kincent.theme.MessengerThemeMeta;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyDBUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ThemeRegistry {
	private static final String TAG = "THEMEREG";
	private static ThemeMeta mCurrentTheme;
	private static List<ThemeMeta> mThemeList;
	private static AppMeta mAppMeta;
	private static ThemeRegistry mInstance = null;
	
	public static final String THEME_SCHEME_PREFIX = "com.kindroid.kincent.theme";
	
	private Context mAppContext;
	
	public static final String FILE_SEPARATOR = "/";
	
	private ThemeRegistry(Context appContext){
		this.mAppContext = appContext;
	}
	public ThemeMeta getCurrentTheme(){
		return mCurrentTheme;
	}
	public void setCurrentTheme(ThemeMeta themeMeta){
		mCurrentTheme = themeMeta;
	}
	public int getIconId(String iconName){
		Context themeContext = this.mCurrentTheme.getThemeContext();
		int iconId = themeContext.getResources().getIdentifier(
				iconName, "drawable",
				themeContext.getPackageName());
		return iconId;
	}
	public Drawable getIconDrawable(int iconId){
		Drawable ret = null;
		Context themeContext = this.mCurrentTheme.getThemeContext();
		try{
			ret = themeContext.getResources().getDrawable(iconId);
		}catch(Exception e){
			
		}
		return ret;
	}
	public View inflate(String layoutFile){
		Context themeContext = mCurrentTheme.getThemeContext();
		AssetManager am = themeContext.getResources().getAssets();
		LayoutInflater mInflator = LayoutInflater.from(themeContext);
		String layoutPath = ThemeMeta.ASSETS_ROOT_PATH + FILE_SEPARATOR + ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + mCurrentTheme.getId() + FILE_SEPARATOR + ThemeMeta.THEME_SKIN_PATH + FILE_SEPARATOR + layoutFile + ThemeMeta.XML_FILE_SUFFIX;
		View layoutView = null;
		try{
			layoutView = mInflator.inflate(am.openXmlResourceParser(layoutPath), null);
		}catch(FileNotFoundException e){
			
		}catch(Exception e){
			
		}
		return layoutView;
	}
	public View inflateAttach(String layoutFile, ViewGroup root, boolean attachToRoot){
		Context themeContext = mCurrentTheme.getThemeContext();
		AssetManager am = themeContext.getResources().getAssets();
		LayoutInflater mInflator = LayoutInflater.from(themeContext);
		String layoutPath = ThemeMeta.ASSETS_ROOT_PATH + FILE_SEPARATOR + ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + mCurrentTheme.getId() + FILE_SEPARATOR + ThemeMeta.THEME_SKIN_PATH + FILE_SEPARATOR + layoutFile;
		View layoutView = null;
		try{
//			layoutView = mInflator.inflate(am.openXmlResourceParser(layoutPath), null);
			layoutView = mInflator.inflate(am.openXmlResourceParser(layoutPath + ThemeMeta.XML_FILE_SUFFIX), root, attachToRoot);
		}catch(FileNotFoundException e){
			
		}catch(Exception e){
			
		}
		return layoutView;
	}
	
	
	
	
	public List<ThemeMeta> getThemeList(){		
		return mThemeList;
	}
	public static ThemeRegistry getInstance(Context appContext){
		if(mInstance == null){
			mInstance = new ThemeRegistry(appContext);
			mInstance.init(appContext);
		}		
		
		return mInstance;
	}
	public void init(Context context){
		if(mThemeList == null){
			mThemeList = new ArrayList<ThemeMeta>();
		}else{
			mThemeList.clear();
		}
		installTheme(context);
		addReceiver(context);
		new LoadThemeThread(context).start();
	}
	private void installTheme(Context context){
		mAppMeta = loadAppMeta(context);
		if(mAppMeta == null){
			ThemeMeta themeMeta = loadTheme(context, context.getPackageName(), Constant.THEME_ID_DEFAULT);
			if(themeMeta != null){
				mCurrentTheme = themeMeta;
				mThemeList.add(mCurrentTheme);
			}
		}else{
			SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(context);
			String themePackage = sh.getString(Constant.THEME_PACKAGE_USED, context.getPackageName());
			String themeId = sh.getString(Constant.THEME_ID_USED, Constant.THEME_ID_DEFAULT);
			ThemeMeta themeMeta = loadTheme(context, themePackage, themeId);
			if(themeMeta != null){
				if(ThemeUtils.checkThemeVersion(themeMeta, mAppMeta) == 0){
					mCurrentTheme = themeMeta;
					mThemeList.add(mCurrentTheme);
				}else{
					themeMeta = loadTheme(context, context.getPackageName(), Constant.THEME_ID_DEFAULT);
					if(themeMeta != null){
						mCurrentTheme = themeMeta;
						mThemeList.add(mCurrentTheme);
					}
				}
			}else{
				themeMeta = loadTheme(context, context.getPackageName(), Constant.THEME_ID_DEFAULT);
				if(themeMeta != null){
					mCurrentTheme = themeMeta;
					mThemeList.add(mCurrentTheme);
					Editor editor = sh.edit();
					editor.putString(Constant.THEME_PACKAGE_USED, context.getPackageName());
					editor.putString(Constant.THEME_ID_USED, Constant.THEME_ID_DEFAULT);
					editor.commit();
				}
			}
			
		}		
		
	}
	private AppMeta loadAppMeta(Context context){
		MessengerAppMeta appMeta = new MessengerAppMeta();
		appMeta.setAppContext(context);
		appMeta.setPackageName(context.getPackageName());
		ApplicationInfo appInfo = context.getApplicationInfo();
		PackageManager pm = context.getPackageManager();
		CharSequence label = appInfo.loadLabel(pm);
		if(label != null){
			appMeta.setName(label.toString());
		}else{
			appMeta.setName(appInfo.name);
		}
		try{
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
			appMeta.setVersionCode(pi.versionCode);
			appMeta.setVersionName(pi.versionName);
		}catch(Exception e){
			
		}
		AssetManager am = context.getResources().getAssets();
		String themeFilePath = ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + AppMeta.THEME_VERSION_FILE;
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(am.open(themeFilePath)));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			if(line != null){
				sb.append(line);
				line = br.readLine();
			}
			JSONObject json = new JSONObject(sb.toString());
			appMeta.setRequiredThemeVersionCode(json.getInt(AppMeta.REQUIRED_THEME_VERSION_CODE));
		}catch(Exception e){
			return null;
		}finally{
			if(br != null){
				try{
					br.close();
				}catch(Exception e){
					
				}
			}
		}
		return appMeta;
	}
	private ThemeMeta loadTheme(Context context, String packageName, String themeId){
		MessengerThemeMeta themeMeta =  new MessengerThemeMeta();		
		Context themeContext = null;
		try{
			themeContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);			
		}catch(Exception e){
			return null;			
		}		
		themeMeta.setId(themeId);
		themeMeta.setPackageName(packageName);
		themeMeta.setThemeContext(themeContext);
		if(packageName.equals(context.getPackageName()) && themeId.equals("default")){
			themeMeta.setDefaultTheme(true);
		}else{
			themeMeta.setDefaultTheme(false);
		}
		AssetManager am = themeContext.getResources().getAssets();
		//初始化当前主题资源
		BufferedReader br = null;
		try{
			String themePath = ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + themeMeta.getId() + FILE_SEPARATOR + ThemeMeta.THEME_META_FILE;
			br = new BufferedReader(new InputStreamReader(am.open(themePath)));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			while(line != null){
				sb.append(line);
				line = br.readLine();
			}
			JSONObject json = new JSONObject(sb.toString());
			themeMeta.setDesp(json.getString(ThemeMeta.DESP));
			themeMeta.setRequiredAppVersionCode(json.getInt(ThemeMeta.REQUIRED_APP_VERSION_CODE));
			themeMeta.setVersionCode(json.getInt(ThemeMeta.VERSION_CODE));
			themeMeta.setVersionName(json.getString(ThemeMeta.VERSION_NAME));
			themeMeta.setName(json.getString(ThemeMeta.NAME));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(br != null){
				try{
					br.close();
				}catch(Exception e){
					
				}
			}
		}
		
		try{
			String iconsPath = ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + themeMeta.getId() + FILE_SEPARATOR + ThemeMeta.THEME_ICONS_PATH;
			String[] iconFiles = am.list(iconsPath);
			String iconFile = iconsPath + FILE_SEPARATOR + iconFiles[0];
			themeMeta.setThemeIcon(new BitmapDrawable(context.getResources(), am.open(iconFile)));
		}catch(Exception e){
			
		}
		return themeMeta;
	}
	//添加应用安装和删除的监听器，当删除或安装应用是，检测该应用是否是主题应用，并进行相应处理
	private void addReceiver(Context context){
		IntentFilter it = new IntentFilter();
		it.setPriority(2147483647);
		it.addAction(Intent.ACTION_PACKAGE_ADDED);
		it.addAction(Intent.ACTION_PACKAGE_REMOVED);
		it.addAction(Intent.ACTION_PACKAGE_REPLACED);
		context.registerReceiver(new ThemeAppReceiver(), it);
	}
	private synchronized void loadThemesFromPkg(Context context, String packageName){
		Context themeContext = null;
		try{
			themeContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		AssetManager am = themeContext.getResources().getAssets();
		String[] pathList = null;
		try{
			pathList = am.list(ThemeMeta.THEME_ROOT_PATH);			
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		if(pathList == null){
			return;
		}
		for(String path : pathList){
			if(themeExisted(packageName, path)){
				continue;
			}
			String themeFile = ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + path + FILE_SEPARATOR + "theme";
			BufferedReader themeReader = null;
			MessengerThemeMeta themeMeta =  new MessengerThemeMeta();
			try{
				themeReader = new BufferedReader(new InputStreamReader(am.open(themeFile)));
				String line = themeReader.readLine();
				StringBuilder sb = new StringBuilder();
				while(line != null){
					sb.append(line);
					line = themeReader.readLine();
				}
				JSONObject json = new JSONObject(sb.toString());				
				themeMeta.setRequiredAppVersionCode(json.getInt(ThemeMeta.REQUIRED_APP_VERSION_CODE));
				themeMeta.setVersionCode(json.getInt(ThemeMeta.VERSION_CODE));
				int versionChecked = ThemeUtils.checkThemeVersion(themeMeta, mAppMeta);
				if(versionChecked != 0){
					continue;
				}				
				themeMeta.setDesp(json.getString(ThemeMeta.DESP));
				themeMeta.setVersionName(json.getString(ThemeMeta.VERSION_NAME));
				themeMeta.setName(json.getString(ThemeMeta.NAME));	
				themeMeta.setId(path);
				themeMeta.setPackageName(packageName);
				themeMeta.setThemeContext(themeContext);
				if(packageName.equals(context.getPackageName()) && path.equals("default")){
					themeMeta.setDefaultTheme(true);
				}else{
					themeMeta.setDefaultTheme(false);
				}
				try{
					String iconsPath = ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + themeMeta.getId() + FILE_SEPARATOR + ThemeMeta.THEME_ICONS_PATH;
					String[] iconFiles = am.list(iconsPath);
					String iconFile = iconsPath + FILE_SEPARATOR + iconFiles[0];
					themeMeta.setThemeIcon(new BitmapDrawable(context.getResources(), am.open(iconFile)));
				}catch(Exception e){
					
				}
				mThemeList.add(themeMeta);
			}catch(Exception e){
				continue;
			}finally{
				if(themeReader != null){
					try{
						themeReader.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
	private boolean themeExisted(String packageName, String themeId){
		boolean ret = false;
		for(ThemeMeta themeMeta : mThemeList){
			if(themeMeta.getPackageName().equals(packageName) && themeMeta.getId().equals(themeId)){
				return true;
			}
		}
		return ret;
	}
	private void delThemeFromPkg(Context context, String packageName){
		for(int i = 0; i < mThemeList.size(); i++){
			ThemeMeta tm = mThemeList.get(i);
			if(tm.getPackageName().equals(packageName)){
				mThemeList.remove(i);
				if(tm == this.mCurrentTheme){
					restoreThemeToDefault();
				}
			}
		}
	}
	private void restoreThemeToDefault(){
		for(int i = 0; i < mThemeList.size(); i++){
			ThemeMeta tm = mThemeList.get(i);
			if(tm.isDefaultTheme()){
				this.mCurrentTheme = tm;
				break;
			}
		}
	}
	private void updateThemeFromPkg(Context context, String packageName){
		delThemeFromPkg(context, packageName);
		loadThemesFromPkg(context, packageName);
	}
	
	private class ThemeAppReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
				String packageName = intent.getData().getEncodedSchemeSpecificPart();
				if(packageName.startsWith(THEME_SCHEME_PREFIX)){
					loadThemesFromPkg(context, packageName);
				}else{
					return;
				}
			}else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
				String packageName = intent.getData().getEncodedSchemeSpecificPart();
				if(packageName.startsWith(THEME_SCHEME_PREFIX)){
					delThemeFromPkg(context, packageName);
				}else{
					return;
				}
			}else if(action.equals(Intent.ACTION_PACKAGE_REPLACED)){
				String packageName = intent.getData().getEncodedSchemeSpecificPart();
				if(packageName.startsWith(THEME_SCHEME_PREFIX)){
					updateThemeFromPkg(context, packageName);
				}else{
					return;
				}
			}
		}
		
	}
	//加载系统中的主题
	private class LoadThemeThread extends Thread{
		private Context mContext;
		
		public LoadThemeThread(Context context){
			this.mContext = context;
		}
		public void run(){
			//load theme from messenger app
			loadThemesFromPkg(mContext, mContext.getPackageName());
			//load other theme app
			List<PackageInfo> themePkgs = ThemeUtils.getInstalledThemePackages(mContext);			
			for(PackageInfo pInfo : themePkgs){
				loadThemesFromPkg(mContext, pInfo.packageName);
			}
		}
	}
}
