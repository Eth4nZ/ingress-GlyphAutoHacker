package com.yivanus.hack;
//图形识别与自动画相关处理逻辑

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yivanus on 15/2/1.
 */

public class Glyph {
    //-------------发送自动画语句
    private static String begin = " 3 57 0\n";
    //    private static String start = " 3 58 45\n";
    private static String x = " 3 53 ";
    private static String y = " 3 54 ";
    private static String bop = " 0 0 0\n";
    private static String ends1 = " 3 57 -1\n";
    private static String ends2 = " 0 0 0\n";
    //-------------自动画结束
    Point[] mypoints;
    private List pas;
    private int background;
    private int startx;
    private int screen;
    private int psize;
    private float xscale = 1;
    private float yscale = 1;
    private boolean adp = false;

    //初始化图形识别
    public void init(Point[] points, int startx, int background, int screen, int psize,float xscale,float yscale,boolean adp){
        mypoints = new Point[11];
        for (int i = 0; i < points.length; i++) {
            mypoints[i] = new Point(points[i].x, points[i].y + startx);
        }
        this.background = background;//图片背景颜色
        this.startx = startx;//截图开始y坐标
        this.screen = screen;//屏幕输入硬件id
        this.psize = psize;//识别关键点的大小
        this.xscale = xscale;//不同的屏幕需要的x比例因子
        this.yscale = yscale;//不同的屏幕需要的y比例因子
        this.adp = adp;
        pas = new ArrayList();//初始化各点名字
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
    }

