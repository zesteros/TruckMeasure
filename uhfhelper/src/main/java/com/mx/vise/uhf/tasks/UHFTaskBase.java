package com.mx.vise.uhf.tasks;

import android.os.Handler;

import com.mx.vise.uhf.tag.TagData;
import com.mx.vise.uhf.UHFHelper;

import java.util.concurrent.ExecutorService;

import android_serialport_api.SerialPortManager;
import android_serialport_api.UHFHXAPI;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el miércoles 23 de mayo del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version combustible
 */

public class UHFTaskBase implements Runnable {

    protected static final String TAG = "VISE";
    protected final UHFHXAPI api;
    protected final ExecutorService pool;
    protected Handler hMsg;
    protected boolean keepRunning;
    protected long startTime;
    public static long timeout = 10000;
    protected TagData tagData;
    protected boolean canOverwrite;
    protected boolean readTIDOnly;

    /**
     * @param api     the mApi object XUHFAPI
     * @param handler the handler (to send messages)
     * @param pool    the thread pool
     */
    public UHFTaskBase(UHFHXAPI api, Handler handler, ExecutorService pool) {
        this.api = api;
        this.hMsg = handler;
        this.pool = pool;
        this.setKeepRunning(true);
    }

    @Override
    public void run() {


    }

    protected boolean canOverwrite() {
        return canOverwrite;
    }

    protected boolean isTimeout() {
        return (System.currentTimeMillis() - getStartTime()) < timeout;
    }

    public void stopTask() {
        if (UHFHelper.dialog != null)
            UHFHelper.dialog.dismiss();
        setKeepRunning(false);
        if (SerialPortManager.getInstance().isOpen())
            SerialPortManager.getInstance().closeSerialPort();
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setTagData(TagData tagData) {
        this.tagData = tagData;
    }

    public TagData getTagData() {
        return tagData;
    }

    public void canOverwrite(boolean canOverwrite) {
        this.canOverwrite = canOverwrite;
    }

    public void readTIDOnly(boolean readTIDOnly) {
        this.readTIDOnly = readTIDOnly;
    }
}
