package com.dkanada.openapk.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;

public class ButtonView extends RelativeLayout {
    public ButtonView(Context context, String title, String summary, OnClickListener onClickListener) {
        super(context);
        this.setOnClickListener(onClickListener);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_button_normal, null);
        addView(view);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);

        if (summary == null) {
            summaryView.setVisibility(GONE);
        } else {
            summaryView.setText(summary);
        }

        titleView.setText(title);
    }
}
