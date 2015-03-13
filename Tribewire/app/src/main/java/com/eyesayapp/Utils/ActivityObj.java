package com.eyesayapp.Utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.tribewire.app.EyeSayDefaultMessage;

public class ActivityObj {

	private static List<Activity> stackActivity;
	private static EyeSayDefaultMessage defaultAudioVideo;

	public static List<Activity> getActivityInstance() {

		if (stackActivity == null) {
			stackActivity = new ArrayList<Activity>();
		}

		return stackActivity;
	}
	
	public static void setDefaultAudioVideo(
			EyeSayDefaultMessage defaultAudioVideo) {
		ActivityObj.defaultAudioVideo = defaultAudioVideo;
	}
	
	public static EyeSayDefaultMessage getDefaultAudioVideo() {
		return defaultAudioVideo;
	}

}
