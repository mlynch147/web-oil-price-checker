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
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MooresFuelsHttpRequestReceiverV2 implements Runnable {

 //   private final TableRow tableRow;
    private final String numberOfLitres;
 //   private final Context context;

    public MooresFuelsHttpRequestReceiverV2(String numberOfLitres) {
        this.numberOfLitres = numberOfLitres;
    }

    @Override
    public void run() {
        String result = doInBackground();
    }

    protected String doInBackground() {
        String returnValue;

        try {
            // Define the URL and form data
            String url = "https://www.mooresfuels.com/wp-admin/admin-ajax.php";

            String action = "delivery_cost";
            String postcode = "BT47";
            String productId = "4790";

            String postData = "action=" + action + "&disPostcode=" + postcode + "&productId=" + productId;

            // Create a URL object
            URL obj = new URL(url);

            // Open a connection to the URL
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set the request method to POST
            con.setRequestMethod("POST");

            // Enable input and output streams
            con.setDoOutput(true);

            // Set the request headers (if needed)
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Get the output stream to write data
            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData);
            writer.flush();
            writer.close();
            os.close();

            // Get the response code
            int responseCode = con.getResponseCode();

            // Check if the request was successful (HTTP status code 200)
            if (responseCode == 200) {
                // Request was successful, you can read the response here
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the response
                System.out.println("Response:\n" + response);

                returnValue = response.toString();
            } else {
                // Request failed
                System.out.println("Request failed with status code: " + responseCode);
                returnValue = "Request failed with status code: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            returnValue = "Exception";
        }

        return returnValue;
    }

    protected void onPostExecute(String result) {

        Pattern pattern;

        String startingPattern = numberOfLitres + "\":";
        if (Integer.parseInt(numberOfLitres) != 1000) {
            pattern = Pattern.compile(Pattern.quote(startingPattern) + "(.*?),");
        } else {
            pattern = Pattern.compile(Pattern.quote(startingPattern) + "(.*?)\\}");

        }
        Matcher matcher = pattern.matcher(result);

        // Find the first match
        if (matcher.find()) {
            String extractedText = matcher.group(1);
            if (!extractedText.contains(".")) {
                extractedText = extractedText + ".00";
            }

            System.out.println(extractedText);

/*            //New code for the weekly comparison
            if (Integer.parseInt(numberOfLitres) == 500) {
                Date today = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(today);

                String weeklyComparisonPrefix = context.getString(R.string.weekly_comparison_filename_prefix);
                FileUtil.writeToFile(context, weeklyComparisonPrefix+"moores.txt", formattedDate, extractedText);
            }*/
        }
    }

}

