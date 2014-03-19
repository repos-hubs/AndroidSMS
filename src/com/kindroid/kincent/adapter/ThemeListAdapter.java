/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.theme.ThemeMeta;
import com.kindroid.theme.ThemeRegistry;

import java.util.List;

public class ThemeListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	private static final String LAYOUT_FILE = "theme_list_item";

	List<ThemeMeta> mItems;

	public ThemeListAdapter(Context context, List<ThemeMeta> items) {
		this.mContext = context;
		mItems = items;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
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
			contentView = mLayoutInflater.inflate(R.layout.theme_list_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = initView();
		}
		
		ImageView iconIv = (ImageView)convertView.findViewById(R.id.theme_list_item_iv);
		TextView themeTv = (TextView)convertView.findViewById(R.id.theme_list_item_tv);
		ImageView usedIv = (ImageView)convertView.findViewById(R.id.theme_used_iv);
		
		ThemeMeta themeMeta = mItems.get(position);
		if(themeMeta == ThemeRegistry.getInstance(mContext).getCurrentTheme()){
			usedIv.setVisibility(View.VISIBLE);
		}else{
			usedIv.setVisibility(View.GONE);
		}
		if(themeMeta.getThemeIcon() != null){
			iconIv.setImageDrawable(themeMeta.getThemeIcon());
		}else{
			
		}
		if(themeMeta.getName() != null){
			themeTv.setText(themeMeta.getName());
		}else{
			themeTv.setText(themeMeta.getPackageName() + ":" + themeMeta.getId());
		}

		return convertView;
	}
}
