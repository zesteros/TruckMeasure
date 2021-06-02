package com.mx.vise.uhf.tag;

import com.authentication.utils.DataUtils;
import com.google.common.primitives.Bytes;
import com.mx.vise.uhf.entities.Tag;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el jueves 26 de julio del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version UHFTAGREAD
 */

public class TagArguments {

    private static final String RECOMMISSIONING_BITS = "00";
    private static final String LOCK_MASK_ACTION_FLAGS = "0a8aa2";

    private Tag tag;
    private byte memoryBank;
    private short offset;
    private short length;
    private String dataToWrite;

    public byte getMemoryBank() {
        return memoryBank;
    }

    public void setMemoryBank(byte memoryBank) {
        this.memoryBank = memoryBank;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public short getLength() {
        return this.length;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public byte[] getArgumentsForWrite(){
        return Bytes.concat(
                //ap from tag to write
                DataUtils.hexStringTobyte(getTag().getAP()),
                //el of epc
                DataUtils.short2byte((short) (getTag().getEpc().length()/2)),
                //epc to write
                DataUtils.hexStringTobyte(getTag().getEpc()),
                //mb to write
                new byte[]{getMemoryBank()},
                //offset to write
                DataUtils.short2byte(getOffset()),
                //length to write
                DataUtils.short2byte(getLength()),
                //data to write
                DataUtils.hexStringTobyte(getDataToWrite())
        );
    }

    public byte[] getArgumentsForRead(){
        return Bytes.concat(
                //ap from tag to write
                DataUtils.hexStringTobyte(getTag().getAP()),
                //el of epc
                DataUtils.short2byte((short) (getTag().getEpc().length()/2)),
                //epc to write
                DataUtils.hexStringTobyte(getTag().getEpc()),
                //mb to write
                new byte[]{getMemoryBank()},
                //offset to write
                DataUtils.short2byte(getOffset()),
                //length to write
                DataUtils.short2byte(getLength())
        );
    }

    public byte[] getArgumentsForKill(){
        return Bytes.concat(
                //ap from tag to write
                DataUtils.hexStringTobyte(getTag().getKillKey()),
                //el of epc
                DataUtils.short2byte((short) (getTag().getEpc().length()/2)),
                //epc to write
                DataUtils.hexStringTobyte(getTag().getEpc()),
                //recommissioning bits
                DataUtils.hexStringTobyte(getRecom())
        );
    }

    public byte[] getArgumentsForLock(){
        return Bytes.concat(
                //ap from tag to write
                DataUtils.hexStringTobyte(getTag().getAP()),
                //el of epc
                DataUtils.short2byte((short) (getTag().getEpc().length()/2)),
                //epc to write
                DataUtils.hexStringTobyte(getTag().getEpc()),
                //recommissioning bits
                DataUtils.hexStringTobyte(getLockMaskActionFlags())
        );
    }

    public String getDataToWrite() {
        return dataToWrite;
    }

    public void setDataToWrite(String dataToWrite) {
        this.dataToWrite = dataToWrite;
    }

    public void setLength(short length) {
        this.length = length;
    }


    public String getRecom() {
        return RECOMMISSIONING_BITS;
    }

    public String getLockMaskActionFlags(){
        return LOCK_MASK_ACTION_FLAGS;
    }
}
