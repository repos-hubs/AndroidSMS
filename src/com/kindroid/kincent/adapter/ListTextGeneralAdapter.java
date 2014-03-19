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

public class ListTextGeneralAdapter extends BaseAdapter {
	private int mDrawableId[];
	private int mStringId[];
	private Context context;

	private static final String LAYOUT_FILE = "more_dialog_item";

	public ListTextGeneralAdapter(Context context, int stringId[],
			int drawableid[]) {
		this.mDrawableId = drawableid;
		this.mStringId = stringId;
		this.context = context;

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

	private View initView() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

		appTextView.setText(mStringId[position]);

		return convertView;
	}

}
