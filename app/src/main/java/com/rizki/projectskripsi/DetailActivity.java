package com.rizki.projectskripsi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.rizki.projectskripsi.api.Rs;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location currentLocation = null;

    private GoogleMap googleMap;

    public static String KEY_DATA_RS = "KEY_DATA_RS";

    private Rs rs;

    private TextView tvHospitalName, tvHospitalPhone,
            tvHospitalWebsite, tvHospitalDesc,
            tvKamarDetail;

    private LinearLayout cardTelp, cardWeb;

    private ImageView ivHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ivHospital = findViewById(R.id.iv_hospital);
        tvHospitalName = findViewById(R.id.tv_hospital_name);
        tvKamarDetail = findViewById(R.id.tvKamarDetail);
//        tvHospitalPhone = findViewById(R.id.tv_hospital_phone);
//        tvHospitalWebsite = findViewById(R.id.tv_hospital_website);
        tvHospitalDesc = findViewById(R.id.tv_hospital_description);

        //btn telepon dan website
        cardTelp = findViewById(R.id.cardTelepon);
        cardWeb = findViewById(R.id.cardWebsite);

        MapView mapView = findViewById(R.id.detail_map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        if (getIntent().hasExtra(KEY_DATA_RS)) {
            rs = getIntent().getParcelableExtra(KEY_DATA_RS);
            initData();
        } else {
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        Glide.with(this).load(rs.getGambar()).into(ivHospital);
        tvHospitalName.setText(rs.getNama_rs());
//        tvHospitalPhone.setText(rs.getTelepon());
//        tvHospitalWebsite.setText(rs.getWebsite());
        tvKamarDetail.setText(rs.getJml_kamar() + " Ruang isolasi tersedia");
        tvHospitalDesc.setText(rs.getDeskripsi());

        cardTelp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + rs.getTelepon()));
            startActivity(intent);
        });
        cardWeb.setOnClickListener(v -> {
            if (rs.getWebsite().startsWith("http")) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(rs.getWebsite()));
                startActivity(i);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        fetchLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void fetchLastLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                LatLng origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 14));

                double latRs = Double.parseDouble(rs.getLatitude());
                double longRs = Double.parseDouble(rs.getLongitude());
                LatLng destination = new LatLng(latRs, longRs);

                initRoute(origin, destination);

                findViewById(R.id.btn_route).setOnClickListener(v -> {
//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                            Uri.parse("http://maps.google.com/maps?saddr="
//                                    + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
//                                    + "&daddr=" + latRs + "," + longRs));
//                    startActivity(intent);
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + latRs + "," + longRs));
                    startActivity(intent);
                });
            }
        });

    }

    private void initRoute(LatLng origin, LatLng destination) {
        MarkerOptions markerOrigin = new MarkerOptions()
                .position(origin)
                //.title("I am here")
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_marker_person));
        googleMap.addMarker(markerOrigin);

        MarkerOptions markerDestination = new MarkerOptions()
                .position(destination)
                .title(rs.getNama_rs())
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_rs_marker));
        googleMap.addMarker(markerDestination);

        GoogleDirection.withServerKey("Your API Key")
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                             @Override
                             public void onDirectionSuccess(@Nullable Direction direction) {
                                 if (direction.getStatus().equals(RequestResult.OK)) {
                                     ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                                     PolylineOptions polylineOptions = DirectionConverter.createPolyline(DetailActivity.this,
                                             directionPositionList, 3, Color.BLACK);
                                     googleMap.addPolyline(polylineOptions);

//                                     zoomMapInitial(origin, destination);
                                 } else {
                                     Toast.makeText(DetailActivity.this, direction.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                 }
                             }

                             @Override
                             public void onDirectionFailure(@NonNull Throwable t) {
                                 t.printStackTrace();
                             }
                         }
                );
    }

    private void zoomMapInitial(LatLng origin, LatLng destination) {
        try {
            int padding = 300;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(origin);
            bc.include(destination);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}