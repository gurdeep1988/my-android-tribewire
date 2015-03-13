package eyesay.adapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.model.FriendModel;
import com.tribewire.app.ConversationMessages;
import com.tribewire.app.EyesayTextImage;
import com.tribewire.app.R;
import com.tribewire.app.TenFourConversations;

import eyesay.adapter.ConversationAdapterNew.ThreadPP;
import eyesay.adapter.ConversationAdapterNew.ViewHolder;

public class MessageFolderAdapter extends BaseAdapter {

	static class ViewHolder {
		TextView display;
		TextView time;
		TextView msgCount;
		Button delete_btn;
		RelativeLayout main_layout;
		ImageView imgUserPic;
		ImageView imgUserOne;
		ImageView imgUserTwo;
		ImageView imgUserThree;
		ImageView imgUserFour;
		RelativeLayout rltImages;
		RelativeLayout topLayout;
		ImageView imgAttachPic;

		RelativeLayout rltAudio;
		TextView txtAudioCount;
		ImageView imgAudioPic;
		ProgressBar progressBar;
	}

	Activity c;
	private LayoutInflater mInflater;
	String[] projection = new String[] { ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,

	};
	ArrayList<ArrayList<FriendModel>> friendModelArray;
	String[] DISPLAY;
	String[] TIME;
	int[] RFlag;
	int msgCount[];
	String topMessage[];
	private Context context;
	static Typeface tf;
	static Typeface tfb;
	String messagetype[];
	String fileUrl[];

	public MessageFolderAdapter(Activity context) {

		this.context = context;
	}

	Integer[] isEmergency; 
	public MessageFolderAdapter(Activity context, String[] display,
			String[] time, int[] readFlag, int msgCount[], String topMessage[],
			ArrayList<ArrayList<FriendModel>> friendModelArray,
			String[] messageType, String[] fileUrl, Integer[] isEmergency) {
	    Log.e("", " -------------------------------------------------- Constructor-------");
		
		c = context;
		DISPLAY = display;
		TIME = time;
		RFlag = readFlag;
		mInflater = LayoutInflater.from(context);
		this.msgCount = msgCount;
		this.topMessage = topMessage;
		this.friendModelArray = friendModelArray;
		this.messagetype = messageType;
		this.isEmergency  = isEmergency;
		this.fileUrl = fileUrl;
		if (tf == null) {
			tf = Typeface.createFromAsset(c.getAssets(), "fonts/arial.ttf");
		}
		if (tfb == null) {
			tfb = Typeface
					.createFromAsset(c.getAssets(), "fonts/ArialBold.ttf");
		}

	}

	public int getCount() {
		return DISPLAY.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		try {

			ViewHolder holder;
			Log.e("", "============= Message Folder Get VIew Called =========");
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.message_folder_list_item,
						null);
				holder = new ViewHolder();
				holder.display = (TextView) convertView
						.findViewById(R.id.txtDisplay);

				holder.time = (TextView) convertView.findViewById(R.id.txtTime);
				holder.msgCount = (TextView) convertView
						.findViewById(R.id.msgCount);
				holder.delete_btn = (Button) convertView
						.findViewById(R.id.delete_item);
				holder.main_layout = (RelativeLayout) convertView
						.findViewById(R.id.mainlayout);
				holder.imgUserPic = (ImageView) convertView
						.findViewById(R.id.img_user_pic);
				holder.display.setTypeface(tf);
				holder.time.setTypeface(tf);
				holder.msgCount.setTypeface(tf);
				holder.imgUserOne = (ImageView) convertView
						.findViewById(R.id.img_user_one);
				holder.imgUserTwo = (ImageView) convertView
						.findViewById(R.id.img_user_two);
				holder.imgUserThree = (ImageView) convertView
						.findViewById(R.id.img_user_three);
				holder.imgUserFour = (ImageView) convertView
						.findViewById(R.id.img_user_four);
				holder.rltImages = (RelativeLayout) convertView
						.findViewById(R.id.rlt_img);
				holder.topLayout = (RelativeLayout) convertView
						.findViewById(R.id.rlt_top);
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
				convertView.setTag(holder);

			} else
				holder = (ViewHolder) convertView.getTag();
			if (getGroupCount(DISPLAY[position]) > 0) {
				holder.display.setText(DISPLAY[position]);

			} else {
				holder.display.setText(DISPLAY[position]);

			}

