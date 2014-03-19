/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.ui.KindroidThemeDetailActivity;
import com.kindroid.kincent.R;

import java.util.List;

public class ThemeImageAdapter  extends BaseAdapter{
	private List<Drawable> mImageList;
	private Context mContext;
	private LayoutInflater mInflater;
	
	private static final String LAYOUT_FILE = "theme_image_item";
	public ThemeImageAdapter(Context context, List<Drawable> mList){
		this.mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mImageList = mList;
		
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImageList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mImageList.get(position);
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
			contentView = mInflater.inflate(R.layout.theme_image_item, null);
		} 
		return contentView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = initView();
		}
		ImageView themeIv = (ImageView)convertView.findViewById(R.id.theme_image_item_iv);
		if(mImageList.get(position) != null){
			themeIv.setImageDrawable(mImageList.get(position));
		}
//		int mScreenWidth = KindroidThemeDetailActivity.mDisplayMetrics.widthPixels;
//		int mScreenHeight = KindroidThemeDetailActivity.mDisplayMetrics.heightPixels;
//		if(mScreenWidth > 0 && mScreenWidth < 350){
//			float scaleWidth = mScreenWidth * 0.6f / 300;
//			themeIv.setAdjustViewBounds(true);
//			themeIv.setMaxWidth(Double.valueOf(mScreenWidth * 0.6).intValue());
//			themeIv.setMaxHeight(Double.valueOf(mScreenHeight * 0.6).intValue());
//		}
		
		return convertView;
	}

}
