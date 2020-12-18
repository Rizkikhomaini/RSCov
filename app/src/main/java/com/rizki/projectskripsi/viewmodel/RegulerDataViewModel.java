package com.rizki.projectskripsi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rizki.projectskripsi.api.regulerData.RegulerData;
import com.rizki.projectskripsi.repo.RegulerDataRepository;

public class RegulerDataViewModel extends ViewModel {
    private MutableLiveData<RegulerData> regulerData;
    private RegulerDataRepository regulerDataRepository;

    public void init() {
        if (regulerData != null) {
            return;
        }
        regulerDataRepository = RegulerDataRepository.getInstance();
        regulerData = regulerDataRepository.getRegulerData();
    }

    public LiveData<RegulerData> getRegulerData() {
        return regulerData;
    }
}
