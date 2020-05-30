package com.wgu.c196.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
interface AssessmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssessment(AssessmentEntity assessmentEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AssessmentEntity> assessments);

    @Delete
    void deleteAssessment(AssessmentEntity assessmentEntity);

    @Query("SELECT * FROM assessments WHERE id = :id")
    AssessmentEntity getAssessmentById(int id);

//    @Query("SELECT * FROM assessments WHERE id = :id")
//    AssessmentEntity getAssessmentById(int id);

    @Query("SELECT * FROM assessments ORDER BY dueDate DESC")
    LiveData<List<AssessmentEntity>> getAll();

    @Query("DELETE FROM assessments")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM assessments")
    int getCount();

    @Query("SELECT * FROM assessments WHERE course_id = :courseId")
    LiveData<List<AssessmentEntity>> getAssessmentsByCourseId(int courseId);
}
