package com.kindroid.kincent.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.DialogueContactsSelectingListAdapter;
import com.kindroid.kincent.adapter.DialogueMessageDetailAdapter;
import com.kindroid.kincent.adapter.DialogueMsgWritingContactsAdapter;
import com.kindroid.kincent.adapter.ImageAdapter;
import com.kindroid.kincent.data.ContactInfo;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity.LoadMessages;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.util.SmsUtils;
import com.kindroid.kincent.R;

public class KindroidMessengerWriteMessageActivity extends Activity implements View.OnClickListener, OnItemClickListener {
	private final static String TAG = "KindroidMessengerWriteMessageActivity";
	public final static int REQUEST_CODE = 0;
	public final static int HANDLE_MESSAGE_SENT = 0;
	public final static int REDIRECT_TO_CONVERSITION_DETAIL = 0;
	public final static int REDIRECT_TO_MULTI_CONVERSITION_DETAIL = 1;
	public final static int MESSAGE_NOT_NULL = 2;
	public final static String FORWARD_MESSAGE_KEY = "forward_message";
	
	private Gallery mGallery;
	private ImageView addContactImageView;
	private GridView contactGridView;
	private ImageView contactImageView;
	private EditText contactEditText;
	private ImageView arrowImageView;
	private RelativeLayout gridViewLayout;
	private ListView contactListView;					//联系人列表
	private LinearLayout footView;
	private Button confirmButton;
	private Button cancelButton;
	private TextView senderTextView;      				//发送按钮
	private EditText messageEditText;					//发送的信息框
	
	private ImageAdapter imageAdapter; 					//功能栏Gallery Adapter
	private List contactList = new ArrayList();  		//添加的联系人列表
	private DialogueMsgWritingContactsAdapter adapter;  //GridView adapter
	private DialogueContactsSelectingListAdapter contactAdapter;//ListView adapter
	private static KindroidMessengerWriteMessageActivity instance;
	
	private boolean arrowStatus = false;                //箭头图片单击的状态
	private String forwardMessage;						//要转发的信息内容
	
