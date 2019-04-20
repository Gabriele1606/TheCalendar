package com.example.gabri.thecalendar.Model;

import android.support.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Gabri on 20/04/19.
 */

public final class Colors {



    public static final String GREEN="#A0DFE28E";

    public static final String LIGHTBLUE="#A08BC3EC";

    public static final String LIGHTGRAY="#40E4DEDE";

    public static final String RED="#3DEB3030";

    public static final String YELLOW="#40D4A106";

    public static final String MAGENTA="#50F069C7";

    public static final String BLUE="#4000189C";

    public static final String WATER="#400BC9BA";

    public static final String ORANGE="#41FF6D00";

    public static final String GRAY="#6A959696";



    public static List<String> getColorSet() {
        List<String> colorsSet = new ArrayList<>();

        colorsSet.add(RED);
        colorsSet.add(LIGHTGRAY);
        colorsSet.add(YELLOW);
        colorsSet.add(LIGHTBLUE);
        colorsSet.add(GREEN);
        colorsSet.add(MAGENTA);
        colorsSet.add(BLUE);
        colorsSet.add(WATER);
        colorsSet.add(ORANGE);
        colorsSet.add(GRAY);

        return colorsSet;
    }


}
