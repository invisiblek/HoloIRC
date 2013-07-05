/*
    LightIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of LightIRC.

    LightIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    LightIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LightIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.lightirc.fragments.ircfragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.fusionx.lightirc.activity.IRCFragmentActivity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.pircbotx.Channel;

import java.util.ArrayList;

public class ChannelFragment extends IRCFragment {
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private ArrayList<String> userList;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container, savedInstanceState);

        final ArrayList<String> list = getArguments().getStringArrayList("userList");
        if (list != null) {
            userList = list;
        }

        return rootView;
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE && getEditText().getText() != null) {
            final String message = getEditText().getText().toString();
            getEditText().setText("");

            final ParserTask task = new ParserTask();
            final String[] strings = {serverName, getTitle(), message};
            task.execute(strings);
        }
        return false;
    }

    private class ParserTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(final String... strings) {
            if (strings != null) {
                final String server = strings[0];
                final String channelName = strings[1];
                final String message = strings[2];
                ((IRCFragmentActivity) getActivity())
                        .getParser().channelMessageToParse(server, channelName, message);
            }
            return null;
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        final Channel channel = ((IRCFragmentActivity) getActivity()).getService().getBot(serverName)
                .getUserChannelDao().getChannel(getTitle());
        writeToTextView(channel.getBuffer());
        setUserList(channel.getUserList());
    }
}