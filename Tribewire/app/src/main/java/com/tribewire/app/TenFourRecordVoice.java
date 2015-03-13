package com.tribewire.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eyesay.dialog.EyesayonWayDialog;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.ExtAudioRecorder;
import com.eyesayapp.Utils.GroupModel;
import com.eyesayapp.Utils.Utils;
import com.eyesayapp.Utils.XMLRequestBuilder;

public class TenFourRecordVoice extends Activity {

	// Create objects of MediaRecorder and Preview class
	private ExtAudioRecorder recorder;
	//

	public String baseurl;
	public ProgressDialog pd;
	boolean flag = false;
	final Context context = this;
	boolean isOngoingRecording = false;
	public int count = 0;
	public int seconds = 60;
	public boolean playbackSettings = false;
	public File sourcefile;
	public boolean isPlayingCurrently = false;
	private boolean single_cont_sel = true;
	private boolean group_cont_sel = true;
	TextView txtRecording = null;
	TextView txtSeconds = null;
	private int REQUEST_CANCEL = 909;

	Button recordvoice_btn_send, btnCancel, btn_save, btn_back;
	private int audioDuration;

	private int audioPlayCount = 0;
	// Handle Conversation

	public String phonenumber = "";
	public String contactName = "";
	boolean reply = false;
	int message_id = 0;
	String conversation_id = "";
	Button btn_group_cont, btn_single_cont;

	// Play recording
	MediaPlayer mp = null;
	final String TAG = "TenFourRecordVoice";
	boolean Flag_FOR_FLOW = true;
	private Button startRecordingBtn;
	// private Button stopRecordingBtn;
	private Button playRecordingBtn;
	private String Cont_Name = null;
	private String Cont_No = null;
	private ProgressBar record_progress_bar;
	static int btn_delete_status = 1;
	private boolean drafts_audio = false;
	private String drafts_message_url = "";
	private Dialog dialog;

	private boolean send_single_msg = false;
	private Intent backToTilescontactData;
	private Intent backToTilesGroupData;
	private Timer timer = null;
	int timerCount = 0;
	private boolean isGroup = false;

	private Button record_btn_done;

	private void initView() {

		txtSeconds = (TextView) findViewById(R.id.second);
		CommonFunctions.setDigitalFont(getBaseContext(), txtSeconds);
		txtRecording = (TextView) findViewById(R.id.header);

		startRecordingBtn = (Button) findViewById(R.id.btn_getStart);

		playRecordingBtn = (Button) findViewById(R.id.btn_getPlay);

		// layout
		btnCancel = (Button) findViewById(R.id.btnCancel);

		recordvoice_btn_send = (Button) findViewById(R.id.recordvoice_btn_send);

		record_progress_bar = (ProgressBar) findViewById(R.id.progressBar1);

		btn_back = (Button) findViewById(R.id.record_audio_back);
		btn_save = (Button) findViewById(R.id.record_audio_save);
		record_btn_done = (Button) findViewById(R.id.record_audio_done);
		btn_save.setVisibility(View.INVISIBLE);

	}

	private void setListener() {
		startRecordingBtn.setOnClickListener(click);

		playRecordingBtn.setOnClickListener(click);
		btnCancel.setOnClickListener(click);

		recordvoice_btn_send.setOnClickListener(click);
		btn_back.setOnClickListener(click);
		btn_save.setOnClickListener(click);
		record_btn_done.setOnClickListener(click);
	}

	private void stopRecording() {
		endRecording();
	}

	private void stopPlaying() {

		try {
			if (mp != null && mp.isPlaying()) {
				mp.stop();
				mp.release();
				mp = null;
				isPlayingCurrently = false;
				// txtRecording.setText("Stopped...");
				playRecordingBtn
						.setBackgroundResource(R.drawable.button_play_inactive);
				btnCancel.setEnabled(true);
				record_progress_bar.setProgress(0);
				Log.e("", "Playing Stopped");
			}
		} catch (Exception e) {
			CommonFunctions
					.writeLog("Error while Stop Playing the Audio Recording 180 "
							+ e.getMessage());
		}
	}

