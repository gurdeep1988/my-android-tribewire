package eyesay.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.model.CompanyMessages;
import com.tribewire.app.R;

import eyesay.adapter.MessageFolderAdapter.ThreadPP;
import eyesay.adapter.MessageFolderAdapter.ViewHolder;

public class CompanyFyiAdapter extends BaseAdapter {

	Activity context;
	Typeface tf;
	Typeface tfb;
	List<CompanyMessages> companyMessages;

	public CompanyFyiAdapter(Activity context,
			List<CompanyMessages> companyMessages) {
		this.context = context;
		tf = TribeWireUtil.getArialTypeface(context);
		tfb = TribeWireUtil.getArialBoldTypeface(context);
		this.companyMessages = companyMessages;
	}

	public void changeData(List<CompanyMessages> companyMessages) {
		this.companyMessages = companyMessages;
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return companyMessages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return companyMessages.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(context);

			convertView = layoutInflater.inflate(R.layout.list_item_companyfy,
					null);
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.txt_title);
			holder.txtMessage = (TextView) convertView
					.findViewById(R.id.txt_message);
			holder.txtReply = (TextView) convertView
					.findViewById(R.id.txt_reply);
			holder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
			holder.senderName = (TextView) convertView.findViewById(R.id.txt_sender_name);
			holder.imgAlert = (ImageView) convertView
					.findViewById(R.id.img_alert);
			holder.rltCompanyLt = (RelativeLayout) convertView
					.findViewById(R.id.rlt_companyFYi);
			holder.txtTitle.setTypeface(tfb);
			holder.senderName.setTypeface(tfb);
			holder.txtMessage.setTypeface(tf);
			holder.txtTime.setTypeface(tf);
			holder.txtReply.setTypeface(tf);
		
