
package eyesay.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.TextView;

import com.eyesayapp.Utils.CommonFunctions;
import com.tribewire.app.R;

/**
 * Adapter class for friends screen
 * 
 * @author xtpl
 * 
 */
public class GroupMemberAdapter extends BaseAdapter {
	static class ViewHolder {

		TextView name;
		ImageView icon;
		ImageView contactPic;
	}

	Context c;
	private LayoutInflater mInflater;
	String[] ID;
	String[] NAME;
	int[] STATUS;
	String[] phoneNumner;
	InputStream inputStream = null;
	String[] projection = new String[] { ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,

	};

	public GroupMemberAdapter() {

	}

	public GroupMemberAdapter(Context context, String[] name, int[] status,
			String[] id, String[] phoneNumner) {
		c = context;
		ID = id;
		NAME = name;
		STATUS = status;
		this.phoneNumner = phoneNumner;
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
// TODO COMMENTED on 15 Dec 2012 Temporary Need to change
		//if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friendlist, null);
			// convertView.setBackgroundColor(Color.WHITE);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.imgstatus);
			holder.name = (TextView) convertView.findViewById(R.id.text);
			//CommonFunctions.setFont(c, holder.name, true, false);

			convertView.setTag(holder);
		//} else
			holder = (ViewHolder) convertView.getTag();
		if (STATUS[position] == -1) {
			holder.name.setTextColor(Color.RED);
			holder.icon.setBackgroundResource(R.drawable.contact_b);
		}
		if (STATUS[position] == 0) {
			holder.name.setTextColor(Color.DKGRAY);
			holder.icon.setBackgroundResource(R.drawable.contact_i);

		} else if (STATUS[position] == 1) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.contact_a);

		} else if (STATUS[position] == -2) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.rightarrow);

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
			

			if (phoneNumner!=null) {
				getPhoneLookUp(phoneNumner[position], holder.contactPic,position);
			}
			Log.e("", "Get View Phone "+phoneNumner[position]+"Name "+NAME[position]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// holder.icon.setImageBitmap(BitmapFactory.decodeResource(c.getResources(),
		// ICON[position]));

		return convertView;

	}



	public InputStream openPhoto(long contactId, Context ctx) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri,
				Contacts.Photo.CONTENT_DIRECTORY);
		Log.e("", "===Photo Uri "+photoUri);
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

	private void getPhoneLookUp(String phoneNumber,ImageView imageview, int position) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor=null;
		try {
			 cursor = c.getContentResolver().query(uri, projection, null,
					null, null);
			if (cursor!=null && cursor.getCount()>0) {
				
				if (cursor.moveToFirst()) {
					long contactId = cursor.getLong(0);
					String name = cursor.getString(1);

					if ((inputStream = openPhoto(contactId, c)) != null) {
						imageview.setImageBitmap(CommonFunctions.decodeSampledBitmapFromResource(inputStream, 50, 50));
			//			Log.e("", "Id " + contactId + " Name=" + name);
Log.e("", "Position "+position+ "Number "+phoneNumner[position] +"Cursor Count "+cursor.getCount()+"Contact Id "+contactId+"Name "+name);
					}

//					do {
//
//					} while (cursor.moveToNext());
				}
			}	
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if (cursor!=null) {
				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		}
	}

}