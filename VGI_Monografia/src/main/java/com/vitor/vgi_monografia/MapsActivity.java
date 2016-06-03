package com.vitor.vgi_monografia;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private Spinner dropdown;
    private EditText texto;
    private Button enviar, positivar, negativar;
    private Marker marker = null;
    private String nome, email, id;
    private int mId;
    private Notification.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.id  = extras.getString("id");
            this.nome  = extras.getString("nome");
            this.email  = extras.getString("email");
        }
        setContentView(R.layout.activity_maps);
        dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"Escolha uma categoria:","Outros", "Crimes","Trânsito","Violência","Limpeza","Estrutura", "Transporte" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        texto = (EditText) findViewById(R.id.editText2);
        enviar = (Button) findViewById(R.id.button2);
        positivar = (Button) findViewById(R.id.button3);
        negativar = (Button) findViewById(R.id.button4);
        enviar.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


         mBuilder =
                new Notification.Builder(this);

// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MapsActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MapsActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapClickListener(this);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                texto.setVisibility(View.INVISIBLE);
                dropdown.setVisibility(View.INVISIBLE);
                enviar.setVisibility(View.INVISIBLE);
                positivar.setVisibility(View.VISIBLE);
                negativar.setVisibility(View.VISIBLE);
                return v;
            }


        });

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();


    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            if (mMap != null) {


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location local_atual) {
                        // TODO Auto-generated method stub
                        float[] results = new float[1];
                        if(marker != null) {
                            Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude,
                                    local_atual.getLatitude(), local_atual.getLongitude(), results);
                            if (results[0] < 50) {
                                mBuilder.setSmallIcon(R.drawable.cast_ic_notification_on);
                                mBuilder.setContentTitle("VGI-Monografia");
                                mBuilder.setContentText("Há uma marcação próxima de você!");
                                mBuilder.setPriority(Notification.PRIORITY_HIGH);
                                mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                                NotificationManager mNotificationManager=
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(mId, mBuilder.build());
                            }
                        }
                    }
                });

            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    public void onMapClick(LatLng point) {
        if(marker != null) {
            marker.remove();
            marker = null;
        }
        texto.setVisibility(View.VISIBLE);
        dropdown.setVisibility(View.VISIBLE);
        enviar.setVisibility(View.VISIBLE);
        positivar.setVisibility(View.INVISIBLE);
        negativar.setVisibility(View.INVISIBLE);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        marker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("O que está acontecendo neste local?"));
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            positivar.setVisibility(View.INVISIBLE);
            negativar.setVisibility(View.INVISIBLE);
            if(marker != null) {
                texto.setVisibility(View.INVISIBLE);
                dropdown.setVisibility(View.INVISIBLE);
                enviar.setVisibility(View.INVISIBLE);
                marker.remove();
                marker = null;
                return true;
            }
            else
                return super.onKeyDown(keyCode, event);
        }
        return true;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                WebServiceRequests.sendPostRequest(this.id, this.nome,this.texto.getText().toString(),this.dropdown.getSelectedItem().toString(), String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude));
                break;

        }
    }



}
