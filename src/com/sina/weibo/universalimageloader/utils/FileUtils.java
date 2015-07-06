package com.sina.weibo.universalimageloader.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FileUtils {
    private static final ThreadPoolExecutor mSingleThreadExecutor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private static final Map<String, Integer> mCacheFolderSize = new ConcurrentHashMap<String, Integer>();
    private static final float FACTOR = 0.3f;

    /**
     * 若文件夹下文件超过最大值，则根据最后使用时间删除最早的1/3文件
     * 
     * @param dir
     * @param maxFileNum
     */
    public static void adjustFolderSize(final File dir, final int maxFileNum) {
        if (dir == null) {
            return;
        }
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        String key = dir.getAbsolutePath();
        Integer folderSize = mCacheFolderSize.get(key);
        if (null == folderSize || 0 == folderSize) {
            mCacheFolderSize.put(key, dir.list().length);
        } else {
            folderSize = folderSize + 1;
            mCacheFolderSize.put(key, folderSize);
        }

        boolean overSize = folderSize > maxFileNum;
        if (overSize) {
            mSingleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    trySharkFolderSize(dir, maxFileNum, FACTOR);
                }
            });
        }
    }

    private static synchronized void trySharkFolderSize(File dir, int maxFileNum, float factor) {
        try {
            boolean confirm = dir.list().length > maxFileNum;
            if (confirm) {
                File[] list = dir.listFiles();
                int fileNum = list.length;
                int sizeToDel = (int) (maxFileNum * factor);
                if (fileNum > sizeToDel) {
                    Arrays.sort(list, new Comparator<File>() {
                        @Override
                        public int compare(File lhs, File rhs) {
                            return lhs.lastModified() > rhs.lastModified() ? 1 : 0;
                        }
                    });
                    int deleteCount = 0;
                    for (int i = 0; i < fileNum && (fileNum - deleteCount > sizeToDel); i++) {
                        if (list[i].exists() && list[i].canWrite()) {
                            list[i].delete();
                            deleteCount++;
                        }
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