    //根据关键点获取图片中的点,并返回字符串
    public String getpoint(Bitmap bitmap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mypoints.length; i++) {
            int tmpy = mypoints[i].y - startx;
            int pp = this.psize / 2;
            boolean test = bitmap.getPixel(mypoints[i].x - pp, tmpy - pp) != background ||
                    bitmap.getPixel(mypoints[i].x, tmpy - pp) != background ||
                    bitmap.getPixel(mypoints[i].x + pp, tmpy - pp) != background ||

                    bitmap.getPixel(mypoints[i].x - pp, tmpy) != background ||
                    bitmap.getPixel(mypoints[i].x + pp, tmpy - pp) != background ||

                    bitmap.getPixel(mypoints[i].x - pp, tmpy + pp) != background ||
                    bitmap.getPixel(mypoints[i].x, tmpy + pp) != background ||
                    bitmap.getPixel(mypoints[i].x + pp, tmpy + pp) != background;
            if (test) {
                stringBuilder.append(pas.get(i).toString());
            }
        }
        return stringBuilder.toString();
    }

    //判断是否是一条线,false则是线,true则是背景
    private boolean isline(int color) {
        return color == background;
    }

    //翻译图案字符串,处理相同关键点的图案
    public String ingressglpyh(Bitmap scalebitmap, String tmp) {
        int x, y, x1, y1, x2, y2;
        switch (tmp) {
            case "DEF":
                x = (mypoints[3].x + mypoints[4].x) / 2;
                y = (mypoints[3].y - this.startx + mypoints[4].y - this.startx) / 2;
                if (isline(scalebitmap.getPixel(x, y))) {
                    tmp = "DEF2";
                    //dfe
                } else {
                    tmp = "DEF1";
                    //defd
                }
                break;

            case "DFGK":
                x = (mypoints[5].x + mypoints[6].x) / 2;
                y = (mypoints[5].y - this.startx + mypoints[6].y - this.startx) / 2;
                if (isline(scalebitmap.getPixel(x, y))) {
                    tmp = "DFGK1";
                    //FGDFK
                } else {
                    tmp = "DFGK2";
                    //DFKGD
                }
                break;


            case "DEFG":
                x = (mypoints[3].x + mypoints[4].x) / 2;
                y = (mypoints[3].y - this.startx + mypoints[4].y - this.startx) / 2;
                if (isline(scalebitmap.getPixel(x, y))) {
                    tmp = "DEFG1";
                    //FDGFEG
                } else {
                    tmp = "DEFG2";
                    //EDFG
                }
                break;

            case "FGHK":
                x = (mypoints[5].x + mypoints[7].x) / 2;
                y = (mypoints[5].y - this.startx + mypoints[7].y - this.startx) / 2;
                if (isline(scalebitmap.getPixel(x, y))) {
                    tmp = "FGHK1";
                    //GFKH
                } else {
                    tmp = "FGHK2";
                    //HFGK
                }
                break;

            case "AEFH":
                x = (mypoints[5].x + mypoints[7].x) / 2;
                y = (mypoints[5].y - this.startx + mypoints[7].y - this.startx) / 2;
                if (isline(scalebitmap.getPixel(x, y))) {
                    tmp = "AEFH1";
                    //AFEH
                } else {
                    tmp = "AEFH2";
                    //AFHEF
                }
                break;
            case "DEGHIJ":
                x = (mypoints[3].x + mypoints[4].x) / 2;
                y = (mypoints[3].y - this.startx + mypoints[4].y - this.startx) / 2;
                if (isline(scalebitmap.getPixel(x, y))) {
                    tmp = "DEGHIJ2";
                    //IDGHEJ
                } else {
                    tmp = "DEGHIJ1";
                    //IGDEHJ
                }
                break;
            case "DEGH":
                x = (mypoints[3].x + mypoints[4].x) / 2;
                y = (mypoints[3].y - this.startx + mypoints[4].y - this.startx) / 2;
                x1 = (mypoints[6].x + mypoints[7].x) / 2;
                y1 = (mypoints[6].y - this.startx + mypoints[7].y - this.startx) / 2;
                if (!isline(scalebitmap.getPixel(x, y)) && !isline(scalebitmap.getPixel(x1, y1))) {
                    tmp = "DEGH1";
                } else if (isline(scalebitmap.getPixel(x, y))) {
                    tmp = "DEGH2";
                } else if (isline(scalebitmap.getPixel(x1, y1))) {
                    tmp = "DEGH3";
                }
                break;
            case "DEFGH":
                Point p3 = mypoints[3];
                Point p4 = mypoints[4];
                Point p5 = mypoints[5];
                Point p6 = mypoints[6];
                Point p7 = mypoints[7];
                if (isline(scalebitmap.getPixel((p4.x+p7.x)/2, (p4.y+p7.y-2*startx)/2))&&
                        isline(scalebitmap.getPixel((p5.x+p7.x)/2, (p5.y+p7.y-2*startx)/2))){
                    tmp = "DEFGH3";
                } else if (!isline(scalebitmap.getPixel((p3.x+p4.x)/2,(p3.y+p4.y-2*startx)/2))&&
                        isline(scalebitmap.getPixel((p6.x+p7.x)/2,(p6.y+p7.y-2*startx)/2))){
                    tmp = "DEFGH1";
                } else if (isline(scalebitmap.getPixel((p3.x+p4.x)/2, (p3.y+p4.y-2*startx)/2)) &&
                        isline(scalebitmap.getPixel((p5.x+p6.x)/2,(p5.y+p6.y-2*startx)/2))) {
                    tmp = "DEFGH0";
                } else if (isline(scalebitmap.getPixel((p3.x+p4.x)/2,(p3.y+p4.y-2*startx)/2))&&
                        isline(scalebitmap.getPixel((p6.x+p7.x)/2,(p6.y+p7.y-2*startx)/2))&&
                        !isline(scalebitmap.getPixel((p6.x+p5.x)/2,(p6.y+p5.y-2*startx)/2))){
                    tmp = "DEFGH2";
                }
                break;
            default:
                break;
        }
        return tmp;
    }

    //返回自动画相关语句,返回的语句可以直接交给命令运行
    public String getglyph(String string) {
        if (string.isEmpty()) {
            return "";
        }

        String glyphString = string;
        Random random = new Random(System.currentTimeMillis());
        if (random.nextInt(10) < 5) {
            StringBuilder sb = new StringBuilder();
            for (int i = string.length() - 1; i >= 0; i--) {
                sb.append(string.charAt(i));
            }
            glyphString = sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("sendevent /dev/input/event" + this.screen + begin);
//        sb.append("sendevent /dev/input/event" + this.screen + start);
        int tmpx1;
        int tmpy1;
        int tmpx2 = 1;
        int tmpy2 = 1;
        for (int i = 0; i < glyphString.length() - 1; i++) {
            tmpx1 = 0;
            tmpy1 = 0;
            switch (glyphString.charAt(i)) {
                case 'a':
                case 'A':
                    tmpx1 = mypoints[0].x;
                    tmpy1 = mypoints[0].y;
                    break;
                case 'b':
                case 'B':
                    tmpx1 = mypoints[1].x;
                    tmpy1 = mypoints[1].y;
                    break;
                case 'c':
                case 'C':

                    tmpx1 = mypoints[2].x;
                    tmpy1 = mypoints[2].y;
                    break;
                case 'd':
                case 'D':

                    tmpx1 = mypoints[3].x;
                    tmpy1 = mypoints[3].y;
                    break;
                case 'e':
                case 'E':

                    tmpx1 = mypoints[4].x;
                    tmpy1 = mypoints[4].y;
                    break;
                case 'f':
                case 'F':

                    tmpx1 = mypoints[5].x;
                    tmpy1 = mypoints[5].y;
                    break;
                case 'g':
                case 'G':

                    tmpx1 = mypoints[6].x;
                    tmpy1 = mypoints[6].y;
                    break;
                case 'h':
                case 'H':

                    tmpx1 = mypoints[7].x;
                    tmpy1 = mypoints[7].y;
                    break;
                case 'i':
                case 'I':

                    tmpx1 = mypoints[8].x;
                    tmpy1 = mypoints[8].y;
                    break;
                case 'j':
                case 'J':

                    tmpx1 = mypoints[9].x;
                    tmpy1 = mypoints[9].y;
                    break;
                case 'k':
                case 'K':

                    tmpx1 = mypoints[10].x;
                    tmpy1 = mypoints[10].y;
                    break;
                default:
                    break;
            }
            tmpx2 = 0;
            tmpy2 = 0;
            switch (glyphString.charAt(i + 1)) {
                case 'a':
                case 'A':

                    tmpx2 = mypoints[0].x;
                    tmpy2 = mypoints[0].y;
                    break;
                case 'b':
                case 'B':

                    tmpx2 = mypoints[1].x;
                    tmpy2 = mypoints[1].y;
                    break;
                case 'c':
                case 'C':

                    tmpx2 = mypoints[2].x;
                    tmpy2 = mypoints[2].y;
                    break;
                case 'd':
                case 'D':

                    tmpx2 = mypoints[3].x;
                    tmpy2 = mypoints[3].y;
                    break;
                case 'e':
                case 'E':

                    tmpx2 = mypoints[4].x;
                    tmpy2 = mypoints[4].y;
                    break;
                case 'f':
                case 'F':

                    tmpx2 = mypoints[5].x;
                    tmpy2 = mypoints[5].y;
                    break;
                case 'g':
                case 'G':

                    tmpx2 = mypoints[6].x;
                    tmpy2 = mypoints[6].y;
                    break;
                case 'h':
                case 'H':

                    tmpx2 = mypoints[7].x;
                    tmpy2 = mypoints[7].y;
                    break;
                case 'i':
                case 'I':
                    tmpx2 = mypoints[8].x;
                    tmpy2 = mypoints[8].y;
                    break;
                case 'j':
                case 'J':
                    tmpx2 = mypoints[9].x;
                    tmpy2 = mypoints[9].y;
                    break;
                case 'k':
                case 'K':

                    tmpx2 = mypoints[10].x;
                    tmpy2 = mypoints[10].y;
                    break;
                default:
                    break;
            }
            double tempx1 = tmpx1 * this.xscale;
            double tempy1 = tmpy1 * this.yscale;
            double tempx2 = tmpx2 * this.xscale;
            double tempy2 = tmpy2 * this.yscale;
            int count;
            double distance = Math.sqrt(Math.pow(tempx2 - tempx1, 2) + Math.pow(tempy2 - tempy1, 2));
            if (distance > 400) {
                count = 8;
            } else if (distance > 300) {
                count = 6;
            } else {
                count = 4;
            }
            System.out.println("the line cut:" + count);
            int xx = (int) (tempx2 - tempx1) / count;
            int yy = (int) (tempy2 - tempy1) / count;

            sb.append("sendevent /dev/input/event" + this.screen + x + (tempx1 + random.nextInt(10) - 5) + "\n");
            sb.append("sendevent /dev/input/event" + this.screen + y + (tempy1 + random.nextInt(10) - 5) + "\n");
            sb.append("sendevent /dev/input/event" + this.screen + bop);
            if (this.adp) {
                for (int z = 1; z < count; z++) {
                    sb.append("sendevent /dev/input/event" + this.screen + x + (tempx1 + z * xx + random.nextInt(10) - 5) + "\n");
                    sb.append("sendevent /dev/input/event" + this.screen + y + (tempy1 + z * yy + random.nextInt(10) - 5) + "\n");
                    sb.append("sendevent /dev/input/event" + this.screen + bop);
                }
            } else {
                sb.append("sendevent /dev/input/event" + this.screen + x + (tempx1 + xx + random.nextInt(10) - 5) + "\n");
                sb.append("sendevent /dev/input/event" + this.screen + y + (tempy1 + yy + random.nextInt(10) - 5) + "\n");
                sb.append("sendevent /dev/input/event" + this.screen + bop);
            }
        }
        sb.append("sendevent /dev/input/event" + this.screen + x + (tmpx2 * this.xscale + random.nextInt(10) - 5) + "\n");
        sb.append("sendevent /dev/input/event" + this.screen + y + (tmpy2 * this.yscale + random.nextInt(10) - 5) + "\n");
        sb.append("sendevent /dev/input/event" + this.screen + bop);
        sb.append("sendevent /dev/input/event" + this.screen + ends1);
        sb.append("sendevent /dev/input/event" + this.screen + ends2);
        return sb.toString();
    }

    //图案序列转换
    public String converString(String string) {
        String tmp="";
        switch (string) {
            case "BG":
                tmp = "BG";
                break;
            case "BDG":
                tmp = "BDG";
                break;
            case "BDGI":
                tmp = "BDGI";
                break;
            case "BDFHJ":
                tmp = "BDFHJ";
                break;
            case "BDFHJK":
                tmp = "BDFHJK";
                break;
            case "BCDEF":
                tmp = "BDFEC";
                break;
            case "BCDEGH":
                tmp = "BDGHEC";
                break;
            case "ACDEFGJK":
                tmp = "EFDGKJCA";
                break;
            case "BCGHK":
                tmp = "BGKHC";
                break;
            case "BDFGH":
                tmp = "BDFGH";
                break;
            case "ABDE":
                tmp = "BDAE";
                break;
            case "AFGHIJK":
                tmp = "AFGIKJHF";
                break;
            case "BDEH":
                tmp = "BDEH";
                break;
            case "ABCEH":
                tmp = "BAECH";
                break;
            case "BCEFGJ":
                tmp = "BGFECJ";
                break;
            case "BDEFGHJ":
                tmp = "BDGFEHJ";
                break;
            case "GI":
                tmp = "GI";
                break;
            case "CEFGI":
                tmp = "CEFGI";
                break;
            case "ADI":
                tmp = "ADI";
                break;
            case "ABI":
                tmp = "ABI";
                break;
            case "AFGI":
                tmp = "AFGI";
                break;
            case "DFGI":
                tmp = "DFGI";
                break;
            case "DGHI":
                tmp = "IDGH";
                break;
            case "CDFHI":
                tmp = "CHFDI";
                break;
            case "DEFGHI":
                tmp = "IDGFEH";
                break;
            case "GHIK":
                tmp = "IGKH";
                break;
            case "CIJK":
                tmp = "CJKI";
                break;
            case "DGI":
                tmp = "IDG";
                break;
            case "IJK":
                tmp = "IKJ";
                break;
            case "FGHIJ":
                tmp = "IGFHJ";
                break;
            case "GHIJ":
                tmp = "IGHJ";
                break;
            case "DEIJ":
                tmp = "IDEJ";
                break;

            case "ADEGHIJ":
                tmp = "IGDAEHJ";
                break;
            case "ADEIJ":
                tmp = "IDAEJ";
                break;
            case "ABCEFGIK":
                tmp = "IBACEFGK";
                break;
            case "ACDEFI":
                tmp = "ACEFDI";
                break;
            case "AD":
                tmp = "AD";
                break;

            case "DEH":
                tmp = "DEH";
                break;
            case "DEFH":
                tmp = "DEHF";
                break;
            case "CDEH":
                tmp = "DEHC";
                break;
            case "CDEGH":
                tmp = "DECHG";
                break;
            case "DEFGHK":
                tmp = "DFEHGK";
                break;
            case "GH":
                tmp = "GH";
                break;
            case "FGH":
                tmp = "GFH";
                break;

            case "CEFGH":
                tmp = "CEHFG";
                break;
            case "CFGH":
                tmp = "CHFG";
                break;
            case "ADFG":
                tmp = "AFDG";
                break;
            case "ADEG":
                tmp = "AEDG";
                break;
            case "ACDEG":
                tmp = "ACEDG";
                break;
            case "BCDEFGHIJ":
                tmp = "ECJHFDBIG";
                break;
            case "AFK":
                tmp = "AFK";
                break;
            case "AEJ":
                tmp = "AEJ";
                break;
            case "AFHJ":
                tmp = "AFHJ";
                break;
            case "AEFGK":
                tmp = "AEFGK";
                break;
            case "ACEJ":
                tmp = "AECJ";
                break;
            case "ADFK":
                tmp = "ADFK";
                break;
            case "ABGIK":
                tmp = "ABIGK";
                break;
            case "ACFHJ":
                tmp = "AFHJC";
                break;
            case "ACDGFGJK":
                tmp = "ACJKGEFE";
                break;
            case "CFH":
                tmp = "CHF";
                break;
            case "EHJ":
                tmp = "EHJ";
                break;
            case "EFGK":
                tmp = "EFGK";
                break;
            case "DEFHJ":
                tmp = "EDFHJ";
                break;
            case "CFGHIK":
                tmp = "CHFGIK";
                break;
            case "BCDEFIK":
                tmp = "CEFDBIK";
                break;
            case "HJ":
                tmp = "HJ";
                break;
            case "CH":
                tmp = "CH";
                break;
            case "CEFH":
                tmp = "CEFH";
                break;
            case "CEHJ":
                tmp = "CEHJ";
                break;
            case "DEFGI":
                tmp = "FDEFGI";
                break;
            case "ADEFGK":
                tmp = "EDAFKG";
                break;
            case "ADEFHJ":
                tmp = "FEADFHJ";
                break;
            case "ACDEFJK":
                tmp = "DEFDACJK";
                break;
            case "AFDGIJK":
                tmp = "AFGIKJHF";
                break;
            case "DEGHK":
                tmp = "DEHKGD";
                break;
            case "GHK":
                tmp = "GHKG";
                break;
            case "FGIK":
                tmp = "FGIKF";
                break;
            case "ABDF":
                tmp = "ABDFA";
                break;
            case "EFHK":
                tmp = "EFKHE";
                break;
            case "ACFHK":
                tmp = "AFKHCA";
                break;
            case "ABCIJK":
                tmp = "ABIKJCA";
                break;
            case "ABCFIJK":
                tmp = "ABIKJCAFK";
                break;
            case "ABCGHIJK":
                tmp = "KJCABIKGHK";
                break;
            case "BCDEGHIJ":
                tmp = "BDECJHGIB";
                break;
            case "ADEFGHK":
                tmp = "ADFHKGFEA";
                break;

            case "DEF1":
                tmp = "DEFD";
                break;
            case "DEF2":
                tmp = "DFE";
                break;

            case "DFGK1":
                tmp = "DFKGD";
                break;
            case "DFGK2":
                tmp = "FGDFK";
                break;

            case "DEFG1":
                tmp = "FDGFEG";
                break;
            case "DEFG2":
                tmp = "EDFG";
                break;

            case "FGHK1":
                tmp = "GFKH";
                break;
            case "FGHK2":
                tmp = "HFGK";
                break;


            case "AEFH1":
                tmp = "AFEH";
                break;
            case "AEFH2":
                tmp = "AFHEF";
                break;


            case "DEGHIJ1":
                tmp = "IGDEHJ";
                break;
            case "DEGHIJ2":
                tmp = "IDGHEJ";
                break;

            case "DEGH1":
                tmp = "DEHGD";
                break;
            case "DEGH2":
                tmp = "DGHE";
                break;
            case "DEGH3":
                tmp = "GDEH";
                break;

            case "DEFGH0":
                tmp = "FEHFDG";
                break;
            case "DEFGH1":
                tmp = "DEHFGD";
                break;
            case "DEFGH2":
                tmp = "DFHEFGD";
                break;
            case "DEFGH3":
                tmp = "FEDGH";
                break;
            default:
                tmp = "";
                break;
            //新的识别序列可在此处添加
        }
        return tmp;
    }
}
