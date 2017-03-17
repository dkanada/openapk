package com.dkanada.openapk.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.utils.AppDbUtils;
import com.dkanada.openapk.async.ClearDataAsync;
import com.dkanada.openapk.async.DisableAsync;
import com.dkanada.openapk.async.HideAsync;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.async.RemoveCacheAsync;
import com.dkanada.openapk.async.ExtractFileAsync;
import com.dkanada.openapk.async.UninstallAsync;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.RootUtils;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.InterfaceUtils;

public class AppActivity extends ThemeActivity {
  private AppPreferences appPreferences;
  private AppDbUtils appDbUtils;
  private Context context;
  private Activity activity;
  private MenuItem favorite;
  private AppInfo appInfo;
  private FloatingActionsMenu fab;
  private int UNINSTALL_REQUEST_CODE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    appPreferences = App.getAppPreferences();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_app);
    context = this;
    this.activity = (Activity) context;

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
      getWindow().setStatusBarColor(InterfaceUtils.darker(appPreferences.getPrimaryColorPref(), 0.8));
      toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
      if (appPreferences.getNavigationColorPref()) {
        getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
      }
    }
  }

  private void setScreenElements() {
    TextView header = (TextView) findViewById(R.id.header);
    ImageView icon = (ImageView) findViewById(R.id.app_icon);
    TextView name = (TextView) findViewById(R.id.app_name);
    TextView version = (TextView) findViewById(R.id.app_version);
    TextView apk = (TextView) findViewById(R.id.app_apk);
    CardView open = (CardView) findViewById(R.id.start_card);
    CardView extract = (CardView) findViewById(R.id.extract_card);
    CardView uninstall = (CardView) findViewById(R.id.uninstall_card);
    CardView cache = (CardView) findViewById(R.id.cache_card);
    CardView data = (CardView) findViewById(R.id.clear_data_card);
    fab = (FloatingActionsMenu) findViewById(R.id.fab);
    FloatingActionButton fab_share = (FloatingActionButton) findViewById(R.id.fab_a);
    final FloatingActionButton fab_hide = (FloatingActionButton) findViewById(R.id.fab_b);
    final FloatingActionButton fab_disable = (FloatingActionButton) findViewById(R.id.fab_c);

    icon.setImageDrawable(appInfo.getIcon());
    name.setText(appInfo.getName());
    apk.setText(appInfo.getAPK());
    version.setText(appInfo.getVersion());

    // configure colors
    header.setBackgroundColor(appPreferences.getPrimaryColorPref());
    fab_share.setColorNormal(appPreferences.getFABColorPref());
    fab_share.setColorPressed(InterfaceUtils.darker(appPreferences.getFABColorPref(), 0.8));
    fab_hide.setColorNormal(appPreferences.getFABColorPref());
    fab_hide.setColorPressed(InterfaceUtils.darker(appPreferences.getFABColorPref(), 0.8));
    fab_disable.setColorNormal(appPreferences.getFABColorPref());
    fab_disable.setColorPressed(InterfaceUtils.darker(appPreferences.getFABColorPref(), 0.8));

    updateOpenButton(open);
    updateExtractButton(extract);
    updateUninstallButton(uninstall);
    updateCacheButton(cache);
    updateDataButton(data);
    updateShareFAB(fab_share);
    updateHideFAB(fab_hide);
    updateDisableFAB(fab_disable);
  }

  protected void updateOpenButton(CardView open) {
    final Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.getAPK());
    if (intent != null) {
      open.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          startActivity(intent);
        }
      });
    } else {
      open.setVisibility(View.GONE);
    }
  }

  protected void updateExtractButton(CardView extract) {
    extract.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
            , String.format(getResources().getString(R.string.dialog_saving), appInfo.getName())
            , getResources().getString(R.string.dialog_saving_description));
        new ExtractFileAsync(context, dialog, appInfo).execute();
      }
    });
  }

  protected void updateUninstallButton(CardView uninstall) {
    if (appInfo.getSystem() && RootUtils.isRooted()) {
      uninstall.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog.Builder materialBuilder = DialogUtils.showUninstall(context)
              .callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                  MaterialDialog dialogUninstalling = DialogUtils.showTitleContentWithProgress(context
                      , String.format(getResources().getString(R.string.dialog_uninstalling), appInfo.getName())
                      , getResources().getString(R.string.dialog_uninstalling_description));
                  new UninstallAsync(context, dialogUninstalling, appInfo).execute();
                  dialog.dismiss();
                }
              });
          materialBuilder.show();
        }
      });
    } else if(appInfo.getSystem()) {
      uninstall.setVisibility(View.GONE);
      uninstall.setForeground(null);
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

  protected void updateCacheButton(CardView cache) {
    if (RootUtils.isRooted()) {
      cache.setVisibility(View.VISIBLE);
      cache.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
              , getResources().getString(R.string.dialog_cache_deleting)
              , getResources().getString(R.string.dialog_cache_deleting_description));
          new RemoveCacheAsync(context, dialog, appInfo).execute();
        }
      });
    }
  }

  protected void updateDataButton(CardView data) {
    if (RootUtils.isRooted()) {
      data.setVisibility(View.VISIBLE);
      data.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
              , getResources().getString(R.string.dialog_clear_data_deleting)
              , getResources().getString(R.string.dialog_clear_data_deleting_description));
          new ClearDataAsync(context, dialog, appInfo).execute();
        }
      });
    }
  }

  protected void updateShareFAB(FloatingActionButton fab_share) {
    fab_share.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AppUtils.extractFile(appInfo);
        Intent shareIntent = AppUtils.getShareIntent(AppUtils.getOutputFilename(appInfo));
        startActivity(Intent.createChooser(shareIntent, String.format(getResources().getString(R.string.send_to), appInfo.getName())));
      }
    });
  }

  protected void updateHideFAB(final FloatingActionButton fab_hide) {
    InterfaceUtils.updateAppHiddenIcon(context, fab_hide, appDbUtils.checkAppInfo(appInfo, 3));
    if (RootUtils.isRooted()) {
      fab_hide.setVisibility(View.VISIBLE);
      fab_hide.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
              , getResources().getString(R.string.dialog_hide_progress)
              , getResources().getString(R.string.dialog_hide_progress_description));
          new HideAsync(context, dialog, appInfo).execute();
        }
      });
    }
  }

  protected void updateDisableFAB(final FloatingActionButton fab_disable) {
    InterfaceUtils.updateAppDisabledIcon(context, fab_disable, appDbUtils.checkAppInfo(appInfo, 4));
    if (RootUtils.isRooted()) {
      fab_disable.setVisibility(View.VISIBLE);
      fab_disable.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
              , getResources().getString(R.string.dialog_disable_progress_deleting)
              , getResources().getString(R.string.dialog_disable_progress_description));
          new DisableAsync(context, dialog, appInfo).execute();
        }
      });
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == UNINSTALL_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Log.i("App", "OK");
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
      } else if (resultCode == RESULT_CANCELED) {
        Log.i("App", "CANCEL");
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
    if (fab.isExpanded()) {
      fab.collapse();
    } else {
      super.onBackPressed();
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