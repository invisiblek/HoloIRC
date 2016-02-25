package com.fusionx.lightirc.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fusionx.lightirc.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.fusionx.relay.dcc.file.DCCFileConnection;

public class DCCFileAdapter extends RecyclerView.Adapter<DCCFileAdapter.ViewHolder> {

    private final Context mContext;

    private final LayoutInflater mLayoutInflater;

    private final List<DCCFileConnection> mConnectionList;

    public DCCFileAdapter(final Context context, final Collection<DCCFileConnection>
            dccConnectionList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mConnectionList = new ArrayList<>(dccConnectionList);
    }

    @Override
    public int getItemCount() {
        return mConnectionList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.dcc_file_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DCCFileConnection connection = mConnectionList.get(position);

        holder.titleView.setText(connection.getFileName());
        holder.progressView.setText(mContext.getString(R.string.dcc_progress_complete,
                connection.getProgress()));
    }

    public void replaceAll(final Collection<DCCFileConnection> fileConnections) {
        mConnectionList.clear();
        mConnectionList.addAll(fileConnections);
        notifyDataSetChanged();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;

        private final TextView progressView;

        public ViewHolder(final View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.dcc_file_list_item_name);
            progressView = (TextView) itemView.findViewById(R.id.dcc_file_list_item_progress);
        }
    }

}
