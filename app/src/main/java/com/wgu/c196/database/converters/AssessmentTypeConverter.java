package com.wgu.c196.database.converters;

import android.arch.persistence.room.TypeConverter;
import com.wgu.c196.database.entities.AssessmentEntity;

import static com.wgu.c196.database.entities.AssessmentEntity.Type.*;

public class AssessmentTypeConverter {
    @TypeConverter
    public static AssessmentEntity.Type toType(int type) {
        if (type == PERFORMANCE.getCode()) {
            return PERFORMANCE;
        } else if (type == OBJECTIVE.getCode()) {
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
