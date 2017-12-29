package com.dkanada.openapk.models;

import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.dkanada.openapk.App;
import com.dkanada.openapk.utils.OtherUtils;

public class AppItem implements Parcelable {
    private Bitmap icon;
    private String packageLabel;
    private String packageName;
    private String versionName;
    private String versionCode;
    private String data;
    private String source;
    private String install;
    private String update;
    public boolean system;
    public boolean disable;
    public boolean hide;
    public boolean favorite;

    public AppItem(PackageInfo packageInfo) {
        icon = OtherUtils.drawableToBitmap(App.getPackageIcon(packageInfo));
        packageLabel = App.getPackageName(packageInfo);
        packageName = packageInfo.packageName;
        versionName = packageInfo.versionName;
        versionCode = Integer.toString(packageInfo.versionCode);
        data = packageInfo.applicationInfo.dataDir;
        source = packageInfo.applicationInfo.sourceDir;
        install = Long.toString(packageInfo.firstInstallTime);
        update = Long.toString(packageInfo.lastUpdateTime);
    }

    public AppItem(Parcel parcel) {
        packageLabel = parcel.readString();
        packageName = parcel.readString();
        versionName = parcel.readString();
        versionCode = parcel.readString();
        data = parcel.readString();
        source = parcel.readString();
        install = parcel.readString();
        update = parcel.readString();
        boolean[] flags = new boolean[4];
        parcel.readBooleanArray(flags);
        system = flags[0];
        disable = flags[1];
        hide = flags[2];
        favorite = flags[3];
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getPackageLabel() {
        return packageLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getData() {
        return data;
    }

    public String getSource() {
        return source;
    }

    public String getInstall() {
        return install;
    }

    public String getUpdate() {
        return update;
    }

    public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator<AppItem>() {
        public AppItem createFromParcel(Parcel parcel) {
            return new AppItem(parcel);
        }
        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(packageLabel);
        parcel.writeString(packageName);
        parcel.writeString(versionName);
        parcel.writeString(versionCode);
        parcel.writeString(data);
        parcel.writeString(source);
        parcel.writeString(install);
        parcel.writeString(update);
        parcel.writeBooleanArray(new boolean[]{ system, disable, hide, favorite });
    }
}
