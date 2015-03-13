package com.tribewire.app;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.asyn.GroupAsyn;
import com.eyesayapp.asyn.IHttpRequest;

import eyesay.obj.Friend;

public class AddEditGroupMembers extends Activity implements OnClickListener,OnCheckedChangeListener{


	String GMgroupid="";
	private LinearLayout group_members;
	
	private ArrayList<View> gr_mem_view_list;
	private View view;
	// public String id[];//, names[], phonenumber[], invites[];
	// public boolean flag[];
	private Button btn_done;
	private CheckBox chk_all;
	ProgressDialog pd;
	//ArrayList<String> updateMemberList  ;
	
	String groupName="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.group_member);

		btn_done = (Button) findViewById(R.id.btn_done_member);
		chk_all = (CheckBox) findViewById(R.id.chk_all);
		group_members = (LinearLayout) findViewById(R.id.group_members);
		TextView textView = (TextView) findViewById(R.id.group_name);

		gr_mem_view_list = new ArrayList<View>();
      //  updateMemberList  = new ArrayList<String>();
		
		try {
			GMgroupid = this.getIntent().getExtras()
					.getString("groupid");
			 groupName = this.getIntent().getExtras()
					.getString("groupname");
			textView.setText(groupName);
			Log.e("", "My Group Id is " + GMgroupid);
		} catch (Exception e) {
			e.printStackTrace();

		}

