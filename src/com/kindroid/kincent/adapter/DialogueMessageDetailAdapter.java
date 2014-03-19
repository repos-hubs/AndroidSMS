package com.kindroid.kincent.adapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;

public class DialogueMessageDetailAdapter extends BaseAdapter {
	private final static String TAG = "DialogueMessageDetailAdapter";
	private Context ctx;
	private List<SmsMmsMessage> list;
	private static final String LEFT_LAYOUT_FILE = "sms_messengers_dialogue_details_left_item";
	private static final String RIGHT_LAYOUT_FILE = "sms_messengers_dialogue_details_right_item";
	public DialogueMessageDetailAdapter(Context context, List<SmsMmsMessage> dataList) {
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
		final SmsMmsMessage msgInfo = list.get(position);
		int type = msgInfo.getMessageType();
		if (type == MessengerUtils.INBOX) {
			view = initLeftView();
		} else {
			view = initRightView();
		}
		
		TextView bodyTextView = (TextView) view.findViewById(R.id.msgBodyTextView);
		final TextView timestampTextView = (TextView) view.findViewById(R.id.msgTimestampTextView);
		
		bodyTextView.setText(msgInfo.getMessageBody());
		
		//设置短信状态
		int messageType = msgInfo.getMessageType();
		if (messageType == SmsMmsMessage.FAILED) {
			timestampTextView.setTextColor(ctx.getResources().getColor(R.color.red));
			timestampTextView.setText(ctx.getResources().getString(R.string.sms_dialogue_send_failure));
		} else if (messageType == SmsMmsMessage.QUEUED) {
			timestampTextView.setTextColor(ctx.getResources().getColor(R.color.dark_green));
			timestampTextView.setText(ctx.getResources().getString(R.string.sms_dialogue_sending));
		} else {
			timestampTextView.setTextColor(ctx.getResources().getColor(R.color.white));
			timestampTextView.setText(msgInfo.getFormattedTimestamp());
		}
		
		return view;
	}
	
	

}
