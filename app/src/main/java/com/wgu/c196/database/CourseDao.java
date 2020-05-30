package com.wgu.c196.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(CourseEntity courseEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CourseEntity> courses);

    @Delete
    void deleteCourse(CourseEntity courseEntity);

    @Query("SELECT * FROM courses WHERE id = :id")
    CourseEntity getCourseById(int id);

//    @Query("SELECT * FROM courses WHERE id = :id")
//    CourseEntity getCourseById(int id);

    @Query("SELECT * FROM courses ORDER BY startDate DESC")
    LiveData<List<CourseEntity>> getAll();

    @Query("DELETE FROM courses")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM courses")
    int getCount();

    @Query("SELECT * FROM courses WHERE term_id = :termId")
    LiveData<List<CourseEntity>> getCoursesByTermId(int termId);

    @Query("SELECT * FROM courses WHERE title = :title")
    CourseEntity getIdByTitle(String title);
}
