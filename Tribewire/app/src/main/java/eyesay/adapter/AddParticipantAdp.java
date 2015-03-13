package eyesay.adapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.StringMatcher;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.model.SectionHeader;
import com.tribewire.app.R;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class AddParticipantAdp extends BaseAdapter implements SectionIndexer{

	static class ViewHolder {

		TextView name;
		TextView phone_number;
		ImageView icon;
		ImageView contactPic;
		TextView txtHeader;
	}

	Context c;
	private LayoutInflater mInflater;
	String[] ID;
	String[] NAME;
	int[] STATUS;
	String[] phoneNumner;
	InputStream inputStream = null;
	String imageUrl[];
    Typeface tf;
    boolean isSelected[];
    
	String[] projection = new String[] { ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,

	};

	public AddParticipantAdp() {

	}

	ArrayList<SectionHeader> sectionHeaders;
	public AddParticipantAdp(Context context, String[] name, int[] status,
			String[] id, String[] phoneNumner, String imageUrl[], ArrayList<SectionHeader> sectionHeaders,boolean isSelected[]) {
		c = context;
		ID = id;
		NAME = name;
		STATUS = status;
		this.phoneNumner = phoneNumner;
		mInflater = LayoutInflater.from(context);
		this.imageUrl   = imageUrl;
         tf = TribeWireUtil.getArialTypeface(c);
         this.sectionHeaders  = sectionHeaders;
         this.isSelected   = isSelected;
	}
	
	 public  void notifyChange (Context context, String[] name, int[] status,
				String[] id, String[] phoneNumner, String imageUrl[], ArrayList<SectionHeader> sectionHeaders,boolean isSelected[]){
		 c = context;
			ID = id;
			NAME = name;
			STATUS = status;
			this.phoneNumner = phoneNumner;
			mInflater = LayoutInflater.from(context);
			this.imageUrl   = imageUrl;
	         tf = TribeWireUtil.getArialTypeface(c);
	         this.sectionHeaders  = sectionHeaders;
	         this.isSelected   = isSelected;
	         notifyDataSetChanged();
	         
	 }
	

	public int getCount() {
		return NAME.length;
	}

	public Object getItem(int position) {
		return NAME[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// TODO COMMENTED on 15 Dec 2012 Temporary Need to change
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friendlist, null);
			// convertView.setBackgroundColor(Color.WHITE);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.imgstatus);
			holder.name = (TextView) convertView.findViewById(R.id.text);
			holder.phone_number = (TextView)convertView.findViewById(R.id.phone_number);
			holder.txtHeader   = (TextView)convertView.findViewById(R.id.header);
			// CommonFunctions.setFont(c, holder.name, true, false);
            holder.name.setTypeface(tf);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		if (STATUS[position] == 3) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_online);
			if (isSelected[position]==true) {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			else {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));	
			}
			
		}
		if (STATUS[position] == 4) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			if (isSelected[position]==true) {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			else {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));	
			}
			
		}
		if (STATUS[position] == 0) {
			holder.name.setTextColor(Color.BLACK);
			//holder.icon.setBackgroundResource(R.drawable.unavailable);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			if (isSelected[position]==true) {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			else {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));	
			}
			
			
		} else if (STATUS[position] == 1) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_online);
			
			if (isSelected[position]==true) {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			else {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));	
			}
			
		} else if (STATUS[position] == 2) {
			holder.name.setTextColor(Color.BLACK);
			//holder.icon.setBackgroundResource(R.drawable.unavailable);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			if (isSelected[position]==true) {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			else {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));	
			}
			

		}
		else if (STATUS[position] == 5) {
			holder.name.setTextColor(c.getResources().getColor(R.color.green_user_group));
			holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
			
			
			
			if (isSelected[position]==true) {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			else {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));	
			}
		}
		else if ( STATUS[position] == 6) {
			holder.name.setTextColor(c.getResources().getColor(R.color.tribewire_grey));
			holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
			if (isSelected[position]==true) {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
			}
			else {
				convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));	
			}
			
			
		}		

		 try {
			 SectionHeader sectionHeader  = sectionHeaders.get(position);
			 if (sectionHeader!=null && sectionHeader.getAlphabet()!=null && sectionHeader.getIsShow()==true) {
				holder.txtHeader.setVisibility(View.VISIBLE);
				holder.txtHeader.setText(sectionHeader.getAlphabet());
			}
			 else {
				 holder.txtHeader.setVisibility(View.GONE);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// holder.id.setTag(ID[position]);
		if (STATUS[position] == -2) {
			String tmp = NAME[position];
			int idx = tmp.indexOf("(");
			tmp = tmp.substring(0, idx) + " Group " + tmp.substring(idx);
			holder.name.setText(tmp);

		} else
			holder.name.setText(NAME[position]);

		try {

			holder.contactPic = (ImageView) convertView
					.findViewById(R.id.contact_pic);

			if (phoneNumner != null) {
				
				// Leave this because no image is coming from Server right now 
				// will be uncommented in future   24-08-2013  15:43
				
				holder.phone_number.setText("");
				if (imageUrl!=null) {
					if (imageUrl[position]!=null && imageUrl[position].length()>0) {
//						ImageDownloader1.getInstance().download(
//								imageUrl[position].trim(),holder.contactPic);	
						checkImage(imageUrl[position], holder.contactPic);
					}
					else {
						
						 if (STATUS[position] == 5) {
					
							 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.group));

						}
						 else if (STATUS[position] == 6) {
								
							 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.tab_group_n));

						}
						 else {
//									holder.name.setTextColor(Color.BLACK);
//								holder.icon.setBackgroundResource(R.drawable.messagebluearrow);

							 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
						 }

					}
				}
				else {
//					holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
					 if (STATUS[position] == 5) {
					
						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.group));

					}
					 else if (STATUS[position] == 6) {
							
						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.tab_group_n));

					}
					 else {
						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));

					 }
				}
				
