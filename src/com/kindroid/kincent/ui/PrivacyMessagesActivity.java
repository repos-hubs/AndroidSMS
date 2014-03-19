/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyCallogListAdapter;
import com.kindroid.kincent.adapter.PrivacyMessageListAdapter;
import com.kindroid.kincent.data.PrivacyCallogDataItem;
import com.kindroid.kincent.data.PrivacyMessageDataItem;
import com.kindroid.kincent.data.PrivacyMessagesItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.util.List;

public class PrivacyMessagesActivity extends ListActivity implements AutoLockActivityInterface{
	private PrivacyMessageListAdapter mAdapter;

	private boolean menuShowed = false;
	private PopupWindow menuWindow;
	private View bottomLinear;
	
	public static final int MENU_REPLY = 0;
	public static final int MENU_DELETE = 1;
	public static final int MENU_RESTORE = 2;

	private static final String LAYOUT_FILE = "privacy_messages_list_activity";
	private static final String MENU_LAYOUT_FILE = "privacy_message_menu";
	
	private Dialog mCurrentDialog;
	
	private GenealDailog mGeneralDailog;
	private static final int LOAD_ADAPTER_FINISH = 0;
	
	private BroadcastReceiver smsReceiver;
	
	private boolean isActive;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();		
		smsReceiver = new PrivacySmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.PRIVACY_SMS_BROADCAST_ACTION);
		this.registerReceiver(smsReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(smsReceiver);
	}

	private void initContentViews() {
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.privacy_messages_list_activity);
		}else{
			setContentView(contentView);
		}
		
		bottomLinear = findViewById(R.id.privacy_message_list_activity_bottom_linear);
		setupMenu();
		getListView().setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				PrivacyMessagesItem item = (PrivacyMessagesItem)mAdapter.getItem(position);
				PrivacyMessageItemClickDialog clickDialog = new PrivacyMessageItemClickDialog(PrivacyMessagesActivity.this.getParent(), R.style.Theme_CustomDialog, mAdapter, item);
				clickDialog.show();
				mCurrentDialog = clickDialog;
				return false;
			}
			
		});
	}
	private class PrivacySmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(!isActive){
				return;
			}
			try{
				String name = intent.getStringExtra(PrivateDBHelper.PrivateMsg.NAME);
				String address = intent.getStringExtra(PrivateDBHelper.PrivateMsg.ADDRESS);
				String body = intent.getStringExtra(PrivateDBHelper.PrivateMsg.BODY);
				long receiveDate = intent.getLongExtra(PrivateDBHelper.PrivateMsg.DATE, System.currentTimeMillis());
				int receiveType = intent.getIntExtra(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE, PrivacyMessageDataItem.RECV_TYPE_INBOX);
				int read = intent.getIntExtra(PrivateDBHelper.PrivateMsg.READ, PrivacyMessageDataItem.READ_STATUS_UNREAD);
				boolean hasItem = false;
				for(int i = 0; i < mAdapter.getCount(); i++){
					PrivacyMessagesItem item = (PrivacyMessagesItem)mAdapter.getItem(i);
					if(item.getPhoneNumber().equals(address)){
						item.setLastMsg(body);
						item.setLastDate(receiveDate);
						item.setMsgNum(item.getMsgNum() + 1);
						hasItem = true;
						break;
					}
				}
				if(!hasItem){
					PrivacyMessagesItem item = new PrivacyMessagesItem(context);
					item.setContactName(name);
					item.setPhoneNumber(address);
					item.setLastDate(receiveDate);
					item.setLastMsg(body);
					item.setMsgNum(1);
					mAdapter.addItem(item);
				}
				mAdapter.notifyDataSetChanged();
			}catch(Exception e){
				
			}
		}
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isActive = true;
		loadListAdapter();
	}

	private void loadListAdapter() {
//		mGeneralDailog = new GenealDailog(this.getParent(), R.style.Theme_CustomDialog);
//		mGeneralDailog.showDialog();
//		new LoadListAdapterThread().start();
		List<PrivacyMessagesItem> mItems = PrivacyDBUtils.getInstance(PrivacyMessagesActivity.this).getThreads(PrivacyMessagesActivity.this);
		mAdapter = new PrivacyMessageListAdapter(getParent());
		if(mItems != null){
			mAdapter.addItemsAll(mItems);
		}
		setListAdapter(mAdapter);
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
			List<PrivacyMessagesItem> mItems = PrivacyDBUtils.getInstance(PrivacyMessagesActivity.this).getThreads(PrivacyMessagesActivity.this);
			mAdapter = new PrivacyMessageListAdapter(getParent());
			if(mItems != null){
				mAdapter.addItemsAll(mItems);
			}		
			mHandler.sendEmptyMessage(LOAD_ADAPTER_FINISH);
		}
	}

	private View initMenuView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(MENU_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = layoutInflater.inflate(R.layout.privacy_message_menu, null);
		} 
		return contentView;
	}
	private void setupMenu() {
		View layout = initMenuView();
		
		menuWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.findViewById(R.id.privacy_message_menu_delete).setOnClickListener(
				menuListener);
//		layout.findViewById(R.id.privacy_message_menu_restore)
//				.setOnClickListener(menuListener);
		

	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;
			return true;
		}
		if (event.getAction() != KeyEvent.ACTION_UP){
			return super.dispatchKeyEvent(event);
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			if (!menuShowed) {
				if(mAdapter.getCount() == 0){
					return super.dispatchKeyEvent(event);
				}
				if (menuWindow == null){
					setupMenu();
				}else{
					menuWindow.showAtLocation(this.findViewById(R.id.privacy_message_list_activity_bottom_linear),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		PrivacyMessagesItem item = (PrivacyMessagesItem)mAdapter.getItem(position);
//		String scheme = Constant.SCHEME_SMSTO;
//		Uri phoneUri = Uri.fromParts(scheme, item.getPhoneNumber(), null);
//		Intent intent = new Intent(Intent.ACTION_SENDTO, phoneUri);
//		startActivity(intent);
		Intent intent = new Intent(this, PrivacyDialogueDetailActivity.class);
		intent.putExtra(PrivacyDialogueDetailActivity.ACTION_TYPE, PrivacyDialogueDetailActivity.ACTION_MMS_REPLY);
		intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_TYPE, PrivacyDialogueDetailActivity.ACTION_FROM_PRIVACY);
		intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NUMBER, item.getPhoneNumber());
		intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, item.getPhoneNumber());
		startActivity(intent);
		if(item.getRead() == PrivacyMessageDataItem.READ_STATUS_UNREAD){
			item.setRead(PrivacyMessageDataItem.READ_STATUS_READ);
			mAdapter.notifyDataSetChanged();
		}
		boolean hasUnread = false;
		for(int i = 0; i < mAdapter.getCount(); i++){
			PrivacyMessagesItem mItem = (PrivacyMessagesItem)mAdapter.getItem(i);
			if(mItem.getRead() == PrivacyMessageDataItem.READ_STATUS_UNREAD){
				hasUnread = true;
			}
		}
		if(!hasUnread){
			PrivacyNotification pNotification = new PrivacyNotification(this);
			pNotification.cancelMmsNotification();
		}
		super.onListItemClick(l, v, position, id);
	}

	private OnClickListener menuListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.privacy_message_menu_delete:
