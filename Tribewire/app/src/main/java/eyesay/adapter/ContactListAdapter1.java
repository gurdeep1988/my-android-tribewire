package eyesay.adapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.StringMatcher;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.model.SectionHeader;
import com.tribewire.app.R;

public class ContactListAdapter1 extends BaseAdapter implements SectionIndexer {

	static class ViewHolder {
		CheckBox id;
		TextView name;
		TextView phone_number;
		ImageView img;
		TextView txtHeader;
		ImageView imageStatus;

	}

	Context c;
	private LayoutInflater mInflater;
	String[] ID;
	String[] NAME;
	boolean[] flags;
	boolean acticon = false;
	InputStream inputStream = null;
	private String[] phoneNumner;
	private int[] status;
	Typeface tf;
	
	String[] projection = new String[] { ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,

	};

	
	ArrayList<SectionHeader> sectionHeaders;
	String friendImage[];
	public ContactListAdapter1(Context context, String id[], String[] name,
			String[] phoneNumber, boolean[] flag, int[] status,ArrayList<SectionHeader> sectionHeaders, String friendImage[]) {
		c = context;
		ID = id;
		NAME = name;
		flags = flag;
		this.phoneNumner = phoneNumber;
		mInflater = LayoutInflater.from(context);
		this.status  = status;
		this.sectionHeaders  = sectionHeaders;
		this.friendImage  = friendImage;
       tf  = TribeWireUtil.getArialTypeface(context);
	}
	
	public void notifyChange (boolean[] flag){
		flags = flag;
		notifyDataSetChanged();
	}

	public ContactListAdapter1(Context context, String id[], String[] name,
			String[] phoneNumber, boolean[] flag, boolean icons) {
		c = context;
		ID = id;
		NAME = name;
		flags = flag;
		acticon = icons;
		this.phoneNumner = phoneNumber;
		mInflater = LayoutInflater.from(context);

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
		// View view = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.contacts, null);
			holder = new ViewHolder();
			holder.id = (CheckBox) convertView.findViewById(R.id.chk);
			holder.id.setFocusable(false);
			holder.name = (TextView) convertView.findViewById(R.id.text);
			holder.img = (ImageView) convertView.findViewById(R.id.imgperson);
			holder.imageStatus  = (ImageView)convertView.findViewById(R.id.imgstatus);
			holder.phone_number = (TextView) convertView
					.findViewById(R.id.phone_number1);
			holder.txtHeader   = (TextView)convertView.findViewById(R.id.header);
			// CommonFunctions.setFont(c, holder.name, true, false);

			holder.name.setTypeface(tf);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

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
		holder.id.setTag(ID[position]);
		holder.name.setText(NAME[position]);
		Log.e("", "Name in the View Section is " + NAME[position]);
	//	if (flags[position]) {
			//holder.id.setChecked(true);
			//convertView.setBackgroundColor(c.getResources().getColor(R.color.skyblue
		//			));
			
		//} else {
			//holder.id.setChecked(false);
			//convertView.setBackgroundColor(Color.WHITE);
		//}

		if (status[position] == 3) {
		
			holder.imageStatus.setBackgroundResource(R.drawable.icons_status_online);
		
		}
		if (status[position] == 4) {
		
			holder.imageStatus.setBackgroundResource(R.drawable.icons_status_offline);
		
		}
		if (status[position] == 0) {
		
			holder.imageStatus.setBackgroundResource(R.drawable.icons_status_offline);
		
		} else if (status[position] == 1) {
		
			holder.imageStatus.setBackgroundResource(R.drawable.icons_status_online);
		
		} else if (status[position] == 2) {
		
			holder.imageStatus.setBackgroundResource(R.drawable.icons_status_offline);
		

		}
		
		if (flags[position]) {
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		}
		else {
			convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}
		if (status[position] == 5) {
			holder.name.setTextColor(c.getResources().getColor(R.color.green_user_group));
			//holder.img.setBackgroundResource(R.drawable.messagebluearrow);
			holder.imageStatus.setBackgroundResource(R.drawable.group_arrow_next_24x24);
			holder.img.setImageDrawable(c.getResources().getDrawable(R.drawable.group));
			//convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}
		else if (status[position] == 6) {
			holder.name.setTextColor(c.getResources().getColor(R.color.tribewire_black));
			//holder.img.setBackgroundResource(R.drawable.messagebluearrow);
			holder.imageStatus.setBackgroundResource(R.drawable.group_arrow_next_24x24);
			holder.img.setImageDrawable(c.getResources().getDrawable(R.drawable.tab_group_n));
			//convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}
		else {
			holder.name.setTextColor(c.getResources().getColor(R.color.tribewire_black));
			holder.img.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
	//					convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
						if (friendImage!=null) {
							if (friendImage[position]!=null && friendImage[position].length()>0) {
//								ImageDownloader1.getInstance().download(
//										imageUrl[position].trim(),holder.contactPic);	
								checkImage(friendImage[position], holder.img);
							}
						
						}
				//		convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}

		return convertView;

	}

	private boolean checkImage(String url , ImageView imageView) {
		boolean isExists  = false;
		try {
			final String fileName = url.substring(url.lastIndexOf("/") + 1);
			
			String anotherurl = url.substring(0, url.lastIndexOf("/"));
			System.out.println("Another Url " + anotherurl);
			String anothernewUrl = anotherurl.substring(anotherurl.lastIndexOf("/") + 1);
			System.out.println("Another new Url "+anothernewUrl);
			
			String requiredFileUrl  = anothernewUrl+fileName;
			System.out.println("My required Url "+requiredFileUrl);

			final File f = new File(CommonFunctions.getImageRL() + requiredFileUrl);
			Log.e("", "=============The file Path is " + f.toString());
			if (f.exists()) {
				isExists  = true;
				
				Log.e("", "=========== File exists " + f.exists());
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
	// a field in the adapter
	private int mSelectedPosition = -1;

	// getter and setter methods for the field above
	public void setSelectedPosition(int selectedPosition) {
		mSelectedPosition = selectedPosition;
		notifyDataSetChanged();
	}

	public int getSelectedPosition() {
		return mSelectedPosition;
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

}
