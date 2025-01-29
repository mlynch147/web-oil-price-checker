package com.ml.oilpricechecker.constants;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

    private Constants() {
    }

    public static Map<String, String> fourteenDaysDisplayNameMap = new HashMap<>();
    public static Map<String, String> weeklyDisplayNameMap = new HashMap<>();
    public static Map<String, String> sixMonthsDisplayNameMap = new HashMap<>();

    public static Map<String, String> fourteenDaysFileNameMap = new HashMap<>();
    public static Map<String, String> weeklyFileNameMap = new HashMap<>();
    public static Map<String, String> sixMonthsFileNameMap = new HashMap<>();

    public static String CRAIGS_DISPLAY_NAME = "Craigs Fuels";
    public static String CAMPSIE_DISPLAY_NAME = "Campsie Fuels";
    public static String SCOTTS_DISPLAY_NAME = "McGinleys Oils";
}
