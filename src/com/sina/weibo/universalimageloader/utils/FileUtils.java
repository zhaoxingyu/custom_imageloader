package com.sina.weibo.universalimageloader.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {
    /**
     * 若文件夹下文件超过最大值，则根据最后使用时间 清除一半文件。
     * @param dir
     * @param maxFileNum
     */
    public static void adjustFolderSize(final File dir, final int maxFileNum) {
        if (dir == null)
            return;
        if (!dir.exists() || !dir.isDirectory())
            return;
        try {
            boolean confirm = dir.listFiles().length > maxFileNum;
            if (confirm) {
                new Thread() {
                    public void run() {
                        deleteHalf(dir, maxFileNum);
                    };
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized void deleteHalf(File dir, int maxFileNum) {
        try {
            File[] list = dir.listFiles();
            int fileNum = list.length;
            int halfCapacity = maxFileNum / 2;
            if (fileNum > halfCapacity) {
                Arrays.sort(list, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        return lhs.lastModified() > rhs.lastModified() ? 1 : 0;
                    }
                });
                int deleteCount = 0;
                for (int i = 0; i < fileNum && (fileNum - deleteCount > halfCapacity); i++) {
                    if (list[i].exists() && list[i].canWrite()) {
                        list[i].delete();
                        deleteCount++;
                    }
                }
            }
        } catch (OutOfMemoryError e) {// 文件太多会oom，因为数组会很大。
            try {
                Runtime.getRuntime().exec("rm -r " + dir.getAbsolutePath());
            } catch (IOException e1) {
                // do nothing
            }
        }
    }
}
