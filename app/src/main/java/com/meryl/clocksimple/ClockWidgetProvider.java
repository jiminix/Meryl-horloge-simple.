package com.meryl.clocksimple;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class ClockWidgetProvider extends AppWidgetProvider {
    static final String PREFS="clock_state";

    @Override public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
        updateAll(c);
    }

    @Override public void onReceive(Context c, Intent i) {
        super.onReceive(c,i);
        updateAll(c);
    }

    public static void updateAll(Context c) {
        AppWidgetManager m=AppWidgetManager.getInstance(c);
        ComponentName cn=new ComponentName(c, ClockWidgetProvider.class);
        int[] ids=m.getAppWidgetIds(cn);

        for(int id:ids) updateOne(c,m,id);
    }

    private static void updateOne(Context c, AppWidgetManager m, int id) {
        RemoteViews rv=new RemoteViews(c.getPackageName(),R.layout.widget_clock);

        Intent open=new Intent(c, MainActivity.class);
        open.setAction("OPEN_SAMSUNG_CLOCK");

        PendingIntent pi=PendingIntent.getActivity(
            c,
            id,
            open,
            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE
        );

        rv.setOnClickPendingIntent(R.id.widget_root,pi);

        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        boolean alarm=am!=null && am.getNextAlarmClock()!=null;
        rv.setViewVisibility(R.id.alarm_plus, alarm ? View.VISIBLE : View.GONE);

        var p=c.getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        boolean active=p.getBoolean("active",false);
        boolean chrono=p.getBoolean("chrono",false);

        if(active) {
            rv.setViewVisibility(R.id.clock_mode,View.GONE);

            if(chrono) {
                long base=p.getLong("base",0L);
                boolean countdown=p.getBoolean("countdown",false);

                rv.setViewVisibility(R.id.fallback_timer_text,View.GONE);
                rv.setViewVisibility(R.id.active_chronometer,View.VISIBLE);
                rv.setChronometer(R.id.active_chronometer,base,null,true);
                rv.setChronometerCountDown(R.id.active_chronometer,countdown);
            } else {
                rv.setViewVisibility(R.id.active_chronometer,View.GONE);
                rv.setViewVisibility(R.id.fallback_timer_text,View.VISIBLE);
                rv.setTextViewText(R.id.fallback_timer_text,p.getString("text",""));
            }
        } else {
            rv.setViewVisibility(R.id.clock_mode,View.VISIBLE);
            rv.setViewVisibility(R.id.active_chronometer,View.GONE);
            rv.setViewVisibility(R.id.fallback_timer_text,View.GONE);
        }

        m.updateAppWidget(id,rv);
    }

    public static void openSamsungClock(Context c) {
        try {
            Intent i=c.getPackageManager().getLaunchIntentForPackage("com.sec.android.app.clockpackage");

            if(i!=null){
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(i);
                return;
            }
        } catch(Exception ignored) {}

        Intent fallback=new Intent(android.provider.AlarmClock.ACTION_SHOW_ALARMS);
        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            c.startActivity(fallback);
        } catch(Exception ignored) {}
    }
}
