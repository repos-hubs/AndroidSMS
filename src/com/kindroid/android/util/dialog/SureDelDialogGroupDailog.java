package com.kindroid.android.util.dialog;

import java.util.ArrayList;
import java.util.List;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.DialogueMessengerListAdapter;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueActivity;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.R;


import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;

import android.widget.TextView;

public class SureDelDialogGroupDailog extends Dialog implements
		View.OnClickListener {
	private TextView mSureTv, mCancelTv, mTitleTv, mContentTv;
	private List<SmsMmsMessage> messages;

	private int type;
	private static final String LAYOUT_FILE = "sure_del_dialog";
	private DialogueMessengerListAdapter adapter;

	public DialogueMessengerListAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(DialogueMessengerListAdapter adapter) {
		this.adapter = adapter;
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
		mContentTv.setText(getContext().getString(R.string.sure_del_the_item));

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sure_tv:

			List<SmsMmsMessage> tmpList = new ArrayList<SmsMmsMessage>();
			for (int i = 0; i < messages.size(); i++) {
				SmsMmsMessage msg = messages.get(i);
				if (msg.isSelected()) {

					long threadId = msg.getThreadId();
					MessengerUtils.deleteThread(getContext(), threadId,
							SmsMmsMessage.MESSAGE_TYPE_MESSAGE);
					tmpList.add(msg);
				}
			}
			if (tmpList.size() > 0) {
				for (int i = 0; i < tmpList.size(); i++) {
					messages.remove(tmpList.get(i));
				}
			}

			adapter.notifyDataSetChanged();

			//
			dismiss();
			break;
		case R.id.cancel_tv:
			dismiss();
			break;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * nc 必填选项
	 * */

	public SureDelDialogGroupDailog(Context context, int theme) {
		super(context, theme);
	}

	public void showDialog() {
		this.show();

	}

	public List<SmsMmsMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<SmsMmsMessage> messages) {
		this.messages = messages;
	}

}
