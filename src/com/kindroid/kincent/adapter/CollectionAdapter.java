/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author: jie.li
 * Date: 2011-07
 * Description:
 */

package com.kindroid.kincent.adapter;

import java.util.List;

import com.kindroid.android.model.CollectCategory;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollectionAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layInflater;
	private List<CollectCategory> mCategorys;

	private static final String LAYOUT_FILE = "sms_collection_item";
	public CollectionAdapter(Activity context, List<CollectCategory> mCategorys) {
		this.context = context;

		layInflater = LayoutInflater.from(context);
		this.mCategorys = mCategorys;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return mCategorys.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
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
			contentView = layInflater.inflate(R.layout.sms_collection_item, null);
		} 
		return contentView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = initView();
		}
		LinearLayout mCategoryLinear = (LinearLayout) convertView
				.findViewById(R.id.category_linear);
		TextView mTagTv = (TextView) convertView.findViewById(R.id.tag_tv);
		if(position==mCategorys.size()){
//			mCategoryLinear.setBackgroundResource(R.drawable.colelct_category_add_bg);
			mCategoryLinear.setSelected(true);
			mTagTv.setText("");
		}else{
//			mCategoryLinear.setBackgroundResource(R.drawable.collect_category_bg);
			mCategoryLinear.setSelected(false);
			mTagTv.setText(mCategorys.get(position).getmName());
		}
	

		return convertView;
	}

}
