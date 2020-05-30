package com.wgu.c196.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.wgu.c196.database.AppRepository;
import com.wgu.c196.database.CourseEntity;
import com.wgu.c196.database.TermEntity;
import com.wgu.c196.database.TermWithCourses;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TermEditorViewModel extends AndroidViewModel {

    public MutableLiveData<TermWithCourses> mLiveTerm = new MutableLiveData<>();
    public LiveData<List<CourseEntity>> mCourses;
    private AppRepository mRepository;
    private Executor executor = Executors.newSingleThreadExecutor();

    public TermEditorViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(getApplication());
        mCourses = mRepository.mCourses;
    }

    public void loadData(final int termId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                TermWithCourses term = mRepository.getTermWithCoursesById(termId);
                if (term.courses != null) {
                    mCourses = mRepository.getCoursesByTermId(termId);
                }
                mLiveTerm.postValue(term);
            }
        });
    }

    public void saveTerm(String newTerm) {
        TermWithCourses termWithCourses = mLiveTerm.getValue();

        TermEntity term = termWithCourses.term;

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

    public void deleteTerm() {
        try {
            mRepository.deleteTerm(mLiveTerm.getValue());
        } catch (SQLiteConstraintException e) {
            throw new SQLiteConstraintException(e.getMessage());
        }
    }
}
