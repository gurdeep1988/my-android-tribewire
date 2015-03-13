package com.app.eyesay.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.widget.ProgressBar;

import com.tribewire.app.R;

public class EyesayonWayDialog extends Dialog {


	private ProgressBar progressBar;

	public EyesayonWayDialog(Context context) {
		super(context);
		setContentView(R.layout.eyesay_onway);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
		progressBar = (ProgressBar) findViewById(R.id.eyesay_onway);
		//progressBar.setBackgroundColor(Color.BLUE);
		progressBar.setProgress(10);

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 10) {
				progressBar.setProgress(80);
			} else if (msg.what == 9) {
				progressBar.setProgress(30);
			} else if (msg.what == 11) {
				progressBar.setProgress(100);
			}
			else if (msg.what == 90) {
				progressBar.setProgress(90);
			}
			else if (msg.what == 95) {
				progressBar.setProgress(95);
			}
		};
	};

	public Handler getHandlerObj() {

		return handler;
	}


}
