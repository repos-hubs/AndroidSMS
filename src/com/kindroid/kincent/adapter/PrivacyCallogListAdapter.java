/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyCallogDataItem;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PrivacyCallogListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PrivacyCallogDataItem> mItems;
	
	private static final String LAYOUT_FILE = "privacy_callog_list_item";
	public PrivacyCallogListAdapter(Context context){
		this.mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems = new ArrayList<PrivacyCallogDataItem>();
	}
	public void sortItems(){
		Collections.sort(mItems, new Comparator<PrivacyCallogDataItem>() {
			@Override
			public int compare(PrivacyCallogDataItem object1,
					PrivacyCallogDataItem object2) {
				// TODO Auto-generated method stub
				if(object1.getDate() > object2.getDate()){
					return -1;
				}else{
					return 1;
				}
			}

		});
	}
	public void clearItems(){
		mItems.clear();
	}
	public void addItemsAll(Collection<PrivacyCallogDataItem> collection){
		mItems.addAll(collection);
	}
	public void addItem(PrivacyCallogDataItem item){
		mItems.add(item);
	}
	public void delItem(PrivacyCallogDataItem item){
		mItems.remove(item);
	}
	public void delItem(int position){
		mItems.remove(position);
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
			contentView = mInflater.inflate(R.layout.privacy_callog_list_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){			
			convertView = initView();
		}
		TextView mNameTv = (TextView)convertView.findViewById(R.id.privacy_callog_list_item_contact_tv);
		TextView mDateTv = (TextView)convertView.findViewById(R.id.privacy_callog_list_item_date_tv);
		ImageView mCallInIv = (ImageView)convertView.findViewById(R.id.privacy_callog_list_item_call_in_iv);
		ImageView mCallOutIv = (ImageView)convertView.findViewById(R.id.privacy_callog_list_item_call_out_iv);
		TextView mNumberTv = (TextView)convertView.findViewById(R.id.privacy_callog_list_item_number_tv);
		TextView mNumberAddressTv = (TextView)convertView.findViewById(R.id.privacy_callog_list_item_phone_address_tv);
		
		PrivacyCallogDataItem item = mItems.get(position);
		if(!TextUtils.isEmpty(item.getName())){
			mNameTv.setText(item.getName());
		}else{
			mNameTv.setText(item.getPhoneNumber());
		}
		int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |  DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM; 
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		int dateFormat = sh.getInt(Constant.SHARE_PREFS_SETTINGS_DATE_FORMAT, format_flags);
		Time then = new Time();  
        then.set(item.getDate());  
        Time now = new Time();  
        now.setToNow(); 
        if (then.year != now.year) {  
        	dateFormat |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;  
        } else if (then.yearDay != now.yearDay) {  
            // If it is from a different day than today, show only the date.  
        	dateFormat |= DateUtils.FORMAT_SHOW_DATE;  
        } else {  
            // Otherwise, if the message is from today, show the time.  
        	dateFormat |= DateUtils.FORMAT_SHOW_TIME;  
        }  
		
		mDateTv.setText(DateUtils.formatDateTime(mContext, item.getDate(), dateFormat));
		
		if(item.getType() == 0){
			mCallInIv.setVisibility(View.VISIBLE);
			mCallOutIv.setVisibility(View.GONE);
		}else{
			mCallInIv.setVisibility(View.GONE);
			mCallOutIv.setVisibility(View.VISIBLE);
		}
		
		mNumberTv.setText(item.getPhoneNumber());
		
		return convertView;
	}

}
