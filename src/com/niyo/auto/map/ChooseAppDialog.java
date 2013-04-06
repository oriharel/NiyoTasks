package com.niyo.auto.map;

import com.niyo.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class ChooseAppDialog extends Dialog {
	
	private Boolean mNeverAsk = false;

	public ChooseAppDialog(Context context) {
		super(context);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.choose_map_app_layout);
		
		CheckBox cb = (CheckBox)findViewById(R.id.noAskMaps);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setNeverAsk(isChecked);
				
			}
		});
	}
	
	public void setGoogleMapsListener(android.view.View.OnClickListener listener)
	{
		findViewById(R.id.gMapBtn).setOnClickListener(listener);
	}
	
	public void setSysMapsListener(android.view.View.OnClickListener listener)
	{
		findViewById(R.id.sysMapBtn).setOnClickListener(listener);
	}

	public Boolean getNeverAsk() {
		return mNeverAsk;
	}

	public void setNeverAsk(Boolean neverAsk) {
		mNeverAsk = neverAsk;
	}

}
