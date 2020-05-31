package com.wgu.c196.database.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import com.wgu.c196.database.entities.CourseEntity;
import com.wgu.c196.database.entities.TermEntity;

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
