package com.wgu.c196.database.entities;

import android.arch.persistence.room.*;
import com.wgu.c196.database.converters.AssessmentTypeConverter;

import java.util.Date;

@Entity(tableName = "assessments",
        indices = {
                @Index("course_id")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = CourseEntity.class,
                        parentColumns = "id",
                        childColumns = "course_id"
                )}
)
public class AssessmentEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "course_id")
    private int courseId;

    private String title;

    private Date dueDate;

    /**
     * Status of the given task.
     * Enumerated Values: 0 (Active), 1 (Inactive), 2 (Completed)
     */
    @TypeConverters(AssessmentTypeConverter.class)
    private AssessmentEntity.Type type;

    public AssessmentEntity(int id, int courseId, String title, Date dueDate, Type type) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.dueDate = dueDate;
        this.type = type;
    }

    @Ignore
    public AssessmentEntity(int courseId, String title, Date dueDate, Type type) {
        this.courseId = courseId;
        this.title = title;
        this.dueDate = dueDate;
        this.type = type;
    }

    @Ignore
    public AssessmentEntity(String title, Date dueDate, Type type) {
        this.title = title;
        this.dueDate = dueDate;
        this.type = type;
    }

    @Ignore
    public AssessmentEntity() {
    }

    //performance, objective
    @TypeConverters(AssessmentTypeConverter.class)
    public enum Type {
        PERFORMANCE(0),
        OBJECTIVE(1);

        private int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
