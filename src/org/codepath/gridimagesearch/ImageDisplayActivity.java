package org.codepath.gridimagesearch;

import com.loopj.android.image.SmartImageView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ImageDisplayActivity extends Activity {
	
	private SmartImageView ivImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		ImageResult image = (ImageResult) getIntent().getSerializableExtra("result");
		ivImage = (SmartImageView) findViewById(R.id.ivResult);
		ivImage.setImageUrl(image.getFullUrl());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		return true;
	}

}
