/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.folderv.timelapse;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qingtian.zhang on 2017/7/1.
 */

public class Timelapse extends Application {

    private static final String TAG = "Timelapse";

    public final static String CRASH_CACHE_DIRECTORY_NAME = "crash";

    private static Timelapse instance;
    public static Timelapse getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initCrashLog();
    }

    private void initCrashLog(){
        final Thread.UncaughtExceptionHandler olderHandler = Thread.getDefaultUncaughtExceptionHandler();
        //printDefaultExceptionHandler();
        Thread.UncaughtExceptionHandler myHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

                try {
                    final Writer result = new StringWriter();
                    final PrintWriter printWriter = new PrintWriter(result);
                    printWriter.append(throwable.getMessage());
                    throwable.printStackTrace(printWriter);
                    Log.getStackTraceString(throwable);
                    // If the exception was thrown in a background thread inside
                    // AsyncTask, then the actual exception can be found with getCause.
                    Throwable cause = throwable.getCause();
                    while (cause != null) {
                        cause.printStackTrace(printWriter);
                        cause = cause.getCause();
                    }
                    String msg = buildCrashLog(result.toString());
                    printWriter.close();
                    saveCrashLog2File(msg);
                    Logger.t(TAG).e(throwable,"crash!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.t(TAG).e(e,"oops!");
                }

                if(olderHandler!=null){
                    olderHandler.uncaughtException(thread, throwable);
                }
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(myHandler);
    }

    private String saveCrashLog2File(String result) {
        if (Utils.sdAvailible()) {
            try {
                String fileName = getCrashFileName(getInstance());
                File file = new File(fileName);
                FileOutputStream trace = new FileOutputStream(file, true);

                String lineSeparator = System.getProperty("line.separator");
                if (lineSeparator == null) {
                    lineSeparator = "\n";
                }

                // Encode and encrypt the message.
                OutputStreamWriter writer = new OutputStreamWriter(trace,"utf-8");
                writer.write(Utils.encrypt(result));
                writer.flush();

                trace.flush();
                trace.close();
                return fileName;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static String getCrashFileName(Context context) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = formatter.format(new Date());
        File sub = new File(getCrashDir(context), "crash_"
                + time + ".txt");
        String sCrashFileName = sub.getAbsolutePath();
        return sCrashFileName;
    }

    public static File getCrashDir(Context context) {
        return Utils.getAppCacheDir(context, CRASH_CACHE_DIRECTORY_NAME);
    }

    private String buildCrashLog(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(new Date().toString());
        sb.append("\n");
        // Add system and device info.
        sb.append(Utils.buildSystemInfo(getInstance()));
        sb.append("\n");
        sb.append("#-------AndroidRuntime-------");
        sb.append(msg);
        sb.append("\n");
        sb.append("#-------activity_stack-------");
        sb.append("\n");
        //sb.append(buildActivityStack());
        sb.append("#end");

        return sb.toString();
    }

}
