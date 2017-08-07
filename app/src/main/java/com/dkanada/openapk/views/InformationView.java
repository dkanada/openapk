package com.dkanada.openapk.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;

public class InformationView extends RelativeLayout {
    public InformationView(Context context, String title, String summary, boolean alternate) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_information, null);
        addView(view);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);
        titleView.setText(title);
        summaryView.setText(summary);

        if (App.getAppPreferences().getTheme().equals("0") && alternate) {
            setBackgroundColor(getResources().getColor(R.color.grey_light));
        } else if (alternate) {
            setBackgroundColor(getResources().getColor(R.color.grey_dark));
        }
    }
}
