package com.example.grubber;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TopRestaurantAdapter extends ArrayAdapter<TopRestaurantContent>{
	private ArrayList<TopRestaurantContent> restList;
	private ImageLoader imageLoader;
	public TopRestaurantAdapter(Context context, ArrayList<TopRestaurantContent> resultsList) {
		super (context, R.layout.toprest_list_item, resultsList);
		this.restList = resultsList;
		imageLoader = new ImageLoader(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		// if null, inflate/render it
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.toprest_list_item, null);
		}
		
		TopRestaurantContent res = restList.get(position);
		
		TextView tname = (TextView) row.findViewById(R.id.toprestaurant_name);
		TextView taddr = (TextView) row.findViewById(R.id.toprest_addr);
		TextView tvote = (TextView) row.findViewById(R.id.toprest_vote);
		//TextView tcomment = (TextView) row.findViewById(R.id.topfood_comments);

		ImageView icon = (ImageView) row.findViewById(R.id.rest_icon);

		//show picture
		String picurl = "http://maps.googleapis.com/maps/api/streetview?size=150x150&location="+ res.getLongitude()+","+res.getLatitude() +"&fov=90&heading=235&pitch=10&sensor=false";
		imageLoader.DisplayImage(picurl, icon);

 		if (tname != null)
			 tname.setText(res.getRestName());
		
		if (taddr != null)
			 taddr.setText(res.getAddress() + ", " + res.getCity() + " " + res.getState());
		
		if (tvote != null) {
			tvote.setText(res.getVote());
		}	
				
		return row;
	}
}
