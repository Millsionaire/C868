package com.wgu.c196.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import com.wgu.c196.utilities.Constants;
import com.wgu.c196.utilities.SampleData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppRepository {
    private static AppRepository ourInstance;

    public LiveData<List<TermEntity>> mTerms;
    public LiveData<List<CourseEntity>> mCourses;
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
                mDb.mentorDao().insertMentor(new MentorEntity("Becky Stovall", "555-222-4444", "bstovall@wgu.edu"));
                mDb.termDao().insertAll(SampleData.getTerms());

                for (TermEntity term : SampleData.getTerms()) {
                    insertCoursesForTerm(term);
                }
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

    public TermWithCourses getTermWithCoursesById(int termId) {
        return mDb.termDao().getTermWithCourses(termId);
    }

    public void insertTerm(final TermEntity term) {
        insertCoursesForTerm(term);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.termDao().insertTerm(term);
            }
        });
    }

    public void deleteTerm(final TermWithCourses term) {
        final Boolean[] foreignKeyConstraint = {false};
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mDb.termDao().deleteTerm(term.term);
                } catch (Exception e) {
                    String msg = e.getMessage();
                    assert msg != null;
                    if (msg.equals("foreign key constraint failed (code 19)")) {
                        foreignKeyConstraint[0] = true;
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (foreignKeyConstraint[0]) {
            throw new SQLiteConstraintException();
        }
    }

    public LiveData<List<CourseEntity>> getCoursedByTermId(final int termId) {
        return mDb.courseDao().getCoursesByTermId(termId);
    }
}
