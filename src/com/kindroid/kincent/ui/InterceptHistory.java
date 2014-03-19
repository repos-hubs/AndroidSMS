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

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.InterceptHistoryAdapter;
import com.kindroid.kincent.notification.InterceptNotification;
import com.kindroid.kincent.R;
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

public class InterceptHistory extends Activity implements View.OnClickListener,
		OnItemClickListener {
	private List<HistoryNativeCursor> mCursors = new ArrayList<HistoryNativeCursor>();

	private InterceptHistoryAdapter mAdapter;
	private ListView mListview;

	private View mHistoryMenuLinear;
	private View mHistoryMenuAddItem;
	private View mHistoryMenuDeleteItem;
	private boolean mShowMenu = false;
	private BroadcastReceiver mReceiver;
	private boolean mCanUse = true;
	private Handler mHandler = new Handler();
	private boolean isActive = false;
	private boolean comFromCreate;
	private boolean hasUnreadSpam = false;
	
	private static final String LAYOUT_FILE = "intercept_history_layout";

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
			setContentView(R.layout.intercept_history_layout);
		}else{
			setContentView(contentView);
		}
		
		findView();
		isActive = true;
		comFromCreate = true;
		
		loadAdapter();
		mAdapter = new InterceptHistoryAdapter(this, mCursors);
		mListview.setAdapter(mAdapter);
		

		initReciver();
		IntentFilter mIt = new IntentFilter(
				Constant.BROACT_UPDATE_INTERCEPT_HISTORY);

		registerReceiver(mReceiver, mIt);
	}

	private void findView() {
	
	
		mListview = (ListView) findViewById(R.id.listproc);

		bindListerToView();
	}

	private void bindListerToView() {
		mListview.setOnItemClickListener(this);

	}

	void initReciver() {
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				int type = 0;
				if (intent != null) {
					type = intent.getIntExtra("sms_or_phone", 0);
				}
				if (type != 3) {
					return;
				}
				if (!isActive) {
					hasUnreadSpam = true;
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						loadAdapter();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mAdapter.setmPosition(-1);
								mAdapter.notifyDataSetChanged();
								
							}
						});

					}
				}).start();
			}
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isActive = true;
		if (hasUnreadSpam) {
			try {
				loadAdapter();
				mAdapter.setmPosition(-1);
				mAdapter.notifyDataSetChanged();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			hasUnreadSpam = false;
		}
		if (comFromCreate) {
			comFromCreate = false;

		} else {
			showNotification();
		}
		
		KindroidMessengerTabMain.dismissInterceptBgRl();
		
		
		
		
	
	}





	private void deleteHistory() {
		int count = mAdapter.getCount();
		if (count == 0) {
			return;
		}
		boolean deleted = false;
		InterceptDataBase mBase = InterceptDataBase.get(this);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			HistoryNativeCursor mCursor = (HistoryNativeCursor) mAdapter
					.getItem(i);
			if (mCursor.isSelect()) {
				// delete this history item
				mBase.DelHistory(mCursor);
				deleted = true;
				sb.append(mCursor.getmBody());
				sb.append("\n");
			}
		}
		if (deleted) {
			// refresh UI
			loadAdapter();
			mAdapter = new InterceptHistoryAdapter(this, mCursors);
			mListview.setAdapter(mAdapter);
			if (sb.length() > 0) {
				UpdateStaticsThread ust = new UpdateStaticsThread(
						sb.toString(), 1, this);
				ust.start();
			}
		} else {
			Toast.makeText(this, R.string.select_content_to_delete,
					Toast.LENGTH_LONG).show();
		}
	}
