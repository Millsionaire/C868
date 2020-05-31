package com.wgu.c196.database.entities;

import android.arch.persistence.room.*;
import com.wgu.c196.database.converters.StatusConverter;

import java.util.Date;
import java.util.List;

@Entity(tableName = "courses",
        indices = {
                @Index("term_id"),
                @Index("mentor_id")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = TermEntity.class,
                        parentColumns = "id",
                        childColumns = "term_id"
                ),
                @ForeignKey(
                        entity = MentorEntity.class,
                        parentColumns = "id",
                        childColumns = "mentor_id"
                )
        }
)
public class CourseEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "term_id")
    private int termId;

    private String title;

    private Date startDate;

    private Date endDate;

    /**
     * Status of the given task.
     * Enumerated Values: 0 (Active), 1 (Inactive), 2 (Completed)
     */
    @TypeConverters(StatusConverter.class)
    private Status status;

    @ColumnInfo(name = "mentor_id")
    private int mentorId;

    @Ignore
    private MentorEntity mentor;

    private String notes;

    @Ignore
    private List<AssessmentEntity> assessments;

    /**
     * Constructor for creating a new course with an auto-generated id
     *
     * @param title
     * @param startDate
     * @param endDate
     * @param status
     * @param mentorId
     * @param notes
     * @param assessments
     */
    @Ignore
    public CourseEntity(String title, Date startDate, Date endDate, Status status, int mentorId, String notes, List<AssessmentEntity> assessments) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.mentorId = mentorId;
        this.notes = notes;
        this.assessments = assessments;
    }

    /**
     * Constructor for creating a new course with an auto-generated id and no notes or assessments
     *
     * @param title
     * @param startDate
     * @param endDate
     * @param status
     * @param mentorId
     */
    @Ignore
    public CourseEntity(String title, Date startDate, Date endDate, Status status, int mentorId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.mentorId = mentorId;
    }

    /**
     * Constructor for when you want to create a new course and then assign values
     * separately
     */
    @Ignore
    public CourseEntity() {
    }

    /**
     * Constructor for editing an existing course with no assessments
     *
     * @param id        int
     * @param title     String
     * @param startDate Date
     * @param endDate   Date
     */
    public CourseEntity(int id, String title, Date startDate, Date endDate, Status status, int mentorId, String notes) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.mentorId = mentorId;
        this.notes = notes;
    }

    /**
     * Constructor for editing an existing course
     *
     * @param id        int
     * @param title     String
     * @param startDate Date
     * @param endDate   Date
     */
    public CourseEntity(int id, String title, Date startDate, Date endDate, Status status, int mentorId, String notes, List<AssessmentEntity> assessments) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.mentorId = mentorId;
        this.notes = notes;
        this.assessments = assessments;
    }

    //in progress, completed, dropped, plan to take
    @TypeConverters(StatusConverter.class)
    public enum Status {
        IN_PROGRESS(0),
        DROPPED(1),
        COMPLETED(2),
        PLAN_TO_TAKE(3);

        private int code;

        Status(int code) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public int getMentorId() {
        return mentorId;
    }

    public void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<AssessmentEntity> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<AssessmentEntity> assessments) {
        this.assessments = assessments;
    }

    public MentorEntity getMentor() {
        return mentor;
    }

    public void setMentor(MentorEntity mentor) {
        this.mentor = mentor;
    }
}
