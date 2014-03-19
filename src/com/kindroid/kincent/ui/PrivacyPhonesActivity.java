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
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyCallogListAdapter;
import com.kindroid.kincent.data.PrivacyCallogDataItem;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivacyMessagesItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.util.List;

public class PrivacyPhonesActivity extends ListActivity implements
		AutoLockActivityInterface {
	private PrivacyCallogListAdapter mAdapter;

	private boolean menuShowed = false;
	private PopupWindow menuWindow;
	private View bottomLinear;

	private static final String LAYOUT_FILE = "privacy_callog_list_activity";
	private static final String MENU_LAYOUT_FILE = "privacy_callog_menu";

	private Dialog mCurrentDialog;

	private GenealDailog mGeneralDailog;
	private static final int LOAD_ADAPTER_FINISH = 0;
	
	private BroadcastReceiver mCallReceiver;
	
	private boolean isActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();
		mCallReceiver = new PrivacyCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.PRIVACY_CALL_BROADCAST_ACTION);
		this.registerReceiver(mCallReceiver, filter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(mCallReceiver);
	}

	private class PrivacyCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(!isActive){
				return;
			}
			try{
				String name = intent.getStringExtra(PrivateDBHelper.PrivateCllIn.NAME);
				String number = intent.getStringExtra(PrivateDBHelper.PrivateCllIn.NUMBER);
				long date = intent.getLongExtra(PrivateDBHelper.PrivateCllIn.DATE, System.currentTimeMillis());
				int blockType = intent.getIntExtra(PrivateDBHelper.PrivateCllIn.BLOCKED_TYPE, 0);
				PrivacyCallogDataItem item = new PrivacyCallogDataItem();
				item.setBlockedType(blockType);
				item.setDate(date);
				item.setName(name);
				item.setPhoneNumber(number);
				item.setType(PrivacyCallogDataItem.CALL_IN_TYPE);
				if(mAdapter != null){
					mAdapter.addItem(item);
					mAdapter.sortItems();
					mAdapter.notifyDataSetChanged();
				}else{
					
				}
			}catch(Exception e){
				
			}
		}
		
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
			setContentView(R.layout.privacy_callog_list_activity);
		} else {
			setContentView(contentView);
		}
		bottomLinear = findViewById(R.id.privacy_callog_list_activity_bottom_linear);
		setupMenu();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isActive = true;
		loadListAdapter();
		PrivacyNotification pNotification = new PrivacyNotification(this);
		pNotification.cancelCallNotifcation();
	}

	private void loadListAdapter() {
		mGeneralDailog = new GenealDailog(this.getParent(), R.style.Theme_CustomDialog);
		mGeneralDailog.showDialog();
		new LoadListAdapterThread().start();
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case LOAD_ADAPTER_FINISH:
				setListAdapter(mAdapter);
				if (mGeneralDailog != null) {
					mGeneralDailog.disMisDialog();
				}
				break;
			}
		}

	};

	private class LoadListAdapterThread extends Thread {
		public void run() {
			List<PrivacyCallogDataItem> mItems = PrivacyDBUtils.getInstance(
					PrivacyPhonesActivity.this).getCallogs(PrivacyPhonesActivity.this);
			mAdapter = new PrivacyCallogListAdapter(PrivacyPhonesActivity.this);
			if (mItems != null) {
				mAdapter.addItemsAll(mItems);
				mAdapter.sortItems();
			}
			mHandler.sendEmptyMessage(LOAD_ADAPTER_FINISH);
		}
	}

	private View initMenuView() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(MENU_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = layoutInflater.inflate(R.layout.privacy_callog_menu,
					null);
		}
		return contentView;
	}

	private void setupMenu() {
		View layout = initMenuView();

		menuWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.findViewById(R.id.privacy_callog_menu_delete)
				.setOnClickListener(menuListener);

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;
			return true;
		}
		if (event.getAction() != KeyEvent.ACTION_UP) {
			return super.dispatchKeyEvent(event);
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			if (!menuShowed) {
				if (mAdapter.getCount() == 0) {
					return super.dispatchKeyEvent(event);
				}
				if (menuWindow == null) {
					setupMenu();
				} else {
					menuWindow
							.showAtLocation(
									this.findViewById(R.id.privacy_callog_list_activity_bottom_linear),
									Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
									0, 0);
				}
				menuShowed = true;
			} else {
				menuWindow.dismiss();
				menuShowed = false;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		if (menuShowed) {
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				menuWindow.dismiss();
				menuShowed = false;
			}
			return true;
		}

		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isActive = false;
		if (menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;

		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		PrivacyCallogItemClickDialog clickDialog = new PrivacyCallogItemClickDialog(
				this.getParent(), R.style.Theme_CustomDialog, mAdapter,
				(PrivacyCallogDataItem) mAdapter.getItem(position));
		clickDialog.show();
		mCurrentDialog = clickDialog;
	}

	private OnClickListener menuListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.privacy_callog_menu_delete:
				// PrivacyDBUtils pdbUtils =
				// PrivacyDBUtils.getInstance(PrivacyPhonesActivity.this);
				// pdbUtils.delCallogs(PrivacyPhonesActivity.this);
				DelConfirmDialog confirmDialog = new DelConfirmDialog(
						PrivacyPhonesActivity.this.getParent(),
						R.style.Theme_CustomDialog);
				confirmDialog.show();
				mCurrentDialog = confirmDialog;
				break;

			}
			menuWindow.dismiss();
			menuShowed = false;

		}
	};

	private class DelConfirmDialog extends Dialog implements
			View.OnClickListener {
		private Context mContext;
		private Button buttonOk;
		private Button buttonCancel;
		private View closeIv;

		public DelConfirmDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			initContentViews();
		}

		private void initContentViews() {
			setContentView(R.layout.confirm_dialog);
			buttonOk = (Button) findViewById(R.id.button_ok);
			buttonCancel = (Button) findViewById(R.id.button_cancel);
			closeIv = findViewById(R.id.dialog_close_iv);
			buttonOk.setOnClickListener(this);
			buttonCancel.setOnClickListener(this);
			closeIv.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button_ok:
				PrivacyDBUtils pdbUtils = PrivacyDBUtils
						.getInstance(PrivacyPhonesActivity.this);
				pdbUtils.delCallogs(PrivacyPhonesActivity.this);
				mAdapter.clearItems();
				mAdapter.notifyDataSetChanged();
				dismiss();
				break;
			case R.id.button_cancel:
				dismiss();
				break;
			case R.id.dialog_close_iv:
				dismiss();
				break;
			}
		}

	}

	@Override
	public void onLock() {
		// TODO Auto-generated method stub
		if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
			mCurrentDialog.dismiss();
		}
		if (menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;

		}
	}

}
