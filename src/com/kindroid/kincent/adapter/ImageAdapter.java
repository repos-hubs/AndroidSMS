package com.kindroid.kincent.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kindroid.google.android.mms.ContentType;
import com.kindroid.kincent.util.Constant;

public class ImageAdapter extends BaseAdapter {

	private Activity mContext;
	private Integer[] mImageIds;
	public ImageAdapter(Activity context) {
		mContext = context;
		mImageIds = Constant.mImageIds;
	}

	@Override
	public int getCount() {
		return mImageIds.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView myImageView = new ImageView(mContext);
		myImageView.setImageResource(mImageIds[position]);
		myImageView.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (position == 0) {//拍照
						Intent intent = new Intent();
						intent.setAction("android.media.action.STILL_IMAGE_CAMERA"); 
						mContext.startActivityForResult(intent, 0);
					} else if (position == 1) {//图库
						Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
						innerIntent.setType("image/*"); //查看类型 String IMAGE_UNSPECIFIED = "image/*";
						Intent wrapperIntent = Intent.createChooser(innerIntent, null);
						mContext.startActivityForResult(wrapperIntent, 0);
					} else if (position == 2) {//表情
						
					} else if (position == 3) {//对话
						
					} else if (position == 4) {//录音
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType(ContentType.AUDIO_AMR); //String AUDIO_AMR = "audio/amr";
						intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.SoundRecorder");
						mContext.startActivityForResult(intent, 0);
					}
				}
			}
		);
		return myImageView;
	}

}
