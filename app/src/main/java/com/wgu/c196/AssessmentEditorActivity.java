package com.wgu.c196;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import com.wgu.c196.database.entities.AssessmentEntity;
import com.wgu.c196.viewmodel.AssessmentEditorViewModel;

import static com.wgu.c196.utilities.Constants.ASSESSMENT_ID_KEY;

public class AssessmentEditorActivity extends AppCompatActivity {

    @BindView(R.id.assessment_text)
    TextView mAssessmentText;

    @BindView(R.id.due_date_text)
    TextView mDueDate;

    @BindView(R.id.assessment_type_select)
    Spinner mSpinner;

    private AssessmentEditorViewModel assessmentEditorViewModel;
    private boolean mNewAssessment, mEditing;
    private ArrayAdapter<AssessmentEntity.Type> assessmentTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        initViewModel();
        loadSpinnerItems();
    }

    private void initViewModel() {
        assessmentEditorViewModel = ViewModelProviders.of(this).get(AssessmentEditorViewModel.class);

        assessmentEditorViewModel.mLiveAssessment.observe(this, new Observer<AssessmentEntity>() {
            @Override
            public void onChanged(@Nullable AssessmentEntity assessmentEntity) {
                if (assessmentEntity != null && !mEditing) {
                    mAssessmentText.setText(assessmentEntity.getTitle());
                    mDueDate.setText(assessmentEntity.getDueDate().toString());
                    int position = getSpinnerPosition(assessmentEntity.getType());
                    mSpinner.setSelection(position);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle(getString(R.string.new_assessment));
            mNewAssessment = true;
        } else {
            setTitle(getString(R.string.edit_assessment));
            int assessmentId = extras.getInt(ASSESSMENT_ID_KEY);
            assessmentEditorViewModel.loadData(assessmentId);
        }
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


    @OnItemSelected(R.id.assessment_type_select)
    public void spinnerItemSelected(Spinner spinner, int position) {
        // code here
    }

}
