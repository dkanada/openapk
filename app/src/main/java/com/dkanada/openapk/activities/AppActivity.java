package com.dkanada.openapk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.InterfaceUtils;

import java.text.SimpleDateFormat;

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

        RelativeLayout open = (RelativeLayout) findViewById(R.id.open);
        RelativeLayout extract = (RelativeLayout) findViewById(R.id.extract);
        RelativeLayout uninstall = (RelativeLayout) findViewById(R.id.uninstall);
        RelativeLayout hide = (RelativeLayout) findViewById(R.id.hide);
        RelativeLayout disable = (RelativeLayout) findViewById(R.id.disable);
        RelativeLayout share = (RelativeLayout) findViewById(R.id.share);

        RelativeLayout information = (RelativeLayout) findViewById(R.id.information_layout);
        TextView apkText = (TextView) findViewById(R.id.app_apk_text);
        TextView versionText = (TextView) findViewById(R.id.app_version_text);
        TextView sizeText = (TextView) findViewById(R.id.app_size_text);
        TextView cacheSizeText = (TextView) findViewById(R.id.app_cache_size_text);
        TextView dataFolderText = (TextView) findViewById(R.id.app_data_folder_text);
        TextView installText = (TextView) findViewById(R.id.app_install_text);
        TextView updateText = (TextView) findViewById(R.id.app_update_text);

        CardView cache = (CardView) findViewById(R.id.remove_cache);
        CardView data = (CardView) findViewById(R.id.clear_data);

        icon.setImageDrawable(appInfo.getIcon());
        name.setText(appInfo.getName());

        apkText.setText(appInfo.getAPK());
        versionText.setText(appInfo.getVersion());
        sizeText.setText(R.string.development_layout);
        cacheSizeText.setText(R.string.development_layout);
        dataFolderText.setText(appInfo.getData());
        PackageManager packageManager = getPackageManager();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        try {
            installText.setText(formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).firstInstallTime));
            updateText.setText(formatter.format(packageManager.getPackageInfo(appInfo.getAPK(), 0).lastUpdateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // update colors
        header.setBackgroundColor(appPreferences.getPrimaryColor());
        if (appPreferences.getTheme().equals("1")) {
            for (int i = 0; i < information.getChildCount(); i += 2) {
                information.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.grey_light));
            }

            ImageView openIcon = (ImageView) findViewById(R.id.open_icon);
            ImageView extractIcon = (ImageView) findViewById(R.id.extract_icon);
            ImageView uninstallIcon = (ImageView) findViewById(R.id.uninstall_icon);
            ImageView hideIcon = (ImageView) findViewById(R.id.hide_icon);
            ImageView disableIcon = (ImageView) findViewById(R.id.disable_icon);
            ImageView shareIcon = (ImageView) findViewById(R.id.share_icon);

            openIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
            extractIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
            uninstallIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
            hideIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
            disableIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
            shareIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
        } else {
            for (int i = 0; i < information.getChildCount(); i += 2) {
                information.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.grey_dark));
            }
        }

        updateOpenButton(open);
        updateExtractButton(extract);
        updateUninstallButton(uninstall);
        updateHideButton(hide);
        updateDisableButton(disable);
        updateShareButton(share);
        updateCacheButton(cache);
        updateDataButton(data);
    }

    protected void updateOpenButton(RelativeLayout open) {
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.open(context, appInfo);
            }
        });
    }

    protected void updateExtractButton(RelativeLayout extract) {
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.extract(context, appInfo);
            }
        });
    }

    protected void updateUninstallButton(RelativeLayout uninstall) {
        if (appInfo.getSystem()) {
            uninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDialog.Builder materialBuilder = DialogUtils.uninstallSystemApp(context)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    ActionUtils.uninstall(context, appInfo);
                                }
                            });
                    materialBuilder.show();
                }
            });
        } else {
            uninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + appInfo.getAPK()));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                }
            });
        }
    }

    protected void updateCacheButton(CardView removeCache) {
        removeCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_cache_progress)
                        , getResources().getString(R.string.dialog_cache_progress_description));
                new RemoveCacheAsync(context, dialog, appInfo).execute();
            }
        });
    }

    protected void updateDataButton(CardView clearData) {
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                        , getResources().getString(R.string.dialog_clear_data_progress)
                        , getResources().getString(R.string.dialog_clear_data_progress_description));
                new ClearDataAsync(context, dialog, appInfo).execute();
            }
        });
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

    protected void updateShareButton(RelativeLayout fab_share) {
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.share(context, appInfo);
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
                if (appDbUtils.checkAppInfo(appInfo, 2)) {
                    appInfo.setFavorite(false);
                    appDbUtils.updateAppInfo(appInfo, 2);
                } else {
                    appInfo.setFavorite(true);
                    appDbUtils.updateAppInfo(appInfo, 2);
                }
                InterfaceUtils.updateAppFavoriteIcon(context, favorite, appDbUtils.checkAppInfo(appInfo, 2));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}