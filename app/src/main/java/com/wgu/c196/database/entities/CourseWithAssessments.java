package com.wgu.c196.database.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import com.wgu.c196.database.entities.AssessmentEntity;
import com.wgu.c196.database.entities.CourseEntity;

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
