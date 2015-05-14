package com.yivanus.hack;

import android.util.Log;

import com.cygery.repetitouch.Event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by yivanus on 15/3/13.
 */
public class draws {
    Process processrec;
    Process processdraw;
    DataInputStream dataInputStreamrec;
    DataOutputStream dataOutputStreamrec;
    int speedx = 1;
    int eventid = 1;
    long drawtime = 1;
    int dsleep = 200;
    boolean drawing = false;

    public draws(int speedx, int eventid, int dsleep) {

        this.speedx = speedx;
        this.eventid = eventid;
        this.dsleep = dsleep;
    }

    public long getDrawtime() {
        return drawtime;
    }

    public void stoprec() {
        try {
            this.dataOutputStreamrec.writeBytes("\n");
            this.dataOutputStreamrec.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopdraw() {
        if (drawing) {
            processdraw.destroy();
        }
    }

    public void record(final String current) {

        Thread threadrec = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Runtime runtime = Runtime.getRuntime();
                    String as[] = new String[1];
                    as[0] = "su";
                    ArrayList events = new ArrayList();
                    processrec = runtime.exec(as);
                    dataOutputStreamrec = new DataOutputStream(processrec.getOutputStream());
                    dataInputStreamrec = new DataInputStream(processrec.getInputStream());
                    dataOutputStreamrec.writeBytes("/data/data/com.yivanus.hack/files/eventserver /dev/input/event record " + eventid + ";exit\n");
                    dataOutputStreamrec.flush();
                    byte bytes[] = new byte[18];
//                    long starttime = System.currentTimeMillis();
                    while (dataInputStreamrec.read(bytes) == 18) {
                        Event event = new Event(bytes);
                        events.add(event);
                    }
                    processrec.waitFor();
                    ObjectOutputStream objectoutputstream = new ObjectOutputStream(new FileOutputStream("/sdcard/autohack/" + current + ".ingress"));

//                    for (int z = 0; z < events.size(); z++) {
//                        System.out.println(events.get(z).toString());
//                    }
                    for (int j = events.size() - 1; j > 0; j--) {
                        Event t = (Event) events.get(j);
                        events.remove(t);
                        if (t.getCode() == 57 && t.getValue() != -1) {
                            break;
                        }
                    }
                    Event start = (Event) events.get(0);
                    Event end = (Event) events.get(events.size()-1);
                    drawtime = (end.b()-start.b())/1000L;
                    System.out.println(drawtime);
                    objectoutputstream.writeLong(drawtime);
                    objectoutputstream.writeObject(events);
                    objectoutputstream.flush();
                    objectoutputstream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    processrec.destroy();
                    try {
                        dataInputStreamrec.close();
                        dataOutputStreamrec.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadrec.start();
    }

    public void rundraw(final ArrayList filenames) {
        Thread threaddraw = new Thread(new Runnable() {
            @Override
            public void run() {
                drawing = true;
                DataInputStream dataInputStreamdraw = null;
                DataOutputStream dataOutputStreamdraw = null;
                try {
                    Runtime runtimedraw = Runtime.getRuntime();
                    processdraw = runtimedraw.exec("su");
                    dataInputStreamdraw = new DataInputStream(processdraw.getInputStream());
                    dataOutputStreamdraw = new DataOutputStream(processdraw.getOutputStream());
                    String startcmd = new StringBuilder().append("/data/data/com.yivanus.hack/files/eventserver /dev/input/event replay_unchecked ").append(eventid).append(";exit\n").toString();
                    System.out.println(startcmd);
                    dataOutputStreamdraw.writeBytes(startcmd);
                    dataOutputStreamdraw.flush();
                    int r = dataInputStreamdraw.read();
                    if (r != 82) {
                        Log.e("error", "start the server is error.");
                    } else {
                        System.out.println("begin start draw.");
                        for (int i = 0; i < filenames.size(); i++) {
                            File file = new File(filenames.get(i).toString());
                            System.out.println("轨迹名称:"+filenames.get(i).toString());
                            if (!file.exists()) {
                                System.out.println("相应的轨迹不存在");
                                Thread.sleep(2000);
                                continue;
                            }
                            ObjectInputStream localObjectInputStream = new ObjectInputStream(new FileInputStream(file));
                            long drawtime = localObjectInputStream.readLong();
                            System.out.println("轨迹时间:" + drawtime + "毫秒");
                            ArrayList localArrayList = (ArrayList) localObjectInputStream.readObject();
                            System.out.println("事件总数:" + localArrayList.size());
                            localObjectInputStream.close();
                            Event eventstart = (Event) localArrayList.get(0);
                            long starttime = eventstart.b();
                            dataOutputStreamdraw.write(eventstart.e());
                            dataOutputStreamdraw.flush();
                            for (int j = 1; j < localArrayList.size(); j++) {
                                Event eventdraw = (Event) localArrayList.get(j);
//
                                long time = eventdraw.b() - starttime;
                                starttime = eventdraw.b();
                                if (time > 0 && speedx != 0) {
                                    Thread.sleep((time / 1000L / speedx), (int) ((time % 1000000L) / speedx));
                                }
                                dataOutputStreamdraw.write(eventdraw.e());
                                dataOutputStreamdraw.flush();
//
                            }
                            System.out.println("自动画结束");
                            Thread.sleep(dsleep);
                        }
                        dataOutputStreamdraw.write(Event.STOP_EVENT.a());
                        dataOutputStreamdraw.flush();
                        Log.i("draw over", "draw the gyhy ok.");
                    }
//                    processdraw.waitFor();
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    processdraw.destroy();
                    drawing = false;
                    try {
                        if (dataOutputStreamdraw != null) {
                            dataOutputStreamdraw.close();
                        }
                        if (dataInputStreamdraw != null) {
                            dataInputStreamdraw.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threaddraw.start();
    }
}
