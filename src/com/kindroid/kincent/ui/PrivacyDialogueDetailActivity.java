/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:jingmiao.li,heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.android.model.CollectCategory;
import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.DialogueMessageDetailAdapter;
import com.kindroid.kincent.adapter.ImageAdapter;
import com.kindroid.kincent.data.PrivacyMessageDataItem;
import com.kindroid.kincent.data.PrivacyMessagesItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity.LoadAddressLocation;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity.LoadContactPhoto;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity.LoadMessages;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity.LoadMessagesFromUri;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity.SendMessagesThread;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity.SetThreadRead;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;
import com.kindroid.security.util.CollectionDataBase;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.DateTimeUtil;
import com.kindroid.security.util.PhoneType;

import java.util.ArrayList;
import java.util.List;

public class PrivacyDialogueDetailActivity extends Activity implements
		View.OnClickListener, OnItemClickListener, OnItemLongClickListener, AutoLockActivityInterface {
	private final static String TAG = "PrivacyDialogueDetailActivity";
	private final static int HANDLE_USER_PROFILE = 0;
	private final static int HANDLE_MESSAGES_DETAILS = 1;
	private final static int HANDLE_MESSAGE_SENT = 2;
	private final static int HANDLE_CONVERSITION = 3;
	private final static int HANDLE_ADDRESS_LOCATION = 4;

	private Gallery mGallery;

	private ImageView userProfileImageView;
	private TextView displayNameTextView;
	private TextView phoneNumTextView;
	private TextView locationTextView;
	private ListView messagesListView; // 信息列表
	private ImageView dialerImageView;
	private EditText senderEditTextView; // 发送文本
	private TextView senderTextView; // 发送按钮
	private ImageView deleteImageView; // 删除会话按钮

	private ImageAdapter imageAdapter; // 功能栏Gallery Adapter
	private DialogueMessageDetailAdapter msgAdapter; // 消息详情Adapter
	private ClipboardManager clipboard; // 剪切板
	//
	// private SmsMmsMessage msgInfo;
	private List<SmsMmsMessage> messagesList;
	// private List<SmsMmsMessage> conversitionList;

	public static final String ACTION_SOURCE_TYPE = "action_source_type";
	public static final String ACTION_SOURCE_NUMBER = "action_source_number";
	public static final String ACTION_SOURCE_NAME = "action_source_name";
	public static final String ACTION_TYPE = "action_type";

	public static final int ACTION_FORM_INTERCEPT = 0;
	public static final int ACTION_FROM_PRIVACY = 1;

	public static final int ACTION_MMS_SEND = 0;
	public static final int ACTION_MMS_REPLY = 1;

	private int mActionType;
	private String mPhoneNumber;
	private String mPhoneName;
	private int mActionSourceType;

	private GenealDailog mGeneralDialog;
	private static final String LAYOUT_FILE = "sms_messengers_dialogue_details_main";
	
	private BroadcastReceiver smsReceiver;
	
	private boolean isActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentViews();
		
		smsReceiver = new PrivacySmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.PRIVACY_SMS_BROADCAST_ACTION);
		this.registerReceiver(smsReceiver, filter);
	}
	private class PrivacySmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try{
				String address = intent.getStringExtra(PrivateDBHelper.PrivateMsg.ADDRESS);
				if(!mPhoneNumber.contains(address)){
					PhoneType pt = ConvertUtils.getPhonetype(address);
					if(!mPhoneNumber.contains(pt.getPhoneNo())){
						return;
					}
				}
				String name = intent.getStringExtra(PrivateDBHelper.PrivateMsg.NAME);				
				String body = intent.getStringExtra(PrivateDBHelper.PrivateMsg.BODY);
				long receiveDate = intent.getLongExtra(PrivateDBHelper.PrivateMsg.DATE, System.currentTimeMillis());
				int receiveType = intent.getIntExtra(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE, PrivacyMessageDataItem.RECV_TYPE_INBOX);
				int read = intent.getIntExtra(PrivateDBHelper.PrivateMsg.READ, PrivacyMessageDataItem.READ_STATUS_UNREAD);
				SmsMmsMessage msg = new SmsMmsMessage();
				msg.setFromAddress(mPhoneNumber);
				msg.setMessageBody(body);
				msg.setMessageType(receiveType);
				if(isActive){
					msg.setReadType(PrivacyMessageDataItem.READ_STATUS_READ);
				}else{
					msg.setReadType(read);
				}
				msg.setTimestamp(receiveDate);
				if(messagesList == null){
					messagesList = new ArrayList<SmsMmsMessage>();
				}
				messagesList.add(msg);
				if(msgAdapter == null){
					msgAdapter = new DialogueMessageDetailAdapter(
							PrivacyDialogueDetailActivity.this, messagesList);
					messagesListView.setAdapter(msgAdapter);					
				}	
				messagesListView.setSelection(msgAdapter.getCount());
				msgAdapter.notifyDataSetChanged();
				
			}catch(Exception e){
				
			}
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isActive = true;
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
		this.unregisterReceiver(smsReceiver);
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
			setContentView(R.layout.sms_messengers_dialogue_details_main);
		} else {
			setContentView(contentView);
		}

		findViews();
		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		mActionType = getIntent().getIntExtra(ACTION_TYPE, 0);
		mActionSourceType = getIntent().getIntExtra(ACTION_SOURCE_TYPE, 0);
		mPhoneNumber = getIntent().getStringExtra(ACTION_SOURCE_NUMBER);
		mPhoneName = getIntent().getStringExtra(ACTION_SOURCE_NAME);
		initComponents();
