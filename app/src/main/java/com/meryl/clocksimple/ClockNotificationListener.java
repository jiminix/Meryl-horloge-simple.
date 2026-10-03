package com.meryl.clocksimple;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClockNotificationListener extends NotificationListenerService {
    private static final String CLOCK="com.sec.android.app.clockpackage";
    private static final Pattern TIME=Pattern.compile("(?<!\\d)(\\d{1,2}:)?\\d{1,2}:\\d{2}(?!\\d)");

    @Override public void onListenerConnected(){ refresh(); }

    @Override public void onNotificationPosted(StatusBarNotification sbn){
        if(CLOCK.equals(sbn.getPackageName())) refresh();
    }

    @Override public void onNotificationRemoved(StatusBarNotification sbn){
        if(CLOCK.equals(sbn.getPackageName())) refresh();
    }

    private void refresh() {
        StatusBarNotification best=null;

        try {
            for(StatusBarNotification s:getActiveNotifications()) {
                if(CLOCK.equals(s.getPackageName()) && looksLikeTimer(s.getNotification())) {
                    best=s;
                    break;
                }
            }
        } catch(Exception ignored) {}

        var e=getSharedPreferences(ClockWidgetProvider.PREFS,Context.MODE_PRIVATE).edit();

        if(best==null) {
            e.clear().putBoolean("active",false).apply();
            ClockWidgetProvider.updateAll(this);
            return;
        }

        Notification n=best.getNotification();
        Bundle x=n.extras;
        boolean show=x.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER,false);

        if(show) {
            long base=SystemClock.elapsedRealtime() + (n.when-System.currentTimeMillis());
            boolean down=x.getBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN,false);

            e.putBoolean("active",true)
                .putBoolean("chrono",true)
                .putLong("base",base)
                .putBoolean("countdown",down)
                .apply();
        } else {
            String text=findTime(n);

            e.putBoolean("active",true)
                .putBoolean("chrono",false)
                .putString("text",text)
                .apply();
        }

        ClockWidgetProvider.updateAll(this);
    }

    private boolean looksLikeTimer(Notification n) {
        Bundle x=n.extras;

        if(x.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER,false)) return true;

        String all=(str(x.getCharSequence(Notification.EXTRA_TITLE))+" "
            +str(x.getCharSequence(Notification.EXTRA_TEXT))+" "
            +str(x.getCharSequence(Notification.EXTRA_BIG_TEXT))).toLowerCase();

        return all.contains("minuteur")
            || all.contains("timer")
            || all.contains("chronom")
            || TIME.matcher(all).find();
    }

    private String findTime(Notification n) {
        Bundle x=n.extras;
        String all=str(x.getCharSequence(Notification.EXTRA_TEXT))+" "
            +str(x.getCharSequence(Notification.EXTRA_BIG_TEXT))+" "
            +str(x.getCharSequence(Notification.EXTRA_TITLE));

        Matcher m=TIME.matcher(all);
        return m.find()?m.group():"EN COURS";
    }

    private String str(CharSequence s){
        return s==null?"":s.toString();
    }
}
