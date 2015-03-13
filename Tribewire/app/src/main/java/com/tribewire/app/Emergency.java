package com.tribewire.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eyesay.custommenu.CustomMenu;
import com.app.eyesay.custommenu.CustomMenu.OnMenuItemSelectedListener;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.ExtAudioRecorder;
import com.eyesayapp.Utils.GroupModel;
import com.eyesayapp.Utils.OnSwipeTouchListener;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;
import com.eyesayapp.asyn.SignOut;

public class Emergency extends Fragment implements IHttpRequest,
		OnClickListener {

	public EditText edtMessage;
	Button btnSend;

	ImageButton imgOpenMedia;
	String uploadFilePath = "";
	private final int ACTIVITY_SELECT_IMAGE = 211;
	private final int ACTIVITY_CAPTURE_IMAGE = 212;
	private final int ACTIVITY_CAPTURE_AUDIO = 313;

	public static final String IMAGE_DATA = "image_data";
	public static final String IMAGE_URI = "image_uri";
	public static final String VIDEO_DATA = "video_data";
	public static final String AUDIO_DATA = "audio_data";
	Uri imageUri;

	String groupId = "";
	private ImageView imgCapture;
	private RelativeLayout rltAudio;

	// Paramters for menu panel
	LinearLayout slidingPanel;
	LinearLayout main_Layout;
	private int panelWidth;
	boolean isExapnd = false;
	private DisplayMetrics metrics;
	LinearLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	boolean isExpanded = false;
	private GestureDetector gestureDetector;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	View.OnTouchListener gestureListener;
	private Button slide_menu_icon;
	RelativeLayout conAllConversation;
	RelativeLayout conCompanyFYi;
	RelativeLayout con_Contacts;
	RelativeLayout con_Groups;
	RelativeLayout con_Emergency;
	private TextView txtAllConversation;
	private TextView txtUsername;
	LinearLayout conProfileData;
	ImageView imgProfilePic;
	private ProgressBar progressBar;
	private TextView txtAudioCount;
	private ImageView btnPlayPause;
	Button btnBadge;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getActionBar().setTitle("EMERGENCY CONVERSATION".toUpperCase());
        
		View view = inflater.inflate(R.layout.activity_emergency, null);
		btnSend = (Button) view.findViewById(R.id.text_send);
		edtMessage = (EditText) view.findViewById(R.id.msg_editText1);
		edtMessage.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		imgOpenMedia = (ImageButton) view.findViewById(R.id.open_media);
		imgCapture = (ImageView) view.findViewById(R.id.img_capture);
		rltAudio = (RelativeLayout) view.findViewById(R.id.rlt_audio);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBarAudio);
		txtAudioCount = (TextView) view.findViewById(R.id.audio_seconds);
		btnPlayPause = (ImageView) view.findViewById(R.id.img_audio_message);
		imgOpenMedia.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		btnPlayPause.setOnClickListener(this);
		edtMessage.requestFocus();
		initializeRecordingUi(view);
		
		
		getEmergencyInfo();
		return view;
	}



	@Override
	public void onResume() {
		handler.sendEmptyMessage(8);
       new Handler().postAtTime(new Runnable() {
			
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(edtMessage, InputMethodManager.SHOW_IMPLICIT);
				
			}
		}, 1000);
		
       ActivityStatus.setActivity(getActivity());
		super.onResume();
	}

	
	private LinearLayout conRecordingLayout;
	// private TextView txtSecondsLeft;
	private TextView txtSecondsPlayed;
	private ProgressBar progressRecording;
	private Button txtPlay;
	private Button txtRecord;
	private Button txtCancelRecording;
	String recordingPath = "";
	boolean isOngoingRecording = false;
	public int count = 0;
	public int seconds = 60;
	public boolean isPlayingCurrently = false;
	private int audioPlayCount = 0;
	private ExtAudioRecorder recorder;
	volatile boolean running;
	private Thread recordThread;
	private int audioDuration;
	
	private void initializeRecordingUi(View v) {
		conRecordingLayout = (LinearLayout) v.findViewById(R.id.con_audio_recording);

		txtSecondsPlayed = (TextView) v.findViewById(R.id.second_played);
		progressRecording = (ProgressBar) v.findViewById(R.id.progressBarRecord);
		txtPlay = (Button) v.findViewById(R.id.btn_play);

		txtRecord = (Button) v.findViewById(R.id.btn_record);
		txtCancelRecording = (Button) v.findViewById(R.id.txt_cancel);
		txtPlay.setOnClickListener(recordingListener);

		txtRecord.setOnClickListener(recordingListener);
		txtCancelRecording.setOnClickListener(recordingListener);
	}
	
	
	
	OnClickListener recordingListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == txtPlay) {
				if (!isPlayingCurrently && !isOngoingRecording) {
					try {
						isOngoingRecording = false;
						// playAudioFile(CommonFunctions.getBaseURL()
						// + "temporary/" + "android.wav");

						if (recordingPath != null && recordingPath.length() > 0) {
							playAudioFile(recordingPath);
						} else {
							Toast.makeText(getActivity(),
									"Please record some file before play",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {

						e.printStackTrace();
					}
				} else if (isPlayingCurrently) {
					Toast.makeText(getActivity(),
							"File is currently playing ", Toast.LENGTH_SHORT)
							.show();
				} else if (isOngoingRecording) {
					Toast.makeText(
							getActivity(),
							"Before playing please stop recording and then play ",
							Toast.LENGTH_SHORT).show();
				}
			}

			// else if (v == txtStop) {
			// if (isOngoingRecording) {
			// stopRecording();
			// } else if (isPlayingCurrently) {
			// stopPlaying();
			// }
			// }
			else if (v == txtRecord) {
				if (!isOngoingRecording) {
					StartRecording();
				} else {
					// Toast.makeText(ConversationMessages.this,
					// "Recording in progress ", Toast.LENGTH_LONG).show();
					if (isOngoingRecording) {
						stopRecording();
					} else if (isPlayingCurrently) {
						stopPlaying();
					}
				}
			} else if (v == txtCancelRecording) {
				conRecordingLayout.setVisibility(View.GONE);
				if (isOngoingRecording) {
					stopRecording();
				} else if (isPlayingCurrently) {
					stopPlaying();
				}
			}

		}
	};
	private String baseurl;	
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
		txtRecord.setBackgroundResource(R.drawable.record_btn_on);
		// txtPlay.setBackgroundResource(R.drawable.videopause);
		// txtPlay.setTextColor(Color.GRAY);
		// txtStop.setTextColor(Color.GRAY);
		// startRecordingBtn.setEnabled(true);
		// btnCancel.setEnabled(false);
		progressRecording.setProgress(0);
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
					progressRecording
							.setProgress((int) (((count + 1) * 100) / seconds));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Log.e("", "===============Count ++"+(count*1.65));
				if (displaycount - 1 >= 0) {
					if (displaycount - 1 <= 9) {
						// txtSeconds.setText("00:0" + (displaycount - 1));
						txtSecondsPlayed.setText("00:" + (count));
					} else {
						// txtSeconds.setText("00:" + (displaycount - 1));
						if (count > 9) {
							txtSecondsPlayed.setText("00:" + (count));
						} else {
							txtSecondsPlayed.setText("00:0" + (count));
						}
					}
				}
				if (displaycount == 0) {

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
					progressRecording
							.setProgress((int) ((audioPlayCount * 100) / audioDuration));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (displayAudiocount >= 0) {
					if (displayAudiocount <= 9) {
						txtSecondsPlayed.setText("00:0" + displayAudiocount);
					} else {
						txtSecondsPlayed.setText("00:" + displayAudiocount);
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

	private void stopRecording() {
		if (isOngoingRecording) {
			endRecording();
		}
	}

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
			progressRecording.setProgress(100);
			// btnCancel.setEnabled(true);
			// txtRecord.setTextColor(Color.GRAY);

			txtRecord.setBackgroundResource(R.drawable.record_btn_off);
			// txtStop.setTextColor(Color.BLUE);
			// txtPlay.setTextColor(Color.GRAY);
			// txtPlay.setBackgroundResource(R.drawable.videopause);
			uploadFilePath = recordingPath;
			// Intent intent = new Intent(this, EyesayPlayAudio.class);
			// intent.putExtra("messagetype", 0);
			// intent.putExtra("type", 0);
			// intent.putExtra("url", baseurl + "temporary/" + "android.wav");
			//
			// intent.putExtra("receiver", "");
			// startActivityForResult(intent, REQUEST_CODE_USE_RETAKE);
			// CommonFunctions.writeLog("Audio Recording ended successfully ");

		} catch (Exception e) {
			e.printStackTrace();
			// Debugger.debugE(e.getMessage());
			CommonFunctions.writeLog("Error while Stoping Audio Recording "
					+ e.getMessage());
		}

	}

	MediaPlayer mp = null;

	public void playAudioFile(String fileName) {

		File file = new File(fileName);
		if (file.exists()) {

			try {
				// btnCancel.setEnabled(false);
				// txtPlay.setTextColor(Color.BLUE);
				txtPlay.setBackgroundResource(R.drawable.videopause);
				// txtRecord.setTextColor(Color.GRAY);
				txtRecord.setBackgroundResource(R.drawable.record_btn_off);
				// txtStop.setTextColor(Color.GRAY);

				mp = new MediaPlayer();
				mp.setDataSource(fileName);
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mp.setVolume(15, 15);
				mp.prepare();
				mp.start();
				isPlayingCurrently = true;

				audioDuration = mp.getDuration() / 1000;
				audioPlayCount = 0;

				progressRecording.setProgress(0);
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

						// btnCancel.setEnabled(true);
						//
					}
				});
			} catch (Exception e) {
				// Debugger.debugE("error " + e.getMessage());
				mp.release();
				mp = null;
				isPlayingCurrently = false;

				CommonFunctions
						.writeLog("Error while Playing Recorded Audio File "
								+ e.getMessage());
			}

		} else {
			// txtRecording.setText("File Does Not Exist");
			Toast.makeText(getActivity(), "Audio File Does Not Exist", 5000)
					.show();
			// Alert dialog if file dose not exit

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			// set title
			alertDialogBuilder.setTitle("Tribewire");

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

	private void stopPlaying() {

		try {
			if (mp != null && mp.isPlaying()) {
				mp.stop();
				mp.release();

				mp = null;
				// txtRecord.setTextColor(Color.GRAY);
				txtRecord.setBackgroundResource(R.drawable.record_btn_off);
				// txtStop.setTextColor(Color.BLUE);
				// txtPlay.setTextColor(Color.GRAY);
				isPlayingCurrently = false;
				// txtRecording.setText("Stopped...");
				// playRecordingBtn
				// .setBackgroundResource(R.drawable.button_play_inactive);
				// btnCancel.setEnabled(true);
				progressRecording.setProgress(0);
				Log.e("", "Playing Stopped");
			}
		} catch (Exception e) {
			CommonFunctions
					.writeLog("Error while Stop Playing the Audio Recording 180 "
							+ e.getMessage());
		}
	}

	private void setDefaultImage() {
		try {
			String path = CommonFunctions.getPref(getActivity(),
					AddEditImage.LOCAL_PROFILE_PIC, "");
			if (path != null && path.length() > 0) {
				File image = new File(path);
				if (image.exists()) {

					imgProfilePic.setImageBitmap(BitmapFactory.decodeFile(image
							.getAbsolutePath()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	ProgressDialog pd;
	int length = 0;

	@Override
	public void onClick(View v) {
		if (v == btnSend) {
			try {
				if (conRecordingLayout.getVisibility() == View.VISIBLE) {
					conRecordingLayout
							.setVisibility(View.GONE);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (edtMessage.getText().toString().length() > 0
					|| uploadFilePath.length() > 0) {
				if (groupId != null && groupId.length() > 0) {

					Thread thread = new Thread(runnable);
					thread.start();

				} else {
					Toast.makeText(
							getActivity(),
							"Unable to fetch group information, Please try again later",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getActivity(),
						"Please type message or select any media",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == imgOpenMedia) {
			showMenu();
		} else if (v == slide_menu_icon) {
			Log.e("", "======== If expanded " + isExpanded);
			if (!isExpanded) {
				isExpanded = true;

				showMenuAnother();
			} else {
				isExpanded = false;

				hideMenuAnother();

			}

		} else if (v == btnPlayPause) {

			if (playflag == false) {
				Log.e("", "==Starting of new media");
				handler3.sendEmptyMessage(3);
				playMyAudioFile(uploadFilePath, false);

			} else if (playflag == true && pauseFlag == false) {
				Log.e("", "=================== Media paused");
				try {
					if (mp != null && mp.isPlaying()) {
						mp.pause();
						threadPP.pauseThread();
						length = mp.getCurrentPosition();
						pauseFlag = true;
						handler3.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (pauseFlag == true) {
				Log.e("",
						"=================== Resuming the Media Player if else case ");
				pauseFlag = false;
				playflag = true;
				handler3.sendEmptyMessage(2);

				mp.seekTo(length);
				mp.start();

				threadPP = new ThreadPP();

				// threadPP.resumeThread();

			}

			// if (mp != null && mp.isPlaying() && playflag == true) {
			// Log.e("",
			// "=================== Pausing the Media Player if case ");
			// mp.pause();
			// threadPP.pauseThread();
			// length = mp.getCurrentPosition();
			// pauseFlag = true;
			// handler3.sendEmptyMessage(1);
			//
			// playflag = false;
			//
			// } else {
			// Log.e("", "=================== Main else case ================");
			// if (playflag == false) {
			// if (!mediaCompleted) {
			// Log.e("",
			// "=================== Starting the  Media Player if case ");
			// handler3.sendEmptyMessage(3);
			// mediaFirstTime = false;
			// // mp.start();
			// playMyAudioFile(uploadFilePath, false);
			//
			// } else {
			// Log.e("",
			// "=================== Resuming the Media Player if else case ");
			// handler3.sendEmptyMessage(2);
			//
			// mp.seekTo(length);
			// mp.start();
			// threadPP.resumeThread();
			// }
			// playflag = true;
			// pauseFlag = false;
			// }
			// }

		}

	}

	private void showMenu() {
		try {
			HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
			hashMap.put(0, "Take Photo");
			hashMap.put(1, "Use Existing Photo");
			hashMap.put(2, "Record Voice Message");
			hashMap.put(3, "Cancel");
			CustomMenu.show(getActivity(), hashMap,
					new OnMenuItemSelectedListener() {

						@Override
						public void MenuItemSelectedEvent(Integer selection) {
							if (selection == 0) {

								takePhoto();
							} else if (selection == 1) {
								chooseExisting();
							} else if (selection == 2) {
								addVoice();
							} else if (selection == 3) {
								CustomMenu.hide();
							}

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showImageContainer() {
		try {
			if (imgCapture.getVisibility() == View.GONE) {
				imgCapture.setVisibility(View.VISIBLE);
			}
			if (rltAudio.getVisibility() == View.VISIBLE) {
				rltAudio.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void showAudioContainer() {
		try {
			if (imgCapture.getVisibility() == View.VISIBLE) {
				imgCapture.setVisibility(View.GONE);
			}
			if (rltAudio.getVisibility() == View.GONE) {
				rltAudio.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	// Used to send emergency message through Emergency Web Service
	private void getEmergencyInfo() {

		List<String> nameList = null;

		nameList = new ArrayList<String>();
		
		nameList.add(Constants.API_KEY_TAG);
		nameList.add(Constants.REF_ID_TAG);
		nameList.add(Constants.COMP_ID_TAG_NEW);
		nameList.add(Constants.SESSION_TAG);

		if (CommonFunctions.isInternetConnected(getActivity())) {
			pd = ProgressDialog.show(getActivity(), "Group Info",
					"Please wait while fetching group information");
			pd.setCancelable(true);
			// new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
			// + TribeWireConstants.EMERGENCY_URL, nameList,
			// Constants.getValueList(this)).execute();
			new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
					+ TribeWireConstants.EMERGENCY_URL, nameList,
					Constants.getValueList(getActivity())).start();
		} else {
			CommonFunctions
					.showAlert(
							getActivity(),
							"Cannot fetch group information , Please check internet connectivity",
							"Network error");
		}

	}

	@Override
	public void onError(String onError) {
		try {
			if (pd != null) {
				pd.dismiss();
				pd = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {"error":false, "error_msg":"",
	 * "emergency_group":[{"group_id":"NjQ=","group_name":"RW1lcmdlbmN5"}]}
	 */
	@Override
	public void onResponse(String response) {

		try {
			Log.e("", "==== Emergency Response from the server is " + response);

			try {
				if (pd != null) {
					pd.dismiss();
					pd = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (response != null && response.length() > 0) {
				try {
					JSONObject jsonObject1 = new JSONObject(response);
					JSONObject jsonObject = jsonObject1
							.getJSONObject("response");
					String error = jsonObject.getString("error");
					if (error.equalsIgnoreCase("false")) {
						JSONArray jsonArray = jsonObject
								.getJSONArray("emergency_group");
						if (jsonArray != null && jsonArray.length() > 0) {
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject groupObj = jsonArray
										.getJSONObject(i);
								groupId = groupObj.getString("group_id");
								String group_name = groupObj
										.getString("group_name");
								Log.e("", "===========Group Id " + groupId);
								Log.e("", "===========Group Name " + group_name);
							}
						}
					} else {
						Toast.makeText(
								getActivity(),
								"Unable to get group info, Please try again later",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {

				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	Uri selectedImage = null;
	Bitmap profile_bm = null;

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTIVITY_CAPTURE_IMAGE
				&& resultCode == Activity.RESULT_OK) {
			if (resultCode == Activity.RESULT_OK) {

				Uri selectedImage = imageUri;
				try {

//					getActivity().getContentResolver().notifyChange(
//							selectedImage, null);
					Log.e("",
							"-------------------------------------------------------------"
									+ selectedImage);

					uploadFilePath = selectedImage.toString();

					if (uploadFilePath.startsWith("file://")) {
						uploadFilePath = uploadFilePath.replace("file://", "");
						Log.e("",
								"After-------------------------------------------------------------"
										+ uploadFilePath);
						Bitmap bitmap = getBitmap(selectedImage);
						profile_bm = TribeWireUtil.rotateBitmapZero(
								selectedImage.toString(), bitmap);

					}
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						boolean isImageWritten = profile_bm.compress(
								Bitmap.CompressFormat.JPEG, 100, bos);
						if (isImageWritten) {

						}

						byte bitmapImage[] = bos.toByteArray();
						File file1 = getTempFile(getActivity());
						FileOutputStream fos = new FileOutputStream(file1);
						uploadFilePath = file1.toString();
						Log.e("", "The Compressed bitmap File path is "
								+ uploadFilePath);
						fos.write(bitmapImage);
						fos.close();

						// handler.sendEmptyMessage(2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		}

		else if (requestCode == ACTIVITY_SELECT_IMAGE
				&& resultCode == Activity.RESULT_OK) {
			if (resultCode == Activity.RESULT_OK) {

				try {
					selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getActivity().getContentResolver().query(
							selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					Log.e("", "File Path is " + filePath);
					uploadFilePath = filePath;
					cursor.close();
					profile_bm = getBitmap(selectedImage);
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						boolean isImageWritten = profile_bm.compress(
								Bitmap.CompressFormat.JPEG, 100, bos);
						if (isImageWritten) {

						}

						byte bitmapImage[] = bos.toByteArray();
						File file1 = getTempFile(getActivity());
						FileOutputStream fos = new FileOutputStream(file1);
						uploadFilePath = file1.toString();
						Log.e("", "The Compressed bitmap File path is "
								+ uploadFilePath);
						fos.write(bitmapImage);
						fos.close();

						handler.sendEmptyMessage(2);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

		else if (requestCode == ACTIVITY_CAPTURE_AUDIO
				&& resultCode == Activity.RESULT_OK) {

			if (resultCode == Activity.RESULT_OK) {

				try {

					uploadFilePath = data.getStringExtra(Emergency.AUDIO_DATA);
					Log.e("",
							"-------------------------------------------------------------"
									+ uploadFilePath + " ");

					handler.sendEmptyMessage(3);

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// Records Voice Using Eyesay Voice UI
	private void addVoice() {
		try {
			// Intent intent = new Intent(this, TenFourRecordVoice.class);
			// startActivityForResult(intent, ACTIVITY_CAPTURE_AUDIO);
			conRecordingLayout.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// Takes Photo Using Default Camera
	private void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		File photo = getTempFile(getActivity());
		photo.delete();
		photo = getTempFile(getActivity());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		imageUri = Uri.fromFile(photo);
		startActivityForResult(intent, ACTIVITY_CAPTURE_IMAGE);

	}

	// choose Existing Image and Video from Library
	private void chooseExisting() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
	}

	public static File getTempFile(Context context) {

		File path1 = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path1.exists()) {
			path1.mkdir();
			Log.e("", "Directory Created Again");
		}

		return new File(path1, System.currentTimeMillis() + "eyesay.jpeg");
	}

	private String TAG = this.getClass().getSimpleName();

	private Bitmap getBitmap(Uri uri) {

		// Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 700000; // 1.2MP
			in = getActivity().getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth
					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in = getActivity().getContentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Log.d(TAG, "1th scale operation dimenions - width: " + width
						+ ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}

			in.close();
			Log.e("", "");
			return b;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					if (profile_bm != null) {
						showImageContainer();
						imgCapture.setImageBitmap(profile_bm);
					}
//					if (imageUri != null) {
//						showImageContainer();
//						imgCapture.setImageURI(imageUri);
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 2) {
				try {
					if (profile_bm != null) {
						showImageContainer();
						imgCapture.setImageBitmap(profile_bm);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == 3) {
				showAudioContainer();
			} else if (msg.what == 4) {
				showAudioContainer();
			} else if (msg.what == 8) {

				try {
					String path = CommonFunctions.getPref(getActivity(),
							AddEditImage.LOCAL_PROFILE_PIC, "");
					if (path != null && path.length() > 0) {
						File image = new File(path);
						if (image.exists()) {

							// imgProfilePic.setImageBitmap(BitmapFactory
							// .decodeFile(image.getAbsolutePath()));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			Looper.prepare();

			sendMessageToGroup();
			Looper.loop();

		}
	};

	public ProgressDialog eyesayonWayDialog;

	private void sendMessageToGroup() {
		try {
			if (CommonFunctions.isInternetConnected(getActivity())) {
				handler2.sendEmptyMessage(5);
			} else {
				CommonFunctions.showAlert(getActivity(),
						"Please check your internet connectivity", "Error");
				return;
			}
			String numbers = "";
			final String names = "Emergency";
			final String file_url = uploadFilePath;
			final Boolean messageType = true;
			final ArrayList<String> grlist = new ArrayList<String>();
			grlist.add("Emergency");
			final ArrayList<GroupModel> grModel = new ArrayList<GroupModel>();
			GroupModel groupModel = new GroupModel();
			groupModel.setGroupIds("" + groupId);
			groupModel.setGroupName("Emergency");
			groupModel.setGroupMember("9999999999");
			groupModel.setGroupMemberNames("Emergency");
			grModel.add(groupModel);
			Log.e("",
					"==========================================Group data ================================================");
			Log.e("", "Name " + names);
			Log.e("", "Numbers " + numbers);
			Log.e("", "File Url" + uploadFilePath);
			Log.e("", "messageType " + messageType);
			Log.e("", "Model ArrayList size is " + grModel.size());
			Log.e("",
					"=======================================================================================================");

			for (int i = 0; i < grModel.size(); i++) {
				numbers = "-g_"
						+ new String(Base64.decode(grModel.get(i).getGroupIds()
								.getBytes(), Base64.DEFAULT)) + "-,";
			}
			if (numbers.endsWith(",")) {
				numbers = numbers.substring(0, numbers.length() - 1);
			}
			Log.e("", "=========================== Finally The numbers are "
					+ numbers + " ==============");
			if (numbers != null && names != null && numbers.length() > 0
					&& names.length() > 0) {

				if (CommonFunctions.SendTextGroupMessage(getActivity(),
						messageType, uploadFilePath, numbers, names, false,
						null, grlist, grModel, edtMessage.getText().toString())) {
					// if (single_cont_sel && group_cont_sel) {
					
					// }
					// else {
					// if (eyesayonWayDialog!=null) {
					// eyesayonWayDialog.dismiss();
					// }
					// finish();
					// }
					handler2.sendEmptyMessage(2);

				} else {
					String dest = " ";
					try {
						CommonFunctions.getBaseURL();
						dest = CommonFunctions.getImageRL()
								+ System.currentTimeMillis() + ".jpeg";

						Log.e("", "================== dest===" + dest);
						for (int i = 0; i < grlist.size(); i++) {

							String groupName = grlist.get(i);
							String groupMembers = grModel.get(i)
									.getGroupMember().trim();
							String groupMemberNames = grModel.get(i)
									.getGroupMemberNames().trim();
							groupMembers = groupMembers.replace("-", "");
							try {
								groupMembers = CommonFunctions
										.sortStringArray(groupMembers);
							} catch (Exception e) {
								e.printStackTrace();
							}
							Log.e("",
									"===================== The group Members are "
											+ groupMembers);
							Log.e("",
									"===================== The group Members Names are "
											+ groupMemberNames);
							// sendFailMessageToDrafts(
							// EyesaySendText.this, groupMembers,
							// groupMemberNames, dest, uploadFilePath,true);
						}
						handler2.sendEmptyMessage(1);
					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (eyesayonWayDialog != null) {
				eyesayonWayDialog.dismiss();

			}
			// finish();
               try {
				if (getActivity() instanceof MenuActivity) {
					MenuActivity activity   = (MenuActivity)getActivity();
					activity.displayView(1);
							
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	public Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				Toast.makeText(
						getActivity(),
						"Looks like there was a problem sending your EyeSay. Not to worry. It's saved in your drafts so you can send it later!",
						9000).show();
			} else if (msg.what == 2) {
				Toast.makeText(getActivity(),
						"Message Sent  successfully",
						Toast.LENGTH_SHORT).show();
				
				android.app.FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, new TenFourConversations()).commit();
				
			} else if (msg.what == 3) {
				Toast.makeText(getActivity(), "Message Sent successfully",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 5) {
				eyesayonWayDialog = ProgressDialog.show(getActivity(), "Emergency message", "Please wait while sending your message");
				eyesayonWayDialog.setCancelable(true);
				eyesayonWayDialog.show();
				
			} else if (msg.what == 8) {
				try {
					Toast.makeText(
							getActivity(),
							"Would you like to save your message to drafts so you can try sending later?",
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == 10) {
				try {
					Toast.makeText(
							getActivity(),
							"Please enter some text or select media to send eyesay ",
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}  
			} else if (msg.what == 15) {
				Toast.makeText(
						getActivity(),
						"No  Member exists in Group, Please add at least one member & then reply again",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						getActivity(),
						"Looks like there was a problem sending your EyeSay to groups. Not to worry. It's saved in your drafts so you can send it later!",
						9000).show();
			}
		};

	};

	private void setFontforMenu() {
		// try {
		// Typeface tf = TribeWireUtil.getArialTypeface(this);
		// TextView txtAllCon = (TextView)
		// findViewById(R.id.txt_all_conversation);
		// TextView txtCompanyFyi = (TextView)
		// findViewById(R.id.txt_company_fyi);
		// TextView txtContact = (TextView)
		// findViewById(R.id.txt_contact_groups);
		// TextView txtEmergency = (TextView) findViewById(R.id.txt_emergency);
		// TextView allConversation = (TextView)
		// findViewById(R.id.txt_static_allconv);
		// TextView txtSignout = (TextView) findViewById(R.id.txt_sign_out);
		// txtSignout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // Toast.makeText(Emergency.this, "======== Toast Clicked",
		// // Toast.LENGTH_SHORT).show();
		// signOut();
		// }
		// });
		// TextView txtStatus = (TextView) findViewById(R.id.txt_status);
		// TextView txtOnline = (TextView) findViewById(R.id.txt_status_online);
		// txtSignout.setTypeface(tf);
		// txtStatus.setTypeface(tf);
		// txtOnline.setTypeface(tf);
		// txtAllCon.setTypeface(tf);
		// txtCompanyFyi.setTypeface(tf);
		// txtContact.setTypeface(tf);
		// txtEmergency.setTypeface(tf);
		// allConversation.setTypeface(TribeWireUtil
		// .getArialBoldTypeface(this));
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

	}

	private void setuserName() {
		try {
			String fName = CommonFunctions.getPref(getActivity(),
					Constants.FIRST_NAME, "");
			String lName = CommonFunctions.getPref(getActivity(),
					Constants.LAST_NAME, "");
			txtUsername.setText(fName + " " + lName);
			txtUsername.setTypeface(TribeWireUtil
					.getArialBoldTypeface(getActivity()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setMenuPanel() {
		// slidingPanel = (LinearLayout) findViewById(R.id.slidingpanel);
		// slidingPanel.setVisibility(View.INVISIBLE);
		// main_Layout = (LinearLayout) findViewById(R.id.mainLayout);
		// slide_menu_icon = (Button) findViewById(R.id.btn_menu);
		// conAllConversation = (RelativeLayout)
		// findViewById(R.id.con_allconversation);
		// conCompanyFYi = (RelativeLayout) findViewById(R.id.con_company_fyi);
		// con_Contacts = (RelativeLayout) findViewById(R.id.con_contacts);
		// con_Groups = (RelativeLayout) findViewById(R.id.con_groups);
		// con_Emergency = (RelativeLayout) findViewById(R.id.con_emergency);
		// txtAllConversation = (TextView)
		// findViewById(R.id.txt_all_conversation);
		// txtUsername = (TextView) findViewById(R.id.txt_username);
		// slide_menu_icon.setOnClickListener(this);
		// conAllConversation.setOnTouchListener(onSwipeAllConversation);
		// conCompanyFYi.setOnTouchListener(onSwipeCompanyFYi);
		// con_Contacts.setOnTouchListener(onSwipeContact);
		//
		// con_Emergency.setOnTouchListener(onSwipeEmergency);
		initiateMenuPanel();
	}

	OnSwipeTouchListener onSwipeAllConversation = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			// finish();
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(),
							TenFourConversations.class);
					startActivity(intent);
					// finish();
				}
			}, 650);

			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}
	};

	OnSwipeTouchListener onSwipeCompanyFYi = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(), CompanyFYI.class);
					startActivity(intent);

				}
			}, 650);

			// finish();
			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}

	};

	OnSwipeTouchListener onSwipeContact = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(),
							FrameActivity.class);
					startActivity(intent);
					// finish();
				}
			}, 650);

			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}

	};

	OnSwipeTouchListener onSwipeProfile = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(),
							AddEditImage.class);
					startActivity(intent);

				}
			}, 650);

			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}
	};

	OnSwipeTouchListener onSwipeEmergency = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}

			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}
	};

	private void initiateMenuPanel() {
		try {
			metrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);

			Log.e("", "Height is ========== " + metrics.widthPixels);

			panelWidth = (int) ((metrics.widthPixels) * 0.83);
			int slidingpanelMargin = (int) ((metrics.widthPixels) * 0.17);
			// Here Main_Layout refer to as Sliding panel
			slidingPanelParameters = (FrameLayout.LayoutParams) main_Layout
					.getLayoutParams();
			slidingPanelParameters.width = metrics.widthPixels;
			main_Layout.setLayoutParams(slidingPanelParameters);

			menuPanelParameters = (LinearLayout.LayoutParams) slidingPanel
					.getLayoutParams();
			menuPanelParameters.width = metrics.widthPixels;
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) slidingPanel
					.getLayoutParams();
			params.rightMargin = slidingpanelMargin;
			Log.e("", "=========== The Panel width is=================== "
					+ panelWidth);
			slidingPanel.setLayoutParams(params);
			slidingPanel.requestLayout();

			gestureDetector = new GestureDetector(new MyGestureDetector());
			gestureListener = new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (gestureDetector.onTouchEvent(event)) {
						return true;
					}
					return false;
				}
			};
			slidingPanel.setOnTouchListener(gestureListener);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.e("", "===Swipe Right to left ");
					if (isExpanded) {
						hideMenuAnother();
						isExpanded = false;
					}

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (isExpanded) {
						hideMenuAnother();
						isExpanded = false;
					}

					Log.e("", "===Swipe Left to right ");

				}
			} catch (Exception e) {

			}
			return false;
		}
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// if (gestureDetector.onTouchEvent(event))
	// return true;
	// else
	// return false;
	// }

	private void showMenuAnother() {
		slidingPanel.setVisibility(View.VISIBLE);

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_right_in);

		LayoutParams params = (LayoutParams) main_Layout.getLayoutParams();
		// params.rightMargin = panelWidth;
		// params.gravity = Gravity.RIGHT;

		params.leftMargin = panelWidth;
		params.gravity = Gravity.LEFT;

		// main_Layout.clearAnimation();
		main_Layout.setLayoutParams(params);
		main_Layout.requestLayout();
		main_Layout.setAnimation(animation);
		isExapnd = true;

	}

	private void hideMenuAnother() {

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_left_in);

		LayoutParams params = (LayoutParams) main_Layout.getLayoutParams();
		params.leftMargin = 0;
		// params.leftMargin = 0;

		// main_Layout.clearAnimation();
		main_Layout.setLayoutParams(params);
		main_Layout.requestLayout();
		main_Layout.setAnimation(animation);
		isExapnd = false;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				slidingPanel.setVisibility(View.INVISIBLE);

			}
		}, 650);
	}

	boolean downlodflag = true;
	boolean playflag = false;
	boolean pauseFlag = false;

	private boolean mediaCompleted = false;
	private boolean mediaFirstTime = true;
	
	int move = 0;
	int temp = 0;
	int sec = 0;

	final Handler handler5 = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (move < 10) {
				txtAudioCount.setText("00:0" + move);
			}

			else {
				txtAudioCount.setText("00:" + move);
			}
			temp++;
			progressBar.setProgress((int) (((temp) * 100) / sec));

		};
	};

	File f = null;
	ThreadPP threadPP;

	private void playMyAudioFile(String fileName, final boolean autodownlod) {

		Log.e("", "========== The File name is " + fileName);
		try {

			mp = new MediaPlayer();
			try {
				mp.setDataSource(fileName);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setVolume(15, 15);
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.start();
			playflag = true;
			sec = mp.getDuration() / 1000;
			if (sec > 60)
				sec = 60;
			// CommonFunctions.setFont(EyesayPlayAudio.this, txt_audio_seconds,
			// false, false);
			// txt_audio_seconds.setText("Duration: 00:"
			// + ((sec <= 9) ? "0" + sec : sec));

			move = sec;
			Log.e("", "===================" + move);
			// new Handler().post(runnable);

			// Thread t = new Thread() {
			// public void run() {
			// while (move > 0) {
			// move--;
			// // handler.post(new Runnable() {
			// // public void run() {
			// // if (move >= 0) {
			// // ((TextView) findViewById(R.id.audio_seconds))
			// // .setText("Duration: 00:"
			// // + ((move <= 9) ? "0" + move
			// // : move));
			// //
			// // }
			// // }
			// // });
			// handler5.sendEmptyMessage(1);
			// try {
			// sleep(1000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			//
			// }
			// }
			// };
			// t.start();

			threadPP = new ThreadPP();

			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Debugger.debugE("on completion method..");
					move = 0;
					temp = 0;
					if (mp != null) {
						mp.stop();
						mp.release();
					}

					playflag = false;
					mediaCompleted = true;
					mp = null;
					Emergency.this.mp = null;
					// handler3.sendEmptyMessage(1);
					handler3.sendEmptyMessage(4);
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
					// finish();
				}

			});

		} catch (Exception e) {
			CommonFunctions.showAlert(getActivity(),
					"Error whle Playing Media", "Error");

			Debugger.debugE("error " + e.getMessage());

			mp.release();
			mp = null;

			playflag = false;

		}

	}

	Handler handler3 = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				btnPlayPause.setBackgroundResource(R.drawable.videoplay);
			} else if (msg.what == 2) {
				btnPlayPause.setBackgroundResource(R.drawable.videopause);
			} else if (msg.what == 3) {
				btnPlayPause.setBackgroundResource(R.drawable.videopause);
			} else if (msg.what == 4) {
				btnPlayPause.setBackgroundResource(R.drawable.videoplay);
				progressBar.setProgress(0);
			}
		};
	};

	boolean isRunning = true;

	class ThreadPP implements Runnable {

		public ThreadPP() {
			Thread thread = new Thread(this);
			thread.start();

			isRunning = true;
			;
		}

		public synchronized void pauseThread() {
			isRunning = false;
			try {
				// this.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public synchronized void resumeThread() {
			isRunning = true;
			try {
				// this.notify();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {

			Log.e("", "=========== The move Count is =============" + move);
			while (move > 0 && isRunning) {
				move--;
				Log.e("", "=========== The move Count is " + move);
				// handler.post(new Runnable() {
				// public void run() {
				// if (move >= 0) {
				// ((TextView) findViewById(R.id.audio_seconds))
				// .setText("Duration: 00:"
				// + ((move <= 9) ? "0" + move
				// : move));
				//
				// }
				// }
				// });
				handler5.sendEmptyMessage(1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

	@Override
	public void onPause() {
		try {
			if (mp != null) {
				mp.stop();
				mp.release();
				move = 0;
				temp = 0;
				playflag = false;
				if (threadPP != null) {
					threadPP.pauseThread();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ActivityStatus.setActivity(null);
		super.onPause();
	}

	private void signOut() {
		progressDialog = ProgressDialog.show(getActivity(), "Logging out",
				"Please wait while sign out from eyesay ......");
		progressDialog.setCancelable(true);

		new SignOut(getActivity()).start();

	}

	public IHttpRequest httpRequest = new IHttpRequest() {

		@Override
		public void onResponse(String response) {

			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			CommonFunctions.CancelAlarm(getActivity());
			CommonFunctions.setPref(getActivity(), CommonFunctions.IS_LOGIN,
					false);
			CommonFunctions.setPref(getActivity(), Constants.SESSION, "");
			CommonFunctions.setPref(getActivity(), CommonFunctions.REF_ID, "");
			try {
				DataBase db = new DataBase(getActivity());
				db.open();
				db.clean();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// finish();

		}

		@Override
		public void onError(String onError) {

			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

			Toast.makeText(
					getActivity(),
					"Some thing went wrong while logout , Please try after some time",
					Toast.LENGTH_LONG).show();

		}
	};
	ProgressDialog progressDialog;

}
