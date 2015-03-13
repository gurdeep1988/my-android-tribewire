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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eyesay.custommenu.CustomMenu;
import com.app.eyesay.custommenu.CustomMenu.OnMenuItemSelectedListener;
import com.app.eyesay.dialog.EyesayonWayDialog;
import com.app.eyesay.widget.PullToRefreshListView;
import com.app.eyesay.widget.PullToRefreshListView.OnRefreshListener;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.ISentChatMessage;
import com.eyesayapp.asyn.RestApiCall;
import com.eyesayapp.model.FriendModel;
import com.eyesayapp.model.SectionHeader;
import com.eyesayapp.service.TribMessageCollectorService;

import eyesay.adapter.AddParticipantAdp;
import eyesay.adapter.ConversationAdapterNew;
import eyesay.adapter.SimpleAdapter;

public class ConversationMessages extends Activity implements ISentChatMessage,
		OnItemClickListener, OnClickListener, IHttpRequest {

	public static int CONV_SCREEN_STATUS= -1;
	int requestMessageCount = 0;
	ProgressDialog progressDialog = null;
	public String THREAD = "";
	ProgressBar progressBar;
	int totalMessageCount = 0;
	int receiveMessageCount = 0;
	int leftMessageCount = 0;
	private final int ACTIVITY_SELECT_IMAGE = 211;
	private final int ACTIVITY_CAPTURE_IMAGE = 212;
	
	
	

	private final int ACTIVITY_CAPTURE_AUDIO = 313;

	public static final String IMAGE_DATA = "image_data";
	public static final String IMAGE_URI = "image_uri";
	Uri imageUri;

	public boolean isNewConversation = false;
	private TextView txtCurrentConv;
	LinearLayout conConversationListeners;
	ArrayList<FriendModel> conversationMessages;
	boolean isFyiReply = false;
	RelativeLayout scrollView;
	private Button btnAddParticipants;
	public static boolean isChatActivityVisible = false;
	RelativeLayout rltImageAttachView;
	ImageView imgAttachPic;
	private ListView addParticipantList;
    private String fyiSubject;
	private RelativeLayout headerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        CONV_SCREEN_STATUS =-1;
		setContentView(R.layout.messagelist);
		addParticipantList = (ListView) findViewById(R.id.list_add_participant);
		headerLayout  = (RelativeLayout)findViewById(R.id.message_list_header_ll);
		addParticipantList.setFastScrollEnabled(true);
		addParticipantList.setBackgroundColor(Color.WHITE);
		addParticipantList.setOnItemClickListener(addParticipantListClick);
		View v = LayoutInflater.from(this).inflate(
				R.layout.addparticpant_header, null);
		addParticipantList.addHeaderView(v);
		try {
			txtCurrentConv = (TextView) findViewById(R.id.txt_Current_conversation);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			conversationMessages = (ArrayList<FriendModel>) getIntent()
					.getSerializableExtra("conListener");
		} catch (Exception e) {
			e.printStackTrace();
		}
		conConversationListeners = (LinearLayout) findViewById(R.id.con_listener);
		scrollView = (RelativeLayout) findViewById(R.id.scrollview);
		btnAddParticipants = (Button) findViewById(R.id.btnAddParticipaths);
		refreshList = (PullToRefreshListView) findViewById(R.id.lst_pull_to_refresh);
		rltImageAttachView = (RelativeLayout) findViewById(R.id.rlt_image_attach_view);
		imgAttachPic = (ImageView) findViewById(R.id.img_selected_image);
		btnAddParticipants.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int requestCode = 10;
				
//				showParticipantList();
//				participants = new ArrayList<String>();
//				participantNames = new ArrayList<String>();
//				positionList = new ArrayList<Integer>();
//				CreateProgressBar();
//				FillFriends();

			}
		});
		refreshList.setOnRefreshListener(onRefreshListener);
		registerForContextMenu(refreshList);
		Button messageListBack = (Button) findViewById(R.id.messagelist_back);
		initializeRecordingUi();
		messageListBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (addParticipantList.getVisibility() == View.VISIBLE) {
					//txtCurrentConv.setText("CURRENT CONVERSATION");
					//showParticipantList();
					addParticipantsToServer();

				} else {
					try {
					//	disconnectService();
					} catch (Exception e) {
					}
					if (isNewConversation) {
						setResult(RESULT_OK);
						isNewConversation = false;
					}
					updateReadValue();
					finish();
				}

			}
		});
		// Button deleteAll = (Button)
		// findViewById(R.id.message_list_btn_delete_all);
		btn_current_conv = (Button) findViewById(R.id.btn_current_conv);
		btn_current_conv.setOnClickListener(this);
		msg_send_txt = (EditText) findViewById(R.id.msd_edit_txt);
		msg_send_txt.setText(edt_message_saved);
		msg_send = (Button) findViewById(R.id.msg_send);
		add_Media = (ImageView) findViewById(R.id.eyesayimage);
		progressBar = (ProgressBar) findViewById(R.id.msg_progress_bar);
		// txtLoadMoreConversation =
		// (TextView)findViewById(R.id.txtLoadMoreConversation);
		// txtLoadMoreConversation.setOnClickListener(loadMoreOnClickListener);
		add_Media.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edt_message_saved = msg_send_txt.getText().toString();
				showMenu();
				// Intent intent = new Intent(ConversationMessages.this,
				// EyesayAddMedia.class);
				// startActivityForResult(intent,EyesaySendText.EYESAY_ADD_MEDIA_CODE);

			}
		});

		super.onCreate(savedInstanceState);

		try {
			THREAD = getIntent().getStringExtra("conid");
			Log.e("", "========== The Conversatio Id is " + THREAD);
			myTempConversationId = THREAD;
			isFyiReply = getIntent().getBooleanExtra("fyireply", false);
			if (isFyiReply) {
				txtCurrentConv.setText("FYIs");
				msg_send.setText("Reply");
				ConversationAdapterNew.setFyiReply(true);
			    fyiSubject =getIntent().getStringExtra("subject");
			    ConversationAdapterNew.setFyiSubject(fyiSubject);
			    headerLayout.setBackgroundColor(getResources().getColor(R.color.blue));
			}
			else {
				ConversationAdapterNew.setFyiReply(false);
				ConversationAdapterNew.setFyiSubject(null);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			requestMessageCount = 10;
			if (CommonFunctions.isInternetConnected(this)) {
				// new TestAsync(true).execute();

				new TestAsync(true).start();
			} else {
				CommonFunctions.showAlert(this,
						"Please check your internet connectivity", "Warning");

				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		recordingPath = "";

		updateReadValue();
		GcmIntentService.convIDNOtify  = null;
	}

	private LinearLayout conRecordingLayout;
	// private TextView txtSecondsLeft;
	private TextView txtSecondsPlayed;
	private ProgressBar progressRecording;
	private Button txtPlay;
	private Button txtRecord;
	private Button txtCancelRecording;

	OnItemClickListener addParticipantListClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int arg2,
				long id) {
			try {
				arg2   = arg2 -1;
				Log.e("","=========== The Selected Position is "+arg2);
				Log.e("", "===== The Friend Id " + Friendid[arg2]
						+ " ====   Status " + status[arg2]);

				try {
					Log.e("", "============= The Positin List  ==== "
							+ positionList.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (addParticipantAdp != null) {
					if (isSelected[arg2] == true) {
						isSelected[arg2] = false;
						if (status[arg2] == 5 || status[arg2] == 6) {
							if (FriendNumber[arg2] != null) {
								String friendId = "-g_" + FriendNumber[arg2]
										+ "-";
								String participantName = FriendName[arg2];
								participants.remove(friendId);
								participantNames.remove(participantName);
								positionList.remove(Integer.valueOf(arg2));
								removeAvatar(arg2);
							}
						} else {
							if (FriendNumber[arg2] != null) {
								String friendId = "-" + FriendNumber[arg2]
										+ "-";
								participants.remove(friendId);
								String participantName = FriendName[arg2];
								participantNames.remove(participantName);
								positionList.remove(Integer.valueOf(arg2));
								removeAvatar(arg2);
							}
						}
					} else if (isSelected[arg2] == false) {
						isSelected[arg2] = true;
						if (status[arg2] == 5 || status[arg2] == 6) {
							if (FriendNumber[arg2] != null) {
								String friendId = "-g_" + FriendNumber[arg2]
										+ "-";
								participants.add(friendId);
								String participantName = FriendName[arg2];
								participantNames.add(participantName);
								positionList.add(arg2);
								addAvatar(arg2);
							}
						} else {
							if (FriendNumber[arg2] != null) {
								String friendId = "-" + FriendNumber[arg2]
										+ "-";
								participants.add(friendId);

								String participantName = FriendName[arg2];
								participantNames.add(participantName);
								positionList.add(arg2);
								addAvatar(arg2);
							}
						}

					}
					addParticipantAdp.notifyChange(ConversationMessages.this,
							FriendName, status, Groupid, FriendNumber,
							friendImage, sectionHeaders, isSelected);
				}

				Log.e("",
						"========== The List of participants "
								+ participants.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	// Set the visibility of participant list to visible or invisible
	private void showParticipantList() {
		try {
			if (addParticipantList.getVisibility() == View.GONE) {
				addParticipantList.setVisibility(View.VISIBLE);
				TribeWireUtil.hideKeyboard(ConversationMessages.this,
						msg_send_txt);
			} else if (addParticipantList.getVisibility() == View.VISIBLE) {
				addParticipantList.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Method for contact starts
	public void CreateProgressBar() {

		pd = new ProgressDialog(this);
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage(this.getResources().getString(R.string.loading));
		pd.setMax(5000);

		pd.show();

	}

	private void showAttachImageView() {
		try {
			if (rltImageAttachView.getVisibility() == View.GONE) {
				rltImageAttachView.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void hideAttachImageView() {
		try {
			if (rltImageAttachView.getVisibility() == View.VISIBLE) {
				rltImageAttachView.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Delete Message");
		menu.add(0, 1, 0, "Delete");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	int deletedPosition = 0;

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == 1) {
			// Toast.makeText(this, "CLicked "+info.position,
			// Toast.LENGTH_SHORT).show();
			deletedPosition = info.position - 1;
			deleteUserMessage(MessageId.get((info.position - 1)));
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void deleteUserMessage(String messageId) {
		List<String> nameList = new ArrayList<String>();
		nameList.add("api_key");
		nameList.add("user_id");
		nameList.add("session");
		nameList.add("message_id");
		String api_key = Constants.API_KEY;
		String ref_id = CommonFunctions.getPref(this, CommonFunctions.REF_ID,
				"");

		String session = CommonFunctions.getPref(this, Constants.SESSION, "");
		List<String> valueList = new ArrayList<String>();
		valueList.add(api_key);
		valueList.add(ref_id);
		valueList.add(session);
		valueList.add(messageId);
		if (CommonFunctions.isInternetConnected(this)) {
			pd = ProgressDialog.show(this, "Delete Message",
					"Please wait while deleting your message");
			pd.setCancelable(true);
			new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
					+ CommonFunctions.DELETE_USER_MESSAGE, nameList, valueList)
					.start();
		} else {
			CommonFunctions.showAlert(this,
					"Please check your internet connectivity", "Network Error");

		}

	}

	public void ondeleteMessageResponse(String response, String messageId) {
		Log.e("", "================ Delete Message Response is " + response);

		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject jsonObject2 = jsonObject.getJSONObject("response");
			String error = jsonObject2.getString("error");
			if (error.equalsIgnoreCase("false")) {
				Log.e("", "========= Message Deleted successfully");

				DataBase db = null;
				try {
					db = new DataBase(this);
					db.open();
					boolean isDeleted = db.delete(DataBase.Receive_table,
							DataBase.Receive_int, "message_id  = '" + messageId
									+ "'");
					Log.e("", "========= Is Message Deleted " + isDeleted);

					if (isDeleted) {
						handler2.sendEmptyMessage(88);
					}
				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					if (db != null) {
						db.close();
					}
				}
				if (pd != null) {
					pd.dismiss();
				}
			} else {
				Toast.makeText(this,
						"Error while deleting message, Please try again later",
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initializeRecordingUi() {
		conRecordingLayout = (LinearLayout) findViewById(R.id.con_audio_recording);

		txtSecondsPlayed = (TextView) findViewById(R.id.second_played);
		progressRecording = (ProgressBar) findViewById(R.id.progressBarRecord);
		txtPlay = (Button) findViewById(R.id.btn_play);

		txtRecord = (Button) findViewById(R.id.btn_record);
		txtCancelRecording = (Button) findViewById(R.id.txt_cancel);
		txtPlay.setOnClickListener(recordingListener);

		txtRecord.setOnClickListener(recordingListener);
		txtCancelRecording.setOnClickListener(recordingListener);
	}

	String recordingPath = "";
	boolean isOngoingRecording = false;
	public int count = 0;
	public int seconds = 60;
	public boolean isPlayingCurrently = false;
	private int audioPlayCount = 0;
	// private ExtAudioRecorder recorder;
	private MediaRecorder recorder;
	volatile boolean running;
	private Thread recordThread;
	private int audioDuration;
	public String baseurl;

	public void StartRecording() {
		isMediaPause = false;
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
		// recorder = ExtAudioRecorder.getInstanse(false);
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

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

									if (isMediaPause == false) {
										audioPlayProgressHandler
												.sendMessage(audioPlayProgressHandler
														.obtainMessage());
										sleep(1000);
										audioPlayCount++;
									}

								}
								if (audioPlayCount >= audioDuration
										&& isPlayingCurrently) {
									if (isMediaPause == false) {
										Debugger.debugE("if audioPlayCount"
												+ audioPlayCount);
									}

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
			Toast.makeText(getBaseContext(), "Audio File Does Not Exist", 5000)
					.show();
			// Alert dialog if file dose not exit

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					ConversationMessages.this);

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

	private boolean isMediaPause = false;

	private void stopPlaying() {

		try {
			txtRecord.setBackgroundResource(R.drawable.record_btn_off);
			txtPlay.setBackgroundResource(R.drawable.videoplay);
			isMediaPause = false;
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

	int length = 0;
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
							Toast.makeText(ConversationMessages.this,
									"Please record some file before play",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {

						e.printStackTrace();
					}
				} else if (isPlayingCurrently) {
					Toast.makeText(ConversationMessages.this,
							"File is currently playing ", Toast.LENGTH_SHORT)
							.show();
					if (isMediaPause == false) {
						if (mp != null && mp.isPlaying()) {
							txtPlay.setBackgroundResource(R.drawable.videoplay);
							mp.pause();
							length = mp.getCurrentPosition();
							isMediaPause = true;
						}

					} else if (isMediaPause == true) {
						if (mp != null) {
							txtPlay.setBackgroundResource(R.drawable.videopause);
							mp.seekTo(length);
							mp.start();
							isMediaPause = false;
						}
					}

				} else if (isOngoingRecording) {
					Toast.makeText(
							ConversationMessages.this,
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

	HashMap<Integer, ImageView> positionViewMap   = new HashMap<Integer, ImageView>();
	private void addAvatar(final int position){
		Log.e("", "======================= The Add Index is "+position);
	          try {
	        ImageView view	=positionViewMap.get(position);
	        	if (view!=null) {
					return;
					
				}
			} catch (Exception e) {
				//e.printStackTrace(); 
				
			}
		
		try {
			final ImageView imageView = new ImageView(this);
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			final float scale = getResources().getDisplayMetrics().density;
			int pixels = (int) (55 * scale + 0.5f);
			layoutParams.width = pixels;
			layoutParams.height = pixels;
			layoutParams.setMargins(10, 10, 10, 10);
			imageView.setLayoutParams(layoutParams);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
              	Toast toast = Toast.makeText(ConversationMessages.this, FriendName[position],Toast.LENGTH_LONG);
					
					int[] pos = new int[2];
					imageView.getLocationInWindow(pos);
					//tGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((contentDesc.length() / 2) * 12), pos[1] - 128);
					toast.setGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((FriendName[position].length() / 2) * 12), pos[1]+50);
					toast.show();   
					
				}
			});
			positionViewMap.put(Integer.valueOf(position), imageView);
			
try {
	if ( friendImage!= null) {
		if (friendImage[position] != null
				&& friendImage[position].length() > 0) {
			// ImageDownloader1.getInstance().download(
			// frdm.getImgUrl(), imageView);

			ConversationAdapterNew.checkImage(
					friendImage[position], imageView);

		} else {
			imageView.setImageDrawable(this.getResources()
					.getDrawable(R.drawable.no_image));
		}
	} else {
		imageView.setImageDrawable(this.getResources()
				.getDrawable(R.drawable.no_image));
	}
} catch (Exception e) {
	e.printStackTrace();
}
			conConversationListeners.addView(imageView);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void removeAvatar(int position){
		try {
			Log.e("", "======================= The Remove Index is "+position);
			ImageView view	=positionViewMap.get(position);
        	if (view!=null) {
        		conConversationListeners.removeView(view);
        		positionViewMap.remove(Integer.valueOf(position));
				
			}
			
		} catch (Exception e) {
		  e.printStackTrace();
		
		}
		
		
	}
	
	private void addConversationListeners() {

		Log.e("",
				"========== The Child Count is "
						+ conConversationListeners.getChildCount());
		if (conConversationListeners.getChildCount() == 0) {

			try {
				if (conversationMessages != null
						&& conversationMessages.size() > 0) {

					for (int i = 0; i < conversationMessages.size(); i++) {
						final FriendModel frdm = conversationMessages.get(i);
						final ImageView imageView = new ImageView(this);
						LayoutParams layoutParams = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						final float scale = getResources().getDisplayMetrics().density;
						int pixels = (int) (55 * scale + 0.5f);
						layoutParams.width = pixels;
						layoutParams.height = pixels;
						layoutParams.setMargins(10, 10, 10, 10);
						imageView.setLayoutParams(layoutParams);
						imageView.setScaleType(ScaleType.CENTER_CROP);
						positionViewMap.put(Integer.valueOf(i), imageView);
						imageView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {

									Toast toast = Toast.makeText(
											ConversationMessages.this,
											frdm.getFriendName(), Toast.LENGTH_LONG);

									int[] pos = new int[2];
									imageView.getLocationInWindow(pos);
									// tGravity(Gravity.TOP | Gravity.LEFT, pos[0] -
									// ((contentDesc.length() / 2) * 12), pos[1] -
									// 128);
									toast.setGravity(Gravity.TOP | Gravity.LEFT,
											pos[0]
													- ((frdm.getFriendName()
															.length() / 2) * 12),
											pos[1] + 50);
									toast.show();

								
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}
						});

						if (frdm != null) {
							if (frdm.getImgUrl() != null
									&& frdm.getImgUrl().length() > 0) {
								// ImageDownloader1.getInstance().download(
								// frdm.getImgUrl(), imageView);

								ConversationAdapterNew.checkImage(
										frdm.getImgUrl(), imageView);

							} else {
								imageView.setImageDrawable(this.getResources()
										.getDrawable(R.drawable.no_image));
							}
						} else {
							imageView.setImageDrawable(this.getResources()
									.getDrawable(R.drawable.no_image));
						}
						conConversationListeners.addView(imageView);

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onClick(View v) {
		if (v == btn_current_conv) {
			// if (conConversationListeners.getVisibility() == View.VISIBLE) {
			// conConversationListeners.setVisibility(View.GONE);
			//
			// } else if (conConversationListeners.getVisibility() == View.GONE)
			// {
			// conConversationListeners.setVisibility(View.VISIBLE);
			// addConversationListeners();
			// }

			if (scrollView.getVisibility() == View.VISIBLE) {
				scrollView.setVisibility(View.GONE);
				txtCurrentConv.setText("CURRENT CONVERSATION");				
				showParticipantList();
				try {
					conConversationListeners.removeAllViews();
					positionViewMap.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
				

			} else if (scrollView.getVisibility() == View.GONE) {
				scrollView.setVisibility(View.VISIBLE);
				txtCurrentConv.setText("PARTICIPANTS");
				//addConversationListeners();
				showParticipantList();
				participants = new ArrayList<String>();
				participantNames = new ArrayList<String>();
				positionList = new ArrayList<Integer>();
				CreateProgressBar();
				FillFriends();
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 4) {
			updateReadValue();
			if (isNewConversation) {
				setResult(RESULT_OK);
				isNewConversation = false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		try {
			
			isChatActivityVisible = true;
			//startMessageService();		
			ActivityStatus.setActivity(this);
			refreshConversationList(0);
			try {
				NotificationManager mNotificationManager = (NotificationManager) this
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {

		try {
			CONV_SCREEN_STATUS = 1;
			isChatActivityVisible = false;
			if (isOngoingRecording) {
				stopRecording();
			} else if (isPlayingCurrently) {
				stopPlaying();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (cv != null) {
				if (cv.mp != null && cv.mp.isPlaying()) {
					cv.mp.stop();
					cv.mp.release();
					cv.mp = null;
					cv.move = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ActivityStatus.setActivity(null);
		try {
			if (isServiceBind) {
			//	disconnectService();
			} else {
				//Log.e("",
			//			"================ Service not bind So No need to disconect=============");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ActivityStatus.appStatus(null, false);
		super.onPause();
	}

	
	
	/**
	 * 
	 * {"error":false, "error_msg":"", "conversation":[ {"msg_id":"MTQ=",
	 * "sender_id":"Mg==", "sender":"Joe Smith", "text":"aGkgIHN1c2k=",
	 * "date_time":"2013-07-26 17:54:57", "attachment_type":"0",
	 * "attached_file_name":null, "thumbnail":""},
	 * 
	 * ] }
	 */

	private static final int SHOW_PROGRESS = 1;
	private static final int DISMISS__PROGRESS = 2;
	private static final int REFRESH_CONVERSATION = 3;
	Handler threadHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == SHOW_PROGRESS) {
				boolean isFillConversation = false;
				try {
					try {
						isFillConversation = msg.getData().getBoolean("isfill",
								false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (progressDialog == null) {
						if (isFillConversation) {
							progressDialog = ProgressDialog.show(
									ConversationMessages.this, "Conversation",
									"Please wait while getting conversation");
							progressDialog.setCancelable(true);
						}

						else {
							progressBar.setVisibility(View.VISIBLE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();

				}

			} else if (msg.what == DISMISS__PROGRESS) {
				try {
					progressDialog.dismiss();
					progressDialog = null;

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == REFRESH_CONVERSATION) {
				refreshConversationData();
			}

		};
	};

	// class TestAsync extends AsyncTask<Void, Void, Void> {
	class TestAsync extends Thread {

		boolean isFillConversation = false;

		public TestAsync(boolean isFillConversation) {

			this.isFillConversation = isFillConversation;
			// Message message = new Message();
			// message.what = SHOW_PROGRESS;
			// message.getData().putBoolean("isfill", isFillConversation);
			// threadHandler.sendMessage(message);
		}

		// @Override
		// protected void onPreExecute() {
		// if (progressDialog == null) {
		// if (isFillConversation) {
		// progressDialog = ProgressDialog.show(
		// ConversationMessages.this, "Conversation",
		// "Please wait while getting conversation");
		// progressDialog.setCancelable(true);
		// }
		//
		// else {
		// progressBar.setVisibility(View.VISIBLE);
		// }
		// }
		// super.onPreExecute();
		// }

		@Override
		public void run() {
			Looper.prepare();
			Message message = new Message();
			message.what = SHOW_PROGRESS;
			message.getData().putBoolean("isfill", isFillConversation);
			threadHandler.sendMessage(message);

			doInBackground();
			Looper.loop();
			super.run();
		}

		// @Override
		protected Void doInBackground() {

			String api_key = Constants.API_KEY;
			String ref_id = CommonFunctions.getPref(ConversationMessages.this,
					CommonFunctions.REF_ID, "");
			String conv_id = THREAD;
			String session = CommonFunctions.getPref(ConversationMessages.this,
					Constants.SESSION, "");
			List<String> nameList = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.CONV_ID_TAG);
			nameList.add(Constants.SESSION_TAG);
			nameList.add(Constants.LIMIT);
			List<String> valueList = new ArrayList<String>();
			valueList.add(api_key);
			valueList.add(ref_id);
			valueList.add(conv_id);

			valueList.add(session);

			valueList.add("" + requestMessageCount);
			try {
				String response = CommonFunctions.postNameValueDataOnServer(
						ConversationMessages.this, nameList.size(), nameList,
						valueList, CommonFunctions.DEMO_SERVER_URL
								+ CommonFunctions.GET_SPECIFIC_CONVERSATION);
				Log.e("", "========= Conversation Message Response is "
						+ response);
				if (response != null && response.length() > 0) {
					try {
						JSONObject jsonObject1 = new JSONObject(response);
						JSONObject jsonObject = jsonObject1
								.getJSONObject("response");
						// JSONObject jsonObject = new JSONObject(response);
						String error = jsonObject.getString("error");
						if (error.equalsIgnoreCase("false")) {
							String total_Count = jsonObject
									.getString("total_count");
							Log.e("", "=== The total count is =========="
									+ total_Count);
							try {
								totalMessageCount = Integer
										.parseInt(total_Count);
								if (totalMessageCount < requestMessageCount) {
									requestMessageCount = totalMessageCount;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							JSONArray conversationArray = jsonObject
									.getJSONArray("conversation");
							if (conversationArray != null
									&& conversationArray.length() > 0) {
								DataBase db = null;

								String user_id = CommonFunctions.getPref(
										ConversationMessages.this,
										CommonFunctions.REF_ID, "");
								String receiver = "";
								try {
									db = new DataBase(ConversationMessages.this);
									db.open();
									try {
										Cursor c = db.fetch(
												DataBase.Receive_table,
												DataBase.Receive_int,
												"conversation_Id ='" + THREAD
														+ "'");

										try {
											receiver = c.getString(3);
										} catch (Exception e) {
											e.printStackTrace();
										}
										try {
											boolean isDelete = db.delete(
													DataBase.Receive_table,
													DataBase.Receive_int,
													"conversation_Id ='"
															+ THREAD + "'");
											Log.e("",
													"============== Table Deleted "
															+ isDelete);
										} catch (Exception e) {
											// TODO: handle exceptionresend

										}
										Log.e("",
												" ============ The receiver Name is "
														+ receiver
														+ "=================");
									} catch (Exception e) {
										e.printStackTrace();
									}

									for (int i = 0; i < conversationArray
											.length(); i++) {

										JSONObject jsonObject2 = conversationArray
												.getJSONObject(i);
										ContentValues cv = new ContentValues();
										cv.put("message_id",
												jsonObject2.getString("msg_id"));
										cv.put("sender", user_id);
										cv.put("receiver", receiver);
										String localMsgType = "0";

										try {
											String message_type = jsonObject2
													.getString("attachment_type");
											if (message_type
													.equalsIgnoreCase("1")) {
												// Means Text Message
												localMsgType = "0";
											} else if (message_type
													.equalsIgnoreCase("0")) {
												// Means Audio Message
												localMsgType = "1";
											} else if (message_type
													.equalsIgnoreCase("2")) {
												// Means Audio Message
												localMsgType = "2";
											} else if (message_type
													.equalsIgnoreCase("3")) {
												// Means Image Message
												localMsgType = "3";
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										cv.put("message_type", localMsgType);
										try {
											String attachedFile_Name = jsonObject2
													.getString("attached_file_name");
											if (attachedFile_Name != null
													&& attachedFile_Name
															.length() > 0) {
												if (localMsgType
														.equalsIgnoreCase("3")) {

													try {
														String extension = attachedFile_Name
																.substring(
																		attachedFile_Name
																				.indexOf("."),
																		attachedFile_Name
																				.length());

														attachedFile_Name = attachedFile_Name
																.substring(
																		0,
																		attachedFile_Name
																				.indexOf("."));
														// attachedFile_Name =
														// attachedFile_Name
														// + "_thumb"
														// + extension;
														attachedFile_Name = attachedFile_Name
																+ ""
																+ extension;
													} catch (Exception e) {
														e.printStackTrace();
													}

													attachedFile_Name = Constants.IMAGE_BASE_URL
															+ attachedFile_Name;

												} else if (localMsgType
														.equalsIgnoreCase("2")) {
													attachedFile_Name = Constants.AUDIO_BASE_URL
															+ attachedFile_Name;
												} else if (localMsgType
														.equalsIgnoreCase("4")) {
													attachedFile_Name = Constants.VIDEO_BASE_URL
															+ attachedFile_Name;
												}
												cv.put("file_url",
														attachedFile_Name);
											} else {
												cv.put("file_url", "");
											}

										} catch (Exception e) {
											e.printStackTrace();
											cv.put("file_url", "");
										}
										Log.e("",
												"======= Long Millis "
														+ CommonFunctions
																.getLongMillisfromDate(jsonObject2
																		.getString("date_time"))
														+ " ==  Server date "
														+ jsonObject2
																.getString("date_time")
														+ "  == Ano Date "
														+ CommonFunctions
																.getdateAno(CommonFunctions
																		.getLongMillisfromDate(jsonObject2
																				.getString("date_time"))));
										cv.put("date_time",
												""
														+ CommonFunctions
																.getLongMillisfromDate(jsonObject2
																		.getString("date_time")));
										String sender_Id = jsonObject2
												.getString("sender_id");
										if (sender_Id.equalsIgnoreCase(user_id)) {
											cv.put("type", "0");
											// Log.e("",
											// "=============== Message sent by Susan ====================");
										} else {
											cv.put("type", "1");
											// Log.e("",
											// "=============== Message sent by Dave ====================");
										}

										try {
											String senderName = jsonObject2
													.getString("sender");
											cv.put("name", "" + senderName);
										} catch (Exception e) {
											// TODO: handle exception
										}
										cv.put("status", "0");
										cv.put("conversation_Id", THREAD);
										cv.put("favourite", "56");
										cv.put("played", "0");
										cv.put("textmessage",
												jsonObject2.getString("text"));
										cv.put("newdate", jsonObject2
												.getString("date_time"));
										String thumbNail = "";
										try {
											thumbNail = jsonObject2
													.getString("thumbnail");
											thumbNail = Constants.VIDEO_BASE_URL
													+ thumbNail;
											// Log.e("",
											// "===================The thumbnail Value is "
											// + thumbNail
											// + "============");
											if (thumbNail != null
													&& thumbNail.length() > 0) {
												cv.put("thumbnail", thumbNail);
											} else {
												cv.put("thumbnail", thumbNail);
											}
										} catch (Exception e) {
											e.printStackTrace();
											cv.put("thumbnail", thumbNail);
										}

										db.insert_content(
												DataBase.Receive_table,
												DataBase.Receive_int, cv);
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (db != null) {
										db.close();
									}
								}

							}

						} else {

							Log.e("",
									"===========Something went wrong with Conversation Web-Service==========");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					try {
						if (!isFillConversation) {
							// handler2.sendEmptyMessage(what)
							// refreshList.onRefreshComplete();;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				onPostExecute(null);
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			// @Override
			// protected void onPostExecute(Void result) {
			try {
				if (isFillConversation) {

					fillConversation();
					if (requestMessageCount == totalMessageCount) {
						// txtLoadMoreConversation.setVisibility(View.GONE);

					}
					if (progressDialog != null) {
						//
						// progressDialog.dismiss();
						// progressDialog = null;
						threadHandler.sendEmptyMessage(DISMISS__PROGRESS);
					}

				} else {
					if (progressDialog != null) {

						// progressDialog.dismiss();
						// progressDialog = null;

						threadHandler.sendEmptyMessage(DISMISS__PROGRESS);
					}

					refreshConversationData();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// super.onPostExecute(result);
		}
	}

	public void refreshConversationData() {
		handler2.sendEmptyMessage(LOAD_MORE_REFRESH);

	}

	String sharedFavConvId = "";
	int position = 0;
	String tempMessageid = "0";
	EditText msg_send_txt;
	Button msg_send;

	String name = "";

	String number = "";

	List<Integer> isdraft = null;
	List<String> file_url = null;
	ConversationAdapterNew cv = null;
	ImageView add_Media;
	List<String> Sender = null;
	List<String> receiver = null;
	List<String> time = null;
	List<Integer> messagetype = null;
	List<String> textMessage = null;
	List<Integer> sendReceiveTyep = null;
	List<String> newServerDate = null;
	List<String> thumbNail = null;
	List<String> senderName = null;

	String edt_message_saved = "";

	String ALLNAME = "";
	private Button btn_current_conv;
	PullToRefreshListView refreshList;
	OnRefreshListener onRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			if (requestMessageCount == totalMessageCount) {

				try {
					// txtLoadMoreConversation.setVisibility(View.GONE);
					handler2.sendEmptyMessage(REFRESH_ALREADY_COMPLETE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			if (requestMessageCount <= totalMessageCount) {

				requestMessageCount = requestMessageCount + 10;

				if (requestMessageCount >= totalMessageCount) {

					requestMessageCount = totalMessageCount;

				}
				// new TestAsync(false).execute();
				new TestAsync(false).start();
			}

		}
	};
	public int messageOrConversation = -1;
	boolean viewExists = true;
	List<String> MessageId = null;
	String FAVOURITE = "Favourite";

	String[] conversationIDs;
	private String uploadFilePath = "";

	private void deleteAudioFile(String audioFile) {
		try {
			File file = new File(audioFile);
			if (file.exists()) {
				file.delete();
				recordingPath = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void fillConversation() {

		// Intent intent = new Intent(this,FillConversation.class);
		// intent.putExtra("THREAD", THREAD);
		// startActivity(intent);

		sharedFavConvId = "";
		messageOrConversation = 2;
		viewExists = true;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				// global_reply.setOnClickListener(ConversationMessages.this);

				conversationIDs = null;
				MessageId = null;
				// db = new DataBase(TenFourConversations.this);
				msg_send.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							Log.e("", "The user name is ========== " + name);
						} catch (Exception e) {
							e.printStackTrace();
						}
						String userIds = Constants.getUserids(name,
								ConversationMessages.this);

						if (userIds != null && userIds.length() > 0) {
							if (CommonFunctions
									.CheckNetConnectivity(ConversationMessages.this)) {
								// handler2.sendEmptyMessage(START_SENDING_MESSAGE);
								if (msg_send_txt.getText().toString().length() > 0
										|| (uploadFilePath != null && uploadFilePath
												.length() > 0)) {

									try {
										if (conRecordingLayout.getVisibility() == View.VISIBLE) {
											conRecordingLayout
													.setVisibility(View.GONE);
										}

									} catch (Exception e) {
										e.printStackTrace();
									}

									if (progressBar.getVisibility() == View.INVISIBLE) {
										progressBar.setVisibility(View.VISIBLE);
									}
									Log.e("",
											"============= The receiver Name is **********"
													+ name);
									ALLNAME = name;
									// new SendMesssage(msg_send_txt.getText()
									// .toString(), uploadFilePath, name,
									// userIds, ConversationMessages.this)
									// .execute();
									String message = msg_send_txt.getText()
											.toString();
									String TempuploadFilePath = uploadFilePath;
									String tempName = name;
									msg_send_txt.setText("");
									uploadFilePath = "";

									new SendMesssage(message,
											TempuploadFilePath, tempName,
											userIds, ConversationMessages.this)
											.start();

								}

								else {
									handler2.sendEmptyMessage(NO_TEXT_MEDIA);
								}
							} else {
								handler2.sendEmptyMessage(INTERNET_FAILED);
							}
						}

						else {
							if (isFyiReply) {

								if (CommonFunctions
										.CheckNetConnectivity(ConversationMessages.this)) {
									// handler2.sendEmptyMessage(START_SENDING_MESSAGE);
									if (msg_send_txt.getText().toString()
											.length() > 0
											|| (uploadFilePath != null && uploadFilePath
													.length() > 0)) {

										try {
											if (conRecordingLayout
													.getVisibility() == View.VISIBLE) {
												conRecordingLayout
														.setVisibility(View.GONE);
											}

										} catch (Exception e) {
											e.printStackTrace();
										}

										if (progressBar.getVisibility() == View.INVISIBLE) {
											progressBar
													.setVisibility(View.VISIBLE);
										}
										Log.e("",
												"============= The receiver Name is **********"
														+ name);
										ALLNAME = name;
										// new
										// SendMesssage(msg_send_txt.getText()
										// .toString(), uploadFilePath, name,
										// userIds, ConversationMessages.this)
										// .execute();
										String message = msg_send_txt.getText()
												.toString();
										String TempuploadFilePath = uploadFilePath;
										String tempName = name;
										msg_send_txt.setText("");
										uploadFilePath = "";

										new SendMesssage(message,
												TempuploadFilePath, tempName,
												userIds,
												ConversationMessages.this)
												.start();

									}

									else {
										handler2.sendEmptyMessage(NO_TEXT_MEDIA);
									}
								} else {
									handler2.sendEmptyMessage(INTERNET_FAILED);
								}

							}

						}

					}
				});
				DataBase db = new DataBase(ConversationMessages.this);
				try {

					db.open();
					Cursor c;
					// if (THREAD.equals(FAVOURITE)) {
					// c = db.fetchAll(DataBase.Receive_table,
					// DataBase.Receive_int, "date_time ASC",
					// "favourite=1");
					// } else
					c = db.fetchAll(DataBase.Receive_table,
							DataBase.Receive_int, "date_time ASC",
							"conversation_Id='" + THREAD + "'");

					if (c != null && c.getCount() > 0) {
						try {

							if (Sender != null && receiver != null
									&& time != null && messagetype != null
									&& textMessage != null
									&& sendReceiveTyep != null
									&& MessageId != null && isdraft != null
									&& file_url != null && Sender.size() > 0
									&& receiver.size() > 0 && time.size() > 0
									&& messagetype.size() > 0
									&& textMessage.size() > 0
									&& sendReceiveTyep.size() > 0
									&& MessageId.size() > 0
									&& isdraft.size() > 0
									&& file_url.size() > 0

							)

							{
								Log.e("",
										"=========================Clear if condition ======================");

								Sender.clear();
								receiver.clear();
								time.clear();
								messagetype.clear();
								textMessage.clear();
								sendReceiveTyep.clear();
								MessageId.clear();
								isdraft.clear();
								file_url.clear();
								newServerDate.clear();
								thumbNail.clear();
								try {
									senderName.clear();
								} catch (Exception e) {
									// TODO: handle exception
								}

							} else {
								Log.e("",
										"=========================New Else Condition ======================");
								Sender = new ArrayList<String>();
								receiver = new ArrayList<String>();
								time = new ArrayList<String>();
								messagetype = new ArrayList<Integer>();
								MessageId = new ArrayList<String>();

								textMessage = new ArrayList<String>();
								file_url = new ArrayList<String>();
								sendReceiveTyep = new ArrayList<Integer>();
								isdraft = new ArrayList<Integer>();
								newServerDate = new ArrayList<String>();
								thumbNail = new ArrayList<String>();
								senderName = new ArrayList<String>();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						// Sender = new ArrayList<String>();
						// receiver = new ArrayList<String>();
						// time = new ArrayList<String>();
						// messagetype = new ArrayList<Integer>();
						// MessageId = new ArrayList<Integer>();
						//
						// textMessage = new ArrayList<String>();
						// file_url= new ArrayList<String>();
						// sendReceiveTyep = new ArrayList<Integer>();
						// isdraft = new ArrayList<Integer>();

						c.moveToFirst();
						do {
							MessageId.add(c.getString(1));

							Sender.add(c.getString(2));
							receiver.add(c.getString(3));
							try {
								Log.e("",
										"============ The Receiver  name under do loop is "
												+ receiver.get(c.getPosition()));
								name = receiver.get(c.getPosition());
							} catch (Exception e) {
								e.printStackTrace();
							}
							time.add(CommonFunctions.getdateAno(c.getLong(6)));
							messagetype.add(c.getInt(4));
							textMessage.add((c.getString(15)));
							file_url.add(c.getString(5));
							sendReceiveTyep.add(c.getInt(7));
							isdraft.add(c.getInt(16));
							newServerDate.add(c.getString(17));
							thumbNail.add(c.getString(18));
							senderName.add(c.getString(19));
							// Log.e("",
							// "==================Here the value is "+sendReceiveTyep[c.getPosition()]+" ================");

							// Log.e("",
							// "The Text Message is ========="+c.getString(15));

							// if (c.getInt(7) == 1) {
							// Debugger.debugE("in receiver,.." + c.getInt(7));
							//
							// Log.e("",
							// "================The Receiver is =============== "+messagetype[c.getPosition()]
							// +"Original  "+c.getInt(4));
							// } else {
							// Debugger.debugE("in sender,.." + c.getInt(7));
							// Log.e("",
							// "==================in Sender is =============== "+messagetype[c.getPosition()]+
							// "Original  "+c.getInt(4));
							// }
							try {
								// tempMessageid = MessageId.get(position);
							} catch (Exception e) {
								e.printStackTrace();
							}

						} while (c.moveToNext());

						// lv = getListView();

						cv = new ConversationAdapterNew(
								ConversationMessages.this,
								Sender.toArray(new String[Sender.size()]),
								receiver.toArray(new String[receiver.size()]),
								time.toArray(new String[time.size()]),
								messagetype.toArray(new Integer[messagetype
										.size()]),
								sendReceiveTyep
										.toArray(new Integer[sendReceiveTyep
												.size()]),
								textMessage.toArray(new String[textMessage
										.size()]),
								file_url.toArray(new String[file_url.size()]),
								MessageId.toArray(new String[MessageId.size()]),
								isdraft.toArray(new Integer[isdraft.size()]),
								newServerDate.toArray(new String[newServerDate
										.size()]),
								thumbNail.toArray(new String[thumbNail.size()]),
								senderName.toArray(new String[senderName.size()]));

						refreshList.setAdapter(cv);

						// cd = new
						// ConversationAdapterNew(TenFourConversations.this, c);
						// lv.setAdapter(cd);
						refreshList
								.setOnItemClickListener(ConversationMessages.this);
						refreshList.setOnLongClickListener(null);
						refreshList.setCacheColorHint(Color.TRANSPARENT);
						refreshList.setSelection(Sender.size() - 1);

						refreshList.setRecyclerListener(new RecyclerListener() {

							@Override
							public void onMovedToScrapHeap(View view) {

								try {
									ImageView imageView = (ImageView) view
											.findViewById(R.id.img_message);
									imageView.setImageDrawable(null);
									ImageView imageView1 = (ImageView) view
											.findViewById(R.id.img_audio_message);
									imageView1.setImageDrawable(null);
									ImageView imageView2 = (ImageView) view
											.findViewById(R.id.img_profile_pic);
									imageView2.setImageDrawable(null);
									// TextView txt= (TextView)
									// view.findViewById(R.id.txtmsg1);
									// txt.setVisibility(View.GONE);

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						// lv = getListView();
						refreshList.setCacheColorHint(Color.TRANSPARENT);
						refreshList.setAdapter(new SimpleAdapter(
								ConversationMessages.this,
								"Messages not available"));
						refreshList.setOnItemClickListener(null);
						refreshList.setOnItemLongClickListener(null);

						if (THREAD.equals(FAVOURITE))
							Toast.makeText(
									ConversationMessages.this,
									"No favorites selected. Please add from messages",
									1).show();

						THREAD = "";
						// FillMessageFolder();
						finish();
					}

					// c.close();
				} catch (Exception e) {

					Debugger.debugE("in error" + e.getMessage());
					e.printStackTrace();
				} finally {
					db.close();
				}

			}
		});
		// TenFourTab.loadAds();
		// ((AdView) findViewById(R.id.ad)).requestFreshAd();

	}

	Intent msgSer;

	private void startMessageService() {
		Log.e("", "========In the Start Message Service==============="
				+ THREAD);
		try {
			msgSer = new Intent(TribMessageCollectorService.class.getName());

			msgSer.putExtra("conv_id", THREAD);
			startService(msgSer);
			bindService(msgSer, serviceConnection, 0);

			isServiceBind = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	final String TAG = "TenFourConversations";
	TribMessageCollectorapi api;
	ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

			Log.e(TAG, "Service connection disconnected");

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "Service connection established");
			api = TribMessageCollectorapi.Stub.asInterface(service);
			try {
				api.addListenerTrib(msgCollecor);
			} catch (RemoteException e) {
				Log.e(TAG, "Failed to add listener", e);
				e.printStackTrace();
			}

		}
	};

	private int FAILED_MSG_SENT_SUCCESS = 2;
	private int FAILED_MSG_SENT_FAILURE = 3;
	private int START_SENDING_MESSAGE = 4;
	private int INTERNET_FAILED = 5;
	private int SEND_CHAT_MESSAGE = 6;
	private int NO_TEXT_MEDIA = 7;
	private int LOAD_MORE_REFRESH = 8;
	private int REFRESH_ALREADY_COMPLETE = 10;

	private int UPDATE_RECIPIENTS = 14;
	boolean isServiceBind = false;

	private void notifyData(int position) {
		if (cv != null) {
			updateSendMessage(true, position);
		}

	}

	public Handler handler3 = new Handler() {

		public void handleMessage(android.os.Message 
				msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
				 TribeWireUtil.showNotificationAlert(ConversationMessages.this, emergencyMessage, "Emergency");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		};
	
	public Handler handler2 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 updateMessage(message, false);
					
					/*if (api.getLatestSearchResult().getMessages().size() > 0
							&& api.getLatestSearchResult().getMessages() != null) {
						Log.e("", "============ Got the message=========");
						String message = api.getLatestSearchResult()
								.getMessages().get(0);

						updateMessage(message, false);

						try {
							api.getLatestSearchResult().getMessages().clear();
						} catch (Exception e) {

						}
					} else {
						Log.e("",
								"============ List is empty======================= :(");
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}

			} 
			
			else if (msg.what == FAILED_MSG_SENT_SUCCESS) {

				try {
					if (cv != null) {
						int position = msg.getData().getInt("position");
						Log.e("", "=========== The message is " + position);
						notifyData(position);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(ConversationMessages.this,
						"Message has been sent successfully", Toast.LENGTH_LONG)
						.show();
			}

			else if (msg.what == LOAD_MORE_REFRESH) {

				try {
					if (cv != null) {

						if (progressBar.getVisibility() == View.VISIBLE) {
							progressBar.setVisibility(View.INVISIBLE);
						}
						if (requestMessageCount == totalMessageCount) {
							// txtLoadMoreConversation.setVisibility(View.GONE);

						}
						try {
							refreshList.onRefreshComplete();
						} catch (Exception e) {
							e.printStackTrace();
						}
						updateSendMessage(false, 0);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == REFRESH_ALREADY_COMPLETE) {

				try {
					if (cv != null) {

						refreshList.onRefreshComplete();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 88) {
				refreshConversationList(deletedPosition);
			}

			else if (msg.what == SEND_CHAT_MESSAGE) {

				try {
					if (eyesayonWayDialog != null) {
						eyesayonWayDialog.dismiss();

					}
					if (cv != null) {
						if (progressBar.getVisibility() == View.VISIBLE) {
							progressBar.setVisibility(View.INVISIBLE);
						}
						hideAttachImageView();
						updateSendMessage(true, 0);
					}
					uploadFilePath = "";

				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Log.e("", "=========== The NAME " + ALLNAME);
					if (ALLNAME.trim().equalsIgnoreCase("ALL")) {
						// TenFourConversations.this.finish();
						try {
							if (isServiceBind) {
								disconnectService();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						finish();
						// FillMessageFolder();
					} else {
						Toast.makeText(ConversationMessages.this,
								"Message has been sent successfully",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			else if (msg.what == FAILED_MSG_SENT_FAILURE) {

				Toast.makeText(ConversationMessages.this,
						"Unable to send message please try again",
						Toast.LENGTH_LONG).show();
				if (cv != null) {
					if (progressBar.getVisibility() == View.VISIBLE) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					updateSendMessage(true, 0);
				}
				uploadFilePath = "";
				uploadFilePath = "";
			} else if (msg.what == INTERNET_FAILED) {

				Toast.makeText(ConversationMessages.this,
						"Please check your internet connectivity",
						Toast.LENGTH_LONG).show();
			}

			else if (msg.what == START_SENDING_MESSAGE) {

				eyesayonWayDialog = new EyesayonWayDialog(
						ConversationMessages.this);
				eyesayonWayDialog.setCancelable(true);
				eyesayonWayDialog.show();
				eyesayonWayDialog.getHandlerObj().sendEmptyMessage(9);
				// showEyesayDialog();

			} else if (msg.what == UPDATE_RECIPIENTS) {
				try {
					Toast.makeText(ConversationMessages.this, "Member updated successfully", Toast.LENGTH_LONG).show();
                try {
					
					if (scrollView.getVisibility() == View.VISIBLE) {
						scrollView.setVisibility(View.GONE);
						txtCurrentConv.setText("CURRENT CONVERSATION");				
						showParticipantList();
						try {
							conConversationListeners.removeAllViews();
							positionViewMap.clear();
						} catch (Exception e) {
							e.printStackTrace();
						}
						

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
					if (conConversationListeners.getChildCount() > 0) {												
						//addConversationListeners();
						//conConversationListeners.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			}

			else if (msg.what == NO_TEXT_MEDIA) {

				try {

				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(ConversationMessages.this,
						"Please enter some text or select media ",
						Toast.LENGTH_SHORT).show();
			}

			else if (msg.what == 8) {

				if (pd != null) {
					pd.dismiss();

					finish();
					// FillMessageFolder();
				}
			}

			else if (msg.what == 9) {

			}
		};
	};
	String myTempConversationId = "";
	ProgressDialog pd;

	private void updateSendMessage(boolean clearEdt, int position) {

		if (refreshList != null) {
			// DataBase db = new DataBase(ConversationMessages.this);
			try {

				// db.open();
				try {
					//
					// Cursor c;
					// if (THREAD.equals(FAVOURITE)) {
					// c = db.fetchAll(DataBase.Receive_table,
					// DataBase.Receive_int, "date_time ASC",
					// "favourite=1");
					// } else
					// c = db.fetchAll(DataBase.Receive_table,
					// DataBase.Receive_int, "date_time ASC",
					// "conversation_Id='" + myTempConversationId
					// + "'");
					if (cv != null) {
						if (viewExists) {
							// cd.changeCursor(c);
							refreshConversationList(position);

						}
						if (clearEdt) {
							msg_send_txt.setText("");
						}

					}

				} catch (Exception e) {
					e.printStackTrace();

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// db.close();
			}
		}

	}

	private void refreshConversationList(int position) {

		DataBase db = new DataBase(ConversationMessages.this);
		db.open();
		Cursor c = null;
		try {

			c = db.fetchAll(DataBase.Receive_table, DataBase.Receive_int,
					"date_time ASC", "conversation_Id='" + THREAD + "'");

			if (c != null && c.getCount() > 0) {
				try {

					if (Sender != null && receiver != null && time != null
							&& messagetype != null && textMessage != null
							&& sendReceiveTyep != null && MessageId != null
							&& isdraft != null && file_url != null
							&& Sender.size() > 0 && receiver.size() > 0
							&& time.size() > 0 && messagetype.size() > 0
							&& textMessage.size() > 0
							&& sendReceiveTyep.size() > 0
							&& MessageId.size() > 0 && isdraft.size() > 0
							&& file_url.size() > 0

					)

					{

						Sender.clear();
						receiver.clear();
						time.clear();
						messagetype.clear();
						textMessage.clear();
						sendReceiveTyep.clear();
						MessageId.clear();
						isdraft.clear();
						file_url.clear();
						newServerDate.clear();
						thumbNail.clear();
						senderName.clear();

					} else {
						Sender = new ArrayList<String>();
						receiver = new ArrayList<String>();
						time = new ArrayList<String>();
						messagetype = new ArrayList<Integer>();
						MessageId = new ArrayList<String>();

						textMessage = new ArrayList<String>();
						file_url = new ArrayList<String>();
						sendReceiveTyep = new ArrayList<Integer>();
						isdraft = new ArrayList<Integer>();
						newServerDate = new ArrayList<String>();
						thumbNail = new ArrayList<String>();
						senderName = new ArrayList<String>();
					}

				} catch (Exception e) {
					e.printStackTrace();

				}

				c.moveToFirst();

				do {
					MessageId.add(c.getString(1));

					Sender.add(c.getString(2));
					receiver.add(c.getString(3));
					try {
						name = receiver.get(c.getPosition());
					} catch (Exception e) {
						e.printStackTrace();
					}
					time.add(CommonFunctions.getdateAno(c.getLong(6)));
					messagetype.add(c.getInt(4));
					textMessage.add((c.getString(15)));
					file_url.add(c.getString(5));
					sendReceiveTyep.add(c.getInt(7));
					isdraft.add(c.getInt(16));
					newServerDate.add(c.getString(17));
					thumbNail.add(c.getString(18));
					senderName.add(c.getString(19));
					// Log.e("",
					// "==================Here the value is "+sendReceiveTyep[c.getPosition()]+" ================");

					// Log.e("",
					// "The Text Message is ========="+c.getString(15));

					// if (c.getInt(7) == 1) {
					// Debugger.debugE("in receiver,.." + c.getInt(7));
					//
					// Log.e("",
					// "================The Receiver is =============== "+messagetype[c.getPosition()]
					// +"Original  "+c.getInt(4));
					// } else {
					// Debugger.debugE("in sender,.." + c.getInt(7));
					// Log.e("",
					// "==================in Sender is =============== "+messagetype[c.getPosition()]+
					// "Original  "+c.getInt(4));
					// }
					try {
						// tempMessageid = MessageId.get(position);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} while (c.moveToNext());

				try {
					if (cv != null) {
						cv.notifiyMe(
								ConversationMessages.this,
								Sender.toArray(new String[Sender.size()]),
								receiver.toArray(new String[receiver.size()]),
								time.toArray(new String[time.size()]),
								messagetype.toArray(new Integer[messagetype
										.size()]),
								sendReceiveTyep
										.toArray(new Integer[sendReceiveTyep
												.size()]),
								textMessage.toArray(new String[textMessage
										.size()]),
								file_url.toArray(new String[file_url.size()]),
								MessageId.toArray(new String[MessageId.size()]),
								isdraft.toArray(new Integer[isdraft.size()]),
								newServerDate.toArray(new String[newServerDate
										.size()]),
								thumbNail.toArray(new String[thumbNail.size()]),
								senderName.toArray(new String[senderName.size()]));

						if (position != 0) {
							Log.e("",
									"============== this worked=================== with position zero");

							refreshList.setSelection(position);
						} else {
							Log.e("",
									"============== this worked=================== with position Greater then zero");
							refreshList.setSelection(MessageId.size() - 1);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	// class SendMesssage extends AsyncTask<Void, Void, String> {
	class SendMesssage extends Thread {
		private String message;

		private String uploadFilePath;
		private String names;
		private String userIds;
		private ISentChatMessage iSentChatMessage;

		public SendMesssage(String message, String uploadFilePath,
				String names, String userIds, ISentChatMessage iSentChatMessage) {
			this.message = message;
			this.names = names;
			this.uploadFilePath = uploadFilePath;
			this.userIds = userIds;
			this.iSentChatMessage = iSentChatMessage;
			Log.e("", "======== The message is " + message + "========");
			Log.e("", "======== The names are " + names + "========");
			Log.e("", "======== The uploadfilepath is " + uploadFilePath
					+ "========");
		}

		@Override
		public void run() {
			Looper.prepare();

			doInBackground();
			Looper.loop();
			super.run();
		}

		protected String doInBackground() {
			// @Override
			// protected String doInBackground(Void... params) {
			String Response = "";

			try {

				// TO DO this value is now changed to true by statically
				boolean isInsertToDb = checkMessageCount();
				Log.e("", "==== Check Message Count " + isInsertToDb);

				isInsertToDb = true;
				boolean isReply = false;
				if (isFyiReply) {
					isReply = true;
				}

				if (CommonFunctions.SendChatTextMessage(
						ConversationMessages.this, false, uploadFilePath,
						this.userIds, this.names, isReply, THREAD,
						this.message, isInsertToDb)) {
					Log.e("",
							"=================== Message sent successfully================");

					Response = "Sent";
					edt_message_saved = "";
					deleteAudioFile(uploadFilePath);
				} else {
					Log.e("",
							"===================Error while sending Message================"
									+ userIds + "========= Names " + names);
					// EyesaySendText.sendFailMessageToDrafts(
					// ConversationMessages.this, this.userIds,
					// this.names, "", uploadFilePath, false);
					edt_message_saved = "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			onPostExecute(Response);
			return Response;
		}

		protected void onPostExecute(String result) {
			// @Override
			// protected void onPostExecute(String result) {

			Log.e("", "================== Post executed Successfully ===");

			uploadFilePath = "";
			// Toast.ma

			if (result != null && result.length() > 0
					&& result.equalsIgnoreCase("sent")) {
				iSentChatMessage.sendChatMessage(true);
			} else {
				iSentChatMessage.sendChatMessage(false);
			}

			uploadFilePath = "";
			// super.onPostExecute(result);
		}

	}

	private boolean checkMessageCount() {
		boolean insertToDb = true;
		try {

			if (requestMessageCount == totalMessageCount) {
				insertToDb = true;
			} else {
				insertToDb = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insertToDb;

	}

	public EyesayonWayDialog eyesayonWayDialog;

	@Override
	public boolean sendChatMessage(boolean isSent) {

		if (isSent) {
			handler2.sendEmptyMessage(SEND_CHAT_MESSAGE);
		} else {
			handler2.sendEmptyMessage(FAILED_MSG_SENT_FAILURE);
		}

		return false;
	}

	private  void updateMessage(String message, boolean clearEdt) {
		String msgConversationId = "";
		if (refreshList != null) {
            
			try {

				try {
					if (message != null && message.length() > 0) {

						try {

							Log.e("", "================ The message is "
									+ message);

							JSONObject jsonObject1 = new JSONObject(message);
							//JSONObject jsonObject = jsonObject1
								//	.getJSONObject("response");
							//String error = jsonObject.getString("error");
							//if (error.equals("false")) {
								//JSONArray jsonArray = jsonObject
								//		.getJSONArray("conversation");

								//if (jsonArray != null && jsonArray.length() > 0) {
									DataBase db = null;
									try {

										db = new DataBase(
												ConversationMessages.this);
										db.open();
										/**
										 * {"msg_id":"192",
										 * "sender":"Dave Brown",
										 * "text":"SGkgVGVzdGluZw==",
										 * "attachment_type":"0",
										 * "attached_file_name":"",
										 * "thumbnail":"",
										 * "date_time":"2013-09-12 07:29:08"},
										 */
//										Log.e("",
//												"============= The Json Array Length is "
//														+ jsonArray.length());
										//for (int i = 0; i < jsonArray.length(); i++) {
											String localMsgType = "2";
											String attached_file_name = "";
											String text = "";
											String senderName = "";

											try {
												//JSONObject jsonObject2 = jsonArray
														//.getJSONObject(i);
												JSONObject jsonObject2 = jsonObject1;
												
												// String msg_Id
												// =jsonObject2.getString("msg_id");
												// String sender =
												// jsonObject2.getString("sender");
												text = jsonObject2
														.getString("text");
												// Log.e("",
												// " ========== The Text Message is "
												// + text
												// + "=============");
												// Log.e("",
												// " ========== The Text Message is "
												// + text
												// + "=============");

												try {

													try {
														String message_type = jsonObject2
																.getString("attachment_type");
														msgConversationId = jsonObject2
																.getString("conversationid");
														Log.e("",
																"--------------- The Message Type "
																		+ message_type
																		+ " ----------------");
														if (message_type
																.equalsIgnoreCase("1")) {
															// Means Text
															// Message
															localMsgType = "0";
														} else if (message_type
																.equalsIgnoreCase("0")) {
															// Means Audio
															// Message
															localMsgType = "1";
														} else if (message_type
																.equalsIgnoreCase("2")) {
															// Means Audio
															// Message
															localMsgType = "2";
														} else if (message_type
																.equalsIgnoreCase("3")) {
															// Means Image
															// Message
															localMsgType = "3";
														}
													} catch (Exception e) {
														e.printStackTrace();
													}
													// String message_type =
													// jsonObject2
													// .getString("attachment_type");
													// if (message_type
													// .equalsIgnoreCase("1")) {
													// // Means Video Message
													// localMsgType = "3";
													// } else if (message_type
													// .equalsIgnoreCase("2")) {
													// // Means Audio Message
													// localMsgType = "4";
													// } else if (message_type
													// .equalsIgnoreCase("3")) {
													// // Means Image Message
													// localMsgType = "2";
													// }
												} catch (Exception e) {
													e.printStackTrace();
												}

												try {
													attached_file_name = jsonObject2
															.getString("attached_file_name");
													if (attached_file_name != null
															&& attached_file_name
																	.length() > 0) {
														if (localMsgType
																.equalsIgnoreCase("2")) {
															attached_file_name = Constants.AUDIO_BASE_URL
																	+ attached_file_name;
														} else if (localMsgType
																.equalsIgnoreCase("3")) {
															attached_file_name = Constants.IMAGE_BASE_URL
																	+ attached_file_name;
														} else if (localMsgType
																.equalsIgnoreCase("4")) {
															attached_file_name = Constants.VIDEO_BASE_URL
																	+ attached_file_name;
														}

													}

												} catch (Exception e) {
													e.printStackTrace();

												}
												try {
													senderName = jsonObject2
															.getString("sender");

												} catch (Exception e) {
													// TODO: handle exception
												}

												String thumbnail = "";
												try {
													thumbnail = jsonObject2
															.getString("thumbnail");
												} catch (Exception e) {
													e.printStackTrace();
												}
												String date_time = jsonObject2
														.getString("date_time");

											} catch (Exception e) {
												e.printStackTrace();
											}

											Log.e("",
													"============= After Decoding text message is "
															+ new String(
																	Base64.decode(
																			text.getBytes(),
																			Base64.DEFAULT)));
											
											
											// if (checkMessageCount()) {
											long rowId = db
													.insert(DataBase.Receive_table,
															DataBase.Receive_int,
															new String[] {
																	"0",
																	CommonFunctions
																			.getPref(
																					ConversationMessages.this,
																					CommonFunctions.PHONE_NUMBER,
																					""),
																	name,
																	localMsgType,
																	attached_file_name,
																	""
																			+ System.currentTimeMillis(),
																	"1",
																	"0",
																	myTempConversationId,
																	"0",
																	"" + "1",
																	"ghgh",
																	"0", "0",
																	"" + text,
																	"", "", "",
																	senderName });
										
										//}

										try {

										} catch (Exception e) {
											// TODO: handle exception
										}

										Log.e("", "=======The name is " + name
												+ "  and Thread is "
												+ myTempConversationId
												+ "=============");

										// Cursor c;
										// if (THREAD.equals(FAVOURITE)) {
										// c =
										// db.fetchAll(DataBase.Receive_table,
										// DataBase.Receive_int,
										// "date_time ASC",
										// "favourite=1");
										// } else
										// c =
										// db.fetchAll(DataBase.Receive_table,
										// DataBase.Receive_int,
										// "date_time ASC",
										// "conversation_Id='" +
										// myTempConversationId + "'");
										if (cv != null) {
											if (viewExists) {
												// cd.changeCursor(c);
												if (msgConversationId.equalsIgnoreCase(myTempConversationId)) {
													refreshConversationList(0);	
												}
												

											}
											if (clearEdt) {
												msg_send_txt.setText("");
											}

										}

									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										try {
											db.close();
										} catch (Exception e2) {
											e2.printStackTrace();
										}
									}

								//}
							//}

						} catch (JSONException e) {
							e.printStackTrace();

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void disconnectService() {

		Log.e("", "========In the Disconnection Of Service===============");
		try {
			api.removeListenerTrib(msgCollecor);
		} catch (RemoteException e) {
			Log.e("", "========Unable to Disconnect Service===============");
			e.printStackTrace();
		}
		try {
			unbindService(serviceConnection);
			stopService(msgSer);
			isServiceBind = false;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	MessageCollectorListener.Stub msgCollecor = new MessageCollectorListener.Stub() {

		@Override
		public void handleMessageUpdated() throws RemoteException {
			// TODO Auto-generated method stub
			Log.e("", "=========Message Received===========");
			handler2.sendEmptyMessage(1);
			// updateMessage(db, message)

		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		try {
			/*
			 * DataBase db = new DataBase(ConversationMessages.this); db.open();
			 * Cursor c = db.fetch(DataBase.Receive_table, DataBase.Receive_int,
			 * MessageId.get(position)); int messageType = c.getInt(4); int type
			 * = c.getInt(7); String URL = c.getString(5); String Receiver =
			 * c.getString(3); Log.e("", "The Db Message Id is " + c.getInt(1));
			 * int played = 0; try { played = c.getInt(13); Log.e("",
			 * "Is Played " + played); } catch (Exception e) {
			 * //e.printStackTrace(); } c.close(); db.close(); try { Log.e("",
			 * "The Message Id is " + MessageId.get(position)); } catch
			 * (Exception e) {
			 * 
			 * }
			 * 
			 * try { if (isdraft.get(position) == 1) {
			 * 
			 * // showDraftActionSheet(arg2); } else { //
			 * checkMediaMessage(messageType, type, URL, Receiver, // played, //
			 * MessageId.get(position)); } } catch (Exception e) {
			 * e.printStackTrace(); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showMenu() {
		try {
			HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
			hashMap.put(0, "Take Photo");
			hashMap.put(1, "Use Existing Photo");
			hashMap.put(2, "Record Voice Message");
			hashMap.put(3, "Cancel");
			CustomMenu.show(this, hashMap, new OnMenuItemSelectedListener() {

				@Override
				public void MenuItemSelectedEvent(Integer selection) {
					if (selection == 0) {

						takePhoto();
					} else if (selection == 1) {
						chooseExisting();
					} else if (selection == 2) {
						hideAttachImageView();
						addVoice();
					} else if (selection == 3) {
						hideAttachImageView();
						CustomMenu.hide();
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		try {
			//Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File photo = getTempFile(this);
			photo.delete();
			photo = getTempFile(this);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
			imageUri = Uri.fromFile(photo);
			Log.e("", "=============== Now new Imagw Uri is "+imageUri);
			startActivityForResult(intent, ACTIVITY_CAPTURE_IMAGE);

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					if (imageUri != null) {
						try {
							imgAttachPic.setImageURI(imageUri);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 2) {
				try {
					imgAttachPic.setImageBitmap(profile_bm);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	};

	Uri selectedImage = null;
	Bitmap profile_bm = null;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_CAPTURE_IMAGE && resultCode == RESULT_OK) {
			if (resultCode == RESULT_OK) {

				Uri selectedImage = imageUri;
				try {

					//getContentResolver().notifyChange(selectedImage, null);
					Log.e("",
							"-------------------------------------------------------------"
									+ selectedImage);

					uploadFilePath = selectedImage.toString();

					if (uploadFilePath.startsWith("file://")) {

						uploadFilePath = uploadFilePath.replace("file://", "");
						Bitmap bitmap = getBitmap(selectedImage);
						profile_bm = TribeWireUtil.rotateBitmapZero(
								selectedImage.toString(), bitmap);

						if (profile_bm != null) {
							showAttachImageView();
						}

						try {
							ByteArrayOutputStream bos = new ByteArrayOutputStream();

							boolean isImageWritten = profile_bm.compress(
									Bitmap.CompressFormat.JPEG, 100, bos);
							if (isImageWritten) {

							}

							byte bitmapImage[] = bos.toByteArray();
							File file1 = getTempFile(this);
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

						Log.e("",
								"After-------------------------------------------------------------"
										+ uploadFilePath);

					}

					// handler.sendEmptyMessage(1);
					handler.sendEmptyMessage(2);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		}

		else if (requestCode == ACTIVITY_SELECT_IMAGE
				&& resultCode == RESULT_OK) {
			if (resultCode == RESULT_OK) {

				try {
					selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					Log.e("", "File Path is " + filePath);
					uploadFilePath = filePath;
					cursor.close();
					// profile_bm = getBitmap(selectedImage);

					try {
						profile_bm = getBitmap(selectedImage);
						profile_bm = TribeWireUtil.rotateBitmap(filePath,
								profile_bm);
						if (profile_bm != null) {
							showAttachImageView();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						boolean isImageWritten = profile_bm.compress(
								Bitmap.CompressFormat.JPEG, 100, bos);
						if (isImageWritten) {

						}

						byte bitmapImage[] = bos.toByteArray();
						File file1 = getTempFile(this);
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
				&& resultCode == RESULT_OK) {

			if (resultCode == RESULT_OK) {

				try {

					uploadFilePath = data.getStringExtra(Emergency.AUDIO_DATA);
					Log.e("",
							"-------------------------------------------------------------"
									+ uploadFilePath + " ");

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		} else if (requestCode == 10 && resultCode == RESULT_OK) {

			ArrayList<FriendModel> friendModels = (ArrayList<FriendModel>) data
					.getSerializableExtra("conlistener");
			conversationMessages = friendModels;
			handler2.sendEmptyMessage(UPDATE_RECIPIENTS);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private Bitmap getBitmap(Uri uri) {

		// Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 700000; // 1.2MP
			in = getContentResolver().openInputStream(uri);

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
			in = getContentResolver().openInputStream(uri);
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

	@Override
	protected void onDestroy() {
		Log.e("",
				"=========================== On Destroy Called----------------------------");
		CONV_SCREEN_STATUS = 2;
		super.onDestroy();
	}

	private void updateReadValue() {
		try {
			List<String> nameList = new ArrayList<String>();
			nameList.add("api_key");
			nameList.add("conv_id");
			List<String> valueList = new ArrayList<String>();
			valueList.add(Constants.API_KEY);
			valueList.add(THREAD);
			new RestApiCall(
					ConversationMessages.this,
					CommonFunctions.DEMO_SERVER_URL + CommonFunctions.READ_CONV,
					nameList, valueList).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onError(String onError) {

	}

	@Override
	public void onResponse(String response) {
		Log.e("", "============= The read response is " + response);
		DataBase db = null;
		try {
			db = new DataBase(this);
			db.open();
			String where = "conversation_Id = '" + THREAD + "'";
			Log.e("", "======== Where " + where);
			ContentValues cv = new ContentValues();
			cv.put(DataBase.isRead, "1");
			boolean isUpdate = db.update(DataBase.emergency_table,
					DataBase.emergency_int, where, cv);
			Log.e("", "=========== Status update " + isUpdate);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	String friendImage[];
	ArrayList<SectionHeader> sectionHeaders;
	boolean isSelected[];
	AddParticipantAdp addParticipantAdp = null;
	List<String> participants = null;
	List<String> participantNames = null;
	List<Integer> positionList = null;
	private Button btnBack;
	public int size = 0;
	public String[] FriendName = null;
	public String[] FriendNumber = null;
	public int[] Friendid = null;
	public int[] status = null;
	Cursor friends, c;
	public String[] Groupid = null;

	public void FillFriends() {
		size = 0;
		DataBase db = new DataBase(ConversationMessages.this);
		try {

			db.open();

			friends = db.fetchAll(DataBase.Friends_table, DataBase.Friends_int,
					"name COLLATE NOCASE ", null, null);

			size = friends.getCount();
			if (size != 0) {
				FriendName = new String[size];
				Friendid = new int[size];
				FriendNumber = new String[size];
				// Groupid = new String[size];
				friendImage = new String[size];
				status = new int[size];
				isSelected = new boolean[size];
				int i = 0;
				sectionHeaders = new ArrayList<SectionHeader>();
				String globaltempChar = "";
				while (friends.moveToNext()) {

					FriendName[i] = friends.getString(2);
					FriendNumber[i] = friends.getString(3);
					status[i] = friends.getInt(6);
					Friendid[i] = friends.getInt(0);
					friendImage[i] = friends.getString(4);
					if (isParticipant(Friendid[i], i)) {
						isSelected[i] = true;
						addAvatar(i);

					} else {
						isSelected[i] = false;
					}


					SectionHeader sectionHeader = new SectionHeader();
					String localChar = FriendName[i].substring(0, 1);
					if (i == 0) {
						sectionHeader.setIsShow(true);
						String alphabet = FriendName[i].substring(0, 1);
						sectionHeader.setAlphabet(alphabet.toUpperCase());
						sectionHeaders.add(sectionHeader);
						globaltempChar = alphabet;
					} else {
						if (globaltempChar.toUpperCase().equalsIgnoreCase(
								localChar.toUpperCase())) {
							SectionHeader sectionHeader2 = new SectionHeader();
							sectionHeader2.setAlphabet(null);
							sectionHeader2.setIsShow(false);
							sectionHeaders.add(sectionHeader2);
							globaltempChar = localChar;
						} else {
							SectionHeader sectionHeader2 = new SectionHeader();
							String alphabet = FriendName[i].substring(0, 1);
							sectionHeader2.setAlphabet(alphabet.toUpperCase());
							sectionHeader2.setIsShow(true);

							globaltempChar = localChar;
							sectionHeaders.add(sectionHeader2);
						}
					}

					i++;
				}

				// lvc = this.getListView();

				addParticipantAdp = new AddParticipantAdp(this, FriendName,
						status, Groupid, FriendNumber, friendImage,
						sectionHeaders, isSelected);
				addParticipantList.setAdapter(addParticipantAdp);
				addParticipantList.setCacheColorHint(Color.WHITE);

				addParticipantList.setRecyclerListener(new RecyclerListener() {

					@Override
					public void onMovedToScrapHeap(View view) {

						try {
							ImageView imageView = (ImageView) view
									.findViewById(R.id.contact_pic);
							imageView.setImageDrawable(null);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			} else {

				addParticipantList.setAdapter(new SimpleAdapter(this,
						getString(R.string.no_contact)));
				addParticipantList.setCacheColorHint(Color.WHITE);
				addParticipantList.setOnItemClickListener(null);

			}
			friends.close();
		} catch (Exception e) {
			if (pd != null)
				pd.dismiss();
			Debugger.debugE("Error in Fill Group " + e.getMessage());
		} finally {
			db.close();
			if (pd != null)
				pd.dismiss();

		}
		// TenFourTab.loadAds(getBaseContext());
	}

	private boolean isParticipant(int friendId, int position) {
		boolean isPartcipent = false;
		try {

			for (int i = 0; i < conversationMessages.size(); i++) {
				FriendModel friendModel = conversationMessages.get(i);
				try {
					if (Integer.parseInt(friendModel.getFriendId()) == friendId) {
						isPartcipent = true;
						if (friendModel.getFriendStatus().equalsIgnoreCase("5")
								|| friendModel.getFriendStatus()
										.equalsIgnoreCase("6")) {
							try {
								String friendNumber = friendModel
										.getFriendNumber();
								String base64Number = new String(
										Base64.decode(friendNumber.getBytes(),
												Base64.DEFAULT));
								participants.add("-g_" + base64Number + "-");
								participantNames.add(friendModel
										.getFriendName());
								positionList.add(position);

							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							try {
								String friendNumber = friendModel
										.getFriendNumber();
								participants.add("-" + friendNumber + "-");
								participantNames.add(friendModel
										.getFriendName());
								positionList.add(position);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						break;
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isPartcipent;

	}

	private void addParticipantsToServer() {
		try {
			String allParticipants = "";
			if (participants != null && participants.size() > 0) {
				for (int i = 0; i < participants.size(); i++) {
					allParticipants += participants.get(i) + ",";
				}

				if (allParticipants != null && allParticipants.length() > 0
						&& allParticipants.endsWith(",")) {
					allParticipants = allParticipants.substring(0,
							allParticipants.length() - 1);
					Log.e("", "=========== The All Participants are "
							+ allParticipants);
				}

				if (CommonFunctions.isInternetConnected(this)) {
					progressDialog = ProgressDialog.show(
							ConversationMessages.this, "Updating Recipients",
							"Please wait while update your information");
					String url = CommonFunctions.DEMO_SERVER_URL
							+ TribeWireConstants.ADD_PARTICIPANTS;
					List<String> nameList = new ArrayList<String>();
					nameList.add(Constants.API_KEY_TAG);
					nameList.add(Constants.REF_ID_TAG);
					nameList.add(Constants.CONV_ID_TAG);
					nameList.add(Constants.SESSION_TAG);
					nameList.add("recipients");
					Log.e("",
							"========= The Name List is " + nameList.toString());
					List<String> valueList = new ArrayList<String>();
					valueList.add(Constants.API_KEY);
					String userId = CommonFunctions.getPref(this,
							CommonFunctions.REF_ID, "");
					valueList.add("" + userId);
					valueList.add(THREAD);
					valueList.add(CommonFunctions.getPref(
							ConversationMessages.this, Constants.SESSION, ""));
					Log.e("",
							"========= The Value List is "
									+ valueList.toString());

					// TODO need to add recipients
					valueList.add(Base64.encodeToString(
							allParticipants.getBytes(), Base64.DEFAULT));
					new RestApiCall(ConversationMessages.this, url, nameList,
							valueList).start();

				} else {
					Toast.makeText(ConversationMessages.this,
							"Please check your internet connectivity",
							Toast.LENGTH_LONG).show();
				}
			}

			else {
				finish();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ConversationMessages.this,
					"Error while updating , Please try again later",
					Toast.LENGTH_LONG).show();
			dismissProgress();
			finish();
		}

	}

	private void dismissProgress() {
		try {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public IHttpRequest particpantsResponse = new IHttpRequest() {

		@Override
		public void onResponse(String response) {
			Log.e("", "============ The response from server is " + response);

			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONObject jsonObject2 = jsonObject.getJSONObject("response");
				String error = jsonObject2.getString("error");
				if (error.equalsIgnoreCase("false")) {
					updateRecipients();
					dismissProgress();
					conversationMessages = prepareNewFriendData(positionList);
					handler2.sendEmptyMessage(UPDATE_RECIPIENTS);
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
				Toast.makeText(ConversationMessages.this,
						" Error while updating recipient", Toast.LENGTH_LONG)
						.show();
			}

		}

		@Override
		public void onError(String onError) {
			Log.e("", "============ The Error  response from server  "
					+ onError);

			dismissProgress();
		}
	};

	private void updateRecipients() {
		DataBase db = null;
		try {
			String participantNames = "";
			if (this.participantNames != null
					&& this.participantNames.size() > 0) {
				for (int i = 0; i < this.participantNames.size(); i++) {
					participantNames += this.participantNames.get(i) + ",";
				}
			}

			if (participantNames != null && participantNames.length() > 0) {
				participantNames = participantNames.substring(0,
						participantNames.length() - 1);
			}
			Log.e("", "============ The Participant Names are "
					+ participantNames);

			if (participantNames != null && participantNames.length() > 0
					&& THREAD != null && THREAD.length() > 0) {
				db = new DataBase(this);
				db.open();
				ContentValues cv = new ContentValues();
				cv.put("receiver", participantNames);
				boolean isUpdate = db.update(DataBase.Receive_table,
						DataBase.Receive_int, "conversation_Id = '" + THREAD
								+ "'", cv);
				Log.e("", "======== is Receiver Updated " + isUpdate);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}

	}

	public ArrayList<FriendModel> prepareNewFriendData(
			List<Integer> positinoList) {
		FriendModel friendModel = null;
		ArrayList<FriendModel> friendData = null;
		if (positinoList != null && positinoList.size() > 0) {
			friendData = new ArrayList<FriendModel>();
		}
		for (int position = 0; position < positinoList.size(); position++) {

			try {
				friendModel = new FriendModel();
				friendModel.setFriendId(""
						+ Friendid[positinoList.get(position)]);
				friendModel
						.setFriendName(FriendName[positinoList.get(position)]);
				friendModel.setFriendNumber(FriendNumber[positinoList
						.get(position)]);
				friendModel.setFriendStatus(""
						+ status[positinoList.get(position)]);
				friendModel.setImgUrl(friendImage[positinoList.get(position)]);
				friendData.add(friendModel);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {

			Log.e("", "============== The size of the Friend Data is "
					+ friendData.size() + " ========================== ");
			Intent intent = new Intent();
			intent.putExtra("conlistener", friendData);

			setResult(RESULT_OK, intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return friendData;

	}

}
