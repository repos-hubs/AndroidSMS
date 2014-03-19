/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author: heli.zhao
 * Date: 2011-09
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.util.dialog.SureDelKeywordDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.InterceptKeywordAdapter;
import com.kindroid.kincent.R;
import com.kindroid.security.util.InterceptDataBase;

/**
 * @author heli.zhao
 * 
 */
public class KeywordSettingActivity extends Activity implements
		View.OnClickListener {
	private InterceptKeywordAdapter mAdapter;
	private Cursor mCursor;

	private View mAddActionLinear;
	private ListView mListView;
	private EditText mKeywordInputText;
	private TextView mFunctionTv;
	
	private static final String LAYOUT_FILE = "intercept_keyword_setting";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
			setContentView(R.layout.intercept_keyword_setting);
		}else{
			setContentView(contentView);
		}
		
		mCursor = InterceptDataBase.get(this).selectKeyWordList(1);
		mAdapter = new InterceptKeywordAdapter(this, mCursor);
		mListView = (ListView) findViewById(R.id.keyword_listview);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new ListItemClickListener());
		mKeywordInputText = (EditText) findViewById(R.id.key_word_edit_text);
		mAddActionLinear = findViewById(R.id.add_action_linear);
		mAddActionLinear.setOnClickListener(this);
		
		mFunctionTv=(TextView) findViewById(R.id.function_title_tv);
		mFunctionTv.getPaint().setFakeBoldText(true);
		mFunctionTv.setText(R.string.add_keyword);
		
	}

	private class ListItemClickListener implements OnItemClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.widget.AdapterView.OnItemClickListener#onItemClick(android
		 * .widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			new SureDelKeywordDailog(KeywordSettingActivity.this,
					R.style.Theme_CustomDialog, mCursor).show();

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_action_linear:
			String kw = mKeywordInputText.getText().toString();
			if (TextUtils.isEmpty(kw)) {
				Toast.makeText(this, R.string.search_keyword_error,
						Toast.LENGTH_LONG).show();
				return;
			}
			boolean isExist = InterceptDataBase.get(this).selectKeyWord(kw, 1);
			if (isExist) {
				Toast.makeText(this, R.string.intercept_keyword_exist,
						Toast.LENGTH_LONG).show();
				return;
			}
			InterceptDataBase.get(this).insertKeyWord(kw, 1);
			mCursor.requery();
			mKeywordInputText.setText("");
			break;
		}
	}

}
