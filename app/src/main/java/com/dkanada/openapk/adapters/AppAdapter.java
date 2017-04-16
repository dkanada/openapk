package com.dkanada.openapk.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.App;
import com.dkanada.openapk.async.ClearDataAsync;
import com.dkanada.openapk.async.DisableAsync;
import com.dkanada.openapk.async.HideAsync;
import com.dkanada.openapk.async.RemoveCacheAsync;
import com.dkanada.openapk.async.UninstallAsync;
import com.dkanada.openapk.utils.AppUtils;
import com.gc.materialdesign.views.ButtonFlat;
import com.dkanada.openapk.activities.AppActivity;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.activities.MainActivity;
import com.dkanada.openapk.async.ExtractFileAsync;
import com.dkanada.openapk.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable {
  private List<AppInfo> appList;
  private List<AppInfo> appListSearch;
  private Context context;

  public AppAdapter(List<AppInfo> appList, Context context) {
    this.appList = appList;
    this.context = context;
  }

  @Override
  public int getItemCount() {
    return appList.size();
  }

  public void clear() {
    appList.clear();
    notifyDataSetChanged();
  }

  @Override
  public void onBindViewHolder(AppViewHolder appViewHolder, int i) {
    AppInfo appInfo = appList.get(i);
    appViewHolder.vName.setText(appInfo.getName());
    appViewHolder.vApk.setText(appInfo.getAPK());
    appViewHolder.vIcon.setImageDrawable(appInfo.getIcon());
    setButtonEvents(appViewHolder, appInfo);
  }

  private void setButtonEvents(AppViewHolder appViewHolder, final AppInfo appInfo) {
    ButtonFlat btnOne = appViewHolder.vOne;
    ButtonFlat btnTwo = appViewHolder.vTwo;
    ButtonFlat btnThree = appViewHolder.vThree;
    final ImageView appIcon = appViewHolder.vIcon;
    final CardView cardView = appViewHolder.vCard;

    btnOne.setVisibility(View.INVISIBLE);
    btnTwo.setVisibility(View.INVISIBLE);
    btnThree.setVisibility(View.INVISIBLE);

    Set<String> actions = App.getAppPreferences().getAction();
    int counter = 0;
    for (String action : actions) {
      if (counter == 0) {
        btnOne.setVisibility(View.VISIBLE);
        setButton(Integer.parseInt(action), btnOne, appInfo);
      } else if (counter == 1) {
        btnTwo.setVisibility(View.VISIBLE);
        setButton(Integer.parseInt(action), btnTwo, appInfo);
      } else if (counter == 2) {
        btnThree.setVisibility(View.VISIBLE);
        setButton(Integer.parseInt(action), btnThree, appInfo);
      } else {
        break;
      }
      counter++;
    }

    cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, AppActivity.class);
        intent.putExtra("app_name", appInfo.getName());
        intent.putExtra("app_apk", appInfo.getAPK());
        intent.putExtra("app_version", appInfo.getVersion());
        intent.putExtra("app_source", appInfo.getSource());
        intent.putExtra("app_data", appInfo.getData());
        intent.putExtra("app_isSystem", appInfo.getSystem());
        intent.putExtra("app_isFavorite", appInfo.getFavorite());
        intent.putExtra("app_isHidden", appInfo.getHidden());
        intent.putExtra("app_isDisabled", appInfo.getDisabled());

        Bitmap bitmap = ((BitmapDrawable) appInfo.getIcon()).getBitmap();
        intent.putExtra("app_icon", bitmap);

        // the icon will smoothly transition to its new location if version is above lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          String transitionName = context.getResources().getString(R.string.transition);
          ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, appIcon, transitionName);
          context.startActivity(intent, transitionActivityOptions.toBundle());
        } else {
          context.startActivity(intent);
        }
      }
    });
  }

  public void setButton(int action, ButtonFlat button, final AppInfo appInfo) {
    switch (action) {
      case 0:
        button.setText(context.getString(R.string.action_open));
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(appInfo.getAPK());
        if (intent != null) {
          button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              context.startActivity(intent);
            }
          });
        } else {
          button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              DialogUtils.showSnackBar((Activity) context, context.getResources().getString(R.string.dialog_no_activity), null, null, 2);
            }
          });
        }
        break;
      case 1:
        button.setText(context.getString(R.string.action_extract));
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                , String.format(context.getResources().getString(R.string.dialog_extract_progress), appInfo.getName())
                , context.getResources().getString(R.string.dialog_extract_progress_description));
            new ExtractFileAsync(context, dialog, appInfo).execute();
          }
        });
        break;
      case 2:
        button.setText(context.getString(R.string.action_uninstall));
        if (appInfo.getSystem()) {
          button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              MaterialDialog.Builder materialBuilder = DialogUtils.uninstallSystemApp(context)
                  .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                      MaterialDialog dialogUninstalling = DialogUtils.showTitleContentWithProgress(context
                          , String.format(context.getResources().getString(R.string.dialog_uninstall_progress), appInfo.getName())
                          , context.getResources().getString(R.string.dialog_uninstall_progress_description));
                      new UninstallAsync(context, dialogUninstalling, appInfo).execute();
                      dialog.dismiss();
                    }
                  });
              materialBuilder.show();
            }
          });
        } else {
          button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
              intent.setData(Uri.parse("package:" + appInfo.getAPK()));
              intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
              context.startActivity(intent);
            }
          });
        }
        break;
      case 3:
        button.setText(context.getString(R.string.action_hide));
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                , context.getResources().getString(R.string.dialog_hide_progress)
                , context.getResources().getString(R.string.dialog_hide_progress_description));
            new HideAsync(context, dialog, appInfo).execute();
          }
        });
        break;
      case 4:
        button.setText(context.getString(R.string.action_disable));
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                , context.getResources().getString(R.string.dialog_disable_progress)
                , context.getResources().getString(R.string.dialog_disable_progress_description));
            new DisableAsync(context, dialog, appInfo).execute();
          }
        });
        break;
      case 5:
        button.setText(context.getString(R.string.action_share));
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            AppUtils.extractFile(appInfo);
            Intent shareIntent = AppUtils.getShareIntent(AppUtils.getOutputFilename(appInfo));
            context.startActivity(Intent.createChooser(shareIntent, String.format(context.getResources().getString(R.string.send_to), appInfo.getName())));
          }
        });
        break;
      case 6:
        button.setText(context.getString(R.string.action_remove_cache));
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                , context.getResources().getString(R.string.dialog_cache_progress)
                , context.getResources().getString(R.string.dialog_cache_progress_description));
            new RemoveCacheAsync(context, dialog, appInfo).execute();
          }
        });
        break;
      case 7:
        button.setText(context.getString(R.string.action_clear_data));
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            MaterialDialog dialog = DialogUtils.showTitleContentWithProgress(context
                , context.getResources().getString(R.string.dialog_clear_data_progress)
                , context.getResources().getString(R.string.dialog_clear_data_progress_description));
            new ClearDataAsync(context, dialog, appInfo).execute();
          }
        });
        break;
    }
  }

  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {
        final FilterResults oReturn = new FilterResults();
        final List<AppInfo> results = new ArrayList<>();
        if (appListSearch == null) {
          appListSearch = appList;
        }
        if (charSequence != null) {
          if (appListSearch != null && appListSearch.size() > 0) {
            for (final AppInfo appInfo : appListSearch) {
              if (appInfo.getName().toLowerCase().contains(charSequence.toString())) {
                results.add(appInfo);
              }
            }
          }
          oReturn.values = results;
          oReturn.count = results.size();
        }
        return oReturn;
      }

      @Override
      protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        if (filterResults.count > 0) {
          MainActivity.setResultsMessage(false);
        } else {
          MainActivity.setResultsMessage(true);
        }
        appList = (ArrayList<AppInfo>) filterResults.values;
        notifyDataSetChanged();
      }
    };
  }

  @Override
  public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View appAdapterView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_app, viewGroup, false);
    return new AppViewHolder(appAdapterView);
  }

  public static class AppViewHolder extends RecyclerView.ViewHolder {
    private TextView vName;
    private TextView vApk;
    private ImageView vIcon;
    private CardView vCard;

    private ButtonFlat vOne;
    private ButtonFlat vTwo;
    private ButtonFlat vThree;

    public AppViewHolder(View v) {
      super(v);
      vName = (TextView) v.findViewById(R.id.txtName);
      vApk = (TextView) v.findViewById(R.id.txtApk);
      vIcon = (ImageView) v.findViewById(R.id.imgIcon);
      vCard = (CardView) v.findViewById(R.id.app_card);

      vOne = (ButtonFlat) v.findViewById(R.id.btnOne);
      vTwo = (ButtonFlat) v.findViewById(R.id.btnTwo);
      vThree = (ButtonFlat) v.findViewById(R.id.btnThree);
    }
  }
}