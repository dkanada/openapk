package com.dkanada.openapk.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.async.DeleteFileAsync;
import com.dkanada.openapk.models.AppItem;
import com.dkanada.openapk.utils.Actions;
import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.OtherUtils;
import com.dkanada.openapk.views.ButtonSwitchView;
import com.dkanada.openapk.views.ButtonView;
import com.dkanada.openapk.views.InformationView;

import java.io.File;

public class AppActivity extends ThemeActivity {
    private Context context;
    private AppItem appItem;
    private MenuItem favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        appItem = getIntent().getParcelableExtra("appItem");
        context = this;

        setInitialConfiguration();
        setScreenElements();
    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setBackgroundColor(AppPreferences.get(context).getPrimaryColor());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(OtherUtils.dark(AppPreferences.get(context).getPrimaryColor(), 0.8));
        getWindow().setNavigationBarColor(AppPreferences.get(context).getPrimaryColor());
    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        ImageView icon = (ImageView) findViewById(R.id.app_icon);
        TextView name = (TextView) findViewById(R.id.app_name);

        header.setBackgroundColor(AppPreferences.get(context).getPrimaryColor());
        icon.setImageDrawable(new BitmapDrawable(getResources(), appItem.getIcon()));
        name.setText(appItem.getPackageLabel());

        ImageView open = (ImageView) findViewById(R.id.open);
        ImageView extract = (ImageView) findViewById(R.id.extract);
        ImageView uninstall = (ImageView) findViewById(R.id.uninstall);
        ImageView share = (ImageView) findViewById(R.id.share);
        ImageView settings = (ImageView) findViewById(R.id.settings);

        if (App.getAppPreferences().getTheme().equals("0")) {
            open.setColorFilter(getResources().getColor(R.color.grey_two));
            extract.setColorFilter(getResources().getColor(R.color.grey_two));
            uninstall.setColorFilter(getResources().getColor(R.color.grey_two));
            share.setColorFilter(getResources().getColor(R.color.grey_two));
            settings.setColorFilter(getResources().getColor(R.color.grey_two));
        }

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actions.open(context, appItem);
            }
        });
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actions.extract(context, appItem);
            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appItem.system) {
                    Actions.uninstall(context, appItem);
                } else {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + appItem.getPackageName()));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, AppPreferences.CODE_UNINSTALL);
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actions.share(context, appItem);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actions.settings(context, appItem);
            }
        });

        LinearLayout information = (LinearLayout) findViewById(R.id.information);
        InformationView packageInformation = new InformationView(context, getString(R.string.layout_package), appItem.getPackageName(), true);
        InformationView versionNameInformation = new InformationView(context, getString(R.string.layout_version_name), appItem.getVersionName(), false);
        InformationView versionCodeInformation = new InformationView(context, getString(R.string.layout_version_code), appItem.getVersionCode(), true);
        InformationView dataFolderInformation = new InformationView(context, getString(R.string.layout_data), new File(appItem.getData()).getParent(), false);
        InformationView sourceFolderInformation = new InformationView(context, getString(R.string.layout_source), new File(new File(appItem.getSource()).getParent()).getParent(), true);
        InformationView installInformation = new InformationView(context, getString(R.string.layout_install), OtherUtils.formatDate(Long.valueOf(appItem.getInstall())), false);
        InformationView updateInformation = new InformationView(context, getString(R.string.layout_update), OtherUtils.formatDate(Long.valueOf(appItem.getUpdate())),  true);
        information.addView(packageInformation);
        information.addView(versionNameInformation);
        information.addView(versionCodeInformation);
        information.addView(dataFolderInformation);
        information.addView(sourceFolderInformation);
        information.addView(installInformation);
        information.addView(updateInformation);

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons);
        Switch hideSwitch = new Switch(context);
        hideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Actions.hide(context, appItem);
            }
        });
        Switch disableSwitch = new Switch(context);
        if (appItem.disable) {
            disableSwitch.setChecked(true);
        }
        disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Actions.disable(context, appItem);
            }
        });
        Switch systemSwitch = new Switch(context);
        ButtonSwitchView hide = new ButtonSwitchView(context, getResources().getString(R.string.action_hide), null, hideSwitch);
        ButtonSwitchView disable = new ButtonSwitchView(context, getResources().getString(R.string.action_disable), null, disableSwitch);
        ButtonSwitchView system = new ButtonSwitchView(context, getResources().getString(R.string.action_system), null, systemSwitch);
        buttons.addView(hide);
        buttons.addView(disable);
        buttons.addView(system);

        ButtonView storage = new ButtonView(context, getString(R.string.storage), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StorageActivity.class);
                intent.putExtra("appItem", appItem);
                context.startActivity(intent);
            }
        });
        ButtonView removeCache = new ButtonView(context, getString(R.string.action_remove_cache), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.dialogProgress(context
                        , getResources().getString(R.string.dialog_progress)
                        , getResources().getString(R.string.dialog_progress_description));
                new DeleteFileAsync(context, dialog, appItem.getData() + "/cache").execute();
            }
        });
        ButtonView removeData = new ButtonView(context, getString(R.string.action_remove_data), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = DialogUtils.dialogProgress(context
                        , getResources().getString(R.string.dialog_progress)
                        , getResources().getString(R.string.dialog_progress_description));
                new DeleteFileAsync(context, dialog, appItem.getData()).execute();
            }
        });
        buttons.addView(storage);
        buttons.addView(removeCache);
        buttons.addView(removeData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppPreferences.CODE_UNINSTALL) {
            if (resultCode == RESULT_OK) {
                Log.i("UNINSTALL: ", appItem.getPackageName() + " SUCCESS");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("UNINSTALL: ", appItem.getPackageName() + " FAILURE");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favorite = menu.findItem(R.id.action_favorite);
        //OtherUtils.updateAppFavoriteIcon(context, favorite, packageInfo);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                //Actions.favorite(packageInfo);
                //OtherUtils.updateAppFavoriteIcon(context, favorite, packageInfo);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
