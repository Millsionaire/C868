package com.wgu.c196;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.wgu.c196.database.AppDatabase;
import com.wgu.c196.database.TermDao;
import com.wgu.c196.database.TermEntity;
import com.wgu.c196.utilities.SampleData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    public static final String TAG = "Junit";
    private AppDatabase mDb;
    private TermDao mDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context,
                AppDatabase.class).build();
        mDao = mDb.termDao();
        Log.i(TAG, "createDb");
    }

    @After
    public void closeDb() {
        mDb.close();
        Log.i(TAG, "closeDb");
    }

    @Test
    public void createAndRetrieveTerms() {
        mDao.insertAll(SampleData.getTerms());
        int count = mDao.getCount();
        Log.i(TAG, "createAndRetrieveTerms: count=" + count);
        assertEquals(SampleData.getTerms().size(), count);
    }

    @Test
    public void compareStrings() {
        mDao.insertAll(SampleData.getTerms());
        TermEntity original = SampleData.getTerms().get(0);
        TermEntity fromDb = mDao.getNoteById(1);
        assertEquals(original.getTitle(), fromDb.getTitle());
        assertEquals(1, fromDb.getId());
    }
}
