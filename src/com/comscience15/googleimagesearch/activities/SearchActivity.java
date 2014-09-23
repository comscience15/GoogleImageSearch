package com.comscience15.googleimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.comscience15.googleimagesearch.R;
import com.comscience15.googleimagesearch.adapters.ImageResultsAdapter;
import com.comscience15.googleimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity{
	private String searchText = null;
	private String fullUrl = null;
	private String filterUrl = "";
	private String query = null;
	private String imgSize = null;
	private String colorFilter = null;
	private String imgType = null;
	private String siteFilter = null;
	private EditText etQuery;
	private GridView gvResults;
	private ArrayList<ImageResult> imgResults;
	private ImageResultsAdapter imgResultsAdapter;
	private static final int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		declareVariables();
		// create data source
		imgResults = new ArrayList<ImageResult>();
		// attaches data source to an adapter
		imgResultsAdapter = new ImageResultsAdapter(this, imgResults);
		// Link adapter to adapterView (gridView)
		gvResults.setAdapter(imgResultsAdapter);
		
	}
	// String for calling Google API search 
	// https://ajax.googleapis.com/ajax/services/search/images
	// width, height, tbURL, title, url
	// responseDate ==> results ==> [x] ==> tbUrl
	// responseDate ==> results ==> [x] ==> url  (full URL)   
	// responseDate ==> results ==> [x] ==> title
	// responseDate ==> results ==> [x] ==> tbUrl
	private void declareVariables(){
		etQuery = (EditText) findViewById(R.id.etQuery);
		
		
		gvResults = (GridView) findViewById(R.id.gvResults);
		gvResults.setGravity(Gravity.CENTER);

		gvResults.setOnScrollListener(new OnScrollListener() {
			private int visibleThreshold = 5;
			private int currentPage = 0;
			private int previousTotalItemCount = 0;
			private boolean loading = true;
			private int startingPageIndex = 0;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i("SCROLL", "onScrollStateChanged is called " + scrollState);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.i("DEBUG", "firstVisibleItem = " + firstVisibleItem);
				Log.i("DEBUG", "visibleItemCount = " + visibleItemCount);
				Log.i("DEBUG", "totalItemCount = " + totalItemCount);
				if (totalItemCount < previousTotalItemCount) {
					this.currentPage = this.startingPageIndex;
					this.previousTotalItemCount = totalItemCount;
					if (totalItemCount == 0) {
						this.loading = true;
					}
				}
				
				if (loading && (totalItemCount > previousTotalItemCount)) {
					loading = false;
					previousTotalItemCount = totalItemCount;
					currentPage++;
				}
				int unseenItemCount = totalItemCount - visibleItemCount;
				if (!loading && unseenItemCount <= (firstVisibleItem + visibleThreshold)) {
					onLoadMore(currentPage + 1, totalItemCount);
					loading = true;
				}
			}
			
			private void onLoadMore(int page, int totalItemCount) {
				Log.i("DEBUG", "Fetching page = " + page);
				Log.i("DEBUG", "Fetching totalItemCount = " + totalItemCount);
				fetchImage(page * 8, query, filterUrl);
			}
			
		});
		
		// add setOnItemClickListener for clicking and open a new intent to display full screen image and title
		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Launch image display activity
				// creating an intent --- need to be more specific which is SearchActivity class instead of using anonymous class (this) 
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);

				// get image result to display
				ImageResult result = imgResults.get(position);
				
				// pass image result into the intent
//				i.putExtra("url", result.fullUrl);
				i.putExtra("result", result);
				
				// launch new activity
				startActivity(i);
			}
			
		});
 		
	}
	
	//fired whenever button is pressed (android:onClick property)
	public void onImageSearch(View v) {
		query = etQuery.getText().toString();
		fullUrl = null;
		if (filterUrl == null) {
			fullUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8";
		}else {
			fullUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + filterUrl;
		}

		imgResults.clear(); // clear the existing images from array (in cases where it's a new search);
		fetchImage(0, query, filterUrl);
	}
	
	private void fetchImage(int start, String query, String filterUrl) {
		query += "&rsz=8";
		String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&start=" + start + filterUrl;

		Toast.makeText(this, "Query for " + query, Toast.LENGTH_LONG).show();
		Toast.makeText(this, "Searching for " + url, Toast.LENGTH_LONG).show();
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new JsonHttpResponseHandler(){
			// JSON data is objects(dictionary) not array
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Log.d("DEBUG - SUCCESS", response.toString());
				JSONArray imgResultsJSON = null;
				try {
					//start parsing data
					// grab array list for results
					imgResultsJSON = response.optJSONObject("responseData").getJSONArray("results");
					// when make adapter, it does modify underlying data
					// using adapter instead of ArrayList so it will auto ".notify" adapter 
					imgResultsAdapter.addAll(ImageResult.fromJSONArray(imgResultsJSON));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.i("INFO", imgResults.toString());
			}
		});
	}
	
	public void onImgFilter(MenuItem mi) {
		Toast.makeText(this, "Image filter is opening!", Toast.LENGTH_SHORT).show();
		// Create an intent
		Intent i = new Intent(SearchActivity.this, ImageFilter.class);

//		// Execute intent
		startActivityForResult(i, REQUEST_CODE);
//		startActivity(i);
	}
	
	@Override
	protected void onActivityResult(int request, int result, Intent data){
		if (result == RESULT_OK && request == REQUEST_CODE) {
			query = etQuery.getText().toString();
			colorFilter = "&imgc="+data.getExtras().getString("Color Filter");
			imgSize = "&imgsz="+data.getExtras().getString("Image Size");
			imgType = "&imgtype="+data.getExtras().getString("Image Type");
			siteFilter = "&as_sitesearch="+data.getExtras().getString("Site Filter");
			searchText = query + "&rsz=8";
			filterUrl = searchText + colorFilter + imgSize + imgType + siteFilter;
			Toast.makeText(this, "Filter URL " + filterUrl, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
}