		btn_done.setOnClickListener(this);
		chk_all.setOnCheckedChangeListener(this);
		showProgressDialog();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {

						fillGroupMembers();

					}
				}).start();

			}
		});
		setResult(RESULT_OK);
		super.onCreate(savedInstanceState);
	}

	private void showProgressDialog() {
		pd = ProgressDialog.show(this, "Loading",
				"Please wait as we load your contacts");
		pd.setCancelable(true);

	}

	Handler gr_mem_handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			Log.e("", "The View List Size is " + gr_mem_view_list.size());
			if (gr_mem_view_list != null && gr_mem_view_list.size() > 0) {
				for (int i = 0; i < gr_mem_view_list.size(); i++) {
					group_members.addView(gr_mem_view_list.get(i));
				}
			}
		}
	};

	private void fillGroupMembers() {

		Looper.prepare();
		DataBase db = new DataBase(AddEditGroupMembers.this);
		// Cursor contact = null;
		// Cursor c = null;
		db.open();

		Cursor contact = db.fetchAll(DataBase.Friends_table,
				DataBase.Friends_int, "name COLLATE NOCASE", null, null);

		try {
			
			if (contact.getCount() > 0) {
				// names = new String[contact.getCount()];
				// id = new String[contact.getCount()];
				// phonenumber = new String[contact.getCount()];
				// invites = new String[contact.getCount()];
				// flag = new boolean[contact.getCount()];
				// int i = 0;
				LayoutInflater inflater = this.getLayoutInflater();

				while (contact.moveToNext()) {
					int status = contact.getInt(Friend.INT_STATUS);
					if (status!=5) {

					//	int status = contact.getInt(Friend.INT_STATUS);
						// id[i] = contact.getString(Friend.INT_SR_NO);
						// names[i] = contact.getString(Friend.INT_NAME);
						// phonenumber[i] = contact.getString(Friend.INT_NUMBER);
						// invites[i] = contact.getString(Friend.INT_INVITE);
						// flag[i] = false;

						// convertView.setBackgroundColor(Color.WHITE);

						view = inflater.inflate(R.layout.friendlist, null);
						TextView textView = (TextView) view.findViewById(R.id.text);
						TextView phone_number = (TextView) view
								.findViewById(R.id.phone_number);

						ImageView imageView = (ImageView) view
								.findViewById(R.id.contact_pic);
						textView.setText(contact.getString(Friend.INT_NAME));
						try {
							phone_number.setText(contact
									.getString(Friend.INT_NUMBER));
						} catch (Exception e) {
							// TODO: handle exception
						}
						// CommonFunctions.setFont(GroupMembers.this, textView,
						// true, false);
						try {
							ImageView imageStatus = (ImageView) view
									.findViewById(R.id.imgstatus);
							
	                          Log.e("", "========= The Status "+status);
							if (status == 3) {
								textView.setTextColor(Color.BLACK);
								imageStatus.setBackgroundResource(R.drawable.bluex);
							}
							if (status == 4) {
								textView.setTextColor(Color.BLACK);
								imageStatus.setBackgroundResource(R.drawable.offline);
							}
							if (status == 0) {
								textView.setTextColor(Color.BLACK);
								imageStatus.setBackgroundResource(R.drawable.unavailable);

							} else if (status == 1) {
								textView.setTextColor(Color.BLACK);
								imageStatus.setBackgroundResource(R.drawable.available);

							} else if (status == 2) {
								textView.setTextColor(Color.BLACK);
								imageStatus.setBackgroundResource(R.drawable.unavailable);

							}


						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (isGroupMember(Integer.parseInt(contact
									.getString(Friend.INT_SR_NO)), db)) {
								view.setBackgroundColor(Color.parseColor("#2AAAFF"));
								view.setTag(new TagValue(contact
										.getString(Friend.INT_SR_NO), true));
							} else {
								view.setTag(new TagValue(contact
										.getString(Friend.INT_SR_NO), false));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						getPhoneLookUp(contact.getString(Friend.INT_NUMBER),
								imageView, 0);
						gr_mem_view_list.add(view);

						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								TagValue tv = (TagValue) v.getTag();

								Log.e("", " Selector " + tv.selector + " Pos "
										+ tv.id);
								if (tv.selector == false) {
									v.setBackgroundColor(Color
											.parseColor("#2AAAFF"));
									tv.selector = true;
									v.setTag(tv);
									DataBase db = new DataBase(AddEditGroupMembers.this);
									try {

										db.open();
										ContentValues values = new ContentValues();
										values.put("group_id", GMgroupid);
										values.put("friend_id", tv.id);
										db.insert_content(
												DataBase.Group_Member_table,
												DataBase.Group_member_int, values);
									//	updateMemberList.add(tv.id);

									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										db.close();
									}
									//updateMemberList.add(tv.id);
									// flag[tv.pos] = true;
								} else {
									v.setBackgroundColor(Color.WHITE);

									tv.selector = false;
									v.setTag(tv);
									
									DataBase db = new DataBase(AddEditGroupMembers.this);
									try {

										db.open();
										ContentValues values = new ContentValues();
										values.put("group_id", GMgroupid);
										values.put("friend_id", tv.id);
										db.delete(DataBase.Group_Member_table,
												DataBase.Group_member_int,
												"group_id='"+ GMgroupid+"'" 
														+ " and friend_id=" + tv.id);

										//updateMemberList.remove(tv.id);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										db.close();
									}
									//updateMemberList.remove(tv.id);
								}

							}
						});

						// i++;

					
					}
					
					
				}
				gr_mem_handler.sendEmptyMessage(1);

				// adapter = new ContactAdapter(ContactListSelection.this,
				// id, names, flag);
				// lvc.setAdapter(adapter);
				// lvc.setCacheColorHint(Color.TRANSPARENT);

				pd.dismiss();
			} else {

				// lvc.setAdapter(new SimpleAdapter(ContactListSelection.this,
				// Message));
				// lvc.setOnCreateContextMenuListener(null);
				// lvc.setCacheColorHint(Color.TRANSPARENT);
				// lvc.setOnItemClickListener(null);

				pd.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (contact != null)
				contact.close();
			// if (c != null)
			// c.close();
			if (pd != null) {
				pd.dismiss();
			}
			db.close();
		}
		Looper.loop();

	}

	private void getPhoneLookUp(String phoneNumber, ImageView imageView,
			int position) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,

		};
		InputStream inputStream = null;
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(uri, projection, null, null,
					null);
			if (cursor != null && cursor.getCount() > 0) {

				if (cursor.moveToFirst()) {
					long contactId = cursor.getLong(0);
					// String name = cursor.getString(1);

					if ((inputStream = openPhoto(contactId, this)) != null) {
						imageView
								.setImageBitmap(decodeSampledBitmapFromResource(
										inputStream, 50, 50));
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

	public InputStream openPhoto(long contactId, Context ctx) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri,
				Contacts.Photo.CONTENT_DIRECTORY);
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

	private class TagValue {
		String id;
		Boolean selector;

		public TagValue(String id, Boolean selector) {
			this.id = id;
			this.selector = selector;
		}

	}

	private boolean isGroupMember(int friend_id, DataBase db) {

		try {

			int cursor = db.getCount(DataBase.Group_Member_table,
					DataBase.Group_member_int, "group_id ='"+GMgroupid+"'"  
							+ " and friend_id=" + friend_id);
			Log.e("", "Cursor Count " + cursor + " And Friend Id is "
					+ friend_id);
			if (cursor > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {

			e.printStackTrace();
			return false;

		}

	}

	@Override
	public void onClick(View v) {

		Log.e("", "============ The Onclick is handled================");
		/*
		 * if (chk_all.isChecked()) { pd =
		 * ProgressDialog.show(GroupMembers.this, "Saving",
		 * "Please wait while saving your information"); pd.setCancelable(true);
		 * DataBase db = new DataBase(GroupMembers.this); db.open(); try { for
		 * (int i = 0; i < gr_mem_view_list.size(); i++) { try { int id =
		 * Integer.parseInt(((TagValue)gr_mem_view_list.get(i).getTag()).id); if
		 * (!(db.getCount(DataBase.Group_Member_table,
		 * DataBase.Group_member_int,
		 * "group_id="+GMgroupid+" and friend_id="+id)>0)) { ContentValues cv =
		 * new ContentValues(); cv.put("group_id", GMgroupid);
		 * cv.put("friend_id", GMgroupid);
		 * db.insert_content(DataBase.Group_Member_table,
		 * DataBase.Group_member_int, cv); } } catch (Exception e) {
		 * e.printStackTrace(); }
		 * 
		 * } } catch (Exception e) { e.printStackTrace(); } finally{ db.close();
		 * if (pd!=null) { pd.dismiss(); finish(); } } }
		 * 
		 * 
		 * 
		 */
		DataBase db =null;
		Cursor cursor=null;
		 
		try {
			db = new DataBase(this);
			db.open();
			
			cursor   = db.fetch(DataBase.Group_Member_table, DataBase.Group_member_int, "group_id= '"+GMgroupid+"'");
			if (cursor!=null && cursor.getCount()>0) {
				String friendIds  = "";
				if (cursor.moveToFirst()) {
						do {
							
						Cursor c    = 	db.fetch(DataBase.Friends_table, DataBase.Friends_int, Friend.SR_NO +" = "+cursor.getInt(2));
						
							
							friendIds +=""+c.getInt(Friend.INT_NUMBER)+",";     				
						} while (cursor.moveToNext());
						
						
						
					}
				    
				    
					try {
						if (friendIds.endsWith(",")) {
							friendIds =friendIds.substring(0, friendIds.length()-1);
							Log.e("", "================ The fiendsIds are "+friendIds);
							friendIds  = Base64.encodeToString(friendIds.getBytes(), Base64.DEFAULT);
						}
						
						if (CommonFunctions.CheckNetConnectivity(AddEditGroupMembers.this)) {
							pd = ProgressDialog.show(AddEditGroupMembers.this, "Group Update", "Please while updating your group members");
							pd.setCancelable(true);
							String encodedGroupName  = Base64.encodeToString(groupName.trim().getBytes(), Base64.DEFAULT);
							new GroupAsyn(this, ""+GMgroupid ,encodedGroupName, friendIds, true, updateGroup).execute();
						}
						else {
							Toast.makeText(this, "Please check your internet connectivity", Toast.LENGTH_LONG).show();
						}
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					db.close();
					cursor.close();
			}
			else {
				db.close();
				cursor.close();
				
					finish();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
		}
		/*if (updateMemberList!=null && updateMemberList.size()>0) {
			Log.e("", "============ Update Memebr List size is "+updateMemberList.size());
			String friendIds  = "";
			for (int i = 0; i < updateMemberList.size(); i++) {
				friendIds+=updateMemberList.get(i)+",";
			}
			
			try {
				if (friendIds.endsWith(",")) {
					friendIds =friendIds.substring(0, friendIds.length()-1);
					friendIds  = Base64.encodeToString(friendIds.getBytes(), Base64.DEFAULT);
				}
				
				if (CommonFunctions.CheckNetConnectivity(GroupMembers.this)) {
					pd = ProgressDialog.show(GroupMembers.this, "Group Update", "Please while updating your group members");
					pd.setCancelable(true);
					String encodedGroupName  = Base64.encodeToString(groupName.trim().getBytes(), Base64.DEFAULT);
					new GroupAsyn(this, ""+GMgroupid ,encodedGroupName, friendIds, true, updateGroup).execute();
				}
				else {
					Toast.makeText(this, "Please check your internet connectivity", Toast.LENGTH_LONG).show();
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}*/
		
		

		

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (gr_mem_view_list != null && gr_mem_view_list.size() > 0) {

			if (isChecked) {
				for (int i = 0; i < gr_mem_view_list.size(); i++) {
					View view = gr_mem_view_list.get(i);
					view.setBackgroundColor(Color.parseColor("#2AAAFF"));
					((TagValue) view.getTag()).selector = true;
					// flag[i] = true;
				}
			} else {
				for (int i = 0; i < gr_mem_view_list.size(); i++) {
					gr_mem_view_list.get(i).setBackgroundColor(Color.WHITE);
					((TagValue) view.getTag()).selector = false;
					// flag[i] = false;
				}
			}
		}

	}

	@Override
	protected void onDestroy() {
		if (gr_mem_view_list != null && gr_mem_view_list.size() > 0) {
			gr_mem_view_list.clear();
			gr_mem_view_list = null;
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
		
			ActivityStatus.setActivity(null);
		
		super.onPause();
	}

	public static Bitmap decodeSampledBitmapFromResource(InputStream is,
			int reqWidth, int reqHeight) {
		try {
			is.reset();
		} catch (Exception e) {
			// TODO: handle exception
		}

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeStream(is, null, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		try {
			is.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return BitmapFactory.decodeStream(is, null, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
	
	IHttpRequest  updateGroup  = new IHttpRequest() {
		
		@Override
		public void onResponse(String response) {
			if (response!=null && response.length()>0) {
				Toast.makeText(AddEditGroupMembers.this, "Group Updated successfully", Toast.LENGTH_LONG).show();
			}
			
			
			
			
			if (pd!=null) {
				pd.dismiss();
			}
			
			finish();
			
		}
		
		@Override
		public void onError(String onError) {
			
			try {
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Toast.makeText(AddEditGroupMembers.this, "Error while Updating group", Toast.LENGTH_LONG).show();
			if (pd!=null) {
				pd.dismiss();
			}
		}
	};
	

}
