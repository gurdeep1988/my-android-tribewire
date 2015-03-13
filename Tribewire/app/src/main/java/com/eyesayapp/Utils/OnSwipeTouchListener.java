package com.eyesayapp.Utils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {

	public OnSwipeTouchListener(){
		
	}
	
	private final GestureDetector gestureDetector = new GestureDetector(
			new GestureListener());

	public boolean onTouch(final View view, final MotionEvent motionEvent) {
		return gestureDetector.onTouchEvent(motionEvent);
	}

	public final class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;
		
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				Log.e("", "======================");
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD
							&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							onSwipeRight();
						} else {
							onSwipeLeft();
						}
					} else {
						Log.e("", "Touch Listener if");
					}
				} else {
					if (Math.abs(diffY) > SWIPE_THRESHOLD
							&& Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							onSwipeBottom();
						} else {
							onSwipeTop();
						}
					} else {
						Log.e("", "Touch Listener else");
					}
				}

			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return result;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.e("", "Single Tap Confirmed");
			onClick();
			return super.onSingleTapConfirmed(e);
		}
	}

	public void onSwipeRight() {
	}

	public void onSwipeLeft() {
	}

	public void onSwipeTop() {
	}

	public void onSwipeBottom() {
	}

	public void onClick() {

	}

}
