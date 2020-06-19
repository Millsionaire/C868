package com.wgu.c196.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wgu.c196.R;
import com.wgu.c196.database.entities.CourseEntity;
import com.wgu.c196.services.TimeFormatService;

import java.util.ArrayList;
import java.util.List;

public class CourseReportAdapter extends RecyclerView.Adapter<CourseReportAdapter.ViewHolder> implements Filterable {

    private List<CourseEntity> courseList;
    private ArrayList<CourseEntity> courseListFull = new ArrayList<>();

    public CourseReportAdapter() { }

    public void setCourses(List<CourseEntity> courses) {
        courseList = courses;
        courseListFull.addAll(courses);
        notifyDataSetChanged();
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
        final CourseEntity course = courseList.get(position);
        holder.mCourseTitleText.setText(course.getTitle());
        holder.mStartDate.setText(TimeFormatService.getDateString(course.getStartDate()));
        holder.mEndDate.setText(TimeFormatService.getDateString(course.getEndDate()));
    }

    @Override
    public int getItemCount() {
        if (courseList == null) {
            return 0;
        }
        return courseList.size();
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

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                constraint = constraint.toString().toLowerCase();
                courseList.clear();
                if (constraint.length() == 0) {
                    courseList.addAll(courseListFull);
                } else {
                    for (CourseEntity course : courseListFull) {
                        if (course.getTitle().toLowerCase().contains(constraint)) {
                            courseList.add(course);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = courseList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}