//		if (mActionType == ACTION_MMS_REPLY) {
//			if (mActionSourceType == ACTION_FORM_INTERCEPT) {
//				fillDataFromIntercept();
//			} else if (mActionSourceType == ACTION_FROM_PRIVACY) {
//				fillDataFromPrivacy();
//				// 初始化自动锁时间
//				KindroidMessengerPrivacyActivity.timeForLock = System
//						.currentTimeMillis();
//				KindroidMessengerPrivacyActivity.registLockedActivity(this);
//			}
//		}
		if (mActionSourceType == ACTION_FORM_INTERCEPT) {
			fillDataFromIntercept();
		} else if (mActionSourceType == ACTION_FROM_PRIVACY) {
			fillDataFromPrivacy();
			// 初始化自动锁时间
			KindroidMessengerPrivacyActivity.timeForLock = System
					.currentTimeMillis();
			KindroidMessengerPrivacyActivity.updateLockTask();
			KindroidMessengerPrivacyActivity.registLockedActivity(this);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (mActionSourceType == ACTION_FROM_PRIVACY) {
			KindroidMessengerPrivacyActivity.timeForLock = System
					.currentTimeMillis();
			KindroidMessengerPrivacyActivity.updateLockTask();			
		}
		return super.dispatchTouchEvent(ev);
	}

	private void fillDataFromIntercept() {

	}

	private void fillDataFromPrivacy() {
		mGeneralDialog = new GenealDailog(this, R.style.Theme_CustomDialog);
		mGeneralDialog.showDialog();
		new LoadPrivacyMessages().start();
	}

	private class LoadPrivacyMessages extends Thread {
		public void run() {
			Message msg = mHandler.obtainMessage();
			messagesList = PrivacyDBUtils.getInstance(
					PrivacyDialogueDetailActivity.this).getSmsMmsMessages(
					PrivacyDialogueDetailActivity.this, mPhoneNumber);
			msg.obj = messagesList;
			msg.arg1 = HANDLE_MESSAGES_DETAILS;
			mHandler.sendMessage(msg);
		}
	}

	private void findViews() {
		mGallery = (Gallery) findViewById(R.id.galleryImages);
		userProfileImageView = (ImageView) findViewById(R.id.contactProfileImageView);
		displayNameTextView = (TextView) findViewById(R.id.displayNameTextView);
		phoneNumTextView = (TextView) findViewById(R.id.addressTextView);
		locationTextView = (TextView) findViewById(R.id.locationTextView);
		messagesListView = (ListView) findViewById(R.id.msgDetailsListView);
		messagesListView.setOnItemLongClickListener(this);

		dialerImageView = (ImageView) findViewById(R.id.dialerImageView);
		dialerImageView.setOnClickListener(this);

		senderEditTextView = (EditText) findViewById(R.id.messageEditTextView);
		senderTextView = (TextView) findViewById(R.id.senderTextView);
		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		int SimState = mTelephonyMgr.getSimState();
		if (SimState == TelephonyManager.SIM_STATE_READY) {
			senderTextView.setOnClickListener(this);
			senderEditTextView.setEnabled(true);
		}else {
			senderTextView.setClickable(false);
			senderEditTextView.setEnabled(false);
			Toast.makeText(this, R.string.sim_absent_prompt, Toast.LENGTH_SHORT).show();
		} 
		
		deleteImageView = (ImageView) findViewById(R.id.deleteImageView);
		if (mActionType == ACTION_MMS_SEND) {
			deleteImageView.setVisibility(View.GONE);
		} else {
			deleteImageView.setVisibility(View.VISIBLE);
			deleteImageView.setOnClickListener(this);
		}
	}

	// 初始化组件
	private void initComponents() {
		// 设置Gallery
		imageAdapter = new ImageAdapter(this);
		mGallery.setAdapter(imageAdapter);
		mGallery.setSelection(imageAdapter.getCount() / 2);
		// mGallery.setOnItemClickListener(this);
		if (!TextUtils.isEmpty(mPhoneName)) {
			displayNameTextView.setText(mPhoneName);
		} else {
			displayNameTextView.setText(mPhoneNumber);
		}
		phoneNumTextView.setText(mPhoneNumber);
		PhoneType pt = ConvertUtils.getPhonetype(mPhoneNumber);
		String mPhoneAddress = getPhoneAddress(pt);
		if (mPhoneAddress != null) {
			locationTextView.setText(mPhoneAddress);
		}
	}

	private String getPhoneAddress(PhoneType pt) {
		String cityName = null;
		if (pt.getPhonetype() == 2) {
			String pNumber = pt.getPhoneNo();
			try {
				cityName = ConvertUtils.getCityNameByCode(this,
						pNumber.substring(0, 3));
			} catch (Exception e) {
				cityName = null;
			}
			if (cityName == null) {
				try {
					cityName = ConvertUtils.getCityNameByCode(this,
							pNumber.substring(0, 4));
				} catch (Exception e) {
					cityName = null;
				}

			}

		} else if (pt.getPhonetype() == 1) {
			try {
				cityName = ConvertUtils.getPhoneCity(this, pt.getPhoneNo());
			} catch (Exception e) {
				cityName = null;
			}
		} else if (pt.getPhonetype() == 0) {
			cityName = ConvertUtils.getAddrForPhone(pt.getPhoneNo());

		}
		return cityName;
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case HANDLE_USER_PROFILE:
				if (null != msg.obj) {
					Bitmap mPhoto = (Bitmap) msg.obj;
					userProfileImageView.setImageBitmap(mPhoto);
				}
				break;
			case HANDLE_MESSAGES_DETAILS:
				if (null != msg.obj) {
					msgAdapter = new DialogueMessageDetailAdapter(
							PrivacyDialogueDetailActivity.this, messagesList);
					messagesListView.setAdapter(msgAdapter);
					messagesListView.setSelection(msgAdapter.getCount());
					msgAdapter.notifyDataSetChanged();
					// new SetThreadRead().start();
				}
				if (!PrivacyDBUtils.getInstance(
						PrivacyDialogueDetailActivity.this).hasUnreadMsg(
						PrivacyDialogueDetailActivity.this)) {
					PrivacyNotification pNotification = new PrivacyNotification(
							PrivacyDialogueDetailActivity.this);
					pNotification.cancelMmsNotification();
				}
				if (mGeneralDialog != null) {
					mGeneralDialog.disMisDialog();
				}
				// initComponents();
				break;
			case HANDLE_MESSAGE_SENT:
				if (mActionSourceType == ACTION_FROM_PRIVACY) {
					senderEditTextView.setText("");
					new LoadPrivacyMessages().start();
				}else{
					if(messagesList == null){
						messagesList = new ArrayList<SmsMmsMessage>();						
					}
					SmsMmsMessage message = new SmsMmsMessage();
					message.setFromAddress(mPhoneNumber);
					message.setMessageBody(senderEditTextView.getText().toString());
					senderEditTextView.setText("");
					message.setTimestamp(System.currentTimeMillis());
					messagesList.add(message);
					if(msgAdapter == null){
						msgAdapter = new DialogueMessageDetailAdapter(
								PrivacyDialogueDetailActivity.this, messagesList);
						messagesListView.setAdapter(msgAdapter);
						messagesListView.setSelection(msgAdapter.getCount());
					}else{
						msgAdapter.notifyDataSetChanged();
					}
					
				}
				break;
			case HANDLE_CONVERSITION:

				break;
			case HANDLE_ADDRESS_LOCATION:
				String fromAddress = (String) msg.obj;
				locationTextView.setText(fromAddress);
				break;
			default:
				break;
			}
		}
	};

	// 加载用户头像
	class LoadContactPhoto extends Thread {

		@Override
		public void run() {
			// Message msg = mHandler.obtainMessage();
			// Bitmap mPhoto =
			// MessengerUtils.getPersonPhoto(PrivacyDialogueDetailActivity.this,
			// msgInfo.getContactLookupUri());
			// msg.obj = mPhoto;
			// msg.arg1 = HANDLE_USER_PROFILE;
			// mHandler.sendMessage(msg);
		}
	}

	// 加载短信详情列表
	class LoadMessages extends Thread {

		@Override
		public void run() {
			// Message msg = mHandler.obtainMessage();
			// Log.d(TAG, "thread id:" + msgInfo.getThreadId());
			// messagesList =
			// MessengerUtils.getMessagesByThreadId(PrivacyDialogueDetailActivity.this,
			// msgInfo.getThreadId());
			// msg.obj = messagesList;
			// msg.arg1 = HANDLE_MESSAGES_DETAILS;
			// mHandler.sendMessage(msg);
		}
	}

	class SetThreadRead extends Thread {
		@Override
		public void run() {

		}
	}

	// 加载电话所属地区
	class LoadAddressLocation extends Thread {
		String addressStr;

		public LoadAddressLocation(String address) {
			this.addressStr = address;
		}

		@Override
		public void run() {
			String fromStr = "";
			if (null != ConvertUtils.getAddrForPhone(addressStr)
					&& !"".equals(ConvertUtils.getAddrForPhone(fromStr))) {
				fromStr = ConvertUtils.getAddrForPhone(fromStr);
			} else {
				PhoneType type = ConvertUtils.getPhonetype(addressStr);
				if (type.getPhonetype() == 2) {
					fromStr = ConvertUtils.getCityNameByCode(
							PrivacyDialogueDetailActivity.this, ConvertUtils
									.getCode(
											PrivacyDialogueDetailActivity.this,
											addressStr));
				} else if (type.getPhonetype() == 1) {
					fromStr = ConvertUtils.getPhoneCity(
							PrivacyDialogueDetailActivity.this, addressStr);
				}
			}
			Message msg = mHandler.obtainMessage();
			msg.arg1 = HANDLE_ADDRESS_LOCATION;
			msg.obj = fromStr;
			mHandler.sendMessage(msg);
		}

	}

	// 开线程发送短信
	class SendMessagesThread extends Thread {

		@Override
		public void run() {
			String address = mPhoneNumber;
			String body = senderEditTextView.getText().toString();
			boolean sent = PhoneUtils.sendMessages(
					PrivacyDialogueDetailActivity.this, address, body,
					SmsMmsMessage.MESSAGE_TYPE_SMS);
			Message msg = mHandler.obtainMessage();
			msg.obj = sent;
			msg.arg1 = HANDLE_MESSAGE_SENT;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.dialerImageView:
			Intent myIntentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + mPhoneNumber));
			startActivity(myIntentDial);
			break;
		case R.id.senderTextView:
			String messageText = senderEditTextView.getText().toString();
			if (TextUtils.isEmpty(messageText)) {
				Toast.makeText(
						PrivacyDialogueDetailActivity.this,
						getResources().getString(
								R.string.sms_msg_text_not_empty),
						Toast.LENGTH_SHORT).show();
			} else {
				new SendMessagesThread().start();
			}
			break;
		case R.id.deleteImageView:
			if (null != mPhoneNumber) {
				showConfirmDeleteConversitionDialog();
			}
			break;
		default:
			break;
		}
	}

	public void dial() {
		try {
			String address = mPhoneNumber;
			if (PhoneUtils.isPhoneNumberValid(address)) {
				Intent myIntentDial = new Intent("Intent.ACTION_CALL",
						Uri.parse("tel:" + address));
				startActivity(myIntentDial);
			} else {
				Toast.makeText(PrivacyDialogueDetailActivity.this,
						"pnone number error", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		makeContextMenu(position);
		return false;
	}
	private void showCollectionCategoryDialog(final int pos) {
		final List<CollectCategory> categoryList = CollectionDataBase.get(PrivacyDialogueDetailActivity.this).getCategorys();
		if (null != categoryList && categoryList.size() > 0) {
			CharSequence[] items = PhoneUtils.formatCategoryList(categoryList).split(",");
			final SmsMmsMessage msgInfo = messagesList.get(pos);
			final long messageId = msgInfo.getMessageId();
			final long threadId = msgInfo.getThreadId();
			final String messageBody = msgInfo.getMessageBody();
			final long timestamp = msgInfo.getTimestamp();
			final String addressStr = msgInfo.getFromAddress();
			final String dateStr = DateTimeUtil.long2String(timestamp, "MM/dd");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);  
			builder.setTitle(getResources().getString(R.string.sms_dialogue_select_category));  
			builder.setItems(items, new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int item) {  
			    	CollectCategory category = categoryList.get(item);
			    	CategorySmsListItem itemInfo = new CategorySmsListItem();
			    	itemInfo.setInsertTime(dateStr);
			    	itemInfo.setmAddress(addressStr);
			    	itemInfo.setmBody(messageBody);
			    	itemInfo.setmCategoryId(category.getmId());
			    	
			    	boolean ret = CollectionDataBase.get(PrivacyDialogueDetailActivity.this).insertCategorySmsItem(itemInfo);
			    	if(!ret){
						Toast.makeText(PrivacyDialogueDetailActivity.this, R.string.collect_fail, Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(PrivacyDialogueDetailActivity.this, R.string.collect_success, Toast.LENGTH_SHORT).show();
					}
			    	dialog.dismiss();
			    }  
			});  
			AlertDialog alert = builder.create();  
			alert.show();
		} else {
			Toast.makeText(PrivacyDialogueDetailActivity.this,
					getResources().getString(R.string.sms_dialogue_detail_add_collection_first), Toast.LENGTH_SHORT).show();
			return;
		}
		
	}
	private void makeContextMenu(final int position) {
		final CharSequence[] items = getResources().getStringArray(
				R.array.privacy_message_context_menu);
		final SmsMmsMessage msgInfo = messagesList.get(position);
		final long messageId = msgInfo.getMessageId();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.sms_dialogue_select_operations));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					PrivacyDBUtils pdbUtils = PrivacyDBUtils
							.getInstance(PrivacyDialogueDetailActivity.this);
					int result = pdbUtils.delSmsMmsMessage(
							PrivacyDialogueDetailActivity.this, messageId);
					if (result > 0) {
						messagesList.remove(position);
						msgAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
					break;
				case 1:
					clipboard.setText(msgInfo.getMessageBody());
					dialog.dismiss();
					break;
				case 2:
					//插入一条记录到收藏短信数据库表
					showCollectionCategoryDialog(position);
					dialog.dismiss();
					break;
				case 3:
					Intent intent = new Intent(
							PrivacyDialogueDetailActivity.this,
							KindroidMessengerWriteMessageActivity.class);
					intent.putExtra(
							KindroidMessengerWriteMessageActivity.FORWARD_MESSAGE_KEY,
							msgInfo.getMessageBody());
					startActivity(intent);
					dialog.dismiss();
					break;

				default:
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void delMessagesFromIntercept() {

	}

	private void delMessagesFromPrivacy() {
		PrivacyDBUtils.getInstance(this).delMessages(this, mPhoneNumber);
	}

	/**
	 * 删除确认框
	 */
	private void showConfirmDeleteConversitionDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(super.getApplicationContext().getString(
				R.string.sms_dialog_delete_thread));
		builder.setPositiveButton(R.string.sms_contact_confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (mActionSourceType == ACTION_FORM_INTERCEPT) {
							delMessagesFromIntercept();
						} else if (mActionSourceType == ACTION_FROM_PRIVACY) {
							delMessagesFromPrivacy();
						}
						finish();
					}
				});
		builder.setNegativeButton(R.string.sms_contact_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked Cancel so do some stuff */
					}
				});
		builder.show();
	}

	@Override
	public void onLock() {
		// TODO Auto-generated method stub
		finish();
	}

	/*
	 * private int showConfirmDeleteMsgDialog(final long messageId, final long
	 * threadId) { Builder builder = new AlertDialog.Builder(this);
	 * builder.setTitle(super.getApplicationContext().getString(
	 * R.string.sms_dialog_delete_thread)); //
	 * builder.setMessage(super.getApplicationContext
	 * ().getString(R.string.msg_quit));
	 * builder.setPositiveButton(R.string.sms_contact_confirm, new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface arg0, int arg1) {
	 * MessengerUtils
	 * .deleteMessage(KindroidMessengerDialogueDetailActivity.this, messageId,
	 * threadId, SmsMmsMessage.MESSAGE_TYPE_SMS); } });
	 * builder.setNegativeButton(R.string.sms_contact_cancel, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int whichButton) { User clicked Cancel so do some stuff } });
	 * builder.show(); }
	 */

}
