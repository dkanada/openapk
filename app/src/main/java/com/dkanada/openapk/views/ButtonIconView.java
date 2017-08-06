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

public class ButtonIconView extends RelativeLayout {
    public ButtonIconView(Context context, Drawable icon, String label, OnClickListener onClickListener) {
        super(context);
        this.setOnClickListener(onClickListener);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_button_icon, null);
        addView(view);

        //TextView textView = (TextView) view.findViewById(R.id.text);
        //ImageView imageView = (ImageView) view.findViewById(R.id.image);

        //textView.setText(label);
        //imageView.setImageDrawable(icon);

        //if (App.getAppPreferences().getTheme().equals("1")) {
        //    imageView.setColorFilter(ContextCompat.getColor(context, R.color.grey));
        //}
    }
}
