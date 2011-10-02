/*
Copyright (C) 2011 Michal Harakal and Michael Kroez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS-IS" BASIS
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
        
*/

package org.wheelmap.android.ui.mapsforge;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.OverlayItem;
import org.wheelmap.android.app.WheelmapApp;
import org.wheelmap.android.manager.SupportManager;
import org.wheelmap.android.model.POIHelper;
import org.wheelmap.android.model.Wheelmap;
import org.wheelmap.android.ui.POIDetailActivity;

import wheelmap.org.WheelchairState;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class POIsCursorMapsforgeOverlay extends ItemizedOverlay<OverlayItem> {
	private final static String TAG = "mapsforge";

	private final static String THREAD_NAME = "MapsforgeOverlay";

	private Context mContext;
	private Cursor mCursor;

	public POIsCursorMapsforgeOverlay(Context context) {
		super(null);
		mContext = context;
	}
	
	@Override
	public void finalize() {
		if ( mCursor != null )
			mCursor.close();
	}

	public synchronized void setCursor(Cursor cursor) {
		if ( mCursor != null)
			mCursor.close();
		
		mCursor = cursor;
		if ( mCursor == null )
			return;
		
		mCursor.registerContentObserver(new ChangeObserver());
		populate();
	}

	@Override
	public synchronized int size() {
		if (mCursor == null)
			return 0;
		return mCursor.getCount();
	}
	
	@Override
	protected synchronized OverlayItem createItem(int i) {
		if (mCursor == null)
			return null;
				
		int count = mCursor.getCount();
		if (count == 0 || i >= count) {
			Log.d( TAG, "createItem cursor count = " + count + " item index = " + i );
			return null;
		} 
		
		mCursor.moveToPosition(i);
		String name = POIHelper.getName(mCursor);
		SupportManager manager = WheelmapApp.getSupportManager();
		WheelchairState state = POIHelper.getWheelchair(mCursor);
		int lat = POIHelper.getLatitudeAsInt(mCursor);
		int lng = POIHelper.getLongitudeAsInt(mCursor);
		int nodeTypeId = POIHelper.getNodeTypeId(mCursor);
		Drawable marker = null;
		if (nodeTypeId != 0)
			marker = manager.lookupNodeType(nodeTypeId).stateDrawables
					.get(state);

		OverlayItem item = new OverlayItem();
		item.setTitle(name);
		item.setSnippet(name);
		item.setPoint(new GeoPoint(lat, lng));
		item.setMarker(marker);
		return item;
	}

	@Override
	protected String getThreadName() {
		return THREAD_NAME;
	}

	private class ChangeObserver extends ContentObserver {
		public ChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return false;
		}

		@Override
		public void onChange(boolean selfChange) {
			reload();
		}
	}
	
	private synchronized void reload() {
		Log.d( TAG, "reload - requery and populate" );
		mCursor.requery();
		// Only populate, if db hasnt deleted
		if ( mCursor.getCount() != 0 )
			populate();
	}
	
	public synchronized void deactivateCursor() {
		Log.d( TAG, "deactivate" );
		mCursor.deactivate();
	}

	@Override
	protected synchronized boolean onTap(int index) {
		if (mCursor == null)
			return false;

		int count = mCursor.getCount();
		if (count == 0 || index >= count)
			return false;
		
		
		mCursor.moveToPosition(index);
		long poiId = POIHelper.getId(mCursor);
		Log.d(TAG, "onTap index = " + index + " id = " + poiId);

		Intent i = new Intent(mContext, POIDetailActivity.class);
		i.putExtra(Wheelmap.POIs.EXTRAS_POI_ID, poiId);
		mContext.startActivity(i);
		return true;
	}
	

	//
	// Does not work - this thread needs Looper.prepare and a way to make a handler
	//
//	@Override
//	protected synchronized boolean onLongPress(int index) {
//		if (mCursor == null)
//			return false;
//
//		int count = mCursor.getCount();
//		if (count == 0 || index >= count)
//			return false;
//		
//		mCursor.moveToPosition(index);
//		int idColumn = mCursor.getColumnIndex(Wheelmap.POIs._ID);
//		int poiId = mCursor.getInt(idColumn);
//
//		Log.d(TAG, "onTap: index = " + index + " id = " + poiId);
//
//		Uri poiUri = Uri.withAppendedPath(Wheelmap.POIs.CONTENT_URI_POI_ID,
//				String.valueOf(poiId));
//
//		// Then query for this specific record:
//		Cursor cur = mContext.getContentResolver().query(poiUri, null, null,
//				null, null);
//		if (cur.moveToFirst()) {
//			Log.d(TAG,
//					Integer.toBinaryString(poiId) + " "
//							+ POIHelper.getName(cur) + ' '
//							+ POIHelper.getAddress(cur));
//
//			Toast.makeText(mContext,
//					POIHelper.getName(cur) + ' ' + POIHelper.getAddress(cur),
//					Toast.LENGTH_SHORT).show();
//		}
//		cur.close();
//		return true;
//	}

}
