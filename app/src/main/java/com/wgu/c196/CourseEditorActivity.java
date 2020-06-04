package com.wgu.c196;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.wgu.c196.database.entities.AssessmentEntity;
import com.wgu.c196.database.entities.CourseEntity;
import com.wgu.c196.database.entities.CourseWithAssessments;
import com.wgu.c196.database.entities.MentorEntity;
import com.wgu.c196.services.AlertDialogService;
import com.wgu.c196.services.TimeFormatService;
import com.wgu.c196.ui.AssessmentAdapter;
import com.wgu.c196.viewmodel.CourseEditorViewModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wgu.c196.utilities.Constants.*;

public class CourseEditorActivity extends AppCompatActivity {

    @BindView(R.id.course_text)
    TextView mCourseText;

    @BindView(R.id.start_date_text)
    TextView mStartDateText;

    @BindView(R.id.end_date_text)
    TextView mEndDateText;

    @BindView(R.id.notes)
    TextView mNotes;

    @BindView(R.id.must_save_text)
    TextView mMustSaveText;

    @BindView(R.id.mentor_spinner)
    Spinner mMentorSpinner;

    @BindView(R.id.mentor_select_label)
    TextView mMentorSelectLabel;

    @BindView(R.id.mentor_information)
    TextView mMentorInfoText;

    @BindView(R.id.mentor_name_text)
    TextView mMentorName;

    @BindView(R.id.mentor_phone_text)
    TextView mMentorPhone;

    @BindView(R.id.mentor_email_text)
    TextView mMentorEmail;

    @BindView(R.id.status_label)
    TextView mStatusLabel;

    @BindView(R.id.status_spinner)
    Spinner mStatusSpinner;

