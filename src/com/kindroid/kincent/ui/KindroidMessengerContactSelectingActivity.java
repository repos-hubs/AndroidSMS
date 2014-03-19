package com.kindroid.kincent.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.DialogueContactsSelectingListAdapter;
import com.kindroid.kincent.data.ContactInfo;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;

public class KindroidMessengerContactSelectingActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "KindroidMessengerContactSelectingActivity";
	private static final int HANDLE_CONTACTS = 0;
	private static final int HANDLE_RECENT_CONTACTS = 1;
	private static final int HANDLE_GROUPS = 2;
	
	public static final int CONTACTS_TYPE = 0;
	public static final int GROUP_TYPE = 1;
	public static final int RECENT_TYPE = 2;
	public static final int RESULT_CODE = 1;
	
	private ListView dataListView;
	
	private LinearLayout contactsLayout;
	private ImageView contactsImageView;
	private TextView contactsTextView;
	
	private LinearLayout groupLayout;
	private ImageView groupImageView;
	private TextView groupTextView;
	
	private LinearLayout recentContactLayout;
	private ImageView recentContactImageView;
	private TextView recentContactTextView;
	private EditText searchEditText;			//搜索联系人编辑框
	
	private Button confirmButton;
	private Button cancelButton;
	
	private DialogueContactsSelectingListAdapter contactAdapter;
	
	private List contactList = new ArrayList(); 				//联系人数据
	private List favoriteContactsList = new ArrayList(); 		//最近联系人数据
	private List tmpContactList = new ArrayList(); 				//临时存储联系人数据
	private List groupList = new ArrayList();					//群组数据
	private int current = 0;
	
	private static KindroidMessengerContactSelectingActivity instance;
	private static final String LAYOUT_FILE = "sms_messengers_contacts_selecting";
	
	private GenealDailog mGeneralDailog;
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
			setContentView(R.layout.sms_messengers_contacts_selecting);
		}else{
			setContentView(contentView);
		}
		
		findViews();
		initComponents();
		mGeneralDailog = new GenealDailog(this, R.style.Theme_CustomDialog);
		mGeneralDailog.showDialog();
		new LoadingContacts().start();
	}

	private void findViews() {
		dataListView = (ListView) findViewById(R.id.contactsListView);
		contactsImageView = (ImageView) findViewById(R.id.contactsImageView);
		contactsLayout = (LinearLayout) findViewById(R.id.contactsLayout);
		contactsLayout.setOnClickListener(this);
		contactsTextView = (TextView) findViewById(R.id.contactsTextView);
		
		groupLayout = (LinearLayout) findViewById(R.id.groupsLayout);
		groupLayout.setOnClickListener(this);
		groupImageView = (ImageView) findViewById(R.id.groupsImageView);
		groupTextView = (TextView) findViewById(R.id.groupsTextView);
		
		recentContactLayout = (LinearLayout) findViewById(R.id.recentLayout);
		recentContactLayout.setOnClickListener(this);
		recentContactImageView = (ImageView) findViewById(R.id.recentImageView);
		recentContactTextView = (TextView) findViewById(R.id.recentTextView);
		
		confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
		
		searchEditText = (EditText) findViewById(R.id.contactsEditText);
		/*
		dataListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Object obj = dataListView.getItemAtPosition(position);
				CheckBox chkBoxImageView = (CheckBox) parent.findViewById(R.id.contactSelectImageView);
				boolean newChecked = !chkBoxImageView.isChecked();
				chkBoxImageView.setChecked(newChecked);
				if (obj instanceof ContactInfo) {
					ContactInfo contactInfo = (ContactInfo)obj;
					contactInfo.setChkBoxSelected(newChecked);
				} else {
					SmsMmsMessage msgInfo = (SmsMmsMessage)obj;
					msgInfo.setChkBoxSelected(newChecked);						
				}
			}
			
		});
		*/
	} 
	
	private void initComponents() {
//		contactsTextView.setTextColor(getResources().getColor(R.color.white));
		contactsTextView.setSelected(true);
		contactsImageView.setImageResource(R.drawable.sms_add_contact_contacts_on);
		
		searchEditText.addTextChangedListener(textWatcher);
	}
	
	public static List getContactsList() {
		return instance.getContactsListImpl();
	}
	
	TextWatcher textWatcher = new TextWatcher(){

		@Override
		public void beforeTextChanged(CharSequence charSeq, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence charSeq, int start, int before, int count) {
			if (charSeq.length() > 0) {
				new SearchingContacts().start();
			} else {
				new LoadingContacts().start();
			}
		}

		@Override
		public void afterTextChanged(Editable editTable) {
			if (editTable.length() == 0) {
				new LoadingContacts().start();
			}
		}
		
	};
	
	private List getContactsListImpl() {
		if (current == CONTACTS_TYPE) {
			return dealSelectedChkList(contactList);
		} else if (current == GROUP_TYPE) {
			return dealSelectedChkList(contactList);
		} else {
			return dealSelectedChkList(favoriteContactsList);
		}
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			
			case HANDLE_CONTACTS:
				
				if (null != contactList && contactList.size() > 0) {
					dataListView.setVisibility(View.VISIBLE);
					contactAdapter = new DialogueContactsSelectingListAdapter(KindroidMessengerContactSelectingActivity.this, contactList, true, 0);
					dataListView.setAdapter(contactAdapter);
				}
				if (null != contactAdapter) {
					contactAdapter.notifyDataSetChanged();
				}
				if (mGeneralDailog != null) {
					mGeneralDailog.disMisDialog();
				}
				break;
			case HANDLE_RECENT_CONTACTS:
				if (null != favoriteContactsList && favoriteContactsList.size() > 0) {
					dataListView.setVisibility(View.VISIBLE);
					contactAdapter = new DialogueContactsSelectingListAdapter(KindroidMessengerContactSelectingActivity.this, favoriteContactsList, false, 0);
					dataListView.setAdapter(contactAdapter);
				} else {
					dataListView.setVisibility(View.INVISIBLE);
				}
				if (null != contactAdapter) {
					contactAdapter.notifyDataSetChanged();
				}
				break;
			case HANDLE_GROUPS:
				if (null != groupList && groupList.size() > 0) {
					dataListView.setVisibility(View.VISIBLE);
					contactAdapter = new DialogueContactsSelectingListAdapter(KindroidMessengerContactSelectingActivity.this, groupList, true, 1);
					dataListView.setAdapter(contactAdapter);
				} else {
					dataListView.setVisibility(View.INVISIBLE);
				}
				if (null != contactAdapter) {
					contactAdapter.notifyDataSetChanged();
				}
				if (mGeneralDailog != null) {
					mGeneralDailog.disMisDialog();
				}
			default:
				break;
			}
		}
		
	};
	//加载通讯录联系人
	class LoadingContacts extends Thread {
		@Override
		public void run() {
			List<ContactInfo> tmpList = MessengerUtils.getContactsData(KindroidMessengerContactSelectingActivity.this);
			contactList = sortContactList(tmpList);
			tmpContactList = contactList;
			Message msg = mHandler.obtainMessage();
			msg.obj = contactList;
			msg.arg1 = HANDLE_CONTACTS;
			mHandler.sendMessage(msg);
		}
	}
	
	//搜索联系人
	class SearchingContacts extends Thread {
		@Override
		public void run() {
			String letters = searchEditText.getText().toString();
			contactList = MessengerUtils.getContactsByLetters(KindroidMessengerContactSelectingActivity.this, letters);
			Message msg = mHandler.obtainMessage();
			msg.obj = contactList;
			msg.arg1 = HANDLE_CONTACTS;
			mHandler.sendMessage(msg);
		}
	}
	
	//加载最近联系人
	class LoadingFavoriteContact extends Thread {
		@Override
		public void run() {
			favoriteContactsList = MessengerUtils.getFavoriteContacts(KindroidMessengerContactSelectingActivity.this, false);
			Message msg = mHandler.obtainMessage();
			msg.obj = favoriteContactsList;
			msg.arg1 = HANDLE_RECENT_CONTACTS;
			mHandler.sendMessage(msg);
		}
	}
	
	//加载群组
	class LoadingGroups extends Thread {
		@Override
		public void run() {
			groupList = MessengerUtils.getContactGroups(KindroidMessengerContactSelectingActivity.this);
			Message msg = mHandler.obtainMessage();
			msg.arg1 = HANDLE_GROUPS;
			mHandler.sendMessage(msg);
		}
	}
	
	//开线程搜索群组下联系人
	class LoadContactsByGroup extends Thread {
		@Override
		public void run() {
			if (null != groupList && groupList.size() > 0) {
				for (int i = 0; i < groupList.size(); i++) {
					ContactInfo info = (ContactInfo) groupList.get(i);
					if (info.isChkBoxSelected()) {
						List tmpList = MessengerUtils.getContactsByGroupId(KindroidMessengerContactSelectingActivity.this, info.getGroupId());
						contactList.addAll(tmpList);
						if (null != contactList && contactList.size() > 0) {
							for (int j = 0; j < contactList.size(); j++) {
								ContactInfo infos = (ContactInfo) contactList.get(j);
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.contactsLayout:
			current = CONTACTS_TYPE;
			if(contactList == null || contactList.size() == 0){
				mGeneralDailog = new GenealDailog(this, R.style.Theme_CustomDialog);
				mGeneralDailog.showDialog();
				new LoadingContacts().start();
			}else{
				dataListView.setVisibility(View.VISIBLE);
				contactAdapter = new DialogueContactsSelectingListAdapter(KindroidMessengerContactSelectingActivity.this, contactList, true, 0);
				dataListView.setAdapter(contactAdapter);
				contactAdapter.notifyDataSetChanged();
			}
			setGreyStatus();
//			contactsTextView.setTextColor(getResources().getColor(R.color.white));
			contactsTextView.setSelected(true);
			contactsImageView.setImageResource(R.drawable.sms_add_contact_contacts_on);
			break;
		case R.id.groupsLayout:
			current = GROUP_TYPE;
			setGreyStatus();
//			groupTextView.setTextColor(getResources().getColor(R.color.white));
			groupTextView.setSelected(true);
			groupImageView.setImageResource(R.drawable.sms_add_contact_group_on);
			if(groupList == null || groupList.size() == 0){
				mGeneralDailog = new GenealDailog(this, R.style.Theme_CustomDialog);
				mGeneralDailog.showDialog();
				new LoadingGroups().start();
			}else{
				dataListView.setVisibility(View.VISIBLE);
				contactAdapter = new DialogueContactsSelectingListAdapter(KindroidMessengerContactSelectingActivity.this, groupList, true, 1);
				dataListView.setAdapter(contactAdapter);
				contactAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.recentLayout:
			current = RECENT_TYPE;
			setGreyStatus();
			recentContactImageView.setImageResource(R.drawable.sms_add_contact_recently_on);
//			recentContactTextView.setTextColor(getResources().getColor(R.color.white));
			recentContactTextView.setSelected(true);
			new LoadingFavoriteContact().start();
			break;
		case R.id.confirmButton:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			
			switch (current) {
			case CONTACTS_TYPE:
				bundle.putInt("contact_type", CONTACTS_TYPE);
				break;
			case GROUP_TYPE:
				bundle.putInt("contact_type", GROUP_TYPE);
				getContactByGroup();
				break;
			case RECENT_TYPE:
				bundle.putInt("contact_type", RECENT_TYPE);
				break;

			default:
				break;
			}
			intent.putExtras(bundle);   
			this.setResult(RESULT_CODE, intent);
			if (null != getContactsListImpl() && getContactsListImpl().size() > 0) {
				this.finish();
			} else {
				Toast.makeText(KindroidMessengerContactSelectingActivity.this,
						getResources().getString(R.string.sms_dialogue_contact_not_empty), Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.cancelButton:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	//加载群组下联系人
	private void getContactByGroup() {
		if (null != groupList && groupList.size() > 0) {
			for (int i = 0; i < groupList.size(); i++) {
				ContactInfo info = (ContactInfo) groupList.get(i);
				if (info.isChkBoxSelected()) {
					List tmpList = MessengerUtils.getContactsByGroupId(KindroidMessengerContactSelectingActivity.this, info.getGroupId());
					contactList.addAll(tmpList);
					if (null != contactList && contactList.size() > 0) {
						for (int j = 0; j < contactList.size(); j++) {
							ContactInfo infos = (ContactInfo) contactList.get(j);
						}
					}
				}
			}
		}
	}
	
	//排序通讯录
	private List<ContactInfo> sortContactList(List<ContactInfo> list) {
		Collections.sort(list, new Comparator<ContactInfo>() {
			@Override
			public int compare(ContactInfo object1, ContactInfo object2) {
					return object1.getFirstCNLetter().compareToIgnoreCase(object2.getFirstCNLetter());
			}

		});
		
		//存放首字母的容器，只存储相同的首字母一次
		List<ContactInfo> tmpList = null;
		if (null != list && list.size() > 0) {
			Set<String> letterSet = new HashSet<String>();
			tmpList = new ArrayList<ContactInfo>();
			for (int i = 0; i < list.size(); i++) {
				ContactInfo info = list.get(i);
				if (!letterSet.contains(info.getFirstCNLetter())) {
					letterSet.add(info.getFirstCNLetter());
				} else {
					info.setFirstCNLetter("");
				}
				tmpList.add(info);
			}
		}
		return tmpList;
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
	
	private void setGreyStatus() {
//		contactsTextView.setTextColor(getResources().getColor(R.color.grey));
		contactsTextView.setSelected(false);
//		groupTextView.setTextColor(getResources().getColor(R.color.grey));
		groupTextView.setSelected(false);
//		recentContactTextView.setTextColor(getResources().getColor(R.color.grey));
		recentContactTextView.setSelected(false);
		contactsImageView.setImageResource(R.drawable.sms_add_contact_contacts_off);
		groupImageView.setImageResource(R.drawable.sms_add_contact_group_off);
		recentContactImageView.setImageResource(R.drawable.sms_add_contact_recently_off);
	}
	
}
