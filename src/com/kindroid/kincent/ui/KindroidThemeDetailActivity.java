package com.kindroid.kincent.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.ThemeImageAdapter;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.widget.CircleFlowIndicator;
import com.kindroid.kincent.widget.ViewFlow;
import com.kindroid.kincent.R;
import com.kindroid.theme.ThemeMeta;

import java.util.ArrayList;
import java.util.List;

public class KindroidThemeDetailActivity extends Activity implements View.OnClickListener{
	private ViewFlow viewFlow;
	private ThemeImageAdapter mAdapter;
	
	private TextView mThemeNameTv;
	private TextView mThemeVersionTv;
	private Button mThemeEnableButton;
	private TextView mThemeDespTv;
	private ViewFlow mViewFlow;

	private static final String LAYOUT_FILE = "theme_detail_activity";
	public static final String THEME_INDEX_EXTRA = "theme_index_extra";
	public static final String FILE_SEPARATOR = "/";
	
	private int mThemeIndex;
	private ThemeMeta mCurrentTheme;
	
	private boolean mThemeChanged = false;
	public static final String THEME_CHANGED_EXTRA = "theme_changed_extra";
	
	public static BitmapFactory.Options mOptions;
	public static DisplayMetrics mDisplayMetrics;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mThemeIndex =  getIntent().getIntExtra(THEME_INDEX_EXTRA, 0);
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		
		initContentViews();
	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.theme_detail_activity);
		}else{
			setContentView(contentView);
		}
		mThemeNameTv = (TextView)findViewById(R.id.theme_detail_activity_theme_name_tv);
		mThemeVersionTv = (TextView)findViewById(R.id.theme_detail_activity_theme_version_tv);
		mThemeDespTv = (TextView)findViewById(R.id.theme_detail_activity_theme_desp_tv);
		mThemeEnableButton = (Button)findViewById(R.id.theme_detail_activity_enable_button);
		mThemeEnableButton.setOnClickListener(this);
		
		mViewFlow = (ViewFlow)findViewById(R.id.theme_detail_viewflow);
		com.kindroid.kincent.widget.CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.theme_detail_viewflow_indication);
		mViewFlow.setFlowIndicator(indic);		
		fillData(mThemeIndex);
	}
	private void fillData(int themeIndex){
		if(mCurrentTheme == null){
			mCurrentTheme = KindroidMessengerApplication.mThemeRegistry.getThemeList().get(themeIndex);
		}
		mThemeNameTv.setText(mCurrentTheme.getName());
		mThemeVersionTv.setText(getString(R.string.theme_version_prefix) + mCurrentTheme.getVersionName());
		if(!TextUtils.isEmpty(mCurrentTheme.getDesp())){
			mThemeDespTv.setText(mCurrentTheme.getDesp());
		}
		if(mAdapter == null){
			List<Drawable> imageList = getDrawables(mCurrentTheme);
			mAdapter =  new ThemeImageAdapter(this, imageList);
		}	
		
		mViewFlow.setAdapter(mAdapter, 1);
	}
	private List<Drawable> getDrawables(ThemeMeta themeMeta){
		if(mOptions == null){
			mOptions = new BitmapFactory.Options();
			mOptions.inDensity = mDisplayMetrics.densityDpi;
			mOptions.inTargetDensity = mDisplayMetrics.densityDpi;
			mOptions.inScaled = true;
			if(mDisplayMetrics.widthPixels < 350){
				if(mDisplayMetrics.density == DisplayMetrics.DENSITY_LOW){
					mOptions.inSampleSize = 3;
				}else{
					mOptions.inSampleSize = 2;
				}
			}else{
				
			}
		}
		
		String packageName = themeMeta.getPackageName();
		List<Drawable> mList = new ArrayList<Drawable>();
		try{
			Context themeContext = createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
			AssetManager am = themeContext.getResources().getAssets();
			String iconsPath = ThemeMeta.THEME_ROOT_PATH + FILE_SEPARATOR + themeMeta.getId() + FILE_SEPARATOR + ThemeMeta.THEME_ICONS_PATH;
			String[] iconFiles = am.list(iconsPath);
			for(String file : iconFiles){
				String iconFile = iconsPath + FILE_SEPARATOR + file;
				//mList.add(new BitmapDrawable(getResources(), am.open(iconFile)));
				mList.add(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(am.open(iconFile), null, mOptions)));
			}
		}catch(Exception e){
			
		}
		return mList;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.theme_detail_activity_enable_button:
			if(KindroidMessengerApplication.mThemeRegistry.getCurrentTheme() != mCurrentTheme){
				try{
					KindroidMessengerApplication.mThemeRegistry.setCurrentTheme(mCurrentTheme);
//					initContentViews();
					mThemeChanged = true;
					SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(this);
					Editor editor = sh.edit();
					editor.putString(Constant.THEME_PACKAGE_USED, mCurrentTheme.getPackageName());
					editor.putString(Constant.THEME_ID_USED, mCurrentTheme.getId());
					editor.commit();
					Intent data = new Intent();
					data.putExtra(THEME_CHANGED_EXTRA, mThemeChanged);	
					setResult(Activity.RESULT_OK, data);
					finish();
				}catch(Exception e){
					mThemeChanged = false;
				}
				
			}
			break;
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mThemeChanged){
			Intent data = new Intent();
			data.putExtra(THEME_CHANGED_EXTRA, mThemeChanged);		
			setResult(Activity.RESULT_OK, data);
		}
		super.onBackPressed();
	}
	
}
