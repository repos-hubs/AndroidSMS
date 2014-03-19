/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.adapter;

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;

public class PrivacyMessageDetailsAdapter extends BaseAdapter {
	private final static String TAG = "PrivacyMessageDetailsAdapter";
	private Context ctx;
	private List<SmsMmsMessage> list;
	private static final String LEFT_LAYOUT_FILE = "sms_messengers_dialogue_details_left_item";
	private static final String RIGHT_LAYOUT_FILE = "sms_messengers_dialogue_details_right_item";
	public PrivacyMessageDetailsAdapter(Context context, List<SmsMmsMessage> dataList) {
		this.list = dataList;
		this.ctx = context;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	private View initLeftView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LEFT_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			contentView = inflater.inflate(R.layout.sms_messengers_dialogue_details_left_item, null);
		} 
		return contentView;
	}
	private View initRightView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(RIGHT_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			contentView = inflater.inflate(R.layout.sms_messengers_dialogue_details_right_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		
		SmsMmsMessage msgInfo = list.get(position);
		int type = msgInfo.getMessageType();
		if (type == MessengerUtils.INBOX) {
			view = initLeftView();
		} else {
			view = initRightView();
		}
		
		TextView bodyTextView = (TextView) view.findViewById(R.id.msgBodyTextView);
		TextView timestampTextView = (TextView) view.findViewById(R.id.msgTimestampTextView);
		
		bodyTextView.setText(msgInfo.getMessageBody());
		timestampTextView.setText(msgInfo.getFormattedTimestamp());
		
		return view;
	}

}

