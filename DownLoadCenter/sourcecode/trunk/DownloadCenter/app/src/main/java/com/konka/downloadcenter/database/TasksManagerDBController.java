package com.konka.downloadcenter.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.konka.downloadcenter.APP;
import com.konka.downloadcenter.domain.TaskManagerModel;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaowu on 2016-5-19.
 */
public class TasksManagerDBController {
    private static final String TAG = "DBController";
    public final static String TABLE_NAME = "tasksmanger";

    private final SQLiteDatabase database;


    public TasksManagerDBController() {
        TaskManagerDBOpenHelper openHelper=new TaskManagerDBOpenHelper(APP.CONTEXT);

        database = openHelper.getWritableDatabase();
    }

    public List<TaskManagerModel> getAllTasks(){
        ArrayList<TaskManagerModel> tasklist=new ArrayList<TaskManagerModel>();

        Cursor cursor=database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        try  {
            if (!cursor.moveToLast()){
                return tasklist;
            }
            do
            {
                TaskManagerModel model=new TaskManagerModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(TaskManagerModel.ID)));
                model.setName(cursor.getString(cursor.getColumnIndex(TaskManagerModel.NAME)));
                model.setPath(cursor.getString(cursor.getColumnIndex(TaskManagerModel.PATH)));
                model.setUrl(cursor.getString(cursor.getColumnIndex(TaskManagerModel.URL)));
//                model.setSofarbytes(cursor.getInt(cursor.getColumnIndex(TaskManagerModel.SOFARBYTES)));
//                model.setTotalbytes(cursor.getInt(cursor.getColumnIndex(TaskManagerModel.TOTALBYTES)));
                tasklist.add(model);
            }while(cursor.moveToPrevious());

        }finally {
            if (cursor!=null)
            {
                cursor.close();
            }
        }

        return tasklist;

    }

    public TaskManagerModel addTask(final String url, final String path, final String name)
    {
        if (TextUtils.isEmpty(url)||TextUtils.isEmpty(path))
        {
            return null;
        }
        int id= FileDownloadUtils.generateId(url,path);

        TaskManagerModel model=new TaskManagerModel();
        model.setId(id);
        model.setUrl(url);
        model.setPath(path);
        model.setName(name);

        boolean succeed=database.insert(TABLE_NAME,null,model.toCotentValues())!=-1;
        Log.d(TAG, "addTask: database"+succeed);
        return succeed ? model:null;
    }

    public boolean deleteTask(int Id)
    {
        boolean succeed=database.delete(TABLE_NAME,"id=?",new String[]{"Id"})!=-1;
        return succeed;
    }



}
