package com.example.grubber;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TopFoodAdapter extends ArrayAdapter<TopFoodContent>{
	private ArrayList<TopFoodContent> foodList;
	private ImageLoader imageLoader;
	public TopFoodAdapter(Context context, ArrayList<TopFoodContent> resultsList) {
		super (context, R.layout.topfood_list_item, resultsList);
		this.foodList = resultsList;
		imageLoader = new ImageLoader(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		// if null, inflate/render it
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.topfood_list_item, null);
		}
		
		TopFoodContent res = foodList.get(position);
		
		TextView tname = (TextView) row.findViewById(R.id.topfood_name);
		TextView trestname = (TextView) row.findViewById(R.id.toprest_name);
		TextView tvote = (TextView) row.findViewById(R.id.topfood_vote);
		//TextView tcomment = (TextView) row.findViewById(R.id.topfood_comments);

		ImageView icon = (ImageView) row.findViewById(R.id.food_icon);

		//show picture
		String picurl = "http://cse190.myftp.org/picture/"+ res.getFoodId() + ".jpg";
		imageLoader.DisplayImage(picurl, icon);

 		if (tname != null) {
	 		String n = res.getFoodName();
			if(n.length() > 22)
				tname.setText(n.substring(0, 22) + "...");
			else
				tname.setText(n); 
 		}
		if (trestname != null) {
			String n = res.getRestName();
			if(n.length() > 17)
				trestname.setText(n.substring(0, 17) + "...");
			else
				trestname.setText(n); 
		}
		if (tvote != null) {
			tvote.setText(res.getVote());
		}	
				
		return row;
	}
}
