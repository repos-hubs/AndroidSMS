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
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyMessagesItem;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrivacyMessageListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PrivacyMessagesItem> mItems;
	private static final String LAYOUT_FILE = "privacy_messages_list_item";
	public PrivacyMessageListAdapter(Context context){
		this.mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems = new ArrayList<PrivacyMessagesItem>();
	}
	public void sortItems(){
		
	}
	public void clearItems(){
		mItems.clear();
	}
	public List<PrivacyMessagesItem> getItemsAll(){
		return mItems;
	}
	public void addItemsAll(Collection<PrivacyMessagesItem> collection){
		this.mItems.addAll(collection);
	}
	public void addItem(PrivacyMessagesItem item){
		mItems.add(item);
	}
	public void delItem(PrivacyMessagesItem item){
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
			contentView = mInflater.inflate(R.layout.privacy_messages_list_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = initView();
		}
		TextView mNameTv = (TextView)convertView.findViewById(R.id.privacy_messages_list_item_contact_tv);
		TextView mMsgTv = (TextView)convertView.findViewById(R.id.privacy_messages_list_item_msg_tv);
		TextView mSumTv = (TextView)convertView.findViewById(R.id.privacy_messages_list_item_num_tv);
		TextView mDateTv = (TextView)convertView.findViewById(R.id.privacy_messages_list_item_date_tv);
		
		final PrivacyMessagesItem item = (PrivacyMessagesItem)getItem(position);
		if(!TextUtils.isEmpty(item.getContactName())){
			mNameTv.setText(item.getContactName());
		}else{
			mNameTv.setText(item.getPhoneNumber());
		}
		int mSum = item.getMsgNum();
		mSumTv.setText(String.format(mContext.getString(R.string.privacy_message_item_sum_format), mSum));
		
		mMsgTv.setText(item.getLastMsg());
		TextPaint textPaint = mMsgTv.getPaint();
		if(item.getRead() == 0){
			textPaint.setFakeBoldText(true);
		}else{
			textPaint.setFakeBoldText(false);
		}
		int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |  DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM; 
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(mContext);
		int dateFormat = sh.getInt(Constant.SHARE_PREFS_SETTINGS_DATE_FORMAT, format_flags);
		Time then = new Time();  
        then.set(item.getLastDate());  
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
        
		mDateTv.setText(DateUtils.formatDateTime(mContext, item.getLastDate(), dateFormat));
		
		return convertView;
	}

}
