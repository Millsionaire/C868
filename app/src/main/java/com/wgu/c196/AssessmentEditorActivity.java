package com.wgu.c196;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.wgu.c196.services.AlertDialogService;
import com.wgu.c196.services.TimeFormatService;
import com.wgu.c196.viewmodel.AssessmentEditorViewModel;

import java.util.Date;

import static com.wgu.c196.utilities.Constants.*;

public class AssessmentEditorActivity extends AppCompatActivity {

    @BindView(R.id.assessment_text)
    TextView mAssessmentText;

    @BindView(R.id.due_date_text)
    TextView mDueDate;

    @BindView(R.id.assessment_type_select)
    Spinner mSpinner;

    @BindView(R.id.assessment_type_label)
    TextView mSpinnerLabel;

    @BindView(R.id.must_save_text)
    TextView mMustSaveText;

    @BindView(R.id.set_alarm_btn)
    Button mAlarmBtn;

    @OnClick(R.id.set_alarm_btn)
    public void setAlarmClickHandler() {
        Date dueDate = TimeFormatService.getFormattedDate(mDueDate.getText().toString(),getString(R.string.invalidDateMessage), mAssessmentText.getContext());
        if (dueDate == null) {
            return;
        }
        AssessmentEntity savedAssessment = assessmentEditorViewModel.mLiveAssessment.getValue();
        if (savedAssessment != null) {
            try {
                long dueDateMilli = dueDate.getTime();
                String message = "Assessment Due: " + mAssessmentText.getText().toString();
                String title = mAssessmentText.getText().toString() + " Due Now";
                AlarmReceiver.setAlarm(getApplicationContext(), dueDateMilli, message, title, savedAssessment.getId(), "assessment");
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogService.showAlert("Something went wrong when setting alarm for due date", mAssessmentText.getContext());
                return;
            }
            AlertDialogService.showAlert("Alarm set for " + mAssessmentText.getText().toString(), mAssessmentText.getContext());
        }

    }

    private AssessmentEditorViewModel assessmentEditorViewModel;
    private boolean mNewAssessment, mEditing;
    private ArrayAdapter<AssessmentEntity.Type> assessmentTypeAdapter;
    private int mCourseId;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }

        try {
            initViewModel();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        loadSpinnerItems();
    }

    private void initViewModel() throws Exception {
        assessmentEditorViewModel = ViewModelProviders.of(this).get(AssessmentEditorViewModel.class);

        assessmentEditorViewModel.mLiveAssessment.observe(this, new Observer<AssessmentEntity>() {
            @Override
            public void onChanged(@Nullable AssessmentEntity assessmentEntity) {
                if (assessmentEntity != null && !mEditing) {
                    mAssessmentText.setText(assessmentEntity.getTitle());
                    mDueDate.setText(TimeFormatService.dateTimeFormat.format(assessmentEntity.getDueDate()));

                    currentPosition = getSpinnerPosition(assessmentEntity.getType());
                    mSpinner.setSelection(currentPosition);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        mCourseId = extras.getInt(COURSE_ID_KEY);
        if (mCourseId == 0) {
            throw new Exception("Invalid course id given to assessment editor activity");
        }

        if (extras.getBoolean(NEW_ASSESSMENT)) {
            setTitle(getString(R.string.new_assessment));
            showOrHideElements(View.VISIBLE, View.GONE);
        } else {
            setTitle(getString(R.string.edit_assessment));
            int assessmentId = extras.getInt(ASSESSMENT_ID_KEY);
            assessmentEditorViewModel.loadData(assessmentId);
            showOrHideElements(View.GONE, View.VISIBLE);
        }
    }

    private void showOrHideElements(int hideElements, int showElements) {
        mAlarmBtn.setVisibility(showElements);
        mMustSaveText.setVisibility(hideElements);
        mSpinner.setVisibility(showElements);
        mSpinnerLabel.setVisibility(showElements);
    }


    private void loadSpinnerItems() {
        AssessmentEntity.Type[] types = AssessmentEntity.Type.values();
        assessmentTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, types);
        mSpinner.setAdapter(assessmentTypeAdapter);
    }

    private AssessmentEntity.Type getSpinnerValue() {
        return (AssessmentEntity.Type) mSpinner.getSelectedItem();
    }

    private int getSpinnerPosition(AssessmentEntity.Type assessmentType) {
        return assessmentTypeAdapter.getPosition(assessmentType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNewAssessment) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_editor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            assessmentEditorViewModel.deleteAssessment();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    private void saveAndReturn() {
        Date dueDate = TimeFormatService.getFormattedDate(mDueDate.getText().toString(),getString(R.string.invalidDateMessage), mAssessmentText.getContext());
        if (dueDate == null) {
            return;
        }
        AssessmentEntity assessment = new AssessmentEntity(mCourseId, mAssessmentText.getText().toString(), dueDate);
        assessmentEditorViewModel.saveAssessment(assessment);
        finish();
    }

    @OnItemSelected(R.id.assessment_type_select)
    public void spinnerItemSelected(Spinner spinner, int position) {
        if (currentPosition != position) {
            assessmentEditorViewModel.updateType(getSpinnerValue());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

}
