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

public class EyesayPlayAudio extends Activity implements OnClickListener {

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
    private Button retake_btn;
	private Button play_pause_btn;
	private Button use_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.eyesayplayaudio);
		textView = (TextView) findViewById(R.id.audio_seconds);
		super.onCreate(savedInstanceState);
		btn_play_media_back = (Button) findViewById(R.id.btn_play_media_back);
		txt_media_type_text = (TextView) findViewById(R.id.txt_media_msg);
		txt_audio_seconds = (TextView) findViewById(R.id.audio_seconds);
		progress_audio_seconds = (ProgressBar) findViewById(R.id.audio_progressBar);
		realtive_layout_audio = (RelativeLayout) findViewById(R.id.audio_relative_layout);
		btn_play_media_back.setOnClickListener(this);
		use_btn = (Button) findViewById(R.id.use);
		retake_btn = (Button) findViewById(R.id.retake);
		play_pause_btn = (Button) findViewById(R.id.playpause);
		play_pause_btn.setBackgroundResource(R.drawable.videopause);
		play_pause_btn.setOnClickListener(this);
		use_btn.setOnClickListener(this);
		retake_btn.setOnClickListener(this);
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
					//playVideo(type, URL, receiver);
					setResult(RESULT_OK,
							getIntent().putExtra("type", messageType));
				} else {
					// callMenu(messageType, Receiver, false);
				//	playVideo(type, URL, receiver);
					setResult(RESULT_OK,
							getIntent().putExtra("type", messageType));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static final int RESULT_CODE_USE_VIDEO =566;
	public static final int RESULT_CODE_RETAKE_VIDEO =567;
	@Override
	public void onClick(View v) {
		if (v == use_btn) {
	         
			 stopVideoView();
			setResult(RESULT_CODE_USE_VIDEO);
			finish();
			
		} else if (v == retake_btn) {
			
			
			stopVideoView();
			setResult(RESULT_CODE_RETAKE_VIDEO);
			finish();
		}

			else if (v == play_pause_btn) {
	              if (mp!=null && mp.isPlaying()&& playflag==true) {
	            	  Log.e("", "=================== Pausing the Media Player if case ");
					 mp.pause();
					 length =mp.getCurrentPosition();
					 pauseFlag  = true;
					 handler3.sendEmptyMessage(1);
					 
					 playflag = false;
					
				}
	              else {
	            	  Log.e("", "=================== Main else case ================");
	            	  if (playflag== false) {
	            		  if (mediaCompleted) {
	            			  Log.e("", "=================== Starting the  Media Player if case ");
	            			  handler3.sendEmptyMessage(3);
						//	mp.start();
	            			  playMyAudioFile(0, URL, "", false);
							
							
						}
	            		  else {
	            			  Log.e("", "=================== Resuming the Media Player if else case ");
	            			  handler3.sendEmptyMessage(2);
	            			  mp.seekTo(length);
	            			  mp.start();
	            			  
	            			  
	            		  }
	            		  playflag = true;
	            		  pauseFlag  = false;
					}
	              }
				
			}
	}
	
	private boolean mediaCompleted  = false;
	boolean downlodflag = true;
	boolean playflag = true;
	boolean pauseFlag  = false;
	int length  = 0;

	public void playAudio(final int type, final String URL,
			final String Receiver) {
		final String fileName = URL;

		try {
			playflag = true;			

			Debugger.debugE("file not found need to download");
			new Thread() {
				public void run() {
					Looper.prepare();
					downlodflag = true;


					if (downlodflag) {
						try {
							// playAudioFile(type, CommonFunctions.getAudioURL()
							// + fileName, Receiver, AutoDownload);
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										playMyAudioFile(type,
												fileName, Receiver,
												false);
									} catch (Exception e) {
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

		} catch (Exception e) {
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

		Log.e("", "========== The File name is "+fileName);
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
			
			sec = mp.getDuration() / 1000;
			if (sec > 60)
				sec = 60;
			CommonFunctions.setFont(EyesayPlayAudio.this, txt_audio_seconds,
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
						{
						mp.stop();
						mp.release();
						}

					playflag = false;
					mediaCompleted= true;
					mp = null;
					EyesayPlayAudio.this.mp  = null;
					handler3.sendEmptyMessage(1);
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
					//finish();
				}
				
			});

		} catch (Exception e) {
			CommonFunctions.showAlert(EyesayPlayAudio.this,
					"Error whle Playing Media", "Error");

			Debugger.debugE("error " + e.getMessage());

			mp.release();
			mp = null;

			playflag = false;

		}
	}

	
			

	
	int temp_Type = 0;



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
	//	finish();
	} catch (Exception e) {
		e.printStackTrace();
	}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void stopVideoView() {
		if (mp!=null && mp.isPlaying() ) {
			if (playflag || pauseFlag) {
				try {
					mp.stop();
					mp.release();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	Handler handler3  = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			if (msg.what ==1) {
				play_pause_btn.setBackgroundResource(R.drawable.videoplay);
			}
			else if (msg.what ==2) {
				play_pause_btn.setBackgroundResource(R.drawable.videopause);
			}
			else if (msg.what ==3) {
				play_pause_btn.setBackgroundResource(R.drawable.videopause);
			}
		};
	};
}
