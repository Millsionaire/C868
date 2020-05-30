package com.wgu.c196;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wgu.c196.database.AssessmentEntity;
import com.wgu.c196.database.CourseWithAssessments;
import com.wgu.c196.ui.CoursesAdapter;
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
//        Intent intent = new Intent(this, TermEditorActivity.class);
//        startActivity(intent);
    }

    private CourseEditorViewModel courseEditorViewModel;
    private boolean mNewCourse, mEditing;
    private CoursesAdapter mAdapter;
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
//        initRecyclerView();
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

//                if (mAdapter == null) {
//                    mAdapter = new CoursesAdapter(assessmentData, CourseEditorActivity.this);
//                    mRecyclerView.setAdapter(mAdapter);
//                } else {
//                    mAdapter.notifyDataSetChanged();
//                }
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

}
