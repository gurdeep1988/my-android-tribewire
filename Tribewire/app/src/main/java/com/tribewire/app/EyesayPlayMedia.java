package com.tribewire.app;



import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Debugger;

public class EyesayPlayMedia extends Activity implements OnClickListener {

	private Button btn_play_media_back;
	private int messageType = 0;
	private int type = 0;
	private String receiver = "";
	private String URL = "";
	private TextView txt_media_type_text;
	private TextView txt_audio_seconds;
	private ProgressBar progress_audio_seconds;
	private RelativeLayout realtive_layout_audio;
	private TextView textView;
    private File f=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.playmedia);
		textView = (TextView) findViewById(R.id.audio_seconds);
		super.onCreate(savedInstanceState);
		btn_play_media_back = (Button) findViewById(R.id.btn_play_media_back);
		txt_media_type_text = (TextView) findViewById(R.id.txt_media_msg);
		txt_audio_seconds = (TextView) findViewById(R.id.audio_seconds);
		progress_audio_seconds = (ProgressBar) findViewById(R.id.audio_progressBar);
		realtive_layout_audio = (RelativeLayout) findViewById(R.id.audio_relative_layout);
		btn_play_media_back.setOnClickListener(this);

		try {

			messageType = getIntent().getExtras().getInt("messagetype", 0);
			type = getIntent().getExtras().getInt("type", 0);
			URL = getIntent().getExtras().getString("url");
			receiver = getIntent().getExtras().getString("receiver");

		} catch (Exception e) {
			e.printStackTrace();
		}

		final String fileName = URL.substring(URL.lastIndexOf('/') + 1);
		if (messageType == 0) {
			// Audio
			if (realtive_layout_audio.getVisibility() == View.INVISIBLE) {
				realtive_layout_audio.setVisibility(View.VISIBLE);
			}
			txt_media_type_text.setText("Streaming Audio");
			try {
				  f = new File(CommonFunctions.getAudioURL()
						+ fileName);
				if (!f.exists()) {
					playAudio(type, URL, receiver);
					setResult(RESULT_OK,
							getIntent().putExtra("type", messageType));
				} else {
					// callMenu(messageType, receiver, false);
					playAudio(type, URL, receiver);
					// playMyAudioFile(type, CommonFunctions.getAudioURL()
					// + fileName, receiver, true);
					setResult(RESULT_OK,
							getIntent().putExtra("type", messageType));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Video
			try {
				txt_media_type_text.setText("Streaming Video");
				  f = new File(CommonFunctions.getVideoURL()
						+ fileName);
				if (!f.exists()) {
					playVideo(type, URL, receiver);
					setResult(RESULT_OK,
							getIntent().putExtra("type", messageType));
				} else {
					// callMenu(messageType, Receiver, false);
					playVideo(type, URL, receiver);
					setResult(RESULT_OK,
							getIntent().putExtra("type", messageType));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v == btn_play_media_back) {
			finish();
		}
	}

	boolean downlodflag = true;
	boolean playflag = true;

	public void playAudio(final int type, final String URL,
			final String Receiver) {
		final String fileName = URL.substring(URL.lastIndexOf("/") + 1);
 
		try {
			playflag = true;
			final File f = new File(CommonFunctions.getAudioURL() + fileName);

			Debugger.debugE("file not found need to download");
			final ProgressDialog pd = ProgressDialog.show(this, "",
					"Retrieving..");
			new Thread() {
				public void run() {
					Looper.prepare();
					downlodflag = true;
					boolean AutoDownload = false;

					if (!f.exists()) {
						try {
							if (CommonFunctions.downloadFile(URL,
									CommonFunctions.getAudioURL() + fileName)) {
								
							}
							else {
								CommonFunctions.downloadFileFromAmazon(URL,
										CommonFunctions.getAudioURL() + fileName);
							}
							downlodflag = true;
						} catch (IOException e) {
							downlodflag = false;
							e.printStackTrace();
							playflag = false;
						} finally {
							pd.dismiss();
						}
						AutoDownload = true;
					} else {
						pd.dismiss();
					}
					if (downlodflag) {
						try {
							// playAudioFile(type, CommonFunctions.getAudioURL()
							// + fileName, Receiver, AutoDownload);
							final boolean tempAuto = AutoDownload;
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										playMyAudioFile(type,
												CommonFunctions.getAudioURL()
														+ fileName, Receiver,
												tempAuto);
									} catch (IOException e) {
										e.printStackTrace();
									}

								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							playflag = false;
						}
					}
					Looper.loop();
				}
			}.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	MediaPlayer mp = null;
	int move = 0;
	int temp = 0;
	int sec = 0;

	final Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (move < 10) {
				textView.setText("00:0" + move);
			}

			else {
				((TextView) findViewById(R.id.audio_seconds)).setText("00:"
						+ move);
			}
			temp++;
			progress_audio_seconds.setProgress((int) (((temp) * 100) / sec));
		};
	};

	private void playMyAudioFile(final int type, String fileName,
			final String Receiver, final boolean autodownlod) {

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
			mp.setAudioStreamType(AudioManager.STREAM_SYSTEM);
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
			sec = mp.getDuration() / 1000;
			if (sec > 60)
				sec = 60;
			CommonFunctions.setFont(EyesayPlayMedia.this, txt_audio_seconds,
					false, false);
			// txt_audio_seconds.setText("Duration: 00:"
			// + ((sec <= 9) ? "0" + sec : sec));

			move = sec;
			Log.e("", "===================" + move);
			// new Handler().post(runnable);

			Thread t = new Thread() {
				public void run() {
					while (move > 0) {
						move--;
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
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				}
			};
			t.start();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Debugger.debugE("on completion method..");
					move = 0;
					if (mp != null)
						mp.release();

					playflag = false;
					mp = null;
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
					finish();
				}
				
			});

		} catch (Exception e) {
			CommonFunctions.showAlert(EyesayPlayMedia.this,
					"Error whle Playing Media", "Error");

			Debugger.debugE("error " + e.getMessage());

			mp.release();
			mp = null;

			playflag = false;

		}
	}

	public void playAudioFile(final int type, String fileName,
			final String Receiver, final boolean autodownlod) {
		final Handler handle = new Handler();
		final Dialog a = new Dialog(EyesayPlayMedia.this);

		try {
			a.requestWindowFeature(Window.FEATURE_NO_TITLE);
			a.setContentView(R.layout.playaudio);
			a.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			final Window w = a.getWindow();
			CommonFunctions.setFont(EyesayPlayMedia.this,
					((TextView) w.findViewById(R.id.lblplay)), false, false);
			final TextView duration = (TextView) w.findViewById(R.id.duration);
			Button btnStop = (Button) w.findViewById(R.id.btnStopAudio);
			btnStop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					move = -1;

					try {
						if (mp.isPlaying()) {
							mp.stop();
						}
					} catch (Exception e) {
					} finally {

						if (mp != null)
							mp.release();
						mp = null;
						a.dismiss();
						playflag = false;
						if (autodownlod) {
							// TODO TOAST HAS TO ADD
							// callMenu(type, Receiver, autodownlod);
						}
					}

				}
			});
			a.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					move = -1;

					try {
						if (mp.isPlaying()) {
							mp.stop();

						}
					} catch (Exception e) {
					} finally {
						if (mp != null)
							mp.release();
						mp = null;
						a.dismiss();
						playflag = false;
						if (autodownlod) {
							// TODO TOAST HAS TO ADD
							// callMenu(type, Receiver, autodownlod);
						}
					}

				}
			});
			a.show();
			mp = new MediaPlayer();
			mp.setDataSource(fileName);
			mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
			// mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setVolume(1, 1);
			mp.prepare();
			mp.start();
			int sec = mp.getDuration() / 1000;
			if (sec > 30)
				sec = 30;
			CommonFunctions.setFont(EyesayPlayMedia.this, duration, false,
					false);
			duration.setText("Duration: 00:" + ((sec <= 9) ? "0" + sec : sec));

			move = sec;

			Thread t = new Thread() {
				public void run() {
					while (move > 0) {
						handle.post(new Runnable() {
							public void run() {
								if (move >= 0) {
									((TextView) a.findViewById(R.id.duration))
											.setText("Duration: 00:"
													+ ((move <= 9) ? "0" + move
															: move));

								}
							}
						});
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						move--;
					}
				}
			};
			t.start();

			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Debugger.debugE("on completion method..");
					move = 0;
					if (mp != null)
						mp.release();
					a.dismiss();
					playflag = false;
					mp = null;
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
				}
			});

		} catch (Exception e) {
			Debugger.debugE("error " + e.getMessage());

			mp.release();
			mp = null;
			if (a != null)
				a.dismiss();
			playflag = false;
			if (autodownlod) {
				// TODO TOAST HAS TO ADD
				// callMenu(type, Receiver, autodownlod);
			}
		}

	}

	String temp_Receiver = "";
	int temp_Type = 0;

	public void playVideo(final int type, final String URL,
			final String Receiver) {
		final String VfileName = URL.substring(URL.lastIndexOf("/") + 1);

		try {
			final File f = new File(CommonFunctions.getVideoURL() + VfileName);

			if (!f.exists()) {
				Debugger.debugE("file not found need to download");
				final ProgressDialog pd = ProgressDialog.show(this, "",
						"Retrieving..");
				new Thread() {
					public void run() {
						Looper.prepare();
						if (!f.exists()) {
							try {
								if (CommonFunctions.downloadFile(URL,
										CommonFunctions.getVideoURL()
										+ VfileName)) {
//									Log.e("", "File Length after downloading is*************** "+new File(CommonFunctions.getVideoURL()
//												+ VfileName).length());
								}
								else {
									CommonFunctions.downloadFileFromAmazon(URL,
											CommonFunctions.getVideoURL()
													+ VfileName);
								}
								downlodflag = true;
								pd.dismiss();
							} catch (IOException e) {
								playflag = false;
								downlodflag = false;
								pd.dismiss();
								e.printStackTrace();
							}
						} else {
							downlodflag = true;
							pd.dismiss();
						}
						if (downlodflag) {
							
							// Video Playing will not be used in Trive Wire So Commenting this
//							Intent i = new Intent(EyesayPlayMedia.this,
//									VideoPlayer.class);
//							playflag = true;
//							try {
//								i.putExtra("fileName",
//										CommonFunctions.getVideoURL()
//												+ VfileName);
//								i.putExtra("disablefacebook", true);
//								temp_Receiver = Receiver;
//								temp_Type = type;
//							} catch (IOException e) {
//								e.printStackTrace();
//								playflag = false;
//							}
//							startActivityForResult(i, 101);

						}
						Looper.loop();
					}
				}.start();
			} else {
				// No Video Playing so commenting
//				Intent i = new Intent(EyesayPlayMedia.this, VideoPlayer.class);
//				i.putExtra("fileName", CommonFunctions.getVideoURL()
//						+ VfileName);
//				i.putExtra("disablefacebook", true);
//				startActivityForResult(i,566);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		Log.e("", "On Stop Called");
		try {
			if (mp != null) {
				mp.release();
				mp = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		try {
			if (f!=null && f.exists()) {
				try {
					if (URL.startsWith("http")) {
						Log.e("", "File has been deleted successfully "+f.delete());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			else {
				Log.e("", "File does not exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		ActivityStatus.appStatus(this, true);
		super.onResume();
	}

	@Override
	protected void onPause() {
		ActivityStatus.appStatus(null, false);
		super.onPause();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	try {
		finish();
	} catch (Exception e) {
		e.printStackTrace();
	}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
