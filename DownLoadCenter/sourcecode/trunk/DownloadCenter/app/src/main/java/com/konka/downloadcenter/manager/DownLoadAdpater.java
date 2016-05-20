package com.konka.downloadcenter.manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konka.downloadcenter.R;
import com.konka.downloadcenter.domain.TaskManagerModel;
import com.konka.downloadcenter.model.DownLoadItem;
import com.konka.downloadcenter.model.DownLoadViewHolder;
import com.konka.downloadcenter.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.util.ArrayList;

/**
 * Created by xiaowu on 2016-5-16.
 */
public class DownLoadAdpater extends RecyclerView.Adapter<DownLoadViewHolder> {
    private static final String TAG = "DownLoadAdpater";
    private Context mContext;
    private ArrayList<DownLoadItem> mList;
    private int mCurItemPosition;//当前item的位置
    private OnItemFocusListener onItemFocusListener;
    private OnKeyEventListener onKeyEventListener;
    public  FileDownloadSampleListener  fileDownloadListener=new FileDownloadSampleListener(){

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            DownLoadViewHolder holder= (DownLoadViewHolder) task.getTag();
            int progress=soFarBytes*100/totalBytes;
            holder.tv_completed.setText(progress+"%");
            holder.tv_state.setText(R.string.downloading);
        }
        @Override
        protected void completed(BaseDownloadTask task) {
            DownLoadViewHolder holder= (DownLoadViewHolder) task.getTag();
            holder.tv_state.setText(R.string.completed);
            holder.tv_completed.setText("");
        }
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            DownLoadViewHolder holder= (DownLoadViewHolder) task.getTag();
            holder.tv_state.setText(R.string.wait_download);
//                viewHolder.tv_completed.setText("");
        }
        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            DownLoadViewHolder holder= (DownLoadViewHolder) task.getTag();
            holder.tv_state.setText(R.string.pause);
            holder.tv_completed.setText("");
        }
        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            DownLoadViewHolder holder= (DownLoadViewHolder) task.getTag();
            holder.tv_state.setText(R.string.failed);
            holder.tv_completed.setText("");
        }
    };

    public DownLoadAdpater(Context context, ArrayList<DownLoadItem> list)
    {
        mContext=context;
        mList=list;




    }

    public void setOnKeyEventListener(OnKeyEventListener onKeyEventListener) {
        this.onKeyEventListener = onKeyEventListener;
    }

    public void setOnItemFocusListener(OnItemFocusListener onItemFocusListener) {
        this.onItemFocusListener = onItemFocusListener;
    }

    //创建View，被layoutmannager所调用
    @Override
    public DownLoadViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(R.layout.downitem,parent,false);
        final DownLoadViewHolder viewHolder=new DownLoadViewHolder(itemView);
        itemView.setTag(viewHolder);
        itemView.setFocusable(true);
        itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (onItemFocusListener!=null)
                    onItemFocusListener.OnItemFocus(view,b);

            }
        });
        itemView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {

                if (onKeyEventListener != null) {

                    return  onKeyEventListener.OnKeyEvent(view,keycode,keyEvent,mCurItemPosition);
                }
                return false;
            }
        });

        return viewHolder;
    }
    //将数据与界面进行绑定
    @Override
    public void onBindViewHolder(DownLoadViewHolder holder, int position) {
        mCurItemPosition=position;
        TaskManagerModel model=TaskManager.getImpl().get(position);

        BaseDownloadTask baseDownloadTask= FileDownloader.getImpl().create(model.getUrl())
                                                                    .setPath(model.getPath())
                                                                    .setCallbackProgressTimes(500)
                                                                    .setListener(fileDownloadListener);
          baseDownloadTask.setTag(holder);
          baseDownloadTask.start();
//        DownLoadItem item=new DownLoadItem();
//        item=mList.get(position);
        holder.tv_name.setText(model.getName());
        holder.tv_size.setText(Utils.getDataSize(model.getTotalbytes()));

        if (TaskManager.getImpl().isReady()) {
            final int status = TaskManager.getImpl().getStatus(model.getId());
            switch (status) {
                case FileDownloadStatus.pending:
                case FileDownloadStatus.started:
                case FileDownloadStatus.connected:
                    holder.tv_state.setText(R.string.wait_download);
                    break;
                case FileDownloadStatus.progress:
                    holder.tv_state.setText(R.string.downloading);
//                    holder.tv_completed.setText((TaskManager.getImpl().getSoFar(model.getId())*100)/(TaskManager.getImpl().getTotal(model.getId()))+"%");
                    break;
                case FileDownloadStatus.paused:
                    holder.tv_state.setText(R.string.pause);
                    break;
                case FileDownloadStatus.completed:
                    holder.tv_state.setText(R.string.completed);
                    break;
                case FileDownloadStatus.error:
                    holder.tv_state.setText(R.string.failed);
                    break;
            }

        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    interface OnItemFocusListener{
        void OnItemFocus(View view, boolean hasFocus);
    }

    interface OnKeyEventListener{
        boolean OnKeyEvent(View view, int keycode, KeyEvent keyEvent,int position);
    }


}
