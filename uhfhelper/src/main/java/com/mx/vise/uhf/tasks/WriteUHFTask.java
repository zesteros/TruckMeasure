package com.mx.vise.uhf.tasks;

import android.os.Handler;

import com.authentication.utils.DataUtils;
import com.mx.vise.uhf.UHFTagReadWrite;
import com.mx.vise.uhf.entities.Key;
import com.mx.vise.uhf.entities.Tag;

import java.util.concurrent.ExecutorService;

import android_serialport_api.UHFHXAPI;

import static com.mx.vise.uhf.UHFHelper.CANNOT_OVERWRITE;
import static com.mx.vise.uhf.UHFHelper.MSG_SHOW_EPC_INFO;
import static com.mx.vise.uhf.UHFHelper.MSG_TAG_WRITE_SUCCESS;
import static com.mx.vise.uhf.UHFHelper.TIMEOUT;
import static com.mx.vise.uhf.UHFHelper.TIMEOUT_WRITE;
import static com.mx.vise.uhf.UHFHelper.WRITE_TIMEOUT;
import static com.mx.vise.uhf.UHFTagReadWrite.DEFAULT_AP;
import static com.mx.vise.uhf.UHFTagReadWrite.ERROR_CODE;


public class WriteUHFTask extends UHFTaskBase {
    /**
     * @param api     the mApi object XUHFAPI
     * @param handler the handler (to send messages)
     * @param pool    the thread pool
     */
    public WriteUHFTask(UHFHXAPI api, Handler handler, ExecutorService pool) {
        super(api, handler, pool);
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
                /*
                *
                * Saca el epc
                *
                * */
                String epc = DataUtils.toHexString(data).substring(4);
                /*
                 * Establece la contraseña default
                 * */
                Tag tag = new Tag().setEPC(epc).setAP(new Key(DEFAULT_AP));

                UHFTagReadWrite uhfTagReadWrite = new UHFTagReadWrite(tag, api);

                /*
                 * Si no la lee con la contraseña default cambia a la establecida
                 * (ya se sabe que el tag no es virgen)
                 * */
                String ap = uhfTagReadWrite.readAP();
                if (ap != null)
                    if (ap.equals(ERROR_CODE)) {
                        /*
                         * Si se ha establecido que se puede sobreescribir entonces sobreescribe.
                         * (es falso por default)
                         * si no regresa error.
                         * */
                        if (canOverwrite())
                            tag.setAP(new Key());
                        else {
                            hMsg.sendEmptyMessage(CANNOT_OVERWRITE);
                            this.end();
                            setKeepRunning(false);
                            return;
                        }
                    }
                boolean writeSuccess = uhfTagReadWrite.writeData(getTagData().getDataToWrite(),true);
                //boolean writeSuccess = uhfTagReadWrite.writeData(getTagData().getTestData());

                /*
                 * Manda resultado de intentar escribir
                 * */
                hMsg.obtainMessage(MSG_TAG_WRITE_SUCCESS, writeSuccess).sendToTarget();
                this.end();
                setKeepRunning(false);
                return;
            }


            @Override
            public void end() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isKeepRunning()) pool.execute(WriteUHFTask.this);

                if (!isTimeout()) {
                    if (getTagData() == null)
                        hMsg.sendEmptyMessage(TIMEOUT);
                    else hMsg.sendEmptyMessage(TIMEOUT_WRITE);
                }
            }
        });

    }

    @Override
    protected boolean isTimeout() {
        return (System.currentTimeMillis() - getStartTime()) < WRITE_TIMEOUT;
    }
}
