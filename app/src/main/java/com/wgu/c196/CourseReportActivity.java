package com.wgu.c196;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wgu.c196.database.entities.CourseEntity;
import com.wgu.c196.ui.CourseReportAdapter;
import com.wgu.c196.viewmodel.CourseReportViewModel;

import java.util.ArrayList;
import java.util.List;

public class CourseReportActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<CourseEntity> courseData = new ArrayList<>();
    private CourseReportAdapter mAdapter;
    private CourseReportViewModel courseReportViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        initRecyclerView();
        initViewModel();
    }

    private void initViewModel() {
        final Observer<List<CourseEntity>> coursesObserver =
                new Observer<List<CourseEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<CourseEntity> courseEntities) {
                        courseData.clear();
                        assert courseEntities != null;
                        courseData.addAll(courseEntities);

                        if (mAdapter == null) {
                            mAdapter = new CourseReportAdapter(courseData);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                };
        courseReportViewModel = ViewModelProviders.of(this).get(CourseReportViewModel.class);
        courseReportViewModel.mCourses.observe(this, coursesObserver);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);
    }
}