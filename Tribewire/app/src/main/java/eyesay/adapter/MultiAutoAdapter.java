package eyesay.adapter;

import java.io.File;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.model.ContactList;
import com.tribewire.app.R;

public class MultiAutoAdapter extends ArrayAdapter<ContactList>{

	private Context c;
	private ContactList contactList[];
	static class ViewHolder {

		TextView name;

		ImageView icon;
		ImageView contactPic;

	}

	
	public MultiAutoAdapter(Context context, int textViewResourceId,
			ContactList[] objects) {
		super(context, textViewResourceId, objects);
	      this.c  = context;
	      this.contactList = objects;
	}
	
	/*@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {


		ViewHolder holder;
		// TODO COMMENTED on 15 Dec 2012 Temporary Need to change
		if (convertView == null) {
			convertView = LayoutInflater.from(c).inflate(R.layout.friendlist, null);
			// convertView.setBackgroundColor(Color.WHITE);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.imgstatus);
			holder.name = (TextView) convertView.findViewById(R.id.text);


			// CommonFunctions.setFont(c, holder.name, true, false);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		 try {
			 holder.name.setText(contactList[position].getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (contactList[position].getStatus() == 3) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_online);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		}
		if (contactList[position].getStatus() == 4) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		}
		if (contactList[position].getStatus() == 0) {
			holder.name.setTextColor(Color.BLACK);
			//holder.icon.setBackgroundResource(R.drawable.unavailable);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		} else if (contactList[position].getStatus() == 1) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_online);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		} else if (contactList[position].getStatus() == 2) {
			holder.name.setTextColor(Color.BLACK);
			//holder.icon.setBackgroundResource(R.drawable.unavailable);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));

		}
		else if (contactList[position].getStatus() == 5) {
			holder.name.setTextColor(c.getResources().getColor(R.color.green_user_group));
			holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}
		else if ( contactList[position].getStatus() == 6) {
			holder.name.setTextColor(c.getResources().getColor(R.color.tribewire_grey));
			holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
            convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}		

		
		


		try {

			holder.contactPic = (ImageView) convertView
					.findViewById(R.id.contact_pic);


			
			// Leave this because no image is coming from Server right now 
			// will be uncommented in future   24-08-2013  15:43
			
			
			if (contactList[position].getImageUrl()!=null) {
				if (contactList[position].getImageUrl()!=null && contactList[position].getImageUrl().length()>0) {
//					ImageDownloader1.getInstance().download(
//							imageUrl[position].trim(),holder.contactPic);	
					checkImage(contactList[position].getImageUrl(), holder.contactPic);
				}
				else {
					
					 if (contactList[position].getStatus() == 5) {
				
						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.group));

					}
					 else if (contactList[position].getStatus() == 6) {
							
						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.tab_group_n));

					}
					 else {
//								holder.name.setTextColor(Color.BLACK);
//							holder.icon.setBackgroundResource(R.drawable.messagebluearrow);

						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
					 }

				}
			}
			else {
//				holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
				 if (contactList[position].getStatus() == 5) {
				
					 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.group));

				}
				 else if (contactList[position].getStatus() == 6) {
						
					 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.tab_group_n));

				}
				 else {
					 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));

				 }
			}
			
//			holder.phone_number.setText(""+phoneNumner[position]);
//			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
//					Uri.encode(phoneNumner[position]));
//			LocalImageDownloader.getInstance().download(uri.toString(),
//					holder.contactPic, c);
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		// holder.icon.setImageBitmap(BitmapFactory.decodeResource(c.getResources(),
		// ICON[position]));

		return convertView;

	
		
	
	}*/
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		// TODO COMMENTED on 15 Dec 2012 Temporary Need to change
		if (convertView == null) {
			convertView = LayoutInflater.from(c).inflate(R.layout.friendlist, null);
			// convertView.setBackgroundColor(Color.WHITE);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.imgstatus);
			holder.name = (TextView) convertView.findViewById(R.id.text);


			// CommonFunctions.setFont(c, holder.name, true, false);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		 try {
			 holder.name.setText(contactList[position].getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (contactList[position].getStatus() == 3) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_online);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		}
		if (contactList[position].getStatus() == 4) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		}
		if (contactList[position].getStatus() == 0) {
			holder.name.setTextColor(Color.BLACK);
			//holder.icon.setBackgroundResource(R.drawable.unavailable);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		} else if (contactList[position].getStatus() == 1) {
			holder.name.setTextColor(Color.BLACK);
			holder.icon.setBackgroundResource(R.drawable.icons_status_online);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));
		} else if (contactList[position].getStatus() == 2) {
			holder.name.setTextColor(Color.BLACK);
			//holder.icon.setBackgroundResource(R.drawable.unavailable);
			holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.white));

		}
		else if (contactList[position].getStatus() == 5) {
			holder.name.setTextColor(c.getResources().getColor(R.color.green_user_group));
			holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
			convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}
		else if ( contactList[position].getStatus() == 6) {
			holder.name.setTextColor(c.getResources().getColor(R.color.tribewire_grey));
			holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
            convertView.setBackgroundColor(c.getResources().getColor(R.color.lighgrey));
		}		

		
		


		try {

			holder.contactPic = (ImageView) convertView
					.findViewById(R.id.contact_pic);


			
			// Leave this because no image is coming from Server right now 
			// will be uncommented in future   24-08-2013  15:43
			
			
			if (contactList[position].getImageUrl()!=null) {
				if (contactList[position].getImageUrl()!=null && contactList[position].getImageUrl().length()>0) {
//					ImageDownloader1.getInstance().download(
//							imageUrl[position].trim(),holder.contactPic);	
					checkImage(contactList[position].getImageUrl(), holder.contactPic);
				}
				else {
					
					 if (contactList[position].getStatus() == 5) {
				
						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.group));

					}
					 else if (contactList[position].getStatus() == 6) {
							
						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.tab_group_n));

					}
					 else {
//								holder.name.setTextColor(Color.BLACK);
//							holder.icon.setBackgroundResource(R.drawable.messagebluearrow);

						 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
					 }

				}
			}
			else {
//				holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
				 if (contactList[position].getStatus() == 5) {
				
					 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.group));

				}
				 else if (contactList[position].getStatus() == 6) {
						
					 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.tab_group_n));

				}
				 else {
					 holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));

				 }
			}
			
//			holder.phone_number.setText(""+phoneNumner[position]);
//			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
//					Uri.encode(phoneNumner[position]));
//			LocalImageDownloader.getInstance().download(uri.toString(),
//					holder.contactPic, c);
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		// holder.icon.setImageBitmap(BitmapFactory.decodeResource(c.getResources(),
		// ICON[position]));

		return convertView;

	
		
	}
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contactList.length;
	}
	
	@Override
	public ContactList getItem(int position) {
		// TODO Auto-generated method stub
		return  contactList[position];
	}
	
	@Override
	public long getItemId(int position) {
		
		return position;
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
}
