/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:
 * Date:
 * Description:
 */

package com.kindroid.android.model;

import java.io.Serializable;

public class CategorySmsListItem implements Serializable{


	private int mId;
	private String mAddress;
	private String mBody;
	private String mDate;
	private int mCategoryId;
	
	private String insertTime;
	

	
	

	public String getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
	public int getmCategoryId() {
		return mCategoryId;
	}
	public void setmCategoryId(int mCategoryId) {
		this.mCategoryId = mCategoryId;
	}
	public int getmId() {
		return mId;
	}
	public void setmId(int mId) {
		this.mId = mId;
	}
	public String getmAddress() {
		return mAddress;
	}
	public void setmAddress(String mAddress) {
		this.mAddress = mAddress;
	}
	public String getmBody() {
		return mBody;
	}
	public void setmBody(String mBody) {
		this.mBody = mBody;
	}
	public String getmDate() {
		return mDate;
	}
	public void setmDate(String mDate) {
		this.mDate = mDate;
	}

	

}
