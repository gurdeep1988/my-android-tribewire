package eyesay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tribewire.app.R;

/**
 * Adpater class for Contact screen
 * 
 * @author xtpl
 * 
 */
public class ContactAdapter extends BaseAdapter {

	static class ViewHolder {
		CheckBox id;
		TextView name;
		ImageView img;

	}

	Context c;
	private LayoutInflater mInflater;
	String[] ID;
	String[] NAME;
	boolean[] flags;
	boolean acticon = false;

	public ContactAdapter() {
		// TODO Auto-generated constructor stub
	}

	public ContactAdapter(Context context, String id[], String[] name,
			boolean[] flag) {
		c = context;
		ID = id;
		NAME = name;
		flags = flag;
		mInflater = LayoutInflater.from(context);

	}

	public ContactAdapter(Context context, String id[], String[] name,
			boolean[] flag, boolean icons) {
		c = context;
		ID = id;
		NAME = name;
		flags = flag;
		acticon = icons;
		mInflater = LayoutInflater.from(context);

	}

	public int getCount() {
		return NAME.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// ViewHolder holder;
		// View view;
		// if (convertView == null) {
		// convertView = mInflater.inflate(R.layout.contacts, null);
		// holder = new ViewHolder();
		// holder.id = (CheckBox) convertView.findViewById(R.id.chk);
		// holder.id.setFocusable(false);
		// holder.name = (TextView) convertView.findViewById(R.id.text);
		// holder.img = (ImageView) convertView.findViewById(R.id.imgperson);
		// CommonFunctions.setFont(c, holder.name,true,false);
		//
		// convertView.setTag(holder);
		// } else
		// holder = (ViewHolder) convertView.getTag();
		//
		// holder.id.setTag(ID[position]);
		// holder.name.setText(NAME[position]);
		// if (flags[position]) {
		// holder.id.setChecked(true);
		// } else {
		// holder.id.setChecked(false);
		// }
		// if (acticon) {
		// holder.img.setBackgroundResource(R.drawable.contact_a);
		// }

		View view = null;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.contacts, parent,false);
			view.setTag(new ContactHolder(view));
		} else {
			view = convertView;
		}
		ContactHolder contactHolder = (ContactHolder) view.getTag();
		CheckBox checkBox = contactHolder.getCheckBox();
		ImageView imageView = contactHolder.getImageView();
		TextView textView = contactHolder.getTextView();
//		Button btn_selector  = contactHolder.getButtonSelector();
//		if (mSelectedPosition == position) {
//			imageView.setVisibility(View.VISIBLE);
//		    } else {
//		    	imageView.setVisibility(View.INVISIBLE);
//		    }   
//		btn_selector.setTag(false);
		
		checkBox.setFocusable(false);
		checkBox.setTag(ID[position]);
		textView.setText(NAME[position]);
		//CommonFunctions.setFont(c, textView, true, false);
		if (flags[position]) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}
		if (acticon) {
			imageView.setBackgroundResource(R.drawable.contact_a);
		}
		 if (position == getSelectedPosition()) {
		       view.setBackgroundColor(Color.BLUE);
		    } 
		 		//Log.e("", "The View is "+view);
		return view;

	}

	private class ContactHolder {
		private TextView textView;
		private CheckBox checkBox;
		private ImageView imageView;
		private View view;
		
		private Button btn_selector;

		public ContactHolder(View view) {
			this.view = view;
		}

		public Button getButtonSelector() {
//			if (btn_selector == null) {
//				btn_selector = (Button) view.findViewById(R.id.btn_selector);
//			}
			return btn_selector;
		}
		public TextView getTextView() {
			if (textView == null) {
				textView = (TextView) view.findViewById(R.id.text);
			}
			return textView;
		}

		public CheckBox getCheckBox() {
			if (checkBox == null) {
				checkBox = (CheckBox) view.findViewById(R.id.chk);
			}
			return checkBox;
		}

		public ImageView getImageView() {
			if (imageView == null) {
				imageView = (ImageView) view.findViewById(R.id.imgperson);
			}
			return imageView;
		}

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
}
