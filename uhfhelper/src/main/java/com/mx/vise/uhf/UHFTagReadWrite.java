package com.mx.vise.uhf;

import android.util.Log;

import com.authentication.utils.DataUtils;
import com.mx.vise.uhf.entities.Key;
import com.mx.vise.uhf.entities.Tag;
import com.mx.vise.uhf.tag.Sector;
import com.mx.vise.uhf.tag.TagArguments;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import android_serialport_api.UHFHXAPI;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el jueves 26 de julio del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version UHFTAGREAD
 */

public class UHFTagReadWrite {

    public static final String ERROR_CODE = "09";
    private static final String TAG = "VISE";
    public static final String DEFAULT_AP = "00000000";
    private static final String SUCCESS_CODE = "00";
    private final UHFHXAPI api;
    private final Tag tag;

    //Number of memory bank to access and kill keys
    public static final byte MEMORY_BANK_KEYS = 0;

    //Number of memory bank to other fields
    public static final byte MEMORY_BANK_EPC = 1;
    public static final byte MEMORY_BANK_TID = 2;
    public static final byte MEMORY_BANK_USER = 3;

    //Offset to write in every bank
    public static final short OFFSET_KP = 0;
    public static final short OFFSET_TID = 2;
    public static final short OFFSET_AP_AND_EPC = 2;

    //length to write in every bank
    public static final short LENGTH_KEYS = 2;
    public static final short LENGTH_EPC = 6;
    public static final short LENGTH_UD = 32;
    public static final short LENGTH_TID_TYPE_1 = 4;
    public static final short LENGTH_TID_TYPE_2 = LENGTH_TID_TYPE_1 * 2;


    /**
     * @param tag the tag to write
     * @param api the mApi to r/w
     */
    public UHFTagReadWrite(Tag tag, UHFHXAPI api) {
        this.tag = tag;
        this.api = api;
    }

    /**
     * @param tagArguments the tag arguments for write
     * @return if write was successful
     */
    public boolean write(TagArguments tagArguments) {

        UHFHXAPI.Response response = api.writeTypeCTagData(tagArguments.getArgumentsForWrite());

        if (response.result == UHFHXAPI.Response.RESPONSE_PACKET && response.data != null)
            return DataUtils.toHexString(response.data).equals(SUCCESS_CODE);

        return false;
    }

    /**
     * @param tagArguments the tag arguments for write
     * @return if write was successful
     */
    public String read(TagArguments tagArguments) {
        UHFHXAPI.Response response = api.readTypeCTagData(tagArguments.getArgumentsForRead());
        return response.data != null ? DataUtils.toHexString(response.data) : null;
    }

    /**
     * @param key    the key to write
     * @param offset the position in memory
     * @return if key writing was correct
     */
    public boolean writeKey(String key, short offset) {
        if (key.length() / 4 == LENGTH_KEYS) {
            TagArguments tagArguments = new TagArguments();

            tagArguments.setTag(this.tag);
            tagArguments.setDataToWrite(key);
            tagArguments.setMemoryBank(MEMORY_BANK_KEYS);
            tagArguments.setOffset(offset);
            tagArguments.setLength(LENGTH_KEYS);

            return write(tagArguments);
        } else
            Log.d("VISE", "The length of the key to set must be 8 hex numbers");
        return false;
    }

    /**
     * @param key the key to write as access key
     * @return if key writing was correct
     */
    public boolean writeAccessKey(String key) {
        key = stringToHex(key);
        return writeKey(key, OFFSET_AP_AND_EPC);
    }

    /**
     * @param key the key to write as access key
     * @return if key writing was correct
     */
    public boolean writeKillPassword(String key) {
        return writeKey(key, OFFSET_KP);
    }

    /**
     * @param newEpc the new epc to be recorded
     * @return if epc writing was correct
     */
    public boolean changeEPC(String newEpc) {
        if (newEpc.length() / 4 == LENGTH_EPC) {
            TagArguments tagArguments = new TagArguments();

            tagArguments.setTag(this.tag);
            tagArguments.setDataToWrite(newEpc);
            tagArguments.setMemoryBank(MEMORY_BANK_EPC);
            tagArguments.setOffset(OFFSET_AP_AND_EPC);
            tagArguments.setLength(LENGTH_EPC);

            return write(tagArguments);
        } else
            Log.d("VISE", "The length of the key to set must be 8 hex numbers");
        return false;
    }

    /**
     * @param userData the user data to be recorded
     * @return if user data writing was correct
     */
    public boolean writeToUserData(String userData) {

        short length = (short) (userData.length() / 4);

        TagArguments tagArguments = new TagArguments();
        tagArguments.setTag(this.tag);
        tagArguments.setDataToWrite(userData);
        tagArguments.setMemoryBank(MEMORY_BANK_USER);
        tagArguments.setOffset((short) 0);
        tagArguments.setLength(length);

        return write(tagArguments);
    }

    /**
     * @param userData the user data to be recorded
     * @return if user data writing was correct
     */
    public boolean writeToUserData(String userData, short offset, short length) {
        TagArguments tagArguments = new TagArguments();
        tagArguments.setTag(this.tag);
        tagArguments.setDataToWrite(userData);
        tagArguments.setMemoryBank(MEMORY_BANK_USER);
        tagArguments.setOffset(offset);
        tagArguments.setLength(length);

        return write(tagArguments);
    }

