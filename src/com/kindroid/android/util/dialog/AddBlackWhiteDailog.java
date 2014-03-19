package com.kindroid.android.util.dialog;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.AddBlackWhiteListDialogAdapter;
import com.kindroid.kincent.ui.InterceptAddListFromContactsActivity;
import com.kindroid.kincent.util.LoadListAdapterThread;
import com.kindroid.kincent.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AddBlackWhiteDailog extends Dialog implements
		View.OnClickListener, OnItemClickListener {
	private TextView mTitleTv;
	private ListView mListView;
	private NativeCursor nc;

	private static final String LAYOUT_FILE = "more_balck_whitelist_dialog";
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
			setContentView(R.layout.more_balck_whitelist_dialog);
		} else {
			setContentView(contentView);
		}
		
		findView();
	}

	private void findView() {
		mTitleTv = (TextView) findViewById(R.id.title_tv);

		findViewById(R.id.button_close).setOnClickListener(this);
		TextPaint tp = mTitleTv.getPaint();
		tp.setFakeBoldText(true);

		mListView = (ListView) findViewById(R.id.listproc);
		mListView.setOnItemClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_close:
			dismiss();
			break;

		}
	}

	/**
	 * nc 必填选项
	 * */
	public AddBlackWhiteDailog(Context context, int theme) {
		super(context, theme);
	}

	public NativeCursor getNc() {
		return nc;
	}

	public void setNc(NativeCursor c) {
		this.nc = c;
	}

	public void showDialog() {
		this.show();
		int stringId[] = null;
		if (nc.getmRequestType() == 1) {
			mTitleTv.setText(getContext().getString(R.string.add_black_list));
			stringId = new int[] { R.string.manually_add,
					R.string.add_from_mobile_contact, R.string.add_from_log,
					R.string.add_from_sms, R.string.add_black_area };
		} else {
			mTitleTv.setText(getContext().getString(R.string.add_white_list));
			stringId = new int[] { R.string.manually_add,
					R.string.add_from_mobile_contact, R.string.add_from_log,
					R.string.add_from_sms, R.string.add_white_area };
		}
		AddBlackWhiteListDialogAdapter adapter = null;
		adapter = new AddBlackWhiteListDialogAdapter(getContext(), stringId);
		mListView.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		cancel();
		switch (arg2) {
		case 0:
			HandlAddBlackWhiteListDailog dialog_handle = new HandlAddBlackWhiteListDailog(
					getContext(), R.style.Theme_CustomDialog);
			dialog_handle.setNc(nc);
			dialog_handle.showDialog();
			break;
		case 1:
			Intent intent = new Intent(getContext(),
					InterceptAddListFromContactsActivity.class);
			intent.putExtra(
					InterceptAddListFromContactsActivity.BLACK_OR_WHITE, nc.getmRequestType());
			intent.putExtra(InterceptAddListFromContactsActivity.SOURCE_TYPE,
					LoadListAdapterThread.CONTACTS_SOURCE_TYPE);
			getContext().startActivity(intent);
			break;
		/*
		 * case 2: intent = new Intent(this, AddListFromContactsActivity.class);
		 * intent.putExtra(BLACK_OR_WHITE, mType); intent.putExtra(SOURCE_TYPE,
		 * LoadListAdapterThread.SIM_CONTACTS_SOURCE_TYPE);
		 * startActivity(intent); break;
		 */
		case 2:
			intent = new Intent(getContext(),
					InterceptAddListFromContactsActivity.class);
			intent.putExtra(
					InterceptAddListFromContactsActivity.BLACK_OR_WHITE, nc.getmRequestType());
			intent.putExtra(InterceptAddListFromContactsActivity.SOURCE_TYPE,
					LoadListAdapterThread.CALL_LOG_SOURCE_TYPE);
			getContext().startActivity(intent);
			break;
		case 3:
			intent = new Intent(getContext(),
					InterceptAddListFromContactsActivity.class);
			intent.putExtra(
					InterceptAddListFromContactsActivity.BLACK_OR_WHITE, nc.getmRequestType());
			intent.putExtra(InterceptAddListFromContactsActivity.SOURCE_TYPE,
					LoadListAdapterThread.SMS_SOURCE_TYPE);
			getContext().startActivity(intent);
			break;
		case 4:
			
			
			AddBlackWhiteAreaDailog dialog_area = new AddBlackWhiteAreaDailog(
					getContext(), R.style.Theme_CustomDialog);
			dialog_area.setNc(nc);
			dialog_area.showDialog();
			break;

		}
	}
}
