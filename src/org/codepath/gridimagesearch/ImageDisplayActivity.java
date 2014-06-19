package org.codepath.gridimagesearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.loopj.android.image.SmartImageView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

public class ImageDisplayActivity extends Activity {
	
	private SmartImageView ivImage;
	private ShareActionProvider miShareAction;
	private String mFullUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		ImageResult image = (ImageResult) getIntent().getSerializableExtra("result");
		ivImage = (SmartImageView) findViewById(R.id.ivResult);
		mFullUrl = image.getFullUrl();
		ivImage.setImageUrl(mFullUrl);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
	    getMenuInflater().inflate(R.menu.image_display, menu);
	    // Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.menu_item_share);
	    // Fetch reference to the share action provider
	    miShareAction = (ShareActionProvider) item.getActionProvider();
	    Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.putExtra(Intent.EXTRA_TEXT, mFullUrl);
	    shareIntent.setType("text/plain");
	    miShareAction.setShareIntent(shareIntent);
	    // Return true to display menu
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

}
