package com.fusionx.lightirc.util;

import com.crashlytics.android.Crashlytics;
import com.fusionx.lightirc.misc.AppPreferences;

import android.content.Context;

import co.fusionx.relay.base.Server;

public class CrashUtils {

    public static void startCrashlyticsIfAppropriate(final Context context) {
        callbackIfReportingEnabled(() -> Crashlytics.start(context));
    }

    public static void logMissingData(final Server server) {
        callbackIfReportingEnabled(() -> {
            Crashlytics.log(server.getConfiguration().getUrl());
            Crashlytics.logException(new IRCException("Missing data on server"));
        });
    }

    public static void logIssue(final String data) {
        callbackIfReportingEnabled(() -> {
            Crashlytics.log(data);
            Crashlytics.logException(new IRCException(data));
        });
    }

    private static void callbackIfReportingEnabled(final Runnable runnable) {
        final AppPreferences appPreferences = AppPreferences.getAppPreferences();
        if (appPreferences.isBugReportingEnabled()) {
            runnable.run();
        }
    }

    private static class IRCException extends Exception {

        public IRCException(final String data) {
            super(data);
        }
    }
}