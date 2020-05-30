package com.wgu.c196.utilities;

import com.wgu.c196.database.AssessmentEntity;
import com.wgu.c196.database.TermEntity;
import com.wgu.c196.database.CourseEntity;

import java.util.*;

public class SampleData {
    private static final String TITLE_1 = "Term 1";
    private static final String TITLE_2 = "Term 2";
    private static final String TITLE_3 = "Term 3";
    private static final String TITLE_4 = "Term 4";
    private static final String TITLE_5 = "Term 5";
    private static final String COURSE_1 = "Course 1";
    private static final String COURSE_2 = "Course 2";
    private static final String ASSESSMENT_1 = "Assessment 1";
    private static final String ASSESSMENT_2 = "Assessment 2";

    private static Date getDate(int diff) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, diff);
        return cal.getTime();
    }

    public static List<TermEntity> getTerms() {
        List<TermEntity> terms = new ArrayList<>();

        List<AssessmentEntity> assessments1 = new ArrayList<>();
        assessments1.add(new AssessmentEntity(ASSESSMENT_1, getDate(1), AssessmentEntity.Type.OBJECTIVE));
        assessments1.add(new AssessmentEntity(ASSESSMENT_2, getDate(1), AssessmentEntity.Type.PERFORMANCE));

        List<CourseEntity> courses1 = new ArrayList<>();
        courses1.add(new CourseEntity(COURSE_1, getDate(0), getDate(1), CourseEntity.Status.IN_PROGRESS, 1, "Some Notes", assessments1));
        courses1.add(new CourseEntity(COURSE_2, getDate(2), getDate(3), CourseEntity.Status.PLAN_TO_TAKE, 1));

        List<CourseEntity> courses2 = new ArrayList<>();
        courses2.add(new CourseEntity(COURSE_1, getDate(0), getDate(1), CourseEntity.Status.IN_PROGRESS, 1));
        courses2.add(new CourseEntity(COURSE_2, getDate(2), getDate(3), CourseEntity.Status.PLAN_TO_TAKE, 1, "Some other notes", assessments1));

        terms.add(new TermEntity(TITLE_1, getDate(0), getDate(1), courses1));
        terms.add(new TermEntity(TITLE_2, getDate(2), getDate(3), courses2));
        terms.add(new TermEntity(TITLE_3, getDate(4), getDate(5)));
        terms.add(new TermEntity(TITLE_4, getDate(5), getDate(6)));
        terms.add(new TermEntity(TITLE_5, getDate(7), getDate(8)));

        return terms;
    }

}
