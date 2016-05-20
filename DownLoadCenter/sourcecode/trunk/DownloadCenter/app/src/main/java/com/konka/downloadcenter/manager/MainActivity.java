package com.konka.downloadcenter.manager;

import android.animation.Animator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konka.android.kkui.lib.KKActivity;
import com.konka.android.kkui.lib.KKButton;
import com.konka.android.kkui.lib.KKDialog;
import com.konka.android.kkui.lib.KKToast;
import com.konka.downloadcenter.R;
import com.konka.downloadcenter.domain.TaskManagerModel;
import com.konka.downloadcenter.model.DownLoadItem;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends KKActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView  mTitle;
    private DownLoadAdpater mAdapter;
    private ArrayList<DownLoadItem> mList =new ArrayList<DownLoadItem>();
    private View mCurrentItemView;//当前选中item

    //menu
    private LinearLayout ll_menu;
    private KKButton btn_stop;
    private KKButton btn_del;
    private boolean once=true;
    private int mCurrentItemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        TaskManager.getImpl().onCreate(new WeakReference<>(this));
    }

    private void initView() {
        mTitle= (TextView) findViewById(R.id.tv_title);
        ll_menu= (LinearLayout) findViewById(R.id.menu);
        btn_stop= (KKButton)findViewById(R.id.btn_stop);
        btn_del= (KKButton) findViewById(R.id.btn_del);
        mRecyclerView= (RecyclerView) findViewById(R.id.rc_downloadlist);
        initRecyclerView();
        initBtnListener();
    }

    private void initBtnListener() {
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
//        btn_del.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
//                switch (keycode)
//                {
//                    case KeyEvent.KEYCODE_DPAD_DOWN:
//                        btnFocus_HandleMenu();
//                        break;
//                    case KeyEvent.KEYCODE_DPAD_UP:
//                        btnFocus_HandleMenu();
//                        break;
//                    case KeyEvent.KEYCODE_BACK:
//                       if (ll_menu.getVisibility()==View.VISIBLE)
//                       {
//                           btnFocus_HandleMenu();
//                           return true;
//                       }
//                        break;
//                }
//
//                return false;
//            }
//        });

        View.OnKeyListener btnOnKeyListener=new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                switch (keycode)
                {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        btnFocus_HandleMenu();
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        btnFocus_HandleMenu();
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        if (ll_menu.getVisibility()==View.VISIBLE)
                        {
                            btnFocus_HandleMenu();
                            return true;
                        }
                        break;
                }
                return false;
            }
        };
        btn_del.setOnKeyListener(btnOnKeyListener);
        btn_stop.setOnKeyListener(btnOnKeyListener);

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              CharSequence action=btn_stop.getText();
                if (action.equals(getResources().getString(R.string.pause)))
                {
                    FileDownloader.getImpl().pause(TaskManager.getImpl().get(mCurrentItemPosition).getId());
                    btn_stop.setText(R.string.continue_download);
                }
                if(action.equals(getResources().getString(R.string.continue_download)))
                {
                    btn_stop.setText(R.string.pause);
                    final TaskManagerModel model = TaskManager.getImpl().get(mCurrentItemPosition);
                    final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                            .setPath(model.getPath())
                            .setCallbackProgressTimes(100);
                    task.setTag(mCurrentItemView.getTag());
                    task.setListener(mAdapter.fileDownloadListener);
                    task.start();
                }
            }
        });
    }
    public void btnFocus_HandleMenu()
    {
        setMenuVisibility(View.GONE);

        reviseFocusBlockPositionAndSize(mCurrentItemView,new MyFocusAnimatorListener(){
            @Override
            public void onAnimationEnd(View view, Animator animator) {
                mCurrentItemView.setFocusable(true);
                mCurrentItemView.requestFocus();
//                Log.d(TAG, "onAnimationEnd: CurFocus  "+mCurrentItemView.findFocus());
            }
        });
    }

    private void showConfirmDialog() {
        final KKDialog.Builder builder=new KKDialog.Builder(this);
        builder.setMessage("确定要中止下载并删除该任务吗?");
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TaskManagerModel model=TaskManager.getImpl().get(mCurrentItemPosition);
                if (TaskManager.getImpl().deleteTask(model.getId(),mCurrentItemPosition))
                {
                    File file=new File(model.getPath());
                    if (file.exists())
                    {
                        file.delete();
                    }
                    mAdapter.notifyItemRemoved(mCurrentItemPosition);
                }else{
                    KKToast.makeText(MainActivity.this,R.string.delete_failed).show();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void initRecyclerView()
    {
        mLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (mList!=null)
        {
            mAdapter=new DownLoadAdpater(this, mList);
            mRecyclerView.setAdapter(mAdapter);
        }
        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b&&once)
                {

                    if (mRecyclerView.getChildCount()>0)
                    {
                        mRecyclerView.getChildAt(0).requestFocus();
                        once=false;
                    }
                }
            }
        });

