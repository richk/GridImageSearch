package org.codepath.gridimagesearch;

import java.io.Serializable;

public class ImageSettingsParams implements Serializable {
	private String mImageSize;
	private String mImageType;
	private String mColorFilter;
	private String mSiteFilter;
	
	public String getImageSize() {
		return mImageSize;
	}
	public void setImageSize(String imageSize) {
		mImageSize = imageSize;
	}
	public String getImageType() {
		return mImageType;
	}
	public void setImageType(String imageType) {
		mImageType = imageType;
	}
	public String getColorFilter() {
		return mColorFilter;
	}
	public void setColorFilter(String colorFilter) {
		mColorFilter = colorFilter;
	}
	public String getSiteFilter() {
		return mSiteFilter;
	}
	public void setSiteFilter(String siteFilter) {
		mSiteFilter = siteFilter;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Image Size").append("=").append(mImageSize).append(",");
		sb.append("Color Filter").append("=").append(mColorFilter).append(",");
		sb.append("Image Type").append("=").append(mImageType).append(",");
		sb.append("Site Filter").append("=").append(mSiteFilter);
		return sb.toString();
	}
}
