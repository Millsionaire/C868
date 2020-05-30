package com.wgu.c196.database;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class CourseWithAssessments {
    @Embedded
    public CourseEntity course;
    @Relation(
            parentColumn = "id",
            entityColumn = "course_id"
    )
    public List<AssessmentEntity> assessments;
}
