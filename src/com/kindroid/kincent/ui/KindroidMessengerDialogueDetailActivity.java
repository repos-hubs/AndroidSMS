package com.kindroid.kincent.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.android.model.CollectCategory;
import com.kindroid.android.provider.Telephony;
import com.kindroid.android.util.dialog.DialogueDetailOperationDialog;
import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.DialogueMessageDetailAdapter;
import com.kindroid.kincent.adapter.ImageAdapter;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.notification.InterceptNotification;
import com.kindroid.kincent.ui.KindroidMessengerDialogueActivity.AysLoadMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueActivity.SmsBroadCastReceiver;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.SmsUtils;
import com.kindroid.kincent.util.MessengerUtils.ContactIdentification;
import com.kindroid.kincent.R;
import com.kindroid.security.util.CollectionDataBase;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.DateTimeUtil;
import com.kindroid.security.util.PhoneType;

public class KindroidMessengerDialogueDetailActivity extends Activity implements
		View.OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private final static String TAG = "KindroidMessengerDialogueDetailActivity";
	private final static int HANDLE_USER_PROFILE = 0;
	private final static int HANDLE_MESSAGES_DETAILS = 1;
	private final static int HANDLE_MESSAGE_SENT = 2;
	private final static int HANDLE_CONVERSITION = 3;
	private final static int HANDLE_ADDRESS_LOCATION = 4;

	private final static int MENU_DELETE = 0;
	private final static int MENU_COPY = 1;
	private final static int MENU_COLLECTION = 2;
	private final static int MENU_FORWARD = 3;

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

	private SmsMmsMessage msgInfo;
	private List<SmsMmsMessage> messagesList;
	private List<SmsMmsMessage> conversitionList;

	private GenealDailog dialog;

	private static final String LAYOUT_FILE = "sms_messengers_dialogue_details_main";

	private SmsBroadCastReceiver smsBroadCastReceiver;

	
	private boolean isActive;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentViews();
		registerBroactReciver();

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
		msgInfo = (SmsMmsMessage) getIntent().getSerializableExtra(
				"messageInfo");
		dialog = new GenealDailog(this, R.style.Theme_CustomDialog);
		// 外部App通过mimetype调用，所传过来的URI值
		String threadStr = getIntent().getDataString();
		if (msgInfo != null) {
			dialog.showDialog();
			new LoadMessages().start();
		} else if (null != threadStr) {
			if (threadStr.startsWith("sms:")) {

				String str[] = threadStr.split(":");
				if (str != null && str.length == 2) {
					msgInfo = new SmsMmsMessage();
					initMsgInfo(msgInfo, str[1]);
					dialog.showDialog();
					new LoadMessages().start();
				}
			}
		} else {
			finish();
		}
	}

	void initMsgInfo(SmsMmsMessage msgInfo, String address) {

		msgInfo.setFromAddress(address);

		msgInfo.setThreadId(MessengerUtils.findThreadIdFromAddress(
				KindroidMessengerDialogueDetailActivity.this,
				msgInfo.getFromAddress()));

		ContactIdentification contact = MessengerUtils
				.getPersonIdFromPhoneNumber(
						KindroidMessengerDialogueDetailActivity.this, address);

		if (contact != null) {
			msgInfo.setContactId(contact.contactId);
		}

		String name = MessengerUtils.getPersonName(
				KindroidMessengerDialogueDetailActivity.this,
				msgInfo.getContactId(), msgInfo.getFromAddress());
		msgInfo.setContactName(name);

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
		deleteImageView.setOnClickListener(this);
	}

	// 初始化组件
	private void initComponents() {
		// 设置Gallery
		imageAdapter = new ImageAdapter(this);
		mGallery.setAdapter(imageAdapter);
		mGallery.setSelection(imageAdapter.getCount() / 2);
		// mGallery.setOnItemClickListener(this);
		// 联系人ID不为空
		if (null != msgInfo.getContactId()
				&& !"".equals(msgInfo.getContactId())) {
			new LoadContactPhoto().start();
		}
		displayNameTextView.setText(msgInfo.getContactName());
		phoneNumTextView.setText(msgInfo.getFromAddress());

		if (msgInfo.getMessageType() == SmsMmsMessage.DRAFT) {
			senderEditTextView.setText(msgInfo.getMessageBody());
		}

		locationTextView.setText(msgInfo.getAddressLoaction());
	}

	public void showNotification() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new InterceptNotification(getApplication(),
						KindroidMessengerDialogueDetailActivity.this)
						.showInterceptNotification();
			}
		});
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
				dialog.disMisDialog();
				if (null != msg.obj) {
					Log.v(TAG, "msgList size:" + messagesList.size());
					msgAdapter = new DialogueMessageDetailAdapter(
							KindroidMessengerDialogueDetailActivity.this,
							messagesList);
					messagesListView.setAdapter(msgAdapter);
					messagesListView.setSelection(msgAdapter.getCount());
					msgAdapter.notifyDataSetChanged();

					new SetThreadRead().start();

				}
				initComponents();
				break;
			case HANDLE_MESSAGE_SENT:
				senderEditTextView.setText("");
				dialog.showDialog();
				new LoadMessages().start();
				break;
			case HANDLE_CONVERSITION:
				dialog.disMisDialog();
				if (null != conversitionList && conversitionList.size() > 0) {
					for (int i = 0; i < conversitionList.size(); i++) {
						msgInfo = conversitionList.get(i);
					}
					initComponents();
					new LoadAddressLocation(msgInfo.getFromAddress()).start();
					new LoadMessages().start();

					new SetThreadRead().start();
				}
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
			Message msg = mHandler.obtainMessage();
			Bitmap mPhoto = MessengerUtils.getPersonPhoto(
					KindroidMessengerDialogueDetailActivity.this,
					msgInfo.getContactLookupUri());
			msg.obj = mPhoto;
			msg.arg1 = HANDLE_USER_PROFILE;
			mHandler.sendMessage(msg);
		}
	}

	// 加载短信详情列表
	class LoadMessages extends Thread {

		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();

			 if (msgInfo.getThreadId() == 0) {
			 msgInfo.setThreadId(MessengerUtils.findThreadIdFromAddress(
			 KindroidMessengerDialogueDetailActivity.this,
			 msgInfo.getFromAddress()));
			 }
			Log.d(TAG, "thread id:" + msgInfo.getThreadId());
			messagesList = MessengerUtils.getMessagesByThreadId(
					KindroidMessengerDialogueDetailActivity.this,
					msgInfo.getThreadId());
			msg.obj = messagesList;
			msg.arg1 = HANDLE_MESSAGES_DETAILS;
			mHandler.sendMessage(msg);
		}
	}

	class SetThreadRead extends Thread {
		@Override
		public void run() {
			MessengerUtils.setThreadRead(
					KindroidMessengerDialogueDetailActivity.this,
					msgInfo.getThreadId(), false);
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
					fromStr = ConvertUtils
							.getCityNameByCode(
									KindroidMessengerDialogueDetailActivity.this,
									ConvertUtils
											.getCode(
													KindroidMessengerDialogueDetailActivity.this,
													type.getPhoneNo()));
				} else if (type.getPhonetype() == 1) {
					fromStr = ConvertUtils.getPhoneCity(
							KindroidMessengerDialogueDetailActivity.this,
							type.getPhoneNo());
				}
			}
			Message msg = mHandler.obtainMessage();
			msg.arg1 = HANDLE_ADDRESS_LOCATION;
			msg.obj = fromStr;
			mHandler.sendMessage(msg);
		}

	}

	// 通过外部APP加载Conversion
	class LoadMessagesFromUri extends Thread {
		String threadId;

		public LoadMessagesFromUri(String conId) {
			this.threadId = conId;
		}

		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();
			conversitionList = MessengerUtils.getConversitionByThread(
					KindroidMessengerDialogueDetailActivity.this, threadId);
			msg.obj = conversitionList;
			msg.arg1 = HANDLE_CONVERSITION;
			mHandler.sendMessage(msg);
		}
	}

	// 开线程发送短信
	class SendMessagesThread extends Thread {

		@Override
		public void run() {
			String address = msgInfo.getFromAddress();
			String body = senderEditTextView.getText().toString();
			boolean sendSig = SmsUtils
					.getSignatureSwitch(KindroidMessengerDialogueDetailActivity.this);
			if (sendSig) {
				String signiture = SmsUtils
						.getSignatureContent(KindroidMessengerDialogueDetailActivity.this);
				if (null == signiture || "".equals(signiture)) {
					signiture = "";
				} else {
					signiture = "--" + signiture;
				}
				body += signiture;
			}
			boolean sendAppName = SmsUtils
					.getSendAppTag(KindroidMessengerDialogueDetailActivity.this);
			if (sendAppName) {
				body += " "
						+ getResources().getString(
								R.string.menu_setting_from_in_sms);
			}
			
			boolean sent = PhoneUtils.sendMessages(
					KindroidMessengerDialogueDetailActivity.this, address,
					body, SmsMmsMessage.MESSAGE_TYPE_SMS);
			Message msg = mHandler.obtainMessage();
			msg.obj = sent;
			msg.arg1 = HANDLE_MESSAGE_SENT;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void onClick(View view) {
		Animation mAnimation = AnimationUtils.loadAnimation(
				KindroidMessengerDialogueDetailActivity.this,
				R.anim.sms_dialogue_func_text_click);
		switch (view.getId()) {
		case R.id.dialerImageView:
			Intent myIntentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + msgInfo.getFromAddress()));
			startActivity(myIntentDial);
			break;
		case R.id.senderTextView:
			String messageText = senderEditTextView.getText().toString();
			// senderTextView.startAnimation(mAnimation);
			if (TextUtils.isEmpty(messageText)) {
				Toast.makeText(
						KindroidMessengerDialogueDetailActivity.this,
						getResources().getString(
								R.string.sms_msg_text_not_empty),
						Toast.LENGTH_SHORT).show();
			} else {
				new SendMessagesThread().start();
			}
			break;
		case R.id.deleteImageView:
			if (null != msgInfo) {
				showConfirmDeleteConversitionDialog();
			}
			break;
		default:
			break;
		}
	}

	public void dial() {
		try {
			String address = msgInfo.getFromAddress();
			if (PhoneUtils.isPhoneNumberValid(address)) {
				Intent myIntentDial = new Intent("Intent.ACTION_CALL",
						Uri.parse("tel:" + address));
				startActivity(myIntentDial);
			} else {
				Toast.makeText(KindroidMessengerDialogueDetailActivity.this,
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
		//2012-1-31 mod
//		makeContextMenu(position);
		new DialogueDetailOperationDialog(KindroidMessengerDialogueDetailActivity.this, 
				R.style.Theme_CustomDialog, position, messagesList, msgAdapter).showDialog();
		return false;
	}

	/**
	 * 长按短信上下文对话框
	 * 
	 * @param position
	 *            Messages ListView单击的条目
	 */
	private void makeContextMenu(final int position) {
		final CharSequence[] items = getResources().getStringArray(
				R.array.dialogue_message_context_menu);
		final SmsMmsMessage msgInfo = messagesList.get(position);
		final long messageId = msgInfo.getMessageId();
		final long threadId = msgInfo.getThreadId();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.sms_dialogue_select_operations));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case MENU_DELETE:
					int result = MessengerUtils
							.deleteMessage(
									KindroidMessengerDialogueDetailActivity.this,
									messageId, threadId,
									SmsMmsMessage.MESSAGE_TYPE_SMS);
					if (result > 0) {
						messagesList.remove(position);
						msgAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
					break;
				case MENU_COPY:
					clipboard.setText(msgInfo.getMessageBody());
					dialog.dismiss();
					break;
				case MENU_COLLECTION:
					// 插入一条记录到收藏短信数据库表
					showCollectionCategoryDialog(position);
					dialog.dismiss();
					break;
				case MENU_FORWARD:
					Intent intent = new Intent(
							KindroidMessengerDialogueDetailActivity.this,
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

	private void showCollectionCategoryDialog(final int pos) {
		final List<CollectCategory> categoryList = CollectionDataBase.get(
				KindroidMessengerDialogueDetailActivity.this).getCategorys();
		if (null != categoryList && categoryList.size() > 0) {
			CharSequence[] items = PhoneUtils.formatCategoryList(categoryList)
					.split(",");
			final SmsMmsMessage msgInfo = messagesList.get(pos);
			final long messageId = msgInfo.getMessageId();
			final long threadId = msgInfo.getThreadId();
			final String messageBody = msgInfo.getMessageBody();
			final long timestamp = msgInfo.getTimestamp();
			final String addressStr = msgInfo.getFromAddress();
			final String dateStr = DateTimeUtil.long2String(timestamp, "MM/dd");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(
					R.string.sms_dialogue_select_category));
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					CollectCategory category = categoryList.get(item);
					CategorySmsListItem itemInfo = new CategorySmsListItem();
					itemInfo.setInsertTime(dateStr);
					itemInfo.setmAddress(addressStr);
					itemInfo.setmBody(messageBody);
					itemInfo.setmCategoryId(category.getmId());

					boolean ret = CollectionDataBase.get(
							KindroidMessengerDialogueDetailActivity.this)
							.insertCategorySmsItem(itemInfo);
					if(!ret){
						Toast.makeText(KindroidMessengerDialogueDetailActivity.this, R.string.collect_fail, Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(KindroidMessengerDialogueDetailActivity.this, R.string.collect_success, Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			Toast.makeText(
					KindroidMessengerDialogueDetailActivity.this,
					getResources().getString(
							R.string.sms_dialogue_detail_add_collection_first),
					Toast.LENGTH_SHORT).show();
			return;
		}

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

						MessengerUtils.deleteThread(
								KindroidMessengerDialogueDetailActivity.this,
								msgInfo.getThreadId(),
								SmsMmsMessage.MESSAGE_TYPE_MESSAGE);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsBroadCastReceiver);
		Intent intent = new Intent(Constant.BROACT_UPDATE_MESSAGE_DIALOG);

		sendBroadcast(intent);

	}
	
	
	private void registerBroactReciver(){
		// 生成广播处理
		smsBroadCastReceiver = new SmsBroadCastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter(
				Constant.BROACT_UPDATE_MESSAGE_DIALOG);
		// 注册广播
		registerReceiver(smsBroadCastReceiver, intentFilter);
	}
	
	
	
	// 注册广播接收
	public class SmsBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			new LoadMessages().start();

		}
	}

	
	

}
