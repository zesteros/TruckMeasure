package com.mx.vise.cubicaciones.util;

public class VolumeCalculator {
    public float getBox1Volume(float[] dimensions) {
        return calculateVolume(dimensions, 0);
    }

    public float getBox2Volume(float[] dimensions) {
        return calculateVolume(dimensions, 3);
    }

    public float getHydraulicJackVolume(float[] hydraulicJackDimensions) {
        return getBox1Volume(hydraulicJackDimensions);
    }

    public String getCurveForTicket(float[] dimensions) {
        return String.format("%.2f", dimensions[6]) + "m " + "=" +
                String.format("%.2f", getCurve(dimensions)) + "m3 ";
    }

    public String getBoxVolumeForTicket(float[] dimensions, int start) {

        String data = "";
        data += "L:" + String.format("%.2f", dimensions[start]) + "m ";
        data += "A:" + String.format("%.2f", dimensions[start + 1]) + "m ";
        data += "H:" + String.format("%.2f", dimensions[start + 2]) + "m ";

        data += "=" + (
                start < 3 ?
                String.format("%.2f", getBox1Volume(dimensions)) :
                String.format("%.2f", getBox2Volume(dimensions)))
                + "m3 ";
        return data;
    }

    public String getVolumeTotalForTicket(float[] dimensions1, float[] dimensions2, float discount) {
        return String.format("%.2f", getVolumeTotal(dimensions1, dimensions2, discount)) + "m3";
    }

    public float getVolumeTotal(float[] dimensions1, float[] dimensions2, float discount) {
        float boxVolume = getBox1Volume(dimensions1) + getBox2Volume(dimensions1);
        float curve = getCurve(dimensions1);
        float hydraulicJackVolume = getHydraulicJackVolume(dimensions2);
        return boxVolume - curve - hydraulicJackVolume + discount;
    }

    public float getCurve(float[] dimensions) {

        float l1 = dimensions[0];
        float l2 = dimensions[3];
        float lc = dimensions[6];

        float vc = (float) ((2 * (l1 + l2)) * ((Math.PI / 4) * (Math.pow((2 * lc) / Math.PI, 2))));

        float vs = (float) ((2 * (l1 + l2)) * Math.pow((2 * lc) / Math.PI, 2));

        return vs - vc;

    }

    public float calculateVolume(float[] dimensions, int start) {
        float volume = 1;
        for (int i = start; i < (start + 3); i++)
            volume *= dimensions[i];
        return volume;
    }

}
