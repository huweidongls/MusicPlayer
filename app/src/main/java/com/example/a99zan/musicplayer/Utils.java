package com.example.a99zan.musicplayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 99zan on 2018/1/9.
 */

public class Utils {

    public static String getCurrentTime (int time){
        long data = time;
        Date date = new Date(data);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }

    static List<String> list = new ArrayList<>();
    public static List<String> searchMp3Infos(File file, String[] ext) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        searchMp3Infos(listFile[i], ext);
                    }
                }
            } else {
                String filename = file.getAbsolutePath();
                for (int i = 0; i < ext.length; i++) {
                    if (filename.endsWith(ext[i])) {
                        list.add(filename);
                        break;
                    }
                }
            }
        }
        return list;
    }

}
