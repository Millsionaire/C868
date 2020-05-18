package com.wgu.c196.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.wgu.c196.database.AppRepository;
import com.wgu.c196.database.TermEntity;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TermEditorViewModel extends AndroidViewModel {

    public MutableLiveData<TermEntity> mLiveTerm = new MutableLiveData<>();
    private AppRepository mRepository;
    private Executor executor = Executors.newSingleThreadExecutor();

    public TermEditorViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(getApplication());
    }

    public void loadData(final int termId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                TermEntity term = mRepository.getTermById(termId);
                mLiveTerm.postValue(term);
            }
        });
    }

    public void saveTerm(String newTerm) {
        TermEntity term = mLiveTerm.getValue();

        if (term == null) {
            if (TextUtils.isEmpty(newTerm.trim())) {
                return;
            }
            term = new TermEntity(newTerm, new Date(), new Date());
        } else {
            term.setTitle(newTerm);
        }

        mRepository.insertTerm(term);
    }

    public void deteteTerm() {
        mRepository.deleteTerm(mLiveTerm.getValue());
    }
}
