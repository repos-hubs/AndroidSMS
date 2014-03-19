/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;

public class AutoLockSettingAdapter extends BaseAdapter {
	private Context mContext;
	private String[] items;
	private int selected;
	
	private static final String LAYOUT_FILE = "privacy_change_auto_lock_list_item";
	public AutoLockSettingAdapter(Context context, int selected){
		mContext = context;
		this.selected = selected;
		items = mContext.getResources().getStringArray(R.array.auto_lock_times);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items[position];
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
			contentView = inflater.inflate(R.layout.privacy_change_auto_lock_list_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = initView();
		}
		TextView timeTv = (TextView)convertView.findViewById(R.id.auto_lock_item_text);
		timeTv.setText(items[position]);
		RadioButton timeRb = (RadioButton)convertView.findViewById(R.id.auto_lock_item_rb);
		if(selected == position){
			timeRb.setChecked(true);
		}else{
			timeRb.setChecked(false);
		}
		return convertView;
	}

}
