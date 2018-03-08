package com.cpen391.healthwatch.util;

import com.cpen391.healthwatch.map.marker.animation.AbstractMarkerAnimationFactory;
import com.cpen391.healthwatch.server.abstraction.AppControlInterface;
import com.cpen391.healthwatch.server.abstraction.ServerInterface;

public class GlobalFactory {
    private static ServerInterface mServerInterface;
    private static AppControlInterface mAppControlInterface;
    private static AbstractMarkerAnimationFactory mAbstractMarkerAnimationFactory;

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
}
