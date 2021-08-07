package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.widget.SearchView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.example.googlemap.Files.FetchURL;
import com.example.googlemap.Files.TaskLoadedCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView defaul, satellite;
    FusedLocationProviderClient client;// It provide the location information
    SupportMapFragment mapFragment;
    SearchView searchView1;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME = 5000;
    private final long MIN_DIST = 5;
    private LatLng latLng;
    Address address;
    LatLng latLng1;

    int coun = 0;
    String location;
    Marker mk;
    MarkerOptions m1, m2;
    private Polyline currentPolyline;
    MarkerOptions options;
    private MarkerOptions place1, place2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        defaul = findViewById(R.id.d);
        satellite = findViewById(R.id.s);


        searchView1 = (SearchView) findViewById(R.id.idSearchView);
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                location = searchView1.getQuery().toString();
                try{
                    List<Address> addresses = null;
                    if (location != null || location.equals("")) {
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        try {
                            addresses = geocoder.getFromLocationName(location, 1);
                            Log.d("address", String.valueOf(addresses));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//
                        if (coun > 0) {
                            if (mk != null) {
                                mk.remove();

                            }
                        }
                        address = addresses.get(0);
                        Log.d("ad", String.valueOf(address));
                        latLng1 = new LatLng(address.getLatitude(), address.getLongitude());
                        mk = mMap.addMarker(new MarkerOptions().position(latLng1)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        coun++;
                        return false;
                    }
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Location Not Found",Toast.LENGTH_SHORT).show();
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        defaul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                defaul.setBackgroundResource(R.drawable.berli);
                satellite.setVisibility(View.VISIBLE);
                defaul.setVisibility(View.GONE);

            }
        });
        satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                satellite.setBackgroundResource(R.drawable.satellite);
                satellite.setVisibility(View.GONE);
                defaul.setVisibility(View.VISIBLE);

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = client.getLastLocation();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("adress", String.valueOf(latLng));
                            options = new MarkerOptions().position(latLng)
                                    .title("I am Here");
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            googleMap.addMarker(options);

                            locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(@NonNull Location location) {
                                    try {
                                        mMap.clear();
                                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        Geocoder geocoder;
                                        List<Address> addresses;
                                        geocoder=new Geocoder(MapsActivity.this, Locale.getDefault());
                                        addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                        String adres=addresses.get(0).getLocality();
                                        String all=adres+" "+location.getLatitude()+","+location.getLongitude();
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(all));
                                        Log.d("all",all);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


                                    } catch (SecurityException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {

                                }
                                @Override
                                public void onProviderEnabled(String s) {

                                }
                                @Override
                                public void onProviderDisabled(String s) {

                                }
                            };

//                            This class provides access to the system location services.
//                            These services allow applications to obtain
//                            periodic updates of the device's geographical location,
//                            or to be notified when the device enters the proximity of a given geographical location.
                            locationManager = (LocationManager)
                                    getSystemService(LOCATION_SERVICE);
                            try {
                                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                            }
                            catch (SecurityException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }



}