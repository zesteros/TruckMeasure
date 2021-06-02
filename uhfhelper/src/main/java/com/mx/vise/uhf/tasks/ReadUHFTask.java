package com.mx.vise.uhf.tasks;

import android.os.Handler;
import android.util.Log;

import com.authentication.utils.DataUtils;
import com.mx.vise.uhf.UHFTagReadWrite;
import com.mx.vise.uhf.entities.Key;
import com.mx.vise.uhf.entities.Tag;
import com.mx.vise.uhf.entities.UserDataStatus;
import com.mx.vise.uhf.util.AESencrp;

import java.util.concurrent.ExecutorService;

import android_serialport_api.UHFHXAPI;

import static com.mx.vise.uhf.UHFHelper.MSG_SHOW_EPC_INFO;
import static com.mx.vise.uhf.UHFHelper.MSG_SHOW_TID;
import static com.mx.vise.uhf.UHFHelper.TIMEOUT;
import static com.mx.vise.uhf.UHFTagReadWrite.DEFAULT_AP;
import static com.mx.vise.uhf.UHFTagReadWrite.hexToString;


public class ReadUHFTask extends UHFTaskBase {

    /**
     * @param api     the mApi object XUHFAPI
     * @param handler the handler (to send messages)
     * @param pool    the thread pool
     */
    public ReadUHFTask(UHFHXAPI api, Handler handler, ExecutorService pool) {
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
            public void processing(byte[] data) {/*
             * Saca el epc
             * */
                String epc = DataUtils.toHexString(data).substring(4);
                /*
                 * Establece la contraseña default
                 * */
                Tag tag = new Tag().setEPC(epc).setAP(new Key(DEFAULT_AP));
                /*
                 * se asume que ya esta bloqueado el tag, es por ello que si no se tiene unos datos a escribir
                 * entonces se manda la contraseña establecida para acceder al tid (para combustible)
                 * */
                if (!readTIDOnly) {

                    String tid = new UHFTagReadWrite(tag, api).readTID();
                    hMsg.obtainMessage(MSG_SHOW_EPC_INFO, tid).sendToTarget();

                } else if (readTIDOnly) {
                    String tid = new UHFTagReadWrite(tag, api).readTID();
                    String userData = new UHFTagReadWrite(tag, api).readUserData((short) 128);

                    if (userData != null) {

                        int countZeros = 0;

                        for (int i = 0; i < userData.length(); i++)
                            if (userData.charAt(i) == '0')
                                countZeros++;
                        Log.i(TAG, "processing: zeros amount:"+countZeros);
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

                    tag.setTid(tid);
                    tag.setEPC(epc);
                    tag.setUserData(userData);

                    hMsg.obtainMessage(MSG_SHOW_TID, tag).sendToTarget();
                }


            }


            @Override
            public void end() {
                if (!isTimeout()) {
                    hMsg.sendEmptyMessage(TIMEOUT);
                }
                if (isKeepRunning()) pool.execute(ReadUHFTask.this);
            }
        });
    }
}
