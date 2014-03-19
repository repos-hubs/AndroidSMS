package com.kindroid.kincent.util;

import android.widget.TextView;

import com.kindroid.kincent.util.AsyLoadAddress.AddressCallback;

public class AddressCallBack implements AddressCallback {
	TextView tv;

	public AddressCallBack(TextView tv) {
		this.tv = tv;
	}

	@Override
	public void addressLoad(String address) {
		// TODO Auto-generated method stub
		tv.setText(address);
	}
}
