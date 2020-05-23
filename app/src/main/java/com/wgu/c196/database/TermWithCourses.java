package com.wgu.c196.database;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class TermWithCourses {
        @Embedded
        public TermEntity term;
        @Relation(
                parentColumn = "id",
                entityColumn = "term_id"
        )
        public List<CourseEntity> courses;
}
