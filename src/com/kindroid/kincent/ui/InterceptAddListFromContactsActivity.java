package com.kindroid.kincent.ui;
/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.09
 * Description:
 */

//import com.kindroid.security.adapter.AddBlackListAdapter;
//import com.kindroid.security.model.BlackListItem;
import com.kindroid.android.model.BlackListItem;
import com.kindroid.android.model.NativeCursor;
import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.AddBlackWhiteListFromContactAdapter;
import com.kindroid.kincent.util.LoadListAdapterThread;
import com.kindroid.kincent.R;

import com.kindroid.security.util.Constant;
import com.kindroid.security.util.InterceptDataBase;

//import com.kindroid.security.util.LoadListAdapterThread;
//import com.kindroid.security.util.NativeCursor;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InterceptAddListFromContactsActivity extends ListActivity {
	private TextView mContactsSum;
	private TextView mActivityTitle;
	private TextView mActionText;
	private CheckBox mSelectAllCheckBox;
	
	private View mAddActionLinear;
	

	private int mType;
	private int mSourceType;
	private AddBlackWhiteListFromContactAdapter mListAdapter;
	public static final int FINISH_LOAD_FROM_CONTACTS = 1;
	public static final int LOAD_FROM_SIM_ERROR = 2;
	public static final int FINISH_LOAD_FROM_SIM = 3;
	public static final int FINISH_LOAD_FROM_CALL_LOG = 4;
	public static final int FINISH_LOAD_FROM_SMS = 5;
	public static final int NO_SIM_EXIST = 6;
	
	public static final String BLACK_OR_WHITE = "black_or_white";
	public static final String SOURCE_TYPE = "source_type";
	
	private boolean mOnlyChangeState = false;
	
	
	private GenealDailog mGeneralDailog;
	
	private static final String LAYOUT_FILE = "intercept_add_list_from_contacts";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
			setContentView(R.layout.intercept_add_list_from_contacts);
		}else{
			setContentView(contentView);
		}
		
		mType = getIntent().getIntExtra(BLACK_OR_WHITE, 1);
		mSourceType = getIntent().getIntExtra(SOURCE_TYPE, 1);
		mGeneralDailog=new GenealDailog(this, R.style.Theme_CustomDialog);
		
		findView();
		loadListAdapter();
	}
	private void findView(){
		mContactsSum = (TextView)findViewById(R.id.contacts_sum);
		mContactsSum.setVisibility(View.GONE);
		mActivityTitle = (TextView)findViewById(R.id.activity_title_tv);
		mActivityTitle.getPaint().setFakeBoldText(true);
		mActionText = (TextView)findViewById(R.id.add_list_action_tv);
		mActionText.getPaint().setFakeBoldText(true);
		if(mType == 1){
			mActivityTitle.setText(R.string.add_black_list);
			mActionText.setText(R.string.add_black_list);
		}else{
			mActivityTitle.setText(R.string.add_white_list);
			mActionText.setText(R.string.add_white_list);
		}
		mSelectAllCheckBox = (CheckBox)findViewById(R.id.select_al_cb);		
		mSelectAllCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(!mOnlyChangeState){
					mListAdapter.selectAllItem(isChecked);
					mListAdapter.notifyDataSetChanged();
				}
			}
			
		});
		
		mAddActionLinear = findViewById(R.id.add_list_linear);
		mAddActionLinear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addListFromAdapter();
			}
		});
	}
	
	
	private void loadListAdapter(){
		mGeneralDailog.showDialog();
		mListAdapter = new AddBlackWhiteListFromContactAdapter(this);
		LoadListAdapterThread loadThread = new LoadListAdapterThread(this, handler, mSourceType, mListAdapter);
		loadThread.start();
	}

	private void addListFromAdapter(){
		List<BlackListItem> items = new ArrayList<BlackListItem>();
		for(int i = 0; i < mListAdapter.getCount(); i++){
			BlackListItem item = (BlackListItem)mListAdapter.getItem(i);
			if(item.isSelected()){
				items.add(item);
			}
		}
		if(items.size() == 0){
			Toast.makeText(this, R.string.add_list_no_select, Toast.LENGTH_LONG).show();
			return;
		}
		addListToDB(items);		
		//sendBroadcast(new Intent(Constant.BROACTUPDATEINTERCEPT));
		finish();
	}
	private void addListToDB(List<BlackListItem> items){
		InterceptDataBase mInterDB = InterceptDataBase.get(this);
		boolean allIsExist = true;
		for(BlackListItem item : items){
			NativeCursor nc = new NativeCursor();
			nc.setmRequestType(mType);
			nc.setmContactName(item.getContactName());
			nc.setmPhoneNum(item.getPhoneNumber());			
			nc = InterceptDataBase.get(this).selectIsExists(nc);
			if (!nc.ismIsExists()) {
				allIsExist = false;
				String contactName = item.getContactName();
				if(contactName != null && contactName.equals(item.getPhoneNumber())){
					contactName = "";
				}
				mInterDB.insertBlackWhitList(mType, contactName, 
						item.getPhoneNumber(), true, true, 1);
			}
		}
		if(allIsExist){
			Toast.makeText(this, R.string.all_exist_when_addlist, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this, R.string.add_list_complete, Toast.LENGTH_LONG).show();
		}

	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			mGeneralDailog.disMisDialog();
			
			switch (msg.what) {
			case FINISH_LOAD_FROM_CONTACTS:
				setListAdapter(mListAdapter);
				mContactsSum.setText(String.format(getString(R.string.contacts_exist), mListAdapter.getCount()));
				mContactsSum.setVisibility(View.VISIBLE);
				getListView().setVisibility(View.VISIBLE);
				break;
			case LOAD_FROM_SIM_ERROR:
				mContactsSum.setText(String.format(getString(R.string.contacts_exist), 0));
				mContactsSum.setVisibility(View.VISIBLE);
				getListView().setVisibility(View.VISIBLE);
				Toast.makeText(InterceptAddListFromContactsActivity.this, R.string.load_from_sim_error, Toast.LENGTH_LONG).show();
				break;
			case FINISH_LOAD_FROM_SIM:
				setListAdapter(mListAdapter);
				mContactsSum.setText(String.format(getString(R.string.sim_contacts_exist), mListAdapter.getCount()));
				mContactsSum.setVisibility(View.VISIBLE);
				getListView().setVisibility(View.VISIBLE);
				break;
			case FINISH_LOAD_FROM_CALL_LOG:
				setListAdapter(mListAdapter);
				mContactsSum.setText(String.format(getString(R.string.call_log_contacts_exist), mListAdapter.getCount()));
				mContactsSum.setVisibility(View.VISIBLE);
				getListView().setVisibility(View.VISIBLE);
				break;
			case FINISH_LOAD_FROM_SMS:
				setListAdapter(mListAdapter);
				mContactsSum.setText(String.format(getString(R.string.sms_contacts_exist), mListAdapter.getCount()));
				mContactsSum.setVisibility(View.VISIBLE);
	
				getListView().setVisibility(View.VISIBLE);
				break;
			}
		}
	};


	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		CheckBox select_cb = (CheckBox)v.findViewById(R.id.select_cb);
		select_cb.setChecked(!select_cb.isChecked());
		BlackListItem item = (BlackListItem)mListAdapter.getItem(position);
		item.setSelected(select_cb.isChecked());
		boolean mAllSelected = true;
		for(int i = 0; i < mListAdapter.getCount(); i++){
			item = (BlackListItem)mListAdapter.getItem(i);
			if(!item.isSelected()){
				mAllSelected = false;
			}
		}
		mOnlyChangeState = true;
		mSelectAllCheckBox.setChecked(mAllSelected);
		mOnlyChangeState = false;
		//super.onListItemClick(l, v, position, id);
		
	}

	

}
