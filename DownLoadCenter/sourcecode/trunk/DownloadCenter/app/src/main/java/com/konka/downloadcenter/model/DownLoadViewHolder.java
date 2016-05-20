package com.konka.downloadcenter.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.konka.downloadcenter.R;

public class DownLoadViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_name;
        public TextView tv_size;
        public TextView tv_state;
        public TextView tv_completed;
        public TextView tv_speed;

        public DownLoadViewHolder(final View itemView) {
            super(itemView);
            tv_name= (TextView) itemView.findViewById(R.id.tv_name);
            tv_size= (TextView) itemView.findViewById(R.id.tv_size);
            tv_state= (TextView) itemView.findViewById(R.id.tv_state);
            tv_completed= (TextView) itemView.findViewById(R.id.tv_completed);
            tv_speed= (TextView) itemView.findViewById(R.id.tv_speed);
        }


    public void updateDownloading(int status, int soFar, int total) {

    }
}