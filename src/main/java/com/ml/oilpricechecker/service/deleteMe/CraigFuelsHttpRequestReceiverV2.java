package com.ml.oilpricechecker.service.deleteMe;

/*
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import com.oilpricechecker.AlarmReceiver;
import com.oilpricechecker.MainActivity;
import com.oilpricechecker.R;
import com.oilpricechecker.Utils;
import com.oilpricechecker.charts.FileUtil;
import com.oilpricechecker.httpReceiversV2.mappers.CraigFuelsAmountMapper;
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraigFuelsHttpRequestReceiverV2 implements Runnable {

//    private final TableRow tableRow;
//    private final Context context;
    private int hour;
    //private final boolean checkWriteToFileAlarm;

    private String numberOfLitres = "500";


 /*   public CraigFuelsHttpRequestReceiverV2(Context context, int hour) {
        this.tableRow = null;
        this.context = context;
        this.hour = hour;
        this.checkWriteToFileAlarm = false;
    }

    public CraigFuelsHttpRequestReceiverV2(Context context, TableRow tableRow, String numberOfLitres) {
        this.context = context;
        this.tableRow = tableRow;
        this.numberOfLitres = numberOfLitres;
        this.checkWriteToFileAlarm = false;
    }

    public CraigFuelsHttpRequestReceiverV2(Context context, String numberOfLitres) {
        this.context = context;
        this.tableRow = null;
        this.numberOfLitres = numberOfLitres;
        this.checkWriteToFileAlarm = true;
    }*/

    @Override
    public void run() {
        String result = doInBackground();

        // UI-related operations are correctly executed on the UI thread,
        // while non-UI processing can be done directly without the need for
        // posting to the UI thread.

            onPostExecute(result);

    }

    protected String doInBackground() {
        String returnValue;

        try {
            // Define the URL and form data
            String url = "https://www.craigfuels.com/purchase";
            String county = "4";
        //    String required_quantity = CraigFuelsAmountMapper.MapAmountToValue(numberOfLitres);
      //      String postData = "county=" + county + "&required_quantity=" + required_quantity;

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
        //    writer.write(postData);
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
        Pattern pattern = Pattern.compile("£(.*?)<");
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
                writeChartDataToFile(extractedText);

            } else if (checkWriteToFileAlarm) {
                writeChartDataToFile(extractedText);
            }
            else {
                createNotification(extractedText);
                setNextAlarm();
            }*/
        }
    }

}

