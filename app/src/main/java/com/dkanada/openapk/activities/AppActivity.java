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
import android.support.v7.app.AppCompatActivity;
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
import com.dkanada.openapk.AppDatabase;
import com.gc.materialdesign.views.Card;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.dkanada.openapk.AppInfo;
import com.dkanada.openapk.OpenAPKApplication;
import com.dkanada.openapk.R;
import com.dkanada.openapk.async.ClearDataInBackground;
import com.dkanada.openapk.async.ExtractFileInBackground;
import com.dkanada.openapk.async.UninstallInBackground;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.UtilsRoot;
import com.dkanada.openapk.utils.UtilsApp;
import com.dkanada.openapk.utils.UtilsDialog;
import com.dkanada.openapk.utils.UtilsUI;

import java.util.Set;

public class AppActivity extends AppCompatActivity {
  // load settings
  private AppPreferences appPreferences;
  private AppDatabase appDatabase;

  // general variables
  private AppInfo appInfo;
  private Set<String> appsFavorite;
  private Set<String> appsHidden;
  private Set<String> appsDisabled;

  // configuration variables
  private int UNINSTALL_REQUEST_CODE = 1;
  private Context context;
  private Activity activity;
  private MenuItem favorite;

  // other variables
  private FloatingActionsMenu fab;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    this.appPreferences = OpenAPKApplication.getAppPreferences();
    if (appPreferences.getTheme().equals("1")) {
      setTheme(R.style.Light);
    } else {
      setTheme(R.style.Dark);
    }
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_app);
    this.context = this;
    this.activity = (Activity) context;

    appDatabase = new AppDatabase(context);

    getInitialConfiguration();
    setInitialConfiguration();
    setScreenElements();
  }

  private void setInitialConfiguration() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("");
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
      toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
      if (appPreferences.getNavigationColorPref()) {
        getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
      }
    }
  }

  private void setScreenElements() {
    TextView header = (TextView) findViewById(R.id.header);
    ImageView icon = (ImageView) findViewById(R.id.app_icon);
    ImageView icon_googleplay = (ImageView) findViewById(R.id.app_googleplay);
    TextView name = (TextView) findViewById(R.id.app_name);
    TextView version = (TextView) findViewById(R.id.app_version);
    TextView apk = (TextView) findViewById(R.id.app_apk);
    CardView googleplay = (CardView) findViewById(R.id.id_card);
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
    fab_share.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));
    fab_hide.setColorNormal(appPreferences.getFABColorPref());
    fab_hide.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));
    fab_disable.setColorNormal(appPreferences.getFABColorPref());
    fab_disable.setColorPressed(UtilsUI.darker(appPreferences.getFABColorPref(), 0.8));

    updateOpenButton(open);
    updateExtractButton(extract);
    updateUninstallButton(uninstall);
    updateCacheButton(cache);
    updateDataButton(data);
    updateShareFAB(fab_share);
    updateHideFAB(fab_hide);
    updateDisableFAB(fab_disable);

    // google play icon
    if (appInfo.getSystem()) {
      icon_googleplay.setVisibility(View.GONE);
    } else {
      googleplay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          UtilsApp.goToGooglePlay(context, appInfo.getAPK());
        }
      });

      googleplay.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
          ClipData clipData;
          ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
          clipData = ClipData.newPlainText("text", appInfo.getAPK());
          clipboardManager.setPrimaryClip(clipData);
          UtilsDialog.showSnackBar(activity, context.getResources().getString(R.string.copied_clipboard), null, null, 2).show();
          return false;
        }
      });
    }
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
        MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(context
            , String.format(getResources().getString(R.string.dialog_saving), appInfo.getName())
            , getResources().getString(R.string.dialog_saving_description));
        new ExtractFileInBackground(context, dialog, appInfo).execute();
      }
    });
  }

  protected void updateUninstallButton(CardView uninstall) {
    if (appInfo.getSystem() && UtilsRoot.isRooted()) {
      uninstall.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog.Builder materialBuilder = UtilsDialog.showUninstall(context)
              .callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                  MaterialDialog dialogUninstalling = UtilsDialog.showTitleContentWithProgress(context
                      , String.format(getResources().getString(R.string.dialog_uninstalling), appInfo.getName())
                      , getResources().getString(R.string.dialog_uninstalling_description));
                  new UninstallInBackground(context, dialogUninstalling, appInfo).execute();
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
    if (UtilsRoot.isRooted()) {
      cache.setVisibility(View.VISIBLE);
      cache.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(context
              , getResources().getString(R.string.dialog_cache_deleting)
              , getResources().getString(R.string.dialog_cache_deleting_description));
          new ClearDataInBackground(context, dialog, appInfo).execute();
        }
      });
    }
  }

  protected void updateDataButton(CardView data) {
    if (UtilsRoot.isRooted()) {
      data.setVisibility(View.VISIBLE);
      data.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MaterialDialog dialog = UtilsDialog.showTitleContentWithProgress(context
              , getResources().getString(R.string.dialog_clear_data_deleting)
              , getResources().getString(R.string.dialog_clear_data_deleting_description));
          new ClearDataInBackground(context, dialog, appInfo).execute();
        }
      });
    }
  }

  protected void updateShareFAB(FloatingActionButton fab_share) {
    fab_share.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        UtilsApp.extractFile(appInfo);
        Intent shareIntent = UtilsApp.getShareIntent(UtilsApp.getOutputFilename(appInfo));
        startActivity(Intent.createChooser(shareIntent, String.format(getResources().getString(R.string.send_to), appInfo.getName())));
      }
    });
  }

  protected void updateHideFAB(final FloatingActionButton fab_hide) {
    if (UtilsRoot.isRooted()) {
      UtilsUI.updateAppHiddenIcon(context, fab_hide, UtilsApp.isAppHidden(appInfo, appsHidden));
      fab_hide.setVisibility(View.VISIBLE);
      fab_hide.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (UtilsApp.isAppHidden(appInfo, appsHidden)) {
            Boolean hidden = UtilsRoot.hideWithRootPermission(appInfo.getAPK(), true);
            if (hidden) {
              UtilsApp.removeIconFromCache(context, appInfo);
              appsHidden.remove(appInfo.toString());
              appPreferences.setHiddenApps(appsHidden);
              UtilsDialog.showSnackBar(activity, getResources().getString(R.string.dialog_reboot), getResources().getString(R.string.button_reboot), null, 3).show();
            }
          } else {
            UtilsApp.saveIconToCache(context, appInfo);
            Boolean hidden = UtilsRoot.hideWithRootPermission(appInfo.getAPK(), false);
            if (hidden) {
              appsHidden.add(appInfo.toString());
              appPreferences.setHiddenApps(appsHidden);
            }
          }
          UtilsUI.updateAppHiddenIcon(context, fab_hide, UtilsApp.isAppHidden(appInfo, appsHidden));
        }
      });
    }
  }

  protected void updateDisableFAB(final FloatingActionButton fab_disable) {
    if (UtilsRoot.isRooted()) {
      UtilsUI.updateAppDisabledIcon(context, fab_disable, appDatabase.checkAppInfo(appInfo, 4));
      fab_disable.setVisibility(View.VISIBLE);
      fab_disable.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (appDatabase.checkAppInfo(appInfo, 4)) {
            Boolean disabled = UtilsRoot.disableWithRootPermission(appInfo.getAPK(), true);
            if (disabled) {
              UtilsApp.removeIconFromCache(context, appInfo);
              appInfo.setDisabled(false);
              appDatabase.updateAppInfo(appInfo, 4);
              UtilsDialog.showSnackBar(activity, getResources().getString(R.string.dialog_reboot), getResources().getString(R.string.button_reboot), null, 3).show();
            }
          } else {
            UtilsApp.saveIconToCache(context, appInfo);
            Boolean disabled = UtilsRoot.disableWithRootPermission(appInfo.getAPK(), false);
            if (disabled) {
              appInfo.setDisabled(true);
              appDatabase.updateAppInfo(appInfo, 4);
            }
          }
          UtilsUI.updateAppDisabledIcon(context, fab_disable, appDatabase.checkAppInfo(appInfo, 4));
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

    appsFavorite = appPreferences.getFavoriteApps();
    appsHidden = appPreferences.getHiddenApps();
    appsDisabled = appPreferences.getDisabledApps();
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
    UtilsUI.updateAppFavoriteIcon(context, favorite, UtilsApp.isAppFavorite(appInfo.getAPK(), appsFavorite));
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.home:
        finish();
        return true;
      case R.id.action_favorite:
        if (UtilsApp.isAppFavorite(appInfo.getAPK(), appsFavorite)) {
          appsFavorite.remove(appInfo.getAPK());
          UtilsApp.removeIconFromCache(context, appInfo);
          appPreferences.setFavoriteApps(appsFavorite);
        } else {
          appsFavorite.add(appInfo.getAPK());
          UtilsApp.saveIconToCache(context, appInfo);
          appPreferences.setFavoriteApps(appsFavorite);
        }
        UtilsUI.updateAppFavoriteIcon(context, favorite, UtilsApp.isAppFavorite(appInfo.getAPK(), appsFavorite));
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
