package com.mx.vise.uhf.tasks;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.authentication.utils.DataUtils;
import com.mx.vise.uhf.UHFTagReadWrite;
import com.mx.vise.uhf.entities.Key;
import com.mx.vise.uhf.entities.Tag;
import com.mx.vise.uhf.entities.UserDataStatus;
import com.mx.vise.uhf.interfaces.OnTagReadListener;
import com.mx.vise.uhf.util.AESencrp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import android_serialport_api.UHFHXAPI;

import static com.mx.vise.uhf.UHFHelper.LIMIT_READ_WRITE;
import static com.mx.vise.uhf.UHFTagReadWrite.hexToString;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el viernes 01 de marzo del 2019 a las 10:56
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public class ReadingTagTask implements Runnable {

    private static final String TAG = "VISE";
    public static final String CODE = "1";
    private final ExecutorService executor;
    private final OnTagReadListener onTagReadListener;
    private final long timeout;
    private final Context context;
    private UHFHXAPI api;
    private String epc;
    private long startTime;

    public ReadingTagTask(Context context, UHFHXAPI api, ExecutorService executor, OnTagReadListener onTagReadListener, long timeout) {
        this.api = api;
        this.executor = executor;
        this.onTagReadListener = onTagReadListener;
        this.timeout = timeout > 0 ? timeout : 15000;
        this.context = context;
    }

    @Override
    public void run() {
        api.startAutoRead2A(0x22, new byte[]{0x00, 0x01}, new UHFHXAPI.AutoRead() {
            @Override
            public void timeout() {

            }

            @Override
            public void start() {

            }

            @Override
            public void processing(byte[] data) {
                String epc = DataUtils.toHexString(data).substring(4);

                ReadingTagTask.this.epc = epc;
            }

            @Override
            public void end() {
                if (ReadingTagTask.this.epc == null) {
                    if (!isTimeout())
                        try {
                            executor.execute(ReadingTagTask.this);
                        } catch (RejectedExecutionException e) {
                            e.printStackTrace();
                            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onTagReadListener.onTagReadFailed();
                                }
                            });
                        }
                    else {
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onTagReadListener.onTagReadTimeout();
                            }
                        });
                    }
                } else {
                    final Tag tag = new Tag();
                    tag.setEPC(ReadingTagTask.this.epc);
                    tag.setAP(new Key(CODE));
                    UHFTagReadWrite uhfTagReadWrite = new UHFTagReadWrite(tag, api);
                    tag.setTid(uhfTagReadWrite.readTID());
                    String userData = uhfTagReadWrite.readUserData(LIMIT_READ_WRITE);
                    tag.setTagFlags(uhfTagReadWrite.readUserData(LIMIT_READ_WRITE, (short)1));

                    if (userData != null) {

                        int countZeros = 0;

                        for (int i = 0; i < userData.length(); i++)
                            if (userData.charAt(i) == '0')
                                countZeros++;
                        Log.i(TAG, "processing: zeros amount:" + countZeros);
                        if (countZeros >= 448) {
                            userData = null;
                            tag.setTagReadStatus(UserDataStatus.USER_DATA_IS_EMPTY);
                        } else {
                            try {
                                userData = AESencrp.decrypt(hexToString(userData).replace(" ", ""));
                                tag.setTagReadStatus(UserDataStatus.CORRECT_READ);
                            } catch (Exception e) {
                                userData = null;
                                tag.setTagReadStatus(UserDataStatus.CANNOT_DECRYPT);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        tag.setTagReadStatus(UserDataStatus.COMMUNICATION_ERROR);
                    }
                    tag.setUserData(userData);
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onTagReadListener.onTagRead(tag);
                        }
                    });

                }
            }
        });
    }

    public boolean isTimeout() {
        return (System.currentTimeMillis() - getStartTime()) > timeout;
    }

    private long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
