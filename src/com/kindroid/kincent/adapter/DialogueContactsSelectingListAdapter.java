package com.kindroid.kincent.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.ContactInfo;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.R;

public class DialogueContactsSelectingListAdapter extends BaseAdapter {

	private List<Object> list;
	private Context ctx;
	private boolean isSelected = false;
	private boolean showingLetter;
	private int groupType;
	
	private static final String LAYOUT_FILE = "sms_messengers_contacts_selecting_contact_item";
	public DialogueContactsSelectingListAdapter(Context context, List<Object> dataList, boolean showLetter, int type) {
		this.list = dataList;
		this.ctx = context;
		this.showingLetter = showLetter;
		this.groupType = type;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
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
			contentView = inflater.inflate(R.layout.sms_messengers_contacts_selecting_contact_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			view = initView();
		}
		TextView displayName = (TextView) view.findViewById(R.id.contactDisplayNameTextView);
		TextView address = (TextView) view.findViewById(R.id.contactNumTextView);
		TextView letterTextView = (TextView) view.findViewById(R.id.letterTextView);
		final CheckBox chkBoxImageView = (CheckBox) view.findViewById(R.id.contactSelectImageView);
		
		if (showingLetter) {
			letterTextView.setVisibility(View.VISIBLE);
		} else {
			letterTextView.setVisibility(View.GONE);
		}
		
		final Object obj = list.get(position);
		/*
		chkBoxImageView.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					isSelected = changeChkStatus(chkBoxImageView.isChecked());					
					chkBoxImageView.setChecked(isSelected);
					if (obj instanceof ContactInfo) {
						ContactInfo contactInfo = (ContactInfo) list.get(position);
						contactInfo.setChkBoxSelected(isSelected);
					} else {
						SmsMmsMessage msgInfo = (SmsMmsMessage) list.get(position);
						msgInfo.setChkBoxSelected(isSelected);						
					}
				}
			}
		);
		*/
		chkBoxImageView.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (obj instanceof ContactInfo) {
					ContactInfo contactInfo = (ContactInfo) list.get(position);
					contactInfo.setChkBoxSelected(isChecked);
				} else {
					SmsMmsMessage msgInfo = (SmsMmsMessage) list.get(position);
					msgInfo.setChkBoxSelected(isChecked);						
				}
			}
			
		});
		
		if (obj instanceof ContactInfo) {
			ContactInfo contactInfo = (ContactInfo) list.get(position);
			if (groupType == 0) {
				displayName.setText(contactInfo.getDisplayName());
			} else {
				displayName.setText(contactInfo.getGroupName());
			}
			address.setText(contactInfo.getAddress());
			/*if (contactInfo.isChkBoxSelected()) {
				chkBoxImageView.setImageResource(R.drawable.icon_gouxuan_on);
			} else {
				chkBoxImageView.setImageResource(R.drawable.icon_gouxuan_off);
			}*/
			letterTextView.setText(contactInfo.getFirstCNLetter());
			chkBoxImageView.setChecked(contactInfo.isChkBoxSelected());
		} else if (obj instanceof SmsMmsMessage) {
			SmsMmsMessage msgInfo = (SmsMmsMessage) list.get(position);
			displayName.setText(msgInfo.getContactName());
			address.setText(msgInfo.getFromAddress());
			/*if (msgInfo.isChkBoxSelected()) {
				chkBoxImageView.setImageResource(R.drawable.icon_gouxuan_on);
			} else {
				chkBoxImageView.setImageResource(R.drawable.icon_gouxuan_off);
			}*/
			chkBoxImageView.setChecked(msgInfo.isChkBoxSelected());
		}
		return view;
	}
	
	private boolean changeChkStatus(boolean isSelected) {
		return !isSelected;
	}

}
