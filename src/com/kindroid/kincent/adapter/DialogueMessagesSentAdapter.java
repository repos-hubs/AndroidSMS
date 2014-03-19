package com.kindroid.kincent.adapter;

import java.util.List;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogueMessagesSentAdapter extends BaseAdapter {
	private final static String TAG = "DialogueMessagesSentAdapter";
	private Context ctx;
	private List<SmsMmsMessage> list;

	private static final String LAYOUT_FILE = "sms_messengers_sent_item";
	public DialogueMessagesSentAdapter(Context context, List<SmsMmsMessage> dataList) {
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
	private View initView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			contentView = inflater.inflate(R.layout.sms_messengers_sent_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		SmsMmsMessage msgInfo = list.get(position);
		view = initView();
		TextView toContactTextView = (TextView) view.findViewById(R.id.toContactTextView);
		TextView bodyTextView = (TextView) view.findViewById(R.id.msgBodyTextView);
		TextView timestampTextView = (TextView) view.findViewById(R.id.msgTimestampTextView);
		
		toContactTextView.setText(String.format(ctx.getString(R.string.sms_contact_sent_to_contact), msgInfo.getContactName()));
		bodyTextView.setText(msgInfo.getMessageBody());
		timestampTextView.setText(msgInfo.getFormattedTimestamp());
		
		return view;
	}

}
