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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PrivacyContactImportAdapter extends BaseAdapter {
	private Context mContext;
	private List<PrivacyContactDataItem> mItems;
	private static final String LAYOUT_FILE = "privacy_contact_import_list_item";
	public PrivacyContactImportAdapter(Context context){
		this.mContext = context;
		mItems = new ArrayList<PrivacyContactDataItem>();
	}
	
	public PrivacyContactImportAdapter(Context context, List<PrivacyContactDataItem> items){
		this.mContext = context;
		this.mItems = items;
	}
	public void sortItemsByContactName(final boolean desc){
		Collections.sort(mItems, new Comparator<PrivacyContactDataItem>() {
			@Override
			public int compare(PrivacyContactDataItem object1,
					PrivacyContactDataItem object2) {
				// TODO Auto-generated method stub
				Locale locale = mContext.getResources().getConfiguration().locale;
				Collator myCollator = Collator.getInstance(locale);
				String name1 = object1.getContactName();
				String name2 = object2.getContactName();
				if (desc) {
					if (myCollator.compare(name1, name2) < 0) {
						return -1;
					} else if (myCollator.compare(name1, name2) > 0) {
						return 1;
					} else {
						return 0;
					}
				} else {
					if (myCollator.compare(name2, name1) < 0) {
						return -1;
					} else if (myCollator.compare(name2, name1) > 0) {
						return 1;
					} else {
						return 0;
					}
				}
			}

		});
	}
	public void addItemsAll(Collection<PrivacyContactDataItem> collection){
		this.mItems.addAll(collection);
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
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = mInflater.inflate(R.layout.privacy_contact_import_list_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = initView();
		}
		TextView nameTv = (TextView)convertView.findViewById(R.id.privacy_contact_import_item_name_tv);
		TextView despTv = (TextView)convertView.findViewById(R.id.privacy_contact_import_item_desp_tv);
		final CheckBox importCb = (CheckBox)convertView.findViewById(R.id.privacy_contact_import_item_cb);
		View outLinear = convertView.findViewById(R.id.privacy_contact_import_item_linear);
		
		final PrivacyContactDataItem item = (PrivacyContactDataItem)getItem(position);
		if(!TextUtils.isEmpty(item.getContactName())){
			nameTv.setText(item.getContactName());
		}else{
			nameTv.setText(item.getPhoneNumber());
		}
		despTv.setText(item.getDesp());
		importCb.setChecked(item.isSelected());
		
		return convertView;
	}

}
