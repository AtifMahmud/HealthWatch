package com.cpen391.healthwatch.util;

import com.cpen391.healthwatch.map.marker.animation.AbstractMarkerAnimationFactory;
import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;
import com.cpen391.healthwatch.user.UserSessionInterface;

public class GlobalFactory {
    private static ServerInterface mServerInterface;
    private static AppControlInterface mAppControlInterface;
    private static AbstractMarkerAnimationFactory mAbstractMarkerAnimationFactory;
    private static UserSessionInterface mUserSession;

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

    public static void setAbstractMarkerAnimationFactory(AbstractMarkerAnimationFactory factory) {
        mAbstractMarkerAnimationFactory = factory;
    }

    public static AbstractMarkerAnimationFactory getAbstractMarkerAnimationFactory() {
        return mAbstractMarkerAnimationFactory;
    }

    public static void setUserSessionInterface(UserSessionInterface userSessionInterface) {
        mUserSession = userSessionInterface;
    }

    public static UserSessionInterface getUserSessionInterface() {
        return mUserSession;
    }
}
