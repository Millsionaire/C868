package com.wgu.c196.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import com.wgu.c196.utilities.SampleData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppRepository {
    private static AppRepository ourInstance;

    public LiveData<List<TermEntity>> mTerms;
    private AppDatabase mDb;
    private Executor executor = Executors.newSingleThreadExecutor();

    public static AppRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new AppRepository(context);
        }
        return ourInstance;
    }

    private AppRepository(Context context) {
        mDb = AppDatabase.getInstance(context);
        mTerms = getAllTerms();
    }

    public void addSampleData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().insertAll(SampleData.getTerms());

                for(TermEntity term : SampleData.getTerms()) {
                    insertCoursesForTerm(term);
                };
            }
        });
    }

    private void insertCoursesForTerm(TermEntity term) {
        List<CourseEntity> courses = term.getCourses();
        if (courses != null) {
            for (CourseEntity course : courses) {
                TermEntity selectedTerm = mDb.termDao().getIdByTitle(term.getTitle());
                course.setTermId(selectedTerm.getId());
            }
            mDb.courseDao().insertAll(courses);
        }
    }

    private LiveData<List<TermEntity>> getAllTerms() {
        return mDb.termDao().getAll();
    }

    public void deleteAllTerms() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.courseDao().deleteAll();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().deleteAll();
            }
        });
    }

    public TermEntity getTermById(int termId) {
        return mDb.termDao().getTermById(termId);
    }

    public void insertTerm(final TermEntity term) {
//        List<CourseEntity> courses = term.getCourses();
//        if (!courses.isEmpty()) {
//            mDb.courseDao().insertAll(courses);
//        }
        insertCoursesForTerm(term);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().insertTerm(term);
            }
        });
    }

    public void deleteTerm(final TermEntity term) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().deleteTerm(term);
            }
        });
    }
}
