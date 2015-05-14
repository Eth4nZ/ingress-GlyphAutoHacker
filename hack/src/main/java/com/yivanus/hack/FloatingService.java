package com.yivanus.hack;
//悬浮窗口相关的代码以及用户接口相关

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FloatingService extends Service {

    Vibrator vibrator;
    private int statusBarHeight;// 状态栏高度
    private View floatview;// 透明窗体
    private boolean viewAdded = false;// 透明窗体是否已经显示
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private StatusReceiver statusReceiver;
    private ImageButton cap;
    private ImageButton restart;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;

    private ImageView resimage1;
    private ImageView resimage2;
    private ImageView resimage3;
    private ImageView resimage4;
    private ImageView resimage5;

    private Point[] points;
    private int count = 0;
    private Handler mHandler;
    private int startx = 1;
    private int width = 1;
    private int sleeptime = 1100;

    private List glnames;
    private Glyph glyph;
    private boolean autoplay = false;
    private int background = 0;
    private int screen = 1;
    private int psize = 28;
    private float xscale;
    private float yscale;
    private boolean adp = false;
    private boolean guiji = false;
    Handler handler;
    Runnable runnable;
    draws ds;

    NotificationManager mNotificationManager;

    //    private MyTask myTask;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.noti);
        Intent stopIntent = new Intent("com.yivanus.hack");
        stopIntent.putExtra("message", "hide");
        PendingIntent intent_stop = PendingIntent.getBroadcast(this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.stopserve, intent_stop);
        Intent startIntent = new Intent("com.yivanus.hack");
        startIntent.putExtra("message", "show");
        PendingIntent intent_start = PendingIntent.getBroadcast(this, 2, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.startserve, intent_start);
        mBuilder.setContent(mRemoteViews).setSmallIcon(R.mipmap.ic_launcher);
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_ONGOING_EVENT;
        notify.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(1, notify);
        count = 0;
        floatview = LayoutInflater.from(this).inflate(R.layout.floatview, null);
        image1 = (ImageView) floatview.findViewById(R.id.image1);
        image2 = (ImageView) floatview.findViewById(R.id.image2);
        image3 = (ImageView) floatview.findViewById(R.id.image3);
        image4 = (ImageView) floatview.findViewById(R.id.image4);
        image5 = (ImageView) floatview.findViewById(R.id.image5);

        resimage1 = (ImageView) floatview.findViewById(R.id.resimage1);
        resimage2 = (ImageView) floatview.findViewById(R.id.resimage2);
        resimage3 = (ImageView) floatview.findViewById(R.id.resimage3);
        resimage4 = (ImageView) floatview.findViewById(R.id.resimage4);
        resimage5 = (ImageView) floatview.findViewById(R.id.resimage5);

        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //此处返回截图的名字
                Bundle bundle = msg.getData();
                final String filename = bundle.getString("filename");
                if (!filename.isEmpty()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + "autohack" + File.separator + filename + ".png");
                    Bitmap scalebitmap = Bitmap.createBitmap(bitmap, 0, startx, width, width);
                    Bitmap newmap = Bitmap.createScaledBitmap(scalebitmap, 110, 110, false);
                    //自动画模式关闭时第5个图案需要自动关闭定时任务
                    if (count == 4) {
                        handler.removeCallbacks(runnable);
                    }
                    //只有打开自动画时才处理判断图形中是有正确的点
                    if (autoplay) {
                        String sb = glyph.getpoint(scalebitmap);
                        if (sb.equals("ABCDEFGHIJK") || sb.equals("DE")) {
                            //根据图案的关键点判断图案结束
                            count = 5;//跳过结束的特殊图
                            handler.removeCallbacks(runnable);
                        } else {
//                            if (autoplay) {
                                //自动画模式打开时根据图片中获取的关键点,去判断图案序列
                                String sb1 = glyph.ingressglpyh(scalebitmap, sb);
                                //转换图案序列
                                String n = glyph.converString(sb1);
                                //保存在glnames,最后图案结束时取出
                                glnames.add(n);
                                int id;
                                if (n.isEmpty()) {
                                    //不认识的图案
                                    id = R.mipmap.empty;
                                } else {
                                    //根据识别出来的图案名字获取相应的图片
                                    id = getResources().getIdentifier(n.toLowerCase(), "drawable", getPackageName());
                                }
                                switch (count) {
                                    case 0:
                                        resimage1.setImageResource(id);
                                        break;
                                    case 1:
                                        resimage2.setImageResource(id);
                                        break;
                                    case 2:
                                        resimage3.setImageResource(id);
                                        break;
                                    case 3:
                                        resimage4.setImageResource(id);
                                        break;
                                    case 4:
                                        resimage5.setImageResource(id);
                                        break;
                                    default:
                                        break;
                                }
//                            }
                        }
                    }else{
                        try {
                            Thread.sleep(50);
                        }catch (Exception e){

                        }
                    }
                    //设置识别结果图片
                    switch (count) {
                        case 0:
                            image1.setImageBitmap(newmap);
                            break;
                        case 1:
                            image2.setImageBitmap(newmap);
                            break;
                        case 2:
                            image3.setImageBitmap(newmap);
                            break;
                        case 3:
                            image4.setImageBitmap(newmap);
                            break;
                        case 4:
                            image5.setImageBitmap(newmap);
                            break;
                        default:
                            break;
                    }
                    count += 1;
//                    if (sleeptime == 0 && count == 5) {
//                        //自动模式
//                        count = 0;
//                    }
                    //图片占用释放
                    bitmap.recycle();
                    scalebitmap.recycle();
//                    达到最后一个或者已经是结束图案
                    if (count >= 5 && autoplay) {
                        Thread threaddraw = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (count == 5) {
                                    try {
                                        Thread.sleep(sleeptime - 100);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    Thread.sleep(sleeptime - 300);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                System.out.println("begin draw the glyph.");
                                //循环画图案
                                if (!guiji) {
                                    for (int i = 0; i < glnames.size(); i++) {
                                        String gl = glnames.get(i).toString();
                                        String cmds = glyph.getglyph(gl);
                                        Boolean b = RootCmd(cmds);
                                        System.out.println(b);
                                        try {
                                            Thread.sleep(70);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    ArrayList tmpfiles = new ArrayList();
                                    for (int i = 0; i < glnames.size(); i++) {
                                        tmpfiles.add("/sdcard/autohack/" + glnames.get(i).toString() + ".ingress");
                                    }
                                    ds.rundraw(tmpfiles);
                                }
                                glnames.clear();
                                count = 0;
                            }
                        });
                        threaddraw.start();
                    }
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "autohack" + File.separator + filename + ".png");
                    f.delete();
                } else {
                    Toast.makeText(getBaseContext(), "截图失败了", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
        //截图开始按钮
        cap = (ImageButton) floatview.findViewById(R.id.test);
        cap.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(20);
//                if (sleeptime != 0) {
//                    count = 0;
                    image1.setImageBitmap(null);
                    image2.setImageBitmap(null);
                    image3.setImageBitmap(null);
                    image4.setImageBitmap(null);
                    image5.setImageBitmap(null);
                    resimage1.setImageBitmap(null);
                    resimage2.setImageBitmap(null);
                    resimage3.setImageBitmap(null);
                    resimage4.setImageBitmap(null);
                    resimage5.setImageBitmap(null);
//                }
//                if (sleeptime == 0 && count == 0) {
//                    image1.setImageBitmap(null);
//                    image2.setImageBitmap(null);
//                    image3.setImageBitmap(null);
//                    image4.setImageBitmap(null);
//                    image5.setImageBitmap(null);
//                }
                if (sleeptime != 0) {
                    handler.postDelayed(runnable, 1);
                } else {
                    //手动模式
                    try {
                        MyTask myTask = new MyTask();
                        myTask.execute("autoHack");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//            清除,自动模式时可用于暂停自动画功能,实现手动画,
//            自动画模式关闭时,
//            可用于清空上次遗留的截图
        restart = (ImageButton) floatview.findViewById(R.id.iid);
        restart.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                count = 0;
//                if (autoplay) {
                    handler.removeCallbacks(runnable);
                    glnames.clear();
                    resimage1.setImageBitmap(null);
                    resimage2.setImageBitmap(null);
                    resimage3.setImageBitmap(null);
                    resimage4.setImageBitmap(null);
                    resimage5.setImageBitmap(null);
//                } else {
                    image1.setImageBitmap(null);
                    image2.setImageBitmap(null);
                    image3.setImageBitmap(null);
                    image4.setImageBitmap(null);
                    image5.setImageBitmap(null);
//                }
            }
        });
        //注册通知,可从通知栏关闭服务
        statusReceiver = new StatusReceiver();

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("com.yivanus.hack");
        registerReceiver(statusReceiver, intentFilter);

        //以下为悬浮窗主要代码
        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        /*
         * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
         * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
         * PixelFormat.TRANSPARENT：悬浮窗透明
         */
        layoutParams = new

                LayoutParams();

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//关键
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;//关键
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        floatview.setOnTouchListener(new View.OnTouchListener() {
            float[] temp = new float[]{0f, 0f};

            public boolean onTouch(View v, MotionEvent event) {
                layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN: // 按下事件，记录按下时手指在悬浮窗的XY坐标值
                        temp[0] = event.getX();
                        temp[1] = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        refreshView((int) (event.getRawX() - temp[0]),
                                (int) (event.getRawY() - temp[1]));
                        break;
                }
                return false;
            }
        });
    }


    /**
     * 刷新悬浮窗
     *
     * @param x 拖动后的X轴坐标
     * @param y 拖动后的Y轴坐标
     */
    public void refreshView(int x, int y) {
        //状态栏高度不能立即取，不然得到的值是0
        if (statusBarHeight == 0) {
            View rootView = floatview.getRootView();
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            statusBarHeight = r.top;
        }
        layoutParams.x = x;
        // y轴减去状态栏的高度，因为状态栏不是用户可以绘制的区域，不然拖动的时候会有跳动
        layoutParams.y = y - statusBarHeight;//STATUS_HEIGHT;
        refresh();
    }


    /**
     * 添加悬浮窗或者更新悬浮窗 如果悬浮窗还没添加则添加 如果已经添加则更新其位置
     */
    private void refresh() {
        if (viewAdded) {
            windowManager.updateViewLayout(floatview, layoutParams);
        } else {
            windowManager.addView(floatview, layoutParams);
            viewAdded = true;
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        SharedPreferences sp = getSharedPreferences("ingress", Activity.MODE_APPEND);
        int speedx = sp.getInt("speed", 1);
        width = sp.getInt("width", 1);
        startx = sp.getInt("startx", 1);
        sleeptime = sp.getInt("sleeptime", 1100);
        autoplay = sp.getBoolean("autoplay", true);
        screen = sp.getInt("screen", 1);
        background = sp.getInt("background", 0);
        adp = sp.getBoolean("adp", false);
        psize = sp.getInt("psize", 28);
        xscale = sp.getFloat("xscale", 1.0F);
        yscale = sp.getFloat("yscale", 1.0F);
        guiji = sp.getBoolean("guiji", false);
        int dsleep = sp.getInt("dsleep", 100);
        points = new Point[11];
        glnames = new ArrayList();
        ds = new draws(speedx, screen, dsleep);
        if (autoplay) {
            Toast.makeText(getBaseContext(), "已开启", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "未开启", Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < points.length; i++) {
            int x = sp.getInt(i + "x", 0);
            int y = sp.getInt(i + "y", 0);
            points[i] = new Point(x, y);
        }
        glyph = new Glyph();
        glyph.init(points, startx, background, screen, psize, xscale, yscale, adp);
        refresh();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (count<5) {
                    MyTask myTask = new MyTask();
                    myTask.execute("autoHack");
                    // TODO Auto-generated method stub
                    //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                    handler.postDelayed(this, sleeptime);
                }
            }
        };
    }

    /**
     * 关闭悬浮窗
     */
    public void removeView() {
        if (viewAdded) {
            windowManager.removeView(floatview);
            viewAdded = false;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(statusReceiver);
        removeView();
        super.onDestroy();
    }

    public boolean RootCmd(String cmd) {
        Process process = null;
        DataOutputStream os = null;
        if (cmd.isEmpty()){
            try {
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.flush();
            os.writeBytes("exit" + "\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    class StatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(intent.getAction().toString());
            if (intent.getAction().toString().equals("com.yivanus.hack")) {
                String message = intent.getStringExtra("message");
                if (message.equals("show")) {
                    refresh();
                } else if (message.equals("hide")) {
                    removeView();
                } else if (message.equals("stop")) {
                    stopSelf();
                }
            }
        }
    }

    public class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String filename = String.valueOf(System.currentTimeMillis());
            String capImage = Environment.getExternalStorageDirectory() + File.separator + "autohack" + File.separator + filename + ".png";
            String capcmd = "/system/bin/screencap -p " + capImage;
            boolean b = RootCmd(capcmd);
            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            if (b) {
                bundle.putString("filename", filename);
            } else {
                bundle.putString("filename", "");
            }
            msg.setData(bundle);
            msg.sendToTarget();
            return "";
        }
    }
}


