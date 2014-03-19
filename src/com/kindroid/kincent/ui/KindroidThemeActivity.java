package com.kindroid.kincent.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.ThemeListAdapter;
import com.kindroid.kincent.R;
import com.kindroid.theme.ThemeMeta;

import java.util.List;

public class KindroidThemeActivity extends Activity implements
		OnItemClickListener {
	private ThemeListAdapter mAdapter;
	private GridView mGridView;

	private static final String LAYOUT_FILE = "theme_list_activity";
	
	public static final int THEME_DETAIL_REQUEST_CODE = 99;

	private boolean mThemeChanged = false;
	private ThemeMeta mCurrentTheme;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();
		mCurrentTheme = KindroidMessengerApplication.mThemeRegistry.getCurrentTheme();
	}

	private void initContentViews() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			setContentView(R.layout.theme_list_activity);
		} else {
			setContentView(contentView);
		}
		mGridView = (GridView)findViewById(R.id.theme_list_gv);
		if(mAdapter == null){
			mAdapter = new ThemeListAdapter(this, KindroidMessengerApplication.mThemeRegistry.getThemeList());
			mGridView.setAdapter(mAdapter);
		}else{
			mGridView.setAdapter(mAdapter);
		}
		
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mCurrentTheme != KindroidMessengerApplication.mThemeRegistry.getCurrentTheme()){
//			Intent data = new Intent();
//			data.putExtra(KindroidThemeDetailActivity.THEME_CHANGED_EXTRA, mThemeChanged);		
//			setResult(Activity.RESULT_OK, data);
			KindroidMessengerDialogueActivity.SHOW_UPGRADE_DIALOG = false;
			Intent intent = new Intent(this, KindroidMessengerTabMain.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == THEME_DETAIL_REQUEST_CODE && resultCode == Activity.RESULT_OK){
			mThemeChanged = data.getBooleanExtra(KindroidThemeDetailActivity.THEME_CHANGED_EXTRA, false);
			if(mThemeChanged){
				initContentViews();
//				try{
//					mAdapter.notifyDataSetChanged();
//				}catch(Exception e){
//					
//				}
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, KindroidThemeDetailActivity.class);
		intent.putExtra(KindroidThemeDetailActivity.THEME_INDEX_EXTRA, position);
		startActivityForResult(intent, THEME_DETAIL_REQUEST_CODE);
	}

}
