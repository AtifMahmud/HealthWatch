package com.cpen391.healthwatch.util;

import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;

public class GlobalFactory {
    private static ServerInterface mServerInterface;
    private static AppControlInterface mAppControlInterface;

    public static void setServerInterface(ServerInterface serverInterface) {
        mServerInterface = serverInterface;
    }

    public static ServerInterface getServerInterface() {
        return mServerInterface;
    }

    public static void setAppControlInterface(AppControlInterface appControlInterface) {
        mAppControlInterface = appControlInterface;
    }

    public static AppControlInterface getAppControlInterface() {
        return mAppControlInterface;
    }
}
