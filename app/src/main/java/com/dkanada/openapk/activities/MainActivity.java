package com.dkanada.openapk.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.adapters.AppAdapter;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.InterfaceUtils;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ThemeActivity implements SearchView.OnQueryTextListener {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

    // settings
    private AppPreferences appPreferences;
    private PackageManager packageManager;
    private AppAdapter appInstalledAdapter;
    private AppAdapter appSystemAdapter;
    private AppAdapter appFavoriteAdapter;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activity = this;
        this.context = this;

        appPreferences = App.getAppPreferences();
        packageManager = getPackageManager();

        setInitialConfiguration();
        AppUtils.checkPermissions(activity);

        recyclerView = (RecyclerView) findViewById(R.id.appList);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        noResults = (LinearLayout) findViewById(R.id.no_results);

        icon = (ImageView) findViewById(R.id.no_results_icon);
        if (appPreferences.getTheme().equals("1")) {
            icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        drawer = InterfaceUtils.setNavigationDrawer(context, toolbar, recyclerView, false, appInstalledAdapter, appSystemAdapter, appFavoriteAdapter, appDisabledAdapter);

        // might be useful in the future
        if (!appPreferences.getInitialSetup()) {
            appPreferences.setInitialSetup(true);
        }

        refresh.setColorSchemeColors(appPreferences.getPrimaryColor());
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                new getInstalledApps().execute();
            }
        });

        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
            }
        });
        new getInstalledApps().execute();
    }

    private void setInitialConfiguration() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(InterfaceUtils.dark(appPreferences.getPrimaryColor(), 0.8));
            toolbar.setBackgroundColor(appPreferences.getPrimaryColor());
            if (appPreferences.getNavigationColor()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColor());
            }
        }
    }

    class getInstalledApps extends AsyncTask<Void, String, Void> {
        private List<PackageInfo> appInstalledList = new ArrayList<PackageInfo>();
        private List<PackageInfo> appSystemList = new ArrayList<PackageInfo>();
        private List<PackageInfo> appFavoriteList = new ArrayList<PackageInfo>();
        private List<PackageInfo> appDisabledList = new ArrayList<PackageInfo>();

        @Override
        protected Void doInBackground(Void... params) {
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            for (PackageInfo packageInfo : packages) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    appSystemList.add(packageInfo);
                } else if (!packageInfo.applicationInfo.enabled) {
                    appDisabledList.add(packageInfo);
                } else {
                    appInstalledList.add(packageInfo);
                }
            }

            appInstalledList = sortAdapter(appInstalledList);
            appSystemList = sortAdapter(appSystemList);
            appFavoriteList = sortAdapter(appFavoriteList);
            appDisabledList = sortAdapter(appDisabledList);

            appInstalledAdapter = new AppAdapter(context, appInstalledList);
            appSystemAdapter = new AppAdapter(context, appSystemList);
            appFavoriteAdapter = new AppAdapter(context, appFavoriteList);
            appDisabledAdapter = new AppAdapter(context, appDisabledList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            switch (App.getCurrentAdapter()) {
                case 0:
                    recyclerView.swapAdapter(appInstalledAdapter, false);
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
                    recyclerView.swapAdapter(appDisabledAdapter, false);
                    InterfaceUtils.setToolbarTitle(activity, getResources().getString(R.string.action_disabled_apps));
                    break;
                default:
                    recyclerView.swapAdapter(appInstalledAdapter, false);
                    InterfaceUtils.setToolbarTitle(activity, getResources().getString(R.string.action_installed_apps));
                    break;
            }
            drawer = InterfaceUtils.setNavigationDrawer(context, toolbar, recyclerView, true, appInstalledAdapter, appSystemAdapter, appFavoriteAdapter, appDisabledAdapter);
            super.onPostExecute(aVoid);
            refresh.setRefreshing(false);
        }
    }

    public List<PackageInfo> sortAdapter(List<PackageInfo> list) {
        switch (appPreferences.getSortMode()) {
            case "0":
                // compare by name
                Collections.sort(list, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo one, PackageInfo two) {
                        return App.getPackageName(one).compareTo(App.getPackageName(two));
                    }
                });
                break;
            case "1":
                // compare by install date
                Collections.sort(list, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo one, PackageInfo two) {
                        return Long.toString(two.firstInstallTime).compareTo(Long.toString(one.firstInstallTime));
                    }
                });
                break;
            case "2":
                // compare by last update
                Collections.sort(list, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo one, PackageInfo two) {
                        return Long.toString(two.lastUpdateTime).compareTo(Long.toString(one.lastUpdateTime));
                    }
                });
                break;
            default:
                // compare by name
                Collections.sort(list, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo one, PackageInfo two) {
                        return App.getPackageName(one).compareTo(App.getPackageName(two));
                    }
                });
                break;
        }
        return list;
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

        searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