			if (RFlag[position] == 1) {
			
				holder.topLayout.setBackgroundColor(c.getResources().getColor(
						R.color.lighgrey));
				holder.time.setText(Html.fromHtml(new String(Base64.decode(
						topMessage[position].getBytes(), Base64.DEFAULT))));
			} else {
				holder.topLayout.setBackgroundColor(c.getResources().getColor(
						R.color.white));
				holder.time.setText(Html.fromHtml(new String(Base64.decode(
						topMessage[position].getBytes(), Base64.DEFAULT))));

			}
			if (isEmergency[position]==1) {
				
				holder.time.setText(""+Html.fromHtml(new String(Base64.decode(
						topMessage[position].getBytes(), Base64.DEFAULT))));
			}
			if (msgCount[position] > 1) {

				holder.msgCount.setText(TIME[position]);

			} else {
				holder.msgCount.setText(TIME[position]);
			}

			// TYPE == IMAGE
			try {
				if (messagetype[position].equalsIgnoreCase("3")) {
					drawAttachedImages(fileUrl[position], holder);
				} else if (messagetype[position].equalsIgnoreCase("2")) {
					drawAudioLayout(fileUrl[position], holder);
				} else {
					if (holder.rltAudio.getVisibility() == View.VISIBLE) {
						holder.rltAudio.setVisibility(View.GONE);
					}
					if (holder.imgAttachPic.getVisibility() == View.VISIBLE) {
						holder.imgAttachPic.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// AUDIO

			try {
				if (this.friendModelArray != null
						&& this.friendModelArray.size() > 0) {
					if (isEmergency[position]==1) {
						holder.rltImages.setVisibility(View.INVISIBLE);
						holder.imgUserPic.setVisibility(View.VISIBLE); 
						if (RFlag[position]==0) {
							holder.imgUserPic.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.emergency_msg_unread_100x100));	
						}
						else {
							holder.imgUserPic.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.emergency_msg_read_100x100));
						}
						
						
					}
					else {
						showImages(holder, position,
								this.friendModelArray.get(position));	
					}
					
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertView;

	}

	private void showImages(ViewHolder viewHolder, int position,
			ArrayList<FriendModel> friendModels) {
		try {
			
			if (friendModels != null && friendModels.size() > 1) {
				// Here show image minimum two and maximum three
				// if (viewHolder.imgUserPic.getVisibility() == View.VISIBLE) {
				viewHolder.imgUserPic.setVisibility(View.INVISIBLE);
				viewHolder.rltImages.setVisibility(View.VISIBLE);
				showAllImages(viewHolder, position, friendModels);
				// }

			} else {
				// It means size is one then hide all other four images and show
				// only single
				setAllFourInvisible(viewHolder, friendModels, position);
			}

		} catch (Exception e) {

		}

	}

