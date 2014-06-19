package org.codepath.gridimagesearch;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ImageSettingsActivity extends Activity {
	private static final String LOG_TAG = ImageSettingsActivity.class.getSimpleName();
	private Spinner etImageSize;
	private Spinner etColorFilter;
	private Spinner etImageType;
	private EditText etSiteFilter;
	private ArrayAdapter<CharSequence> imageSizeAdapter;
	private ArrayAdapter<CharSequence> imageColorAdapter;
	private ArrayAdapter<CharSequence> imageTypeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_settings);
		setupViews();
		ImageSettingsParams imageParams = (ImageSettingsParams) getIntent().getSerializableExtra("settings");
		if (imageParams != null) {
	    	if (imageParams.getImageSize() != null) { 
	    		int itemPos = imageSizeAdapter.getPosition(imageParams.getImageSize());
	    		etImageSize.setSelection(itemPos);
	    	}
	    	if (imageParams.getColorFilter() != null) {
	    		int itemPos = imageColorAdapter.getPosition(imageParams.getColorFilter());
	    		etColorFilter.setSelection(itemPos);
	    	}
	    	if (imageParams.getImageType() != null) {
	    		int itemPos = imageTypeAdapter.getPosition(imageParams.getImageType());
	    		etImageType.setSelection(itemPos);
	    	}
	    	if (imageParams.getSiteFilter() != null) {
	    		etSiteFilter.setText(imageParams.getSiteFilter());
	    	}
	    }
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	public void setupViews() {
		etImageSize = (Spinner) findViewById(R.id.etImageSize);
		imageSizeAdapter = ArrayAdapter.createFromResource(this,
		        R.array.image_size_array, android.R.layout.simple_spinner_item);
		imageSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etImageSize.setAdapter(imageSizeAdapter);
		etColorFilter = (Spinner) findViewById(R.id.etColorFilter);
		imageColorAdapter = ArrayAdapter.createFromResource(this,
		        R.array.image_color_array, android.R.layout.simple_spinner_item);
		imageColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etColorFilter.setAdapter(imageColorAdapter);
		etImageType = (Spinner) findViewById(R.id.etImageType);
		imageTypeAdapter = ArrayAdapter.createFromResource(this,
		        R.array.image_type_array, android.R.layout.simple_spinner_item);
		imageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etImageType.setAdapter(imageTypeAdapter);
		etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_settings, menu);
		return true;
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item){
	    
	    switch (item.getItemId()) {
	      // Respond to the action bar's Up/Home button
	      case android.R.id.home:
	        Intent intent = NavUtils.getParentActivityIntent(this); 
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP); 
	        NavUtils.navigateUpTo(this, intent);
	        return true;        
	    }
	    
	    return super.onOptionsItemSelected(item);
	  }
	
	public void onSaveImageSettings(View v) {
	    ImageSettingsParams imageSettingsParams = new ImageSettingsParams();
	    String imageSize = (String) etImageSize.getSelectedItem();
	    String colorFilter = (String) etColorFilter.getSelectedItem();
	    String imageType = (String) etImageType.getSelectedItem();
	    String siteFilter = etSiteFilter.getText() == null?ImageSearchContracts.DEFAULT_SITE_FILTER:etSiteFilter.getText().toString();
	    imageSettingsParams.setImageSize(imageSize);
	    imageSettingsParams.setColorFilter(colorFilter);
	    imageSettingsParams.setImageType(imageType);
	    imageSettingsParams.setSiteFilter(siteFilter);
	    Intent i = new Intent();
	    i.putExtra("settings", imageSettingsParams);
	    setResult(RESULT_OK, i);
	    finish();
	}
}