//
//	private void recoverSms() {
//		int count = mAdapter.getCount();
//		if (count == 0) {
//			return;
//		}
//		InterceptDataBase mBase = InterceptDataBase.get(this);
//		boolean recovered = false;
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < count; i++) {
//			HistoryNativeCursor mCursor = (HistoryNativeCursor) mAdapter
//					.getItem(i);
//			if (mCursor.isSelect()) {
//				// recover this history item
//				UtilDailog.InsertSmstoBox(this, mCursor);
//				sb.append(mCursor.getmBody());
//				sb.append("\n");
//				// delete this history item
//				mBase.DelHistory(mCursor);
//				recovered = true;
//			}
//		}
//		if (recovered) {
//			// refresh UI
//			loadAdapter();
//			mAdapter = new InterceptHistoryAdapter(this, mCursors, 3);
//			mListview.setAdapter(mAdapter);
//			// reset checkbox
//			if (sb.length() > 0) {
//				UpdateStaticsThread ust = new UpdateStaticsThread(
//						sb.toString(), 0, this);
//				ust.start();
//			}
//		} else {
//			Toast.makeText(this, R.string.select_sms_to_recover,
//					Toast.LENGTH_LONG).show();
//		}
//
//	}

//	private void toConfirmDialog(int id) {
//		switch (id) {
//		case 0:
//			// show confirm dialog
//			final Dialog promptDialog = new Dialog(this.getParent(),
//					R.style.Theme_CustomDialog);
//			promptDialog.setContentView(R.layout.soft_uninstall_prompt_dialog);
//			TextView promptText = (TextView) promptDialog
//					.findViewById(R.id.prompt_text);
//			promptText.setText(R.string.confirm_to_restore_sms);
//			Button button_ok = (Button) promptDialog
//					.findViewById(R.id.button_ok);
//			Button button_cancel = (Button) promptDialog
//					.findViewById(R.id.button_cancel);
//			button_ok.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					recoverSms();
//					promptDialog.dismiss();
//				}
//			});
//			button_cancel.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					promptDialog.dismiss();
//				}
//			});
//			promptDialog.show();
//			break;
//		case 1:
//			final Dialog dialog = new Dialog(this.getParent(),
//					R.style.softDialog);
//			dialog.setContentView(R.layout.soft_uninstall_prompt_dialog);
//			promptText = (TextView) dialog.findViewById(R.id.prompt_text);
//			promptText.setText(R.string.confirm_to_delete_history);
//			button_ok = (Button) dialog.findViewById(R.id.button_ok);
//			button_cancel = (Button) dialog.findViewById(R.id.button_cancel);
//			button_ok.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					deleteHistory();
//					dialog.dismiss();
//				}
//			});
//			button_cancel.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//				}
//			});
//			dialog.show();
//			break;
//		}
//	}

	private boolean hasSelectedItems() {
		boolean ret = false;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			HistoryNativeCursor hnc = (HistoryNativeCursor) mAdapter.getItem(i);
			if (hnc.isSelect()) {
				ret = true;
			}
		}

		return ret;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.intercept_history_sms_menu_add:
//			// recover sms history
//			if (mAdapter.getCount() != 0) {
//				if (hasSelectedItems()) {
//					toConfirmDialog(0);
//				} else {
//					Toast.makeText(this, R.string.select_sms_to_recover,
//							Toast.LENGTH_LONG).show();
//				}
//			}
//
//			BlockTabMain.showBottomLinear();
//			mHistoryMenuLinear.setVisibility(View.GONE);
//			mShowMenu = false;
//			break;
//		case R.id.intercept_history_sms_menu_delete:
//			// delete intercept history of sms
//			if (mAdapter.getCount() != 0) {
//				if (hasSelectedItems()) {
//					toConfirmDialog(1);
//				} else {
//					Toast.makeText(this, R.string.select_content_to_delete,
//							Toast.LENGTH_LONG).show();
//				}
//			}
//			BlockTabMain.showBottomLinear();
//			mHistoryMenuLinear.setVisibility(View.GONE);
//			mShowMenu = false;
//			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		if (mAdapter.getmPosition() == arg2) {
			// mAdapter=new InterceptHistoryAdapter(this,mCursors, 3);
			mAdapter.setmPosition(-1);
		} else {
			// mAdapter=new InterceptHistoryAdapter(this,mCursors, 3);
			mAdapter.setmPosition(arg2);
		}
		mAdapter.notifyDataSetChanged();
		// mListview.setAdapter(mAdapter);

	}

	private void loadAdapter() {
		InterceptDataBase.get(this).changeReadStatus(3);
		
		mCursors.clear();
		Cursor c = InterceptDataBase.get(this).selectAllInterceptHistoryList(3);
		if (c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex(InterceptDataBase.ID));
				String address = c.getString(c
						.getColumnIndex(InterceptDataBase.ADDRESS));
				String date = c.getString(c
						.getColumnIndex(InterceptDataBase.DATE));
				String body = c.getString(c
						.getColumnIndex(InterceptDataBase.BODY));
				int read = c.getInt(c.getColumnIndex(InterceptDataBase.READ));
				HistoryNativeCursor hnc = new HistoryNativeCursor();
				hnc.setmId(id);
				hnc.setmOriginDate(date);
				hnc.setmAddress(address);
				try {
					date=InterceptDataBase.DF_DATE_MD.format(InterceptDataBase.DF_DATE.parse(date));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				hnc.setmDate(date);
				hnc.setmBody(body);
				hnc.setmRead(read);
				hnc.setmRequestType(3);
				mCursors.add(hnc);
			}
		}

		if (c != null) {
			c.close();
		}
		if (!isActive) {
			return;
		}
		showNotification();
	}

	private void showNotification() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new InterceptNotification(getApplication(),
						InterceptHistory.this).showInterceptNotification();
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isActive = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}