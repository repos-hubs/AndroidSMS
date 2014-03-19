package com.kindroid.kincent.adapter;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogCollectionAdapter extends BaseAdapter {
	private CharSequence[] items;
	private Context ctx;
	
	private static final String LAYOUT_FILE = "sms_messengers_dialogue_detail_operation_collect_dialog_item";
	public DialogCollectionAdapter(Context context, CharSequence[] item) {
		this.items = item;
		this.ctx = context;
	}
	
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int i) {
		return items[i];
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
			contentView = inflater.inflate(R.layout.sms_messengers_dialogue_detail_operation_collect_dialog_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null) {
			view = initView();
		}
		TextView displayName = (TextView) view.findViewById(R.id.folderName);
		String folderName = (String) items[position];
		displayName.setText(folderName);
		return view;
	}
}
