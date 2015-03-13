package eyesay.adapter;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter class for displaying Simple list 
 * 
 * @author xtpl
 *
 */
public class SimpleAdapter extends BaseAdapter {
   Context c;
   private LayoutInflater mInflater;
   boolean acticon = false;
   String msg="";

   

   public SimpleAdapter(Context context, String msg) {
      c = context;
      mInflater = LayoutInflater.from(context);
      this.msg=msg;
   }

   public int getCount() {
      return 1;
   }

   public Object getItem(int position) {
      return position;
   }

   public long getItemId(int position) {
      return position;
   }

   public View getView(int position, View convertView, ViewGroup parent) {
      convertView = mInflater.inflate(R.layout.simple_list_item_1, null);
      TextView tv=(TextView) convertView.findViewById(android.R.id.text1);      
      //CommonFunctions.setFont(c, tv,true,false);
      tv.setText(msg);
      return convertView;   }

}
