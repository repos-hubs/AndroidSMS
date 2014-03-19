package com.kindroid.android.model;

import java.util.ArrayList;
import java.util.List;

public class BlackWhiteListModel {
	
	private String name;
	private boolean isBlack=true;
	private boolean isShowCurList=true;
	private List<NativeCursor>mLists=new ArrayList<NativeCursor>();
	
	public BlackWhiteListModel(boolean isBlack,String name){
		this.isBlack=isBlack;
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isShowCurList() {
		return isShowCurList;
	}
	public void setShowCurList(boolean isShowCurList) {
		this.isShowCurList = isShowCurList;
	}
	public List<NativeCursor> getmLists() {
		return mLists;
	}
	public boolean isBlack() {
		return isBlack;
	}
	public void setmLists(List<NativeCursor> mLists) {
		this.mLists = mLists;
	}
	
}
