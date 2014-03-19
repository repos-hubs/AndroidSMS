package com.kindroid.android.util.dialog;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.security.util.PhoneType;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditBlackWhiteDailog extends Dialog implements
		View.OnClickListener {
	private TextView mSureTv, mCancelTv, mTitleTv;
	private NativeCursor nc;

	private EditText mMobileEt, mRemarkEt;
	
	private static final String LAYOUT_FILE = "insert_edit_black_white_dialog";
	/**
	 * nc 必填选项
	 * */
	public EditBlackWhiteDailog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();
	}
	private void initContentViews(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			setContentView(R.layout.insert_edit_black_white_dialog);
		} else {
			setContentView(contentView);
		}
		
		findView();
	}

	private void findView() {

		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mSureTv = (TextView) findViewById(R.id.sure_tv);
		mCancelTv = (TextView) findViewById(R.id.cancel_tv);
		mSureTv.setOnClickListener(this);
		mCancelTv.setOnClickListener(this);
		TextPaint tp = mSureTv.getPaint();
		tp.setFakeBoldText(true);
		tp = mCancelTv.getPaint();
		tp.setFakeBoldText(true);
		tp = mTitleTv.getPaint();
		tp.setFakeBoldText(true);
		mSureTv.setText(getContext().getString(R.string.sms_contact_confirm));
		mCancelTv.setText(getContext().getString(R.string.sms_contact_cancel));
		mMobileEt = (EditText) findViewById(R.id.mobile_et);
		mRemarkEt = (EditText) findViewById(R.id.remark_et);

	}

	public boolean checkMessage(Context context, String str) {
		boolean isTrue = true;
		if (str.equals("")) {
			Toast.makeText(context, R.string.phone_number_not_empty,
					Toast.LENGTH_SHORT).show();
			isTrue = false;
		} else if (!PhoneNumberUtils.isGlobalPhoneNumber(str)) {
			Toast.makeText(context, R.string.input_valid_number,
					Toast.LENGTH_SHORT).show();
			isTrue = false;
		}else if(str.length()<5||str.length()>16){
			Toast.makeText(context, R.string.input_valid_number,
					Toast.LENGTH_SHORT).show();
			isTrue = false;
		}
		return isTrue;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sure_tv:
			String str = mMobileEt.getText().toString().trim();

			boolean isTrue = checkMessage(getContext(), str);

			if (isTrue) {
				nc.setmPhoneNum(str);
				String contactName = mRemarkEt.getText().toString().trim();
				nc.setmContactName(contactName);

				InterceptDataBase.get(getContext()).UpdateBlackWhiteList(nc);
				getContext().sendBroadcast(
						new Intent(Constant.BROACT_INTERCEPT_BALCKWHITE_LIST));
				cancel();
			}
			break;
		case R.id.cancel_tv:
			dismiss();
			break;
		}
	}

	public NativeCursor getNc() {
		return nc;
	}

	public void setNc(NativeCursor c) {
		this.nc = c;
	}

	public void showDialog() {
		this.show();
		if (nc != null) {
			mTitleTv.setText(getContext().getString(
					nc.getmRequestType() == 1 ? R.string.edit_black_list
							: R.string.edit_white_list));

			mMobileEt.setText(nc.getmPhoneNum() == null ? "" : nc
					.getmPhoneNum());
			mRemarkEt.setText(nc.getmContactName() == null ? "" : nc
					.getmContactName());

		}

	}

}
