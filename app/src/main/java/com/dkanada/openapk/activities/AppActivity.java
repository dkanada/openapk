package com.dkanada.openapk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.utils.ActionUtils;
import com.dkanada.openapk.async.ClearDataAsync;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.async.RemoveCacheAsync;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.InterfaceUtils;
import com.dkanada.openapk.views.ButtonSwitchView;
import com.dkanada.openapk.views.ButtonView;
import com.dkanada.openapk.views.InformationView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppActivity extends ThemeActivity {
    private int UNINSTALL_REQUEST_CODE = 1;

    private AppPreferences appPreferences;
    private Context context;
    private MenuItem favorite;
    private PackageInfo packageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        context = this;

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();
    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(InterfaceUtils.dark(appPreferences.getPrimaryColor(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColor());
            if (appPreferences.getNavigationColor()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColor());
            }
        }
    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        ImageView icon = (ImageView) findViewById(R.id.app_icon);
        TextView name = (TextView) findViewById(R.id.app_name);

        header.setBackgroundColor(appPreferences.getPrimaryColor());
        icon.setImageDrawable(App.getPackageIcon(packageInfo));
        name.setText(App.getPackageName(packageInfo));

        ImageView open = (ImageView) findViewById(R.id.open);
        ImageView extract = (ImageView) findViewById(R.id.extract);
        ImageView uninstall = (ImageView) findViewById(R.id.uninstall);
        ImageView share = (ImageView) findViewById(R.id.share);
        ImageView settings = (ImageView) findViewById(R.id.settings);

        if (App.getAppPreferences().getTheme().equals("1")) {
            open.setColorFilter(getResources().getColor(R.color.grey));
            extract.setColorFilter(getResources().getColor(R.color.grey));
            uninstall.setColorFilter(getResources().getColor(R.color.grey));
            share.setColorFilter(getResources().getColor(R.color.grey));
            settings.setColorFilter(getResources().getColor(R.color.grey));
        }

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.open(context, packageInfo);
            }
        });
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.extract(context, packageInfo);
            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    ActionUtils.uninstall(context, packageInfo);
                } else {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + packageInfo.packageName));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.share(context, packageInfo);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.settings(context, packageInfo);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
        LinearLayout information = (LinearLayout) findViewById(R.id.information);
        InformationView packageInformation = new InformationView(context, getString(R.string.package_layout), packageInfo.packageName, true);
        InformationView versionInformation = new InformationView(context, getString(R.string.version_layout), packageInfo.versionName, false);
        InformationView sizeInformation = new InformationView(context, getString(R.string.size_layout), getString(R.string.development_layout), true);
        InformationView dataFolderInformation = new InformationView(context, getString(R.string.data_layout), packageInfo.applicationInfo.dataDir, false);
        InformationView sourceFolderInformation = new InformationView(context, getString(R.string.source_layout), packageInfo.applicationInfo.sourceDir, true);
        InformationView installInformation = new InformationView(context, getString(R.string.install_layout), formatter.format(packageInfo.firstInstallTime), false);
        InformationView updateInformation = new InformationView(context, getString(R.string.update_layout), formatter.format(packageInfo.lastUpdateTime),  true);
        information.addView(packageInformation);
        information.addView(versionInformation);
        information.addView(sizeInformation);
        information.addView(dataFolderInformation);
        information.addView(sourceFolderInformation);
        information.addView(installInformation);
        information.addView(updateInformation);

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons);
        Switch hideSwitch = new Switch(context);
        hideSwitch.setClickable(false);
        hideSwitch.setAlpha(0.5f);
        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActionUtils.hide(context, packageInfo);
            }
        });
        Switch disableSwitch = new Switch(context);
        if (!packageInfo.applicationInfo.enabled) {
            disableSwitch.setChecked(true);
        }
        disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActionUtils.disable(context, packageInfo);
            }
        });
        Switch systemSwitch = new Switch(context);
        systemSwitch.setClickable(false);
        systemSwitch.setAlpha(0.5f);
        systemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
        ButtonSwitchView hide = new ButtonSwitchView(context, getResources().getString(R.string.action_hide), null, hideSwitch);
        ButtonSwitchView disable = new ButtonSwitchView(context, getResources().getString(R.string.action_disable), null, disableSwitch);
        ButtonSwitchView system = new ButtonSwitchView(context, getResources().getString(R.string.action_system), null, systemSwitch);
        buttons.addView(hide);
        buttons.addView(disable);
        buttons.addView(system);

        ButtonView removeCache = new ButtonView(context, getString(R.string.action_remove_cache), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_cache_progress)
                        , getResources().getString(R.string.dialog_cache_progress_description));
                new RemoveCacheAsync(context, dialog, packageInfo).execute();
            }
        });
        ButtonView clearData = new ButtonView(context, getString(R.string.action_clear_data), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_clear_data_progress)
                        , getResources().getString(R.string.dialog_clear_data_progress_description));
                new ClearDataAsync(context, dialog, packageInfo).execute();
            }
        });
        buttons.addView(removeCache);
        buttons.addView(clearData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("App", packageInfo.packageName + "OK");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("App", packageInfo.packageName + "CANCEL");
            }
        }
    }

    private void getInitialConfiguration() {
        String packageName = getIntent().getStringExtra("package");
        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favorite = menu.findItem(R.id.action_favorite);
        // TODO
        InterfaceUtils.updateAppFavoriteIcon(context, favorite, false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                ActionUtils.favorite(context, packageInfo);
                // TODO
                InterfaceUtils.updateAppFavoriteIcon(context, favorite, false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
