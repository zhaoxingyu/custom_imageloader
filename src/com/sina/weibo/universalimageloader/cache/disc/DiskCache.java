/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.sina.weibo.universalimageloader.cache.disc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;

import com.sina.weibo.universalimageloader.utils.IoUtils;

/**
 * Interface for disk cache
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public interface DiskCache {

    /**
     * Returns root directory of disk cache
     *
     * @return Root directory of disk cache
     */
    File getDirectory();

    File getDirectory(DiskCacheFolder folder);

    /**
     * Returns file of cached image
     *
     * @param imageUri
     *            Original image URI
     * @param DiskCacheFolder
     *            cache dir
     * @return File of cached image or <b>null</b> if image wasn't cached
     */
    File get(String imageUri, DiskCacheFolder folder);
    
    /**
     * return file of cached image from default dir 
     * @param imageUri
     * @return cache file
     * @see {@link DiskCacheFolder#DEFAULT}
     */
    File get(String imageUri);

    /**
     * Saves image stream in disk cache.
     *
     * @param imageUri
     *            Original image URI
     * @param imageStream
     *            Input stream of image
     * @param listener
     *            Listener for saving progress, can be ignored if you don't use
     *            {@linkplain com.sina.weibo.universalimageloader.core.listener.ImageLoadingProgressListener
     *            progress listener} in ImageLoader calls
     * @return <b>true</b> - if image was saved successfully; <b>false</b> - if
     *         image wasn't saved in disk cache.
     * @throws IOException
     */
    boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException;

    /**
     * Saves image bitmap in disk cache.
     *
     * @param imageUri
     *            Original image URI
     * @param bitmap
     *            Image bitmap
     * @return <b>true</b> - if bitmap was saved successfully; <b>false</b> - if
     *         bitmap wasn't saved in disk cache.
     * @throws IOException
     */
    boolean save(String imageUri, Bitmap bitmap) throws IOException;

    /**
     * Removes image file associated with incoming URI
     *
     * @param imageUri
     *            Image URI
     * @param folder
     *            : cache dir
     * @return <b>true</b> - if image file is deleted successfully; <b>false</b>
     *         - if image file doesn't exist for incoming URI or image file
     *         can't be deleted.
     *         
     * @see {@link DiskCacheFolder}

     */
    boolean remove(String imageUri, DiskCacheFolder folder);

    boolean remove(String imageUri);

    /** Closes disk cache, releases resources. */
    void close();

    /** Clears disk cache. */
    void clear();
}
