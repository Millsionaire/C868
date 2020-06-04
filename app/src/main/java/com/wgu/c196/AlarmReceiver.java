package com.wgu.c196;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import static com.wgu.c196.utilities.Constants.ASSESSMENT_ID_KEY;
import static com.wgu.c196.utilities.Constants.COURSE_ID_KEY;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String alarmCounterFile = "alarmCounterFile";
    public static final String NEXT_ID = "nextId";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 0);
        int nextAlarmId = intent.getIntExtra("nextAlarmId", getAndIncrementNextId(context));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_today)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("message"));

        Intent newIntent;
        if (intent.getStringExtra("type").equals("course")) {
            newIntent = new Intent(context, CourseEditorActivity.class);
            newIntent.putExtra(COURSE_ID_KEY, id);
        } else {
            newIntent = new Intent(context, AssessmentEditorActivity.class);
            newIntent.putExtra(ASSESSMENT_ID_KEY, id);
        }

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(newIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent).setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(nextAlarmId, builder.build());
    }

    public static void setAlarm(Context context, long when, String message, String title, int id, String type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int nextId = getNextId(context);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        intent.putExtra("type", type);
        intent.putExtra("nextId", nextId);
        alarmManager.set(AlarmManager.RTC_WAKEUP, when, PendingIntent.getBroadcast(context, nextId, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        SharedPreferences sharingPreferences = context.getSharedPreferences(alarmCounterFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharingPreferences.edit();
        editor.putInt(Integer.toString(id), nextId);
        editor.commit();

        incrementNextId(context);
    }

    private static int getNextId(Context context) {
        SharedPreferences alarmPreferences;
        alarmPreferences = context.getSharedPreferences(alarmCounterFile, Context.MODE_PRIVATE);
        return alarmPreferences.getInt(NEXT_ID, 1);
    }


    private static int getAndIncrementNextId(Context context) {
        int nextId = getNextId(context);
        incrementNextId(context);
        return nextId;
    }

    private static void incrementNextId(Context context) {
        SharedPreferences alarmPreferences = context.getSharedPreferences(alarmCounterFile, Context.MODE_PRIVATE);
        int nextId = alarmPreferences.getInt(NEXT_ID, 1);
        SharedPreferences.Editor alarmEditor = alarmPreferences.edit();
        alarmEditor.putInt(NEXT_ID, nextId++);
        alarmEditor.apply();
    }
}
