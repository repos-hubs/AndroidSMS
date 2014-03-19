/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.AutoMsgListAdapter;
import com.kindroid.kincent.data.AutoMsgDataItem;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.util.List;

public class PrivacyChangeAutoMsgListActivity extends Activity implements View.OnClickListener, AutoLockActivityInterface{
	private ListView mListView;
	private Button mButton;
	private AutoMsgListAdapter mAdapter;
	
	private static final String LAYOUT_FILE = "privacy_change_auto_msg_list_activity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentView();
	}
	private void initContentView(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.privacy_change_auto_msg_list_activity);
		}else{
			setContentView(contentView);
		}
		
		mListView = (ListView)findViewById(R.id.privacy_auto_msg_list);
		mButton = (Button)findViewById(R.id.privacy_add_auto_msg_button);
		PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(this);
		List<AutoMsgDataItem> mItems = pdbUtils.getAllAutoMsgs();
		mAdapter = new AutoMsgListAdapter(this, mItems);
		mButton.setOnClickListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(mAdapter.getPosition() == position){
					mAdapter.setPosition(-1);
				}else{
					mAdapter.setPosition(position);
				}
				mAdapter.notifyDataSetChanged();
			}
			
		});
		//初始化自动锁时间		
		KindroidMessengerPrivacyActivity.registLockedActivity(this);
		KindroidMessengerPrivacyActivity.timeForLock = System.currentTimeMillis();
		KindroidMessengerPrivacyActivity.updateLockTask();
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		KindroidMessengerPrivacyActivity.timeForLock = System.currentTimeMillis();
		KindroidMessengerPrivacyActivity.updateLockTask();
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.privacy_add_auto_msg_button:
			EditAutoMsgDialog editDialog = new EditAutoMsgDialog(this, R.style.Theme_CustomDialog);
			editDialog.show();
			break;
		}
	}
	private class EditAutoMsgDialog extends Dialog implements View.OnClickListener{
		private Button mButtonOk;
		private Button mButtonCancel;
		private EditText mMsgEditEt;

		public EditAutoMsgDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			setContentView(R.layout.privacy_auto_msg_edit_dialog);
			mButtonOk = (Button)findViewById(R.id.button_ok);
			mButtonCancel = (Button)findViewById(R.id.button_cancel);
			mMsgEditEt = (EditText)findViewById(R.id.privacy_auto_msg_edit_et);			
			mButtonOk.setOnClickListener(this);
			mButtonCancel.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.button_ok:
				String newMsgContent = mMsgEditEt.getText().toString().trim();
				if(newMsgContent.equals("")){
					Toast.makeText(PrivacyChangeAutoMsgListActivity.this, R.string.privacy_change_text_empty_prompt, Toast.LENGTH_LONG).show();
					return;
				}
				AutoMsgDataItem item = new AutoMsgDataItem(newMsgContent, 0, -1);
				PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(PrivacyChangeAutoMsgListActivity.this);
				pdbUtils.addAutoMsg(PrivacyChangeAutoMsgListActivity.this, item);
				mAdapter.addItem(item);
				mAdapter.notifyDataSetChanged();
				dismiss();
				break;
			case R.id.button_cancel:
				dismiss();
				break;
			}
		}
		
	}
	@Override
	public void onLock() {
		// TODO Auto-generated method stub
		finish();
	}

}
