package com.smartdevicelink.util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import java.util.Comparator;
import java.util.Set;

/**
 * Created by Joey Grover on 2/2/18.
 */

public class SdlAppInfo {
    private static final String TAG = "SdlAppInfo";

    //FIXME we shouldn't be duplicating constants, but this currently keeps us from needing a context instance.
    private static final String SDL_ROUTER_VERSION_METADATA = "sdl_router_version";
    private static final String SDL_ROUTER_LOCATION_METADATA = "sdl_router_location";
    private static final String SDL_CUSTOM_ROUTER_METADATA = "sdl_custom_router";

    private static final String ROUTER_SERVICE_SUFFIX = ".SdlRouterService";

    String packageName;
    ComponentName receiverComponentName, routerServiceComponentName;
    int routerServiceVersion = 4; //We use this as a default and assume if the number doens't exist in meta data it is because the app hasn't updated.
    boolean isCustomRouterService = false;
    long lastUpdateTime;


    public SdlAppInfo(ResolveInfo resolveInfo, PackageInfo packageInfo){
        if(resolveInfo.activityInfo != null){

            this.packageName = resolveInfo.activityInfo.packageName;

            receiverComponentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);


            Bundle metadata = resolveInfo.activityInfo.metaData;

            if(metadata.containsKey(SDL_ROUTER_VERSION_METADATA)){
                this.routerServiceVersion = metadata.getInt(SDL_ROUTER_VERSION_METADATA);
            }

            if(metadata.containsKey(SDL_ROUTER_LOCATION_METADATA)){
                String rsName = metadata.getString(SDL_ROUTER_LOCATION_METADATA);
                if(rsName != null){
                    this.routerServiceComponentName = new ComponentName(rsName.substring(0, rsName.lastIndexOf(".")), rsName);
                } //else there's an issue
            }else{
                this.routerServiceComponentName = new ComponentName(this.packageName, this.packageName+ROUTER_SERVICE_SUFFIX);
            }

            if(metadata.containsKey(SDL_CUSTOM_ROUTER_METADATA)){
                this.isCustomRouterService = metadata.getBoolean(SDL_CUSTOM_ROUTER_METADATA);
            }
        }

        if(packageInfo != null){
            this.lastUpdateTime = packageInfo.lastUpdateTime;
            if(this.lastUpdateTime == 0){
                this.lastUpdateTime = packageInfo.firstInstallTime;
            }
        }
    }

    public int getRouterServiceVersion() {
        return routerServiceVersion;
    }

    public boolean isCustomRouterService() {
        return isCustomRouterService;
    }

    public ComponentName getReceiverComponentName() {
        return receiverComponentName;
    }

    public ComponentName getRouterServiceComponentName() {
        return routerServiceComponentName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("-------- Sdl App Info ------\n");

        builder.append("Package Name: ");
        builder.append(packageName);

        builder.append("\nBroadcast Receiver: ");
        builder.append(this.receiverComponentName.getClassName());

        builder.append("\nRouter Service: ");
        builder.append(this.routerServiceComponentName.getClassName());

        builder.append("\nRouter Service Version: ");
        builder.append(this.routerServiceVersion);

        builder.append("\nCustom Router Service?: ");
        builder.append(this.isCustomRouterService);

        builder.append("\nLast updated: ");
        builder.append(this.lastUpdateTime);

        builder.append("\n-------- Sdl App Info End------");

        return builder.toString();
    }

    /**
     * This comparator will sort a list to find the best router service to start out of the known SDL enabled apps
     *
     */
    public static class BestRouterComparator implements Comparator<SdlAppInfo>{

        @Override
        public int compare(SdlAppInfo one, SdlAppInfo two) {
            if(one != null){
                if(two != null){
                    if(one.isCustomRouterService){
                        if(two.isCustomRouterService){
                            return 0;
                        }else{
                            return -1;
                        }
                    }else if(two.isCustomRouterService){
                        return -1;

                    }else {
                        //Do nothing. Move to version check
                    }

                    int versionCompare =  two.routerServiceVersion  - one.routerServiceVersion;

                    if(versionCompare == 0){ //Versions are equal so lets use the one that has been updated most recently
                        int updateTime =  (int)(two.lastUpdateTime - one.lastUpdateTime);
                        if(updateTime == 0){
                            //This is arbitrary, but we want to ensure all lists are sorted in the same order
                            return  one.routerServiceComponentName.getPackageName().compareTo(two.routerServiceComponentName.getPackageName());
                        }else{
                            return updateTime;
                        }
                    }else{
                        return versionCompare;
                    }

                }else{
                    return -1;
                }
            }else{
                if(two != null){
                    return 1;
                }
            }
            return 0;
        }


    }
}