//				PrivacyDBUtils.getInstance(PrivacyMessagesActivity.this).delMessages(PrivacyMessagesActivity.this);
				DelConfirmDialog confirmDialog = new DelConfirmDialog(PrivacyMessagesActivity.this.getParent(), R.style.Theme_CustomDialog);
				confirmDialog.show();
				mCurrentDialog = confirmDialog;
				break;
			case R.id.privacy_message_menu_restore:
				PrivacyDBUtils.getInstance(PrivacyMessagesActivity.this).restoreMessages(PrivacyMessagesActivity.this);
				loadListAdapter();
				break;
			
			}
			menuWindow.dismiss();
			menuShowed = false;

		}
	};
	private class DelConfirmDialog extends Dialog implements View.OnClickListener{
		private Button buttonOk;
		private Button buttonCancel;
		private View closeIv;

		public DelConfirmDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			initContentViews();
		}
		private void initContentViews(){
			setContentView(R.layout.confirm_dialog);
			buttonOk = (Button)findViewById(R.id.button_ok);
			buttonCancel = (Button)findViewById(R.id.button_cancel);
			closeIv = findViewById(R.id.dialog_close_iv);
			buttonOk.setOnClickListener(this);
			buttonCancel.setOnClickListener(this);
			closeIv.setOnClickListener(this);
			
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.button_ok:
				PrivacyDBUtils.getInstance(PrivacyMessagesActivity.this).delMessages(PrivacyMessagesActivity.this);
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
	public void onLock() {
		// TODO Auto-generated method stub
		if(mCurrentDialog != null && mCurrentDialog.isShowing()){
			mCurrentDialog.dismiss();
		}
		if (menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;
		
		}
	}

}
