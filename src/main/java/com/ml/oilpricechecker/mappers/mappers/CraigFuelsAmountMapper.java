package com.ml.oilpricechecker.mappers.mappers;

public class CraigFuelsAmountMapper {

    public static String mapAmountToValue(final String amount) {
        switch (amount) {
            case "100": return "22";
            case "200": return "1";
            case "300": return "32";
            case "400": return "3";
            case "500": return "4";
            case "600": return "5";
            case "700": return "6";
            case "800": return "7";
            case "900": return "8";
            case "1000": return "30";
            default: return "4";

        }
    }



}
