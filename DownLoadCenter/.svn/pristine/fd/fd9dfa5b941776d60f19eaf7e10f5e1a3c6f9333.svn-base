package com.konka.downloadcenter.manager;

import com.konka.downloadcenter.database.TasksManagerDBController;
import com.konka.downloadcenter.domain.TaskManagerModel;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by xiaowu on 2016-5-20.
 */
public class TaskManager {


    private final static class HolderClass {
        private final static TaskManager INSTANCE
                = new TaskManager();
    }

    public static TaskManager getImpl() {
        return HolderClass.INSTANCE;

    }

    private List<TaskManagerModel> mTaskList;
    private TasksManagerDBController mController;
    private FileDownloadConnectListener listener;
    public TaskManager() {
        mController=new TasksManagerDBController();
        initData();
        mTaskList=mController.getAllTasks();

    }
    public int getTaskCount()
    {
        return mTaskList.size();
    }
    public  TaskManagerModel  get(int position)
    {
        return mTaskList.get(position);
    }
    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }
    public int getStatus(final  int id)
    {
        return FileDownloader.getImpl().getStatus(id);
    }
    public int getSoFar(final  int id)
    {
        return (int) FileDownloader.getImpl().getSoFar(id);
    }
    public int getTotal(final  int id)
    {
        return (int) FileDownloader.getImpl().getTotal(id);
    }

    public boolean deleteTask(final int id,final int position)
    {
           boolean success=mController.deleteTask(id);
          if (success)
          {
             mTaskList.remove(position);
          }
        return success;
    }

    public void onCreate(final WeakReference<MainActivity> activityWeakReference) {
        FileDownloader.getImpl().bindService();
        if (listener != null) {
            FileDownloader.getImpl().removeServiceConnectListener(listener);
        }
        listener = new FileDownloadConnectListener() {
            @Override
            public void connected() {
                if (activityWeakReference == null
                        || activityWeakReference.get() == null) {
                    return;
                }

                activityWeakReference.get().postNotifyDataChanged();
            }
            @Override
            public void disconnected() {
                if (activityWeakReference == null
                        || activityWeakReference.get() == null) {
                    return;
                }

                activityWeakReference.get().postNotifyDataChanged();
            }
        };

        FileDownloader.getImpl().addServiceConnectListener(listener);
    }


    public void initData()
    {
        mController.addTask("http://download.chinaunix.net/down.php?id=10608&ResourceID=5267&site=1","data/data/filedownfiles/test1","test1");
        mController.addTask("http://180.153.105.144/dd.myapp.com/16891/E2F3DEBB12A049ED921C6257C5E9FB11.apk","data/data/filedownfiles/test2","test1");
        mController.addTask("http://7xjww9.com1.z0.glb.clouddn.com/Hopetoun_falls.jpg","data/data/filedownfiles/test3","test1");

    }
}
