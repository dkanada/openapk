package com.dkanada.openapk.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dkanada.openapk.utils.AppDbUtils;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.adapters.AppAdapter;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.InterfaceUtils;
import com.mikepenz.materialdrawer.Drawer;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ThemeActivity implements SearchView.OnQueryTextListener {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

    // settings
    private AppPreferences appPreferences;
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
    private ImageView icon;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activity = this;
        this.context = this;

        setInitialConfiguration();
        AppUtils.checkPermissions(activity);

        recyclerView = (RecyclerView) findViewById(R.id.appList);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        noResults = (LinearLayout) findViewById(R.id.noResults);

        icon = (ImageView) findViewById(R.id.noResultsIcon);
        if (appPreferences.getTheme().equals("1")) {
            icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        drawer = InterfaceUtils.setNavigationDrawer((Activity) context, context, toolbar, appAdapter, appSystemAdapter, appFavoriteAdapter, appHiddenAdapter, appDisabledAdapter, recyclerView);

        refresh.setColorSchemeColors(appPreferences.getPrimaryColor());
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

        // getInitialSetup is currently redundant but might be useful in the future
        if (!appPreferences.getInitialSetup()) {
            appPreferences.setInitialSetup(true);
            new updateInstalledApps().execute();
        } else {
            new updateInstalledApps().execute();
        }
    }

    private void setInitialConfiguration() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(InterfaceUtils.darker(appPreferences.getPrimaryColor(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColor());
            if (appPreferences.getNavigationColor()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColor());
            }
        }
    }

    class getInstalledApps extends AsyncTask<Void, String, Void> {
        private List<AppInfo> appInstalledList;
        private List<AppInfo> appSystemList;
        private List<AppInfo> appFavoriteList;
        private List<AppInfo> appHiddenList;
        private List<AppInfo> appDisabledList;

        @Override
        protected Void doInBackground(Void... params) {
            AppDbUtils db = new AppDbUtils(context);

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
            switch (App.getCurrentAdapter()) {
                default:
                    recyclerView.swapAdapter(appAdapter, false);
                    InterfaceUtils.setToolbarTitle(activity, getResources().getString(R.string.action_installed_apps));
                    break;
                case 1:
                    recyclerView.swapAdapter(appSystemAdapter, false);
                    InterfaceUtils.setToolbarTitle(activity, getResources().getString(R.string.action_system_apps));
                    break;
                case 2:
                    recyclerView.swapAdapter(appFavoriteAdapter, false);
                    InterfaceUtils.setToolbarTitle(activity, getResources().getString(R.string.action_favorite_apps));
                    break;
                case 3:
                    recyclerView.swapAdapter(appHiddenAdapter, false);
                    InterfaceUtils.setToolbarTitle(activity, getResources().getString(R.string.action_hidden_apps));
                    break;
                case 4:
                    recyclerView.swapAdapter(appDisabledAdapter, false);
                    InterfaceUtils.setToolbarTitle(activity, getResources().getString(R.string.action_disabled_apps));
                    break;
            }
            drawer = InterfaceUtils.setNavigationDrawer((Activity) context, context, toolbar, appAdapter, appSystemAdapter, appFavoriteAdapter, appHiddenAdapter, appDisabledAdapter, recyclerView);
            super.onPostExecute(aVoid);
            refresh.setRefreshing(false);
        }
    }

    class updateInstalledApps extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            AppDbUtils db = new AppDbUtils(context);
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
        final PackageManager packageManager = getPackageManager();
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
                // TODO fix this
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
                // compare by installation date
                Collections.sort(appList, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo appOne, AppInfo appTwo) {
                        try {
                            PackageInfo infoOne = packageManager.getPackageInfo(appOne.getAPK(), 0);
                            PackageInfo infoTwo = packageManager.getPackageInfo(appTwo.getAPK(), 0);
                            return Long.toString(infoTwo.firstInstallTime).compareTo(Long.toString(infoOne.firstInstallTime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 1;
                    }
                });
                break;
            case "3":
                // compare by last update
                Collections.sort(appList, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo appOne, AppInfo appTwo) {
                        try {
                            PackageInfo infoOne = packageManager.getPackageInfo(appOne.getAPK(), 0);
                            PackageInfo infoTwo = packageManager.getPackageInfo(appTwo.getAPK(), 0);
                            return Long.toString(infoTwo.lastUpdateTime).compareTo(Long.toString(infoOne.lastUpdateTime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 1;
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
                    DialogUtils.showTitleContent(context, getResources().getString(R.string.dialog_permissions), getResources().getString(R.string.dialog_permissions_description));
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
