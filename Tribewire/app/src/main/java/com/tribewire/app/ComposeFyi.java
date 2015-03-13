package com.tribewire.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
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
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eyesay.custommenu.CustomMenu;
import com.app.eyesay.custommenu.CustomMenu.OnMenuItemSelectedListener;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.Utils.UploadFiles;
import com.eyesayapp.Utils.Utils;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;

public class ComposeFyi extends Activity implements OnClickListener {

	private EditText edtSubject;
	private EditText edtExpires;
	private EditText edtLink;
	private EditText edtMessage;
	private Button imgSendFyi;
	private ImageButton imgAttachMedia;
	private final int ACTIVITY_SELECT_IMAGE = 211;
	private final int ACTIVITY_CAPTURE_IMAGE = 212;
	private final int ACTIVITY_CAPTURE_AUDIO = 313;
	public static final String IMAGE_DATA = "image_data";
	public static final String IMAGE_URI = "image_uri";
	Uri imageUri;
	private String uploadFilePath = "";
	RelativeLayout rltImageAttachView;
	ImageView imgAttachPic;
	private LinearLayout conRecordingLayout;
	private TextView txtSecondsPlayed;
	private ProgressBar progressRecording;
	private Button txtPlay;
	private Button txtRecord;
	private Button txtCancelRecording;
	String expireDate = "";
	int year = -1;
	int monthOfYear = -1;
	int dayOfMonth = -1;
	private ScrollView scrollView;
	private ImageButton imgBackButton;
	private boolean isFocus = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_compose_fyi);
		edtSubject = (EditText) findViewById(R.id.edt_subject);
		edtExpires = (EditText) findViewById(R.id.edt_expires);
		Calendar c = Calendar.getInstance();		
		year = c.get(Calendar.YEAR);
		monthOfYear = c.get(Calendar.MONTH);
		dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		expireDate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
		//edtExpires.init(year, monthOfYear, dayOfMonth, onDateChangedListener);
		edtExpires.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isFocus = false;
					showDialog(999);
				} else {
					isFocus = true;
				}

			}
		});
		
		edtExpires.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(999);
				
			}
		});
		
		edtLink = (EditText) findViewById(R.id.edt_link);
		edtMessage = (EditText) findViewById(R.id.edt_message);
		edtLink.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isFocus = false;
				} else {
					isFocus = true;
				}

			}
		});
		edtSubject.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isFocus = false;
				} else {
					isFocus = true;
				}

			}
		});

		imgAttachMedia = (ImageButton) findViewById(R.id.img_btn_media);
		imgSendFyi = (Button) findViewById(R.id.btn_send_fyi);
		rltImageAttachView = (RelativeLayout) findViewById(R.id.rlt_image_attach_view);
		imgAttachPic = (ImageView) findViewById(R.id.img_selected_image);
		scrollView = (ScrollView) findViewById(R.id.scrollview);
		imgBackButton = (ImageButton) findViewById(R.id.messagelist_back);
		imgBackButton.setOnClickListener(this);
		imgAttachMedia.setOnClickListener(this);
		imgSendFyi.setOnClickListener(this);
		initializeRecordingUi();
		edtMessage.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// Toast.makeText(getApplicationContext(), "got the focus",
					// Toast.LENGTH_LONG).show();
					if (isFocus == true) {
						sendScroll();
						TribeWireUtil.showKeyboard(ComposeFyi.this, edtMessage);
					}
					// sendScroll();

					// TribeWireUtil.showKeyboard(ComposeFyi.this, edtMessage);
				} else {

					isFocus = false;
					// TribeWireUtil.hideKeyboard(ComposeFyi.this, edtMessage);
				}
			}
		});

		super.onCreate(savedInstanceState);
	}

	
	public Handler handler2 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
				 TribeWireUtil.showNotificationAlert(ComposeFyi.this, emergencyMessage, "Emergency");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		};
	OnDateChangedListener onDateChangedListener = new OnDateChangedListener() {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Log.e("", "============ Selected Year " + year + " Selected Month "
					+ monthOfYear + " Selected Day of Month " + dayOfMonth);

			expireDate = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
			ComposeFyi.this.year = year;
			ComposeFyi.this.monthOfYear = monthOfYear;
			ComposeFyi.this.dayOfMonth = dayOfMonth;

		}
	};

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

	@Override
	public void onClick(View v) {
		if (v == imgSendFyi) {
			if (!(edtSubject.getText().toString().length() > 0
					&& edtMessage.getText().toString().length() > 0 && expireDate
					.length() > 0)) {
				Toast.makeText(ComposeFyi.this,
						" All enter subject title and message  ",
						Toast.LENGTH_LONG).show();
				return;
			} else if (!(edtSubject.getText().toString().length() > 0)) {
				Toast.makeText(ComposeFyi.this, " Please enter subject name ",
						Toast.LENGTH_LONG).show();
				return;
			}

			else if (!(edtMessage.getText().toString().length() > 0)) {
				Toast.makeText(ComposeFyi.this, " Please enter message ",
						Toast.LENGTH_LONG).show();
				return;
			}

			else if (edtSubject.getText().toString().length() > 0
					&& edtMessage.getText().toString().length() > 0

					&& expireDate.length() > 0) {
				if (uploadFilePath!=null && uploadFilePath.length()>0) {
					  if (CommonFunctions.isInternetConnected(this)) {
						  pd = ProgressDialog.show(ComposeFyi.this, "Sending .... ",
									"Please wait while composing FYI");
							pd.setCancelable(true);
					}
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							Looper.prepare();
							sendFyi();
							Looper.loop();
							
						}
					}).start();
				}
				else {
					  if (CommonFunctions.isInternetConnected(this)) {
						  pd = ProgressDialog.show(ComposeFyi.this, "Sending .... ",
									"Please wait while composing FYI");
							pd.setCancelable(true);
					}
					sendFyi();
				}
			}

		} else if (v == imgAttachMedia) {
			showMenu();
		} else if (v == imgBackButton) {
			finish();
		}
	}

	
	private String uploadFile(String baseFileUrl){
		
		String Response  = "";
		String file_url = "";
		UploadFiles files = new UploadFiles();
		String Response1="";
		try {
			Response1 = files.SendRecord(baseFileUrl,
					this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// TYPE = 3 // Its an image
		// TYPE = 0 // Its an text message only
		// TYPE = 2 // Its an Audio File
		if (Response1 != null && Response1.length() > 0) {

			try {
				// JSONObject jsonObject = new JSONObject(
				// Response1);

				JSONObject jsonObject1 = new JSONObject(
						Response1);
				JSONObject jsonObject = jsonObject1
						.getJSONObject("response");
				String error = jsonObject.getString("error");
				if (error.equalsIgnoreCase("false")) {
					String fileName = jsonObject
							.getString("file_name");
					Log.e("",
							"======== The Video file Name is "
									+ fileName
									+ "==============");		
					
					
					file_url= fileName;
				} else {
					Log.e("",
							"============Error while uploading Video to server==========");
					Response = "";

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		return file_url;
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
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

			File photo = getTempFile(this);
			photo.delete();
			photo = getTempFile(this);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
			imageUri = Uri.fromFile(photo);
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

	private static int SHOW_FYI_TOAST = 6;
	private static int SHOW_ERROR_TOAST = 7;
	private static int SHOW_TEXT = 8;
	private static int SERVER_ERROR = 9;
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
			} else if (msg.what == SHOW_FYI_TOAST) {
				try {
					Toast.makeText(ComposeFyi.this,
							" Message sent successfully ", Toast.LENGTH_LONG)
							.show();
					setResult(RESULT_OK);
					finish();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == SHOW_ERROR_TOAST) {
				try {
					Toast.makeText(ComposeFyi.this,
							" Error while sending Fyi ", Toast.LENGTH_LONG)
							.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (msg.what == SERVER_ERROR){
				try {
					Toast.makeText(ComposeFyi.this,
							" Server error , Please try after some time", Toast.LENGTH_LONG)
							.show();

				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			else if (msg.what == SHOW_TEXT){
			try {
				edtExpires.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
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

					getContentResolver().notifyChange(selectedImage, null);
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

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String TAG = this.getClass().getSimpleName();

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

	int length = 0;
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
							Toast.makeText(ComposeFyi.this,
									"Please record some file before play",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {

						e.printStackTrace();
					}
				} else if (isPlayingCurrently) {
					Toast.makeText(ComposeFyi.this,
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
							ComposeFyi.this,
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
					ComposeFyi.this);

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

	ProgressDialog pd = null;

	private void sendFyi() {

		try {
			if (CommonFunctions.isInternetConnected(ComposeFyi.this)) {
				
				List<String> nameList = null;

				nameList = new ArrayList<String>();
				nameList.add(Constants.API_KEY_TAG);
				nameList.add(Constants.REF_ID_TAG);
				nameList.add(Constants.COMP_ID_TAG);
				nameList.add(Constants.SESSION_TAG);
				nameList.add(Constants.FYI_SUBJECT);
				nameList.add(Constants.FYI_DATE);
				nameList.add(Constants.FYI_TEXT);
				if (uploadFilePath!=null && uploadFilePath.length()>0) {
					nameList.add("attachment");
					nameList.add("attachment_type");
					}
				List<String> valueList = new ArrayList<String>();				
				String api_key = Constants.API_KEY;
				String ref_id = CommonFunctions
						.getPref(this, CommonFunctions.REF_ID, "");
				String com_id = CommonFunctions
						.getPref(this, Constants.COM_ID, "");	
				
				String session = CommonFunctions
						.getPref(this, Constants.SESSION, "");
								
			    valueList.add(api_key);
			    valueList.add(ref_id);
			    valueList.add(com_id);
			    valueList.add(session);			
			    valueList.add(edtSubject.getText().toString());
				valueList.add("" + expireDate);
				valueList.add(edtMessage.getText().toString()+" <a href="+edtLink.getText().toString()+">"+edtLink.getText().toString()+"</a>");
				String serverFileName  = "";
				if (uploadFilePath!=null && uploadFilePath.length()>0) {
					serverFileName =	uploadFile(uploadFilePath);
				if (serverFileName != null && serverFileName.length() > 2) {
					valueList.add(
							serverFileName);

					String attachment_type = "2";
					if (serverFileName.endsWith(".mp4")) {
						attachment_type = "1";
					} else if (serverFileName.endsWith(".mp3")) {
						attachment_type = "2";
					} else if (serverFileName.endsWith(".jpg")
							|| serverFileName.endsWith(".jpeg")
							|| serverFileName.endsWith(".png")
							|| serverFileName.endsWith(".gif")) {
						attachment_type = "3";
					}

					Log.e("", "================   The Attachment type is  "
							+ attachment_type);
					valueList.add(
							attachment_type);
				}
				}
				
				new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
						+ TribeWireConstants.COMPOSE_FYI_URL, nameList,
						valueList).start();
			} else {
				CommonFunctions.showAlert(ComposeFyi.this,
						"Please check your internet connectivity",
						"Network Error");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {
		ActivityStatus.setActivity(this);
		super.onResume();
	}
	@Override
	public void onPause() {
		ActivityStatus.setActivity(null);
		super.onPause();
	}
	public IHttpRequest composeFyi = new IHttpRequest() {

		/**
		 * {"response":{"error":false,"error_msg":"FYI Sent Successfully!",
		 * "conv_id":133}}
		 */
		@Override
		public void onResponse(String response) {
			Log.e("", "============ The Compose Fyi Response is " + response);
			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONObject errorJson = jsonObject.getJSONObject("response");
				String error = errorJson.getString("error");
				if (error.equalsIgnoreCase("false")) {
					String errorMsg = errorJson.getString("error_msg");

					if (errorMsg.equalsIgnoreCase("FYI Sent Successfully!")) {
						if (pd != null) {
							pd.dismiss();
						}
						handler.sendEmptyMessage(SHOW_FYI_TOAST);
					}
					else {
						if (pd != null) {
							pd.dismiss();
						}
						handler.sendEmptyMessage(SERVER_ERROR);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (pd != null) {
						pd.dismiss();
					}
					handler.sendEmptyMessage(SERVER_ERROR);
				} catch (Exception e2) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onError(String onError) {
			try {
				if (pd != null) {
					pd.dismiss();
				}
				handler.sendEmptyMessage(SHOW_ERROR_TOAST);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	private void sendScroll() {
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						scrollView.fullScroll(View.FOCUS_DOWN);
						edtMessage.requestFocus();
					}
				});
			}
		}).start();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(this, dateSetListener, year, month, day);

	}

	OnDateSetListener dateSetListener = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			try {
				Log.e("", "============ Selected Year " + year
						+ " Selected Month " + monthOfYear
						+ " Selected Day of Month " + dayOfMonth);

				expireDate = "" + year + "-" + (monthOfYear + 1) + "-"
						+ dayOfMonth;
				ComposeFyi.this.year = year;
				ComposeFyi.this.monthOfYear = (monthOfYear+1);
				ComposeFyi.this.dayOfMonth = dayOfMonth;
				handler.sendEmptyMessage(SHOW_TEXT);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

}
