package com.wgu.c196;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wgu.c196.database.TermEntity;
import com.wgu.c196.ui.TermsAdapter;
import com.wgu.c196.viewmodel.TermsViewModel;

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
    private TermsViewModel termsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initViewModel();
        initRecyclerView();

        termsData.addAll(termsViewModel.mTerms);
        for (TermEntity term : termsData) {
            Log.i("C196", term.toString());
        }
    }

    private void initViewModel() {
        termsViewModel = ViewModelProviders.of(this).get(TermsViewModel.class);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new TermsAdapter(termsData, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_sample_data) {
            addSampleData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSampleData() {
        termsViewModel.addSampleData();
    }

}
