package com.example.root.albumbytime;

import android.app.Activity;
import android.content.Context;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import java.util.List;

/**
 * Created by jack
 * On 18-3-8:下午5:08
 * Desc:
 */

public class PermissionUtils {
    public static void ReqPermission(Activity activity, final IPersissionCallback callback, String hintStr, String[]... persision) {
        if (callback == null) {
            return;
        }
        AndPermission.with(activity)
                .permission(persision)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        callback.onGranted(permissions);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        callback.onDenied(permissions);

                    }
                })
                .start();
    }

    public interface IPersissionCallback {
        void onGranted(List<String> granteds);
        void onDenied(List<String> denieds);
    }
}
