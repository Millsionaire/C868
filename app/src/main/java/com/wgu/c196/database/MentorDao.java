package com.wgu.c196.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
interface MentorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMentor(MentorEntity mentorEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<MentorEntity> mentors);

    @Delete
    void deleteMentor(MentorEntity mentorEntity);

    @Query("SELECT * FROM mentors WHERE id = :id")
    MentorEntity getMentorById(int id);

//    @Query("SELECT * FROM mentors WHERE id = :id")
//    MentorEntity getMentorById(int id);

    @Query("SELECT * FROM mentors ORDER BY name ASC")
    LiveData<List<MentorEntity>> getAll();

    @Query("DELETE FROM mentors")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM mentors")
    int getCount();

//    @Query("SELECT * FROM mentors WHERE term_id = :termId")
//    LiveData<List<MentorEntity>> getMentorsByTermId(int termId);
}
