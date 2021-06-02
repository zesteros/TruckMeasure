package com.mx.vise.uhf.interfaces;

import com.mx.vise.uhf.entities.Tag;

import java.util.ArrayList;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el miércoles 23 de mayo del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version combustible
 */

public interface OnUHFDetectListener {
    void onUHFSDetected(ArrayList<String> tids, boolean isValid);
    void onTagWriteSuccess();
    void onTagWriteFailed(boolean overwriteIntent);
    void onTagRead(Tag tag);
    void onTagWriteTimeout();
    void onTagReadTimeout();
}
