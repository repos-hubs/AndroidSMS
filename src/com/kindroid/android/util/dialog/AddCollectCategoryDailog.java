package com.kindroid.android.util.dialog;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.CollectionDataBase;
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

public class AddCollectCategoryDailog extends Dialog implements
		View.OnClickListener {
	private TextView mSureTv, mCancelTv, mTitleTv;
	private NativeCursor nc;

	private EditText mInputEt;

	private static final String LAYOUT_FILE = "add_collect_category_dialog";
	/**
	 * nc 必填选项
	 * */
	public AddCollectCategoryDailog(Context context, int theme) {
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
			setContentView(R.layout.add_collect_category_dialog);
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
			int num = CollectionDataBase.get(getContext()).insertCategory(str);
			if (num == 1) {
			Toast.makeText(getContext(),
						R.string.the_category_has_exsits,
						Toast.LENGTH_LONG).show();

			} else if (num == 2) {
				Toast.makeText(getContext(),
						R.string.add_category_error,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getContext(),
						R.string.add_category_suc,
						Toast.LENGTH_LONG).show();
				getContext().sendBroadcast(new Intent(Constant.BROACT_UPDATE_COLLECT_CATEGORY));
				dismiss();
				
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
		mTitleTv.setText(getContext().getString(R.string.add_collect_category));

	}

}