	private static final String LAYOUT_FILE = "sms_messengers_write_message_main";
	private static final String FOOT_LAYOUT_FILE = "sms_contacts_foot";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentViews();
		instance = this;
	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.sms_messengers_write_message_main);
		}else{
			setContentView(contentView);
		}		
		
		forwardMessage = getIntent().getStringExtra(FORWARD_MESSAGE_KEY);
		findViews();
		initComponents();
	}
	private View initFootView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(FOOT_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = layoutInflater.inflate(R.layout.sms_contacts_foot, null);
		} 
		return contentView;
	}
	private void findViews() {
		mGallery = (Gallery) findViewById(R.id.galleryImages);
		addContactImageView = (ImageView) findViewById(R.id.addContactsImageView);
		addContactImageView.setOnClickListener(this);
		
		contactGridView = (GridView) findViewById(R.id.contactsGridView);
		contactGridView.setOnItemClickListener(this);
		contactImageView = (ImageView) findViewById(R.id.contactImageView);
		contactEditText = (EditText) findViewById(R.id.contactsEditText);
		arrowImageView = (ImageView) findViewById(R.id.arrowImageView);
		arrowImageView.setOnClickListener(this);
		
		contactListView = (ListView) findViewById(R.id.contactsListView);
		
		gridViewLayout = (RelativeLayout) findViewById(R.id.gridViewLayout);
		
		footView = (LinearLayout) initFootView();
		confirmButton = (Button) footView.findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(this);
		cancelButton = (Button) footView.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
		contactListView.addFooterView(footView);
		
		senderTextView = (TextView) findViewById(R.id.senderTextView);
		messageEditText = (EditText) findViewById(R.id.messageEditTextView);
		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		int SimState = mTelephonyMgr.getSimState();
		if (SimState == TelephonyManager.SIM_STATE_READY) {
			senderTextView.setOnClickListener(this);
			messageEditText.setEnabled(true);
		}else  {
			senderTextView.setClickable(false);
			messageEditText.setEnabled(false);
			Toast.makeText(this, R.string.sim_absent_prompt, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void initComponents() {
		//设置Gallery
		imageAdapter = new ImageAdapter(this);
		mGallery.setAdapter(imageAdapter);
		mGallery.setSelection(imageAdapter.getCount() / 2);
		
		if (null != forwardMessage && !"".equals(forwardMessage)) {
			messageEditText.setText(forwardMessage);
		}
	}
	
	public static List getContactsList() {
		return instance.getContactsListImpl();
	}
	
	private List getContactsListImpl() {
		return contactList;
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case HANDLE_MESSAGE_SENT:
				//FIXME:跳转到会话详细列表
				if (msg.arg2 == REDIRECT_TO_CONVERSITION_DETAIL) {
					String address = contactEditText.getText().toString();
					PrivacyContactDataItem pcdi = SafeUtils.getPrivacyContact(address);
					if(pcdi == null){
						/*if (null != contactList && contactList.size() > 0) {
							
							for (int i = 0; i < contactList.size(); i++) {
								Object obj = contactList.get(i);
								if (obj instanceof SmsMmsMessage) {
									SmsMmsMessage msgInfo = (SmsMmsMessage) obj;
									address = msgInfo.getFromAddress();
								} else if (obj instanceof ContactInfo) {
									ContactInfo contactInfo = (ContactInfo) obj;
									address = contactInfo.getAddress();
								}
							}
						}*/
						long threadId = MessengerUtils.findThreadIdFromAddress(KindroidMessengerWriteMessageActivity.this, address);
						Long[] threadIds = {threadId};
						List<SmsMmsMessage> tmpList = MessengerUtils.getThreadsByIds(KindroidMessengerWriteMessageActivity.this, threadIds);
						SmsMmsMessage msgInfo = null;
						if (null != tmpList && tmpList.size() > 0) {
							for (int i = 0; i < tmpList.size(); i++) {
								msgInfo = tmpList.get(i);
							}
						}
						if (null != msgInfo) {
							Intent intent = new Intent(KindroidMessengerWriteMessageActivity.this, KindroidMessengerDialogueDetailActivity.class);
							intent.putExtra("messageInfo", msgInfo);
							startActivity(intent);
						}
					}else{
						Intent intent = new Intent(KindroidMessengerWriteMessageActivity.this, PrivacyDialogueDetailActivity.class);
						intent.putExtra(PrivacyDialogueDetailActivity.ACTION_TYPE, PrivacyDialogueDetailActivity.ACTION_MMS_SEND);
						intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_TYPE, PrivacyDialogueDetailActivity.ACTION_FROM_PRIVACY);
						intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NUMBER, pcdi.getPhoneNumber());
						if(TextUtils.isEmpty(pcdi.getContactName())){
							intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, pcdi.getContactName());
						}else{
							intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, pcdi.getPhoneNumber());
						}
						startActivity(intent);
					}
					finish();
				} else if (msg.arg2 == REDIRECT_TO_MULTI_CONVERSITION_DETAIL) {
					//FIXME:跳转到群发详细列表
					Intent intent = new Intent(KindroidMessengerWriteMessageActivity.this, KindroidMessengerSentActivity.class);
					startActivity(intent);
					finish();
				} else if (msg.arg2 == MESSAGE_NOT_NULL) {
					Toast.makeText(KindroidMessengerWriteMessageActivity.this, getResources().getString(R.string.sms_msg_text_not_empty), Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.addContactsImageView:
			Intent intent = new Intent(KindroidMessengerWriteMessageActivity.this, KindroidMessengerContactSelectingActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
			//clear contacts edit text
			contactEditText.setText("");
			break;
		case R.id.arrowImageView:
			arrowStatus = !arrowStatus;
			if (arrowStatus) {
				contactListView.setVisibility(View.VISIBLE);
			} else {
				if (null != contactList && contactList.size() > 0) {
					
					contactListView.setVisibility(View.GONE);
				}
			}
			break;
		case R.id.confirmButton:
			List tmpList = dealSelectedChkList(contactList);
			contactList.clear();
			contactList.addAll(tmpList);
			contactListView.setVisibility(View.GONE);
			
			if (null == contactList || contactList.size() == 0) {
				gridViewLayout.setVisibility(View.GONE);
				contactEditText.setVisibility(View.VISIBLE);
				contactImageView.setVisibility(View.VISIBLE);
			}
			adapter.notifyDataSetChanged();
			contactAdapter.notifyDataSetChanged();
			break;
		case R.id.cancelButton:
			contactListView.setVisibility(View.GONE);
			break;
			
		case R.id.senderTextView:
			Animation mAnimation = AnimationUtils.loadAnimation(
					KindroidMessengerWriteMessageActivity.this,
					R.anim.sms_dialogue_func_text_click);
//			senderTextView.startAnimation(mAnimation);
			String contactStr = contactEditText.getText().toString();
			if ((null == contactList || contactList.size() == 0) && TextUtils.isEmpty(contactStr)) {
				Toast.makeText(KindroidMessengerWriteMessageActivity.this, getResources().getString(R.string.sms_dialogue_contact_not_empty), Toast.LENGTH_SHORT).show();
			} else {
				new SendMessagesThread().start();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_CODE == requestCode && resultCode == KindroidMessengerContactSelectingActivity.RESULT_CODE) {
			Bundle bundle = data.getExtras();
			int contactType = bundle.getInt("contact_type");
			List tmpList = KindroidMessengerContactSelectingActivity.getContactsList();
			tmpList = removeSameContacts(tmpList);
			contactList.addAll(tmpList);
			if (null != contactList && contactList.size() > 0) {
				gridViewLayout.setVisibility(View.VISIBLE);
				contactEditText.setVisibility(View.GONE);
				contactImageView.setVisibility(View.GONE);
			} else {
				gridViewLayout.setVisibility(View.GONE);
				contactEditText.setVisibility(View.VISIBLE);
				contactImageView.setVisibility(View.VISIBLE);
			}
			
			adapter = new DialogueMsgWritingContactsAdapter(KindroidMessengerWriteMessageActivity.this, contactList);
			contactGridView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			
			contactAdapter = new DialogueContactsSelectingListAdapter(KindroidMessengerWriteMessageActivity.this, contactList, false, 0);
			contactListView.setAdapter(contactAdapter);
			contactAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.contactsGridView:
			contactList.remove(position);
			if (null == contactList || contactList.size() == 0) {
				gridViewLayout.setVisibility(View.GONE);
				contactEditText.setVisibility(View.VISIBLE);
				contactImageView.setVisibility(View.VISIBLE);
			}
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}
	
	//开线程发送短信
	class SendMessagesThread extends Thread {

		@Override
		public void run() {
			String contactStr = contactEditText.getText().toString();
			String body = messageEditText.getText().toString();
			Message msg = mHandler.obtainMessage();
			msg.arg1 = HANDLE_MESSAGE_SENT;
			if (TextUtils.isEmpty(body)) {
				msg.arg2 = MESSAGE_NOT_NULL;
			} else {
				boolean sendSig = SmsUtils.getSignatureSwitch(KindroidMessengerWriteMessageActivity.this);
				if (sendSig) {
					String signiture = SmsUtils.getSignatureContent(KindroidMessengerWriteMessageActivity.this);
					if (null == signiture || "".equals(signiture)) {
						signiture = "";
					} else {
						signiture = "--" + signiture;
					}
					body += signiture;
				}
				boolean sendAppName = SmsUtils.getSendAppTag(KindroidMessengerWriteMessageActivity.this);
				if (sendAppName) {
					body += " " + getResources().getString(R.string.menu_setting_from_in_sms);
				}
				if (null != contactList && contactList.size() > 0) {
					for (int i = 0; i < contactList.size(); i++) {
						Object obj = contactList.get(i);
						if (obj instanceof SmsMmsMessage) {
							SmsMmsMessage msgInfo = (SmsMmsMessage) obj;
							PhoneUtils.sendMessages(KindroidMessengerWriteMessageActivity.this, msgInfo.getFromAddress(), body, SmsMmsMessage.MESSAGE_TYPE_SMS);
						} else if (obj instanceof ContactInfo) {
							ContactInfo contactInfo = (ContactInfo) obj;
							PhoneUtils.sendMessages(KindroidMessengerWriteMessageActivity.this, contactInfo.getAddress(), body, SmsMmsMessage.MESSAGE_TYPE_SMS);
						}
					}
					msg.arg2 = REDIRECT_TO_MULTI_CONVERSITION_DETAIL;
				} else if (!TextUtils.isEmpty(contactStr)) {
					PhoneUtils.sendMessages(KindroidMessengerWriteMessageActivity.this, contactStr, body, SmsMmsMessage.MESSAGE_TYPE_SMS);
					msg.arg2 = REDIRECT_TO_CONVERSITION_DETAIL;
				}
			}
			
			mHandler.sendMessage(msg);
		}		
	}
	
	/**
	 * 保留选中的联系人
	 * @param list
	 */
	private List dealSelectedChkList(List list) {
		List tmpList = new ArrayList();
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if (obj instanceof SmsMmsMessage) {
					SmsMmsMessage msgInfo = (SmsMmsMessage) obj;
					if (msgInfo.isChkBoxSelected()) {
						tmpList.add(msgInfo);
					}
				} else if (obj instanceof ContactInfo) {
					ContactInfo contactInfo = (ContactInfo) obj;
					if (contactInfo.isChkBoxSelected()) {
						tmpList.add(contactInfo);
					}
				}
			}
		}
		return tmpList;
	}
	
	//去除相同联系人
	private List removeSameContacts(List list) {
		List tmpList = new ArrayList();
		Set set = new HashSet();
		if (null != contactList && contactList.size() > 0) {
			for (int j = 0; j < contactList.size(); j++) {
				Object contactObj = contactList.get(j);
				if (contactObj instanceof SmsMmsMessage) {
					SmsMmsMessage msgInfo = (SmsMmsMessage) contactObj;
					set.add(msgInfo.getFromAddress());
				} else if (contactObj instanceof ContactInfo) {
					ContactInfo contactInfo = (ContactInfo) contactObj;
					set.add(contactInfo.getAddress());
				}
			}
			
			if (null != list && list.size() > 0) {
				
				for (int i = 0; i < list.size(); i++) {
					Object obj = list.get(i);
					if (obj instanceof SmsMmsMessage) {
						SmsMmsMessage msgInfo = (SmsMmsMessage) obj;
						if (!set.contains(msgInfo.getFromAddress())) {
							tmpList.add(msgInfo);
						}
					} else if (obj instanceof ContactInfo) {
						ContactInfo contactInfo = (ContactInfo) obj;
						if (!set.contains(contactInfo.getAddress())) {
							tmpList.add(contactInfo);
						}
					}
				}
			}
		} else {
			return list;
		}
		
		return tmpList;
	}
}
