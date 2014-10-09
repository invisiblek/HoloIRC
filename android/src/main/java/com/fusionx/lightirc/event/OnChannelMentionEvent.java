package com.fusionx.lightirc.event;

import co.fusionx.relay.base.Channel;

public class OnChannelMentionEvent {

    public final Channel channel;
    public final CharSequence message;

    public OnChannelMentionEvent(final Channel channel, CharSequence message) {
        this.channel = channel;
        this.message = message;
    }
}