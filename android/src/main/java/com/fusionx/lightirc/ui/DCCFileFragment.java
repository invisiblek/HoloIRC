package com.fusionx.lightirc.ui;

import com.fusionx.bus.Subscribe;
import com.fusionx.bus.ThreadType;
import com.fusionx.lightirc.R;
import com.fusionx.lightirc.event.OnConversationChanged;
import com.fusionx.lightirc.misc.FragmentType;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.fusionx.relay.base.Conversation;
import co.fusionx.relay.dcc.event.file.DCCFileGetStartedEvent;
import co.fusionx.relay.dcc.event.file.DCCFileProgressEvent;
import co.fusionx.relay.dcc.file.DCCFileConversation;

import static com.fusionx.lightirc.util.MiscUtils.getBus;

public class DCCFileFragment extends BaseIRCFragment {

    private final EventHandler mEventHandler = new EventHandler();

    private RecyclerView mRecyclerView;

    private Conversation mConversation;

    private DCCFileAdapter mAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final OnConversationChanged event = getBus().getStickyEvent(OnConversationChanged.class);
        mConversation = event.conversation;

        mAdapter = new DCCFileAdapter(getActivity(), getFileConversation().getFileConnections());
    }

    @Override
    public View onCreateView(final LayoutInflater inflate, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflate.inflate(R.layout.dcc_file_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mConversation.getBus().register(mEventHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mConversation.getBus().unregister(mEventHandler);
    }

    @Override
    public FragmentType getType() {
        return FragmentType.DCCFILE;
    }

    @Override
    public boolean isValid() {
        return mConversation.isValid();
    }

    public DCCFileConversation getFileConversation() {
        return (DCCFileConversation) mConversation;
    }

    private class EventHandler {

        @Subscribe(threadType = ThreadType.MAIN)
        public void onFileProgress(final DCCFileProgressEvent event) {
            mAdapter.notifyDataSetChanged();
        }

        @Subscribe(threadType = ThreadType.MAIN)
        public void onNewFileConnection(final DCCFileGetStartedEvent event) {
            mAdapter.replaceAll(event.fileConversation.getFileConnections());
        }
    }
}