	private void showAllImages(ViewHolder viewHolder, int position,
			ArrayList<FriendModel> friendModels) {
		try {
			if (friendModels != null && friendModels.size() > 0) {

				int userSize = friendModels.size();

				if (userSize == 2) {
					showTwoImages(viewHolder, position, friendModels);

				}

				else if (userSize == 3) {

					showThreeImages(viewHolder, position, friendModels);
				} else if (userSize >= 4) {
					showFourImages(viewHolder, position, friendModels);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showTwoImages(ViewHolder viewHolder, int position,
			ArrayList<FriendModel> friendModels) {
		try {
			try {
				// viewHolder.imgUserTwo.setBackgroundDrawable(c.getResources()
				// .getDrawable(R.drawable.no_image));
				// viewHolder.imgUserThree.setBackgroundDrawable(c.getResources()
				// .getDrawable(R.drawable.no_image));

				if (friendModels != null) {
					try {
						FriendModel friendModel = friendModels.get(0);
						if (friendModel != null) {
//							Log.e("",
//									"======== User One Image path============ "
//											+ friendModel.getImgUrl());
							if (friendModel.getImgUrl() != null
									&& friendModel.getImgUrl().length() > 0
									&& friendModel.getImgUrl().startsWith(
											"http")) {
								// ImageDownloader1.getInstance().download(
								// friendModel.getImgUrl(),
								// viewHolder.imgUserOne);

								checkImage(friendModel.getImgUrl(),
										viewHolder.imgUserOne);
							} else {
								viewHolder.imgUserOne.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}

						} else {
							viewHolder.imgUserOne.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}
					} catch (Exception e) {
						viewHolder.imgUserOne.setBackgroundDrawable(c
								.getResources()
								.getDrawable(R.drawable.no_image));
					}

					try {
						FriendModel friendModel2 = friendModels.get(1);
						if (friendModel2 != null) {

							if (friendModel2.getImgUrl() != null
									&& friendModel2.getImgUrl().length() > 0
									&& friendModel2.getImgUrl().startsWith(
											"http")) {
								// ImageDownloader1.getInstance().download(
								// friendModel2.getImgUrl(),
								// viewHolder.imgUserFour);

								checkImage(friendModel2.getImgUrl(),
										viewHolder.imgUserFour);
							} else {
								viewHolder.imgUserFour.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} else {
							viewHolder.imgUserFour.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}
					} catch (Exception e) {
						viewHolder.imgUserFour.setBackgroundDrawable(c
								.getResources()
								.getDrawable(R.drawable.no_image));
					}

				} else {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showThreeImages(ViewHolder viewHolder, int position,
			ArrayList<FriendModel> friendModels) {
		try {
			try {
				try {

					// viewHolder.imgUserFour.setBackgroundDrawable(c
					// .getResources().getDrawable(R.drawable.no_image));

					if (friendModels != null) {
						try {
							FriendModel friendModel = friendModels.get(0);
							if (friendModel != null) {
//								Log.e("",
//										"======== User One Image path============ "
//												+ friendModel.getImgUrl());

								if (friendModel.getImgUrl() != null
										&& friendModel.getImgUrl().length() > 0
										&& friendModel.getImgUrl().startsWith(
												"http")) {
									// ImageDownloader1.getInstance().download(
									// friendModel.getImgUrl(),
									// viewHolder.imgUserOne);

									checkImage(friendModel.getImgUrl(),
											viewHolder.imgUserOne);
								} else {
									viewHolder.imgUserOne
											.setBackgroundDrawable(c
													.getResources()
													.getDrawable(
															R.drawable.no_image));
								}

							} else {
								viewHolder.imgUserOne.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} catch (Exception e) {
							viewHolder.imgUserOne.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}

						try {
							FriendModel friendModel2 = friendModels.get(1);
							if (friendModel2 != null) {

								if (friendModel2.getImgUrl() != null
										&& friendModel2.getImgUrl().length() > 0
										&& friendModel2.getImgUrl().startsWith(
												"http")) {
									// ImageDownloader1.getInstance().download(
									// friendModel2.getImgUrl(),
									// viewHolder.imgUserTwo);
									checkImage(friendModel2.getImgUrl(),
											viewHolder.imgUserTwo);
								} else {
									viewHolder.imgUserTwo
											.setBackgroundDrawable(c
													.getResources()
													.getDrawable(
															R.drawable.no_image));
								}
								// ImageDownloader1.getInstance().download(
								// friendModel2.getImgUrl(),
								// viewHolder.imgUserTwo);
							} else {
								viewHolder.imgUserTwo.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} catch (Exception e) {
							viewHolder.imgUserTwo.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}

						try {
							FriendModel friendModel3 = friendModels.get(2);
							if (friendModel3 != null) {
								// ImageDownloader1.getInstance().download(
								// friendModel3.getImgUrl(),
								// viewHolder.imgUserThree);

								if (friendModel3.getImgUrl() != null
										&& friendModel3.getImgUrl().length() > 0
										&& friendModel3.getImgUrl().startsWith(
												"http")) {
									// ImageDownloader1.getInstance().download(
									// friendModel3.getImgUrl(),
									// viewHolder.imgUserThree);
									checkImage(friendModel3.getImgUrl(),
											viewHolder.imgUserThree);
								} else {
									viewHolder.imgUserThree
											.setBackgroundDrawable(c
													.getResources()
													.getDrawable(
															R.drawable.no_image));
								}
							} else {
								viewHolder.imgUserThree.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} catch (Exception e) {
							viewHolder.imgUserThree.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}

					} else {

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showFourImages(ViewHolder viewHolder, int position,
			ArrayList<FriendModel> friendModels) {

		try {
			try {
				try {
					// viewHolder.imgUserTwo.setBackgroundDrawable(c
					// .getResources().getDrawable(R.drawable.no_image));
					// viewHolder.imgUserFour.setBackgroundDrawable(c
					// .getResources().getDrawable(R.drawable.no_image));
					// viewHolder.imgUserOne.setBackgroundDrawable(c
					// .getResources().getDrawable(R.drawable.no_image));
					// viewHolder.imgUserThree.setBackgroundDrawable(c
					// .getResources().getDrawable(R.drawable.no_image));
					if (friendModels != null) {
						try {
							FriendModel friendModel = friendModels.get(0);
							if (friendModel != null) {
//								Log.e("",
//										"======== User One Image path============ "
//												+ friendModel.getImgUrl());
								// ImageDownloader1.getInstance().download(
								// friendModel.getImgUrl(),
								// viewHolder.imgUserOne);

								if (friendModel.getImgUrl() != null
										&& friendModel.getImgUrl().length() > 0
										&& friendModel.getImgUrl().startsWith(
												"http")) {
									// ImageDownloader1.getInstance().download(
									// friendModel.getImgUrl(),
									// viewHolder.imgUserOne);
									checkImage(friendModel.getImgUrl(),
											viewHolder.imgUserOne);
								} else {
									viewHolder.imgUserOne
											.setBackgroundDrawable(c
													.getResources()
													.getDrawable(
															R.drawable.no_image));
								}

							} else {
								viewHolder.imgUserOne.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} catch (Exception e) {
							viewHolder.imgUserOne.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}

						try {
							FriendModel friendModel2 = friendModels.get(1);
							if (friendModel2 != null) {

								// ImageDownloader1.getInstance().download(
								// friendModel2.getImgUrl(),
								// viewHolder.imgUserTwo);

								if (friendModel2.getImgUrl() != null
										&& friendModel2.getImgUrl().length() > 0
										&& friendModel2.getImgUrl().startsWith(
												"http")) {
									// ImageDownloader1.getInstance().download(
									// friendModel2.getImgUrl(),
									// viewHolder.imgUserTwo);
									checkImage(friendModel2.getImgUrl(),
											viewHolder.imgUserTwo);
								} else {
									viewHolder.imgUserTwo
											.setBackgroundDrawable(c
													.getResources()
													.getDrawable(
															R.drawable.no_image));
								}
							} else {
								viewHolder.imgUserTwo.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} catch (Exception e) {
							viewHolder.imgUserTwo.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}

						try {
							FriendModel friendModel3 = friendModels.get(2);
							if (friendModel3 != null) {
								// ImageDownloader1.getInstance().download(
								// friendModel3.getImgUrl(),
								// viewHolder.imgUserThree);

								if (friendModel3.getImgUrl() != null
										&& friendModel3.getImgUrl().length() > 0
										&& friendModel3.getImgUrl().startsWith(
												"http")) {
									// ImageDownloader1.getInstance().download(
									// friendModel3.getImgUrl(),
									// viewHolder.imgUserThree);
									checkImage(friendModel3.getImgUrl(),
											viewHolder.imgUserThree);
								} else {
									viewHolder.imgUserThree
											.setBackgroundDrawable(c
													.getResources()
													.getDrawable(
															R.drawable.no_image));
								}
							} else {
								viewHolder.imgUserThree.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} catch (Exception e) {
							viewHolder.imgUserThree.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}

						try {
							FriendModel friendModel4 = friendModels.get(3);
							if (friendModel4 != null) {
								// ImageDownloader1.getInstance().download(
								// friendModel4.getImgUrl(),
								// viewHolder.imgUserFour);

								if (friendModel4.getImgUrl() != null
										&& friendModel4.getImgUrl().length() > 0
										&& friendModel4.getImgUrl().startsWith(
												"http")) {
									// ImageDownloader1.getInstance().download(
									// friendModel4.getImgUrl(),
									// viewHolder.imgUserFour);
									checkImage(friendModel4.getImgUrl(),
											viewHolder.imgUserFour);
								} else {
									viewHolder.imgUserFour
											.setBackgroundDrawable(c
													.getResources()
													.getDrawable(
															R.drawable.no_image));
								}
							} else {
								viewHolder.imgUserFour.setBackgroundDrawable(c
										.getResources().getDrawable(
												R.drawable.no_image));
							}
						} catch (Exception e) {
							viewHolder.imgUserFour.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}

					} else {

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setAllFourInvisible(ViewHolder holder,
			ArrayList<FriendModel> friendModels, int position) {

		try {
			// if (holder.rltImages.getVisibility() == View.VISIBLE) {} else {
			//
			// }

			holder.rltImages.setVisibility(View.INVISIBLE);
			holder.imgUserPic.setVisibility(View.VISIBLE);
			if (friendModels != null) {
				FriendModel friendModel = friendModels.get(0);
				if (friendModel != null) {
					if (isEmergency[position]==1) {
						holder.imgUserPic.setBackgroundDrawable(c
								.getResources().getDrawable(
										R.drawable.emergency_msg_50x50));
					} else {
						if (friendModel.getImgUrl() != null
								&& friendModel.getImgUrl().length() > 0
								&& friendModel.getImgUrl().startsWith("http")) {

							checkImage(friendModel.getImgUrl(),
									holder.imgUserPic);
						} else {
							holder.imgUserPic.setBackgroundDrawable(c
									.getResources().getDrawable(
											R.drawable.no_image));
						}
					}

				} else {
					holder.imgUserPic.setBackgroundDrawable(c.getResources()
							.getDrawable(R.drawable.no_image));
				}

			} else {
				holder.imgUserPic.setBackgroundDrawable(c.getResources()
						.getDrawable(R.drawable.no_image));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getGroupCount(String name) {
		int groupCount = -1;
		DataBase db = null;
		try {
			db = new DataBase(c);
			db.open();
			groupCount = db.getCount(DataBase.Group_table, DataBase.Group_int,
					"groupname = '" + name + "'");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return groupCount;

	}

	

	public InputStream openPhoto(long contactId, Context ctx) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri,
				Contacts.Photo.CONTENT_DIRECTORY);
		//Log.e("", "===Photo Uri " + photoUri);
		Cursor cursor = ctx.getContentResolver().query(photoUri,
				new String[] { Contacts.Photo.DATA15 }, null, null, null);
		if (cursor == null) {
			return null;
		}
		try {
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					return new ByteArrayInputStream(data);
				}
			}
		} finally {
			cursor.close();
		}
		return null;
	}

	private void getPhoneLookUp(String phoneNumber, ImageView imageview,
			int position) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = null;
		InputStream inputStream;
		try {
			cursor = c.getContentResolver().query(uri, projection, null, null,
					null);
			if (cursor != null && cursor.getCount() > 0) {

				if (cursor.moveToFirst()) {
					long contactId = cursor.getLong(0);
					String name = cursor.getString(1);

					if ((inputStream = openPhoto(contactId, c)) != null) {
						imageview.setImageBitmap(CommonFunctions
								.decodeSampledBitmapFromResource(inputStream,
										50, 50));
						// Log.e("", "Id " + contactId + " Name=" + name);
						// Log.e("", "Position "+position+
						// "Number "+phoneNumner[position]
						// +"Cursor Count "+cursor.getCount()+"Contact Id "+contactId+"Name "+name);
					}

					// do {
					//
					// } while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		}
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
		//	Log.e("", "=============The file Path is " + f.toString());
			if (f.exists()) {
				isExists = true;

			//	Log.e("", "=========== File exists " + f.exists());
				
				
				imageView.setImageURI(Uri.parse(f.toString()));
				
				
			} else {
				isExists = false;
				ImageDownloader1.getInstance().download(url.trim(), imageView, c);
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
					//	Log.e("", "==Starting of new media==");
						//handler3.sendEmptyMessage(3);
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
					MessageFolderAdapter.this.mp = null;
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
			} else if (msg.what == 4) {
				tempPlayImage.setBackgroundResource(R.drawable.videoplay);
				tempProgrssbar.setProgress(0);
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

	public void playAudio(final int type, final String URL,
			final String Receiver) {
		final String fileName = URL.substring(URL.lastIndexOf("/") + 1);

		try {
			// playflag = true;
			final File f = new File(CommonFunctions.getAudioURL() + fileName);

			Debugger.debugE("file not found need to download");
			final ProgressDialog pd = ProgressDialog
					.show(c, "", "Retrieving..");
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
							c.runOnUiThread(new Runnable() {

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

}
