package com.tribewire.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.SyncAddressBook;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.ContactSync;
import com.eyesayapp.asyn.IContactResult;
import com.eyesayapp.model.CompanyMessages;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService implements IContactResult{

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static String convIDNOtify  = null;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCM Demo";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				//sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				//sendNotification("Deleted messages on server: "
					//	+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// This loop represents the service doing some work.
				for (int i = 0; i < 5; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				Log.i(TAG, "Received: " + extras.toString());
				// Post notification of received message.
				try {
				 String isFyiResponse   =   extras.getString("is_fyi");
                  if (isFyiResponse!=null) {
    				  if (ActivityStatus.getActivity() != null) {
    					  try {
    							if (MenuActivity.fragment != null) {
    								if (MenuActivity.fragment instanceof TenFourConversations) {

    									if (ActivityStatus.getActivity() != null) {
    										TenFourConversations tenFourConversations = (TenFourConversations) MenuActivity.fragment;
    										tenFourConversations.getCompanyFyiMessages();
    										sendNotification("New Fyi",null,false);
    									}

    								}
    								else if (MenuActivity.fragment instanceof CompanyFYI) {

    									if (ActivityStatus.getActivity() != null) {
    										CompanyFYI companyFYI = (CompanyFYI) MenuActivity.fragment;
    										companyFYI.handler2.sendEmptyMessage(2);
    										
    									}

    								}    								
    								else {
    			    						sendNotification("New Fyi", null,false);
    			    				  }
    								
    							}
    							else {
		    						sendNotification("New Fyi",null,false);
		    				  }
    						} catch (Exception e) {
    							e.printStackTrace();
    						}	
    					  
    				  }
    				  
    				  return;
                  }
				 
				 
				
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					String message = extras.getString("message");
					if (!message.equalsIgnoreCase("login/logout activity")) {
						try {
							if (ActivityStatus.getActivity() != null) {
								if (ActivityStatus.getActivity() instanceof ConversationMessages) {
									
									ConversationMessages conversationMessages   = (ConversationMessages)ActivityStatus.getActivity();
									Message message2 = new Message();									
									message2.getData().putString("message", message);									
									message2.what =1;
									conversationMessages.handler2.sendMessage(message2);
								}								
								
								else {
								    
									String title  =  TribeWireUtil.getNotificationMessage(message);
									sendNotification(title,null,false);
								}
								
								showEmergencyAlert(ActivityStatus.getActivity(),message);
								
							}
							else {
								// 1 means screen is in pause state
								if (ConversationMessages.CONV_SCREEN_STATUS ==1) {
									TribeWireUtil.updateMessage(GcmIntentService.this, message, false);
								}
								
								String title  =  TribeWireUtil.getNotificationMessage(message);
								String convId  =  TribeWireUtil.getNotificationConvId(message);
							    boolean isEmergency =	TribeWireUtil.isEmergency(message);
								TribeWireUtil.insertNewConversation(message, GcmIntentService.this);
								Log.e("", "================== The Conversation Id when is in back ground  ===="+convId);
								sendNotification(title,convId , isEmergency);		
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
												

					}
					else {
						
						syncLoginLogoutStatus();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void showEmergencyAlert (Context c ,String message){
		try {
			try {
				boolean isEmergency =	TribeWireUtil.isEmergency(message);
				if (isEmergency) {
					String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
					Message message2 = new Message();									
					message2.getData().putString("message", message);									
					message2.what =1;
					
					if (c instanceof MenuActivity) {
						MenuActivity menuActivity  = (MenuActivity)c;		
						
						menuActivity.handler2.sendMessage(message2);
						
						
					}
					else if (c instanceof ConversationMessages) {
						ConversationMessages menuActivity  = (ConversationMessages)c;
						menuActivity.handler3.sendMessage(message2);
					
					}
					else if (c instanceof EyesaySendText) {
						EyesaySendText menuActivity  = (EyesaySendText)c;
						menuActivity.handler5.sendMessage(message2);
					
					}
					
					else if (c instanceof ComposeFyi) {
						ComposeFyi menuActivity  = (ComposeFyi)c;
						menuActivity.handler2.sendMessage(message2);
					
					}

					
					else if (c instanceof AllGroups) {
						AllGroups menuActivity  = (AllGroups)c;
						menuActivity.handler2.sendMessage(message2);
					
					}
					
					
					else if (c instanceof AddGroup) {
						AddGroup menuActivity  = (AddGroup)c;
						menuActivity.handler2.sendMessage(message2);
					
					}
					
					else if (c instanceof GroupMembers) {
						GroupMembers menuActivity  = (GroupMembers)c;
						menuActivity.handler2.sendMessage(message2);
					
					}
					
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	private void syncLoginLogoutStatus() {

		try {
			if (MenuActivity.fragment != null) {
				if (MenuActivity.fragment instanceof FrameActivity) {

					if (ActivityStatus.getActivity() != null) {
						FrameActivity frameActivity = (FrameActivity) MenuActivity.fragment;
						
						frameActivity.handler.sendEmptyMessage(10);
					}
					else {
						Log.e("",
								"======================= 3 ===============");
						new ContactSync(GcmIntentService.this, GcmIntentService.this).execute();;
					}

				}
				else {
					Log.e("",
							"======================= 2 ===============");
					new ContactSync(GcmIntentService.this, GcmIntentService.this).execute();;
				}
			}
			else {
				Log.e("",
						"======================= 1 ===============");
				new ContactSync(GcmIntentService.this, GcmIntentService.this).execute();;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	

	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.

	/**
	 * 
	 * @param msg
	 * NotificationManager		mNotificationManager = (NotificationManager) getActivity()
					.getSystemService(Context.NOTIFICATION_SERVICE);
	mNotificationManager.cancel(1);
	 */
	private void sendNotification(String msg, String convId, boolean isEmergency) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(this, MenuActivity.class);
		intent.putExtra("conv_id", convId);
		intent.putExtra("is_emergency", isEmergency);
		if (convId!=null && convId.length()>0) {
			convIDNOtify     = convId;
		}
		Log.e("", "============= The Notification Conv  Id here is "+convId +" Static Conv Id is "+convIDNOtify);
//		intent.setFlags(
//			Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.app_logo)
				.setContentTitle("New Tribewire")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg)
				.setVibrate(new long[] { 1000, 1000, 1000, 1000,1000 })
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setLights(Color.WHITE, 3000, 3000);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		try {
			if (MenuActivity.fragment != null) {
				if (MenuActivity.fragment instanceof TenFourConversations) {

					if (ActivityStatus.getActivity() != null) {
						TenFourConversations tenFourConversations = (TenFourConversations) MenuActivity.fragment;
						tenFourConversations.handler.sendEmptyMessage(10);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void onContactError(String onError) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onContactResponse(String response) {
	try {
		SyncAddressBook.GSSyncContact(GcmIntentService.this, response);
	} catch (Exception e) {
		e.printStackTrace();
	}
		
	}

}
