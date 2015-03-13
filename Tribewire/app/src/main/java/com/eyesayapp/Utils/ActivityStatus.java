package com.eyesayapp.Utils;

import android.app.Activity;
import android.app.Fragment;

public class ActivityStatus {

	private static Activity activity;
	private static Fragment fragment;
	private static boolean isForeground;

	
	public static Fragment getFragment() {
		return fragment;
	}
	
	public static void setFragment(Fragment fragment) {
		ActivityStatus.fragment = fragment;
	}
	public static void setActivity(Activity activity) {
		ActivityStatus.activity = activity;
	}

	public static Activity getActivity() {
		return activity;
	}

	public static boolean isForeground() {
		return isForeground;
	}

	public static void setForeground(boolean isForeground) {
		ActivityStatus.isForeground = isForeground;
	}

	public static void appStatus(Activity activity, boolean isForeground) {
		setActivity(activity);
		setForeground(isForeground);
	}

}
