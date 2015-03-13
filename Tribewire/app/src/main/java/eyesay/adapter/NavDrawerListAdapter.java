package eyesay.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.SignOut;
import com.eyesayapp.model.Badge;
import com.eyesayapp.model.NavDrawerItem;
import com.tribewire.app.AddEditImage;
import com.tribewire.app.MenuActivity;
import com.tribewire.app.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	ArrayList<NavDrawerItem> drawerItems;

	public NavDrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {

		this.context = context;
		this.drawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {

		return drawerItems.size();
	}

	@Override
	public Object getItem(int position) {

		return drawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(R.layout.list_item_menu, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			// view = convertView;
			viewHolder = (ViewHolder) convertView.getTag();

		}
		if (position == 0) {
			viewHolder.getLinearLayout().setVisibility(View.VISIBLE);
			viewHolder.getIconLayout().setVisibility(View.GONE);
            viewHolder.getTxtVersionNumber().setVisibility(View.VISIBLE);  
			setDefaultImage(viewHolder.getImageProfilePic());
			setuserName(viewHolder.getTxtUserName(), viewHolder.getTxtStatus() , viewHolder.getTxtStatusOnline());
			
			try {
				viewHolder.getTxtLogout().setTypeface(TribeWireUtil.getArialTypeface(context));
			} catch (Exception e) {
				// TODO: handle exception
			}
			viewHolder.getTxtLogout().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					signOut();

				}
			});

		} else {
			viewHolder.getIconLayout().setVisibility(View.VISIBLE);
			viewHolder.getLinearLayout().setVisibility(View.GONE);
			viewHolder.getTxtVersionNumber().setVisibility(View.GONE);
		}

		try {
			if (drawerItems.get(position).isBadge()) {
			//	viewHolder.getBtnBadgeGreen().setVisibility(View.VISIBLE);
				if (position == 1) {
					if (getAllConvUnreadCount()>0) {
						viewHolder.getBtnBadgeGreen().setVisibility(View.VISIBLE);
						viewHolder.getBtnBadgeGreen().setText(""+getAllConvUnreadCount());
					}
					else {
						viewHolder.getBtnBadgeGreen().setVisibility(View.GONE);
					}
					
					
					
				} else if (position == 2) {
					if (getFyiUnreadCount()>0) {
						viewHolder.getBtnBadgeGreen().setVisibility(View.VISIBLE);
						viewHolder.getBtnBadgeGreen().setText(""+getFyiUnreadCount());
					}
					else {
						viewHolder.getBtnBadgeGreen().setVisibility(View.GONE);
					}
				}
			} else {
				viewHolder.getBtnBadgeGreen().setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!(position == 0)) {
			try {
				viewHolder.getTxtTitle().setText(
						drawerItems.get(position).getTitle());
				viewHolder.getImgTitleIcon().setImageDrawable(
						context.getResources().getDrawable(
								drawerItems.get(position).getIconId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

	private int getAllConvUnreadCount() {
		 int unreadCount   = 0;
		 DataBase db = null;
		 try {
			db = new DataBase(context);
			db.open();
		unreadCount =	db.getCount(DataBase.emergency_table, DataBase.emergency_int, "isread  = 0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		 finally {
			 if (db!=null) {
				db.close();
			}
		 }
		 return unreadCount;

	}
	private int getFyiUnreadCount() {
		int unreadCount   = 0;
		 DataBase db = null;
		 try {
			db = new DataBase(context);
			db.open();
		    unreadCount =	db.getCount(DataBase.Company_Fyi_table, DataBase.Company_Fyi_int, "status  = 0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		 finally {
			 if (db!=null) {
				db.close();
			}
		 }
		 return unreadCount;
	}
	
	private void signOut() {
        try {
			if (context instanceof MenuActivity) {
				MenuActivity menuActivity    = (MenuActivity)context;
		
				if (CommonFunctions.isInternetConnected(context)) {
					menuActivity.handler.sendEmptyMessage(MenuActivity.SHOW_PROGRESS);
					new SignOut(context).start();
				}
				else {
					menuActivity.handler.sendEmptyMessage(MenuActivity.NO_INTERNET_CONNECTION);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		

	}

	private void setDefaultImage(ImageView imgProfilePic) {
		try {
			String path = CommonFunctions.getPref(context,
					AddEditImage.LOCAL_PROFILE_PIC, "");
			if (path != null && path.length() > 0) {
				File image = new File(path);
				if (image.exists()) {

//					imgProfilePic.setImageBitmap(BitmapFactory.decodeFile(image
//							.getAbsolutePath()));
				Bitmap bitmap = 	getBitmap(Uri.fromFile(image));
					imgProfilePic.setImageBitmap(TribeWireUtil.rotateBitmap(image.toString(), bitmap));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private Bitmap getBitmap(Uri uri) {

		// Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 700000; // 1.2MP
			in =context. getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
//			Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth
//					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in =context
					.getContentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
//				Log.d(TAG, "1th scale operation dimenions - width: " + width
//						+ ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}

			in.close();
			Log.e("", "");
			return b;
		} catch (IOException e) {
			//Log.e(TAG, e.getMessage(), e);
			e.printStackTrace();
			return null;
		}
	}
	private void setuserName(TextView txtUsername, TextView status , TextView statusOnline) {
		try {
			String fName = CommonFunctions.getPref(context,
					Constants.FIRST_NAME, "");
			String lName = CommonFunctions.getPref(context,
					Constants.LAST_NAME, "");
			txtUsername.setText(fName + " " + lName);
			txtUsername
					.setTypeface(TribeWireUtil.getArialTypeface(context));
			status.setText("Status: ");
			statusOnline.setText("On Mobile");
			status.setTypeface(TribeWireUtil.getArialTypeface(context));
			statusOnline.setTypeface(TribeWireUtil.getArialTypeface(context));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static class ViewHolder {

		View view;
		private TextView txtTitle;
		private ImageView imgTitleIcon;
		private LinearLayout linearLayout;
		private ImageView imageProfilePic;
		private TextView txtUserName;
		private TextView txtLogout;
		private RelativeLayout iconLayout;
		private TextView btnBadgeGreen;
		private TextView txtStatus;
		private TextView txtStatusOnline;
		private TextView txtVersionNumber;
		
		public TextView getTxtVersionNumber() {
		     if (txtVersionNumber == null) {
				txtVersionNumber  = (TextView)view.findViewById(R.id.txtVersionNumber);
			}
			return txtVersionNumber;
		}
		public TextView getTxtStatus() {
		         if (txtStatus == null) {
					txtStatus  = (TextView)view.findViewById(R.id.txt_status);
				}
			return txtStatus;
		}
		
		public TextView getTxtStatusOnline() {
			if (txtStatusOnline  == null) {
				txtStatusOnline   = (TextView)view.findViewById(R.id.txt_status_online);
			}
			
			return txtStatusOnline;
		}
		public RelativeLayout getIconLayout() {
			if (iconLayout == null) {
				iconLayout = (RelativeLayout) view
						.findViewById(R.id.con_allconversation);
			}
			return iconLayout;
		}

		public TextView getBtnBadgeGreen() {
			if (btnBadgeGreen == null) {
				btnBadgeGreen = (TextView) view
						.findViewById(R.id.btn_badge_green);
			}
			return btnBadgeGreen;
		}

		public ViewHolder(View view) {
			this.view = view;
		}

		public TextView getTxtTitle() {
			if (txtTitle == null) {
				txtTitle = (TextView) view.findViewById(R.id.txt_title);
			}
			return txtTitle;
		}

		public ImageView getImgTitleIcon() {
			if (imgTitleIcon == null) {
				imgTitleIcon = (ImageView) view.findViewById(R.id.img_icon);
			}
			return imgTitleIcon;
		}

		public LinearLayout getLinearLayout() {
			if (linearLayout == null) {
				linearLayout = (LinearLayout) view
						.findViewById(R.id.con_profile_data);
			}
			return linearLayout;
		}

		public ImageView getImageProfilePic() {
			if (imageProfilePic == null) {
				imageProfilePic = (ImageView) view
						.findViewById(R.id.img_profile_local);
			}
			return imageProfilePic;
		}

		public TextView getTxtUserName() {
			if (txtUserName == null) {
				txtUserName = (TextView) view.findViewById(R.id.txt_username);
			}
			return txtUserName;
		}

		public TextView getTxtLogout() {
			if (txtLogout == null) {
				txtLogout = (TextView) view.findViewById(R.id.txt_sign_out);
			}
			return txtLogout;
		}

	}
}
