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
    private Executor executor = Executors.newSingleThreadExecutor();

    public AssessmentEditorViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(getApplication());
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

    public void saveAssessment(final AssessmentEntity newAssessment) {
        final AssessmentEntity assessment = mLiveAssessment.getValue();

        if (assessment == null) {
            if (TextUtils.isEmpty(newAssessment.getTitle().trim())) {
                return;
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    AssessmentEntity assessment;
                    assessment = new AssessmentEntity(newAssessment.getCourseId(), newAssessment.getTitle(), newAssessment.getDueDate(), AssessmentEntity.Type.OBJECTIVE);
                    mRepository.insertAssessment(assessment);
                }
            });
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    assessment.setTitle(newAssessment.getTitle());
                    assessment.setDueDate(newAssessment.getDueDate());
                    mRepository.insertAssessment(assessment);
                }
            });

        }

    }

    public void deleteAssessment() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    mRepository.deleteAssessment(mLiveAssessment.getValue());
                }
            });
        } catch (SQLiteConstraintException e) {
            throw new SQLiteConstraintException(e.getMessage());
        }
    }

    public void updateType(final AssessmentEntity.Type assessmentType) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AssessmentEntity assessment = mLiveAssessment.getValue();
                assessment.setType(assessmentType);
                mRepository.insertAssessment(assessment);
                mLiveAssessment.postValue(assessment);
            }
        });
    }
}
