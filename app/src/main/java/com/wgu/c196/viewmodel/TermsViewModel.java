package com.wgu.c196.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.wgu.c196.database.AppRepository;
import com.wgu.c196.database.entities.TermEntity;

import java.util.List;

public class TermsViewModel extends AndroidViewModel {

    public LiveData<List<TermEntity>> mTerms;
    private AppRepository mRepository;

    public TermsViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mTerms = mRepository.mTerms;
    }

    public void addSampleData() {
        mRepository.addSampleData();
    }

    public void deleteAllData() {
        mRepository.deleteAllData();
    }
}