    @BindView(R.id.assessments_label)
    TextView mAssessmentsLabel;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.shareNotesButton)
    Button mButton;

    @OnClick(R.id.fab)
    public void fabClickHandler() {
        Intent intent = new Intent(this, AssessmentEditorActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.shareNotesButton)
    public void btnClickHandler() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mNotes.getText().toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private int mTermId;
    private CourseEditorViewModel courseEditorViewModel;
    private boolean mNewCourse, mEditing;
    private AssessmentAdapter mAdapter;
    private List<AssessmentEntity> assessmentData = new ArrayList<>();
    private List<String> mentors = new ArrayList<>();
    private ArrayAdapter<CourseEntity.Status> courseStatusAdapter;
    private ArrayAdapter<String> courseMentorAdapter;
    private int currentMentorPosition;
    private int currentStatusPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }
        loadMentorSpinnerItems();
        initRecyclerView();
        try {
            initViewModel();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        loadCourseStatusSpinnerItems();
    }

    private void initViewModel() throws Exception {
        courseEditorViewModel = ViewModelProviders.of(this).get(CourseEditorViewModel.class);

        final Observer<List<AssessmentEntity>> assessmentObserver = new Observer<List<AssessmentEntity>>() {
            @Override
            public void onChanged(@Nullable List<AssessmentEntity> assessments) {
                assessmentData.clear();
                assert assessments != null;
                assessmentData.addAll(assessments);

                if (mAdapter == null) {
                    mAdapter = new AssessmentAdapter(assessmentData, CourseEditorActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        courseEditorViewModel.mMentors.observe(this, new Observer<List<MentorEntity>>() {
            @Override
            public void onChanged(@Nullable List<MentorEntity> mentorEntityList) {
                for (MentorEntity mentor: mentorEntityList) {
                    mentors.add(mentor.toString());
                }
                loadMentorSpinnerItems();
            }
        });

        courseEditorViewModel.mLiveCourse.observe(this, new Observer<CourseWithAssessments>() {
            @Override
            public void onChanged(@Nullable CourseWithAssessments courseEntity) {
                if (courseEntity != null && !mEditing) {
                    mCourseText.setText(courseEntity.course.getTitle());
                    mStartDateText.setText(TimeFormatService.dateFormat.format(courseEntity.course.getStartDate()));
                    mEndDateText.setText(TimeFormatService.dateFormat.format(courseEntity.course.getEndDate()));
                    mMentorName.setText(courseEntity.course.getMentor().getName());
                    mMentorPhone.setText(courseEntity.course.getMentor().getPhoneNumber());
                    mMentorEmail.setText(courseEntity.course.getMentor().getEmail());

                    currentMentorPosition = getMentorSpinnerPosition(courseEntity.course.getMentor().getName());
                    currentStatusPosition = getStatusSpinnerPosition(courseEntity.course.getStatus());

                    mMentorSpinner.setSelection(currentMentorPosition);
                    mStatusSpinner.setSelection(currentStatusPosition);

                    int coursePosition = getStatusSpinnerPosition(courseEntity.course.getStatus());
                    mStatusSpinner.setSelection(coursePosition);

                    mNotes.setText(courseEntity.course.getNotes());
                    if (courseEntity.assessments != null) {
                        courseEditorViewModel.mAssessments.observe(CourseEditorActivity.this, assessmentObserver);
                    }
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        mTermId = extras.getInt(TERM_ID_KEY);
        if (mTermId == 0) {
            throw new Exception("Invalid term id given to course editor activity");
        }

        if (extras.getBoolean("NEW_COURSE")) {
            setTitle(getString(R.string.new_course));
            mNewCourse = true;
            showOrHideElements(View.VISIBLE, View.GONE);
        } else {
            setTitle(getString(R.string.edit_course));
            int courseId = extras.getInt(COURSE_ID_KEY);
            showOrHideElements(View.GONE, View.VISIBLE);
            courseEditorViewModel.loadData(courseId);
        }
    }

    @SuppressLint("RestrictedApi")
    private void showOrHideElements(int hideElements, int showElements) {
        mMustSaveText.setVisibility(hideElements);
        mMentorSelectLabel.setVisibility(showElements);
        mMentorSpinner.setVisibility(showElements);
        mMentorInfoText.setVisibility(showElements);
        mMentorName.setVisibility(showElements);
        mMentorPhone.setVisibility(showElements);
        mMentorEmail.setVisibility(showElements);
        mStatusLabel.setVisibility(showElements);
        mStatusSpinner.setVisibility(showElements);
        mAssessmentsLabel.setVisibility(showElements);
        mRecyclerView.setVisibility(showElements);
        mFab.setVisibility(showElements);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);
    }

    private void loadCourseStatusSpinnerItems() {
        courseStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CourseEntity.Status.values());
        mStatusSpinner.setAdapter(courseStatusAdapter);
    }

    private void loadMentorSpinnerItems() {
        courseMentorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mentors);
        mMentorSpinner.setAdapter(courseMentorAdapter);
    }

    private CourseEntity.Status getCourseSpinnerValue() {
        return (CourseEntity.Status) mStatusSpinner.getSelectedItem();
    }

    private String getMentorSpinnerValue() {
        return mMentorSpinner.getSelectedItem().toString();
    }

    private int getStatusSpinnerPosition(CourseEntity.Status courseStatus) {
        return courseStatusAdapter.getPosition(courseStatus);
    }

    private int getMentorSpinnerPosition(String mentorName) {
        return courseMentorAdapter.getPosition(mentorName);
    }

    @OnItemSelected(R.id.status_spinner)
    public void spinnerStatusItemSelected(Spinner spinner, int position) {
        if (currentStatusPosition != position) {
            courseEditorViewModel.updateStatus(getCourseSpinnerValue());
        }
    }

    @OnItemSelected(R.id.mentor_spinner)
    public void spinnerMentorItemSelected(Spinner spinner, int position) {
        if (currentMentorPosition != position) {
            courseEditorViewModel.updateMentor(getMentorSpinnerValue());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNewCourse) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_term_editor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            try {
                if (assessmentData.size() == 0) {
                    courseEditorViewModel.deleteCourse();
                    finish();
                }
                showDeleteCourseWithAssessmentssErrorAlert();
            } catch (SQLiteConstraintException e) {
                showDeleteCourseWithAssessmentssErrorAlert();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void showDeleteCourseWithAssessmentssErrorAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mCourseText.getContext());
        builder1.setMessage(R.string.course_delete_alert_message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.alert_ok_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {
        Date startDate;
        Date endDate;
        try {
            startDate = TimeFormatService.dateFormat.parse(mStartDateText.getText().toString());
            endDate = TimeFormatService.dateFormat.parse(mEndDateText.getText().toString());
        } catch (Exception e) {
            AlertDialogService.showAlert(getString(R.string.invalidDateMessage), mCourseText.getContext());
            return;
        }
        CourseEntity course = new CourseEntity(mTermId, mCourseText.getText().toString(), startDate, endDate, mNotes.getText().toString());
        courseEditorViewModel.saveCourse(course);

        int courseId = courseEditorViewModel.mLiveCourse.getValue().course.getId();

        long startDateMilli = startDate.getTime();
        String startMessage = "Course Starting! " + mCourseText.getText().toString() + " is about to begin. " + startDate;
        String startTitle = "Course starting soon!";
        setAlarm(startDateMilli, startMessage, startTitle, courseId);

        long endDateMilli = endDate.getTime();
        String endMessage = "Course Ending! " + mCourseText.getText().toString() + " is about to end. " + endDate;
        String endTitle = "Course ending soon!";
        setAlarm(endDateMilli, endMessage, endTitle, courseId);

        finish();
    }

    private void setAlarm(long when, String message, String title, int courseId){
          AlarmReceiver.setAlarm(getApplicationContext(), when, message, title, courseId, "course");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

}
