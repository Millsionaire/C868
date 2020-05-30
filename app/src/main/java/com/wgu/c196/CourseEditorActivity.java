package com.wgu.c196;

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
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wgu.c196.database.AssessmentEntity;
import com.wgu.c196.database.CourseWithAssessments;
import com.wgu.c196.ui.AssessmentAdapter;
import com.wgu.c196.viewmodel.CourseEditorViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.wgu.c196.utilities.Constants.*;

public class CourseEditorActivity extends AppCompatActivity {

    @BindView(R.id.course_text)
    TextView mCourseText;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView((R.id.start_date_text))
    TextView mStartDateText;

    @BindView((R.id.end_date_text))
    TextView mEndDateText;

    @OnClick(R.id.fab)
    public void fabClickHandler() {
        Intent intent = new Intent(this, AssessmentEditorActivity.class);
        startActivity(intent);
    }

    private CourseEditorViewModel courseEditorViewModel;
    private boolean mNewCourse, mEditing;
    private AssessmentAdapter mAdapter;
    private List<AssessmentEntity> assessmentData = new ArrayList<>();

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
        initRecyclerView();
        initViewModel();
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

        courseEditorViewModel.mLiveCourse.observe(this, new Observer<CourseWithAssessments>() {
            @Override
            public void onChanged(@Nullable CourseWithAssessments courseEntity) {
                if (courseEntity != null && !mEditing) {
                    mCourseText.setText(courseEntity.course.getTitle());
                    mStartDateText.setText(courseEntity.course.getStartDate().toString());
                    mEndDateText.setText(courseEntity.course.getEndDate().toString());
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
        builder1.setMessage(R.string.term_delete_alert_message);
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
        courseEditorViewModel.saveTerm(mCourseText.getText().toString());
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

}
