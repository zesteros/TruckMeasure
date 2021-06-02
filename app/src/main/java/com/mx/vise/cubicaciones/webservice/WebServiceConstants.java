package com.mx.vise.cubicaciones.webservice;

/**
 * Es una clase que maneja puras variables estaticas esto para manejar la seguridad y las URLS que se manejan
 * Created by mpalomino on 23/12/17.
 */

public class WebServiceConstants {

    public static final String HOST = "https://tomcat.vise.com.mx";
    //public static final String HOST = "http://192.168.60.23:8080";

    public static final String PROJECT = "/acarreos_app";
    //public static final String PROJECT = "/acarreos";

    public static final String REQUEST = "/requests";

    public static final String CONTROLLER_PUT_DATA = "/putData";

    public static final String CONTROLLER_GET_DATA = "/getData";

    public static final String URL_PUT_DATA = HOST + PROJECT + REQUEST + CONTROLLER_PUT_DATA;

    public static final String URL_GET_DATA = HOST + PROJECT + REQUEST + CONTROLLER_GET_DATA;

//"https://tomcat.vise.com.mx/combustiblevise/peticion/combustible/empleadoscom/v1.0/empleadologeo
}
