package com.kindroid.android.util.dialog;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.InterceptHistoryAdapter;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.security.util.UpdateStaticsThread;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

public class SureRestorySmsDailog extends Dialog implements
		View.OnClickListener {
	private TextView mSureTv, mCancelTv, mTitleTv, mContentTv;
	private HistoryNativeCursor hnc;
	private InterceptHistoryAdapter mAdapter;

	private static final String LAYOUT_FILE = "sure_del_dialog";

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
			setContentView(R.layout.sure_del_dialog);
		} else {
			setContentView(contentView);
		}

		findView();
	}

	private void findView() {

		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mContentTv = (TextView) findViewById(R.id.content_tv);
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
		mTitleTv.setText(getContext().getString(R.string.sms_remind));
		mContentTv.setText(getContext()
				.getString(R.string.sure_restore_the_sms));

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sure_tv:
			InterceptDataBase.get(getContext()).DelHistory(hnc);
			mAdapter.removePosition();
			String mBody = hnc.getmBody();
			InsertSmstoBox(getContext(), hnc);
			UpdateStaticsThread ust = new UpdateStaticsThread(mBody, 0,
					getContext());
			ust.start();
			dismiss();
			Intent intent = new Intent(Constant.BROACT_UPDATE_MESSAGE_DIALOG);

			getContext().sendBroadcast(intent);

			break;
		case R.id.cancel_tv:
			dismiss();
			break;
		}
	}

	/**
	 * nc 必填选项
	 * */

	public SureRestorySmsDailog(Context context, int theme,
			InterceptHistoryAdapter mAdapter) {
		super(context, theme);
		this.mAdapter = mAdapter;
	}

	public HistoryNativeCursor getHnc() {
		return hnc;
	}

	public void setHnc(HistoryNativeCursor hnc) {
		this.hnc = hnc;
	}

	public void showDialog() {
		this.show();

	}

	public static void InsertSmstoBox(Context context, HistoryNativeCursor hnc) {

		final String ADDRESS = "address";
		final String DATE = "date";
		final String READ = "read";
		final String STATUS = "status";
		final String TYPE = "type";
		final String BODY = "body";
		ContentValues values = new ContentValues();
		/* 手机号 */
		values.put(ADDRESS, hnc.getmAddress());
		/* 时间 */
		if (hnc.getmOriginDate() != null && hnc.getmOriginDate().length() > 0) {
			try {
				long time = InterceptDataBase.DF_DATE.parse(
						hnc.getmOriginDate()).getTime();
				values.put(DATE, time);
			} catch (java.text.ParseException pe) {
				pe.printStackTrace();
			}

		}
		values.put(READ, hnc.getmRead());
		values.put(STATUS, -1);
		/* 类型1为收件箱，2为发件箱 */
		values.put(TYPE, 1);
		/* 短信体内容 */
		values.put(BODY, hnc.getmBody());
		/* 插入数据库操作 */
		context.getContentResolver().insert(Uri.parse("content://sms"), values);

	}

}
