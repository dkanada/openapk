package com.dkanada.openapk.activities;

import android.os.Bundle;

import com.dkanada.openapk.R;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

public class LicenseActivity extends LibsActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    setIntent(new LibsBuilder()
        .withActivityTitle(getResources().getString(R.string.settings_license))
        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
        .withAutoDetect(true)
        .intent(this));
    super.onCreate(savedInstanceState);
  }
}
