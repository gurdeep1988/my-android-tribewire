package eyesay.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.TribeWireUtil;
import com.tribewire.app.GroupMembers;
import com.tribewire.app.GroupMembers.Model;
import com.tribewire.app.GroupMembers.MyTouchListener;
import com.tribewire.app.R;

public class GroupMemberAdapter extends BaseAdapter {

	private Context context;
	List<Integer> ID;
	List<String> NAME;
	List<Integer> STATUS;
	List<String> phoneNumner;

	List<String> imageUrl;
	List<Model> models;
	private MyTouchListener mOnTouchListener;
	boolean isSelected[];
	static Typeface tf;

	public GroupMemberAdapter(Context context, List<String> name,
			List<Integer> status, List<Integer> id, List<String> phoneNumner,
			List<String> imageUrl, List<Model> models,
			MyTouchListener mOnTouchListener, boolean isSelected[]) {
		this.context = context;
		this.NAME = name;
		this.STATUS = status;
		this.ID = id;
		this.phoneNumner = phoneNumner;
		this.imageUrl = imageUrl;
		this.models = models;
		this.mOnTouchListener = mOnTouchListener;
		this.isSelected = isSelected;
        tf = TribeWireUtil.getArialTypeface(context);
	}

	public void changeData(Context context, List<String> name,
			List<Integer> status, List<Integer> id, List<String> phoneNumner,
			List<String> imageUrl, List<Model> models, boolean isSelected[]) {
		this.context = context;
		this.NAME = name;
		this.STATUS = status;
		this.ID = id;
		this.phoneNumner = phoneNumner;
		this.imageUrl = imageUrl;
		this.models = models;
		this.isSelected = isSelected;
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {

		return NAME.size();
	}

	@Override
	public Object getItem(int position) {

		return NAME.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		GroupMemberWrapper groupMemberWrapper = null;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.friendlist, null);
			groupMemberWrapper = new GroupMemberWrapper(view);
			view.setTag(R.layout.friendlist, groupMemberWrapper);
			groupMemberWrapper.getBtnRemove().setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							int position = (Integer) v.getTag();
							models.get(position).setVisible(false);
							isSelected[position] =false;
							((GroupMembers) context)
									.deleteMemberFromGroup(position);

						}
					});

		} else {
			view = convertView;
			groupMemberWrapper = (GroupMemberWrapper) view
					.getTag(R.layout.friendlist);
		}
		view.setBackgroundColor(context.getResources().getColor(
				R.color.lighgrey));
		try {
			if (isSelected[position] == true) {
				view.setBackgroundColor(context.getResources().getColor(
						R.color.white));

			} else {
				view.setBackgroundColor(context.getResources().getColor(
						R.color.lighgrey));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		view.setTag(position);
		view.setOnTouchListener(mOnTouchListener);

		groupMemberWrapper.getBtnRemove().setTag(position);
		if (this.models.get(position) != null) {
			try {
				if (models.get(position).isVisible())
					groupMemberWrapper.getBtnRemove().setVisibility(
							View.VISIBLE);
				else
					groupMemberWrapper.getBtnRemove().setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		TextView textView = groupMemberWrapper.getTxtName();
		try {
			textView.setText("" + NAME.get(position));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageView imageStatus = groupMemberWrapper.getImgStatus();
		ImageView profilePic = groupMemberWrapper.getImgcontactPic();
		if (imageUrl != null) {
			if (imageUrl.get(position) != null
					&& imageUrl.get(position).length() > 0) {
//				ImageDownloader1.getInstance().download(
//						imageUrl.get(position).trim(), profilePic);
				checkImage(imageUrl.get(position), profilePic);
			} else {

				profilePic.setImageDrawable(context.getResources().getDrawable(
						R.drawable.no_image));

			}
		} else {

			profilePic.setImageDrawable(context.getResources().getDrawable(
					R.drawable.no_image));

		}

		try {
			int status = STATUS.get(position);
			if (status == 3) {
				textView.setTextColor(Color.BLACK);
				imageStatus.setBackgroundResource(R.drawable.icons_status_online);
			}
			if (status == 4) {
				textView.setTextColor(Color.BLACK);
				imageStatus.setBackgroundResource(R.drawable.icons_status_offline);
			}
			if (status == 0) {
				textView.setTextColor(Color.BLACK);
				imageStatus.setBackgroundResource(R.drawable.icons_status_offline);

			} else if (status == 1) {
				textView.setTextColor(Color.BLACK);
				imageStatus.setBackgroundResource(R.drawable.icons_status_online);

			} else if (status == 2) {
				textView.setTextColor(Color.BLACK);
				imageStatus.setBackgroundResource(R.drawable.icons_status_offline);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	private boolean checkImage(String url , ImageView imageView) {
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
			Log.e("", "=============The file Path is " + f.toString());
			if (f.exists()) {
				isExists  = true;
				
				Log.e("", "=========== File exists " + f.exists());
				imageView.setImageURI(Uri.parse(f.toString()));
			}
			else {
				isExists    = false;
				ImageDownloader1.getInstance().download(
						url.trim(),imageView,context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
           return isExists ;
	}
	public static class GroupMemberWrapper {

		View view;
		ImageView ImgcontactPic;
		TextView txtName;
		ImageView imgStatus;
		Button btnRemove;


		public GroupMemberWrapper(View view) {
			this.view = view;
		}

		public Button getBtnRemove() {
			if (btnRemove == null) {
				btnRemove = (Button) view.findViewById(R.id.btn_remove);

			}
			return btnRemove;
		}

		public ImageView getImgcontactPic() {
			if (ImgcontactPic == null) {
				ImgcontactPic = (ImageView) view.findViewById(R.id.contact_pic);
			}
			return ImgcontactPic;
		}

		public ImageView getImgStatus() {
			if (imgStatus == null) {
				imgStatus = (ImageView) view.findViewById(R.id.imgstatus);
			}
			return imgStatus;
		}

		public TextView getTxtName() {

			if (txtName == null) {
				txtName = (TextView) view.findViewById(R.id.text);
				txtName.setTypeface(tf);
			}

			return txtName;
		}

	}

}
