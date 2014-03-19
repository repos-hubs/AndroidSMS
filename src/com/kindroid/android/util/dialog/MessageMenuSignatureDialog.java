package com.kindroid.android.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.SmsUtils;
import com.kindroid.kincent.R;

public class MessageMenuSignatureDialog extends Dialog implements View.OnClickListener {
	private static final String TAG = "MessageMenuSignatureDialog";
	private TextView mTitleTv;
	private Button confirmButton;
	private Button cancelButton;
	private ImageView radioImageView;
	private EditText mInputEt;

	private boolean radioStatus = false;
	private static final String LAYOUT_FILE = "sms_messengers_menu_dialog";
	/**
	 * nc 必填选项
	 * */
	public MessageMenuSignatureDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			setContentView(R.layout.sms_messengers_menu_dialog);
		} else {
			setContentView(contentView);
		}
		
		findViews();
	}

	private void findViews() {
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		TextPaint tp = mTitleTv.getPaint();
		tp.setFakeBoldText(true);
		mInputEt = (EditText) findViewById(R.id.input_et);
		confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(this);
		radioImageView = (ImageView) findViewById(R.id.radioImageView);
		radioImageView.setOnClickListener(this);
		
		radioStatus = SmsUtils.getSendAppTag(getContext());
		if (radioStatus) {
			radioImageView.setImageResource(R.drawable.sms_messenger_dialogue_radio_yellow_selected);
		} else {
			radioImageView.setImageResource(R.drawable.sms_messenger_dialogue_radio_unselect);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.confirmButton:
			String contentStr = mInputEt.getText().toString();
			if (TextUtils.isEmpty(contentStr)) {
				Toast.makeText(getContext(), getContext().getResources().getString(R.string.menu_setting_content_not_null), Toast.LENGTH_SHORT).show();
			} else {
				SmsUtils.setSignatureContent(contentStr, getContext());
			}
		case R.id.cancelButton:
			dismiss();
			break;
		case R.id.radioImageView:
			radioStatus = !radioStatus;
			if (radioStatus) {
				radioImageView.setImageResource(R.drawable.sms_messenger_dialogue_radio_yellow_selected);
			} else {
				radioImageView.setImageResource(R.drawable.sms_messenger_dialogue_radio_unselect);
			}
			SmsUtils.setSendAppTag(radioStatus, getContext());
			break;
		}
	}

	public void showDialog() {
		this.show();
	}

}
