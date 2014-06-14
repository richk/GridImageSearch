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
	private static final String GIS_RSZ_PARAM = "rsz";
	private static final String GIS_START_PARAM = "start";
	
	private EditText etQuery;
	private Button btnSearch;
	private GridView gvResults;
	private ImageResultsArrayAdapter imageResultsArrayAdapter;
	
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
		Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(this, ImageSettingsActivity.class);
		startActivity(i);
	}
	
	public void onImageSearch(View v) {
	    String query = etQuery.getText().toString();
	    Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
	    AsyncHttpClient ayAsyncHttpClient = new AsyncHttpClient();
	    StringBuilder sb = new StringBuilder();
	    sb.append(GIS_BASE_URL);
	    sb.append("?rsz=8&start=0&v=1.0&q=").append(Uri.encode(query));
	    Log.d(LOG_TAG, "Image query:" + sb.toString());
	    ayAsyncHttpClient.get(sb.toString(), 
	    		new JsonHttpResponseHandler() {
	    	        @Override
	    	        public void onSuccess(JSONObject response) {
	    	        	JSONArray imageJsonResults = null;
	    	        	Log.d(LOG_TAG, "Result:" + response.toString());
	    	        	try {
	    	        		imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
	    	        		imageResults.clear();
	    	        		imageResultsArrayAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
	    	        		Log.d(LOG_TAG, imageResults.toString());
	    	        	} catch(JSONException je) {
	    	        		Log.e(LOG_TAG, "JSONException", je);
	    	        	}
	    	        }
	    	
	    	
	    });
	}
}
