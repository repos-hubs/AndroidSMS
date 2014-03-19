package com.kindroid.kincent.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity;
import com.kindroid.kincent.util.AddressCallBack;
import com.kindroid.kincent.util.AsyLoadAddress;
import com.kindroid.kincent.util.AsyLoadDisplayName;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.util.AsyLoadAddress.AddressCallback;
import com.kindroid.kincent.util.AsyLoadDisplayName.DisplayNameCallback;
import com.kindroid.kincent.R;



public class DialogueMessengerListAdapter extends BaseAdapter {
	private List<SmsMmsMessage> list;
	private Context ctx;
	private boolean isSelected = false;

	private Map<String, String> maps = new HashMap<String, String>();

	private static final String LAYOUT_FILE = "sms_messengers_dialogue_list_item";

	public DialogueMessengerListAdapter(Context context,
			List<SmsMmsMessage> dataList) {

		this.list = dataList;
		this.ctx = context;
	}

	@Override
	public int getCount() {
		if(list==null){
			return 0;
		}else{
			return list.size();
		}
		
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private View initView() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = inflater.inflate(
					R.layout.sms_messengers_dialogue_list_item, null);
		}
		return contentView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = initView();
		}

		final ImageView userProfile = (ImageView) view
				.findViewById(R.id.contactIconImageView);
		final RadioButton radioImageView = (RadioButton) view
				.findViewById(R.id.radioButtonImageView);
		LinearLayout itemLayout = (LinearLayout) view
				.findViewById(R.id.itemContentLayout);
		TextView userNameTextView = (TextView) view
				.findViewById(R.id.contactDisplayNameTextView);
		TextView msgNumTextView = (TextView) view
				.findViewById(R.id.contactNumTextView);
		final TextView locationTextView = (TextView) view
				.findViewById(R.id.locationTextView);
		TextView msgDetailTextView = (TextView) view
				.findViewById(R.id.messageContentTextView);
		TextView msgDateTextView = (TextView) view
				.findViewById(R.id.messageDateTextView);
		TextView draftTextView = (TextView) view
				.findViewById(R.id.draftTextView);
		RelativeLayout readLogoLayout = (RelativeLayout) view.findViewById(R.id.newArrivalLogoLayout);

		final SmsMmsMessage msgInfo = list.get(position);
		// 显示会话列表单选按钮
		if (msgInfo.isShown()) {
			radioImageView.setVisibility(View.VISIBLE);
			radioImageView.setChecked(msgInfo.isSelected());

		} else {
			radioImageView.setVisibility(View.GONE);
		}
		// 设置草稿信息标识
		if (msgInfo.getMessageType() == SmsMmsMessage.DRAFT) {
			draftTextView.setVisibility(View.VISIBLE);
			draftTextView.setText(ctx.getResources().getString(
					R.string.sms_dialog_draft_msg));
		} else {
			draftTextView.setVisibility(View.GONE);
		}
		

		// 处理Radio单击事件
		radioImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				msgInfo.setSelected(!msgInfo.isSelected());
		
				
				radioImageView.setChecked(msgInfo.isSelected());
			

			}

		});

		// 设置会话来源是否粗体
		TextPaint textPaint = userNameTextView.getPaint();
		if (msgInfo.getReadType() == SmsMmsMessage.UNREAD) {
			textPaint.setFakeBoldText(true);
			textPaint = msgNumTextView.getPaint();
			textPaint.setFakeBoldText(true);
			textPaint = locationTextView.getPaint();
			textPaint.setFakeBoldText(true);
			readLogoLayout.setVisibility(View.VISIBLE);
		} else {
			textPaint.setFakeBoldText(false);
			textPaint = msgNumTextView.getPaint();
			textPaint.setFakeBoldText(false);
			textPaint = locationTextView.getPaint();
			textPaint.setFakeBoldText(false);
			readLogoLayout.setVisibility(View.GONE);
		}

		// 设置来往信息数量
		TextPaint tp = msgNumTextView.getPaint();
		if (msgInfo.getTotalCount() > 1) {
			msgNumTextView.setVisibility(View.VISIBLE);
			if (msgInfo.getReadType() == SmsMmsMessage.UNREAD) {
				tp.setFakeBoldText(true);
			} else {
				tp.setFakeBoldText(false);
			}
			msgNumTextView.setText("(" + msgInfo.getTotalCount() + ")");
		} else {
			msgNumTextView.setVisibility(View.GONE);
		}

		msgDetailTextView.setText(msgInfo.getMessageBody());
		// Handler加载头像
		Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.arg1) {
				case 0:
					Bitmap mPhoto = (Bitmap) msg.obj;
					if (mPhoto != null) {
						userProfile.setImageBitmap(mPhoto);
					} else {
						userProfile.setImageDrawable(ctx.getResources()
								.getDrawable(R.drawable.sms_contact_profile));
					}
					break;
				default:
					break;
				}

			}
		};

		// get contact photo when contact id is not null
		if (null != msgInfo.getContactId()
				&& !"".equals(msgInfo.getContactId())) {

			new LoadContactPhoto(mHandler, msgInfo).start();
		} else {
			userProfile.setImageDrawable(ctx.getResources().getDrawable(
					R.drawable.sms_contact_profile));
		}
		msgDateTextView.setText(msgInfo.getFormattedTimestamp());

		String str = AsyLoadDisplayName.GetAddressBody().AsyDisplayName(
				msgInfo.getContactId(), msgInfo.getFromAddress(),
				new DisplayNameCallBack(userNameTextView), ctx);
		if (str != null) {
			userNameTextView.setText(str);
		}else{
			userNameTextView.setText("");
		}

		str = AsyLoadAddress.GetAddressBody().AsyAddress(
				msgInfo.getFromAddress(),
				new AddressCallBack(locationTextView), ctx);
		if (str != null) {
			locationTextView.setText(str);
		}else{
			locationTextView.setText("");
		}

		return view;
	}

	class LoadContactPhoto extends Thread {
		Handler myHandler;
		SmsMmsMessage message;

		public LoadContactPhoto(Handler mHandler, SmsMmsMessage msgInfo) {
			this.myHandler = mHandler;
			this.message = msgInfo;
		}

		@Override
		public void run() {
			Message msg = myHandler.obtainMessage();
			Bitmap mPhoto = MessengerUtils.getPersonPhoto(ctx,
					message.getContactLookupUri());
			msg.obj = mPhoto;
			msg.arg1 = 0;
			myHandler.sendMessage(msg);
		}
	}

	private void changeRadioStatus(boolean selected) {
		isSelected = !selected;
	}

	class DisplayNameCallBack implements DisplayNameCallback {

		TextView tv;

		public DisplayNameCallBack(TextView tv) {
			this.tv = tv;
		}

		@Override
		public void displayNameLoad(String name) {
			// TODO Auto-generated method stub
			tv.setText(name);

		}

	}

	/*
	 * class ViewClick implements View.OnClickListener { SmsMmsMessage msgInfo;
	 * public ViewClick(SmsMmsMessage msgInfos) { this.msgInfo = msgInfos; }
	 * 
	 * @Override public void onClick(View v) { Intent intent = new Intent(ctx,
	 * KindroidMessengerDialogueDetailActivity.class);
	 * intent.putExtra("messageInfo", msgInfo); ctx.startActivity(intent); } }
	 */

}
