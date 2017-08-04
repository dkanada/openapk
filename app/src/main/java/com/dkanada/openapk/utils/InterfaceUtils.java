package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.dkanada.openapk.App;
import com.dkanada.openapk.activities.AboutActivity;
import com.dkanada.openapk.R;
import com.dkanada.openapk.activities.SettingsActivity;
import com.dkanada.openapk.adapters.AppAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class InterfaceUtils {

    public static int dark(int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }

    public static Drawer setNavigationDrawer(final Context context, final Toolbar toolbar, final RecyclerView recyclerView, boolean badge, final AppAdapter appInstalledAdapter, final AppAdapter appSystemAdapter, final AppAdapter appFavoriteAdapter, final AppAdapter appDisabledAdapter) {
        AppPreferences appPreferences = App.getAppPreferences();
        final Activity activity = (Activity) context;
        int header;

        // check for dark theme
        Integer badgeColor;
        BadgeStyle badgeStyle;
        if (appPreferences.getTheme().equals("1")) {
            badgeColor = ContextCompat.getColor(context, R.color.badge_light);
            badgeStyle = new BadgeStyle(badgeColor, badgeColor).withTextColor(context.getResources().getColor(R.color.text_light));
            header = R.drawable.header_day;
        } else {
            badgeColor = ContextCompat.getColor(context, R.color.badge_dark);
            badgeStyle = new BadgeStyle(badgeColor, badgeColor).withTextColor(context.getResources().getColor(R.color.text_dark));
            header = R.drawable.header_night;
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(header)
                .build();

        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder.withActivity(activity);
        drawerBuilder.withToolbar(toolbar);
        drawerBuilder.withAccountHeader(headerResult);
        drawerBuilder.withStatusBarColor(InterfaceUtils.dark(appPreferences.getPrimaryColor(), 0.8));

        if (badge) {
            String installedApps = Integer.toString(appInstalledAdapter.getItemCount());
            String systemApps = Integer.toString(appSystemAdapter.getItemCount());
            String favoriteApps = Integer.toString(appFavoriteAdapter.getItemCount());
            String disabledApps = Integer.toString(appDisabledAdapter.getItemCount());
            drawerBuilder.addDrawerItems(
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_installed_apps)).withIcon(GoogleMaterial.Icon.gmd_phone_android).withBadge(installedApps).withBadgeStyle(badgeStyle).withIdentifier(0),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_system_apps)).withIcon(GoogleMaterial.Icon.gmd_android).withBadge(systemApps).withBadgeStyle(badgeStyle).withIdentifier(1),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_favorite_apps)).withIcon(GoogleMaterial.Icon.gmd_star).withBadge(favoriteApps).withBadgeStyle(badgeStyle).withIdentifier(2),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_disabled_apps)).withIcon(GoogleMaterial.Icon.gmd_remove_circle_outline).withBadge(disabledApps).withBadgeStyle(badgeStyle).withIdentifier(3),
                    new DividerDrawerItem(),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_settings)).withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false).withIdentifier(4),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_about)).withIcon(GoogleMaterial.Icon.gmd_info).withSelectable(false).withIdentifier(5));
        } else {
            drawerBuilder.addDrawerItems(
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_installed_apps)).withIcon(GoogleMaterial.Icon.gmd_phone_android).withIdentifier(0),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_system_apps)).withIcon(GoogleMaterial.Icon.gmd_android).withIdentifier(1),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_favorite_apps)).withIcon(GoogleMaterial.Icon.gmd_star).withIdentifier(2),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_disabled_apps)).withIcon(GoogleMaterial.Icon.gmd_remove_circle_outline).withIdentifier(3),
                    new DividerDrawerItem(),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_settings)).withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false).withIdentifier(4),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_about)).withIcon(GoogleMaterial.Icon.gmd_info).withSelectable(false).withIdentifier(5));
        }

        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem iDrawerItem) {
                switch (iDrawerItem.getIdentifier()) {
                    case 0:
                        recyclerView.setAdapter(appInstalledAdapter);
                        App.setCurrentAdapter(0);
                        setToolbarTitle(activity, context.getResources().getString(R.string.action_installed_apps));
                        break;
                    case 1:
                        recyclerView.setAdapter(appSystemAdapter);
                        App.setCurrentAdapter(1);
                        setToolbarTitle(activity, context.getResources().getString(R.string.action_system_apps));
                        break;
                    case 2:
                        recyclerView.setAdapter(appFavoriteAdapter);
                        App.setCurrentAdapter(2);
                        setToolbarTitle(activity, context.getResources().getString(R.string.action_favorite_apps));
                        break;
                    case 3:
                        recyclerView.setAdapter(appDisabledAdapter);
                        App.setCurrentAdapter(3);
                        setToolbarTitle(activity, context.getResources().getString(R.string.action_disabled_apps));
                        break;
                    case 4:
                        context.startActivity(new Intent(context, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case 5:
                        context.startActivity(new Intent(context, AboutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        return drawerBuilder.build();
    }

    // set the toolbar title with any string
    public static void setToolbarTitle(Activity activity, String title) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    // update the state of the favorite icon
    public static void updateAppFavoriteIcon(Context context, MenuItem menuItem, Boolean isFavorite) {
        if (isFavorite) {
            menuItem.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_star));
        } else {
            menuItem.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_star_border));
        }
    }
}