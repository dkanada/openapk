package com.dkanada.openapk.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
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

import com.dkanada.openapk.AppDatabase;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
  private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

  // settings
  private AppPreferences appPreferences;

  // general variables
  private List<AppInfo> appInstalledList;
  private List<AppInfo> appSystemList;
  private List<AppInfo> appFavoriteList;
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
      setTheme(R.style.DrawerDark);
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

    refresh.setColorSchemeColors(appPreferences.getPrimaryColorPref());
    refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refresh.setRefreshing(true);
        new updateInstalledApps().execute();
      }
    });
    refresh.post(new Runnable() {
      @Override
      public void run() {
        refresh.setRefreshing(true);
      }
    });

    if (!appPreferences.getInitialSetup()) {
      appPreferences.setInitialSetup(true);
      new updateInstalledApps().execute();
    } else {
      new getInstalledApps().execute();
    }
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
      if (appPreferences.getNavigationColorPref()) {
        getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
      }
    }
  }

  class getInstalledApps extends AsyncTask<Void, String, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      AppDatabase db = new AppDatabase(context);

      appInstalledList = sortAdapter(db.getAppList(context, 0));
      appSystemList = sortAdapter(db.getAppList(context, 1));
      appFavoriteList = sortAdapter(db.getAppList(context, 2));
      appHiddenList = sortAdapter(db.getAppList(context, 3));
      appDisabledList = sortAdapter(db.getAppList(context, 4));

      appAdapter = new AppAdapter(appInstalledList, context);
      appSystemAdapter = new AppAdapter(appSystemList, context);
      appFavoriteAdapter = new AppAdapter(appFavoriteList, context);
      appHiddenAdapter = new AppAdapter(appHiddenList, context);
      appDisabledAdapter = new AppAdapter(appDisabledList, context);
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      switch(OpenAPKApplication.getCurrentAdapter()) {
        default:
          recyclerView.swapAdapter(appAdapter, false);
          UtilsUI.setToolbarTitle(activity, getResources().getString(R.string.action_apps));
          break;
        case 1:
          recyclerView.swapAdapter(appSystemAdapter, false);
          UtilsUI.setToolbarTitle(activity, getResources().getString(R.string.action_system_apps));
          break;
        case 2:
          recyclerView.swapAdapter(appFavoriteAdapter, false);
          UtilsUI.setToolbarTitle(activity, getResources().getString(R.string.action_favorite_apps));
          break;
        case 3:
          recyclerView.swapAdapter(appHiddenAdapter, false);
          UtilsUI.setToolbarTitle(activity, getResources().getString(R.string.action_hidden_apps));
          break;
        case 4:
          recyclerView.swapAdapter(appDisabledAdapter, false);
          UtilsUI.setToolbarTitle(activity, getResources().getString(R.string.action_disabled_apps));
          break;
      }
      drawer = UtilsUI.setNavigationDrawer((Activity) context, context, toolbar, appAdapter, appSystemAdapter, appFavoriteAdapter, appHiddenAdapter, appDisabledAdapter, recyclerView);
      super.onPostExecute(aVoid);
      refresh.setRefreshing(false);
    }
  }

  class updateInstalledApps extends AsyncTask<Void, String, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      AppDatabase db = new AppDatabase(context);
      db.updateDatabase(context);
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      new getInstalledApps().execute();
    }
  }

  public List<AppInfo> sortAdapter(List<AppInfo> appList) {
    switch (appPreferences.getSortMode()) {
      default:
        // compare by name
        Collections.sort(appList, new Comparator<AppInfo>() {
          @Override
          public int compare(AppInfo appOne, AppInfo appTwo) {
            return appOne.getName().toLowerCase().compareTo(appTwo.getName().toLowerCase());
          }
        });
        break;
      case "1":
        // compare by size
        Collections.sort(appList, new Comparator<AppInfo>() {
          @Override
          public int compare(AppInfo appOne, AppInfo appTwo) {
            Long size1 = new File(appOne.getData()).length();
            Long size2 = new File(appTwo.getData()).length();
            return size2.compareTo(size1);
          }
        });
        break;
      case "2":
        // compare by size
        Collections.sort(appList, new Comparator<AppInfo>() {
          @Override
          public int compare(AppInfo appOne, AppInfo appTwo) {
            Long size1 = new File(appOne.getData()).length();
            Long size2 = new File(appTwo.getData()).length();
            return size2.compareTo(size1);
          }
        });
        break;
      case "3":
        // compare by size
        Collections.sort(appList, new Comparator<AppInfo>() {
          @Override
          public int compare(AppInfo appOne, AppInfo appTwo) {
            Long size1 = new File(appOne.getData()).length();
            Long size2 = new File(appTwo.getData()).length();
            return size2.compareTo(size1);
          }
        });
        break;
    }
    return appList;
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
    } else if (appPreferences.getDoubleTap()) {
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
    } else {
      super.onBackPressed();
      return;
    }
  }
}
