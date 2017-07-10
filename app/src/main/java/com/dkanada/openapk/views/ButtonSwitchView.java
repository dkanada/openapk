package com.dkanada.openapk.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dkanada.openapk.R;

public class ButtonSwitchView extends RelativeLayout {
    public ButtonSwitchView(Context context, String title, String summary, Switch mSwitch) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_button_switch, null);
        addView(view);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);

        container.addView(mSwitch);
        if (summary == null) {
            summaryView.setVisibility(GONE);
        } else {
            summaryView.setText(summary);
        }
        titleView.setText(title);
    }
}
