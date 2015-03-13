package eyesay.adapter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.ImageDownloader;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.TribeWireUtil;
import com.tribewire.app.AddEditImage;
import com.tribewire.app.ConversationMessages;
import com.tribewire.app.EyesayTextImage;
import com.tribewire.app.R;

import eyesay.obj.Friend;

public class ConversationAdapterNew extends BaseAdapter {

	static Context c;
	private LayoutInflater mInflater;

	String[] SENDER;
	String[] RECEIVER;
	String[] TIME;
	Integer[] messageType;
	String textMessage[];
	String file_url[];
	String message_id[];
	Integer sendReceiveType[];
	Integer isdraft[];
	String newServerDate[];
	String thumbNail[];
	String senderName[];
	static Typeface tf;
	static Typeface tfb;
	File image;
	HashMap<String, String> userImagesMap = null;

	private static boolean isFYiReply;
	private static String fyiSubject;
	public static void setFyiSubject(String fyiSubject) {
		ConversationAdapterNew.fyiSubject = fyiSubject;
	}
	
	
	public static String getFyiSubject() {
		return fyiSubject;
	}
	public static boolean isFYiReply() {
		return isFYiReply;
	}
	
	
	
	
	
	public static void setFyiReply(boolean isFYiReply){
		ConversationAdapterNew.isFYiReply  =isFYiReply;
	}
	
