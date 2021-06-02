package com.mx.vise.cubicaciones.tasks;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el martes 25 de septiembre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */

public interface OnProgressUpdate {
     void publishProgress(int progress, int secondaryProgress, int totalElements);
     void publishProgress(Progress progress);
}
