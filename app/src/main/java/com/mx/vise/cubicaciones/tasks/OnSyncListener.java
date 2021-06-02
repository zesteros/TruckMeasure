package com.mx.vise.cubicaciones.tasks;

import com.mx.vise.cubicaciones.pojos.CubageProcessPOJO;

import java.util.ArrayList;

/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el martes 02 de octubre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */
public interface OnSyncListener {
    void onSyncSuccessful(ArrayList<CubageProcessPOJO> cubagesFailed);
    void onSyncFailed(SyncStatus syncStatus, ArrayList<CubageProcessPOJO> cubagesFailed);
}
