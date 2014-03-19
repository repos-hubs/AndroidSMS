package com.kindroid.kincent.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kindroid.android.util.dialog.HistoryMoreDailog;
import com.kindroid.android.util.dialog.SureDelHistoryDailog;
import com.kindroid.android.util.dialog.SureRestorySmsDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.AddressCallBack;
import com.kindroid.kincent.util.AsyLoadAddress;
import com.kindroid.kincent.R;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.security.util.PhoneType;

import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.LinearLayout;
import android.widget.TextView;

public class InterceptHistoryAdapter extends BaseAdapter {
	private LayoutInflater mLayoutFlater;

	private Context mContext;
	private int mPosition = -1;
	private List<HistoryNativeCursor> mCursors;
	
	private Map<String, String>maps=new HashMap<String, String>();
	
	private static final String LAYOUT_FILE = "intercept_history_item";

	public InterceptHistoryAdapter(Context context,
			List<HistoryNativeCursor> cursors) {
		super();
		mContext = context;
		this.mCursors = cursors;
		mLayoutFlater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public void removePosition() {
		if (mCursors.size() > mPosition && mPosition != -1) {
			mCursors.remove(mPosition);
			mPosition = -1;
			notifyDataSetChanged();
		}
	}

	class LinearLister implements View.OnClickListener {
		private HistoryNativeCursor nc;

		public LinearLister(HistoryNativeCursor nc) {
			this.nc = nc;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.restore_tv:
				SureRestorySmsDailog res_dialog=new SureRestorySmsDailog(((Activity)(mContext)).getParent(), R.style.Theme_CustomDialog, InterceptHistoryAdapter.this);
				res_dialog.setHnc(nc);
				res_dialog.showDialog();
				break;
			case R.id.del_tv:

				SureDelHistoryDailog del_dialog=new SureDelHistoryDailog(((Activity)(mContext)).getParent(), R.style.Theme_CustomDialog, InterceptHistoryAdapter.this);
				del_dialog.setHnc(nc);
				del_dialog.showDialog();
			
				break;
			case R.id.more_tv:
				HistoryMoreDailog h_dialog=new HistoryMoreDailog(((Activity)(mContext)).getParent(), R.style.Theme_CustomDialog,InterceptHistoryAdapter.this);
				h_dialog.setHnc(nc);
				h_dialog.showDialog();


				break;
			default:
				break;
			}

		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCursors.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCursors.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	private View initView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			contentView = mLayoutFlater.inflate(R.layout.intercept_history_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = initView();

			holder.mContentLinear = (RelativeLayout) convertView
					.findViewById(R.id.content_linear);
			holder.mBotomLinear = (LinearLayout) convertView
					.findViewById(R.id.bottom_linear);

			holder.mDelTv = (TextView) convertView.findViewById(R.id.del_tv);
			holder.mRestoreTv = (TextView) convertView
					.findViewById(R.id.restore_tv);
			holder.mMoreTv = (TextView) convertView.findViewById(R.id.more_tv);
			holder.mContactNameTv = (TextView) convertView
					.findViewById(R.id.contact_display_name_tv);
			holder.mSmsContentTv = (TextView) convertView
					.findViewById(R.id.sms_content_tv);
			holder.mContactTimeTv = (TextView) convertView
					.findViewById(R.id.contact_time_tv);
			holder.mContactAddressTv = (TextView) convertView
					.findViewById(R.id.contact_address_tv);

			TextPaint tp = holder.mDelTv.getPaint();
			tp.setFakeBoldText(true);
			tp = holder.mRestoreTv.getPaint();
			tp.setFakeBoldText(true);
			tp = holder.mMoreTv.getPaint();
			tp.setFakeBoldText(true);
			holder.mDelTv.setText(mContext
					.getString(R.string.sms_dialogue_delete));
			holder.mRestoreTv.setText(mContext.getString(R.string.restore));
			holder.mMoreTv.setText(mContext
					.getString(R.string.sms_dialogue_more));

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position >= mCursors.size()) {
			return convertView;
		}

		final HistoryNativeCursor hnc = mCursors.get(position);
		if (mPosition == position) {
			holder.mBotomLinear.setVisibility(View.VISIBLE);
			holder.mSmsContentTv.setSingleLine(false);
			holder.mDelTv.setOnClickListener(new LinearLister(hnc));
			holder.mRestoreTv.setOnClickListener(new LinearLister(hnc));
			holder.mMoreTv.setOnClickListener(new LinearLister(hnc));
			

		} else {
			holder.mSmsContentTv.setSingleLine(true);
			holder.mBotomLinear.setVisibility(View.GONE);
		}
		holder.mContactTimeTv.setText(hnc.getmDate() == null ? "" : hnc
				.getmDate());
		holder.mSmsContentTv.setText(hnc.getmBody() == null ? "" : hnc
				.getmBody());
		
		holder.mContactNameTv.setText(hnc.getmAddress() == null ? "" : hnc.getmAddress());
		
		String str = AsyLoadAddress.GetAddressBody().AsyAddress(
				hnc.getmAddress(),
				new AddressCallBack(holder.mContactAddressTv), mContext);
		if(str!=null){
			holder.mContactAddressTv.setText(str);
		}else{
			holder.mContactAddressTv.setText("");
		}
		
		return convertView;
	}

	class ViewHolder {
		// public TextView
		public RelativeLayout mContentLinear;
		public LinearLayout mBotomLinear;
		public TextView mDelTv, mRestoreTv, mMoreTv, mContactNameTv,
				mContactTimeTv, mContactAddressTv, mSmsContentTv;
		public ImageView mTagIv;

	}

}
