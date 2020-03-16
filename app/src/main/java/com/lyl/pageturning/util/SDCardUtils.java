package com.lyl.pageturning.util;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lym on 2020/3/3
 * Describe : 读取SD卡里的文本内容
 */
public class SDCardUtils {
    private static final String TAG = "SDCardUtils";

    private static String path1 = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String path2 = Environment.getDownloadCacheDirectory().getAbsolutePath();
    private static String pathExt = "/111/222/333/444/555/";
    private static String fileName = "6.txt";

    public static void write(String str) {
        String filePath = null;
        boolean hasSDCard =Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = path1 + pathExt + fileName;
        } else {
            filePath = path2 + pathExt + fileName;
        }
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                //mkdirs()方法生成多层文件夹
                //mkdir()方法生成一层层文件夹
//                File dir = new File(file.getParent());
//                dir.mkdirs();
                file.getParentFile().mkdirs();//生成文件外层的文件夹
                file.createNewFile();//生成文件
            }
            FileOutputStream os = new FileOutputStream(file);
            os.write(str.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(String path, String fileName) {
        String content = "";
        String filePath;

        boolean sdcard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdcard) {
            filePath = /*path1 + pathExt*/path + fileName;
        } else {
            filePath = /*path2 + pathExt*/path + fileName;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                InputStreamReader inputReader = new InputStreamReader(is);//设置流读取方式
                BufferedReader buffReader = new BufferedReader(inputReader);
                String line;
                try {
                    while (null != (line = buffReader.readLine())) {
                        content += line + "\n";//读取的文件容
                    }
                    is.close();//关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != is) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String getString(){

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();//getPath();

        Log.d(TAG, "path: " + path);
        boolean b = checkFileExist(path, "/MDMLog.txt");
        Log.d(TAG, "exist " + b);
        if (!b) return null;
        String s = read(path, "/MDMLog.txt");
        Log.d(TAG, "s : " + s);
        return s;
    }


    /**
     * 判断文件是否存在在SDCard卡上
     *
     * @param path 路径
     * @param fileName 文件名
     * @return 文件是否存在在SDCard卡上
     */
    public static boolean checkFileExist(String path, String fileName) {

        File file = new File(path + fileName);

        return file.exists();
    }



}