//				holder.phone_number.setText(""+phoneNumner[position]);
//				Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
//						Uri.encode(phoneNumner[position]));
//				LocalImageDownloader.getInstance().download(uri.toString(),
//						holder.contactPic, c);
			}
			Log.e("", "Get View Phone " + phoneNumner[position] + "Name "
					+ NAME[position]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// holder.icon.setImageBitmap(BitmapFactory.decodeResource(c.getResources(),
		// ICON[position]));

		return convertView;

	}

	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Override
	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section will be
		// selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				if (i == 0) {
					// For numeric section
					for (int k = 0; k <= 9; k++) {
						if (StringMatcher
								.match(String.valueOf(((String) getItem(j))
										.charAt(0)), String.valueOf(k)))
							return j;
					}
				} else {
					if (StringMatcher.match(
							String.valueOf(((String) getItem(j)).charAt(0)),
							String.valueOf(mSections.charAt(i))))
						return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}

	public InputStream openPhoto(long contactId, Context ctx) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri,
				Contacts.Photo.CONTENT_DIRECTORY);
		Log.e("", "===Photo Uri " + photoUri);
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
						Log.e("", "Position " + position + "Number "
								+ phoneNumner[position] + "Cursor Count "
								+ cursor.getCount() + "Contact Id " + contactId
								+ "Name " + name);
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
	private boolean checkImage(String url , ImageView imageView) {
		boolean isExists  = false;
		try {
			final String fileName = url.substring(url.lastIndexOf("/") + 1);
			
			String anotherurl = url.substring(0, url.lastIndexOf("/"));
			//System.out.println("Another Url " + anotherurl);
			String anothernewUrl = anotherurl.substring(anotherurl.lastIndexOf("/") + 1);
			//System.out.println("Another new Url "+anothernewUrl);
			
			String requiredFileUrl  = anothernewUrl+fileName;
			//System.out.println("My required Url "+requiredFileUrl);

			final File f = new File(CommonFunctions.getImageRL() + requiredFileUrl);
		//	Log.e("", "=============The file Path is " + f.toString());
			if (f.exists()) {
				isExists  = true;
				
			//	Log.e("", "=========== File exists " + f.exists());
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


}
