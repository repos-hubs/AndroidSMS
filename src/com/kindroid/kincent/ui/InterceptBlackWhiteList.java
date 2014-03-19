/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:zili.chen
 * Date:2011.09
 * Description:
 */
package com.kindroid.kincent.ui;

import java.util.ArrayList;
import java.util.List;

import com.kindroid.android.model.BlackWhiteListModel;
import com.kindroid.android.model.NativeCursor;
import com.kindroid.android.util.dialog.AddBlackWhiteDailog;
import com.kindroid.android.util.dialog.EditBlackWhiteAreaDailog;
import com.kindroid.android.util.dialog.EditBlackWhiteDailog;
import com.kindroid.android.util.dialog.BlackWhiteMoreDailog;
import com.kindroid.android.util.dialog.SureDelDailog;
import com.kindroid.android.view.AmazingAdapter;
import com.kindroid.android.view.AmazingListView;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.AddBlackWhiteListDialogAdapter;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.InterceptDataBase;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InterceptBlackWhiteList extends Activity implements
		View.OnClickListener, OnItemClickListener {
	private AmazingListView mBlackWListView;
	private List<BlackWhiteListModel> mBlackWhiteListModels = new ArrayList<BlackWhiteListModel>();
	private SectionComposerAdapter mAdapter;

	private BroadcastReceiver mReceiver;
	private TextView mAddBlackListTv, mAddWhiteListTv;
	
	private static final String LAYOUT_FILE = "intercept_black_white_list_activity";
	private static final String SPIN_HEADER_LAYOUT_FILE = "item_composer_header";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentViews();
	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.intercept_black_white_list_activity);
		}else{
			setContentView(contentView);
		}
		
		findView();
		bindListenerToView();
		initReciver();
		IntentFilter mIt = new IntentFilter(
				Constant.BROACT_INTERCEPT_BALCKWHITE_LIST);

		registerReceiver(mReceiver, mIt);
	}

	private void findView() {
		mBlackWListView = (AmazingListView) findViewById(R.id.balck_white_listview);
		mAddBlackListTv = (TextView) findViewById(R.id.add_blacklist_tv);
		mAddWhiteListTv = (TextView) findViewById(R.id.add_whitelist_tv);
		TextPaint tp=mAddBlackListTv.getPaint();
		tp.setFakeBoldText(true);
		tp=mAddWhiteListTv.getPaint();
		tp.setFakeBoldText(true);
		mAddBlackListTv.setText(R.string.add_black_list);
		mAddWhiteListTv.setText(R.string.add_white_list);
		

	}

	private void bindListenerToView() {
		mBlackWListView.setOnItemClickListener(this);
		mAddBlackListTv.setOnClickListener(this);
		mAddWhiteListTv.setOnClickListener(this);

	}

	void initReciver() {
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				new AsyLoadRequest().execute();

			}
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new AsyLoadRequest().execute();
	}

	class AsyLoadRequest extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			loadBlackAndWhiteList();
			return null;
		}
		private View initView(ViewGroup root, boolean attached){
			View contentView = null;
			try {
				contentView = KindroidMessengerApplication.mThemeRegistry.inflateAttach(SPIN_HEADER_LAYOUT_FILE, root, attached);
			} catch (Exception e) {
				contentView = null;
			}
			if (contentView == null) {
				contentView = LayoutInflater.from(
						InterceptBlackWhiteList.this).inflate(R.layout.item_composer_header, mBlackWListView, false);
			} 
			return contentView;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

//			mBlackWListView.setPinnedHeaderView(LayoutInflater.from(
//					InterceptBlackWhiteList.this).inflate(
//					R.layout.item_composer_header, mBlackWListView, false));
			mBlackWListView.setPinnedHeaderView(initView(mBlackWListView, false));
			TextView tagTv = (TextView) mBlackWListView.mHeaderView.findViewById(R.id.tag_tv);
			tagTv.setText(R.string.intercept_black_list);
			if(mAdapter==null){
				mAdapter = new SectionComposerAdapter();
				mBlackWListView.setAdapter(mAdapter);
			}else{
				mAdapter.notifyDataSetChanged();
			}
			
			
		}

		void loadBlackAndWhiteList() {

			BlackWhiteListModel blackListModel = new BlackWhiteListModel(true,
					getString(R.string.intercept_black_list));
			BlackWhiteListModel whiteListModel = new BlackWhiteListModel(false,
					getString(R.string.intercept_white_list));
			Cursor c = InterceptDataBase.get(InterceptBlackWhiteList.this)
					.selectAllList(1);
			if (c != null) {
				convertCursorToNativeCursor(c, blackListModel);
				c.close();
			}
			c = InterceptDataBase.get(InterceptBlackWhiteList.this)
					.selectAllList(2);
			if (c != null) {
				convertCursorToNativeCursor(c, whiteListModel);
				c.close();
			}
			if(mBlackWhiteListModels.size()==2){
				mBlackWhiteListModels.get(0).setmLists(blackListModel.getmLists());
				mBlackWhiteListModels.get(1).setmLists(whiteListModel.getmLists());
			}else{
				mBlackWhiteListModels.clear();
				mBlackWhiteListModels.add(blackListModel);
				mBlackWhiteListModels.add(whiteListModel);
			}
			

		}

	}

	private void convertCursorToNativeCursor(Cursor c,
			BlackWhiteListModel listmodel) {
		List<NativeCursor> cursors = new ArrayList<NativeCursor>();
		int mType = listmodel.isBlack() ? 1 : 2;

		try {
			while (c.moveToNext()) {
				NativeCursor nc = new NativeCursor();
				nc.setmId(c.getInt(c.getColumnIndex(InterceptDataBase.ID)));
				nc.setmPhoneNum(c.getString(c
						.getColumnIndex(InterceptDataBase.PHONENUM)));

				nc.setmContactName(c.getString(c
						.getColumnIndex(InterceptDataBase.CONTACTNAME)));
				nc.setmSmsStatus(c.getInt(c
						.getColumnIndex(InterceptDataBase.SMSSTATUS)) > 0);
				nc.setmRingStatus(c.getInt(c
						.getColumnIndex(InterceptDataBase.RINGSTATUS)) > 0);
				nc.setmPhoneType(c.getInt(c
						.getColumnIndex(InterceptDataBase.PHONETYPE)));
				
				cursors.add(nc);
				nc.setmRequestType(mType);

			}
			listmodel.setmLists(cursors);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class SectionComposerAdapter extends AmazingAdapter implements
			View.OnClickListener {
		private static final String AMAZE_LAYOUT_FILE = "black_white_list_item";
		@Override
		public int getCount() {
			int res = 0;
			for (int i = 0; i < mBlackWhiteListModels.size(); i++) {
				if (mBlackWhiteListModels.get(i).isShowCurList()) {
					res += mBlackWhiteListModels.get(i).getmLists().size();
				}
			}
			res += mBlackWhiteListModels.size();
			return res;
		}

		@Override
		public NativeCursor getItem(int position) {
			int c = 0;
			for (int i = 0; i < mBlackWhiteListModels.size(); i++) {
				c++;
				int sumNum = 0;
				if (mBlackWhiteListModels.get(i).isShowCurList()) {
					sumNum = mBlackWhiteListModels.get(i).getmLists().size();
				}
				if (position >= c && position <= c + sumNum) {
					if (position == c + sumNum) {
						return null;
					}
					return mBlackWhiteListModels.get(i).getmLists()
							.get(position - c);
				}
				c += sumNum;
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		protected void onNextPageRequested(int page) {
		}

		@Override
		protected void bindSectionHeader(View view, int position,
				boolean displaySectionHeader) {
		}
		private View initView(){
			View contentView = null;
			try {
				contentView = KindroidMessengerApplication.mThemeRegistry.inflate(AMAZE_LAYOUT_FILE);
			} catch (Exception e) {
				contentView = null;
			}
			if (contentView == null) {
				contentView = getLayoutInflater().inflate(R.layout.black_white_list_item, null);
			} 
			return contentView;
		}
		@Override
		public View getAmazingView(int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
//				convertView = getLayoutInflater().inflate(
//						R.layout.black_white_list_item, null);
				convertView = initView();
				holder.mHeaderRl = (RelativeLayout) convertView
						.findViewById(R.id.header);
				holder.mContentLinear = (RelativeLayout) convertView
						.findViewById(R.id.content_linear);
				holder.mBotomLinear = (LinearLayout) convertView
						.findViewById(R.id.bottom_linear);
				holder.mTagTv = (TextView) convertView
						.findViewById(R.id.tag_tv);
				holder.mTagIv = (ImageView) convertView
						.findViewById(R.id.tag_iv);
				holder.mDelTv = (TextView) convertView
						.findViewById(R.id.del_tv);

				holder.mEditTv = (TextView) convertView
						.findViewById(R.id.edit_tv);
				holder.mMoreTv = (TextView) convertView
						.findViewById(R.id.more_tv);
				holder.mContactNameTv = (TextView) convertView
						.findViewById(R.id.contact_display_name_tv);
				holder.mPhoneNumTv = (TextView) convertView
						.findViewById(R.id.phone_num_tv);

				TextPaint tp = holder.mDelTv.getPaint();
				tp.setFakeBoldText(true);
				tp = holder.mEditTv.getPaint();
				tp.setFakeBoldText(true);
				tp = holder.mMoreTv.getPaint();
				tp.setFakeBoldText(true);
				holder.mDelTv.setText(getString(R.string.sms_dialogue_delete));
				holder.mEditTv
						.setText(getString(R.string.privacy_auto_msg_list_edit_text));
				holder.mMoreTv.setText(getString(R.string.sms_dialogue_more));

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			NativeCursor model = getItem(position);
			if (model == null) {
				holder.mHeaderRl.setVisibility(View.VISIBLE);
				holder.mContentLinear.setVisibility(View.GONE);

				holder.mBotomLinear.setVisibility(View.GONE);

				final int section = getSectionForPosition(position);

				holder.mTagTv.setText(mBlackWhiteListModels.get(section)
						.getName());
				boolean isTrue = mBlackWhiteListModels.get(section)
						.isShowCurList();
				holder.mTagIv.setSelected(isTrue);
//				holder.mTagIv
//						.setBackgroundResource(isTrue ? R.drawable.intercept_show_all_icon
//								: R.drawable.intercept_disshow_all_icon);
				holder.mHeaderRl.setOnClickListener(new HeaderClick(section));
			} else {
				holder.mHeaderRl.setVisibility(View.GONE);
				holder.mContentLinear.setVisibility(View.VISIBLE);

				if (model.ismIsSelected()) {
					holder.mBotomLinear.setVisibility(View.VISIBLE);
					holder.mDelTv.setOnClickListener(new LinearLister(model));
					holder.mEditTv.setOnClickListener(new LinearLister(model));
					holder.mMoreTv.setOnClickListener(new LinearLister(model));
				} else {
					holder.mBotomLinear.setVisibility(View.GONE);
				}

				holder.mContactNameTv.setText(model.getmContactName() + "");
				holder.mPhoneNumTv.setText(model.getmPhoneNum() + "");

			}
			return convertView;
		}

		class ViewHolder {
			// public TextView
			public RelativeLayout mHeaderRl, mContentLinear;
			public LinearLayout mBotomLinear;
			public TextView mTagTv, mDelTv, mEditTv, mMoreTv, mContactNameTv,
					mPhoneNumTv;
			public ImageView mTagIv;

		}

		class HeaderClick implements View.OnClickListener {
			private int section;

			public HeaderClick(int section) {
				this.section = section;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BlackWhiteListModel bwl = mBlackWhiteListModels.get(section);
				bwl.setShowCurList(!bwl.isShowCurList());
				notifyDataSetChanged();
			}

		}

		class LinearLister implements View.OnClickListener {
			private NativeCursor nc;

			public LinearLister(NativeCursor nc) {
				this.nc = nc;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.edit_tv:
					if(nc.getmPhoneType()==2){
						EditBlackWhiteAreaDailog dialog_area = new EditBlackWhiteAreaDailog(
								InterceptBlackWhiteList.this.getParent(),
								R.style.Theme_CustomDialog);
						dialog_area.setNc(nc);
						dialog_area.showDialog();
					}else{
						EditBlackWhiteDailog dialog_e = new EditBlackWhiteDailog(
								InterceptBlackWhiteList.this.getParent(),
								R.style.Theme_CustomDialog);
						dialog_e.setNc(nc);
						dialog_e.showDialog();
					}
					break;
				case R.id.del_tv:
					SureDelDailog dialog = new SureDelDailog(
							InterceptBlackWhiteList.this.getParent(),
							R.style.Theme_CustomDialog);
					dialog.setNc(nc);
					dialog.show();
					

					break;
				case R.id.more_tv:
					BlackWhiteMoreDailog dialog_mo = new BlackWhiteMoreDailog(
							InterceptBlackWhiteList.this.getParent(),
							R.style.Theme_CustomDialog);
					dialog_mo.setNc(nc);
					dialog_mo.showDialog();
					
					break;
				default:
					break;
				}

			}
		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {

			TextView tagTv = (TextView) header.findViewById(R.id.tag_tv);
			ImageView tagIv = (ImageView) header.findViewById(R.id.tag_iv);
			tagTv.setText(mBlackWhiteListModels.get(
					getSectionForPosition(position)).getName());
			boolean isTrue = mBlackWhiteListModels.get(
					getSectionForPosition(position)).isShowCurList();
			
			tagIv.setSelected(isTrue);
//			tagIv.setBackgroundResource(isTrue ? R.drawable.intercept_show_all_icon
//					: R.drawable.intercept_disshow_all_icon);
			header.setTag(position);

			header.setOnClickListener(SectionComposerAdapter.this);

			// TextView lSectionHeader = (TextView) header;
			// lSectionHeader
			// .setText(getSections()[getSectionForPosition(position)]);
			// lSectionHeader.setBackgroundColor(alpha << 24 | (0xbbffbb));
			// lSectionHeader.setTextColor(alpha << 24 | (0x000000));
		}

		@Override
		public int getPositionForSection(int section) {
			if (section < 0)
				section = 0;
			if (section >= mBlackWhiteListModels.size())
				section = mBlackWhiteListModels.size() - 1;
			int c = 0;
			for (int i = 0; i < mBlackWhiteListModels.size(); i++) {
				if (section == i) {
					// System.out.println("section:"+section+";"+c);
					return c;
				}
				int sumNum = 0;
				if (mBlackWhiteListModels.get(i).isShowCurList()) {
					sumNum = mBlackWhiteListModels.get(i).getmLists().size();
				}
				c += sumNum + 1;
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			int c = 0;
			for (int i = 0; i < mBlackWhiteListModels.size(); i++) {

				int sumNum = 0;
				if (mBlackWhiteListModels.get(i).isShowCurList()) {
					sumNum = mBlackWhiteListModels.get(i).getmLists().size();
				}
				if (position >= c && position <= c + sumNum) {
					return i;
				}
				c += sumNum + 1;
			}
			return -1;
		}

		@Override
		public String[] getSections() {
			return null;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int section = getSectionForPosition((Integer) v.getTag());

			BlackWhiteListModel bwl = mBlackWhiteListModels.get(section);
			bwl.setShowCurList(!bwl.isShowCurList());
			notifyDataSetChanged();
			mBlackWListView.setSelection(getPositionForSection(section));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_blacklist_tv:
			
			AddBlackWhiteDailog dialog=new AddBlackWhiteDailog(getParent(), R.style.Theme_CustomDialog);
			NativeCursor nc=new NativeCursor();
			nc.setmRequestType(1);
			
			dialog.setNc(nc);
			dialog.showDialog();

			break;
		case R.id.add_whitelist_tv:
			AddBlackWhiteDailog dialog_w=new AddBlackWhiteDailog(getParent(), R.style.Theme_CustomDialog);
			NativeCursor n=new NativeCursor();
			n.setmRequestType(2);
			
			dialog_w.setNc(n);
			dialog_w.showDialog();

			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		NativeCursor c = mAdapter.getItem(arg2);
		if (c != null) {
			initIsSelected(arg2, c);
			c.setmIsSelected(!c.ismIsSelected());
			mAdapter.notifyDataSetChanged();
		}

	}

	void initIsSelected(int position, NativeCursor c) {

		for (int i = 0; i < mBlackWhiteListModels.size(); i++) {
			for (int j = 0; j < mBlackWhiteListModels.get(i).getmLists().size(); j++) {
				if (mBlackWhiteListModels.get(i).getmLists().get(j)
						.ismIsSelected()) {
					if (mBlackWhiteListModels.get(i).getmLists().get(j)
							.getmId() != c.getmId()) {
						mBlackWhiteListModels.get(i).getmLists().get(j)
								.setmIsSelected(false);
					}
					return;
				}
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

}