//        mAdapter.setOnItemFocusListener(new DownLoadAdpater.OnItemFocusListener() {
//
//            @Override
//            public void OnItemFocus(View view, boolean b, int position) {
//                 if (b)
//                 {
////                     mFocusImage.setVisibility(View.VISIBLE);
//////                     view.setBackgroundColor(Color.BLUE);
////
////                     ViewGroup.MarginLayoutParams marginParams= (ViewGroup.MarginLayoutParams) mFocusImage.getLayoutParams();
////                     marginParams.topMargin=view.getTop()+mTitle.getHeight()+((ViewGroup.MarginLayoutParams)mTitle.getLayoutParams()).topMargin+16;
////                     marginParams.width=1900;
////                     marginParams.height=130;
////                     mFocusImage.setLayoutParams(marginParams);
//
//                 }else{
//                 }
//
//
//            }
//        });

        mAdapter.setOnKeyEventListener(new DownLoadAdpater.OnKeyEventListener() {

            @Override
            public boolean OnKeyEvent(View view, int keycode, KeyEvent keyEvent,int position) {
                mCurrentItemView=view;
                mCurrentItemPosition=position;
//                Log.d(TAG, "OnKeyEvent: position  "+position);
                switch (keycode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        showMenu(view,keyEvent);
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                       if (ll_menu.getVisibility()==View.VISIBLE) {
                           reviseFocusBlockPositionAndSize(btn_stop);
                       }
                        break;
                }
                return false;
            }
        });
    }
    /*
    * 设置Menu的位置，并让暂停/删除按钮获取焦点
    * */
    public void showMenu(View view,KeyEvent keyEvent)
    {
        ll_menu.setVisibility(View.VISIBLE);
        setMenuPosition(view);
        if (keyEvent.getAction()==KeyEvent.ACTION_UP) {
            reviseFocusBlockPositionAndSize(btn_del, new MyFocusAnimatorListener() {
                @Override
                public void onAnimationEnd(View view, Animator animator) {
                    btn_del.setFocusable(true);
                    btn_stop.setFocusable(true);
                    btn_del.requestFocus();
                }
            });
        }
    }
    //设置Menu显示/隐藏
    public void setMenuVisibility(int visibility)
    {
        ll_menu.setVisibility(visibility);
    }
    //设置Menu位置
      public void setMenuPosition(View v)
      {

         ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) ll_menu.getLayoutParams();
          params.topMargin=v.getTop()+mTitle.getHeight()+((ViewGroup.MarginLayoutParams)mTitle.getLayoutParams()).topMargin+
                  ((ViewGroup.MarginLayoutParams)mTitle.getLayoutParams()).bottomMargin+16;
          ll_menu.setLayoutParams(params);
      }


    private void initData()
    {

    }

    public void postNotifyDataChanged() {
        if (mAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

}