	public OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_getStart) {
				Log.e("", "============ isOngoingRecording ==="
						+ isOngoingRecording);
				if (isOngoingRecording) {
					stopRecording();
					// startRecordingBtn
					// .setBackgroundResource(R.drawable.button_record_inactivev);
					Log.e("", "Stopping...............");
				} else {
					if (isPlayingCurrently) {
						stopPlaying();
						Log.e("", "Current Playing Stoppin...............");

					}
					StartRecording();

				}
				btn_delete_status = 1;
			} else if (v.getId() == R.id.record_audio_done) {
				try {
					Intent intent = new Intent();
					intent.putExtra(AUDIO_DATA, recordingPath);
					setResult(RESULT_OK, intent);
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (v.getId() == R.id.btn_getPlay) {
				if (btn_delete_status == 0) {
					Toast.makeText(getApplicationContext(),
							"there is no file to play", 6000).show();
				} else {
					if (isOngoingRecording)
						stopRecording();
					if (isPlayingCurrently)
						stopPlaying();
					if (!isPlayingCurrently && !isOngoingRecording) {
						try {
							playAudioFile(CommonFunctions.getBaseURL()
									+ "temporary/" + "android.wav");

						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
				isOngoingRecording = false;
			} else if (v.getId() == R.id.btnCancel) {
				File file = new File(baseurl + "temporary/", "android.wav");
				if (file.exists()) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							context);

					// set title
					alertDialogBuilder.setTitle("Alert");

					// set dialog message
					alertDialogBuilder
							.setMessage("Recorded file deleted successfully")
							.setCancelable(false)
							.setPositiveButton("ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// if this button is clicked, close
											// // current activity

											if (isOngoingRecording)
												stopRecording();
											if (isPlayingCurrently)
												stopPlaying();

											if (!isPlayingCurrently
													&& !isOngoingRecording) {
												File file = new File(baseurl
														+ "temporary/",
														"android.wav");
												if (file.exists()) {
													file.delete();
													txtSeconds.setText("00:00");
													recordingPath = null;

												}
											}

											dialog.cancel();

										}

									});

					// create alert dialog AlertDialog alertDialog =
					alertDialogBuilder.create();

					// show it
					alertDialogBuilder.show();
					isOngoingRecording = false;
				} else {
					Toast.makeText(getApplicationContext(),
							"there is no file to delete", 6000).show();
					btn_delete_status = 0;

				}
			} else if (v.getId() == R.id.record_audio_back) {
				finish();
			} else if (v.getId() == R.id.record_audio_save) {
				if (isOngoingRecording) {
					endRecording();
				}
				if (isPlayingCurrently) {
					stopPlaying();
				}
				saveMessageToDrafts();
				finish();
			} else if (v.getId() == R.id.btn_group_cont) {
				if (group_cont_sel) {
					Intent test_intent = new Intent();

					test_intent.setClass(TenFourRecordVoice.this,
							GroupListSelection.class);
					// test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
					// Utils.MESSAGE_AUDIO);
					// startActivity(test_intent);

					if (drafts_audio) {
						test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
								Utils.MESSAGE_DRAFTS);
						test_intent.putExtra(Utils.MESSAGE_DRAFTS,
								drafts_message_url);
						startActivityForResult(test_intent, 610);
						Log.e("", "Now the Value while sending is "
								+ drafts_message_url);
					} else {
						test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
								Utils.MESSAGE_AUDIO);

						startActivityForResult(test_intent, REQUEST_CANCEL);
					}

					// btn_group_cont
					// .setBackgroundResource(R.drawable.button_sharetogroups_inactive);
					group_cont_sel = false;

				} else {

					// btn_group_cont
					// .setBackgroundResource(R.drawable.button_sharetogroups_active);
					group_cont_sel = true;
				}
			} else if (v.getId() == R.id.btn_single_cont) {
				if (single_cont_sel) {

					Intent test_intent = new Intent();

					test_intent.setClass(TenFourRecordVoice.this,
							ContactListSelection.class);

					// test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
					// Utils.MESSAGE_AUDIO);
					// startActivity(test_intent);

					if (drafts_audio) {
						test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
								Utils.MESSAGE_DRAFTS);
						test_intent.putExtra(Utils.MESSAGE_DRAFTS,
								drafts_message_url);
						Log.e("", "Now the Value while sending is "
								+ drafts_message_url);
						startActivityForResult(test_intent, 610);
					} else {
						test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
								Utils.MESSAGE_AUDIO);
						startActivityForResult(test_intent, REQUEST_CANCEL);
					}

					// btn_single_cont
					// .setBackgroundResource(R.drawable.button_sharetocontacts_inactive);
					single_cont_sel = false;
				} else {
					// btn_single_cont
					// .setBackgroundResource(R.drawable.button_sharetocontacts_active);
					single_cont_sel = true;
				}
			} else if (v.getId() == R.id.recordvoice_btn_send) {
				File file1 = new File(baseurl + "temporary/", "android.wav");
				if (file1.exists()) {

					if (isOngoingRecording)
						stopRecording();
					if (isPlayingCurrently)
						stopPlaying();
					// send message when contact is already selected........
					if (isReplyConstPresent()) {
						if (send_single_msg) {

							sendAudioToFriends();
						} else {
							if (CommonFunctions
									.isInternetConnected(TenFourRecordVoice.this)) {
								if (checkFileToSend()) {
									replyToFriend();
								} else {
									Toast.makeText(
											TenFourRecordVoice.this,
											"Before you reply to your friend its time to record ",
											Toast.LENGTH_LONG).show();
								}
							}

							else {
								try {
									CommonFunctions
											.showAlert(
													TenFourRecordVoice.this,
													getString(R.string.no_connectivity),
													"Network Error");
								} catch (Exception e) {

								}
							}

						}
					} else {
						sendAudioToFriends();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							"there is no file to send", 6000).show();

				}
			}
		}

	};

	private void sendAudioToFriends() {
		dialog = new Dialog(context,
				android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		// dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.alert_cont_group);
		// dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);

		// dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		btn_single_cont = (Button) dialog.findViewById(R.id.btn_single_cont);
		btn_group_cont = (Button) dialog.findViewById(R.id.btn_group_cont);
		// if button is clicked, close the custom dialog
		Button btn_dialog_back = (Button) dialog
				.findViewById(R.id.record_dialog_back);
		Button btn_dialog_save = (Button) dialog
				.findViewById(R.id.record_dialog_save);
		btn_dialog_save.setVisibility(View.INVISIBLE);
		Button btn_dialog_btn_send = (Button) dialog
				.findViewById(R.id.record_dialog_btn_send);
		// if button is clicked, close the custom dialog
		btn_dialog_back.setOnClickListener(dialog_Click);
		btn_dialog_save.setOnClickListener(dialog_Click);
		btn_dialog_btn_send.setOnClickListener(dialog_Click);
		btn_single_cont.setOnClickListener(click);
		btn_group_cont.setOnClickListener(click);

		dialog.show();
		try {
			if (send_single_msg) {
				// btn_single_cont
				// .setBackgroundResource(R.drawable.button_sharetocontacts_inactive);
				single_cont_sel = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	OnClickListener dialog_Click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.record_dialog_back) {
				if (dialog != null) {
					dialog.dismiss();
					try {
						setResult(RESULT_OK);
					} catch (Exception e) {
						e.printStackTrace();
					}
					finish();
				}
			} else if (v.getId() == R.id.record_dialog_save) {

				if (isOngoingRecording) {
					endRecording();
				}
				if (isPlayingCurrently) {
					stopPlaying();
				}
				saveMessageToDrafts();
				if (dialog != null) {
					dialog.dismiss();
				}
				finish();
			} else if (v.getId() == R.id.record_dialog_btn_send) {
				if (!single_cont_sel || !group_cont_sel) {
					if (CommonFunctions
							.isInternetConnected(TenFourRecordVoice.this)) {
						Thread sendMsgThread = new Thread(new Runnable() {

							@Override
							public void run() {
								Looper.prepare();
								sendMultipleMessages();
								Looper.loop();
							}
						});
						sendMsgThread.start();
					} else {
						try {
							Toast.makeText(getApplicationContext(),
									"Please check your internet connectivity",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						Toast.makeText(
								getApplicationContext(),
								"Please select atleast one tile to enjoy eyesay",
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {

					}

				}
			}

		}
	};

	// In this method, create an object of MediaRecorder class. Create an object
	// of
	// RecorderPreview class(Customized View). Add RecorderPreview class object
	// as content of UI.
	public void onCreate(Bundle savedInstanceState) {
		CommonFunctions.writeLog("On Create of Record Voice Called");
		super.onCreate(savedInstanceState);
		try {
			CommonFunctions.writeLog("The OS Version"
					+ XMLRequestBuilder.getOSVersion() + "==Device Model ="
					+ Build.MODEL + ", SDK INT" + Build.VERSION.SDK_INT);
			Log.e("", "==Device Model =========" + Build.MODEL);

		} catch (Exception e) {
			e.printStackTrace();
		}
		setContentView(R.layout.recordvoice);
		initView();

		Log.v(TAG, "onCreate is called");
		// Get All the friend number from the Send Message... check if it is
		// reply or not???
		// friend=FrameActivity.friend;
		try {
			Cont_Name = FrameActivity.Cont_Name;
			Cont_No = FrameActivity.Cont_No;
			if (Cont_Name != null && Cont_No != null) {
				// Toast.makeText(getApplicationContext(),
				// "message is sending to " + Cont_Name + "::" + Cont_No, 9000)
				// .show();
				FrameActivity.Cont_Name = null;
				FrameActivity.Cont_No = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			reply_number = getIntent().getStringExtra(Utils.REPLY_NUMBER);
			reply_name = getIntent().getStringExtra(Utils.REPLY_FRIEND_NAME);
			reply_message_id = getIntent().getIntExtra(Utils.REPLY_MESSAGE_ID,
					0);
			reply_status = getIntent()
					.getBooleanExtra(Utils.REPLY_STATUS, true);
			isGroup = getIntent().getBooleanExtra(Utils.REPLY_STATUS_GROUP,
					false);
			reply_conversation_id = getIntent().getStringExtra(
					Utils.REPLY_CONVERSATION_ID);
			try {
				send_single_msg = getIntent().getBooleanExtra(
						Utils.SEND_SINGLE_MESSAGE, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		// /

		// Check PlayBack exist
		playbackSettings = CommonFunctions.getPref(TenFourRecordVoice.this,
				CommonFunctions.PLAYBACK, true);

		Debugger.debugE("Play Back +" + playbackSettings);

		running = false;
		setListener();

		try {
			drafts_audio = getIntent().getExtras().getBoolean(Utils.DIALOG_KEY);
			drafts_message_url = getIntent().getExtras().getString(
					Utils.MESSAGE_DRAFTS);
			Log.e("", "From Eyesaydrafts value is " + drafts_message_url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Commented on 9th DEC 2012 as Audio Recording is stoped to auto
		// recording
		// if (!drafts_audio) {
		// startRecordingBtn.setEnabled(false);
		//
		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// StartRecording();
		// startRecordingBtn
		// .setBackgroundResource(R.drawable.button_record_active);
		// }
		// }, 1000);
		// }

		if (drafts_audio) {
			sendAudioToFriends();
		}

		// txtRecording.setText("Recording...");
		// StartRecording();
	}

	volatile boolean running;
	private Thread recordThread;

	/**
	 * Start recording the voice
	 */

	String recordingPath = "";

	public void StartRecording() {

		if (recordThread != null) {
			try {
				// recordThread.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			CommonFunctions.writeLog("Audio Recording Started ");
		} catch (Exception e) {

		}
		startRecordingBtn.setEnabled(true);
		btnCancel.setEnabled(false);
		record_progress_bar.setProgress(0);
		recorder = ExtAudioRecorder.getInstanse(false);
		running = true;
		try {
			baseurl = CommonFunctions.getBaseURL();
		} catch (IOException e1) {
			e1.printStackTrace();
			try {
				CommonFunctions.writeLog("Base Url Null Line No 781 "
						+ e1.getMessage());
			} catch (Exception e) {

			}
		}
		// recorder.setMaxDuration(30000);

		recorder.setOutputFile(baseurl + "temporary/" + "android.wav");

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			try {
				CommonFunctions
						.writeLog("Unable to Prepare Audio Recorder IllegalStateException 794"
								+ e.getMessage());
			} catch (Exception e2) {

			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions.writeLog("Unable to Prepare Audio Recorder 800"
					+ e.getMessage());
		}
		recorder.start();
		recordingPath = baseurl + "temporary/" + "android.wav";
		try {
			CommonFunctions.writeLog("Audio Recorder Start Called 804");
		} catch (Exception e) {
			e.printStackTrace();

		}
		isOngoingRecording = true;
		txtRecording.setText("Recording...");
		count = 0;
		try {
			recordThread = new Thread() {
				public void run() {
					Looper.prepare();
					try {
						while (count++ <= seconds && isOngoingRecording) {
							progressHandler.sendMessage(progressHandler
									.obtainMessage());
							sleep(1000);
						}
						if (count >= seconds && isOngoingRecording) {
							Debugger.debugE("if count" + count);
							endRecording();
						}
					} catch (Exception e) {
						Debugger.debugE("Exception" + e.getMessage());
					}
					Looper.loop();
				}
			};
			recordThread.start();
		} catch (Exception e) {
			CommonFunctions
					.writeLog("  Exception In the Audio Recorder Recording Thread "
							+ e.getMessage());
		}

	}

	Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			updateUICount();
		}
	};

	Handler audioPlayProgressHandler = new Handler() {
		public void handleMessage(Message msg) {
			updateUIAudioCountCount();
		}
	};

	void updateUICount() {
		try {

			if (isOngoingRecording) {
				int displaycount = 60 - count + 1;
				try {
					record_progress_bar
							.setProgress((int) (((count + 1) * 100) / seconds));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Log.e("", "===============Count ++"+(count*1.65));
				if (displaycount - 1 >= 0) {
					if (displaycount - 1 <= 9) {
						// txtSeconds.setText("00:0" + (displaycount - 1));
						txtSeconds.setText("00:" + (count));
					} else {
						// txtSeconds.setText("00:" + (displaycount - 1));
						if (count > 9) {
							txtSeconds.setText("00:" + (count));
						} else {
							txtSeconds.setText("00:0" + (count));
						}
					}
				}
				if (displaycount == 0) {
					txtRecording.setText("Stopped...");
					endRecording();
				}

				CommonFunctions
						.writeLog("Updating Audio Recording UI Count with count Value "
								+ count);
			}

		} catch (Exception e) {
			CommonFunctions.writeLog("Error while updating UI count "
					+ e.getMessage());
		}

	}

	void updateUIAudioCountCount() {

		try {

			if (isPlayingCurrently) {

				int displayAudiocount = audioDuration - audioPlayCount;

				try {
					record_progress_bar
							.setProgress((int) ((audioPlayCount * 100) / audioDuration));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (displayAudiocount >= 0) {
					if (displayAudiocount <= 9) {
						txtSeconds.setText("00:0" + displayAudiocount);
					} else {
						txtSeconds.setText("00:" + displayAudiocount);
					}
				}
				CommonFunctions.writeLog("Updating audio playing UI Count"
						+ audioPlayCount);
			}

		} catch (Exception e) {
			CommonFunctions
					.writeLog("Error while updating Audio PLaying UI Count "
							+ e.getMessage());
		}
	}

	// Handler h = new Handler();

	public void endRecording() {
		// txtRecording.setText("Stopped...");
		try {
			running = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			isOngoingRecording = false;

			isOngoingRecording = false;
			isPlayingCurrently = false;
			record_progress_bar.setProgress(100);
			btnCancel.setEnabled(true);
			Intent intent = new Intent(this, EyesayPlayAudio.class);
			intent.putExtra("messagetype", 0);
			intent.putExtra("type", 0);
			intent.putExtra("url", baseurl + "temporary/" + "android.wav");

			intent.putExtra("receiver", "");
			startActivityForResult(intent, REQUEST_CODE_USE_RETAKE);
			CommonFunctions.writeLog("Audio Recording ended successfully ");

		} catch (Exception e) {
			e.printStackTrace();
			// Debugger.debugE(e.getMessage());
			CommonFunctions.writeLog("Error while Stoping Audio Recording "
					+ e.getMessage());
		}

	}

	/*
	 * final Runnable playfile = new Runnable() {
	 * 
	 * @Override public void run() { try { while (audioPlayCount++ <=
	 * audioDuration ) { updateUIAudioCountCount(); Thread.sleep(1000); } }
	 * catch (Exception e) { } } };
	 */

	public void playAudioFile(String fileName) {

		File file = new File(fileName);
		if (file.exists()) {

			try {
				btnCancel.setEnabled(false);
				mp = new MediaPlayer();
				mp.setDataSource(fileName);
				mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
				mp.setVolume(15, 15);
				mp.prepare();
				mp.start();
				isPlayingCurrently = true;
				txtRecording.setText("Playing...");
				audioDuration = mp.getDuration() / 1000;
				audioPlayCount = 0;

				record_progress_bar.setProgress(0);
				try {
					new Thread() {
						public void run() {
							Looper.prepare();
							try {
								while (audioPlayCount <= audioDuration
										&& isPlayingCurrently) {
									audioPlayProgressHandler
											.sendMessage(audioPlayProgressHandler
													.obtainMessage());
									sleep(1000);
									audioPlayCount++;
								}
								if (audioPlayCount >= audioDuration
										&& isPlayingCurrently) {
									Debugger.debugE("if audioPlayCount"
											+ audioPlayCount);
								}
							} catch (Exception e) {
								e.printStackTrace();
								Debugger.debugE("Exception" + e.getMessage());
							}
							Looper.loop();
						}
					}.start();
				} catch (Exception e) {
					CommonFunctions
							.writeLog("Error while starting Audio Playing Thread===="
									+ e.getMessage());
				}

				// h.post(playfile);
				mp.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {

						stopPlaying();

						btnCancel.setEnabled(true);
						//
					}
				});
			} catch (Exception e) {
				// Debugger.debugE("error " + e.getMessage());
				mp.release();
				mp = null;
				isPlayingCurrently = false;
				txtRecording.setText("Stopped...");
				CommonFunctions
						.writeLog("Error while Playing Recorded Audio File "
								+ e.getMessage());
			}

		} else {
			txtRecording.setText("File Does Not Exist");
			Toast.makeText(getBaseContext(), "Audio File Does Not Exist", 5000)
					.show();
			// Alert dialog if file dose not exit

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set title
			alertDialogBuilder.setTitle("EyeSay");

			// set dialog message
			alertDialogBuilder
					.setMessage("Before you play, it's time to record!")
					.setCancelable(false)
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									dialog.cancel();

								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();

			CommonFunctions
					.writeLog("File Does Not exists so cannot play this audio File");
		}
	}

	// final Runnable sendRecordedMessage = new Runnable() {
	//
	// public void run() {
	//
	// NotificationManager mNotificationManager = (NotificationManager)
	// getSystemService(Context.NOTIFICATION_SERVICE);
	// mNotificationManager.cancel(CommonFunctions.NOTIFICATION_ID);
	//
	// try {
	// baseurl = CommonFunctions.getBaseURL();
	//
	// Debugger.debugE("in sending...");
	// if (CommonFunctions.SendMessage(TenFourRecordVoice.this,
	// true,
	// baseurl + "temporary/" + "android.wav",
	// phonenumber,
	// contactName,
	// reply,
	// conversation_id)) {
	// Debugger.debugE("in mesage id ... + " + message_id);
	// if (message_id != 0) {
	//
	// DataBase db = new DataBase(TenFourRecordVoice.this);
	// try {
	// ContentValues cv = new ContentValues();
	// cv.put("reply", 1);
	// db.open();
	// db.update(DataBase.Receive_table, DataBase.Receive_int, "sr_no=" +
	// message_id, cv);
	// db.close();
	// } catch (Exception e) {
	//
	// }
	// }
	// }
	// pd.dismiss();
	// }
	//
	// catch (IOException e) {
	// Debugger.debugE("in exception " + e.getMessage());
	// } finally {
	// setResult(RESULT_FIRST_USER);
	// finish();
	// }
	// }
	// };

	// public void CreateProgressBar() {
	//
	// pd = new ProgressDialog(TenFourRecordVoice.this);
	// pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// pd.setMessage("10-4Me on its way..");
	// pd.setCancelable(false);
	// pd.show();
	//
	// }

	@Override
	protected void onResume() {
		ActivityStatus.appStatus(this, true);
		super.onResume();
		CommonFunctions.writeLog("In the Audio Recording onResume");
		try {
			if (isPlayingCurrently) {
				// txtRecording.setText("Playing...ravi on resume");
				mp.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions.writeLog("Error in Audio Recording onResume "
					+ e.getMessage());
		}
	}

	@Override
	protected void onPause() {
		ActivityStatus.appStatus(null, false);
		super.onPause();
		CommonFunctions.writeLog("In the Audio Recording onPause");
		try {
			if (isOngoingRecording) {
				// txtRecording.setText("Recording...on pause ravi");
				recorder.stop();
				recorder.release();
				recorder = null;
				isOngoingRecording = false;
				finish();
			} else if (isPlayingCurrently) {
				if (mp != null) {
					// txtRecording.setText("Playing...ravi on pause");
					// mp.pause();
					try {
						stopPlaying();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions.writeLog("Error in Audio Recording onPause "
					+ e.getMessage());
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 4) {

			try {
				Intent intent = new Intent();
				intent.putExtra(AUDIO_DATA, recordingPath);
				setResult(RESULT_OK, intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
		finish();
	}

	public void CreateProgressBar() {

		pd = new ProgressDialog(TenFourRecordVoice.this);
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage(" Please Wait..");
		pd.setCancelable(false);
		pd.show();
	}

	Handler h = new Handler();

	public EyesayonWayDialog eyesayonWayDialog;

	/**
	 * Used to save audio message in draft
	 */
	private void saveMessageToDrafts() {

		if (checkFileToSend())
			try {
				{
					String dest = null;
					try {
						dest = CommonFunctions.getAudioURL()
								+ System.currentTimeMillis() + ".wav";
						Log.e("", "================== dest===" + dest);
					} catch (IOException e1) {

						e1.printStackTrace();
					}
					try {
						Log.e("",
								"================== "
										+ CommonFunctions.getAudioURL());

						CommonFunctions.copy(
								new File(CommonFunctions.getBaseURL()
										+ "/temporary/android.wav"), new File(
										dest));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (backToTilescontactData != null) {
						DataBase db1 = null;
						try {

							db1 = new DataBase(this);
							db1.open();
							try {
								final String numbers = backToTilescontactData
										.getStringExtra("receiver");
								final String names = backToTilescontactData
										.getStringExtra("names");
								// final String
								// file_url=backToTilescontactData.getStringExtra("msg_url");
								// final Boolean
								// messageType=backToTilescontactData.getBooleanExtra("messageType",
								// false);
								String[] numberArray = numbers.split(",");
								String[] namesArray = names.split(",");
								for (int i = 0; i < namesArray.length; i++) {
									try {

										ContentValues cv = new ContentValues();
										cv.put("message_id", "");
										cv.put("sender",
												CommonFunctions
														.getPref(
																TenFourRecordVoice.this,
																CommonFunctions.PHONE_NUMBER,
																""));
										cv.put("receiver", namesArray[i]);
										cv.put("message_type", 0);
										try {
											cv.put("file_url", dest);
										} catch (Exception e) {
											e.printStackTrace();
										}
										cv.put("date_time",
												"" + System.currentTimeMillis());
										cv.put("type", 0);
										cv.put("phonenumber", numberArray[i]);
										db1.insert_content(
												DataBase.drafts_table,
												DataBase.drafts_int, cv);

										setResult(Utils.EYE_SAY_AUDIO_SAVE_RESULT);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();

						} finally {
							if (db1 != null) {
								db1.close();
							}

						}

						setResult(Utils.EYE_SAY_AUDIO_SAVE_RESULT);
					}

					if (backToTilesGroupData != null) {
						DataBase db2 = null;
						try {

							db2 = new DataBase(this);
							db2.open();
							try {
								final String numbers = backToTilesGroupData
										.getStringExtra("receiver");
								final String names = backToTilesGroupData
										.getStringExtra("names");
								// final String
								// file_url=backToTilescontactData.getStringExtra("msg_url");
								// final Boolean
								// messageType=backToTilesGroupData.getBooleanExtra("messageType",
								// false);
								String[] numberArray = numbers.split(",");
								String[] namesArray = names.split(",");
								for (int i = 0; i < namesArray.length; i++) {
									try {

										ContentValues cv = new ContentValues();
										cv.put("message_id", "");
										cv.put("sender",
												CommonFunctions
														.getPref(
																TenFourRecordVoice.this,
																CommonFunctions.PHONE_NUMBER,
																""));
										cv.put("receiver", namesArray[i]);
										cv.put("message_type", 0);
										try {
											cv.put("file_url", dest);
										} catch (Exception e) {
											e.printStackTrace();
										}
										cv.put("date_time",
												"" + System.currentTimeMillis());
										cv.put("type", 0);
										cv.put("phonenumber", numberArray[i]);
										db2.insert_content(
												DataBase.drafts_table,
												DataBase.drafts_int, cv);

										setResult(Utils.EYE_SAY_VIDEO_SAVE_RESULT);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();

						} finally {
							if (db2 != null) {
								db2.close();
							}
						}

						setResult(Utils.EYE_SAY_AUDIO_SAVE_RESULT);
					}

					if (backToTilescontactData == null
							&& backToTilesGroupData == null) {

						if (isReplyConstPresent()) {
							DataBase db = null;
							try {

								db = new DataBase(this);
								db.open();
								ContentValues cv = new ContentValues();

								cv.put("message_id", "");
								cv.put("sender", CommonFunctions.getPref(
										TenFourRecordVoice.this,
										CommonFunctions.PHONE_NUMBER, ""));
								cv.put("receiver", reply_name);
								cv.put("message_type", 0);
								try {
									cv.put("file_url", dest);
								} catch (Exception e) {
									e.printStackTrace();
								}
								cv.put("date_time",
										"" + System.currentTimeMillis());
								cv.put("type", 0);
								cv.put("phonenumber", reply_number);
								long rowId = db.insert_content(
										DataBase.drafts_table,
										DataBase.drafts_int, cv);
								Log.e("", "The Row Entered successfully "
										+ rowId);
								db.close();
								setResult(Utils.EYE_SAY_AUDIO_SAVE_RESULT);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						else {
							DataBase db = null;
							try {

								db = new DataBase(this);
								db.open();
								ContentValues cv = new ContentValues();

								cv.put("message_id", "");
								cv.put("sender", CommonFunctions.getPref(
										TenFourRecordVoice.this,
										CommonFunctions.PHONE_NUMBER, ""));
								cv.put("receiver", "To: No Recipient");
								cv.put("message_type", 0);
								try {
									cv.put("file_url", dest);
								} catch (Exception e) {
									e.printStackTrace();
								}
								cv.put("date_time",
										"" + System.currentTimeMillis());
								cv.put("type", 0);
								cv.put("phonenumber", "");
								long rowId = db.insert_content(
										DataBase.drafts_table,
										DataBase.drafts_int, cv);
								Log.e("", "The Row Entered successfully "
										+ rowId);
								db.close();
								setResult(Utils.EYE_SAY_AUDIO_SAVE_RESULT);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

				}
			} catch (Exception e) {

				e.printStackTrace();
				try {
					if (eyesayonWayDialog != null) {
						eyesayonWayDialog.dismiss();
					}
					if (dialog != null) {
						dialog.dismiss();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		else {
			Toast.makeText(this, "Before save message,Its time to record",
					Toast.LENGTH_SHORT).show();
		}

	}

	private boolean checkFileToSend() {
		boolean isFile = false;
		try {
			File file = new File(baseurl + "temporary/" + "android.wav");

			Log.e("", "================File Exists " + file.exists()
					+ "========= File Length " + file.length());
			if (file.exists()) {

				isFile = true;

			} else {

				isFile = false;

			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return isFile;

	}

	public static final String AUDIO_DATA = "audio_data";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 610 && resultCode == 777) {
			try {
				if (dialog != null) {
					dialog.dismiss();
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == REQUEST_CANCEL && resultCode == RESULT_OK) {
			try {
				// btn_single_cont
				// .setBackgroundResource(R.drawable.button_sharetocontacts_active);
				single_cont_sel = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (requestCode == REQUEST_CANCEL && resultCode == 777) {
			try {
				if (dialog != null) {
					dialog.dismiss();
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == REQUEST_CANCEL && resultCode == 779) {
			try {
				// btn_group_cont
				// .setBackgroundResource(R.drawable.button_sharetogroups_active);
				group_cont_sel = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (requestCode == REQUEST_CANCEL
				&& resultCode == Utils.SINGLE_BACKTOTILE_RESULT) {
			try {
				backToTilescontactData = data;
				if (backToTilescontactData == null) {
					try {
						btn_single_cont
								.setBackgroundResource(R.drawable.button_sharetocontacts_active);
						single_cont_sel = true;
						Log.e("", "=================Here");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == REQUEST_CANCEL
				&& resultCode == Utils.GROUP_BACKTOTILE_RESULT) {
			try {
				backToTilesGroupData = data;
				if (backToTilesGroupData == null) {

					try {
						btn_group_cont
								.setBackgroundResource(R.drawable.button_sharetogroups_active);
						group_cont_sel = true;
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// ////////////////////////////////////

		else if (requestCode == 610 && resultCode == RESULT_OK) {
			try {
				btn_single_cont
						.setBackgroundResource(R.drawable.button_sharetocontacts_active);
				single_cont_sel = true;
				Log.e("", "=================Here");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		else if (requestCode == 610 && resultCode == 779) {
			try {
				btn_group_cont
						.setBackgroundResource(R.drawable.button_sharetogroups_active);
				group_cont_sel = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (requestCode == 610
				&& resultCode == Utils.SINGLE_BACKTOTILE_RESULT) {
			try {
				backToTilescontactData = data;
				if (backToTilescontactData == null) {
					try {
						btn_single_cont
								.setBackgroundResource(R.drawable.button_sharetocontacts_active);
						single_cont_sel = true;
						Log.e("", "=================Here");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == 610
				&& resultCode == Utils.GROUP_BACKTOTILE_RESULT) {
			try {
				backToTilesGroupData = data;
				if (backToTilesGroupData == null) {

					try {
						btn_group_cont
								.setBackgroundResource(R.drawable.button_sharetogroups_active);
						group_cont_sel = true;
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (requestCode == REQUEST_CODE_USE_RETAKE
				&& resultCode == EyesayPlayAudio.RESULT_CODE_USE_VIDEO) {
			Log.e("", "--------------------------- Use Audio ----------------");
			try {
				Intent intent = new Intent();
				String videoUrl = null;

				try {
					// baseurl = CommonFunctions.getBaseURL();

					// boolean isExists =checkFileToSend();
					// / if (isExists) {

					// }
					// else {
					// videoUrl = null;
					// }
					Log.e("", "The full Url is  " + baseurl + "temporary/"
							+ "android.wav");
				} catch (Exception e) {
					e.printStackTrace();
				}

				intent.putExtra(AUDIO_DATA, baseurl + "temporary/"
						+ "android.wav");

				setResult(RESULT_OK, intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

			finish();

		} else if (requestCode == REQUEST_CODE_USE_RETAKE
				&& resultCode == EyesayPlayAudio.RESULT_CODE_RETAKE_VIDEO) {
			Log.e("",
					"--------------------------- Retake Video ----------------");
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private int REQUEST_CODE_USE_RETAKE = 565;

	private boolean isReplyConstPresent() {

		try {
			if (reply_number != null && reply_name != null
					&& reply_conversation_id != null
					&& reply_number.length() > 0 && reply_name.length() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private void replyToFriend() {
		eyesayonWayDialog = new EyesayonWayDialog(TenFourRecordVoice.this);
		eyesayonWayDialog.setCancelable(true);
		eyesayonWayDialog.show();
		eyesayonWayDialog.getHandlerObj().sendEmptyMessage(9);
		new Thread(sendRecordedMessage).start();

	}

	private String reply_number, reply_name, reply_conversation_id;
	private boolean reply_status;
	private int reply_message_id;

	final Runnable sendRecordedMessage = new Runnable() {

		public void run() {
			Looper.prepare();
			try {
				CommonFunctions
						.writeLog("Replying to friend in the Audio Recording ");
				baseurl = CommonFunctions.getBaseURL();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// message type false means it is a video message
			boolean messageType = true;

			String media_message_url = baseurl + "temporary/android.wav";
			try {
				Debugger.debugE("in sending...");
				Log.e("", "The Commma Separated Numbers are " + reply_number
						+ "\n Names " + reply_name);
				CommonFunctions
						.writeLog("The Commma Separated Numbers in Audio Recording reply "
								+ reply_number + "\n Names " + reply_name);
				try {
					CommonFunctions
							.writeLog("The Size of file in Audio Recording reply case "
									+ new File(media_message_url).length());
				} catch (Exception e) {
					e.printStackTrace();

				}
				try {
					if (eyesayonWayDialog != null) {
						eyesayonWayDialog.getHandlerObj().sendEmptyMessage(9);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (isGroup) {
					DataBase db = null;
					int groupMemberCount = 0;
					try {

						db = new DataBase(TenFourRecordVoice.this);
						db.open();

						Cursor c = db.fetchAll(DataBase.Group_table,
								DataBase.Group_int, null, "groupname = '"
										+ reply_name.trim() + "'");
						Log.e("", "The Group Count is " + c.getCount());
						if (c != null && c.getCount() > 0) {
							c.moveToFirst();
							int group_Id = c.getInt(0);
							Cursor c1 = db.fetchAll(
									DataBase.Group_Member_table,
									DataBase.Group_member_int, null,
									"group_id = " + group_Id);
							Log.e("",
									"Group Member Detail Count is "
											+ c1.getCount());
							if (c1 != null && c1.getCount() > 0) {
								reply_number = "";
								if (c1.moveToFirst()) {
									do {
										int friendId = c1.getInt(2);
										Log.e("", "  Friend Id is " + friendId);
										try {
											Cursor c2 = db.fetch(
													DataBase.Friends_table,
													DataBase.Friends_int,
													"sr_no =" + friendId);
											String phoneNumber = c2
													.getString(3);
											Log.e("",
													"  Friend Phonenumber is "
															+ phoneNumber);
											reply_number += phoneNumber + ",";
											groupMemberCount++;

											c2.close();
											// 3 for Number
										} catch (Exception e) {
											e.printStackTrace();
										}
									} while (c1.moveToNext());
								}
							}
							c1.close();

						}
						c.close();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (db != null) {
							db.close();
						}
					}
					if (groupMemberCount <= 0) {
						try {
							if (eyesayonWayDialog != null) {
								eyesayonWayDialog.dismiss();

							}

							setResult(RESULT_OK);
							handler2.sendEmptyMessage(15);

							// finish();

						} catch (Exception e) {
							e.printStackTrace();
						}

						return;
					}
					ArrayList<String> list = new ArrayList<String>();

					list.add(reply_name);
					if (reply_number.endsWith(",")) {
						reply_number = reply_number.substring(0,
								reply_number.length() - 1);
						Log.e("", "The Required Numbers are " + reply_number);
					}
					ArrayList<GroupModel> groupModels = new ArrayList<GroupModel>();
					GroupModel groupModel = new GroupModel();
					groupModel.setGroupName(reply_name);
					groupModel.setGroupMember(reply_number);
					groupModels.add(groupModel);
					CommonFunctions
							.writeLog("Sending reply message to groups in Audio Recording Activity");
					if (CommonFunctions.SendGroupMessage(
							TenFourRecordVoice.this, messageType,
							media_message_url, reply_number, reply_name,
							reply_status, reply_conversation_id, list,
							groupModels)) {
						CommonFunctions
								.writeLog(" reply message  sent to groups in Audio Recording Activity successfully");
						try {
							if (eyesayonWayDialog != null) {
								eyesayonWayDialog.getHandlerObj()
										.sendEmptyMessage(11);
							}
							setResult(RESULT_OK);

						} catch (Exception e) {
							e.printStackTrace();
						}
						Debugger.debugE("in mesage id ... + " + message_id);
						if (reply_message_id != 0) {

							DataBase db1 = new DataBase(TenFourRecordVoice.this);
							try {
								ContentValues cv = new ContentValues();
								cv.put("reply", 1);
								db1.open();
								db1.update(DataBase.Receive_table,
										DataBase.Receive_int, "sr_no="
												+ message_id, cv);
								db1.close();
							} catch (Exception e) {

							}
						}
					} else {
						if (eyesayonWayDialog != null) {
							eyesayonWayDialog.dismiss();
						}

						handler2.sendEmptyMessage(1);
					}

				} else {
					if (CommonFunctions.SendMessage(TenFourRecordVoice.this,
							messageType, media_message_url, reply_number,
							reply_name, reply_status, reply_conversation_id)) {

						try {
							if (eyesayonWayDialog != null) {
								eyesayonWayDialog.getHandlerObj()
										.sendEmptyMessage(11);
							}
							setResult(RESULT_OK);

							CommonFunctions
									.writeLog(" reply message  sent to Single User in Audio Recording Activity successfully");
						} catch (Exception e) {
							e.printStackTrace();
						}
						Debugger.debugE("in mesage id ... + " + message_id);
						if (reply_message_id != 0) {

							DataBase db = new DataBase(TenFourRecordVoice.this);
							try {
								ContentValues cv = new ContentValues();
								cv.put("reply", 1);
								db.open();
								db.update(DataBase.Receive_table,
										DataBase.Receive_int, "sr_no="
												+ message_id, cv);
								db.close();
							} catch (Exception e) {

							}
						}
					} else {
						if (eyesayonWayDialog != null) {
							eyesayonWayDialog.dismiss();
						}

						handler2.sendEmptyMessage(1);
						CommonFunctions
								.writeLog(" reply message  sent to Single User in Audio Recording Activity failed");
					}
				}

			}

			catch (Exception e) {
				CommonFunctions
						.writeLog("Error while reply message in Audio recording"
								+ e.getMessage());
			} finally {

				if (eyesayonWayDialog != null)
					eyesayonWayDialog.dismiss();

				finish();
			}
			Looper.loop();

		}
	};

	private void sendMultipleMessages() {
		CommonFunctions
				.writeLog("Sending Multiple Message in Audio Recording Activity");
		if (!single_cont_sel || !group_cont_sel) {
			if (dialog != null) {
				dialog.dismiss();
			}
			handler2.sendEmptyMessage(5);
		}

		try {
			if (CommonFunctions.isInternetConnected(TenFourRecordVoice.this)) {

				startTimer();

				if (!single_cont_sel) {

					if (send_single_msg) {
						try {
							baseurl = CommonFunctions.getBaseURL();
						} catch (Exception e) {
							e.printStackTrace();
						}
						// message type false means it is a video message
						boolean messageType = true;

						String media_message_url = baseurl
								+ "temporary/android.wav";
						if (reply_number != null && reply_name != null
								&& reply_conversation_id != null
								&& reply_number.length() > 0
								&& reply_name.length() > 0
								&& reply_conversation_id.length() > 0) {

							// false means Video Message
							try {
								CommonFunctions
										.writeLog("Length of wav file during Sending of Reply msg in Audio Recording "
												+ new File(media_message_url)
														.length());
							} catch (Exception e) {
								CommonFunctions
										.writeLog(" Audio Recoding Send single "
												+ e.getMessage());
							}
							if (CommonFunctions.SendMessage(context,
									messageType, media_message_url,
									reply_number, reply_name, false,
									reply_conversation_id)) {
								eyesayonWayDialog.getHandlerObj()
										.sendEmptyMessage(90);

								// }
								// handler2.sendEmptyMessage(3);

								setResult(RESULT_OK);
								CommonFunctions
										.writeLog("Audio Recording Send single message sent successfully");
							} else {
								String dest = "";
								try {
									dest = CommonFunctions.getAudioURL()
											+ System.currentTimeMillis()
											+ ".wav";

									Log.e("", "================== dest==="
											+ dest);
									CommonFunctions.sendFailMessageToDrafts(
											TenFourRecordVoice.this,
											reply_number, reply_name, dest,
											media_message_url);
									handler2.sendEmptyMessage(1);
								} catch (IOException e1) {

									e1.printStackTrace();
								}
								setResult(RESULT_OK);
								CommonFunctions
										.writeLog("Audio Recording Send single message sent failed so saving to drafts");
							}

						}
					}

					if (backToTilescontactData != null) {
						final String numbers = backToTilescontactData
								.getStringExtra("receiver");
						final String names = backToTilescontactData
								.getStringExtra("names");
						final String file_url = backToTilescontactData
								.getStringExtra("msg_url");
						final Boolean messageType = backToTilescontactData
								.getBooleanExtra("messageType", false);

						Log.e("",
								"==========================================Single Contact ====================================================================");
						Log.e("", "Name " + names);
						Log.e("", "Numbers " + numbers);
						Log.e("", "File Url" + file_url);
						Log.e("", "messageType " + messageType);
						CommonFunctions
								.writeLog("Audio Recording BackToTilescontactData "
										+ "Name "
										+ names
										+ "====numbers"
										+ numbers
										+ "=====file_url"
										+ file_url
										+ "====" + messageType);
						try {
							CommonFunctions
									.writeLog("Length of wav file during Sending of BackToTilescontactData msg in Audio Recording "
											+ new File(file_url).length());
						} catch (Exception e) {
							CommonFunctions.writeLog(" Audio Recoding Reply "
									+ e.getMessage());
						}
						Log.e("",
								"=======================================================================================================");
						if (numbers != null && names != null
								&& file_url != null && numbers.length() > 0
								&& names.length() > 0 && file_url.length() > 0) {

							if (CommonFunctions.SendMessage(context,
									messageType, file_url, numbers, names,
									false, null)) {
								eyesayonWayDialog.getHandlerObj()
										.sendEmptyMessage(90);

								// }
								// handler2.sendEmptyMessage(3);
								CommonFunctions
										.writeLog("BackToTilescontactData Audio Recording Message Sent successfully ");

							} else {
								String dest = "";
								try {
									dest = CommonFunctions.getAudioURL()
											+ System.currentTimeMillis()
											+ ".wav";

									Log.e("", "================== dest==="
											+ dest);
									CommonFunctions.sendFailMessageToDrafts(
											TenFourRecordVoice.this, numbers,
											names, dest, file_url);
									handler2.sendEmptyMessage(1);
									CommonFunctions
											.writeLog("BackToTilescontactData Audio Recording Message Sent Failed");
								} catch (IOException e1) {

									e1.printStackTrace();
								}

							}

						}
					}
				}
				if (!group_cont_sel) {
					if (backToTilesGroupData != null) {
						final String numbers = backToTilesGroupData
								.getStringExtra("receiver");
						final String names = backToTilesGroupData
								.getStringExtra("names");
						final String file_url = backToTilesGroupData
								.getStringExtra("msg_url");
						final Boolean messageType = backToTilesGroupData
								.getBooleanExtra("messageType", false);
						final ArrayList<String> grlist = backToTilesGroupData
								.getStringArrayListExtra("grlist");
						final ArrayList<GroupModel> grModel = (ArrayList<GroupModel>) backToTilesGroupData
								.getSerializableExtra("grdata");
						Log.e("",
								"==========================================Group data ================================================");
						Log.e("", "Name of the Gr List is " + grlist.size());
						Log.e("", "Name " + names);
						Log.e("", "Numbers " + numbers);
						Log.e("", "File Url" + file_url);
						Log.e("", "messageType " + messageType);
						Log.e("Size of the Model ArrayList is ",
								"" + grModel.size());
						for (int i = 0; i < grModel.size(); i++) {
							GroupModel gr = grModel.get(i);
							Log.e("", "The Group Name is " + gr.getGroupName());
							Log.e("",
									"The Model Group Members are "
											+ gr.getGroupMember());
						}
						try {
							CommonFunctions
									.writeLog("Length of wav file during Sending of BackToTilesGroupData msg in Audio Recording "
											+ new File(file_url).length());
						} catch (Exception e) {
							CommonFunctions.writeLog(" Audio Recoding Reply "
									+ e.getMessage());
						}
						CommonFunctions
								.writeLog("Audio Recording BackToTilesGroupData "
										+ "Name "
										+ names
										+ "====numbers"
										+ numbers
										+ "=====file_url"
										+ file_url
										+ "====messageType" + messageType);
						Log.e("",
								"=======================================================================================================");
						if (numbers != null && names != null
								&& file_url != null && numbers.length() > 0
								&& names.length() > 0 && file_url.length() > 0) {
							if (CommonFunctions.SendGroupMessage(context,
									messageType, file_url, numbers, names,
									false, null, grlist, grModel)) {
								// if (single_cont_sel && group_cont_sel) {
								eyesayonWayDialog.getHandlerObj()
										.sendEmptyMessage(95);
								// }
								// else {
								// if (eyesayonWayDialog!=null) {
								// eyesayonWayDialog.dismiss();
								// }
								// finish();
								// }
								handler2.sendEmptyMessage(2);
								CommonFunctions
										.writeLog("BackToTilesGroupData Audio Recording Message Sent successfully ");

							} else {
								String dest = "";
								try {
									dest = CommonFunctions.getAudioURL()
											+ System.currentTimeMillis()
											+ ".wav";

									Log.e("", "================== dest==="
											+ dest);
									CommonFunctions.sendFailMessageToDrafts(
											TenFourRecordVoice.this, numbers,
											names, dest, file_url);
									handler2.sendEmptyMessage(2);
								} catch (IOException e1) {

									e1.printStackTrace();
								}
								CommonFunctions
										.writeLog("BackToTilesGroupData Audio Recording Message Sent Failed");
							}

						}
					}
				}

			}

			// / No Internet Connection
			else {
				if (eyesayonWayDialog != null) {
					eyesayonWayDialog.dismiss();
				}
				CommonFunctions
						.showAlert(
								TenFourRecordVoice.this,
								"Please Check your internet connection, You can save your message in drafts by clicking save &  can send later when network avilable ",
								"Network Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions
					.writeLog("Error while sending Audio Recorded Message"
							+ e.getMessage());
		} finally {
			if (eyesayonWayDialog != null) {
				eyesayonWayDialog.dismiss();

			}
			try {
				stopTimer();
			} catch (Exception e2) {
				e2.printStackTrace();
				CommonFunctions
						.writeLog("Error in Audio Recording Activity while sending message "
								+ e2.getMessage());
			}
			setResult(Utils.DRAFTS_MESSAGE_SEND_RESULT);
			finish();

		}

	}

	Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				Toast.makeText(
						getApplicationContext(),
						"Looks like there was a problem sending your EyeSay. Not to worry. It's saved in your drafts so you can send it later!",
						9000).show();
			} else if (msg.what == 2) {
				Toast.makeText(TenFourRecordVoice.this,
						"Message Sent to Group successfully",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 3) {
				Toast.makeText(TenFourRecordVoice.this,
						"Message Sent successfully", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 5) {
				eyesayonWayDialog = new EyesayonWayDialog(
						TenFourRecordVoice.this);
				eyesayonWayDialog.setCancelable(true);
				eyesayonWayDialog.show();
				eyesayonWayDialog.getHandlerObj().sendEmptyMessage(9);
			} else if (msg.what == 6) {
				Toast.makeText(
						getApplicationContext(),
						"Would you like to save your message to drafts so you can try sending later?",
						Toast.LENGTH_LONG).show();
			} else if (msg.what == 15) {
				Toast.makeText(
						getApplicationContext(),
						"No  Member exists in Group, Please add atleast one member & then reply again",
						Toast.LENGTH_LONG).show();
			}

			else {
				Toast.makeText(
						getApplicationContext(),
						"Looks like there was a problem sending your EyeSay to groups. Not to worry. It's saved in your drafts so you can send it later!",
						9000).show();
			}
		};

	};

	private void startTimer() {
		try {

			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					timerCount++;
					if (timerCount >= 45) {
						stopTimer();
					}
				}
			}, 50, 1000);

		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions
					.writeLog("Error in Audio Recording while Starting timer");
		}
	}

	private void stopTimer() {
		try {

			if (timer != null) {
				timer.cancel();
				timer = null;
				if (timerCount >= 45) {
					handler2.sendEmptyMessage(6);
				}
				timerCount = 0;

			}

		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions
					.writeLog("Error in Audio Recording while Stoping timer");
		}

	}

	@Override
	protected void onDestroy() {
		CommonFunctions.writeLog("In the on destroy");
		try {
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.e("", "=============on Stop is Called===============");

		super.onStop();
	}
}
