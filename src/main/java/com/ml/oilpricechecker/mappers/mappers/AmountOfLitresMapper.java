package com.ml.oilpricechecker.mappers.mappers;

public final class AmountOfLitresMapper {

    private AmountOfLitresMapper() {
    }
    public static String mapAmountOfLitres(final String mapper, final int amountOfLitres) {

        if (mapper == null) {
            return String.valueOf(amountOfLitres);
        }

        switch (mapper) {
            case "CraigsMapper":
                return CraigFuelsAmountMapper.mapAmountToValue(amountOfLitres);
            default:
                return String.valueOf(amountOfLitres);
        }
    }
}
