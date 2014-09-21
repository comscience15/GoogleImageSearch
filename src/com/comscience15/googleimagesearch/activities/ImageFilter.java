package com.comscience15.googleimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.comscience15.googleimagesearch.R;

public class ImageFilter extends Activity {
	private Spinner sColorFilter, sImageSize, sImageType;
	private EditText etSiteFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_filter);
//		addImgSize();
		declareVariables();
	}

	private void declareVariables() {
		sColorFilter = (Spinner) findViewById(R.id.sColorFilter);
		sImageSize = (Spinner) findViewById(R.id.sImageSize);
		sImageType = (Spinner) findViewById(R.id.sImageType);
		etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
	}
	
	public void onBtnSave(View v) {
		Intent i = new Intent(this, SearchActivity.class);
		i.putExtra("Color Filter", String.valueOf(sColorFilter.getSelectedItem()));
		i.putExtra("Image Size", String.valueOf(sImageSize.getSelectedItem()));
		i.putExtra("Image Type", String.valueOf(sImageType.getSelectedItem()));
		i.putExtra("Site Filter", etSiteFilter.getText().toString());
		setResult(RESULT_OK, i);
		finish();
	}
	
	public void onBtnCancel(View v){
		sColorFilter.setSelection(0);
		sImageSize.setSelection(0);
		sImageType.setSelection(0);
		etSiteFilter.setText("");
	}

//	private void addImgSize() {
//		String value = imgSizeSpinner.getSelectedItem().toString();
//		imgSizeSpinner = (Spinner) findViewById(R.id.imgSize);
//		ArrayAdapter<CharSequence> arrAdapter = ArrayAdapter.createFromResource(this, R.array.imgSize_arrays, android.R.layout.simple_spinner_dropdown_item);
//		arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		imgSizeSpinner.setAdapter(arrAdapter);
//	}

//	public void setSpinnerToValue(Spinner spinner, String value){
//		int index = 0;
//		SpinnerAdapter sAdapter = spinner.getAdapter();
//		for (int i = 0; i < sAdapter.getCount(); i++){
//			if (sAdapter.getItem(i).equals(value)){
//				index = i;
//				break;
//			}
//		}
//		spinner.setSelection(index);
//	}
	
}
