package com.example.david.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.example.david.gridimagesearch.R;
import com.example.david.gridimagesearch.adapters.ImageResultsAdapter;
import com.example.david.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {
    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private String updatedUrl;
    private String searchUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        // creates the data source
        imageResults = new ArrayList<ImageResult>();
        //attaches the data source to an adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        // link the adapter to the adapterview(gridview)
        gvResults.setAdapter(aImageResults);
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView


                if (totalItemsCount != 0)
                {//customLoadMoreDataFromApi(page);}
                customLoadMoreDataFromApi(totalItemsCount);}
            }
        });


    }
    //check network connection
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        AsyncHttpClient client = new AsyncHttpClient();

        int offsetValue = offset;
        client.get(searchUrl + "&start=" + offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJson = null;
                try {

                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");

                    //aImageResults.clear(); // clear the existing images from the array(in case of a new search). only do this for initial search
                    // when you make changes to the adapter, it does modify the underlying data
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                    //aImageResults.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });


    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    //Fired whenever the button is pressed (android onClick property), links button to method
    public void onImageSearch(View v){
       /* if (!(isNetworkAvailable())){
            Toast toast_one = Toast.makeText(this, "Cannot connect to Network - Check Connection", Toast.LENGTH_LONG);
            toast_one.show();
        }
        if (isOnline() == false){
            Toast toast_two = Toast.makeText(this, "Cannot connect to Internet - Check Connection", Toast.LENGTH_LONG);
            toast_two.show();
        }*/
        String query = etQuery.getText().toString();
       //Toast.makeText(this, "Search for " + query, Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        if (updatedUrl != null)
        {searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8" + updatedUrl;}
        else
        {searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8";}
        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    imageResults.clear(); // clear the existing images from the array(in case of a new search). only do this for initial search
                    // when you make changes to the adapter, it does modify the underlying data
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                    //aImageResults.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void setupViews() {

        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the image display activity
                // Create an intent
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                // Get the image result to display
                ImageResult result = imageResults.get(position);
                // Pass image result into the intent
                i.putExtra("result", result); // result implements SERIALIZABLE for convenience rather than PARCELABLE for speed
                // Launch the new activity
                startActivity(i);
            }
        });
    }

    private int REQUEST_CODE = 1;
    public void onSettingsClick(View v)
    {
        Intent i = new Intent(SearchActivity.this, SettingsActivity.class);
        //i.putExtra("mode", 2); // pass arbitrary data to launched activity
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            //String name = data.getExtras().getString("name");
            //int code = data.getExtras().getInt("code", 0);
            String image_size = data.getExtras().getString("size");
            if (image_size.equals("large")){
                image_size = "xxlarge";
            }
            if (image_size.equals("small")){
                image_size.equals("icon");
            }
            String image_type = data.getExtras().getString("type");
            if (image_type.equals("any")){
                image_type = "";
            }
            String color_filter = data.getExtras().getString("color_filter");
            String site_filter = data.getExtras().getString("site_filter");
            updatedUrl = "&as_filetype=" + image_type + "&imgsz=" + image_size + "&imgcolor=" + color_filter;// + "&as_sitesearch=" + site_filter;

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
