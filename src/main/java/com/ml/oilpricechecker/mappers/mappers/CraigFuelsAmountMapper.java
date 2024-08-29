package com.ml.oilpricechecker.mappers.mappers;

public final class CraigFuelsAmountMapper {

    public static final int ONE_HUNDRED = 100;
    public static final int TWO_HUNDRED = 200;
    public static final int THREE_HUNDRED = 300;
    public static final int FOUR_HUNDRED = 400;
    public static final int FIVE_HUNDRED = 500;
    public static final int SIX_HUNDRED = 600;
    public static final int SEVEN_HUNDRED = 700;
    public static final int EIGHT_HUNDRED = 800;
    public static final int NINE_HUNDRED = 900;
    public static final int ONE_THOUSAND = 1000;

    private CraigFuelsAmountMapper() {
    }

    public static String mapAmountToValue(final int amount) {
        switch (amount) {
            case ONE_HUNDRED: return "22";
            case TWO_HUNDRED: return "1";
            case THREE_HUNDRED: return "32";
            case FOUR_HUNDRED: return "3";
            case FIVE_HUNDRED: return "4";
            case SIX_HUNDRED: return "5";
            case SEVEN_HUNDRED: return "6";
            case EIGHT_HUNDRED: return "7";
            case NINE_HUNDRED: return "8";
            case ONE_THOUSAND: return "30";
            default: return "4";

        }
    }



}
