package com.dkanada.openapk.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.pm.ApplicationInfo;

import com.dkanada.openapk.AppInfo;
import com.dkanada.openapk.OpenAPKApplication;
import com.dkanada.openapk.R;
import com.dkanada.openapk.adapters.AppAdapter;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.UtilsApp;
import com.dkanada.openapk.utils.UtilsDialog;
import com.dkanada.openapk.utils.UtilsUI;
import com.mikepenz.materialdrawer.Drawer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
  private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

  // settings
  private AppPreferences appPreferences;

  // general variables
  private List<AppInfo> appList;
  private List<AppInfo> appSystemList;
  private List<AppInfo> appHiddenList;
  private List<AppInfo> appDisabledList;

  private AppAdapter appAdapter;
  private AppAdapter appSystemAdapter;
  private AppAdapter appFavoriteAdapter;
  private AppAdapter appHiddenAdapter;
  private AppAdapter appDisabledAdapter;

  // configuration variables
  private Boolean doubleBackToExitPressedOnce = false;
  private Toolbar toolbar;
  private Activity activity;
  private Context context;
  private RecyclerView recyclerView;
  private Drawer drawer;
  private MenuItem searchItem;
  private SearchView searchView;
  private static LinearLayout noResults;
  private SwipeRefreshLayout refresh;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.appPreferences = OpenAPKApplication.getAppPreferences();
    if (appPreferences.getTheme().equals("1")) {
      setTheme(R.style.Light);
    } else {
      setTheme(R.style.Dark);
    }
    setContentView(R.layout.activity_main);
    this.activity = this;
    this.context = this;

    setInitialConfiguration();
    UtilsApp.checkPermissions(activity);

    recyclerView = (RecyclerView) findViewById(R.id.appList);
    refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    noResults = (LinearLayout) findViewById(R.id.noResults);

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(linearLayoutManager);
    drawer = UtilsUI.setNavigationDrawer((Activity) context, context, toolbar, appAdapter, appSystemAdapter, appFavoriteAdapter, appHiddenAdapter, appDisabledAdapter, recyclerView);

    new getInstalledApps().execute();

    refresh.setColorSchemeColors(appPreferences.getPrimaryColorPref());
    refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refresh.setRefreshing(true);
        (new Handler()).postDelayed(new Runnable() {
          @Override
          public void run() {
            new getInstalledApps().execute();
            refresh.setRefreshing(false);
          }
        }, 2000);
      }
    });
  }

  private void setInitialConfiguration() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(R.string.app_name);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
      toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
      if (!appPreferences.getNavigationBlackPref()) {
        getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
      }
    }
  }

  class getInstalledApps extends AsyncTask<Void, String, Void> {
    public getInstalledApps() {
      appList = new ArrayList<>();
      appSystemList = new ArrayList<>();
      appHiddenList = new ArrayList<>();
      appDisabledList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
      final PackageManager packageManager = getPackageManager();
      List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
      Set<String> hiddenApps = appPreferences.getHiddenApps();
      Set<String> disabledApps = appPreferences.getDisabledApps();
      Set<String> installedApps = appPreferences.getInstalledApps();
      Set<String> systemApps = appPreferences.getSystemApps();

      // sort mode
      switch (appPreferences.getSortMode()) {
        default:
          // compare by name
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              return packageManager.getApplicationLabel(p1.applicationInfo).toString().toLowerCase().compareTo(packageManager.getApplicationLabel(p2.applicationInfo).toString().toLowerCase());
            }
          });
          break;
        case "2":
          // compare by size
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              Long size1 = new File(p1.applicationInfo.sourceDir).length();
              Long size2 = new File(p2.applicationInfo.sourceDir).length();
              return size2.compareTo(size1);
            }
          });
          break;
        case "3":
          // compare by installation date
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              return Long.toString(p2.firstInstallTime).compareTo(Long.toString(p1.firstInstallTime));
            }
          });
          break;
        case "4":
          // compare by last update
          Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
              return Long.toString(p2.lastUpdateTime).compareTo(Long.toString(p1.lastUpdateTime));
            }
          });
          break;
      }

      // list of hidden apps
      for (String app : hiddenApps) {
        AppInfo tempApp = new AppInfo(app);
        Drawable tempAppIcon = UtilsApp.getIconFromCache(context, tempApp);
        tempApp.setIcon(tempAppIcon);
        appHiddenList.add(tempApp);
      }

      // list of disabled apps
      for (String app : disabledApps) {
        AppInfo tempApp = new AppInfo(app);
        Drawable tempAppIcon = UtilsApp.getIconFromCache(context, tempApp);
        tempApp.setIcon(tempAppIcon);
        appDisabledList.add(tempApp);
      }

      // installed and system apps
      for (PackageInfo packageInfo : packages) {
        if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals(""))) {
          if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            try {
              // installed apps
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), false);
              appList.add(tempApp);
              installedApps.add(tempApp.toString());
            } catch (OutOfMemoryError e) {
              //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, getResources().getDrawable(R.drawable.ic_android), false);
              appList.add(tempApp);
              installedApps.add(tempApp.toString());
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
            try {
              // system apps
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), true);
              appSystemList.add(tempApp);
              systemApps.add(tempApp.toString());
            } catch (OutOfMemoryError e) {
              //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
              AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, getResources().getDrawable(R.drawable.ic_android), false);
              appSystemList.add(tempApp);
              systemApps.add(tempApp.toString());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      }

      appPreferences.setInstalledApps(installedApps);
      appPreferences.setSystemApps(systemApps);
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);

      appAdapter = new AppAdapter(appList, context);
      appSystemAdapter = new AppAdapter(appSystemList, context);
      appFavoriteAdapter = new AppAdapter(getFavoriteList(appList, appSystemList), context);
      appHiddenAdapter = new AppAdapter(appHiddenList, context);
      appDisabledAdapter = new AppAdapter(appDisabledList, context);

      recyclerView.swapAdapter(appAdapter, false);
      UtilsUI.setToolbarTitle(activity, getResources().getString(R.string.action_apps));

      searchItem.setVisible(true);
      drawer = UtilsUI.setNavigationDrawer((Activity) context, context, toolbar, appAdapter, appSystemAdapter, appFavoriteAdapter, appHiddenAdapter, appDisabledAdapter, recyclerView);
    }
  }

  private List<AppInfo> getFavoriteList(List<AppInfo> appList, List<AppInfo> appSystemList) {
    List<AppInfo> res = new ArrayList<>();

    for (AppInfo app : appList) {
      if (UtilsApp.isAppFavorite(app.getAPK(), appPreferences.getFavoriteApps())) {
        res.add(app);
      }
    }
    for (AppInfo app : appSystemList) {
      if (UtilsApp.isAppFavorite(app.getAPK(), appPreferences.getFavoriteApps())) {
        res.add(app);
      }
    }
    return res;
  }

  @Override
  public boolean onQueryTextChange(String search) {
    if (search.isEmpty()) {
      ((AppAdapter) recyclerView.getAdapter()).getFilter().filter("");
    } else {
      ((AppAdapter) recyclerView.getAdapter()).getFilter().filter(search.toLowerCase());
    }
    return false;
  }

  public static void setResultsMessage(Boolean result) {
    if (result) {
      noResults.setVisibility(View.VISIBLE);
    } else {
      noResults.setVisibility(View.GONE);
    }
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);

    searchItem = menu.findItem(R.id.action_search);
    searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(this);
    return true;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_WRITE_READ: {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          UtilsDialog.showTitleContent(context, getResources().getString(R.string.dialog_permissions), getResources().getString(R.string.dialog_permissions_description));
        }
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen()) {
      drawer.closeDrawer();
    } else if (searchItem.isVisible() && !searchView.isIconified()) {
      searchView.onActionViewCollapsed();
    } else {
      if (doubleBackToExitPressedOnce) {
        super.onBackPressed();
        return;
      }
      this.doubleBackToExitPressedOnce = true;
      Toast.makeText(this, R.string.tap_exit, Toast.LENGTH_SHORT).show();
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          doubleBackToExitPressedOnce = false;
        }
      }, 2000);
    }
  }
}