	public ConversationAdapterNew(Context context, String[] Sender,
			String[] receiver, String[] time, Integer[] messageType,
			Integer sendReceiveType[], String[] textMessage, String file_url[],
			String message_id[], Integer isdraft[], String newServerDate[],
			String thumbNail[], String senderName[]) {
		c = context;
		SENDER = Sender;
		RECEIVER = receiver;
		TIME = time;
		this.messageType = messageType;
		this.message_id = message_id;
		mInflater = LayoutInflater.from(context);
		this.textMessage = textMessage;
		this.newServerDate = newServerDate;
		this.file_url = file_url;
		this.sendReceiveType = sendReceiveType;
		this.isdraft = isdraft;
		this.thumbNail = thumbNail;
		this.senderName = senderName;

		if (tf == null) {
			tf = TribeWireUtil.getArialTypeface(c);
		}
		if (tfb == null) {
			tfb = TribeWireUtil.getArialBoldTypeface(c);
		}

		try {
			userImagesMap = new HashMap<String, String>();
			String path = CommonFunctions.getPref(c,
					AddEditImage.LOCAL_PROFILE_PIC, "");
			if (path != null && path.length() > 0) {
				image = new File(path);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notifiyMe(Context context, String[] Sender, String[] receiver,
			String[] time, Integer[] messageType, Integer sendReceiveType[],
			String[] textMessage, String file_url[], String message_id[],
			Integer isdraft[], String newServerDate[], String thumbNail[],
			String senderName[]) {

		try {
			c = context;

			SENDER = Sender;
			RECEIVER = receiver;
			TIME = time;
			this.messageType = messageType;
			this.message_id = message_id;
			mInflater = LayoutInflater.from(context);
			this.textMessage = textMessage;

			this.file_url = file_url;
			this.sendReceiveType = sendReceiveType;
			this.isdraft = isdraft;
			this.newServerDate = newServerDate;
			this.thumbNail = thumbNail;
			this.senderName = senderName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return SENDER.length;
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int messageTypeValue = messageType[position];
		int sendReceiveValue = sendReceiveType[position];
		String messageValue = textMessage[position];
		String fileUrlValue = file_url[position];
		String thumbNailValue = thumbNail[position];
		final String msgIdValue = message_id[position];
		final int isDraftValue = isdraft[position];

		ViewHolder holder = new ViewHolder();

		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.list_item_conversation,
					null);

			holder.imgUserImg = (ImageView) convertView
					.findViewById(R.id.img_profile_pic);
			holder.txtName = (TextView) convertView
					.findViewById(R.id.txt_receiver);

			holder.txtMessage = (TextView) convertView
					.findViewById(R.id.txt_message);
			holder.txtTime = (TextView) convertView
					.findViewById(R.id.txt_time_msg);
			holder.imgAttachPic = (ImageView) convertView
					.findViewById(R.id.img_message);
			holder.imgAudioPic = (ImageView) convertView
					.findViewById(R.id.img_audio_message);
			holder.rltAudio = (RelativeLayout) convertView
					.findViewById(R.id.rlt_audio);
			holder.txtAudioCount = (TextView) convertView
					.findViewById(R.id.audio_seconds);
			holder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar);
			try {
				holder.txtName.setTypeface(tf);
				holder.txtMessage.setTypeface(tf);
				holder.txtTime.setTypeface(tf);

			} catch (Exception e) {
				// TODO: handle exception
			}
			convertView.setTag(R.layout.list_item_conversation, holder);
		} else {
			holder = (ViewHolder) convertView
					.getTag(R.layout.list_item_conversation);
		}
		//Log.e("", "========== Name " + senderName[position]);
		if (sendReceiveValue ==0) {
			holder.txtName.setText("Me");	
		}
		else {
			holder.txtName.setText(senderName[position]);
		}
		
		holder.txtMessage.setText(Html.fromHtml(new String(com.coremedia.drm.packager.Base64
				.decodeBase64(messageValue.trim().getBytes()))));
		if (isFYiReply) {
			holder.txtMessage.setAutoLinkMask(Linkify.WEB_URLS);
			if (getFyiSubject()!=null) {
				holder.txtName.setText(getFyiSubject());
			}
		}
		holder.txtTime.setText(TIME[position]);
		//Log.e("", "========= Message Type value ======= "+messageType[position]);
		// TYPE == IMAGE
		if (messageType[position] == 3) {
			drawAttachedImages(fileUrlValue, holder);
		}
		// AUDIO
		else if (messageType[position] == 2) {
			drawAudioLayout(fileUrlValue, holder, msgIdValue);
		} 
		// Text
		else if (messageType[position] == 0 || messageType[position] == 1) {
			holder.rltAudio.setVisibility(View.GONE);
			holder.imgAttachPic.setVisibility(View.GONE);
		}
		else {
			if (holder.rltAudio.getVisibility() == View.VISIBLE) {
				holder.rltAudio.setVisibility(View.GONE);
			}
			if (holder.imgAttachPic.getVisibility() == View.VISIBLE) {
				holder.rltAudio.setVisibility(View.GONE);
			}
		}

//		Log.e("", "========== Send Receive Value " + sendReceiveValue
//				+ "========== Name " + senderName[position]);
		showUserImages(holder, position, sendReceiveValue);
		return convertView;
	}

	private void showUserImages(ViewHolder holder, int position,
			int sendReceiveValue) {
		try {
			if (sendReceiveValue == 0) {
				showSenderImage(holder);
			} else {
				showReceiverImage(holder, position);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showSenderImage(ViewHolder holder) {
		try {

			try {
				if (image.exists()) {

//					holder.imgUserImg.setImageBitmap(BitmapFactory
//							.decodeFile(image.getAbsolutePath()));
					holder.imgUserImg.setImageBitmap(TribeWireUtil.getBitmap(c, Uri.fromFile(image)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showReceiverImage(ViewHolder holder, int position) {
		try {

			try {
				String value = userImagesMap.get(senderName[position]);
				if (value != null && value.length() > 0) {
					ImageDownloader.getInstance().download(value,
							holder.imgUserImg);
				} else {
					holder.imgUserImg.setImageDrawable(c.getResources()
							.getDrawable(R.drawable.no_image));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			String userImage = getFriendData(senderName[position]);
			if (userImage != null && userImage.startsWith("http")) {
				checkImage(userImage, holder.imgUserImg);
//				ImageDownloader.getInstance().download(userImage,
//						holder.imgUserImg);
			} else {
				holder.imgUserImg.setImageDrawable(c.getResources()
						.getDrawable(R.drawable.no_image));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static boolean checkImage(String url , ImageView imageView) {
		boolean isExists  = false;
		try {
			final String fileName = url.substring(url.lastIndexOf("/") + 1);
			
			String anotherurl = url.substring(0, url.lastIndexOf("/"));
		//	System.out.println("Another Url " + anotherurl);
			String anothernewUrl = anotherurl.substring(anotherurl.lastIndexOf("/") + 1);
			//System.out.println("Another new Url "+anothernewUrl);
			
			String requiredFileUrl  = anothernewUrl+fileName;
			//System.out.println("My required Url "+requiredFileUrl);

			final File f = new File(CommonFunctions.getImageRL() + requiredFileUrl);
			//Log.e("", "=============The file Path is " + f.toString());
			if (f.exists()) {
				isExists  = true;
				
				//Log.e("", "=========== File exists " + f.exists());
				
				imageView.setImageURI(Uri.parse(f.toString()));
			}
			else {
				isExists    = false;
				    
				ImageDownloader1.getInstance().download(
						url.trim(),imageView,c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
           return isExists ;
	}

	private String getFriendData(String receiverName) {

		DataBase db = null;
		String userImage = "";
		try {
			db = new DataBase(c);
			db.open();
			String where = Friend.NAME + " = '" + receiverName + "'";

			Cursor cursor = db.fetch(DataBase.Friends_table,
					DataBase.Friends_int, where);
			//Log.e("", "============= The Cursor Count is " + cursor.getCount()
					//+ "=============== With Friend Name " + receiverName);

			if (cursor != null && cursor.getCount() > 0) {

				userImage = cursor.getString(4);

				userImagesMap.put(receiverName, userImage);

				//Log.e("", "=============== the User Image is " + userImage);
			}

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			db.close();
		}
		return userImage;
	}

	TextView tempSecondCount;
	ProgressBar tempProgrssbar;
	ImageView tempPlayImage;
	boolean pauseFlag = false;
	int length = 0;
  ThreadPP threadPP ;
  String audioFilePath= "";
  
	private void drawAudioLayout(String fileUrlValue, final ViewHolder holder,
			final String msgIdValue) {
		try {
			
			holder.rltAudio.setVisibility(View.VISIBLE);
			holder.imgAudioPic.setTag(fileUrlValue);
			holder.imgAttachPic.setVisibility(View.GONE);
			holder.txtAudioCount.setTag(fileUrlValue);
			holder.progressBar.setTag(holder.txtAudioCount);
			holder.imgAudioPic.setTag(holder.progressBar);			
			holder.imgAudioPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//String image_url = (String) v.getTag();

					tempProgrssbar = (ProgressBar) v.getTag();
				tempSecondCount=	(TextView)tempProgrssbar.getTag();
				String	image_url =(String )tempSecondCount.getTag();
				tempPlayImage  = (ImageView)v;
//					tempSecondCount = holder.txtAudioCount;
//					tempProgrssbar = holder.progressBar;
//					tempPlayImage = holder.imgAudioPic;
                    
					 if (!(audioFilePath.length()>0)) {
						audioFilePath  = image_url;
						playflag = false;
					}
					 else if (audioFilePath.length()>0 && (!audioFilePath.equalsIgnoreCase(image_url))) {
						
						 audioFilePath = image_url;
						 playflag = false;
						 pauseFlag  = false;
						 move = 0;
						 temp = 0;
						 try {
							if (mp!=null && mp.isPlaying()) {
								mp.stop();
								mp.release();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					 else if (audioFilePath.length()>0 && audioFilePath.equalsIgnoreCase(image_url)) {
						
						 audioFilePath   = image_url;
					}
					 
					

   					if (playflag == false) {
						Log.e("", "==Starting of new media==");
						//handler3.sendEmptyMessage(3);
						playAudio(0, image_url, "");
						//playMyAudioFile(image_url.trim(), false);

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
				
					}						
				
				
					
					
					
//					DataBase db = null;
//					try {
//						db = new DataBase(c);
//						db.open();
//						ContentValues cv = new ContentValues();
//						cv.put("played", 1);
//
//						Log.e("",
//								"Updated "
//										+ db.update(DataBase.Receive_table,
//												DataBase.Receive_int, " _id = "
//														+ msgIdValue, cv));
//					} catch (Exception e) {
//						e.printStackTrace();
//					} finally {
//						if (db != null) {
//							db.close();
//						}
//					}

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	 ProgressDialog pd = null;
	public void playAudio(final int type, final String URL,
			final String Receiver) {
		final String fileName = URL.substring(URL.lastIndexOf("/") + 1);
 
		try {
			//playflag = true;
			final File f = new File(CommonFunctions.getAudioURL() + fileName);

			Debugger.debugE("file not found need to download");
			if (ConversationMessages.isChatActivityVisible) {
				 pd = ProgressDialog.show(c, "",
						"Retrieving..");
				 pd.setCancelable(true);
			}
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
							if (pd!=null) {
								pd.dismiss();
							}
						}
						AutoDownload = true;
					} else {
						if (pd!=null) {
							pd.dismiss();
						}
					}
					if (downlodflag) {
						try {
							// playAudioFile(type, CommonFunctions.getAudioURL()
							// + fileName, Receiver, AutoDownload);
							final boolean tempAuto = AutoDownload;
							((ConversationMessages)c).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										if (ConversationMessages.isChatActivityVisible) {
											playMyAudioFile(
													CommonFunctions.getAudioURL()
															+ fileName, false);
										}
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

	private void drawAttachedImages(String fileUrlValue, ViewHolder holder) {
		try {
			if (fileUrlValue != null && fileUrlValue.length() > 0
					&& !fileUrlValue.equalsIgnoreCase("null")
					&& !fileUrlValue.equalsIgnoreCase("(null)")) {
				if (fileUrlValue.startsWith("http")) {
					holder.imgAttachPic.setVisibility(View.VISIBLE);
					holder.rltAudio.setVisibility(View.GONE);
					holder.imgAttachPic.setTag(fileUrlValue);
					checkImage(fileUrlValue, holder.imgAttachPic);
//					ImageDownloader1.getInstance().download(fileUrlValue,
//							holder.imgAttachPic);


				} else {
					holder.imgAttachPic.setVisibility(View.GONE);
				}

			} else {
				holder.imgAttachPic.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static class ViewHolder {

		ImageView imgUserImg;
		TextView txtName;
		TextView txtMessage;
		TextView txtTime;
		ImageView imgAttachPic;

		RelativeLayout rltAudio;
		TextView txtAudioCount;
		ImageView imgAudioPic;
		ProgressBar progressBar;

	}

	public MediaPlayer mp = null;
	public int move = 0;
	int temp = 0;
	int sec = 0;

	final Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (move < 10) {
				tempSecondCount.setText("00:0" + move);
			}

			else {
				tempSecondCount.setText("00:" + move);
			}
			temp++;
			tempProgrssbar.setProgress((int) (((temp) * 100) / sec));
		};
	};

	private static int SET_AUDIO_DURATION = 4;
	Handler handler2 = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
		  if (msg.what == SET_AUDIO_DURATION) {
				if (sec < 10) {
					tempSecondCount.setText("00:0" + sec);
				}

				else {
					tempSecondCount.setText("00:" + sec);
				}			
			  
			  
		}
			
		}
	};
	
	File f = null;

	boolean downlodflag = true;
	boolean playflag = false;
	private boolean mediaFirstTime = true;

	private boolean mediaCompleted = false;

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
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			// Below mp.start has been moved  inside thread class
			//mp.start();
			
			playflag = true;
			sec = mp.getDuration() / 1000;
			handler2.sendEmptyMessage(SET_AUDIO_DURATION);
			if (sec > 60)
				sec = 60;
			// CommonFunctions.setFont(EyesayPlayAudio.this, txt_audio_seconds,
			// false, false);
			// txt_audio_seconds.setText("Duration: 00:"
			// + ((sec <= 9) ? "0" + sec : sec));

			move = sec;
			Log.e("", "===================" + move);
			// new Handler().post(runnable);

			threadPP = new ThreadPP();
//			Thread t = new Thread() {
//				public void run() {
//					while (move > 0) {
//						move--;
//						// handler.post(new Runnable() {
//						// public void run() {
//						// if (move >= 0) {
//						// ((TextView) findViewById(R.id.audio_seconds))
//						// .setText("Duration: 00:"
//						// + ((move <= 9) ? "0" + move
//						// : move));
//						//
//						// }
//						// }
//						// });
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
//			t.start();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Debugger.debugE("on completion method..");
					move = 0;
					temp= 0;
					if (mp != null) {
						mp.stop();
						mp.release();
					}

					playflag = false;
					mediaCompleted = true;
					mp = null;
					ConversationAdapterNew.this.mp = null;
					handler3.sendEmptyMessage(4);
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
					// finish();
				}

			});

		} catch (Exception e) {
			CommonFunctions.showAlert(c, "Error whle Playing Media", "Error");

			Debugger.debugE("error " + e.getMessage());

			mp.release();
			mp = null;

			playflag = false;

		}

	}

	Handler handler3 = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				tempPlayImage.setBackgroundResource(R.drawable.videoplay);
			} else if (msg.what == 2) {
				tempPlayImage.setBackgroundResource(R.drawable.videopause);
			} else if (msg.what == 3) {
				tempPlayImage.setBackgroundResource(R.drawable.videopause);
			}
			else if (msg.what==4) {
				tempPlayImage.setBackgroundResource(R.drawable.videoplay);
				tempProgrssbar.setProgress(0);
			}
		};
	};
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

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				handler3.sendEmptyMessage(3);
				if (mp!=null) {
					mp.start();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
              
			
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
}
