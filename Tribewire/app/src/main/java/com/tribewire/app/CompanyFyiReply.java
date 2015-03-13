package com.tribewire.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.lang3.text.translate.LookupTranslator;

import com.app.eyesay.custommenu.CustomMenu;
import com.app.eyesay.custommenu.CustomMenu.OnMenuItemSelectedListener;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.model.CompanyMessages;
import com.tribewire.app.Emergency.ThreadPP;

import eyesay.obj.Friend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CompanyFyiReply extends Activity implements OnClickListener {

	CompanyMessages companyMessages;
	Button imgOpenMedia;
	String uploadFilePath = "";
	private final int ACTIVITY_SELECT_IMAGE = 211;
	private final int ACTIVITY_CAPTURE_IMAGE = 212;
	private final int ACTIVITY_CAPTURE_AUDIO = 313;

	public static final String IMAGE_DATA = "image_data";
	public static final String IMAGE_URI = "image_uri";
	public static final String VIDEO_DATA = "video_data";
	public static final String AUDIO_DATA = "audio_data";
	Uri imageUri;
	private ImageView imgCapture;
	private RelativeLayout rltAudio;
	private ProgressBar progressBar;
	private TextView txtAudioCount;
	private ImageView btnPlayPause;
	private Button msg_txt_send;
	private Button msg_txt_back;
	private EditText edtTextMessage;
	private MultiAutoCompleteTextView mcV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_company_message);
		msg_txt_send = (Button) findViewById(R.id.text_send);
		imgCapture = (ImageView) findViewById(R.id.img_capture);
		rltAudio = (RelativeLayout) findViewById(R.id.rlt_audio);
		progressBar = (ProgressBar) findViewById(R.id.progressBarAudio);
		txtAudioCount = (TextView) findViewById(R.id.audio_seconds);
		btnPlayPause = (ImageView) findViewById(R.id.img_audio_message);
		imgOpenMedia = (Button) findViewById(R.id.open_media);
		msg_txt_back = (Button) findViewById(R.id.text_back);
		edtTextMessage = (EditText) findViewById(R.id.msg_editText1);
		mcV   = (MultiAutoCompleteTextView)findViewById(R.id.contactNames);
		msg_txt_send.setOnClickListener(this);
		msg_txt_back.setOnClickListener(this);
		imgOpenMedia.setOnClickListener(this);
		btnPlayPause.setOnClickListener(this);
		try {

			companyMessages = (CompanyMessages) getIntent()
					.getSerializableExtra("replyData");
		} catch (Exception e) {
			e.printStackTrace();
		}
		getFriendName(companyMessages.getSenderId());
		super.onCreate(savedInstanceState);
	}

	private void getFriendName(String friendId) {
		DataBase db = null;
		try {
			db = new DataBase(this);
			db.open();
			
			Cursor cursor  = db.fetch(DataBase.Friends_table, DataBase.Friends_int, Friend.NUMBER+" = '"+new String(Base64.decode(friendId.getBytes(), Base64.DEFAULT))+"'");
			Log.e("", "========= Cursor Count is "+cursor.getCount()+"=== Friend Id "+friendId);
			 if (cursor!=null && cursor.getCount()>0) {
				   String friendName  = cursor.getString(2);
				   mcV.setEnabled(false);
				   mcV.setText("To: "+friendName);
				   
			}
			//Log.e("", "============ Is Status  updated " + isUpdated);
			 cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

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

	// --------------------------------------------//

	private String TAG = this.getClass().getSimpleName();

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

	// Records Voice Using Eyesay Voice UI
	private void addVoice() {
		try {
			Intent intent = new Intent(this, TenFourRecordVoice.class);
			startActivityForResult(intent, ACTIVITY_CAPTURE_AUDIO);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// Takes Photo Using Default Camera
	private void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		File photo = getTempFile(this);
		photo.delete();
		photo = getTempFile(this);
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

	Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					if (imageUri != null) {
						// showContainer();
						// imgCaptureImage.setImageURI(imageUri);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 2) {
				try {
					if (profile_bm != null) {
						// showContainer();
						// imgCaptureImage.setImageBitmap(profile_bm);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == 3) {
				if (pd!=null) {
					pd.dismiss();
				}
				Toast.makeText(CompanyFyiReply.this,
						"Message sent successfully", Toast.LENGTH_SHORT).show();
				finish();
			}

			else if (msg.what == 4) {
				if (pd!=null) {
					pd.dismiss();
				}
				Toast.makeText(CompanyFyiReply.this,
						"Error while reply to user , Try again later",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	boolean downlodflag = true;
	boolean playflag = false;
	boolean pauseFlag = false;

	private boolean mediaCompleted = false;
	private boolean mediaFirstTime = true;
	MediaPlayer mp = null;
	int move = 0;
	int temp = 0;
	int sec = 0;

	final Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (move < 10) {
				txtAudioCount.setText("00:0" + move);
			}

			else {
				txtAudioCount.setText("00:" + move);
			}
			temp++;
			try {
				progressBar.setProgress((int) (((temp) * 100) / sec));
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	File f = null;

	private void playMyAudioFile(String fileName, final boolean autodownlod) {

		Log.e("", "========== The File name is " + fileName);
		try {
			mp = new MediaPlayer();
			try {
				mp.setDataSource(fileName);
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setVolume(15, 15);
			try {
				mp.prepare();
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
			mp.start();

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

			threadPP  = new ThreadPP();
//			Thread t = new Thread() {
//				public void run() {
//					while (move > 0) {
//						move--;
//						
//						handler.sendEmptyMessage(1);
//						try {
//							sleep(1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//
//					}
//				}
//			};
			//t.start();
			playflag = true;
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Debugger.debugE("on completion method..");
					move = 0;
					temp=0;
					if (mp != null) {
						mp.stop();
						mp.release();
					}

					playflag = false;
					mediaCompleted = true;
					mp = null;
					CompanyFyiReply.this.mp = null;
					handler3.sendEmptyMessage(4);
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
					// finish();
				}

			});

		} catch (Exception e) {
			CommonFunctions.showAlert(CompanyFyiReply.this,
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
			}
			else if (msg.what==4) {
				btnPlayPause.setBackgroundResource(R.drawable.videoplay);
				progressBar.setProgress(0);
			}
		};
	};

	Uri selectedImage = null;
	Bitmap profile_bm = null;

	Handler handler4 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					if (imageUri != null) {
						showImageContainer();
						imgCapture.setImageURI(imageUri);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 2) {
				try {
					if (profile_bm != null) {
						showImageContainer();
						imgCapture.setImageURI(selectedImage);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (msg.what ==3) {
				showAudioContainer();
			}
			else if (msg.what==4) {
				showAudioContainer();
			}
			
		}

	};

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
						Log.e("",
								"After-------------------------------------------------------------"
										+ uploadFilePath);

					}

					handler4.sendEmptyMessage(1);
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
					profile_bm = getBitmap(selectedImage);
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

						handler4.sendEmptyMessage(1);
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

					 handler4.sendEmptyMessage(3);

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	int length = 0;

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

	ThreadPP threadPP;
	@Override
	public void onClick(View v) {
		if (v == imgOpenMedia) {
			showMenu();
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
				
				threadPP  = new ThreadPP();
				
				//threadPP.resumeThread();

			}

			
		} else if (v == msg_txt_back) {
			finish();
		} else if (v == msg_txt_send) {

			
			if (CommonFunctions.isInternetConnected(CompanyFyiReply.this)) {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						Looper.prepare();
						sendMessage();
						Looper.loop();

					}
				});
				thread.start();				
			}
			else {
				CommonFunctions.showAlert(CompanyFyiReply.this, "Please check your internet connectivity", "Network Error");
			}
			


		}

	}
	ProgressDialog pd ;
	private void sendMessage() {
		try {
			pd = ProgressDialog.show(CompanyFyiReply.this, "Sending...", "Please wait while sending your reply");
			String names = companyMessages.getSenderId();	
			boolean isBroadCast  = false;
			if (CommonFunctions.SendTextMessage(
					CompanyFyiReply.this,
					false,
					uploadFilePath,
					new String(Base64.decode(companyMessages.getSenderId()
							.getBytes(), Base64.DEFAULT)), names, false, null,
					edtTextMessage.getText().toString(),isBroadCast)) {

				handler1.sendEmptyMessage(3);

			} else {
				handler1.sendEmptyMessage(4);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	boolean isRunning = true;
	
	class ThreadPP implements Runnable {

		public ThreadPP() {
        Thread thread  = new Thread(this);
        thread.start();
        
        isRunning = true;;
		}

		public synchronized void pauseThread() {
			isRunning = false;
			try {
				//this.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public synchronized void resumeThread() {
			isRunning = true;
			try {
				//this.notify();
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
				handler.sendEmptyMessage(1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		
		
			
		}

	}
	
	@Override
	protected void onPause() {
		try {
			if (mp != null) {
				mp.stop();
				mp.release();
				move = 0;
				temp= 0;
				playflag = false;
				if (threadPP!=null) {
					threadPP.pauseThread();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}
}
