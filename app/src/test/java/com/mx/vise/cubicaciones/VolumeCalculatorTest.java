package com.mx.vise.cubicaciones;

import com.mx.vise.cubicaciones.util.VolumeCalculator;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class VolumeCalculatorTest {

    VolumeCalculator volumeCalculator;

    @Test
    public void testVolumeCalculator() throws Exception {
        volumeCalculator = new VolumeCalculator();

        float [] values = new float[7];

        values[0] = 8.38f;
        values[1] = 2.37f;
        values[2] = 1.6f;
        values[6] = 6f;

        System.out.println(volumeCalculator.getHydraulicJackVolume(values));


    }
}