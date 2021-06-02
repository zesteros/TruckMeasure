package com.mx.vise.zebraprinterandroid.interfaces;

import com.mx.vise.zebraprinterandroid.enums.PrintStatus;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el lunes 15 de octubre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */
public interface OnPrintListener {
    void onPrintSuccess();
    void onPrintFailed(PrintStatus status);
}
