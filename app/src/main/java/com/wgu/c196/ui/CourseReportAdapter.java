package com.wgu.c196.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wgu.c196.R;
import com.wgu.c196.database.entities.CourseEntity;
import com.wgu.c196.services.TimeFormatService;

import java.util.List;

public class CourseReportAdapter extends RecyclerView.Adapter<CourseReportAdapter.ViewHolder> {

    private final List<CourseEntity> mCourses;

    public CourseReportAdapter(List<CourseEntity> mCourses) {
        this.mCourses = mCourses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.course_report_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CourseEntity course = mCourses.get(position);
        holder.mCourseTitleText.setText(course.getTitle());
        holder.mStartDate.setText(TimeFormatService.getDateString(course.getStartDate()));
        holder.mEndDate.setText(TimeFormatService.getDateString(course.getEndDate()));
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_text)
        TextView mCourseTitleText;

        @BindView(R.id.start_date_text)
        TextView mStartDate;

        @BindView(R.id.end_date_text)
        TextView mEndDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
