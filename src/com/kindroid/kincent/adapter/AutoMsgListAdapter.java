/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.AutoMsgDataItem;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.util.List;

public class AutoMsgListAdapter extends BaseAdapter implements View.OnClickListener{
	private Context mContext;
	private List<AutoMsgDataItem> mItems;
	private LayoutInflater mInflater;
	private int mPosition = -1;
	
	private static final String LAYOUT_FILE = "privacy_change_auto_msg_list_item";
	
	public AutoMsgListAdapter(Context context, List<AutoMsgDataItem> mItems){
		this.mContext = context;
		this.mItems = mItems;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void addItem(AutoMsgDataItem item){
		mItems.add(item);
	}
	public void delItem(AutoMsgDataItem item){
		mItems.remove(item);
	}
	public void delItem(int position){
		mItems.remove(position);
	}
	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	private View initView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = inflater.inflate(R.layout.privacy_change_auto_msg_list_item, null);
		} 
		return contentView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = initView();
		}
		TextView contentTv = (TextView)convertView.findViewById(R.id.privacy_auto_msg_content_tv);
		RadioButton selectedRb = (RadioButton)convertView.findViewById(R.id.privacy_auto_msg_selected_radion_button);
		View selectLinear = convertView.findViewById(R.id.privacy_auto_msg_list_item_select_linear);
		View delLinear = convertView.findViewById(R.id.privacy_auto_msg_list_item_del_linear);
		View editLinear = convertView.findViewById(R.id.privacy_auto_msg_list_item_edit_linear);
		View actionLinear = convertView.findViewById(R.id.privacy_auto_msg_action_linear);
		
		AutoMsgDataItem item = (AutoMsgDataItem)getItem(position);
		contentTv.setText(item.getMsg());
		if(item.getFlag() == 1){
			selectedRb.setChecked(true);
		}else{
			selectedRb.setChecked(false);
		}
		selectLinear.setOnClickListener(this);
		delLinear.setOnClickListener(this);
		editLinear.setOnClickListener(this);
		
		if (mPosition == position) {
			actionLinear.setVisibility(View.VISIBLE);
		}else{
			actionLinear.setVisibility(View.GONE);		
		}
		
		return convertView;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.privacy_auto_msg_list_item_select_linear:
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
			pdbUtils.setAutoMsg(mContext, (AutoMsgDataItem)getItem(mPosition));
			for(int i = 0; i < getCount(); i++){
				AutoMsgDataItem item = (AutoMsgDataItem)getItem(i);
				if(i == mPosition){
					item.setFlag(1);
				}else{
					item.setFlag(0);
				}
			}
			this.setPosition(-1);
			notifyDataSetChanged();
			break;
		case R.id.privacy_auto_msg_list_item_del_linear:
			pdbUtils = PrivacyDBUtils.getInstance(mContext);
			pdbUtils.delAutoMsg(mContext, (AutoMsgDataItem)getItem(mPosition));
			AutoMsgDataItem item = (AutoMsgDataItem)getItem(mPosition);
			if(item.getFlag() == 1){
				if(getCount() > 1){
					((AutoMsgDataItem)getItem(0)).setFlag(1);
				}
			}
			delItem(mPosition);			
			this.setPosition(-1);
			notifyDataSetChanged();
			break;
		case R.id.privacy_auto_msg_list_item_edit_linear:
			EditAutoMsgDialog editDialog = new EditAutoMsgDialog(mContext, R.style.Theme_CustomDialog);
			editDialog.show();
			break;		
		}

	}
	private class EditAutoMsgDialog extends Dialog implements View.OnClickListener{
		private Button mButtonOk;
		private Button mButtonCancel;
		private EditText mMsgEditEt;

		public EditAutoMsgDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			setContentView(R.layout.privacy_auto_msg_edit_dialog);
			mButtonOk = (Button)findViewById(R.id.button_ok);
			mButtonCancel = (Button)findViewById(R.id.button_cancel);
			mMsgEditEt = (EditText)findViewById(R.id.privacy_auto_msg_edit_et);
			AutoMsgDataItem item = (AutoMsgDataItem)getItem(getPosition());
			if(item != null){
				mMsgEditEt.setText(item.getMsg());
			}
			mButtonOk.setOnClickListener(this);
			mButtonCancel.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.button_ok:
				String newMsgContent = mMsgEditEt.getText().toString().trim();
				if(newMsgContent.equals("")){
					Toast.makeText(mContext, R.string.privacy_change_text_empty_prompt, Toast.LENGTH_LONG).show();
					return;
				}
				AutoMsgDataItem item = (AutoMsgDataItem)getItem(mPosition);
				item.setMsg(newMsgContent);
				PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(mContext);
				pdbUtils.updateAutoMsg(mContext, item);
				AutoMsgListAdapter.this.notifyDataSetChanged();
				dismiss();
				break;
			case R.id.button_cancel:
				dismiss();
				break;
			}
		}
		
	}

}
