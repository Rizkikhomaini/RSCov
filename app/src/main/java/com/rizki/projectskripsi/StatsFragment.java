package com.rizki.projectskripsi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.rizki.projectskripsi.adapter.NewsAdapter;
import com.rizki.projectskripsi.api.News;
import com.rizki.projectskripsi.api.NewsDataHolder;
import com.rizki.projectskripsi.api.Repository;
import com.rizki.projectskripsi.api.ResponseApi;
import com.rizki.projectskripsi.api.regulerData.RegulerData;
import com.rizki.projectskripsi.ui.BottomSheetDonateDialog;
import com.rizki.projectskripsi.ui.BottomSheetPrixaDialog;
import com.rizki.projectskripsi.util.LoadLocale;
import com.rizki.projectskripsi.viewmodel.RegulerDataViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends Fragment {

    private LoadLocale loadLocale;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.stat_box_layout)
    TableLayout mBoxLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvUpdateDate)
    TextView mUpdateDate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.stat_kasus_positif)
    TextView mKasusPositif;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.stat_kasus_meninggal)
    TextView mKasusMeninggal;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.stat_kasus_sembuh)
    TextView mKasusSembuh;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.test_button)
    RelativeLayout mTestButton;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.donate_button)
    RelativeLayout mDonate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rv_berita)
    RecyclerView rvBerita;

    private NewsAdapter newsAdapter;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        ButterKnife.bind(this, view);
        loadLocale = new LoadLocale(getActivity());

//        Mengambil data reguler
        RegulerDataViewModel regulerDataViewModel;

        regulerDataViewModel = ViewModelProviders.of(this).get(RegulerDataViewModel.class);
        regulerDataViewModel.init();

        regulerDataViewModel.getRegulerData().observe(this, new Observer<RegulerData>() {
            @Override
            public void onChanged(RegulerData regulerData) {
                showRegulerData(regulerData);
            }
        });

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetPrixaDialog bottomSheetPrixaDialog = new BottomSheetPrixaDialog();
                bottomSheetPrixaDialog.show(getFragmentManager(), "PrixaBottomSheet");
            }
        });

        mDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDonateDialog bottomSheetDonateDialog = new BottomSheetDonateDialog();
                bottomSheetDonateDialog.show(getFragmentManager(), "DonateBottomSheet");
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<News> news = new ArrayList<>();

        newsAdapter = new NewsAdapter(news, newsOnClick -> {
            startActivity(new Intent(getActivity(), DetailBeritaActivity.class).putExtra(DetailBeritaActivity.KEY_DATA, newsOnClick));
        });

        rvBerita.setAdapter(newsAdapter);

        getNews();
    }

    private void getNews() {
        Repository.createService(NewsDataHolder.class).getNews().enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if (response.isSuccessful()) {
                    if (response.body().response_code == 200) {
                        List<News> news = response.body().response_data.news;
                        newsAdapter.setData(news);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //    Menampilkan data kasus
    @SuppressLint("SetTextI18n")
    private void showRegulerData(RegulerData regulerData) {
        int mPositif = regulerData.getUpdatedData().getTotalCases().getmPositif();
        int mMeninggal = regulerData.getUpdatedData().getTotalCases().getmMeninggal();
        int mSembuh = regulerData.getUpdatedData().getTotalCases().getmSembuh();
        String mUpdate = regulerData.getUpdatedData().getNewCases().getmWaktuUpdate();

        mKasusPositif.setText(numberSeparator(mPositif));
        mKasusMeninggal.setText(numberSeparator(mMeninggal));
        mKasusSembuh.setText(numberSeparator(mSembuh));

        if (loadLocale.getLocale().equals("en")) {
            mUpdateDate.setText("Updated on: " + mUpdate);
        } else {
            mUpdateDate.setText(mUpdate);
        }
    }

    private String numberSeparator(int value) {
        return String.valueOf(NumberFormat.getNumberInstance(Locale.ITALY).format(value));
    }
}