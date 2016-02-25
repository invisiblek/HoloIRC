package com.fusionx.lightirc.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fusionx.lightirc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import co.fusionx.relay.event.server.DCCChatRequestEvent;
import co.fusionx.relay.event.server.DCCRequestEvent;
import co.fusionx.relay.event.server.DCCSendRequestEvent;

public class DCCRequestAdapter extends RecyclerView.Adapter<DCCRequestAdapter.ViewHolder> {
    private final List<DCCRequestEvent> mRequestEventList;

    private final Context mContext;

    private final LayoutInflater mLayoutInflater;

    private final View.OnClickListener mAcceptListener;

    private final View.OnClickListener mDeclineListener;

    public DCCRequestAdapter(final Context context, final View.OnClickListener acceptListener,
            final View.OnClickListener declineListener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mRequestEventList = new ArrayList<>();
        mAcceptListener = acceptListener;
        mDeclineListener = declineListener;
    }

    @Override
    public int getItemCount() {
        return mRequestEventList.size();
    }

    @Override
    public DCCRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.dcc_pending_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DCCRequestAdapter.ViewHolder holder, int position) {
        final DCCRequestEvent requestEvent = mRequestEventList.get(position);
        String type = "";
        if (requestEvent instanceof DCCChatRequestEvent) {
            type = "CHAT";
        } else if (requestEvent instanceof DCCSendRequestEvent) {
            type = "SEND";
        }
        final String titleText = mContext.getString(R.string.dcc_requested, type,
                requestEvent.getPendingConnection().getDccRequestNick());
        holder.titleView.setText(titleText);

        final String contentText = mContext.getString(R.string.dcc_ip_port,
                requestEvent.getPendingConnection().getIP(),
                requestEvent.getPendingConnection().getPort(),
                requestEvent.getPendingConnection().getArgument());
        holder.contentView.setText(contentText);

        holder.acceptButton.setOnClickListener(mAcceptListener);
        holder.acceptButton.setTag(requestEvent);

        holder.declineButton.setOnClickListener(mDeclineListener);
        holder.declineButton.setTag(requestEvent);
    }

    public void replaceAll(final Set<DCCRequestEvent> dccRequests) {
        mRequestEventList.clear();
        if (dccRequests != null) {
            mRequestEventList.addAll(dccRequests);
        }
        notifyDataSetChanged();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;

        private final TextView contentView;

        private final ImageView acceptButton;

        private final ImageView declineButton;

        public ViewHolder(final View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.dcc_pending_list_item_title);
            contentView = (TextView) itemView.findViewById(R.id.dcc_pending_list_item_content);
            acceptButton = (ImageView) itemView.findViewById(R.id.accept_list_item);
            declineButton = (ImageView) itemView.findViewById(R.id.decline_list_item);
        }
    }
}
