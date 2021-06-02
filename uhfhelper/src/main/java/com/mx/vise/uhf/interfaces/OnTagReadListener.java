package com.mx.vise.uhf.interfaces;

import com.mx.vise.uhf.entities.Tag;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el viernes 01 de marzo del 2019 a las 11:21
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreos_app-android
 */
public interface OnTagReadListener {
    void onTagRead(Tag tag);
    void onTagReadTimeout();
    void onTagReadFailed();
}
