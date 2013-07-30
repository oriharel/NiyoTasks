package com.niyo.radar;

import com.niyo.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class NiyoMarker extends ImageView {

	public NiyoMarker(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMarker();
	}
	
	public NiyoMarker(Context context) {
		super(context);
		initMarker();
	}
	
	private void initMarker(){
		setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.friend_placard));
		setPadding(7, 9, 7, 9);
		LayoutParams params = new LayoutParams(63, 77);
//		setLayoutParams(params);
		setScaleType(ScaleType.FIT_START);
	}
}
