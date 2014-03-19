/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.data;

public class AutoMsgDataItem {
	private String msg;
	private int flag;
	private int id;
	
	public AutoMsgDataItem(String msg, int flag, int id){
		this.msg = msg;
		this.flag = flag;
		this.id = id;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}
	public void setFlag(int flag){
		this.flag = flag;
	}
	public void setMsg(String msg){
		this.msg = msg;
	}
	public int getFlag(){
		return this.flag;
	}
	public String getMsg(){
		return this.msg;
	}
}
