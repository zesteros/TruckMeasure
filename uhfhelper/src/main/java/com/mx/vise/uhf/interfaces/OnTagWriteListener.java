package com.mx.vise.uhf.interfaces;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por Angelo el viernes 01 de marzo del 2019 a las 12:25
 *
 * @author Angelo de Jesus Loza Martinez
 * @version cubicaciones
 */
public interface OnTagWriteListener {
    void onTagWriteSuccess();

    void onTagWriteFailed(boolean overwriteIntent);

    void onTagWriteTimeout();
}
