package com.wgu.c196.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.wgu.c196.database.AppRepository;
import com.wgu.c196.database.entities.AssessmentEntity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AssessmentEditorViewModel extends AndroidViewModel {

    public MutableLiveData<AssessmentEntity> mLiveAssessment = new MutableLiveData<>();
    private AppRepository mRepository;
//    private LiveData<List<AssessmentEntity>> mAssessmentTypes;
    private Executor executor = Executors.newSingleThreadExecutor();

    public AssessmentEditorViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(getApplication());
//        mAssessmentTypes = mRepository.getAssessmentTypes();
    }

    public void loadData(final int assessmentId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AssessmentEntity assessment = mRepository.getAssessmentById(assessmentId);
                mLiveAssessment.postValue(assessment);
            }
        });
    }
//
//    public LiveData<List<String>> getAssessmentTypes() {
//        for (AssessmentEntity assessment : mAssessmentTypes) {
//
//        }
//    }

    public void saveTerm(String newAssessment) {
        AssessmentEntity assessment = mLiveAssessment.getValue();

        if (assessment == null) {
            if (TextUtils.isEmpty(newAssessment.trim())) {
                return;
            }
            assessment = new AssessmentEntity(newAssessment, new Date(), AssessmentEntity.Type.PERFORMANCE);
        } else {
            assessment.setTitle(newAssessment);
        }
        mRepository.insertAssessment(assessment);
    }

    public void deleteCourse() {
        try {
            mRepository.deleteAssessment(mLiveAssessment.getValue());
        } catch (SQLiteConstraintException e) {
            throw new SQLiteConstraintException(e.getMessage());
        }
    }
}
