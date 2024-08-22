package com.ml.oilpricechecker.service.deleteMe;
/*

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;
import com.oilpricechecker.R;
import com.oilpricechecker.Utils;
import com.oilpricechecker.charts.FileUtil;
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringtownFuelsHttpRequestReceiverV2 implements Runnable {

    private final String numberOfLitres;

    public SpringtownFuelsHttpRequestReceiverV2(String numberOfLitres) {
        this.numberOfLitres = numberOfLitres;
    }


    @Override
    public void run() {
        String result = doInBackground();
    }

    protected String doInBackground() {
        String returnValue ;

        try {
            URL url = new URL("https://order.springtownfuels.com/api/Quote/GetQuote" +
                    "?brandId=1&customerTypeId=1&statedUse=1&productCode=K&postcode=BT474BN&quantity=" +
                    numberOfLitres + "&maxSpend=0");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set up a timeout for the connection (optional)
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                returnValue = response.toString();
            } else {
                // Handle the error (e.g., connection.getResponseMessage())
                returnValue = "Error: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnValue = "Error: " + e.getMessage();
        }

        return returnValue;
    }


    protected void onPostExecute(String result) {
        Pattern pattern = Pattern.compile("totalIncVat\":(.*?),");
        Matcher matcher = pattern.matcher(result);

        // Find the first match
        if (matcher.find()) {
            String extractedText = matcher.group(1);
            if (!extractedText.contains(".")) {
                extractedText = extractedText + ".00";
            }

            System.out.println(extractedText);

/*            if (tableRow != null) {
                TextView costTextView = (TextView) tableRow.getChildAt(1);
                TextView priceTextView = (TextView) tableRow.getChildAt(2);

                costTextView.setText(Utils.getPrice(numberOfLitres, extractedText));
                priceTextView.setText(Utils.getPricePerLitre(numberOfLitres, extractedText));
            }*/

            //New code for the weekly comparison
            if (Integer.parseInt(numberOfLitres) == 500) {
                Date today = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(today);

                try {
/*
                String weeklyComparisonPrefix = context.getString(R.string.weekly_comparison_filename_prefix);
                FileUtil.writeToFile(context, weeklyComparisonPrefix+"springtown.txt", formattedDate, extractedText);
*/
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                }
            }
        }
    }

}

