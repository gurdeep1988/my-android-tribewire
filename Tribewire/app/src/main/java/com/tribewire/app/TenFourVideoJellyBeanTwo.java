package com.tribewire.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class TenFourVideoJellyBeanTwo  extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	public Handler handler2 = new Handler(){
		public void handleMessage(android.os.Message msg) {};
		
	};
}

