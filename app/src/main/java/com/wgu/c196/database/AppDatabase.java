package com.wgu.c196.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.*;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {TermEntity.class, CourseEntity.class, MentorEntity.class, AssessmentEntity.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, StatusConverter.class, AssessmentTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "AppDatabase.db";
    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract MentorDao mentorDao();
    public abstract AssessmentDao assessmentDao();

//    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE `courses` (\n" +
//                    "    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
//                    "    `term_id` INTEGER NOT NULL, \n" +
//                    "    `title` TEXT, \n" +
//                    "    `startDate` INTEGER, \n" +
//                    "    `endDate` INTEGER, \n" +
//                    "    `status` INTEGER, \n" +
//                    "    FOREIGN KEY(term_id) REFERENCES terms(id) \n" +
//                    ");");
//        }
//    };
//
//    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE `mentors` (\n" +
//                    "    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
//                    "    `name` TEXT NOT NULL, \n" +
//                    "    `phoneNumber` TEXT, \n" +
//                    "    `email` TEXT, \n" +
//                    "    UNIQUE(`name`, `phoneNumber`, `email`) \n" +
//                    ");");
//
//            database.execSQL("ALTER TABLE `courses` ADD COLUMN `mentor_id` INTEGER NOT NULL");
//            database.execSQL("");
//        }
//    };

//    .addMigrations(MIGRATION_1_2)

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }

        return instance;
    }
}
