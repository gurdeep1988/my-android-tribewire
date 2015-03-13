package com.eyesayapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.eyesayapp.Utils.MessageSearch;
import com.tribewire.app.MessageCollectorListener;
import com.tribewire.app.MessageSearchResult;
import com.tribewire.app.TribMessageCollectorapi;

public class TribMessageCollectorService extends Service {
	
	private static final String TAG = TribMessageCollectorService.class.getSimpleName();
	
	private Timer timer;
	
	private String garry = "";
	
	private String conv_id  = "";
	private static MessageSearch messageSearch;
	
	
	
	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Timer task doing work +"+conv_id);			
			try {
				MessageSearch searcher  = new MessageSearch(TribMessageCollectorService.this);
				MessageSearchResult newSearchResult = searcher.messageSearch(conv_id);
				//Log.d(TAG, "Retrieved " + newSearchResult.getTweets().size() + " tweets");
				
				synchronized (latestSearchResultLock) {
					latestSearchResult = newSearchResult;
				}
			
				synchronized (listeners) {
					for (MessageCollectorListener listener : listeners) {
						try {
							if (listener!=null) {
								listener.handleMessageUpdated();
							}
						} catch (RemoteException e) {
							Log.w(TAG, "Failed to notify listener " + listener, e);
						}
					}
				}
			} catch (Throwable t) { /* you should always ultimately catch 
									   all exceptions in timer tasks, or 
									   they will be sunk */
				Log.e(TAG, "Failed to retrieve the twitter results", t);
			}
		}
	};
	
	private final Object latestSearchResultLock = new Object();
	
	private MessageSearchResult latestSearchResult = new MessageSearchResult();

	private List<MessageCollectorListener> listeners = new ArrayList<MessageCollectorListener>();
	
	private TribMessageCollectorapi.Stub apiEndpoint = new TribMessageCollectorapi.Stub() {
		
		@Override
		public MessageSearchResult getLatestSearchResult() throws RemoteException {
			synchronized (latestSearchResultLock) {
				return latestSearchResult;
			}
		}
		
		@Override
		public void addListenerTrib(MessageCollectorListener listener)
				throws RemoteException {
			
			synchronized (listeners) {
				listeners.add(listener);
			}
		}

		@Override
		public void removeListenerTrib(MessageCollectorListener listener)
				throws RemoteException {
			
			synchronized (listeners) {
				listeners.remove(listener);
			}
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		if (TribMessageCollectorService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "Bound by intent " + intent);
			return apiEndpoint;
		} else {
			return null;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Service creating");
		
		
		timer = new Timer("TweetCollectorTimer");
		timer.schedule(updateTask, 1000L, 10 * 1000L);
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 Log.e("", "----------In the on Start Command--------");
		   String conv_id =    intent.getExtras().getString("conv_id");
		   this.conv_id  = conv_id;
		   Log.e("", "--------The Conversation id is ----------"+conv_id);
		 
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Service destroying");
		
		timer.cancel();
		timer = null;
	}
}
