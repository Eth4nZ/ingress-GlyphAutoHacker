package com.yivanus.hack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

//import android.support.v7.app.ActionBarActivity;

//import java.lang.reflect.Field;

public class drawseting extends ActionBarActivity {
    static String[] images = new String[]{"abdfa", "abi", "abigk", "abikjca", "abikjcafk", "acedg", "acefdi", "ad", "adfhkgfea", "adfk", "adi", "aecj", "aedg", "aefgk", "aej", "afdg", "afeh", "afgi", "afgikjhf", "afhef", "afhj", "afhjc", "afk", "afkhca", "baech", "bdae", "bdecjhgib", "bdeh", "bdfec", "bdfgh", "bdfhj", "bdfhjk", "bdg", "bdgfehj", "bdghec", "bdgi", "bg", "bgfecj", "bgkhc", "cefdbik", "cefgi", "cefh", "cehfg", "cehj", "ch", "chf", "chfdi", "chfg", "chfgik", "cjki", "dechg", "defd", "defdacjk", "deh", "dehc", "dehf", "dehfgd", "dehgd", "dehkgd", "dfe", "dfehgk", "dfgi", "dfhefgd", "dfkgd", "dghe", "ecjhfdbig", "edafkg", "edfg", "edfhj", "efdgkjca", "efgk", "efkhe", "ehj", "fdefgi", "fdgfeg", "feadfhj", "fedgh", "fehfdg", "fgdfk", "fgikf", "gdeh", "gfh", "gfkh", "gh", "ghkg", "gi", "hfgk", "hj", "ibacefgk", "idaej", "idej", "idgfeh", "idgh", "idghej", "igdaehj", "igdehj", "igfhj", "ighj", "igkh", "ikj", "kjcabikghk"};
    Bitmap srcBitmap;
    Bitmap bitmappreview;
    ImageView imgtest;
    ImageView imgpreview;
    ImageView show;
    Canvas canvas;
    TextView c;
    TextView a;
    TextView curstatus;
    Button record;
    Button stop;
    Button test;
    Paint paint;
    Paint paintline;
    Paint paintup;
    Path path;
    Path pathline;
    float dx;
    float dy;
    int current = 0;
    draws ds = null;
    int startx = 1;
    int eventid = 1;
    int speed = 1;
    int size = 10;
    int dsleep = 200;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawsettings);
        Intent intent = getIntent();
        startx = intent.getIntExtra("startx", 1);
        eventid = intent.getIntExtra("eventid", 1);
        speed = intent.getIntExtra("speed", 1);
        size = intent.getIntExtra("psize", 5);
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paintline = new Paint();
        paintline.setColor(Color.RED);
        paintline.setStrokeWidth(1);
        paintline.setStyle(Paint.Style.STROKE);
        path = new Path();
        pathline = new Path();
        ds = new draws(speed, eventid, dsleep);
        imgtest = (ImageView) findViewById(R.id.image);
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + "autohack/test.png");
        int width = bitmap.getWidth();
        imgpreview = (ImageView) findViewById(R.id.imgpreview);
        bitmappreview = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        imgpreview.setImageBitmap(bitmappreview);
        canvas = new Canvas(bitmappreview);
        imgpreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bitmappreview.eraseColor(Color.TRANSPARENT);
                    path.reset();
                    pathline.reset();
                    path.moveTo(event.getX(), event.getY());
                    pathline.moveTo(event.getX(),event.getY());
                }
                bitmappreview.eraseColor(Color.TRANSPARENT);
//                canvas.drawPoint(event.getX(),event.getY(),paint);
                pathline.lineTo(event.getX(), event.getY());
                path.addCircle(event.getX(), event.getY(), size - 8, Path.Direction.CW);
//                path.moveTo(event.getX(),event.getY());
                canvas.drawPath(path, paint);
                canvas.drawPath(pathline,paintline);
                imgpreview.setImageBitmap(bitmappreview);
                return true;
            }
        });
        System.out.println(width);
        srcBitmap = Bitmap.createBitmap(bitmap, 0, startx, width, width);
        imgtest.setImageBitmap(srcBitmap);
        show = (ImageView) findViewById(R.id.show);
        show.setImageResource(R.drawable.abdfa);
        a = (TextView) findViewById(R.id.a);
        c = (TextView) findViewById(R.id.c);
        curstatus = (TextView) findViewById(R.id.curstatus);
        checkstatus();
        a.setText(images.length + "");
        c.setText("1");
        record = (Button) findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record.setEnabled(false);
                test.setEnabled(false);
                stop.setEnabled(true);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ds.record(images[current]);
                    }
                });
                thread.start();
            }
        });
        stop = (Button) findViewById(R.id.stoprec);
        stop.setEnabled(false);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ds.stoprec();
                stop.setEnabled(false);
                record.setEnabled(true);
                test.setEnabled(true);
                Toast.makeText(getApplicationContext(),getString(R.string.humanlint)+ds.getDrawtime()+getString(R.string.sec),Toast.LENGTH_SHORT).show();
                System.out.println(ds.getDrawtime());
            }
        });
        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList files = new ArrayList();
                files.add("/sdcard/autohack/" + images[current] + ".ingress");
                ds.rundraw(files);
            }
        });
        Button testall = (Button) findViewById(R.id.testall);
        testall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList tmpfiles = new ArrayList();
                System.out.println("addfiles.");
                for (int i = 0; i < images.length; i++) {
                    tmpfiles.add("/sdcard/autohack/" + images[i] + ".ingress");
                }
                        ds.rundraw(tmpfiles);
            }
        });
        Button left = (Button) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current - 1 >= 0) {
                    current -= 1;
                    c.setText(current + 1 + "");
                    checkstatus();
                    show.setImageResource(getResources().getIdentifier(images[current].toLowerCase(), "drawable", getPackageName()));
                } else {
                    Toast.makeText(getBaseContext(),getString(R.string.firstlint), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button right = (Button) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current < images.length - 1) {
                    current += 1;
                    c.setText(current + 1 + "");
                    checkstatus();
                    show.setImageResource(getResources().getIdentifier(images[current].toLowerCase(), "drawable", getPackageName()));
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.lastlint), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void checkstatus() {
        if (new File("/sdcard/autohack/" + images[current] + ".ingress").exists()) {
            curstatus.setText(this.getString(R.string.ok));
        } else {
            curstatus.setText(this.getString(R.string.no));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ds.stopdraw();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
                Log.d("get", "the height:" + statusBarHeight + ";" + contentTop);
                RelativeLayout.LayoutParams absoluteLayout = (RelativeLayout.LayoutParams) imgtest.getLayoutParams();
                System.out.println(contentTop);
                absoluteLayout.setMargins(0, startx - statusBarHeight - contentTop, 0, 0);
                imgtest.setLayoutParams(absoluteLayout);
                imgpreview.setLayoutParams(absoluteLayout);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(r, 200);
    }
}
