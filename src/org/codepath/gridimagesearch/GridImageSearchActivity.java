package org.codepath.gridimagesearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class GridImageSearchActivity extends Activity {
	private static final String LOG_TAG = GridImageSearchActivity.class.getSimpleName();
	private static final String GIS_BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
	private static final String GIS_SIZE_PARAM = "imgsz";
	private static final String GIS_COLOR_PARAM = "imgcolor";
	private static final String GIS_TYPE_PARAM = "imgtype";
	private static final String GIS_SITE_PARAM = "as_sitesearch";
	private static final int PAGESIZE = 8;
	private static final String RECENT_SEARCH_FILE_NAME = "recent";
	
	private GridView gvResults;
	private ImageResultsArrayAdapter imageResultsArrayAdapter;
	private ImageSettingsParams mImageSettingsParams;
	
	private List<ImageResult> imageResults = new ArrayList<ImageResult>(); 
	private Set<ImageResult> recentSearches = new HashSet<ImageResult>();
	private boolean mNoQueryRefresh = false;
	private String mCurrentQuery = "";
	private int mResultCount;
	private LinearLayout mHomeScreen;

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
			    recentSearches.add(imageResult);
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
		if (savedInstanceState != null && savedInstanceState.getString("query") != null) {
			String query = savedInstanceState.getString("query");
			mCurrentQuery = query;
			newSearch(query);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		newSearch(mCurrentQuery);
	}
	
	public void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);
		mHomeScreen = (LinearLayout) findViewById(R.id.llHomeScreen);
	}
	
	protected void onSaveInstanceState(Bundle state) {
	    super.onSaveInstanceState(state);
	    state.putString("query", mCurrentQuery);
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				Log.e(LOG_TAG, "Action View Expanded");
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				Log.e(LOG_TAG, "Action View Collapse. Query:" + searchView.getQuery());
				Log.e(LOG_TAG, "Current Query:" + mCurrentQuery);
				mNoQueryRefresh = true;
				return true;
			}
		});
	    if (searchView != null) {
	    	Log.e(LOG_TAG, "Setting actions on search view");
	    	searchView.setOnQueryTextListener(new OnQueryTextListener() {
	    		@Override
	    		public boolean onQueryTextSubmit(String query) {
	    			Log.e(LOG_TAG, "onQuerySubmit. Query=" + query);
	    			mCurrentQuery = query;
	    			hideSoftKeyboard(searchView);
	    			mNoQueryRefresh = false;
	    			newSearch(query);
	    			return true;
	    		}

	    		@Override
	    		public boolean onQueryTextChange(String newText) {
	    			String query = newText.trim();
	    			Log.e(LOG_TAG, "New Query:" + query);
	    			Log.e(LOG_TAG, "New Query length:" + query.length());
	    			if (mNoQueryRefresh) {
	    				Log.e(LOG_TAG, "Not refresing query");
	    				mNoQueryRefresh = false;
	    			} else {
	    				if (query.isEmpty()) {
	    					Log.e(LOG_TAG, "onQueryTextChange. Empty text");
	    					mHomeScreen.setVisibility(View.VISIBLE);
	    					imageResultsArrayAdapter.clear();
	    					imageResultsArrayAdapter.notifyDataSetChanged();
	    				} else {
	    					mCurrentQuery = query;
	    					Log.e(LOG_TAG, "onQueryTextChange. Query=" + query);
	    					newSearch(query);
	    				}
	    			}
	    			return false;
	    		}
	    	});
	    }
	    return true;
	}
	
	public void onSettings(MenuItem v) {
		Intent i = new Intent(this, ImageSettingsActivity.class);
		i.putExtra("settings", mImageSettingsParams);
		startActivityForResult(i, 40);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 40 && resultCode == RESULT_OK) {
			Log.d(LOG_TAG, "Updated Settings:" + data.getSerializableExtra("settings"));
			mImageSettingsParams = (ImageSettingsParams) data.getSerializableExtra("settings");
			newSearch(mCurrentQuery);
		}
	}

	public void newSearch(String query) {
		Log.e(LOG_TAG, "New Search:" + query);
		if (mNoQueryRefresh == true) {
			Log.e(LOG_TAG, "Action View collapsed");
			mNoQueryRefresh = false;
			return;
		}
		if (query != null) {
			Log.e(LOG_TAG, "Cleared the adapter");
			imageResultsArrayAdapter.clear();
			mResultCount = 0;
			search(query, 0);
		}
	}
	
	public void search(final String query, final int offset) {
		Log.e(LOG_TAG, "Search:" + query);
		AsyncHttpClient ayAsyncHttpClient = new AsyncHttpClient();
		if (!isNetworkAvailable()) {
			Toast.makeText(this, "Network not available. Try again later", Toast.LENGTH_SHORT).show();
			return;
		}
		ayAsyncHttpClient.get(getImageSearchUrl(query, offset), 
				new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults = null;
				Log.d(LOG_TAG, "Result:" + response.toString());
				try {
					if (response.isNull("responseData")) {
						return;
					}
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					Log.e(LOG_TAG, "Response received for query:" + query);
					if (mCurrentQuery.equals(query)) {
						if (offset == 0) {
							imageResultsArrayAdapter.clear();
						}
						mHomeScreen.setVisibility(View.GONE);
						imageResultsArrayAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					}
					mResultCount = imageResultsArrayAdapter.getCount();
					Log.d(LOG_TAG, imageResults.toString());
					Log.e(LOG_TAG, "Result Count:" + mResultCount);
				} catch(JSONException je) {
					Log.e(LOG_TAG, "JSONException", je);
				}
			}

			@Override
			public void onFailure(Throwable e, JSONArray errorResponse){
				Log.e(LOG_TAG, "Error:" + errorResponse.toString());
				Toast.makeText(getApplicationContext(), "Error - please check your Internet connection", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure ( Throwable e, JSONObject errorResponse ) {
				Log.e(LOG_TAG, "Error:" + errorResponse.toString());
				Toast.makeText(getApplicationContext(), "Error - please check your Internet connection", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure ( Throwable e, String errorResponse ) {
				Log.e(LOG_TAG, "Error:" + errorResponse.toString());
				Toast.makeText(getApplicationContext(), "Error - please check your Internet connection", Toast.LENGTH_SHORT).show();
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
    
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
