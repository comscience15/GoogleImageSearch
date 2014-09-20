package com.comscience15.googleimagesearch.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comscience15.googleimagesearch.R;
import com.comscience15.googleimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {
	// create a constructor
	public ImageResultsAdapter(Context context,List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// custom following item_image_result.xml
		ImageResult imgInfo = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
		}
		ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
		// clear out image from last time
		ivImage.setImageResource(0);
		// populate title and remote download image url
		tvTitle.setText(Html.fromHtml(imgInfo.title));
		// remotely download the image data in the background (using Picaso)
		Picasso.with(getContext()).load(imgInfo.tbUrl).into(ivImage);
		// return completed view to be displayed
		return  convertView;
	}
}