    /**
     * @param sector the new sector
     * @return success writing
     */
    public boolean writeToUserData(Sector sector) {
        TagArguments tagArguments = new TagArguments();

        tagArguments.setTag(this.tag);

        tagArguments.setDataToWrite(sector.getData());

        tagArguments.setMemoryBank(MEMORY_BANK_USER);

        tagArguments.setOffset((short) sector.getOffset());
        tagArguments.setLength((short) sector.getLength());

        if (!write(tagArguments)) return false;

        String dataWrited = readUserData((short) sector.getOffset(), (short) sector.getLength());

        if (dataWrited == null) return false;

        return dataWrited.equals(sector.getData());
    }

    /**
     * @return the tid from the tag
     */
    public String readTID() {
        TagArguments tagArguments = new TagArguments();

        tagArguments.setTag(this.tag);
        tagArguments.setMemoryBank(MEMORY_BANK_TID);
        tagArguments.setOffset(OFFSET_TID);
        tagArguments.setLength(LENGTH_TID_TYPE_1);

        return read(tagArguments);
    }

    /**
     * @return the access password
     */
    public String readAP() {

        TagArguments tagArguments = new TagArguments();
        tagArguments.setTag(this.tag);
        tagArguments.setMemoryBank(MEMORY_BANK_KEYS);
        tagArguments.setOffset(OFFSET_AP_AND_EPC);
        tagArguments.setLength(LENGTH_KEYS);

        return read(tagArguments);
    }

    /**
     * @return the user data read from tag
     */
    public String readUserData() {
        TagArguments tagArguments = new TagArguments();

        tagArguments.setTag(this.tag);
        tagArguments.setMemoryBank(MEMORY_BANK_USER);
        tagArguments.setOffset((short) 0);
        tagArguments.setLength(LENGTH_UD);

        return read(tagArguments);
    }

    /**
     * @return the user data read from tag
     */
    public String readUserData(short length) {
        TagArguments tagArguments = new TagArguments();

        tagArguments.setTag(this.tag);
        tagArguments.setMemoryBank(MEMORY_BANK_USER);
        tagArguments.setOffset((short) 0);
        tagArguments.setLength(length);

        return read(tagArguments);
    }

    /**
     * @return the user data read from tag
     */
    public String readUserData(short offset, short length) {
        TagArguments tagArguments = new TagArguments();

        tagArguments.setTag(this.tag);
        tagArguments.setMemoryBank(MEMORY_BANK_USER);
        tagArguments.setOffset(offset);
        tagArguments.setLength(length);

        return read(tagArguments);
    }


    /**
     * @return if tag was killed successfully
     */
    public boolean killTag() {
        TagArguments tagArguments = new TagArguments();
        tagArguments.setTag(this.tag);

        UHFHXAPI.Response response = api.killTypeCTag(tagArguments.getArgumentsForKill());

        if (response.result == UHFHXAPI.Response.RESPONSE_PACKET && response.data != null)
            return DataUtils.toHexString(response.data).equals(SUCCESS_CODE);

        return false;
    }

    /**
     * @return if tag was locked successfully
     */
    public boolean lockTag() {
        TagArguments tagArguments = new TagArguments();
        tagArguments.setTag(this.tag);

        UHFHXAPI.Response response = api.lockTypeCTag(tagArguments.getArgumentsForLock());

        if (response.result == UHFHXAPI.Response.RESPONSE_PACKET && response.data != null)
            return DataUtils.toHexString(response.data).equals(SUCCESS_CODE);

        return false;
    }

    /**
     * @param data los datos a escribir
     * @return si la escritura fue exitosa
     */
    public boolean writeData(String data, boolean isVirgin) {
        if (isVirgin) {
            /*escribe contraseña*/
            writeAccessKey(new Key());
            //bloquea el tag con la nueva contraseña
            lockTag();
        }
        //escribe los datos
        return writeToUserData(data);
    }

    public void secureTag() {
        /*escribe contraseña*/
        /*
         * Si no es virgen aseguralo con pass
         * */
        String ap;

        boolean tryToWriteAP = true;
        while (tryToWriteAP) {
            writeAccessKey(new Key());
            this.tag.setAP(new Key());
            ap = readAP();
            tryToWriteAP = ap != null ? !ap.equals(new Key().key) : true;
            this.tag.setAP(new Key(SUCCESS_CODE));
        }
        this.tag.setAP(new Key());
        //bloquea el tag con la nueva contraseña
        while (!lockTag()) {
            Log.i(TAG, "secureTag: locking tag....");
        }

    }

    /**
     * @param key the kir
     * @return
     */
    public boolean writeAccessKey(Key key) {
        return writeKey(key.key, OFFSET_AP_AND_EPC);
    }


    /**
     * @param text the string
     * @return the hex value
     */
    public static String stringToHex(String text) {
        if (text != null) {
            //Change encoding according to your need
            try {
                return String.format("%04x", new BigInteger(1, text.getBytes("UTF8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param hex the hex string
     * @return a UTF formed string
     */
    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char) decimal);
        }
        return sb.toString();
    }

}
