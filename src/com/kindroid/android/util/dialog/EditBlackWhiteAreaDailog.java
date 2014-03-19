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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditBlackWhiteAreaDailog extends Dialog implements
		View.OnClickListener {
	private TextView mSureTv, mCancelTv, mTitleTv;
	private NativeCursor nc;

	private EditText mInputEt;

	private static final String LAYOUT_FILE = "intercept_add_list_from_region_dialog";
	/**
	 * nc 必填选项
	 * */
	public EditBlackWhiteAreaDailog(Context context, int theme) {
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
			setContentView(R.layout.intercept_add_list_from_region_dialog);
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
		mInputEt = (EditText) findViewById(R.id.input_et);

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
		} else if (str.startsWith("1")) {

			isTrue = ConvertUtils.isCellphone(str);
			if (!isTrue) {
				Toast.makeText(context, R.string.input_valide_mobile,
						Toast.LENGTH_SHORT).show();
			}

		} else if (str.startsWith("0")) {
			PhoneType pt = ConvertUtils.getPhonetype(str);
			if (pt.getPhonetype() == 0) {
				isTrue = false;
				Toast.makeText(context, R.string.input_mobile_as_rule,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			isTrue = false;
			Toast.makeText(context, R.string.input_mobile_as_rule,
					Toast.LENGTH_SHORT).show();

		}
		return isTrue;
	}

	public static boolean isRegionCode(String str) {
		// return str.startsWith("0");
		return TextUtils.isDigitsOnly(str);
	}

	public static boolean validRegionCode(String str) {
		return TextUtils.isDigitsOnly(str);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sure_tv:
			String str = mInputEt.getText().toString().trim();
			if (TextUtils.isEmpty(str)) {
				Toast.makeText(getContext(),
						R.string.add_list_from_region_empty_prompt,
						Toast.LENGTH_LONG).show();
				return;
			}
			boolean isRegionCode = isRegionCode(str);
			boolean validRegionCode = true;
			if (isRegionCode) {
				validRegionCode = validRegionCode(str);
			}
			if (isRegionCode && !validRegionCode) {
				Toast.makeText(getContext(),
						R.string.add_list_from_region_code_error,
						Toast.LENGTH_LONG).show();
				return;
			}
			if (isRegionCode) {
				String cityName = ConvertUtils.getCityNameByCode(getContext(),
						str);
				nc.setmPhoneNum(str);
				if (cityName != null) {
					nc.setmContactName(cityName);
				} else {
					Toast.makeText(getContext(),
							R.string.add_region_code_unexist, Toast.LENGTH_LONG)
							.show();
					return;
				}
			} else {
				String regionCode = ConvertUtils.getCodeByCityName(
						getContext(), str);
				if (regionCode == null) {
					Toast.makeText(getContext(),
							R.string.add_list_from_region_code_unexist,
							Toast.LENGTH_LONG).show();
					return;
				}
				nc.setmPhoneNum(regionCode);
				nc.setmContactName(str);
			}

			nc.setmSmsStatus(true);
			nc.setmRingStatus(true);
			InterceptDataBase.get(getContext()).UpdateBlackWhiteList(nc);
			getContext().sendBroadcast(new Intent(
					Constant.BROACT_INTERCEPT_BALCKWHITE_LIST));
			
			Toast.makeText(getContext(), R.string.update_suc,
					Toast.LENGTH_LONG).show();
			dismiss();
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
			mTitleTv.setText(getContext()
					.getString(
							nc.getmRequestType() == 1 ? R.string.edit_blacklist_area_list
									: R.string.edit_whitelist_area_list));
		}

	}

}
