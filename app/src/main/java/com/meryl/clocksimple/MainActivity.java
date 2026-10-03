package com.meryl.clocksimple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);

        if ("OPEN_SAMSUNG_CLOCK".equals(getIntent().getAction())) {
            ClockWidgetProvider.openSamsungClock(this);
            finish();
            return;
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);

        int p=(int)(24*getResources().getDisplayMetrics().density);
        root.setPadding(p,p,p,p);

        TextView title=new TextView(this);
        title.setText("Meryl Horloge Simple");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        root.addView(title,new LinearLayout.LayoutParams(-1,-2));

        TextView info=new TextView(this);
        info.setText("Ajoute le widget 1×1 sur l’écran d’accueil.\n\nPour afficher automatiquement un minuteur ou chronomètre Samsung en cours, autorise l’accès aux notifications une seule fois.");
        info.setTextSize(16);
        info.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams ilp=new LinearLayout.LayoutParams(-1,-2);
        ilp.setMargins(0,p,0,p);
        root.addView(info,ilp);

        Button access=new Button(this);
        access.setText("Autoriser l’accès aux notifications");
        access.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        root.addView(access,new LinearLayout.LayoutParams(-1,-2));

        Button clock=new Button(this);
        clock.setText("Ouvrir Horloge Samsung");
        clock.setOnClickListener(v -> ClockWidgetProvider.openSamsungClock(this));
        LinearLayout.LayoutParams clp=new LinearLayout.LayoutParams(-1,-2);
        clp.setMargins(0,16,0,0);
        root.addView(clock,clp);

        setContentView(root);
    }
}
