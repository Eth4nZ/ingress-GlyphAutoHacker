package com.yivanus.hack;
//主设置界面与用户接口逻辑

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends ActionBarActivity {
    private Switch sh;
    private Switch addpoint;
    private Switch gs;
    private Button start;
    private EditText edt;
    private EditText tedit;
    private EditText screen;
    private EditText esize;
    private EditText xscale;
    private EditText yscale;
    private boolean save = false;
    private ImageView imageView;
    private int width = 1;
    private boolean autoplay = false;
    private boolean adp = false;
    private int speed = 2;
    private RatingBar speedbar;
    NotificationManager mNotificationManager;
    private boolean guiji = true;
    private boolean ready = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt = (EditText) findViewById(R.id.editText);
        tedit = (EditText) findViewById(R.id.sleeptime);
        screen = (EditText) findViewById(R.id.screen);
        esize = (EditText) findViewById(R.id.psize);
        start = (Button) findViewById(R.id.start);
        imageView = (ImageView) findViewById(R.id.image);
        sh = (Switch) findViewById(R.id.autocheck);

        xscale = (EditText) findViewById(R.id.xscale);
        yscale = (EditText) findViewById(R.id.yscale);
        addpoint = (Switch) findViewById(R.id.addpoint);

        SharedPreferences sp = getSharedPreferences("ingress", Activity.MODE_APPEND);
        autoplay = sp.getBoolean("autoplay", false);
        guiji = sp.getBoolean("guiji",true);
        adp = sp.getBoolean("adp", false);
        edt.setText(String.valueOf(sp.getInt("startx", 1)));
        tedit.setText(String.valueOf(sp.getInt("sleeptime", 1100)));
        screen.setText(String.valueOf(sp.getInt("screen", 1)));
        esize.setText(String.valueOf(sp.getInt("psize", 28)));
        xscale.setText(String.valueOf(sp.getFloat("xscale", 1)));
        yscale.setText(String.valueOf(sp.getFloat("yscale", 1)));

        addpoint.setChecked(adp);
        gs = (Switch) findViewById(R.id.guiji);
        gs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    guiji = true;
                }else{
                    guiji = false;
                }
            }
        });
        gs.setChecked(guiji);
        if (autoplay) {
//            screen.setEnabled(true);
            esize.setEnabled(true);
//            xscale.setEnabled(true);
//            yscale.setEnabled(true);
//            addpoint.setEnabled(true);
//            gs.setEnabled(true);
        } else {
//            screen.setEnabled(false);
            esize.setEnabled(false);
//            xscale.setEnabled(false);
//            yscale.setEnabled(false);
//            addpoint.setEnabled(false);
//            gs.setEnabled(false);
        }
        addpoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adp = true;
                } else {
                    adp = false;
                }
            }
        });
        speedbar = (RatingBar) findViewById(R.id.speedBar);
        speedbar.setMax(3);
        speedbar.setRating(Integer.valueOf(sp.getInt("speed",1)));
        speedbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v == 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.speedfast), Toast.LENGTH_SHORT).show();
//                    speed = 0;
                } else {
//                    speed = (int) v;
                    Toast.makeText(getBaseContext(),getString(R.string.speed) + speed, Toast.LENGTH_SHORT).show();
                }
                speed = (int)v;
            }
        });

        sh.setChecked(autoplay);

        sh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle(getString(R.string.alert));
                    alertDialog.setMessage(getString(R.string.message));
                    alertDialog.setNegativeButton(getString(R.string.donotfly), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            autoplay = false;
                            sh.setChecked(false);
                            dialogInterface.dismiss();

                        }
                    });
                    alertDialog.setPositiveButton(getString(R.string.fly), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            autoplay = true;
//                            screen.setEnabled(true);
                            esize.setEnabled(true);
//                            xscale.setEnabled(true);
//                            yscale.setEnabled(true);
//                            addpoint.setEnabled(true);
//                            gs.setEnabled(true);
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.create().show();
                } else {
                    autoplay = false;
//                    screen.setEnabled(false);
                    esize.setEnabled(false);
//                    xscale.setEnabled(false);
//                    yscale.setEnabled(false);
//                    addpoint.setEnabled(false);
//                    gs.setEnabled(false);
                }
            }
        });


//        width = getWindowManager().getDefaultDisplay().getWidth();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savesp();
                save = true;
                Intent intent = new Intent(getBaseContext(), FloatingService.class);
                startService(intent);
                finish();
            }
        });
        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent("com.yivanus.hack");
                intent2.putExtra("message", "stop");
                sendBroadcast(intent2);
//                mNotificationManager.cancel(0);
            }
        });
        Button testpic = (Button) findViewById(R.id.testpic);
        testpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "autohack/test.png");
                if (file.exists()) {
                    String s = edt.getText().toString();
                    String st = tedit.getText().toString();
//                    if (s.isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "请先测试出正确的屏幕的数据", Toast.LENGTH_SHORT).show();
//                    } else {
                    int startx = Integer.valueOf(s);
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + "autohack/test.png");
                    width = bitmap.getWidth();
                    Bitmap scalebitmap = Bitmap.createBitmap(bitmap, 0, startx, width, width);
                    Bitmap newmap = Bitmap.createScaledBitmap(scalebitmap, 110, 110, false);
                    imageView.setImageBitmap(newmap);
                    scalebitmap.recycle();
                    bitmap.recycle();
