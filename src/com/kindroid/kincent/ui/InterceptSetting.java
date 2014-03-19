/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:zili.chen
 * Date:2011.09
 * Description:
 */

package com.kindroid.kincent.ui;

import java.util.Locale;

import org.json.JSONObject;


import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Constant;


import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class InterceptSetting extends Activity implements View.OnClickListener {
	private View mInterceptModeRl;
	
	private View mDefineKeywordsRl;
	
	

	private TextView mCurrentInterceptMode;

	

	private String[] mInterceptModeNames;

	
	private Dialog dialog;
	
	private static final String LAYOUT_FILE = "intercept_setting";

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
			setContentView(R.layout.intercept_setting);
		}else{
			setContentView(contentView);
		}

		mInterceptModeNames = getResources().getStringArray(
				R.array.intercept_mode_name);
		findView();
		bindListenerToView();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// refresh UI
		SharedPreferences sp = KindroidMessengerApplication.sh;
		int mDefaultInterceptMode = sp.getInt(
				Constant.SHAREDPREFERENCES_BLOCKINGRULES, 1);
		mCurrentInterceptMode
				.setText(mInterceptModeNames[mDefaultInterceptMode]);
	}

	private void findView() {
		mInterceptModeRl = findViewById(R.id.intercept_mode_rl);
	
		mDefineKeywordsRl = findViewById(R.id.key_word_rl);

		mCurrentInterceptMode = (TextView) findViewById(R.id.current_intercept_mode);
		
		

	}

	private void bindListenerToView() {
		mInterceptModeRl.setOnClickListener(this);
		mDefineKeywordsRl.setOnClickListener(this);
	}
//
//	private int saveTreatMode(Dialog dialog) {
//		SharedPreferences sp = KindroidSecurityApplication.sh;
//		Editor editor = sp.edit();
//		ListView mListView = (ListView) dialog
//				.findViewById(R.id.treat_mode_list);
//		InterceptTreatModeListAdapter mListAdapter = (InterceptTreatModeListAdapter) mListView
//				.getAdapter();
//		int mSelectedMode = mListAdapter.getSelected();
//		editor.putInt(Constant.INTERCEPT_TREAT_MODE, mSelectedMode + 1);
//		editor.commit();
//		mCurrentTreatMode.setText(mListAdapter
//				.getSelectedModeName(mSelectedMode));
//		return mSelectedMode + 1;
//	}
//
//	private void loadTreatModeListAdapter(Dialog dialog, int defaultMode) {
//		final InterceptTreatModeListAdapter mListAdapter = new InterceptTreatModeListAdapter(
//				this);
//		mListAdapter.setPosition(defaultMode);
//		ListView mListView = (ListView) dialog
//				.findViewById(R.id.treat_mode_list);
//		mListView.setAdapter(mListAdapter);
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				mListAdapter.setPosition(position);
//				mListAdapter.notifyDataSetChanged();
//			}
//
//		});
//	}

	private Uri getPhoneUri(int type) {
		Uri uri = null;
		if (type == 1 || type == 2) {
			uri = Uri.parse("tel:" + "%23%2367%23");
		} else if (type == 3) {
			uri = Uri.parse("tel:" + "**67*13810538911%23");
		} else if (type == 4) {
			uri = Uri.parse("tel:" + "**67*13701110216%23");
		} else if (type == 5) {
			uri = Uri.parse("tel:" + "**67*13800000000%23");
		}
		return uri;

	}
	private Uri getCDMAUri(int type){
		Uri uri = null;
		if (type == 1 || type == 2) {
			uri = Uri.parse("tel:" + "*900");
		} else if (type == 3) {
			uri = Uri.parse("tel:" + "*90*13810538911%23");
		} else if (type == 4) {
			uri = Uri.parse("tel:" + "*90*13701110216%23");
		} else if (type == 5) {
			uri = Uri.parse("tel:" + "*90*13800000000%23");
		}
		return uri;
	}
	private String getCDMAString(int type){
		String ret = null;
		if (type == 1 || type == 2) {
			ret = "tel:" + "*900";
		} else if (type == 3) {
			ret = "tel:" + "*90*13810538911%23";
		} else if (type == 4) {
			ret = "tel:" + "*90*13701110216%23";
		} else if (type == 5) {
			ret = "tel:" + "*90*13800000000%23";
		}
		return ret;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == 100){
			//saveTreatMode(dialog);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.intercept_mode_rl:
			Intent intent = new Intent(this,
					InterceptModeSettingListActivity.class);
			startActivity(intent);
			break;
		case R.id.key_word_rl:
			intent = new Intent(this, KeywordSettingActivity.class);
			startActivity(intent);
			break;

		}

	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent intent = new Intent(InterceptSetting.this,
//					DefenderTabMain.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//
//			startActivity(intent);
//			finish();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}