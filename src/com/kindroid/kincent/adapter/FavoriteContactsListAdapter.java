package com.kindroid.kincent.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity;
import com.kindroid.kincent.ui.KindroidMessengerWriteMessageActivity;
import com.kindroid.kincent.ui.PrivacyDialogueDetailActivity;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;

public class FavoriteContactsListAdapter extends BaseAdapter {

	private List<SmsMmsMessage> list;
	private Context ctx;
	
	private static final String LAYOUT_FILE = "sms_messengers_fovarite_contacts_list_item";
	public FavoriteContactsListAdapter(Context context, List<SmsMmsMessage> dataList) {
		this.list = dataList;
		this.ctx = context;
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
			contentView = inflater.inflate(R.layout.sms_messengers_fovarite_contacts_list_item, null);
		} 
		return contentView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {			
//			view = inflater.inflate(R.layout.sms_messengers_fovarite_contacts_list_item, null);
			view = initView();
		}
		TextView displayName = (TextView) view.findViewById(R.id.displayNameTextView);
		ImageView writeMessageImageView = (ImageView) view.findViewById(R.id.writeMessageImageView);
		
		final SmsMmsMessage msgInfo = list.get(position);
		
		writeMessageImageView.setOnClickListener(
			new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					PrivacyContactDataItem pcdi = SafeUtils.getPrivacyContact(msgInfo.getFromAddress());
					if(pcdi == null){
						Intent intent = new Intent(ctx, KindroidMessengerDialogueDetailActivity.class);
						intent.putExtra("messageInfo", msgInfo);
						ctx.startActivity(intent);
					}else{
						Intent intent = new Intent(ctx, PrivacyDialogueDetailActivity.class);
						intent.putExtra(PrivacyDialogueDetailActivity.ACTION_TYPE, PrivacyDialogueDetailActivity.ACTION_MMS_SEND);
						intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_TYPE, PrivacyDialogueDetailActivity.ACTION_FROM_PRIVACY);
						intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NUMBER, pcdi.getPhoneNumber());
						if(TextUtils.isEmpty(pcdi.getContactName())){
							intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, pcdi.getContactName());
						}else{
							intent.putExtra(PrivacyDialogueDetailActivity.ACTION_SOURCE_NAME, pcdi.getPhoneNumber());
						}
						ctx.startActivity(intent);
					}
				}
			}
		);
		
		displayName.setText(msgInfo.getContactName());
		
		return view;
	}

}
