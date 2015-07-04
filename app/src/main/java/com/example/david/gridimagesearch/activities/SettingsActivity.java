package com.example.david.gridimagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.david.gridimagesearch.R;

public class SettingsActivity extends ActionBarActivity {
    Spinner spinnerImageSize;
    Spinner spinnerImageType;
    Spinner spinnerColorFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> madapter_size = ArrayAdapter.createFromResource(this,
                R.array.image_size_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> madapter_type = ArrayAdapter.createFromResource(this,
                R.array.image_type_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> madapter_color = ArrayAdapter.createFromResource(this,
                R.array.color_filter_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        madapter_size.  setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        madapter_type.  setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        madapter_color.  setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerImageSize = (Spinner) findViewById(R.id.spinnerImageSize);
        spinnerImageType = (Spinner) findViewById(R.id.spinnerImageType);
        spinnerColorFilter = (Spinner) findViewById(R.id.spinnerColorFilter);
        spinnerImageSize.setAdapter(madapter_size);
        spinnerImageType.setAdapter(madapter_type);
        spinnerColorFilter.setAdapter(madapter_color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void onClickSaveButton(View v){
        String image_size = spinnerImageSize.getSelectedItem().toString();
        String image_type = spinnerImageType.getSelectedItem().toString();
        String color_filter = spinnerColorFilter.getSelectedItem().toString();
        String site_filter = (findViewById(R.id.tvSiteFilter)).getContext().toString();

        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("size", image_size);
        data.putExtra("type", image_type);
        data.putExtra("color_filter", color_filter);
        data.putExtra("site_filter", site_filter);
        //data.putExtra("code", 1); // ints work too
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
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
