package com.kindroid.kincent.ui;

import java.util.ArrayList;
import java.util.List;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.DialogueMessagesSentAdapter;
import com.kindroid.kincent.adapter.ImageAdapter;
import com.kindroid.kincent.data.ContactInfo;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.SmsUtils;
import com.kindroid.kincent.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class KindroidMessengerSentActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "KindroidMessengerSentActivity";
	private static final int HANDLE_CONVERSITION_MESSAGE = 0;
	private static final int HANDLE_MESSAGE_SENT = 1;
	private static final int MESSAGE_NOT_NULL = 2;
	private static final int HANDLE_ADDRESS_NULL = 3;
	
	private Gallery mGallery;
	private ListView messagesListView; 								//信息列表
	private ImageView dialerImageView;
	private TextView contactsTitleTextView;							//联系人	
	private TextView senderTextView;      							//发送按钮
	private EditText messageEditText;								//发送的信息框
	
	private DialogueMessagesSentAdapter adapter;					//已发送的消息适配器
	private ImageAdapter imageAdapter; 								//功能栏Gallery Adapter
	
	private List contactsList = new ArrayList();  					//添加的联系人列表
	List<String> displayNameList = new ArrayList<String>();			//所有联系人的列表
	List<Long> threadIdsList = new ArrayList<Long>();				//所有联系人的会话ID
	List<String> addressList = new ArrayList<String>();        		//电话号码列表
	List<SmsMmsMessage> messagesList = new ArrayList<SmsMmsMessage>();		//信息数据
	
	private static final String LAYOUT_FILE = "sms_messengers_sent";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			setContentView(R.layout.sms_messengers_sent);
		}else{
			setContentView(contentView);
		}
		
		findViews();
		initComponents();
	}

	private void findViews() {
		mGallery = (Gallery) findViewById(R.id.galleryImages);
		messagesListView = (ListView) findViewById(R.id.msgDetailsListView);
		contactsList = KindroidMessengerWriteMessageActivity.getContactsList(); 
		contactsTitleTextView = (TextView) findViewById(R.id.contactsTextView);
		
		messageEditText = (EditText) findViewById(R.id.messageEditTextView);
		senderTextView = (TextView) findViewById(R.id.senderTextView);
		senderTextView.setOnClickListener(this);
	}
	//初始化组件
	private void initComponents() {
		//设置Gallery
		imageAdapter = new ImageAdapter(this);
		mGallery.setAdapter(imageAdapter);
		mGallery.setSelection(imageAdapter.getCount() / 2);
		
		if (null != contactsList && contactsList.size() > 0) {
			for (int i = 0; i < contactsList.size(); i++) {
				Object obj = contactsList.get(i);
				if (obj instanceof SmsMmsMessage) {
					SmsMmsMessage msgInfo = (SmsMmsMessage) obj;
					displayNameList.add(msgInfo.getContactName());
					addressList.add(msgInfo.getFromAddress());
				} else if (obj instanceof ContactInfo) {
					ContactInfo contactInfo = (ContactInfo) obj;
					displayNameList.add(contactInfo.getDisplayName());
					addressList.add(contactInfo.getAddress());
				}
			}
		}
		//初始化Title栏，填充联系人
		contactsTitleTextView.setText(PhoneUtils.formatList(displayNameList));
		new LoadThreadMessages().start();
	} 
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case HANDLE_CONVERSITION_MESSAGE:
				if (null != messagesList && messagesList.size() > 0) {
					adapter = new DialogueMessagesSentAdapter(KindroidMessengerSentActivity.this, messagesList);
					messagesListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
				break;
			case HANDLE_MESSAGE_SENT:
				if (msg.arg2 == MESSAGE_NOT_NULL) {
					Toast.makeText(KindroidMessengerSentActivity.this, getResources().getString(R.string.sms_msg_text_not_empty), Toast.LENGTH_SHORT).show();
				} else if (msg.arg2 == HANDLE_ADDRESS_NULL) {
					Toast.makeText(KindroidMessengerSentActivity.this, getResources().getString(R.string.sms_dialogue_contact_not_empty), Toast.LENGTH_SHORT).show();
				} else {
					messageEditText.setText("");
					new LoadThreadMessages().start();
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onClick(View view) {
		Animation mAnimation = AnimationUtils.loadAnimation(
				KindroidMessengerSentActivity.this,
				R.anim.sms_dialogue_func_text_click);
		switch (view.getId()) {
		case R.id.senderTextView:
//			senderTextView.startAnimation(mAnimation);			
			if (null == addressList || addressList.size() == 0) {
				Toast.makeText(KindroidMessengerSentActivity.this, getResources().getString(R.string.sms_dialogue_contact_not_empty), Toast.LENGTH_SHORT).show();
			} else {
				new SendMessagesThread().start();
			}
			break;

		default:
			break;
		}
	
	}
	
	/**
	 * 通过地址List查找会话Ids
	 * @param addressList
	 * @return
	 */
	private Long[] getThreadIdsByAddress(List<String> addressList) {
		Long[] threadIds = new Long[addressList.size()];
		if (null != addressList && addressList.size() > 0) {
			for (int i = 0; i < addressList.size(); i++) {
				threadIds[i] = MessengerUtils.findThreadIdFromAddress(KindroidMessengerSentActivity.this, addressList.get(i));
			}
		}
		return threadIds;
	}
	
	class LoadThreadMessages extends Thread {
		@Override
		public void run() {
			Long[] threadIds = getThreadIdsByAddress(addressList);
			List<SmsMmsMessage> tmpList = MessengerUtils.getThreadsByIds(KindroidMessengerSentActivity.this, threadIds);
			messagesList.addAll(tmpList);
			Message msg = mHandler.obtainMessage();
			msg.arg1 = HANDLE_CONVERSITION_MESSAGE;
			mHandler.sendMessage(msg);
		}
	}
	
	//开线程发送短信
	class SendMessagesThread extends Thread {

		@Override
		public void run() {
			String body = messageEditText.getText().toString();
			Message msg = mHandler.obtainMessage();
			msg.arg1 = HANDLE_MESSAGE_SENT;
			if (TextUtils.isEmpty(body)) {
				msg.arg2 = MESSAGE_NOT_NULL;
			} else {
				boolean sendSig = SmsUtils.getSignatureSwitch(KindroidMessengerSentActivity.this);
				if (sendSig) {
					String signiture = SmsUtils.getSignatureContent(KindroidMessengerSentActivity.this);
					if (null == signiture || "".equals(signiture)) {
						signiture = "";
					} else {
						signiture = "--" + signiture;
					}
					body += signiture;
				}
				boolean sendAppName = SmsUtils.getSendAppTag(KindroidMessengerSentActivity.this);
				if (sendAppName) {
					body += " " + getResources().getString(R.string.menu_setting_from_in_sms);
				}
				if (null != addressList && addressList.size() > 0) {
					for (int i = 0; i < addressList.size(); i++) {
						String address = addressList.get(i);
						PhoneUtils.sendMessages(KindroidMessengerSentActivity.this, address, body, SmsMmsMessage.MESSAGE_TYPE_SMS);
					}
				} else {
					msg.arg2 = HANDLE_ADDRESS_NULL;
				}
			}
			
			mHandler.sendMessage(msg);
		}		
	}
}
