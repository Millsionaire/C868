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
import com.wgu.c196.database.CourseEntity;
import com.wgu.c196.database.TermWithCourses;
import com.wgu.c196.ui.CoursesAdapter;
import com.wgu.c196.viewmodel.TermEditorViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.wgu.c196.utilities.Constants.*;

public class TermEditorActivity extends AppCompatActivity {

    @BindView(R.id.term_text)
    TextView mTermText;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView((R.id.start_date_text))
    TextView mStartDateText;

    @BindView((R.id.end_date_text))
    TextView mEndDateText;

    @OnClick(R.id.fab)
    public void fabClickHandler() {
        Intent intent = new Intent(this, TermEditorActivity.class);
        startActivity(intent);
    }

    private TermEditorViewModel termEditorViewModel;
    private boolean mNewTerm, mEditing;
    private CoursesAdapter mAdapter;
    private List<CourseEntity> courseData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
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
        termEditorViewModel = ViewModelProviders.of(this).get(TermEditorViewModel.class);

        final Observer<List<CourseEntity>> coursesObserver = new Observer<List<CourseEntity>>() {
            @Override
            public void onChanged(@Nullable List<CourseEntity> courseEntities) {
                courseData.clear();
                assert courseEntities != null;
                courseData.addAll(courseEntities);

                if (mAdapter == null) {
                    mAdapter = new CoursesAdapter(courseData, TermEditorActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        termEditorViewModel.mLiveTerm.observe(this, new Observer<TermWithCourses>() {
            @Override
            public void onChanged(@Nullable TermWithCourses termEntity) {
                if (termEntity != null && !mEditing) {
                    mTermText.setText(termEntity.term.getTitle());
                    mStartDateText.setText(termEntity.term.getStartDate().toString());
                    mEndDateText.setText(termEntity.term.getEndDate().toString());
                    if (termEntity.courses != null) {
                        termEditorViewModel.mCourses.observe(TermEditorActivity.this, coursesObserver);
                    }
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle(getString(R.string.new_term));
            mNewTerm = true;
        } else {
            setTitle(getString(R.string.edit_term));
            int termId = extras.getInt(TERM_ID_KEY);
            termEditorViewModel.loadData(termId);
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
        if (!mNewTerm) {
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
                if (courseData.size() == 0) {
                    termEditorViewModel.deleteTerm();
                    finish();
                }
                showDeleteTermWithCoursesErrorAlert();
            } catch (SQLiteConstraintException e) {
                showDeleteTermWithCoursesErrorAlert();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void showDeleteTermWithCoursesErrorAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mTermText.getContext());
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
        termEditorViewModel.saveTerm(mTermText.getText().toString());
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }
}
