package com.wgu.c196;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wgu.c196.database.TermEntity;
import com.wgu.c196.ui.TermsAdapter;
import com.wgu.c196.utilities.SampleData;

import java.util.ArrayList;
import java.util.List;

public class TermsActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @OnClick(R.id.fab)
    public void fabClickHandler() {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }

    private List<TermEntity> termsData = new ArrayList<>();
    private TermsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initRecyclerView();

        termsData.addAll(SampleData.getTerms());
        for (TermEntity term : termsData) {
            Log.i("C196", term.toString());
        }

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new TermsAdapter(termsData, this);
        mRecyclerView.setAdapter(mAdapter);
    }

}
