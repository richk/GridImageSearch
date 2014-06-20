package org.codepath.gridimagesearch;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ImageSettingsDialogFragment extends DialogFragment implements OnClickListener{
	private static final String LOG_TAG = ImageSettingsDialogFragment.class.getSimpleName();
	
	private Spinner etImageSize;
	private Spinner etColorFilter;
	private Spinner etImageType;
	private EditText etSiteFilter;
	private Button btnSaveButton;
	
	private ArrayAdapter<CharSequence> mImageSizeAdapter;
	private ArrayAdapter<CharSequence> mImageColorAdapter;
	private ArrayAdapter<CharSequence> mImageTypeAdapter;
	private ImageSettingsParams mImageParams;
	
	private Context mContext;

	public ImageSettingsDialogFragment() {
	}
	
	public static ImageSettingsDialogFragment newInstance(Context context, ImageSettingsParams params) {
		ImageSettingsDialogFragment fragment = new ImageSettingsDialogFragment();
	    fragment.setContext(context);
	    fragment.setImageParams(params);
	    Bundle args = new Bundle();
	    fragment.setArguments(args);
	    return fragment;
	}
	
	public void setImageParams(ImageSettingsParams params) {
		mImageParams = params;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(LOG_TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_settings, container);
		setupViews(view);
		loadViews();
		getDialog().setTitle("Advanced Search Options");
		getDialog().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}
	
	private void loadViews() {
		if (mImageParams != null) {
	    	if (mImageParams.getImageSize() != null) { 
	    		int itemPos = mImageSizeAdapter.getPosition(mImageParams.getImageSize());
	    		etImageSize.setSelection(itemPos);
	    	}
	    	if (mImageParams.getColorFilter() != null) {
	    		int itemPos = mImageColorAdapter.getPosition(mImageParams.getColorFilter());
	    		etColorFilter.setSelection(itemPos);
	    	}
	    	if (mImageParams.getImageType() != null) {
	    		int itemPos = mImageTypeAdapter.getPosition(mImageParams.getImageType());
	    		etImageType.setSelection(itemPos);
	    	}
	    	if (mImageParams.getSiteFilter() != null) {
	    		etSiteFilter.setText(mImageParams.getSiteFilter());
	    	}
	    }
	}

	public void setupViews(View view) {
		etImageSize = (Spinner) view.findViewById(R.id.etImageSize);
		mImageSizeAdapter = ArrayAdapter.createFromResource(mContext,
		        R.array.image_size_array, android.R.layout.simple_spinner_item);
		mImageSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etImageSize.setAdapter(mImageSizeAdapter);
		etColorFilter = (Spinner) view.findViewById(R.id.etColorFilter);
		mImageColorAdapter = ArrayAdapter.createFromResource(mContext,
		        R.array.image_color_array, android.R.layout.simple_spinner_item);
		mImageColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etColorFilter.setAdapter(mImageColorAdapter);
		etImageType = (Spinner) view.findViewById(R.id.etImageType);
		mImageTypeAdapter = ArrayAdapter.createFromResource(mContext,
		        R.array.image_type_array, android.R.layout.simple_spinner_item);
		mImageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		etImageType.setAdapter(mImageTypeAdapter);
		etSiteFilter = (EditText) view.findViewById(R.id.etSiteFilter);
		btnSaveButton = (Button) view.findViewById(R.id.btnSaveSettings);
		btnSaveButton.setOnClickListener(this);
		etImageSize.requestFocus();
	}
	
	public interface ImageSettingsListener {
        void onSettingsSave(ImageSettingsParams imageSettingsParams);
    }
	
	@Override
	public void onClick(View v) {
		ImageSettingsListener listener = (ImageSettingsListener) getActivity();
		ImageSettingsParams imageSettingsParams = new ImageSettingsParams();
	    String imageSize = (String) etImageSize.getSelectedItem();
	    String colorFilter = (String) etColorFilter.getSelectedItem();
	    String imageType = (String) etImageType.getSelectedItem();
	    String siteFilter = etSiteFilter.getText() == null?ImageSearchContracts.DEFAULT_SITE_FILTER:etSiteFilter.getText().toString();
	    imageSettingsParams.setImageSize(imageSize);
	    imageSettingsParams.setColorFilter(colorFilter);
	    imageSettingsParams.setImageType(imageType);
	    imageSettingsParams.setSiteFilter(siteFilter);
		listener.onSettingsSave(imageSettingsParams);
		dismiss();
	}
}
