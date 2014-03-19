/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:zili.chen
 * Date:2011.09
 * Description:
 */
package com.kindroid.kincent.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.CollectListAdapter;
import com.kindroid.kincent.adapter.CollectionAdapter;
import com.kindroid.kincent.adapter.InterceptHistoryAdapter;
import com.kindroid.kincent.notification.InterceptNotification;
import com.kindroid.kincent.R;
import com.kindroid.security.util.CollectionDataBase;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.security.util.UpdateStaticsThread;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CollectListActivity extends Activity implements
		OnItemClickListener {
	
	public static String COLLET_LIST_ITEM="collect_list_item";
	
	
	private List<CategorySmsListItem> mCursors = new ArrayList<CategorySmsListItem>();

	private CollectListAdapter mAdapter;
	private ListView mListview;
	private int mCategoryId;
	private TextView mTitleTv;

	private static final String LAYOUT_FILE = "collect_list_layout";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentViews();
//		loadAdapter();
//		mAdapter = new CollectListAdapter(this, mCursors);
//		mListview.setAdapter(mAdapter);
	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.collect_list_layout);
		}else{
			setContentView(contentView);
		}
		
		findView();
		mCategoryId=getIntent().getIntExtra("categoryId", -1);
		System.out.println(mCategoryId);
	}

	private void findView() {

		mListview = (ListView) findViewById(R.id.listproc);
		mTitleTv=(TextView) findViewById(R.id.activity_title_tv);
		mTitleTv.getPaint().setFakeBoldText(true);
		mTitleTv.setText(R.string.collect_list);
		bindListerToView();
	}

	private void bindListerToView() {
		mListview.setOnItemClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new AsyLoadRequest().execute();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub	
		Intent intent=new Intent(CollectListActivity.this,CollectListDetailActivity.class);
		intent.putExtra(COLLET_LIST_ITEM, mCursors.get(arg2));
		startActivity(intent);
	}
	
	
	
	
	
	class AsyLoadRequest extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			mCursors = CollectionDataBase.get(
					CollectListActivity.this).getCategorySmsListItems(mCategoryId);
			System.out.println(mCursors.size()+"  fee");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mAdapter = new CollectListAdapter(CollectListActivity.this, mCursors);
			mListview.setAdapter(mAdapter);
			//mAdapter.notifyDataSetChanged();
		}

	}

}