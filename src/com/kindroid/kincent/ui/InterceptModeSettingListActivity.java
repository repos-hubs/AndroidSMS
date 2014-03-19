/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author: heli.zhao
 * Date: 2011-09
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.kindroid.android.model.InterceptModeItem;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.InterceptModeSettingAdapter;
import com.kindroid.kincent.R;

import com.kindroid.security.util.Constant;


/**
 * @author heli.zhao
 *
 */
public class InterceptModeSettingListActivity extends ListActivity {
	
	private InterceptModeSettingAdapter mListAdapter;
	private String[] mInterceptModeNames;
	private String[] mInterceptModeDesps;
	private TextView mTitleTv;
	
	private static final String LAYOUT_FILE = "intercept_mode_setting";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			setContentView(R.layout.intercept_mode_setting);
		}else{
			setContentView(contentView);
		}
		
		mTitleTv=(TextView) findViewById(R.id.title_tv);
		mTitleTv.getPaint().setFakeBoldText(true);
		mTitleTv.setText(R.string.select_mode_intercept);
		
		mInterceptModeNames = getResources().getStringArray(R.array.intercept_mode_name);
		mInterceptModeDesps = getResources().getStringArray(R.array.intercept_mode_desp);
		loadListAdapter();
	}
	private void loadListAdapter(){
		SharedPreferences sp = KindroidMessengerApplication.sh;
		int mDefaultInterceptMode = sp.getInt(Constant.SHAREDPREFERENCES_BLOCKINGRULES, 1);
		mListAdapter = new InterceptModeSettingAdapter(this);
		for(int i = 1; i < mInterceptModeNames.length; i++){
			InterceptModeItem item = new InterceptModeItem();
			item.setModeTitle(mInterceptModeNames[i]);
			item.setModeDesp(mInterceptModeDesps[i]);
			if(i == (mDefaultInterceptMode)){
				item.setIsSelected(true);
			}else{
				item.setIsSelected(false);
			}
			mListAdapter.addItem(item);
		}
		setListAdapter(mListAdapter);
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		SharedPreferences sp = KindroidMessengerApplication.sh;
		Editor editor = sp.edit();		
		editor.putInt(Constant.SHAREDPREFERENCES_BLOCKINGRULES, position + 1);		
		editor.commit();
		/*
		mListAdapter.setMode(position);
		mListAdapter.notifyDataSetChanged();
		*/
		finish();
	}
	
}
