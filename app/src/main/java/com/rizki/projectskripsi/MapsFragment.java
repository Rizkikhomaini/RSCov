package com.rizki.projectskripsi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rizki.projectskripsi.adapter.ProvAdapter;
import com.rizki.projectskripsi.api.ProvData;
import com.rizki.projectskripsi.ui.BottomSheetMapsDialog;
import com.rizki.projectskripsi.util.LoadLocale;
import com.rizki.projectskripsi.viewmodel.ProvDataViewModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsFragment extends Fragment implements
        OnMapReadyCallback,
        ProvAdapter.ListClickedListener {

    private ProvAdapter provAdapter;

    private ArrayList<ProvData.ProvListData> provListData = new ArrayList<>();

    private ArrayList<ProvData.ProvListData> filteredList = new ArrayList<>();

    private GoogleMap googleMap;

    private SlidingUpPanelLayout mSlideUpLayout;

    private SearchView mFilter;

    private TextView mProvCollectedData;

    private ArrayList<Marker> markerArrayList = new ArrayList<>();

    private LinearLayout mProvDetailedCaseButton;

    private Marker marker;

    private int arraySizeBefore;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mProvCollectedData = view.findViewById(R.id.prov_collected_data);
        mSlideUpLayout = view.findViewById(R.id.sliding_layout);
        mFilter = view.findViewById(R.id.prov_filter);

        mFilter.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mSlideUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        mFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!provListData.isEmpty()) {
                    filter(newText);
                }
                return false;
            }
        });

        return view;
    }

    private void filter(String toString) {
        arraySizeBefore = markerArrayList.size();

        filteredList.clear();
        googleMap.clear();
        marker.remove();
        for (ProvData.ProvListData theProvData : provListData) {
            if (theProvData.getProvName().toLowerCase().contains(toString.toLowerCase())) {
                filteredList.add(theProvData);
                double lat = theProvData.getProvDataLocation().getLat();
                double lng = theProvData.getProvDataLocation().getLng();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_virus_marker));
                marker = googleMap.addMarker(markerOptions);
                markerArrayList.add(marker);
            }
        }

        provAdapter.filterList(filteredList);
    }

    private void setupRecyclerView(RecyclerView mProvRecyclerView, ProvData provData) {
        List<ProvData.ProvListData> provListData = provData.getProvListDataLists();

        this.provListData.addAll(provListData);
        this.filteredList.addAll(provListData);

        provAdapter = new ProvAdapter(this.provListData, this);
        mProvRecyclerView.setAdapter(provAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        mProvRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void hideLoading(ShimmerFrameLayout mProvCardShimmer, RecyclerView mProvRecyclerView) {
        mProvCardShimmer.setVisibility(View.GONE);
        mProvRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading(ShimmerFrameLayout mCardShimmer, RecyclerView mProvRecyclerView) {
        mCardShimmer.setVisibility(View.VISIBLE);
        mProvRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mapView = view.findViewById(R.id.prov_map_view);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(this.getContext()));

        this.googleMap = googleMap;

        final double LAT = -8.0675589d;
        final double LNG = 120.9046018d;
        final float ZOOM = 3.17f;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(LAT, LNG), ZOOM);

        googleMap.moveCamera(cameraUpdate);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.getContext(), R.raw.style_json));

        //prov data widget
        ShimmerFrameLayout mProvCardShimmer = Objects.requireNonNull(this.getView()).findViewById(R.id.prov_shimmer);
        RecyclerView mProvRecyclerView = this.getView().findViewById(R.id.prov_rv);

        //prov data fetching
        ProvDataViewModel provDataViewModel;

        provDataViewModel = ViewModelProviders.of(this).get(ProvDataViewModel.class);
        provDataViewModel.init();

        provDataViewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showLoading(mProvCardShimmer, mProvRecyclerView);
                } else {
                    hideLoading(mProvCardShimmer, mProvRecyclerView);
                }
            }
        });

        provDataViewModel.getRegulerData().observe(this, new Observer<ProvData>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onChanged(ProvData provData) {
                List<ProvData.ProvListData> provListData = provData.getProvListDataLists();
                for (ProvData.ProvListData theProvListData : provListData) {
                    ProvData.ProvListData.ProvDataLocation provDataLocation = theProvListData.getProvDataLocation();
                    double lat = -1;
                    double lng = -1;
                    if (!(provDataLocation == null)) {
                        lat = provDataLocation.getLat();
                        lng = provDataLocation.getLng();
                    }
                    LatLng latLng = new LatLng(lat, lng);
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                    markerArrayList.add(marker);
                }

                LoadLocale loadLocale = new LoadLocale(getActivity());

                if (loadLocale.getLocale().equals("en")) {
                    mProvCollectedData.setText("Data collected: " +
                            String.format("%.1f",provData.getCurrentData()) +
                            "% on " + provData.getLastUpdate());
                } else {
                    mProvCollectedData.setText("Data dihimpun: " +
                            String.format("%.1f",provData.getCurrentData()) +
                            "% pada " + provData.getLastUpdate());
                }

                googleMap.setInfoWindowAdapter(new ProvInfoWindowAdapter());

                setupBottomSheet(provData);

                setupRecyclerView(mProvRecyclerView, provData);
            }
        });
    }

    private void setupBottomSheet(ProvData provData) {
        Toast mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        //BottomSheetMapsDialog bottomSheetMapsDialog = new BottomSheetMapsDialog();

        /*mProvDetailedCaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(view.getContext(), bottomSheetMapsDialog.getClass());
//                intent.putExtra("provData", provData);
//                startActivity(intent);
//                bottomSheetMapsDialog.show(getFragmentManager(),"BottomSheet");
                mToast.setText("Other data coming soon!");
                mToast.show();
            }
        }); */
    }

    private class ProvInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.info_window_prov, null);

        @SuppressLint("DefaultLocale")
        @Override
        public View getInfoWindow(Marker marker) {
            TextView mProvName, mProvCase, mProvDeath, mProvCured, mProvTreated, mProvPercentage,
                    mProvMale, mProvFemale, mProvBaby, mProvTeen, mProvMan, mProvAdult, mProvOld, mProvGrandParents;

            mProvName = view.findViewById(R.id.info_window_prov_name);
            mProvCase = view.findViewById(R.id.info_window_prov_case);
            mProvDeath = view.findViewById(R.id.info_window_prov_death);
            mProvCured = view.findViewById(R.id.info_window_prov_cured);
            mProvTreated = view.findViewById(R.id.info_window_prov_treated);
            mProvPercentage = view.findViewById(R.id.info_window_prov_percentage);
            mProvMale = view.findViewById(R.id.info_window_prov_male);
            mProvFemale = view.findViewById(R.id.info_window_prov_female);
            mProvBaby = view.findViewById(R.id.info_window_prov_age_baby);
            mProvTeen = view.findViewById(R.id.info_window_prov_age_teen);
            mProvMan = view.findViewById(R.id.info_window_prov_age_man);
            mProvAdult = view.findViewById(R.id.info_window_prov_age_adult);
            mProvOld = view.findViewById(R.id.info_window_prov_age_old);
            mProvGrandParents = view.findViewById(R.id.info_window_prov_age_grandparent);

            //remove the "m" from getId() to returning integer

            String id = marker.getId();
            int convId = Integer.parseInt(id.replaceAll("[^\\d.]", "")) - arraySizeBefore;

            ProvData.ProvListData provListData = filteredList.get(convId);

            //HANDLING ERROR CAUSE NULL __ NOT THE EFFECTIVE WAY
            //BECAUSE WE FORCE TO PUT THE FIRST ageList DATA

            ArrayList<Integer> ageList = new ArrayList<>();

            for (ProvData.ProvListData.ProvDataAgeList theAgeList : provListData.getProvDataAgeLists()) {
                if (provListData.getProvDataAgeLists().size() < 6) {
                    if (ageList.size() == 0) {
                        ageList.add(0);
                    }
                }
                ageList.add(theAgeList.getDocCount());
            }

            mProvName.setText(provListData.getProvName());
            mProvCase.setText(numberSeparator(provListData.getCaseAmount()));
            mProvDeath.setText(numberSeparator(provListData.getDeathAmount()));
            mProvCured.setText(numberSeparator(provListData.getHealedAmount()));
            mProvTreated.setText(numberSeparator(provListData.getTreatedAmount()));
            mProvPercentage.setText(String.format("%.1f", provListData.getDocCount()));
            mProvMale.setText(numberSeparator(provListData.getProvDataSexLists().get(0).getDocCount()));
            mProvFemale.setText(numberSeparator(provListData.getProvDataSexLists().get(1).getDocCount()));
            mProvBaby.setText(numberSeparator(ageList.get(0)));
            mProvTeen.setText(numberSeparator(ageList.get(1)));
            mProvMan.setText(numberSeparator(ageList.get(2)));
            mProvAdult.setText(numberSeparator(ageList.get(3)));
            mProvOld.setText(numberSeparator(ageList.get(4)));
            mProvGrandParents.setText(numberSeparator(ageList.get(5)));

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    public void onListClicked(int position) {
        int zoom = (int) googleMap.getCameraPosition().zoom;

        mFilter.clearFocus();
        markerArrayList.get(position + arraySizeBefore).showInfoWindow();
        double LAT = filteredList.get(position).getProvDataLocation().getLat() + 0.655d;
        double LNG = filteredList.get(position).getProvDataLocation().getLng();
        final float ZOOM = 7;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(LAT, LNG), ZOOM);

        final Handler handler = new Handler();

        //Tricky part that collapse the slide up panel after 200ms (the keyboard already hide)
        //Can't find another approach to do the same

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                googleMap.animateCamera(cameraUpdate, 1000, null);
                mSlideUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }, 200);

    }

    private String numberSeparator(int value) {
        return String.valueOf(NumberFormat.getNumberInstance(Locale.ITALY).format(value));
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