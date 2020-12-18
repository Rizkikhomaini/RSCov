package com.rizki.projectskripsi.repo;

import androidx.lifecycle.MutableLiveData;

import com.rizki.projectskripsi.api.regulerData.RegulerData;
import com.rizki.projectskripsi.api.regulerData.RegulerDataFetch;
import com.rizki.projectskripsi.api.regulerData.RegulerDataHolder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RegulerDataRepository {

    private static RegulerDataRepository regulerDataRepository;

    public static RegulerDataRepository getInstance() {
        if (regulerDataRepository == null) {
            regulerDataRepository = new RegulerDataRepository();
        }

        return regulerDataRepository;
    }

    private RegulerDataHolder regulerDataHolder;

    private RegulerDataRepository() {
        regulerDataHolder = RegulerDataFetch.createService(RegulerDataHolder.class);
    }

    public MutableLiveData<RegulerData> getRegulerData() {
        MutableLiveData<RegulerData> regulerData = new MutableLiveData<>();
        regulerDataHolder.getRegulerData().enqueue(new Callback<RegulerData>() {
            @Override
            public void onResponse(Call<RegulerData> call, Response<RegulerData> response) {
                if (response.isSuccessful()) {
                    regulerData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<RegulerData> call, Throwable t) {
                Timber.e(t);
            }
        });

        return regulerData;
    }
}
