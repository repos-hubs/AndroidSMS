package com.kindroid.android.util.dialog;

import java.text.ParseException;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.security.util.PhoneType;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
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

public class HandlAddBlackWhiteListDailog extends Dialog implements
		View.OnClickListener {
	private TextView mSureTv, mCancelTv, mTitleTv;
	private NativeCursor nc;

	private EditText mMobileEt, mRemarkEt;

	//private GenealDailog mDialog;

	private static final String LAYOUT_FILE = "insert_edit_black_white_dialog";

	/**
	 * nc 必填选项
	 * */
	public HandlAddBlackWhiteListDailog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();
	}

	private void initContentViews() {
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
//		} else if (str.startsWith("1")) {
//
//			isTrue = ConvertUtils.isCellphone(str);
//			if (!isTrue) {
//				Toast.makeText(context, R.string.input_valide_mobile,
//						Toast.LENGTH_SHORT).show();
//			}
//
//		} else if (str.startsWith("0")) {
//			PhoneType pt = ConvertUtils.getPhonetype(str);
//			if (pt.getPhonetype() == 0) {
//				isTrue = false;
//				Toast.makeText(context, R.string.input_mobile_as_rule,
//						Toast.LENGTH_SHORT).show();
//			}
//		} else {
//			isTrue = false;
//			Toast.makeText(context, R.string.input_mobile_as_rule,
//					Toast.LENGTH_SHORT).show();
//
//		}
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
				nc = InterceptDataBase.get(getContext()).selectIsExists(nc);
				if (!nc.ismIsExists()) {
					// if(nc.getmRequestType()==2){
					// new AsyRestoreSms(nc).execute();
					// }else{

					String contactName = mRemarkEt.getText().toString().trim();

					InterceptDataBase.get(getContext()).insertBlackWhitList(
							nc.getmRequestType(), contactName,
							nc.getmPhoneNum(), true, true, 1);
					getContext().sendBroadcast(
							new Intent(
									Constant.BROACT_INTERCEPT_BALCKWHITE_LIST));
					Toast.makeText(getContext(), R.string.add_list_complete,
							Toast.LENGTH_LONG).show();
					cancel();

					// }

				} else {
					Toast.makeText(getContext(), R.string.number_already_exist,
							Toast.LENGTH_LONG).show();
					return;
				}
			}
			break;
		case R.id.cancel_tv:
			cancel();
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
		if (nc.getmRequestType() == 1) {
			mTitleTv.setText(R.string.add_black_list);
		} else {
			mTitleTv.setText(R.string.add_white_list);
		}

	}

}
