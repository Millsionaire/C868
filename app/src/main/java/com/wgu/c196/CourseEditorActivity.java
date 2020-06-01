package com.wgu.c196;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
import com.wgu.c196.ui.AssessmentAdapter;
import com.wgu.c196.viewmodel.CourseEditorViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wgu.c196.utilities.Constants.*;

public class CourseEditorActivity extends AppCompatActivity {

    @BindView(R.id.course_text)
    TextView mCourseText;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.start_date_text)
    TextView mStartDateText;

    @BindView(R.id.end_date_text)
    TextView mEndDateText;

    @BindView(R.id.mentor_spinner)
    Spinner mMentorSpinner;

    @BindView(R.id.mentor_name_text)
    TextView mMentorName;

    @BindView(R.id.mentor_phone_text)
    TextView mMentorPhone;

    @BindView(R.id.mentor_email_text)
    TextView mMentorEmail;

    @OnClick(R.id.fab)
    public void fabClickHandler() {
        Intent intent = new Intent(this, AssessmentEditorActivity.class);
        startActivity(intent);
    }

    @BindView(R.id.status_spinner)
    Spinner mStatusSpinner;

    @BindView(R.id.notes)
    TextView mNotes;

    private CourseEditorViewModel courseEditorViewModel;
    private boolean mNewCourse, mEditing;
    private AssessmentAdapter mAdapter;
    private List<AssessmentEntity> assessmentData = new ArrayList<>();
    private List<String> mentors = new ArrayList<>();
    private ArrayAdapter<CourseEntity.Status> courseStatusAdapter;
    private ArrayAdapter<String> courseMentorAdapter;
    private int currentMentorPosition;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }
        loadMentorSpinnerItems();
        initRecyclerView();
        initViewModel();
        loadCourseStatusSpinnerItems();
    }

    private void initViewModel() {
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
                    mStartDateText.setText(dateFormat.format(courseEntity.course.getStartDate()));
                    mEndDateText.setText(dateFormat.format(courseEntity.course.getEndDate()));
                    mMentorName.setText(courseEntity.course.getMentor().getName());
                    mMentorPhone.setText(courseEntity.course.getMentor().getPhoneNumber());
                    mMentorEmail.setText(courseEntity.course.getMentor().getEmail());

                    currentMentorPosition = getMentorSpinnerPosition(courseEntity.course.getMentor().getName());
                    mMentorSpinner.setSelection(currentMentorPosition);

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
        if (extras == null) {
            setTitle(getString(R.string.new_course));
            mNewCourse = true;
        } else {
            setTitle(getString(R.string.edit_course));
            int courseId = extras.getInt(COURSE_ID_KEY);
            courseEditorViewModel.loadData(courseId);
        }
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

    private MentorEntity getMentorSpinnerValue() {
        return (MentorEntity) mMentorSpinner.getSelectedItem();
    }

    private int getStatusSpinnerPosition(CourseEntity.Status courseStatus) {
        return courseStatusAdapter.getPosition(courseStatus);
    }

    private int getMentorSpinnerPosition(String mentorName) {
        return courseMentorAdapter.getPosition(mentorName);
    }


    @OnItemSelected(R.id.status_spinner)
    public void spinnerStatusItemSelected(Spinner spinner, int position) {
        return;
    }

    @OnItemSelected(R.id.mentor_spinner)
    public void spinnerMentorItemSelected(Spinner spinner, int position) {
        if (currentMentorPosition != position) {
            courseEditorViewModel.updateMentor(spinner.getSelectedItem().toString());
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
            try {
                saveAndReturn();
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
        try {
            saveAndReturn();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveAndReturn() throws ParseException {
        Date startDate = dateFormat.parse(mStartDateText.getText().toString());
        Date endDate = dateFormat.parse(mEndDateText.getText().toString());
        CourseEntity course = new CourseEntity(mCourseText.getText().toString(), startDate, endDate, mNotes.getText().toString());
        courseEditorViewModel.saveTerm(course);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

}
