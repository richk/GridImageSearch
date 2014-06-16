package org.codepath.gridimagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class GridImageSearchActivity extends Activity {
	private static final String LOG_TAG = GridImageSearchActivity.class.getSimpleName();
	private static final String GIS_BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
	private static final String GIS_SIZE_PARAM = "imgsz";
	private static final String GIS_COLOR_PARAM = "imgcolor";
	private static final String GIS_TYPE_PARAM = "imgtype";
	private static final String GIS_SITE_PARAM = "as_sitesearch";
	private static final int PAGESIZE = 8;
	
	private GridView gvResults;
	private ImageResultsArrayAdapter imageResultsArrayAdapter;
	private ImageSettingsParams mImageSettingsParams;
	private ImageView ivBackgroundImage;
	
	private List<ImageResult> imageResults = new ArrayList<ImageResult>(); 
	private boolean mNoQueryRefresh = false;
	private String mCurrentQuery;
	private int mResultCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_image_search);
		setupViews();
		imageResultsArrayAdapter = new ImageResultsArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageResultsArrayAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long arg3) {
			    Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
			    ImageResult imageResult = imageResults.get(position);
			    i.putExtra("result", imageResult);
			    startActivity(i);
			}
		});
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
	            customLoadMoreDataFromApi(totalItemsCount); 
			}
		});
		mImageSettingsParams = new ImageSettingsParams();
	}
	
	public void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);
		ivBackgroundImage = (ImageView) findViewById(R.id.ivBackgroundImage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				Log.e(LOG_TAG, "Action View Expanded");
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				Log.e(LOG_TAG, "Action View Collapse");
				mNoQueryRefresh = true;
				return true;
			}
		});
	    SearchView searchView = (SearchView) searchItem.getActionView();
	    if (searchView != null) {
	    	Log.e(LOG_TAG, "Setting actions on search view");
	    	searchView.setOnQueryTextListener(new OnQueryTextListener() {
	    		@Override
	    		public boolean onQueryTextSubmit(String query) {
	    			Log.e(LOG_TAG, "onQuerySubmit. Query=" + query);
	    			mCurrentQuery = query;
	    			newSearch(query);
	    			return true;
	    		}

	    		@Override
	    		public boolean onQueryTextChange(String newText) {
	    			Log.e(LOG_TAG, "onQueryTextChange. Query=" + newText);
	    			mCurrentQuery = newText;
	    			newSearch(newText);
	    			return false;
	    		}
	    	});
	    }
	    return true;
	}
	
	public void onSettings(MenuItem v) {
		Intent i = new Intent(this, ImageSettingsActivity.class);
		i.putExtra("settings", mImageSettingsParams);
		mNoQueryRefresh = true;
		startActivityForResult(i, 40);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 40 && resultCode == RESULT_OK) {
			Log.d(LOG_TAG, "Updated Settings:" + data.getSerializableExtra("settings"));
			mImageSettingsParams = (ImageSettingsParams) data.getSerializableExtra("settings");
		}
	}

	public void newSearch(String query) {
		Log.e(LOG_TAG, "New Search:" + query);
		ivBackgroundImage.setVisibility(View.GONE);
		if (mNoQueryRefresh == true) {
			Log.e(LOG_TAG, "Action View collapsed");
			mNoQueryRefresh = false;
			return;
		}
		if (query != null) {
			imageResultsArrayAdapter.clear();
			mResultCount = 0;
			search(query, 0);
		}
	}
	
	public void search(String query, int offset) {
		Log.e(LOG_TAG, "Search:" + query);
		AsyncHttpClient ayAsyncHttpClient = new AsyncHttpClient();
	    ayAsyncHttpClient.get(getImageSearchUrl(query, offset), 
	    		new JsonHttpResponseHandler() {
	    	        @Override
	    	        public void onSuccess(JSONObject response) {
	    	        	JSONArray imageJsonResults = null;
	    	        	Log.d(LOG_TAG, "Result:" + response.toString());
	    	        	try {
	    	        		imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
	    	        		imageResultsArrayAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
	    	        		mResultCount = imageResultsArrayAdapter.getCount();
	    	        		Log.d(LOG_TAG, imageResults.toString());
	    	        		Log.e(LOG_TAG, "Result Count:" + mResultCount);
	    	        	} catch(JSONException je) {
	    	        		Log.e(LOG_TAG, "JSONException", je);
	    	        	}
	    	        }
	    });
	}
	
	public String getImageSearchUrl(String query, int offset) {
		StringBuilder sb = new StringBuilder();
	    sb.append(GIS_BASE_URL);
	    sb.append("?rsz=8&v=1.0&q=").append(Uri.encode(query));
	    sb.append("&start=").append(offset);
	    if (mImageSettingsParams != null) {
	    	if (mImageSettingsParams.getImageSize() != null && !"None".equals(mImageSettingsParams.getImageSize())) { 
	    		sb.append("&").append(GIS_SIZE_PARAM).append("=").append(mImageSettingsParams.getImageSize());
	    	}
	    	if (mImageSettingsParams.getColorFilter() != null && !"None".equals(mImageSettingsParams.getColorFilter())) {
	    		sb.append("&").append(GIS_COLOR_PARAM).append("=").append(mImageSettingsParams.getColorFilter());
	    	}
	    	if (mImageSettingsParams.getImageType() != null  && !"None".equals(mImageSettingsParams.getImageType())) {
	    		sb.append("&").append(GIS_TYPE_PARAM).append("=").append(mImageSettingsParams.getImageType());
	    	}
	    	if (mImageSettingsParams.getSiteFilter() != null) {
	    		sb.append("&").append(GIS_SITE_PARAM).append("=").append(mImageSettingsParams.getSiteFilter());
	    	}
	    }
	    Log.d(LOG_TAG, "Search Url:" + sb.toString());
	    return sb.toString();
	}
	
    public void customLoadMoreDataFromApi(int offset) {
    	if (mCurrentQuery != null) {
    		search(mCurrentQuery, offset);	
    	}
    }
    
    public void hideSoftKeyboard(View view){
		  InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
}
