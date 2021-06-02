package com.mx.vise.uhf.tag;

import com.mx.vise.uhf.UHFTagReadWrite;
import com.mx.vise.uhf.interfaces.OnTagWriteInfo;
import com.mx.vise.uhf.util.AESencrp;


/**
 * Clase para convertir los datos del flujo de cubicaciones a una sola linea de información.
 */
public class TagData {
    private static final String TAG = "VISE";
    private final OnTagWriteInfo onTagWriteInfo;
    private String EPC;

    /**
     * @param mFlowObject the flow object to parse
     */
    public TagData(OnTagWriteInfo mFlowObject) {
        this.onTagWriteInfo = mFlowObject;
    }

    /**
     * @return the data to write in tag
     */
    public String getDataToWrite() {
        String data = null;
        try {
            data = AESencrp.encrypt(onTagWriteInfo.getDataToWrite());
            if (UHFTagReadWrite.stringToHex(data).length() % 4 != 0)
                data = makeEven(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UHFTagReadWrite.stringToHex(data);
    }

    /**
     * @param data the data to parse into even string
     * @return the data in even form
     */
    public String makeEven(String data) {
        while (UHFTagReadWrite.stringToHex(data).length() % 4 != 0)
            data += " ";
        return data;
    }

    public String getTestData(){
        return UHFTagReadWrite.stringToHex("hola123456789111");
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }
}