//                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
//                    Toast.makeText(getApplicationContext(), "请先在游戏里面截图一张图保存到sd卡的autohack目录test.png", Toast.LENGTH_LONG).show();
                }
            }
        });

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "autohack");
        System.out.println(folder.toString());
        boolean success;
        if (!folder.exists()) {
            success = folder.mkdirs();
//            if (success) {
////                Toast.makeText(MainActivity.this, "目录创建完成", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(MainActivity.this, "目录创建失败", Toast.LENGTH_SHORT).show();
//            }d
        }
        File pic = new File(Environment.getExternalStorageDirectory() + File.separator + "autohack/test.png");
        if(pic.exists()){
            ready = true;
        }else{
            ready = false;
        }
        final File execfile = new File(getFilesDir().getPath() + File.separator + "eventserver");
        if (!execfile.exists()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        copyBigDataToSD();
                        execfile.setExecutable(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
    private void fileChannelCopy(File s, File t) {

        FileInputStream fi = null;

        FileOutputStream fo = null;

        FileChannel in = null;

        FileChannel out = null;

        try {

            fi = new FileInputStream(s);

            fo = new FileOutputStream(t);

            in = fi.getChannel();//得到对应的文件通道

            out = fo.getChannel();//得到对应的文件通道

            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                fi.close();

                in.close();

                fo.close();

                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private void copyBigDataToSD() throws IOException {
        OutputStream myOutput = this.openFileOutput("eventserver", Context.MODE_PRIVATE);
        InputStream myInput = this.getAssets().open("eventserver");
        byte[] buffer = new byte[1024];
        int byteCount;
        while ((byteCount = myInput.read(buffer)) != -1) {//循环从输入流读取 buffer字节
            System.out.println(byteCount);
            myOutput.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
    private void savesp() {
        SharedPreferences sp = getSharedPreferences("ingress", Activity.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("startx", Integer.valueOf(edt.getText().toString()));
        editor.putInt("width", width);
        editor.putBoolean("guiji",guiji);
        editor.putInt("sleeptime", Integer.valueOf(tedit.getText().toString()));
        editor.putInt("screen", Integer.valueOf(screen.getText().toString()));
        editor.putBoolean("autoplay", autoplay);
        editor.putInt("psize", Integer.valueOf(esize.getText().toString()));
        editor.putBoolean("adp", adp);
        editor.putFloat("xscale", Float.valueOf(xscale.getText().toString()));
        editor.putFloat("yscale", Float.valueOf(yscale.getText().toString()));
        editor.putInt("speed",speed);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (!save) {
            savesp();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (autoplay && ready) {
            if (id == R.id.action_settings) {
                Intent intent = new Intent(getApplicationContext(), settingActivity.class);
                intent.putExtra("startx", Integer.valueOf(edt.getText().toString()));
                intent.putExtra("psize", Integer.valueOf(esize.getText().toString()));
                startActivity(intent);
            } else if (id == R.id.setdraw) {
                Intent intent = new Intent(getApplicationContext(), drawseting.class);
                intent.putExtra("startx", Integer.valueOf(edt.getText().toString()));
                intent.putExtra("eventid", Integer.valueOf(screen.getText().toString()));
                intent.putExtra("speed", speed);
                intent.putExtra("psize",Integer.valueOf(esize.getText().toString()));
                startActivity(intent);
            }
        } else {
            if(!ready){
                Toast.makeText(getBaseContext(),getString(R.string.selectpic),Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getBaseContext(), getString(R.string.needopenauto), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "autohack/test.png");
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                outputStream.flush();
                outputStream.close();
                int startx = Integer.valueOf(edt.getText().toString());
                Bitmap scalebitmap = Bitmap.createBitmap(bitmap, 0, startx, width, width);
                Bitmap newmap = Bitmap.createScaledBitmap(scalebitmap, 110, 110, false);
                imageView.setImageBitmap(newmap);
                scalebitmap.recycle();
                bitmap.recycle();
                ready=true;
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    public void showButtonNotify() {
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.noti);
//        Intent stopIntent = new Intent("com.yivanus.stopserve");
//        PendingIntent intent_stop = PendingIntent.getBroadcast(this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mRemoteViews.setOnClickPendingIntent(R.id.stopserve, intent_stop);
//        Intent startIntent = new Intent("com.yivanus.hack.startserve");
//        PendingIntent intent_start = PendingIntent.getService(this, 1, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mRemoteViews.setOnClickPendingIntent(R.id.startserve, intent_start);
//        mBuilder.setContent(mRemoteViews).setSmallIcon(R.mipmap.ic_launcher);
//        Notification notify = mBuilder.build();
//        notify.flags |= Notification.FLAG_ONGOING_EVENT;
//        notify.flags |= Notification.FLAG_NO_CLEAR;
//        mNotificationManager.notify(0, notify);
//    }
}
