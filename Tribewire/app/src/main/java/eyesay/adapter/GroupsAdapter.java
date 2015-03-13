package eyesay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tribewire.app.AllGroups.Model;
import com.tribewire.app.AllGroups.MyTouchListener;
import com.tribewire.app.R;

public class GroupsAdapter extends BaseAdapter {

	private Context context;
	private String groupId[];
	private String groupName[];
	private String groupCount[];
	private int groupType[];
	private MyTouchListener myTouchListener;
	Model modelArray[];

	public GroupsAdapter(Context context, String groupId[], String groupName[],
			String groupCount[], int groupType[], Model modelArray[],
			MyTouchListener myTouchListener) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupCount = groupCount;
		this.groupType = groupType;
		this.modelArray = modelArray;
		this.context = context;

		this.myTouchListener = myTouchListener;
	}

	public void changeData(String groupId[], String groupName[],
			String groupCount[], int groupType[]) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupCount = groupCount;
		this.groupType = groupType;

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return groupId.length;
	}

	@Override
	public Object getItem(int position) {

		return groupId[position];
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_all_groups, null);
			
			holder.btnRemove = (Button) convertView
					.findViewById(R.id.btn_remove);
			holder.txtGroupName = (TextView) convertView
					.findViewById(R.id.txt_group_name);
			holder.txtGroupCount = (TextView) convertView
					.findViewById(R.id.txt_group_count);
			holder.relativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.rlt_group_bg);

			holder.btnPlusMore.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			convertView.setTag(R.layout.listitem_all_groups, holder);

		} else {
			holder = (ViewHolder) convertView
					.getTag(R.layout.listitem_all_groups);
		}
		convertView.setTag(position);
		convertView.setOnTouchListener(myTouchListener);

		holder.btnRemove.setTag(position);
		if (modelArray[position].isVisible())
			holder.btnRemove.setVisibility(View.VISIBLE);
		else
			holder.btnRemove.setVisibility(View.GONE);

		try {
			holder.txtGroupName.setText(groupName[position]);
			if (groupType[position] == 1) {
				holder.txtGroupName.setTextColor(Color.BLACK);
				holder.relativeLayout.setBackgroundColor(Color.WHITE);
				holder.btnPlusMore.setVisibility(View.INVISIBLE);
			} else {
				holder.txtGroupName.setTextColor(Color.DKGRAY);
				holder.relativeLayout.setBackgroundColor(Color.GRAY);
				holder.btnPlusMore.setVisibility(View.VISIBLE);
			}
			holder.txtGroupCount.setText(groupCount[position]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public class ViewHolder {
		public  TextView txtGroupName;
		public TextView txtGroupCount;
		public RelativeLayout relativeLayout;
		public Button btnPlusMore;
		public Button btnRemove;
	}

	public class GroupWrapper {

		private View view;
		private TextView txtGroupName;
		private TextView txtGroupCount;
		private RelativeLayout relativeLayout;
		private Button btnPlusMore;
		private Button btnRemove;

		public GroupWrapper(View view) {
			this.view = view;
		}

		public RelativeLayout getRelativeLayout() {
			if (relativeLayout == null) {
				relativeLayout = (RelativeLayout) view
						.findViewById(R.id.rlt_group_bg);
			}
			return relativeLayout;
		}

		public Button getBtnRemove() {
			if (btnRemove == null) {
				btnRemove = (Button) view.findViewById(R.id.btn_remove);
			}
			return btnRemove;
		}

		

		public TextView getTxtGroupCount() {
			if (txtGroupCount == null) {
				txtGroupCount = (TextView) view
						.findViewById(R.id.txt_group_count);
			}
			return txtGroupCount;
		}

		public TextView getTxtGroupName() {
			if (txtGroupName == null) {
				txtGroupName = (TextView) view
						.findViewById(R.id.txt_group_name);
			}
			return txtGroupName;
		}

	}

}
