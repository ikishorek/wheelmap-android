package org.osmdroid.mapsforge.wrapper;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IProjection;
import org.osmdroid.util.GeoPoint;

import android.graphics.Point;

/**
 * A wrapper for the mapsforge {@link org.mapsforge.android.maps.Projection} implementation. This
 * implements {@link IProjection}, which is also implemented by the osmdroid {@link
 * org.osmdroid.views.MapView.Projection}.
 *
 * @author Neil Boyd
 */
public class Projection implements IProjection {

    private final org.mapsforge.android.maps.Projection mProjection;

    public Projection(final org.mapsforge.android.maps.Projection pProjection) {
        mProjection = pProjection;
    }

    @Override
    public Point toPixels(final IGeoPoint in, final Point out) {

        final org.mapsforge.android.maps.GeoPoint googleGeoPoint =
                new org.mapsforge.android.maps.GeoPoint(in.getLatitudeE6(), in.getLongitudeE6());
        return mProjection.toPixels(googleGeoPoint, out);
    }

    @Override
    public IGeoPoint fromPixels(final int x, final int y) {
        final org.mapsforge.android.maps.GeoPoint mapsforgeGeoPoint = mProjection.fromPixels(x, y);
        return new GeoPoint(mapsforgeGeoPoint.getLatitude(), mapsforgeGeoPoint.getLongitude());
    }

    @Override
    public float metersToEquatorPixels(final float meters) {
        return 0;
        // TODO return mProjection.metersToEquatorPixels(meters);
    }

    @Override
    public IGeoPoint getNorthEast() {
        return null;
    }

    @Override
    public IGeoPoint getSouthWest() {
        return null;
    }
}
