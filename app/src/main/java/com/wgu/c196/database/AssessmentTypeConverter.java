package com.wgu.c196.database;

import android.arch.persistence.room.TypeConverter;

import static com.wgu.c196.database.AssessmentEntity.Type.*;

class AssessmentTypeConverter {
    @TypeConverter
    public static AssessmentEntity.Type toType(int status) {
        if (status == PERFORMANCE.getCode()) {
            return PERFORMANCE;
        } else if (status == OBJECTIVE.getCode()) {
            return OBJECTIVE;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static int toInteger(AssessmentEntity.Type type) {
        return type.getCode();
    }
}
