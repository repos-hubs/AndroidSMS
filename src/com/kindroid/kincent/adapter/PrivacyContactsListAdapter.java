/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.R;

import java.util.List;

public class PrivacyContactsListAdapter extends BaseAdapter {
	private Context mContext;
	private List<PrivacyContactDataItem> mItems;
	private LayoutInflater mInflater;
	private static final String LAYOUT_FILE = "privacy_contact_list_item";
	public PrivacyContactsListAdapter(Context context, List<PrivacyContactDataItem> items){
		this.mContext = context;
		this.mItems = items;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public List<PrivacyContactDataItem> getItemsAll(){
		return mItems;
	}
	public void setItems(List<PrivacyContactDataItem> items){
		this.mItems = items;
	}
	public void addItem(PrivacyContactDataItem item){
		this.mItems.add(item);
	}
	public void delItem(PrivacyContactDataItem item){
		this.mItems.remove(item);
	}
	public void delItem(int position){
		this.mItems.remove(position);
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
			contentView = mInflater.inflate(R.layout.privacy_contact_list_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = initView();
		}
		CheckBox mSelectedCheckBox = (CheckBox)convertView.findViewById(R.id.privacy_contact_select_cb);
		TextView mNameTv = (TextView)convertView.findViewById(R.id.privacy_contact_item_name_tv);
		TextView mNumberTv = (TextView)convertView.findViewById(R.id.privacy_contact_item_phone_tv);
		TextView mBlockedTypeTv = (TextView)convertView.findViewById(R.id.privacy_contact_block_type_tv);
		PrivacyContactDataItem item = (PrivacyContactDataItem)getItem(position);
		if(item.isShowSelected()){
			mSelectedCheckBox.setVisibility(View.VISIBLE);
		}else{
			mSelectedCheckBox.setVisibility(View.GONE);
		}
		if(item.isSelected()){
			mSelectedCheckBox.setChecked(true);
		}else{
			mSelectedCheckBox.setChecked(false);
		}
		if(!TextUtils.isEmpty(item.getContactName())){
			mNameTv.setText(item.getContactName());
		}else{
			mNameTv.setText(item.getPhoneNumber());
		}
		
		mNumberTv.setText(item.getPhoneNumber());
		if(item.getBlockedType() == 0){
			mBlockedTypeTv.setText(R.string.privacy_contact_blocked_type_normal);
		}else{
			mBlockedTypeTv.setText(R.string.privacy_contact_blocked_type_hangup);
		}
		return convertView;
	}

}
