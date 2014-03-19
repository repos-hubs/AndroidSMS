package com.kindroid.kincent.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.kindroid.kincent.adapter.DialogueMessengerListAdapter;
import com.kindroid.kincent.adapter.FavoriteContactsListAdapter;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;

public class KindroidMessengerActivity extends Activity implements View.OnClickListener {
	private final static String TAG = "KindroidMessengerActivity";
	private final static int HANDLE_MESSAGES = 0;
	private final static int HANDLE_CONTACTS = 1;
	
	private ListView messengerListView;
	private ListView contactPersonListView;	
	private LinearLayout dialogListLayout;
	private LinearLayout tabContainerLayout;
	private ImageView rightArrowImageView;
	private View classifyLayout;
//	private ImageView tabControlImageView;
	private TextView smsTabTitleTextView;
	private DialogueMessengerListAdapter adapter;
	private FavoriteContactsListAdapter contactAdapter;
	
	List<SmsMmsMessage> dataList;
	List<SmsMmsMessage> contactsList;
	private boolean classifyTag = false;
	private boolean tabControlTag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.sms_messengers_main);
//		findViews();
//		new LoadingMessagesContext().start();
//		new LoadingFavoriteContact().start();
	}
	
	private void findViews() {
		classifyLayout = (View) findViewById(R.id.classifyLayout);
        
		messengerListView = (ListView) findViewById(R.id.msgListView);
		contactPersonListView = (ListView) findViewById(R.id.favoriteContactPersonListView);
		rightArrowImageView = (ImageView) findViewById(R.id.classifySmsImageView);
		rightArrowImageView.setOnClickListener(this);
	//	tabControlImageView = (ImageView) findViewById(R.id.tabControlImageView);
	//	tabControlImageView.setOnClickListener(this);
		dialogListLayout = (LinearLayout) messengerListView.getParent();
		tabContainerLayout = (LinearLayout) findViewById(R.id.tabContainerLayout);
		smsTabTitleTextView = (TextView) findViewById(R.id.smsTabTitleTextView);
		smsTabTitleTextView.setOnClickListener(this);
		//set linearlayout params
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int mScreenWidth = metric.widthPixels;
		
		dialogListLayout.setLayoutParams(new LayoutParams(mScreenWidth, LayoutParams.FILL_PARENT)); 
		dialogListLayout.setBackgroundResource(R.color.bg_color);
		dialogListLayout.requestLayout();
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case HANDLE_MESSAGES:
				
				if (null != dataList && dataList.size() > 0) {
					adapter = new DialogueMessengerListAdapter(KindroidMessengerActivity.this, dataList);
					messengerListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
				
				break;
			case HANDLE_CONTACTS:
				
				if (null != contactsList && contactsList.size() > 0) {
					contactAdapter = new FavoriteContactsListAdapter(KindroidMessengerActivity.this, contactsList);
					contactPersonListView.setAdapter(contactAdapter);
					contactAdapter.notifyDataSetChanged();
				}
				
				break;

			default:
				break;
			}
		}
		
	};
	
	class LoadingMessagesContext extends Thread {

		@Override
		public void run() {
			dataList = MessengerUtils.getMessages(KindroidMessengerActivity.this);
//			dataList = MessengerUtils.getFavoriteContacts(KindroidMessengerActivity.this);
			Message msg = mHandler.obtainMessage();
			msg.obj = dataList;
			msg.arg1 = HANDLE_MESSAGES;
			mHandler.sendMessage(msg);
		}
	}
	
	class LoadingFavoriteContact extends Thread {
		
		@Override
		public void run() {
			contactsList = MessengerUtils.getFavoriteContacts(KindroidMessengerActivity.this, true);
			Message msg = mHandler.obtainMessage();
			msg.obj = contactsList;
			msg.arg1 = HANDLE_CONTACTS;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.classifySmsImageView:
			classifyTag = !classifyTag;
			if (classifyTag) {
				classifyLayout.setVisibility(View.VISIBLE);
				rightArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_classify_left_arrow_icon));
			} else {
				classifyLayout.setVisibility(View.GONE);
				rightArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_classify_right_arrow_icon));
			}
			break;
//		case R.id.tabControlImageView:
//			tabControlTag = !tabControlTag;
//			if (tabControlTag) {
//				tabContainerLayout.setVisibility(View.VISIBLE);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_down_arrow_icon));
//			} else {
//				tabContainerLayout.setVisibility(View.GONE);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_up_arrow_icon));
//			}
//			break;
		case R.id.smsTabTitleTextView:
			tabControlTag = !tabControlTag;
			if (tabControlTag) {
				tabContainerLayout.setVisibility(View.VISIBLE);
			//	tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_down_arrow_icon));
			} else {
				tabContainerLayout.setVisibility(View.GONE);
			//	tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_up_arrow_icon));
			}
			break;

		default:
			break;
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}
