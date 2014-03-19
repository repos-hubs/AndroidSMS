/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author: heli.zhao
 * Date: 2011-09
 * Description:
 */
package com.kindroid.kincent.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;


import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.InterceptDataBase;

/**
 * @author heli.zhao
 *
 */
public class InterceptKeywordAdapter extends CursorAdapter {
	private LayoutInflater mLayoutFlater;
	private static final String LAYOUT_FILE = "key_word_list_item";
	private Context mContext;

	/**
	 * @param context
	 * @param c
	 */
	public InterceptKeywordAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
		mLayoutFlater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	private View initView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			contentView = mLayoutFlater.inflate(R.layout.key_word_list_item, null);
		} 
		return contentView;
	}
	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return initView();
	}

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		TextView tv = (TextView)view.findViewById(R.id.key_word);
		final String keyword = cursor.getString(cursor.getColumnIndex(InterceptDataBase.KEYWORDZH));
		tv.setText(keyword);
		
	}


}