			holder.rltImages = (RelativeLayout) convertView
					.findViewById(R.id.rlt_img);

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
			convertView.setTag(R.layout.list_item_companyfy, holder);
		} else {
			holder = (ViewHolder) convertView
					.getTag(R.layout.list_item_companyfy);
		}

		try {

//			holder.txtTitle.setText(companyMessages.get(position)
//					.getRecipientNames());
			holder.txtTitle.setText(companyMessages.get(position)
					.getSubject());
			holder.txtMessage.setText(Html.fromHtml(companyMessages.get(
					position).getLastMessage()));
			
			try {
				holder.txtTime.setText(CommonFunctions.getdateAno(Long
						.parseLong(companyMessages.get(position)
								.getConversationStartDate())));
				if (companyMessages.get(position).getSenderName().length()>0) {
					holder.senderName.setVisibility(View.VISIBLE);	
					holder.senderName.setText(companyMessages.get(position).getSenderName());
				}
				else {
					holder.senderName.setVisibility(View.GONE);
				}
				
			} catch (Exception e) {
				e.printStackTrace();

			}
			if (companyMessages.get(position).getReadStatus() == 0) {
				holder.rltCompanyLt.setBackgroundColor(context.getResources()
						.getColor(R.color.white));
				holder.txtTitle.setTextColor(context.getResources().getColor(
						R.color.blue));
				holder.txtReply.setTextColor(context.getResources().getColor(
						R.color.blue));
				holder.txtMessage.setTextColor(context.getResources().getColor(
						R.color.tribewire_black));
				holder.imgAlert.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.fyi_unread_60x60));
			} else {
				holder.rltCompanyLt.setBackgroundColor(context.getResources()
						.getColor(R.color.lighgrey));
				
				holder.txtTitle.setTextColor(context.getResources().getColor(
						R.color.tribewire_grey));
				holder.txtReply.setTextColor(context.getResources().getColor(
						R.color.tribewire_grey));
				holder.txtMessage.setTextColor(context.getResources().getColor(
						R.color.tribewire_black));
				holder.txtTime.setTextColor(context.getResources().getColor(
						R.color.tribewire_black));
				holder.imgAlert.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.fyi_read_60x60));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			if (companyMessages.get(position).getAttacheMessageType()
//					.equalsIgnoreCase("3")) {
//				drawAttachedImages(companyMessages.get(position)
//						.getAttachMessageUrl(), holder);
//			} else if (companyMessages.get(position).getAttacheMessageType()
//					.equalsIgnoreCase("2")) {
//				drawAudioLayout(companyMessages.get(position)
//						.getAttachMessageUrl(), holder);
//			} else {
//				if (holder.rltAudio.getVisibility() == View.VISIBLE) {
//					holder.rltAudio.setVisibility(View.GONE);
//				}
//				if (holder.imgAttachPic.getVisibility() == View.VISIBLE) {
//					holder.imgAttachPic.setVisibility(View.GONE);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return convertView;
	}

	private boolean checkImage(String url, ImageView imageView) {
		boolean isExists = false;
		try {
			final String fileName = url.substring(url.lastIndexOf("/") + 1);

			String anotherurl = url.substring(0, url.lastIndexOf("/"));
			// System.out.println("Another Url " + anotherurl);
			String anothernewUrl = anotherurl.substring(anotherurl
					.lastIndexOf("/") + 1);
			// System.out.println("Another new Url "+anothernewUrl);

			String requiredFileUrl = anothernewUrl + fileName;
			// System.out.println("My required Url "+requiredFileUrl);

			final File f = new File(CommonFunctions.getImageRL()
					+ requiredFileUrl);
			Log.e("", "=============The file Path is " + f.toString());
			if (f.exists()) {
				isExists = true;

				Log.e("", "=========== File exists " + f.exists());

				// imageView.setImageURI(Uri.parse(f.toString()));
				imageView.setImageBitmap(TribeWireUtil.getBitmap(context,
						Uri.fromFile(f)));
			} else {
				isExists = false;
				ImageDownloader1.getInstance().download(url.trim(), imageView,context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExists;
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
					// ImageDownloader1.getInstance().download(fileUrlValue,
					// holder.imgAttachPic);

					// holder.imgAttachPic
					// .setOnClickListener(new OnClickListener() {
					//
					// @Override
					// public void onClick(View v) {
					// String image_url = (String) v.getTag();
					// Intent intent = new Intent(c,
					// EyesayTextImage.class);
					// intent.putExtra("imageurl", image_url);
					// c.startActivity(intent);
					//
					// }
					// });

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

	TextView tempSecondCount;
	ProgressBar tempProgrssbar;
	ImageView tempPlayImage;
	boolean pauseFlag = false;
	int length = 0;
	ThreadPP threadPP;
	String audioFilePath = "";

	Handler handler3 = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				tempPlayImage.setBackgroundResource(R.drawable.videoplay);
			} else if (msg.what == 2) {
				tempPlayImage.setBackgroundResource(R.drawable.videopause);
			} else if (msg.what == 3) {
				tempPlayImage.setBackgroundResource(R.drawable.videopause);
			} else if (msg.what == 4) {
				tempPlayImage.setBackgroundResource(R.drawable.videoplay);
				tempProgrssbar.setProgress(0);
			}
		};
	};

	public void playAudio(final int type, final String URL,
			final String Receiver) {
		final String fileName = URL.substring(URL.lastIndexOf("/") + 1);

		try {
			// playflag = true;
			final File f = new File(CommonFunctions.getAudioURL() + fileName);

			Debugger.debugE("file not found need to download");
			final ProgressDialog pd = ProgressDialog.show(context, "",
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

							} else {
								CommonFunctions.downloadFileFromAmazon(URL,
										CommonFunctions.getAudioURL()
												+ fileName);
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
							context.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										playMyAudioFile(
												CommonFunctions.getAudioURL()
														+ fileName, false);
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

	private void drawAudioLayout(String fileUrlValue, final ViewHolder holder) {
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
					// String image_url = (String) v.getTag();

					tempProgrssbar = (ProgressBar) v.getTag();
					tempSecondCount = (TextView) tempProgrssbar.getTag();
					String image_url = (String) tempSecondCount.getTag();
					tempPlayImage = (ImageView) v;
					// tempSecondCount = holder.txtAudioCount;
					// tempProgrssbar = holder.progressBar;
					// tempPlayImage = holder.imgAudioPic;

					if (!(audioFilePath.length() > 0)) {
						audioFilePath = image_url;
						playflag = false;
					} else if (audioFilePath.length() > 0
							&& (!audioFilePath.equalsIgnoreCase(image_url))) {

						audioFilePath = image_url;
						playflag = false;
						pauseFlag = false;
						move = 0;
						temp = 0;
						try {
							if (mp != null && mp.isPlaying()) {
								mp.stop();
								mp.release();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (audioFilePath.length() > 0
							&& audioFilePath.equalsIgnoreCase(image_url)) {

						audioFilePath = image_url;
					}

					if (playflag == false) {
						Log.e("", "==Starting of new media==");
						handler3.sendEmptyMessage(3);
						playAudio(0, image_url, "");
						// playMyAudioFile(image_url.trim(), false);

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

					}

					// DataBase db = null;
					// try {
					// db = new DataBase(c);
					// db.open();
					// ContentValues cv = new ContentValues();
					// cv.put("played", 1);
					//
					// Log.e("",
					// "Updated "
					// + db.update(DataBase.Receive_table,
					// DataBase.Receive_int, " _id = "
					// + msgIdValue, cv));
					// } catch (Exception e) {
					// e.printStackTrace();
					// } finally {
					// if (db != null) {
					// db.close();
					// }
					// }

				}
			});

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
				tempSecondCount.setText("00:0" + move);
			}

			else {
				tempSecondCount.setText("00:" + move);
			}
			temp++;
			tempProgrssbar.setProgress((int) (((temp) * 100) / sec));
		};
	};

	File f = null;

	boolean downlodflag = true;
	boolean playflag = false;
	private boolean mediaFirstTime = true;

	private boolean mediaCompleted = false;
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
				handler.sendEmptyMessage(1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

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

			threadPP = new ThreadPP();
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
			// handler.sendEmptyMessage(1);
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
					CompanyFyiAdapter.this.mp = null;
					handler3.sendEmptyMessage(4);
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
					// finish();
				}

			});

		} catch (Exception e) {
			CommonFunctions.showAlert(context, "Error whle Playing Media",
					"Error");

			Debugger.debugE("error " + e.getMessage());

			mp.release();
			mp = null;

			playflag = false;

		}

	}

	static class ViewHolder {
		TextView txtTitle;
		TextView txtMessage;
		TextView txtReply;
		TextView txtTime;
		ImageView imgAlert;
		RelativeLayout rltCompanyLt;

		RelativeLayout rltImages;

		ImageView imgAttachPic;

		RelativeLayout rltAudio;
		TextView txtAudioCount;
		TextView senderName;
		ImageView imgAudioPic;
		ProgressBar progressBar;

	}

}
