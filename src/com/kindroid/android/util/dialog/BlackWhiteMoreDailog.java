package com.kindroid.android.util.dialog;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.MoreBlackWhiteDialogAdapter;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity;
import com.kindroid.kincent.ui.PrivacyDialogueDetailActivity;
import com.kindroid.kincent.R;
import com.kindroid.security.util.InterceptDataBase;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class BlackWhiteMoreDailog extends Dialog implements
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
		mTitleTv.setText(getContext().getString(R.string.sms_remind));
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
	public BlackWhiteMoreDailog(Context context, int theme) {
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
		MoreBlackWhiteDialogAdapter adapter = null;

		int stringId[] = new int[] { R.string.sms_dialogue_delete,
				R.string.privacy_auto_msg_list_edit_text,
				R.string.send_sms_phone_num };

		adapter = new MoreBlackWhiteDialogAdapter(getContext(), nc, stringId,
				null);
		mListView.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		dismiss();
		switch (arg2) {
		case 0:
			SureDelDailog dialog = new SureDelDailog(getContext(),
					R.style.Theme_CustomDialog);
			dialog.setNc(nc);
			dialog.showDialog();
			break;
		case 1:
			EditBlackWhiteDailog dialog_e = new EditBlackWhiteDailog(
					getContext(), R.style.Theme_CustomDialog);
			dialog_e.setNc(nc);
			dialog_e.showDialog();
			break;
		case 2:
			String telNoSms = nc.getmPhoneNum();
			
			if ((telNoSms != null) && (!"".equals(telNoSms.trim()))) {
				Intent intent=new Intent(getContext(),KindroidMessengerDialogueDetailActivity.class);
				intent.setData(Uri.parse("sms:"+telNoSms));
				getContext().startActivity(intent);
			}

			break;
		}
	}
}
