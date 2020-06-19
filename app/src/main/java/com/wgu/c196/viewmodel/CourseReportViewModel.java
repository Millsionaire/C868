package com.wgu.c196.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import com.wgu.c196.database.AppRepository;
import com.wgu.c196.database.entities.CourseEntity;

import java.util.List;

public class CourseReportViewModel extends AndroidViewModel {

    public LiveData<List<CourseEntity>> mCourses;
    private AppRepository mRepository;

    public CourseReportViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mCourses = mRepository.mReportCourses;
    }

//    public void filterCourses(String searchText) {
//        if (searchText == "") {
//            mCourses = mRepository.mReportCourses;
//            return;
//        }
//
//        mCourses = Transformations.map(mCourses, course -> {
//
//        });
//
//    }
}
