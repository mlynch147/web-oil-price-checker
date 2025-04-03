package com.ml.oilpricechecker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PriceUtilities {

    private PriceUtilities() {
    }

    public static String extractPriceFromContent(final String content, final Pattern pattern) {
        if (content == null) {
            return "N/A";
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String extractedText = "£" + matcher.group(1);
            if (!extractedText.contains(".")) {
                extractedText += ".00";
            } else {
                int indexOfDecimal = extractedText.indexOf(".");
                String pennies = extractedText.substring(indexOfDecimal + 1);
                if (pennies.length() == 1) {
                    extractedText += "0";
                }
            }
            return extractedText;
        }
        return "N/A";
    }
}
