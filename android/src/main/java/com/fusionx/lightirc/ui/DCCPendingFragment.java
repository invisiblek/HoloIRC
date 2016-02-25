package com.fusionx.lightirc.ui;

import com.fusionx.bus.Subscribe;
import com.fusionx.bus.ThreadType;
import com.fusionx.lightirc.R;
import com.fusionx.lightirc.event.OnConversationChanged;
import com.fusionx.lightirc.event.OnServiceConnectionStateChanged;
import com.fusionx.lightirc.misc.Theme;
import com.fusionx.lightirc.service.IRCService;
import com.fusionx.lightirc.service.ServiceEventInterceptor;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.event.server.DCCRequestEvent;

import static com.fusionx.lightirc.misc.AppPreferences.getAppPreferences;
import static com.fusionx.lightirc.util.MiscUtils.getBus;

public class DCCPendingFragment extends DialogFragment {

    private final EventHandler mEventHandler = new EventHandler();

    private RecyclerView mRecyclerView;

    private DCCRequestAdapter mAdapter;

    private ServiceEventInterceptor mInterceptor;

    public static DCCPendingFragment createInstance() {
        return new DCCPendingFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_FRAME, getAppPreferences().getTheme() == Theme.DARK
                ? android.R.style.Theme_DeviceDefault_Dialog
                : android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dcc_pending_fragment, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBus().registerSticky(mEventHandler);

        mAdapter = new DCCRequestAdapter(getActivity(),
                new AcceptListener(), new DeclineListener());
        mRecyclerView.setAdapter(mAdapter);
        if (mInterceptor != null) {
            mAdapter.replaceAll(mInterceptor.getDCCRequests());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getBus().unregister(mEventHandler);
        updateInterceptor(null);
    }

    @Subscribe(threadType = ThreadType.MAIN)
    public void onEvent(final DCCRequestEvent requestEvent) {
        mAdapter.replaceAll(mInterceptor.getDCCRequests());
    }

    private void updateInterceptor(ServiceEventInterceptor interceptor) {
        final Collection<DCCRequestEvent> events = new ArrayList<>();
        if (mInterceptor != null && interceptor == null) {
            mInterceptor.getServer().getServerWideBus().unregister(this);
            if (mAdapter != null) {
                mAdapter.replaceAll(null);
            }
            mInterceptor = null;
        } else if (mInterceptor == null && interceptor != null) {
            mInterceptor = interceptor;
            mInterceptor.getServer().getServerWideBus().register(this);
            if (mAdapter != null) {
                mAdapter.replaceAll(mInterceptor.getDCCRequests());
            }
        }
    }

    private class AcceptListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final DCCRequestEvent event = (DCCRequestEvent) v.getTag();
            mInterceptor.acceptDCCConnection(event);
            mAdapter.replaceAll(mInterceptor.getDCCRequests());
        }
    }

    private class DeclineListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final DCCRequestEvent event = (DCCRequestEvent) v.getTag();
            mInterceptor.declineDCCRequestEvent(event);
            mAdapter.replaceAll(mInterceptor.getDCCRequests());
        }
    }

    private class EventHandler {
        private Server mServer;
        private IRCService mService;

        @Subscribe
        public void onEvent(final OnConversationChanged conversationChanged) {
            mServer = conversationChanged.conversation != null
                    ? conversationChanged.conversation.getServer() : null;
            updateInterceptor(getInterceptor());
        }

        @Subscribe
        public void onEvent(final OnServiceConnectionStateChanged serviceChanged) {
            mService = serviceChanged.getService();
            updateInterceptor(getInterceptor());
        }

        private ServiceEventInterceptor getInterceptor() {
            return mServer != null && mService != null
                    ? mService.getEventHelper(mServer) : null;
        }
    }
}