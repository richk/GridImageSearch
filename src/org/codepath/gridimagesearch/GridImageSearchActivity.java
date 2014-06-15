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
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class GridImageSearchActivity extends Activity {
	private static final String LOG_TAG = GridImageSearchActivity.class.getSimpleName();
	private static final String GIS_BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
	private static final String GIS_SIZE_PARAM = "imgsz";
	private static final String GIS_COLOR_PARAM = "imgcolor";
	private static final String GIS_TYPE_PARAM = "imgtype";
	private static final String GIS_SITE_PARAM = "as_sitesearch";
	private static final int PAGESIZE = 8;
	
	private EditText etQuery;
	private Button btnSearch;
	private GridView gvResults;
	private ImageResultsArrayAdapter imageResultsArrayAdapter;
	private ImageSettingsParams mImageSettingsParams;
	
	List<ImageResult> imageResults = new ArrayList<ImageResult>(); 

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
		etQuery = (EditText) findViewById(R.id.etQuery);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		gvResults = (GridView) findViewById(R.id.gvResults);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
			String query = etQuery.getText().toString();
			imageResultsArrayAdapter.clear();
			search(query, 0);
		}
	}

	public void onImageSearch(View v) {
	    String query = etQuery.getText().toString();
	    Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
	    imageResultsArrayAdapter.clear();
	    search(query, 0);
	}
	
	public void search(String query, int offset) {
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
	    	        		Log.d(LOG_TAG, imageResults.toString());
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
    	String query = etQuery.getText().toString();
        search(query, offset/PAGESIZE);	
    }
}
