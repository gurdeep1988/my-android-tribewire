package com.tribewire.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import com.eyesayapp.Utils.CommonFunctions;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.blue)));
		setContentView(R.layout.activity_splash);

	}

	@Override
	protected void onResume() {

		Handler handler = new Handler();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				boolean isLogin = CommonFunctions.getPref(Splash.this,
						CommonFunctions.IS_LOGIN, false);

				if (isLogin) {
					Intent intent = new Intent(Splash.this, MenuActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(Splash.this, Login.class);
					// Intent intent = new Intent(Splash.this,
					// MenuActivity.class);
					startActivity(intent);
				}

				finish();

			}
		}, 3000);

		super.onResume();
	}

}
