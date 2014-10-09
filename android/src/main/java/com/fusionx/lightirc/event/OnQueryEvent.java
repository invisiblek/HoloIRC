package com.fusionx.lightirc.event;

import co.fusionx.relay.base.QueryUser;

public class OnQueryEvent {

    public final QueryUser queryUser;
    public final CharSequence message;

    public OnQueryEvent(final QueryUser queryUser, final CharSequence message) {
        this.queryUser = queryUser;
        this.message = message;
    }
}