package org.codepath.gridimagesearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ImageResult implements Serializable {
	private static final long serialVersionUID = 3094777985674780623L;

	private static final String LOG_TAG = ImageResult.class.getSimpleName();
	
	private String fullUrl;
	private String thumbUrl;
	
	public ImageResult(JSONObject json) {
		try {
			fullUrl = json.getString("url");
			thumbUrl = json.getString("tbUrl");
		} catch (JSONException e) {
			Log.d(LOG_TAG, "JSONException", e);
			fullUrl = null;
			thumbUrl = null;
		}
	}
	
	public ImageResult(String tUrl, String fUrl) {
		fullUrl = fUrl;
		thumbUrl = tUrl;
	}
	
	public String getFullUrl() {
		return fullUrl;
	}
	
	public String getThumbUrl() {
		return thumbUrl;
	}
	
	public static ArrayList<ImageResult> fromJSONArray(JSONArray imageJsonArray) {
		ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	    for (int i=0;i<imageJsonArray.length();++i) {
	    	ImageResult imageResult = null;
	    	try {
				imageResult = new ImageResult(imageJsonArray.getJSONObject(i));
				imageResults.add(imageResult);
			} catch (JSONException e) {
				Log.e(LOG_TAG, "JSONException", e);
			}
	    }
	    return imageResults;
	}
	
	@Override
	public String toString() {
		return thumbUrl + "," + fullUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ImageResult) {
		    ImageResult other = (ImageResult) o;
		    if (thumbUrl.equals(other.getThumbUrl()) && fullUrl.equals(other.getFullUrl())) {
		    	return true;
		    } else {
		    	return false;
		    }
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (fullUrl.hashCode() + thumbUrl.hashCode());
	}
	
}
