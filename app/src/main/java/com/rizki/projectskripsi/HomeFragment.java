package com.rizki.projectskripsi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rizki.projectskripsi.adapter.HospitalAdapter;
import com.rizki.projectskripsi.api.ResponseApi;
import com.rizki.projectskripsi.api.Rs;
import com.rizki.projectskripsi.api.RsDataHolder;
import com.rizki.projectskripsi.api.Repository;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private SlidingUpPanelLayout mSlidePanelLayoutHospital;

    private ArrayList<Rs> listRs;

    private RecyclerView rv_hospital;

    private HospitalAdapter hospitalAdapter;

    private TextView tvHospital, tvCurrentCity, tvDistanceDropdown;

    private FusedLocationProviderClient fusedLocationProviderClient; //klien penyedia lokasi

    private Location currentLocation = null; // lokasi terakhir user

    private GoogleMap googleMap;

    private LinearLayout linear;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        mSlidePanelLayoutHospital = view.findViewById(R.id.sliding_layout);

        //tvHospital = view.findViewById(R.id.tv_hospital);
        tvCurrentCity = view.findViewById(R.id.tv_current_city);
        tvDistanceDropdown = view.findViewById(R.id.tv_distance_dropdown);

        rv_hospital = view.findViewById(R.id.hospital_rv);
        linear = view.findViewById(R.id.linear);

        //Sliding Panel Demo
        linear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mSlidePanelLayoutHospital.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mapView = view.findViewById(R.id.home_map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        listRs = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(listRs, rs -> {
            startActivity(new Intent(requireActivity(), DetailActivity.class)
                    .putExtra(DetailActivity.KEY_DATA_RS, rs));
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        rv_hospital.setLayoutManager(linearLayoutManager);
        rv_hospital.setAdapter(hospitalAdapter);

        // Menentukan Radius
        tvDistanceDropdown.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireActivity(), tvDistanceDropdown);
            popup.getMenuInflater().inflate(R.menu.menu_distance, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.five_km) {
                    getRumahSakit(5);
                    tvDistanceDropdown.setText("5 Km");
                } else if (item.getItemId() == R.id.ten_km) {
                    getRumahSakit(10);
                    tvDistanceDropdown.setText("10 Km");
                } else if (item.getItemId() == R.id.twenty_km) {
                    getRumahSakit(20);
                    tvDistanceDropdown.setText("20 Km");
                }else if(item.getItemId() == R.id.thirty_km) {
                    getRumahSakit(30);
                    tvDistanceDropdown.setText("30 Km");
                } else {
                    getRumahSakit(5);
                    tvDistanceDropdown.setText("5 Km");
                }
                return true;
            });

            popup.show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        fetchLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void fetchLastLocation() { // Mengambil lokasi terakhir user
        requestLocationPermission(new PermissionCallback() {
            @Override
            public void onPermissionGranted() { //atas izin yang diberikan
                Task<Location> task = fusedLocationProviderClient.getLastLocation();
                task.addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLocation = location;

                        googleMap.setMyLocationEnabled(true);

                        getCurrentCity();

                        getRumahSakit(5);
                    }
                });
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(requireContext(), "Ijin diperlukan untuk mengakses aplikasi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Untuk mendapatkan lokasi kota saat ini
    private void getCurrentCity() {
        try {
            Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), 1);
            String cityName = addresses.get(0).getLocality();
            tvCurrentCity.setText(cityName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mengambil daftar rumah sakit
    private void getRumahSakit(double radiusAwal) {
        googleMap.clear();

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        if (currentLocation != null) {
            Repository.createService(RsDataHolder.class).getRs(
                    String.valueOf(currentLocation.getLatitude()),
                    String.valueOf(currentLocation.getLongitude()),
                    String.valueOf(radiusAwal)
            ).enqueue(new Callback<ResponseApi>() {
                @Override
                public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                    if (response.isSuccessful()) {
                        if (response.body().response_code == 200) {
                            List<Rs> rsList = response.body().response_data.rs;
                            hospitalAdapter.setData(rsList);
                            initRsPin(rsList);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseApi> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    //
    private void initRsPin(List<Rs> rsList) {
        for (int i = 0; i < rsList.size(); i++) {
            LatLng latLng = new LatLng(Double.parseDouble(listRs.get(i).getLatitude()),
                    Double.parseDouble(listRs.get(i).getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(listRs.get(i).getNama_rs())
                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_rs_marker));
            googleMap.addMarker(markerOptions);
        }
    }

    // Meminta izin lokasi pengguna
    private void requestLocationPermission(PermissionCallback callback) {
        Dexter.withContext(requireActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        callback.onPermissionGranted();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            openAppSetting();
                        } else {
                            callback.onPermissionDenied();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                   PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private void openAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", (requireActivity()).getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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