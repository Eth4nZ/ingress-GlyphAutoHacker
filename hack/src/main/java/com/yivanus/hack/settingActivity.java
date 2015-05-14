package com.yivanus.hack;
//设置页面关键点相关
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class settingActivity extends ActionBarActivity {

    Button btnleft;
    Button btnright;
    Button btnup;
    Button btndown;
    Button btntest;
    Point[] points;
    TextView point;

    Bitmap smallbitmap;
    Bitmap srcBitmap;
    TextView result;
    Canvas canvas;
    ImageView imgtest;
    ImageView imgpreview;
    Paint paintcir;
    Paint painttext;
    Paint paintp;
    Glyph glyph;

    int startx=0;
    int background=0;
    int width=0;
    int b=0;
    int psize = 28;

    List pas;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.settings);
        startx = intent.getIntExtra("startx",0);
        psize = intent.getIntExtra("psize",30);
        sp = getSharedPreferences("ingress",0);
        ed = sp.edit();
        points = new Point[11];
        for(int i=0;i<points.length;i++){
            int x = sp.getInt(i+"x",0);
            int y = sp.getInt(i+"y",0);
            points[i] = new Point(x,y);
        }
        pas = new ArrayList();
        pas.add('A');
        pas.add('B');
        pas.add('C');
        pas.add('D');
        pas.add('E');
        pas.add('F');
        pas.add('G');
        pas.add('H');
        pas.add('I');
        pas.add('J');
        pas.add('K');
        initUI();
        glyph = new Glyph();
        glyph.init(points,startx,background,1,psize,1,1,false);
    }

    public void initUI(){
        imgtest = (ImageView)findViewById(R.id.image);
        imgtest.setScaleType(ImageView.ScaleType.CENTER);
        imgpreview = (ImageView)findViewById(R.id.preview);
        imgpreview.setScaleType(ImageView.ScaleType.CENTER);
        imgpreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int i = Integer.valueOf(point.getText().toString());

                int j = imgpreview.getHeight() / 2 - width / 2;
                points[i].set((int)event.getX(),(int)event.getY()-j);
                smallbitmap.eraseColor(Color.TRANSPARENT);
                drawpoints();
                imgpreview.setImageBitmap(smallbitmap);
                return true;
            }
        });
        result = (TextView)findViewById(R.id.result);
        point = (TextView) findViewById(R.id.xy);

        btntest =(Button)findViewById(R.id.testbutton);
        btntest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder=new StringBuilder();
                for(int i =0;i<points.length;i++) {

                    boolean test =  srcBitmap.getPixel(points[i].x-psize/2,points[i].y-psize/2)!= background ||
                                    srcBitmap.getPixel(points[i].x,points[i].y-psize/2)!= background ||
                                    srcBitmap.getPixel(points[i].x+psize/2,points[i].y-psize/2)!= background ||

                                    srcBitmap.getPixel(points[i].x-psize/2,points[i].y)!= background ||
                                    srcBitmap.getPixel(points[i].x,points[i].y)!= background ||
                                    srcBitmap.getPixel(points[i].x+psize/2,points[i].y-psize/2)!= background ||

                                    srcBitmap.getPixel(points[i].x-psize/2,points[i].y+psize/2)!= background ||
                                    srcBitmap.getPixel(points[i].x,points[i].y+psize/2)!= background ||
                                    srcBitmap.getPixel(points[i].x+psize/2,points[i].y+psize/2)!= background;
                    if(test) {
                        stringBuilder.append(pas.get(i).toString());
                    }
                }
                if(stringBuilder.toString().isEmpty()){
                    result.setText(getString(R.string.passed));
                }else{
                    result.setText(getString(R.string.faile)+stringBuilder.toString());
                }
            }
        });

        Button autotest = (Button) findViewById(R.id.autotest);
        autotest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //11个关键点坐标计算
                comput(points[0], 1, 90);
                comput(points[1], 1, 150);
                comput(points[2], 1, 30);
                comput(points[3], 0.5F, 150);
                comput(points[4], 0.5F, 30);
                comput(points[5], 0, 0);
                comput(points[6], 0.5F, 210);
                comput(points[7], 0.5F, 330);
                comput(points[8], 1, 210);
                comput(points[9], 1, 330);
                comput(points[10], 1, 270);
                smallbitmap.eraseColor(Color.TRANSPARENT);
                drawpoints();
                imgpreview.setImageBitmap(smallbitmap);
            }
        });
        Button addnum = (Button) findViewById(R.id.addnum);
        addnum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = Integer.valueOf(point.getText().toString());
                if(p<10) {
                    point.setText(String.valueOf(p + 1));
                }
            }
        });
        Button subnum = (Button) findViewById(R.id.subnum);
        subnum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = Integer.valueOf(point.getText().toString());
                if(p>0) {
                    point.setText(String.valueOf(p - 1));
                }
            }
        });
        btnleft = (Button) findViewById(R.id.left);
        btnleft.setOnTouchListener(new buttontouch());

        btnright = (Button) findViewById(R.id.right);
        btnright.setOnTouchListener(new buttontouch());

        btnup = (Button) findViewById(R.id.up);
        btnup.setOnTouchListener(new buttontouch());

        btndown = (Button) findViewById(R.id.down);
        btndown.setOnTouchListener(new buttontouch());

        procSrc2Gray();
        drawpoints();
        imgtest.setImageBitmap(srcBitmap);
        imgpreview.setImageBitmap(smallbitmap);
    }
    //根据图形计算大概的关键点位置
    public void comput(Point p,float radio,int angle){
        int center = width/2;
        float centerRadius = (0.83F * (radio * center));
        int x = (int)(center + (double) (centerRadius * (float) Math.cos(angle * 3.14159F / 180.0F)));
        int y = (int)(center - (double) (centerRadius * (float) Math.sin(angle * 3.14159F / 180.0F)));
        p.set(x,y);
    }

    private void drawpoints(){
        int tmp = this.psize/2;
        for (int i =0;i<points.length;i++){
            canvas.drawRect(points[i].x-tmp,points[i].y-tmp,points[i].x+tmp,points[i].y+tmp,paintcir);

            canvas.drawPoint(points[i].x-tmp,points[i].y-tmp,paintp);
            canvas.drawPoint(points[i].x,points[i].y-tmp,paintp);
            canvas.drawPoint(points[i].x+tmp,points[i].y-tmp,paintp);

            canvas.drawPoint(points[i].x-tmp,points[i].y,paintp);
            canvas.drawPoint(points[i].x,points[i].y,paintp);
            canvas.drawPoint(points[i].x+tmp,points[i].y,paintp);

            canvas.drawPoint(points[i].x-tmp,points[i].y+tmp,paintp);
            canvas.drawPoint(points[i].x,points[i].y+tmp,paintp);
            canvas.drawPoint(points[i].x+tmp,points[i].y+tmp,paintp);

            canvas.drawText(String.valueOf(i)+pas.get(i).toString(),points[i].x-this.psize/2,points[i].y+this.psize+20,painttext);
        }
    }
    public void procSrc2Gray(){
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + "autohack/test.png");
        width = bitmap.getWidth();
        System.out.println(width);
        srcBitmap = Bitmap.createBitmap(bitmap,0,startx,width,width);
        background = srcBitmap.getPixel(1,1);
        smallbitmap = Bitmap.createBitmap(srcBitmap.getWidth(),srcBitmap.getHeight(), Config.ARGB_8888);
        canvas = new Canvas(smallbitmap);
        paintcir = new Paint();
        paintcir.setColor(Color.YELLOW);
        paintcir.setStyle(Paint.Style.STROKE);
        paintcir.setStrokeWidth(3);
        painttext = new Paint();
        painttext.setColor(Color.YELLOW);
        painttext.setTextSize(this.psize);
        paintp = new Paint();
        paintp.setColor(Color.RED);
        paintp.setStrokeWidth(3);
        int a = width/11;
        b = a/2;
        for(int i =0;i<11;i++){
            if(points[i].y == 0) {
                points[i].y = 100;
            }
            if(points[i].x == 0){
                points[i].x = i*a+a-b;
            }
        }
    }
    private class buttontouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int buttonid = v.getId();
            String string = point.getText().toString();
            int i = Integer.valueOf(string);
            System.out.println("current:" + i);
            int x = points[i].x;
            int y = points[i].y;
            System.out.println("current: X=" + x + " Y=" + y);
            switch (buttonid) {
                case R.id.down:
                    System.out.println("down");
                    points[i].set(x,y+1);
                    break;
                case R.id.up:
                    System.out.println("up");
                    points[i].set(x,y-1);
                    break;
                case R.id.left:
                    System.out.println("left");
                    points[i].set(x-1,y);
                    break;
                case R.id.right:
                    System.out.println("right");
                    points[i].set(x+1,y);
                    break;
                default:
                    break;
            }
            smallbitmap.eraseColor(Color.TRANSPARENT);
            drawpoints();
            imgpreview.setImageBitmap(smallbitmap);
            return true;
        }
    }

    @Override
    protected void onStop() {
        for(int i=0;i<points.length;i++){
            int x =points[i].x;
            int y =points[i].y;
            ed.putInt(i+"x",x);
            ed.putInt(i+"y",y);
        }
        ed.putInt("background",background);
        ed.commit();
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}