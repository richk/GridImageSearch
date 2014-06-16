package org.codepath.gridimagesearch;

import java.util.List;

import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ImageResultsArrayAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultsArrayAdapter(Context context, List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);
	}
	
	private static class ViewHolder {
        SmartImageView image;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageResult imageInfo = this.getItem(position);
		SmartImageView ivImage;
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflator = LayoutInflater.from(getContext());
		    convertView = inflator.inflate(R.layout.item_image_result, parent, false);
		    viewHolder.image = (SmartImageView) convertView.findViewById(R.id.sivImageResult);
		    convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.image.setImageUrl(imageInfo.getThumbUrl());
		return convertView;
	}

}
