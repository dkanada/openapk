package com.dkanada.openapk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
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
import com.dkanada.openapk.utils.AppDbUtils;
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
    private AppPreferences appPreferences;
    private AppDbUtils appDbUtils;
    private Context context;
    private MenuItem favorite;
    private AppInfo appInfo;
    private int UNINSTALL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        context = this;

        appDbUtils = new AppDbUtils(context);

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
            getWindow().setStatusBarColor(InterfaceUtils.darker(appPreferences.getPrimaryColor(), 0.8));
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
        icon.setImageDrawable(appInfo.getIcon());
        name.setText(appInfo.getName());

        ImageView open = (ImageView) findViewById(R.id.open);
        ImageView extract = (ImageView) findViewById(R.id.extract);
        ImageView uninstall = (ImageView) findViewById(R.id.uninstall);
        ImageView share = (ImageView) findViewById(R.id.share);
        ImageView settings = (ImageView) findViewById(R.id.settings);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.open(context, appInfo);
            }
        });
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.extract(context, appInfo);
            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appInfo.getSystem()) {
                    ActionUtils.uninstall(context, appInfo);
                } else {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + appInfo.getAPK()));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.share(context, appInfo);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.settings(context, appInfo);
            }
        });

        LinearLayout information = (LinearLayout) findViewById(R.id.information);
        InformationView packageInformation = new InformationView(context, getString(R.string.package_layout), appInfo.getAPK(), getResources().getColor(R.color.grey));
        InformationView versionInformation = new InformationView(context, getString(R.string.version_layout), appInfo.getVersion(), getResources().getColor(R.color.grey_dark));
        InformationView appSizeInformation = new InformationView(context, getString(R.string.size_layout), getString(R.string.development_layout), getResources().getColor(R.color.grey));
        InformationView cacheSizeInformation = new InformationView(context, getString(R.string.cache_size_layout), getString(R.string.development_layout), getResources().getColor(R.color.grey_dark));
        InformationView dataFolderInformation = new InformationView(context, getString(R.string.data_layout), appInfo.getData(), getResources().getColor(R.color.grey));
        InformationView sourceFolderInformation = new InformationView(context, getString(R.string.source_layout), appInfo.getSource(), getResources().getColor(R.color.grey_dark));
        information.addView(packageInformation);
        information.addView(versionInformation);
        information.addView(appSizeInformation);
        information.addView(cacheSizeInformation);
        information.addView(dataFolderInformation);
        information.addView(sourceFolderInformation);

        PackageManager packageManager = getPackageManager();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
        try {
            InformationView iInstall = new InformationView(context, getString(R.string.install_layout), formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).firstInstallTime), getResources().getColor(R.color.grey));
            InformationView iUpdate = new InformationView(context, getString(R.string.update_layout), formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).lastUpdateTime), getResources().getColor(R.color.grey_dark));
            information.addView(iInstall);
            information.addView(iUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons);
        Switch hideSwitch = new Switch(context);
        if (appInfo.getHidden()) {
            hideSwitch.setChecked(true);
        }
        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActionUtils.hide(context, appInfo);
            }
        });
        Switch disableSwitch = new Switch(context);
        if (appInfo.getDisabled()) {
            disableSwitch.setChecked(true);
        }
        disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActionUtils.disable(context, appInfo);
            }
        });
        Switch systemSwitch = new Switch(context);
        if (appInfo.getSystem()) {
            systemSwitch.setChecked(true);
        }
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
                new RemoveCacheAsync(context, dialog, appInfo).execute();
            }
        });
        ButtonView clearData = new ButtonView(context, getString(R.string.action_clear_data), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_clear_data_progress)
                        , getResources().getString(R.string.dialog_clear_data_progress_description));
                new ClearDataAsync(context, dialog, appInfo).execute();
            }
        });

        buttons.addView(removeCache);
        buttons.addView(clearData);
    }

    protected void updateHideButton(final RelativeLayout hide) {
        InterfaceUtils.updateAppHiddenIcon(context, hide, appDbUtils.checkAppInfo(appInfo, 3));
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.hide(context, appInfo);
                InterfaceUtils.updateAppHiddenIcon(context, hide, appDbUtils.checkAppInfo(appInfo, 3));
            }
        });
    }

    protected void updateDisableButton(final RelativeLayout disable) {
        InterfaceUtils.updateAppDisabledIcon(context, disable, appDbUtils.checkAppInfo(appInfo, 4));
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.disable(context, appInfo);
                InterfaceUtils.updateAppDisabledIcon(context, disable, appDbUtils.checkAppInfo(appInfo, 4));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("App", appInfo.getAPK() + "OK");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("App", appInfo.getAPK() + "CANCEL");
            }
        }
    }

    private void getInitialConfiguration() {
        String appName = getIntent().getStringExtra("app_name");
        String appApk = getIntent().getStringExtra("app_apk");
        String appVersion = getIntent().getStringExtra("app_version");
        String appSource = getIntent().getStringExtra("app_source");
        String appData = getIntent().getStringExtra("app_data");
        Boolean appIsSystem = getIntent().getExtras().getBoolean("app_isSystem");
        Boolean appIsFavorite = getIntent().getExtras().getBoolean("app_isFavorite");
        Boolean appIsHidden = getIntent().getExtras().getBoolean("app_isHidden");
        Boolean appIsDisabled = getIntent().getExtras().getBoolean("app_isDisabled");

        Bitmap bitmap = getIntent().getParcelableExtra("app_icon");
        Drawable appIcon = new BitmapDrawable(getResources(), bitmap);
        appInfo = new AppInfo(appName, appApk, appVersion, appSource, appData, appIsSystem, appIsFavorite, appIsHidden, appIsDisabled, appIcon);
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
        InterfaceUtils.updateAppFavoriteIcon(context, favorite, appDbUtils.checkAppInfo(appInfo, 2));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                ActionUtils.favorite(context, appInfo);
                InterfaceUtils.updateAppFavoriteIcon(context, favorite, appDbUtils.checkAppInfo(appInfo, 2));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
