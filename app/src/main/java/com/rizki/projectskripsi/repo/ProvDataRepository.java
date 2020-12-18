package com.rizki.projectskripsi.repo;

import androidx.lifecycle.MutableLiveData;

import com.rizki.projectskripsi.api.ProvData;
import com.rizki.projectskripsi.api.ProvDataFetch;
import com.rizki.projectskripsi.api.ProvDataHolder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProvDataRepository {

    private static ProvDataRepository provDataRepository;

    public static ProvDataRepository getInstance() {
        if(provDataRepository == null) {
            provDataRepository = new ProvDataRepository();
        }
        return provDataRepository;
    }

    private ProvDataHolder provDataHolder;

    private ProvDataRepository() {
        provDataHolder = ProvDataFetch.createService(ProvDataHolder.class);
    }

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLoading() {
        return isLoading;
    }

    public MutableLiveData<ProvData> getProvData() {
        MutableLiveData<ProvData> ProvData = new MutableLiveData<>();
        isLoading.setValue(true);
        provDataHolder.getProvData().enqueue(new Callback<ProvData>() {
            @Override
            public void onResponse(Call<ProvData> call, Response<ProvData> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ProvData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ProvData> call, Throwable t) {
                isLoading.setValue(false);
                Timber.e(t);
            }
        });

        return ProvData;

    }
}
