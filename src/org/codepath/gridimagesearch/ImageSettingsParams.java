package org.codepath.gridimagesearch;

import java.io.Serializable;

public class ImageSettingsParams implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7378114458694198762L;
	private static final String GIS_SIZE_PARAM = "imgsz";
	private static final String GIS_COLOR_PARAM = "imgcolor";
	private static final String GIS_TYPE_PARAM = "imgtype";
	private static final String GIS_SITE_PARAM = "as_sitesearch";

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

	public String toQueryParams() {
		StringBuilder sb = new StringBuilder();
		if (mImageSize != null && !"None".equals(mImageSize)) { 
			sb.append("&").append(GIS_SIZE_PARAM).append("=").append(mImageSize);
		}
		if (mColorFilter != null && !"None".equals(mColorFilter)) {
			sb.append("&").append(GIS_COLOR_PARAM).append("=").append(mColorFilter);
		}
		if (mImageType != null  && !"None".equals(mImageType)) {
			sb.append("&").append(GIS_TYPE_PARAM).append("=").append(mImageType);
		}
		if (mSiteFilter != null) {
			sb.append("&").append(GIS_SITE_PARAM).append("=").append(mSiteFilter);
		}
		return sb.toString();
	}
}
