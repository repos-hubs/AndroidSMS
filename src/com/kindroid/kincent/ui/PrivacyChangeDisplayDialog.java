/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;

public class PrivacyChangeDisplayDialog extends Dialog implements
		View.OnClickListener {
	public static final int PRIVACY_ICONS_SUM = 2;
	public static final String PRIVACY_ICON_NAME_PREF = "privacy_icon";

	private Context mContext;

	private Button mButtonOk;
	private Button mButtonCancel;
	private View mChangeDisplayIconLinear;
	private EditText mChangeDisplayTextEt;
	private ImageView mChangeDisplayIconIv;
	private int iconIndex = 0;

	private static final String LAYOUT_FILE = "privacy_change_display_dialog";
	
	private static final int MAX_TEXT_INPUT_LENGTH = 4;

	public PrivacyChangeDisplayDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		mContext = context;
		initContentViews();
		bindListeners();
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
			setContentView(R.layout.privacy_change_display_dialog);
		} else {
			setContentView(contentView);
		}

		mChangeDisplayIconLinear = findViewById(R.id.privacy_change_display_icon_linear);
		mChangeDisplayTextEt = (EditText) findViewById(R.id.privacy_display_text_et);
		mButtonOk = (Button) findViewById(R.id.button_ok);
		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mChangeDisplayIconIv = (ImageView) findViewById(R.id.privacy_display_icon_iv);

		SharedPreferences sh = KindroidMessengerApplication
				.getSharedPrefs(mContext);
		String displayName = sh.getString(
				Constant.SHARE_PREFS_PRIVACY_DISPLAY_NAME,
				mContext.getString(R.string.privacy_default_display_name));
		mChangeDisplayTextEt.setText(displayName);
		mChangeDisplayTextEt.addTextChangedListener(mTextWatcher);	
		
		iconIndex = sh.getInt(Constant.SHARE_PREFS_PRIVACY_DISPLAY_ICON, 0);
		try {
			// int iconId = mContext.getResources().getIdentifier(
			// PRIVACY_ICON_NAME_PREF + iconIndex, "drawable",
			// mContext.getPackageName());
			String themeId = KindroidMessengerApplication.mThemeRegistry
					.getCurrentTheme().getId();
			int iconId = KindroidMessengerApplication.mThemeRegistry
					.getIconId(themeId + "_" + PRIVACY_ICON_NAME_PREF + iconIndex);
			if (iconId > 0) {
				// mChangeDisplayIconIv.setImageResource(iconId);
				mChangeDisplayIconIv
						.setImageDrawable(KindroidMessengerApplication.mThemeRegistry
								.getIconDrawable(iconId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void bindListeners() {
		mChangeDisplayIconLinear.setOnClickListener(this);
		mButtonOk.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.privacy_change_display_icon_linear:
			if (iconIndex == PRIVACY_ICONS_SUM) {
				iconIndex = 0;
			} else {
				iconIndex = iconIndex + 1;
			}
			try {
				String themeId = KindroidMessengerApplication.mThemeRegistry
						.getCurrentTheme().getId();
				// int iconId = mContext.getResources().getIdentifier(
				// PRIVACY_ICON_NAME_PREF + iconIndex, "drawable",
				// mContext.getPackageName());
				int iconId = KindroidMessengerApplication.mThemeRegistry
						.getIconId(themeId + "_" + PRIVACY_ICON_NAME_PREF
								+ iconIndex);
				if (iconId > 0) {
					// mChangeDisplayIconIv.setImageResource(iconId);
					mChangeDisplayIconIv
							.setImageDrawable(KindroidMessengerApplication.mThemeRegistry
									.getIconDrawable(iconId));
				} else {
					// iconIndex = 0;
					iconId = mContext.getResources().getIdentifier(
							"default" + PRIVACY_ICON_NAME_PREF + iconIndex,
							"drawable", mContext.getPackageName());
					if (iconId > 0) {
						mChangeDisplayIconIv.setImageResource(iconId);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case R.id.button_ok:
			try {
				String newText = mChangeDisplayTextEt.getText().toString()
						.trim();
				if (newText.equals("")) {
					Toast.makeText(mContext,
							R.string.privacy_change_text_empty_prompt,
							Toast.LENGTH_LONG).show();
					return;
				}
				SharedPreferences sh = KindroidMessengerApplication
						.getSharedPrefs(mContext);
				Editor editor = sh.edit();
				editor.putString(Constant.SHARE_PREFS_PRIVACY_DISPLAY_NAME,
						newText);
				editor.putInt(Constant.SHARE_PREFS_PRIVACY_DISPLAY_ICON,
						iconIndex);
				editor.commit();
				KindroidMessengerTabMain.setNewDisplay();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dismiss();
			break;
		case R.id.button_cancel:
			dismiss();
			break;
		}
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		Toast mToast = null;

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}

		public void afterTextChanged(Editable s) {
			int nSelStart = 0;
			int nSelEnd = 0;
			boolean nOverMaxLength = false;

			nSelStart = mChangeDisplayTextEt.getSelectionStart();
			nSelEnd = mChangeDisplayTextEt.getSelectionEnd();

			nOverMaxLength = (SafeUtils.getCharacterNum(s.toString()) > MAX_TEXT_INPUT_LENGTH) ? true
					: false;
			if (nOverMaxLength) {
				if (null == mToast) {
					mToast = Toast.makeText(mContext,
							String.format(mContext.getString(R.string.privacy_change_dispaly_text_overlength), MAX_TEXT_INPUT_LENGTH),
							Toast.LENGTH_SHORT);
				}
				mToast.show();

				s.delete(nSelStart - 1, nSelEnd);
				mChangeDisplayTextEt.setTextKeepState(s);// 请读者注意这一行，保持光标原先的位置，而
															// mEditText.setText(s)会让光标跑到最前面，
															// 就算是再加mEditText.setSelection(nSelStart)
															// 也不起作用
			}
		}
	};

}
