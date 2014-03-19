package com.kindroid.kincent.adapter;

import java.util.List;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.ContactInfo;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogueMsgWritingContactsAdapter extends BaseAdapter {
	private final static String TAG = "DialogueContactsAdapter";
	private Context ctx;
	private List<Object> list;

	private static final String LAYOUT_FILE = "sms_messengers_contacts_writing_item";
	public DialogueMsgWritingContactsAdapter(Context context, List<Object> dataList) {
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
			contentView = inflater.inflate(R.layout.sms_messengers_contacts_writing_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			view = initView();
		}
		TextView displayName = (TextView) view.findViewById(R.id.contactTextView);
		Object obj = list.get(position);
		if (obj instanceof ContactInfo) {
			ContactInfo msgInfo = (ContactInfo) list.get(position);
			displayName.setText(msgInfo.getDisplayName());
		} else if (obj instanceof SmsMmsMessage) {
			SmsMmsMessage msgInfo = (SmsMmsMessage) list.get(position);
			displayName.setText(msgInfo.getContactName());
		}
		return view;
	}

}
