package com.dkanada.openapk.utils;

import android.content.pm.PackageInfo;
import android.os.Build;

import com.dkanada.openapk.App;

import java.io.File;

public class SystemUtils {
    private static final int ROOT_STATUS_NOT_CHECKED = 0;
    private static final int ROOT_STATUS_ROOTED = 1;
    private static final int ROOT_STATUS_NOT_ROOTED = 2;

    public static boolean isRoot() {
        int rootStatus = App.getAppPreferences().getRootStatus();
        boolean rootEnabled = App.getAppPreferences().getRootEnabled();
        boolean isRooted = false;
        if (rootEnabled) {
            if (rootStatus == ROOT_STATUS_NOT_CHECKED) {
                isRooted = isRootByBuildTag() || isRootedByFileSU() || isRootedByExecutingCommand();
                App.getAppPreferences().setRootStatus(isRooted ? ROOT_STATUS_ROOTED : ROOT_STATUS_NOT_ROOTED);
            } else if (rootStatus == ROOT_STATUS_ROOTED) {
                isRooted = true;
            }
        }
        return isRooted;
    }

    public static boolean isRootByBuildTag() {
        String buildTags = Build.TAGS;
        return ((buildTags != null && buildTags.contains("test-keys")));
    }

    public static boolean isRootedByFileSU() {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isRootedByExecutingCommand() {
        return canExecuteCommand("/system/xbin/which su") ||
                canExecuteCommand("/system/bin/which su") ||
                canExecuteCommand("which su");
    }

    // copy files to the system partition
    public static boolean cpSystemPartition(String input, String output) {
        String[] command_write = new String[]{"su", "-c", "mount -o rw,remount /system"};
        String[] command_delete = new String[]{"su", "-c", "cp -R " + input + " " + output};
        String[] command_read = new String[]{"su", "-c", "mount -o ro,remount /system"};
        if (executeCommand(command_write) == 0 && executeCommand(command_delete) == 0 && executeCommand(command_read) == 0) {
            return true;
        }
        return false;
    }

    // copy files to the data partition
    public static boolean cpDataPartition(String input, String output) {
        String[] command = new String[]{"su", "-c", "cp -R " + input + " " + output};
        if (executeCommand(command) == 0) {
            return true;
        }
        return false;
    }

    // remove files from the system partition
    public static boolean rmSystemPartition(String directory) {
        String[] command_write = new String[]{"su", "-c", "mount -o rw,remount /system"};
        String[] command_delete = new String[]{"su", "-c", "rm -rf " + directory};
        String[] command_read = new String[]{"su", "-c", "mount -o ro,remount /system"};
        if (executeCommand(command_write) == 0 && executeCommand(command_delete) == 0 && executeCommand(command_read) == 0) {
            return true;
        }
        return false;
    }

    // remove files from the data partition
    public static boolean rmDataPartition(String directory) {
        String[] command = new String[]{"su", "-c", "rm -rf " + directory};
        if (executeCommand(command) == 0) {
            return true;
        }
        return false;
    }

    // use package manager to disable a package
    public static boolean disable(PackageInfo packageInfo) {
        String[] command;
        if (!packageInfo.applicationInfo.enabled) {
            command = new String[]{"su", "-c", "pm enable " + packageInfo.packageName};
        } else {
            command = new String[]{"su", "-c", "pm disable " + packageInfo.packageName};
        }
        if (executeCommand(command) == 0) {
            return true;
        }
        return false;
    }

    // use package manager to hide a package
    public static boolean hide(PackageInfo packageInfo) {
        String[] command;
        if (!packageInfo.applicationInfo.enabled) {
            command = new String[]{"su", "-c", "pm unhide " + packageInfo.packageName};
        } else {
            command = new String[]{"su", "-c", "pm hide " + packageInfo.packageName};
        }
        if (executeCommand(command) == 0) {
            return true;
        }
        return false;
    }

    public static boolean rebootSystem() {
        String[] command = new String[]{"su", "-c", "reboot"};
        if (executeCommand(command) == 0) {
            return true;
        }
        return false;
    }

    private static boolean canExecuteCommand(String command) {
        boolean isExecuted;
        try {
            Runtime.getRuntime().exec(command);
            isExecuted = true;
        } catch (Exception e) {
            isExecuted = false;
        }
        return isExecuted;
    }

    private static int executeCommand(String[] commands) {
        try {
            Process process = Runtime.getRuntime().exec(commands);
            process.waitFor();
            return process.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
