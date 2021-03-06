package com.wgu.c196.database.converters;

import android.arch.persistence.room.TypeConverter;
import com.wgu.c196.database.entities.CourseEntity;

import static com.wgu.c196.database.entities.CourseEntity.Status.*;

public class StatusConverter {
    @TypeConverter
    public static CourseEntity.Status toStatus(int status) {
        if (status == IN_PROGRESS.getCode()) {
            return IN_PROGRESS;
        } else if (status == DROPPED.getCode()) {
            return DROPPED;
        } else if (status == COMPLETED.getCode()) {
            return COMPLETED;
        } else if (status == PLAN_TO_TAKE.getCode()) {
            return PLAN_TO_TAKE;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static int toInteger(CourseEntity.Status status) {
        return status.getCode();
    }
}
