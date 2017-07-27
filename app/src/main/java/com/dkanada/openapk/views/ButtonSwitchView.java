package com.dkanada.openapk.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;

public class ButtonSwitchView extends ConstraintLayout {
    public ButtonSwitchView(Context context, String title, String summary, View object) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_button_switch, null);
        addView(view);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.addView(object);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);
        titleView.setText(title);
        if (summary == null) {
            summaryView.setVisibility(GONE);
        } else {
            summaryView.setText(summary);
        }

        View divider = findViewById(R.id.divider);
        if (App.getAppPreferences().getTheme().equals("1")) {
            divider.setBackgroundColor(getResources().getColor(R.color.grey_light));
        } else {
            divider.setBackgroundColor(getResources().getColor(R.color.grey_dark));
        }
    }
}
