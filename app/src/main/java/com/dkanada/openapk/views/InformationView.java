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

public class InformationView extends RelativeLayout {
    public InformationView(Context context, String title, String summary, OnClickListener onClickListener) {
        super(context);
        this.setOnClickListener(onClickListener);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_information, null);
        addView(view);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);

        titleView.setText(title);
        summaryView.setText(summary);
    }
}
