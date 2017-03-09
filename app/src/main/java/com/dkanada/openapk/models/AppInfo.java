package com.dkanada.openapk.models;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppInfo implements Serializable {
  private String name;
  private String apk;
  private String version;
  private String source;
  private String data;
  private Boolean system;
  private Boolean favorite;
  private Boolean hidden;
  private Boolean disabled;
  private Drawable icon;

  public AppInfo(String name, String apk, String version, String source, String data, Boolean system, Boolean favorite, Boolean hidden, Boolean disabled, Drawable icon) {
    this.name = name;
    this.apk = apk;
    this.version = version;
    this.source = source;
    this.data = data;
    this.system = system;
    this.favorite = favorite;
    this.hidden = hidden;
    this.disabled = disabled;
    this.icon = icon;
  }

  public AppInfo(String string) {
    String[] split = string.split("##");
    if (split.length == 9) {
      this.name = split[0];
      this.apk = split[1];
      this.version = split[2];
      this.source = split[3];
      this.data = split[4];
      this.system = Boolean.parseBoolean(split[5]);
      this.favorite = Boolean.parseBoolean(split[6]);
      this.hidden = Boolean.parseBoolean(split[7]);
      this.disabled = Boolean.parseBoolean(split[8]);
    }
  }

  public String getName() {
    return name;
  }

  public String getAPK() {
    return apk;
  }

  public String getVersion() {
    return version;
  }

  public String getSource() {
    return source;
  }

  public String getData() {
    return data;
  }

  public Boolean getSystem() {
    return system;
  }

  public void setSystem(boolean bool) {
    system = bool;
  }

  public Boolean getFavorite() {
    return favorite;
  }

  public void setFavorite(boolean bool) {
    favorite = bool;
  }

  public Boolean getHidden() {
    return hidden;
  }

  public void setHidden(boolean bool) {
    hidden = bool;
  }

  public Boolean getDisabled() {
    return disabled;
  }

  public void setDisabled(boolean bool) {
    disabled = bool;
  }

  public Drawable getIcon() {
    return icon;
  }

  public void setIcon(Drawable icon) {
    this.icon = icon;
  }

  public String toString() {
    return getName() + "##" + getAPK() + "##" + getVersion() + "##" + getSource() + "##" + getData() + "##" + getSystem() + "##" + getFavorite() + "##" + getHidden() + "##" + getDisabled();
  }
}
