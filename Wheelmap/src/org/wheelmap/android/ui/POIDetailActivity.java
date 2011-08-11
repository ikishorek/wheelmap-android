package org.wheelmap.android.ui;

import org.wheelmap.android.R;
import org.wheelmap.android.model.POIHelper;
import org.wheelmap.android.model.Wheelmap;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;

public class POIDetailActivity extends Activity {

	private EditText name=null;
	private EditText address=null;
	private EditText notes=null;
	private RadioGroup types=null;
	private String poiID=null;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);   

		name=(EditText)findViewById(R.id.name);
		address=(EditText)findViewById(R.id.addr);
		notes=(EditText)findViewById(R.id.notes);
		types=(RadioGroup)findViewById(R.id.wheel_chair_type);

		poiID=getIntent().getStringExtra(Wheelmap.POIs.EXTRAS_POI_ID);

		if (poiID != null) {
			load();
		}
	}

	@Override
	public void onPause() {
		//save();

		super.onPause();
	}

	private void load() {

		// Use the ContentUris method to produce the base URI for the contact with _ID == 23.
		Uri poiUri = Uri.withAppendedPath(Wheelmap.POIs.CONTENT_URI, poiID);

		// Then query for this specific record:
		Cursor cur = managedQuery(poiUri, null, null, null, null);
		
		POIHelper poi_helper = new POIHelper();


		if (cur.moveToFirst()) {		
			name.setText(poi_helper.getName(cur));
			address.setText(poi_helper.getAddress(cur));
			notes.setText(cur.getString(cur.getColumnIndexOrThrow(Wheelmap.POIsColumns.WEBSITE)));

			switch (poi_helper.getWheelchair(cur)) {
			case UNKNOWN: {
				types.check(R.id.unknown);
				break;
			}
			case YES:
				types.check(R.id.yes);
				break;
			case LIMITED:
				types.check(R.id.limited);
				break;
			case NO:
				types.check(R.id.no);
				break;
			default:
				types.check(R.id.unknown);
				break;
			}

			cur.close();
		}
	}
}	