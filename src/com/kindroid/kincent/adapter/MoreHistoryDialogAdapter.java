package com.kindroid.kincent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.HistoryNativeCursor;

public class MoreHistoryDialogAdapter extends BaseAdapter {
	private int mDrawableId[];
	private int mStringId[];
	private Context context;
	private HistoryNativeCursor hnc;

	private static final String LAYOUT_FILE = "more_dialog_item";
	public MoreHistoryDialogAdapter(Context context, HistoryNativeCursor hnc,
			int stringId[], int drawableid[]) {
		this.mDrawableId = drawableid;
		this.mStringId = stringId;
		this.context = context;
		this.hnc = hnc;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mStringId.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mStringId[position];
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
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = inflater.inflate(R.layout.more_dialog_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {			
			convertView = initView();
		}
		// ImageView mIv=(ImageView)
		// convertView.findViewById(R.id.appImageView);
		TextView appTextView = (TextView) convertView
				.findViewById(R.id.appTextView);
		// mIv.setBackgroundResource(mDrableId[position]);
		if (position==0) {
			appTextView.setText(mStringId[position]);
		}else{
			String str=context.getString(mStringId[position], hnc.getmAddress());
			appTextView.setText(str);
		}

		return convertView;
	}

}
