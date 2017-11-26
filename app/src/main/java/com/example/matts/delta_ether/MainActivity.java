package com.example.matts.delta_ether;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;
import android.app.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private double quantityPurchased;
    private double purchasePrice;
    public static SharedPreferences sp;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static double price;
    public static double total;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private final String TAG = "troubleshoot";
    private double poloniexPrice;
    private double krakenPrice;
    private double coinMarketCapPrice;
    private int sourceId;
    private String poloniexAddress = "https://poloniex.com/public?command=returnTicker";
    private String krakenAddress = "https://api.kraken.com/0/public/Ticker?pair=ethusd";
    private String coinMarketCapAddress = "http://coinmarketcap-nexuist.rhcloud.com/api/eth/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!isNetworkAvailable()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Network Unavailable", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        sp = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        sourceId = sp.getInt("sourceId", R.id.poloniexButton);
        new SecondaryThread().execute();
        promptForRating();
    }

    public void onResume() {
        super.onResume();
        sourceId = sp.getInt("sourceId", R.id.poloniexButton);
        new SecondaryThread().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.meetDevsButton: {
                Intent intent = new Intent(this, BioActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.donateButton: {
                Intent intent = new Intent(this, DonationActivity.class);
                startActivity(intent);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Creates a dialog to prompt the user to rate the app every so often
     */
    public void promptForRating() {
        try {

            // Get the app's shared preferences
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);

            // Get the value for the run counter
            int counter = app_preferences.getInt("counter", 0);

            // Do every x times
            int RunEvery = 15;

            if(counter != 0  && counter % RunEvery == 0 )
            {
                //Toast.makeText(this, "This app has been started " + counter + " times.", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alert = new AlertDialog.Builder(
                        this);
                alert.setTitle("Thank you so much!");
                alert.setIcon(R.drawable.logodelta3needle); //app icon here
                alert.setMessage("We really hope our app has been helpful! \n\nWe'd appreciate your feedback and suggestions for improvements if you've got just a few moments!");

                alert.setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                //Do nothing
                            }
                        });

                alert.setNegativeButton("Rate it",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                final String appName = getApplicationContext().getPackageName();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id="
                                                    + appName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id="
                                                    + appName)));
                                }

                            }
                        });
                alert.show();
            }


            // Increment the counter
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("counter", ++counter);
            editor.apply(); // Very important

        } catch (Exception e) {
            //Do nothing, don't run but don't break
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Creates a dialog to prompt the user to rate the app every so often
     */
    public void promptForRating() {
        try {

            // Get the app's shared preferences
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);

            // Get the value for the run counter
            int counter = app_preferences.getInt("counter", 0);

            // Do every x times
            int RunEvery = 15;

            if(counter != 0  && counter % RunEvery == 0 )
            {
                //Toast.makeText(this, "This app has been started " + counter + " times.", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alert = new AlertDialog.Builder(
                        this);
                alert.setTitle("Thank you so much!");
                alert.setIcon(R.drawable.logodelta3needle); //app icon here
                alert.setMessage("We really hope our app has been helpful! \n\nWe'd appreciate your feedback and suggestions for improvements if you've got just a few moments!");

                alert.setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                //Do nothing
                            }
                        });

                alert.setNegativeButton("Rate it",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                final String appName = getApplicationContext().getPackageName();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id="
                                                    + appName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id="
                                                    + appName)));
                                }

                            }
                        });
                alert.show();
            }


            // Increment the counter
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("counter", ++counter);
            editor.apply(); // Very important

        } catch (Exception e) {
            //Do nothing, don't run but don't break
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Loads the last selected source; Poloniex, Kraken, CoinMarketCap, or Average.
     */
    public void initializeSourceId() {
        sourceId = sp.getInt("sourceId", R.id.poloniexButton);
    }

    /**
     * Opens the settings activity.
     *
     * @param view clicked button
     */
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Gets the saved purchase price from the shared preferences.
     *
     * @return purchase price of ethereum
     */
    public double getPurchasePrice() {
        purchasePrice = Double.parseDouble(sp.getString("purchasePriceString", "0"));
        return purchasePrice;
    }

    /**
     * Gets the saved quantity purchased from the shared preferences.
     *
     * @return purchased quantity of ethereum
     */
    public double getQuantityPurchased() {
        quantityPurchased = Double.parseDouble(sp.getString("quantityString", "0"));
        return quantityPurchased;
    }

    /**
     * Updates the UI with the value of the quantity purchased.
     */
    public void setQuantity() {
        quantityPurchased = getQuantityPurchased();
        TextView quantityText = (TextView) findViewById(R.id.qtyPurchasedText);
        quantityText.setText(String.format(getResources().getConfiguration().locale,"%.2f",quantityPurchased));
    }

    /**
     * Updates the UI with the value of the purchase price.
     */
    public void setPurchasePrice() {
        purchasePrice = getPurchasePrice();
        TextView purchasePriceText = (TextView) findViewById(R.id.purchasePriceText);
        purchasePriceText.setText(currencyFormat.format(purchasePrice));
    }

    /**
     * Calculates the quantity of the total and updates the UI with the total.
     */
    public void setCurrentTotal() {
        TextView totalText = (TextView) findViewById(R.id.currentTotalText);
        double quantity = quantityPurchased;
        total = quantity * price;
        totalText.setText(currencyFormat.format(total));
    }

    /**
     * Calculates the growth of the investment, given current price and purchase price.
     * Updates the UI with the growth.
     */
    public void setGrowth() {
        TextView sincePurchaseText = (TextView) findViewById(R.id.sincePurchaseText);
        double percentChange;
        if (purchasePrice != 0) {
            percentChange = ((price - purchasePrice) / purchasePrice) * 100;
        }
        else {
            percentChange = 0.0;
        }
        String percentChangeText = String.format(Locale.US,"%.2f",percentChange) +"%";
        sincePurchaseText.setText(percentChangeText);
    }

    /**
     * Updates the UI with the current price, obtained from shared preferences.
     */
    public void setCurrentPrice() {
        price =  Double.parseDouble(sp.getString("priceString", "0"));
        TextView currentPrice = (TextView) findViewById(R.id.priceText);
        currentPrice.setText(NumberFormat.getCurrencyInstance().format(price));
    }

    /**
     * Makes the call to the given url address and returns a string containing the
     * website's response.
     *
     * @param urlAddress the url address to be accessed
     * @return string containing the response from the url address
     * @throws IOException if unable to reach web api
     */
    public String makeWebCall(String urlAddress) throws IOException{
        String result = "";
        URL url = new URL(urlAddress);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        in.close();
        return result;
    }

    /**
     * Saves the current price value by adding it to shared preferences.
     */
    public void storeNewPriceValue() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("priceString", Double.toString(price));
        editor.apply();
    }

    /**
     * Checks to see which source is selected by user, and accesses
     * the corresponding website to obtain the data.
     *
     * @throws IOException if the website is unable to load
     * @throws JSONException if json exception thrown
     */
    public void determineSource() throws IOException, JSONException {
        switch (sourceId) {
            case R.id.poloniexButton:
                getPoloniexPriceData(false);
                break;
            case R.id.krakenButton:
                getKrakenPriceData(false);
                break;
            case R.id.coinMarketCapButton:
                getCoinMarketCapPriceData(false);
                break;
            case R.id.averageButton:
                getAveragePriceData();
                break;
        }
    }

    /**
     * Makes a call to the poloniex website and retrieves the price value.
     *
     * @param average whether or not the call is used to compute the average
     * @throws JSONException json exception
     * @throws IOException IOExeption
     */
    public void getPoloniexPriceData(boolean average) throws JSONException, IOException {
        JSONObject webResponse = new JSONObject(makeWebCall(poloniexAddress));
        JSONObject etherTicker = (JSONObject) webResponse.get("USDT_ETH");
        if (!average) {
            price = etherTicker.getDouble("last");
        }
        else {
            poloniexPrice = etherTicker.getDouble("last");
        }

    }

    /**
     * Makes a call to the poloniex website and retrieves the price value.
     *
     * @param average whether or not the call is used to compute the average
     * @throws JSONException json exception
     * @throws IOException IOExeption
     */
    public void getKrakenPriceData(boolean average) throws JSONException, IOException {
        JSONObject webResponse = new JSONObject(makeWebCall(krakenAddress));
        JSONObject resultField = (JSONObject) webResponse.get("result");
        JSONObject etherTicker = (JSONObject) resultField.get("XETHZUSD");
        JSONArray priceArray = (JSONArray) etherTicker.get("c");
        if (!average) {
            price = priceArray.getDouble(0);
        }
        else {
            krakenPrice = priceArray.getDouble(0);
        }

    }

    /**
     * Makes a call to the coinmarketcap website and retrieves the price value.
     *
     * @param average whether or not the call is used to compute the average
     * @throws JSONException json exception
     * @throws IOException IOExeption
     */
    public void getCoinMarketCapPriceData(boolean average) throws JSONException, IOException{
        JSONObject webResponse = new JSONObject(makeWebCall(coinMarketCapAddress));
        JSONObject etherTicker = (JSONObject) webResponse.get("price");
        if (!average) {
            price = etherTicker.getDouble("usd");
        }
        else {
            coinMarketCapPrice = etherTicker.getDouble("usd");
        }
    }

    /**
     * Makes a call to all 3 sources, and averages the resulting price values.
     *
     * @throws JSONException json exception
     * @throws IOException IOExeption
     */
    public void getAveragePriceData() throws IOException, JSONException {
        getPoloniexPriceData(true);
        getCoinMarketCapPriceData(true);
        getKrakenPriceData(true);
        calculateAveragePrice();
    }

    /**
     * Checks to see if a network connection is available.
     *
     * @return whether a network connection is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Calculates the average price of all 3 sources.
     */
    public void calculateAveragePrice() {
        price = (poloniexPrice + krakenPrice + coinMarketCapPrice) / 3;
    }

    /**
     * Open email when users click the email in navigation drawer.
     *
     * @param view clicked email
     */
    public void onEmailClick(View view) {
        // Create the text message with a string
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {this.getString(R.string.mattsEmail), this.getString(R.string.adamsEmail)});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.emailSubject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Please include your Google Play username, and describe your issue with as much detail as possible.\n\nIf you're just contacting us to say hi, then thank you in advance and we appreciate your kindness! :)\n\n\n");
        sendIntent.setType("text/plain");

        // Verify that the intent will resolve to an activity
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    /**
     * Updates the UI to show the current source
     */
    public void updateSourceText() {
        TextView sourceText = (TextView) findViewById(R.id.sourceText);
        switch (sp.getInt("sourceId", R.id.poloniexButton)) {
            case R.id.poloniexButton:
                sourceText.setText("Poloniex");
                break;
            case R.id.krakenButton:
                sourceText.setText("Kraken");
                break;
            case R.id.coinMarketCapButton:
                sourceText.setText("CoinMarketCap");
                break;
            case R.id.averageButton:
                sourceText.setText("Average");
                break;
        }
    }

    private class SecondaryThread extends AsyncTask<Void, Void, String> {
        ProgressDialog pd;
        protected void onPreExecute() {
            showProgressDialog();
        }

        protected String doInBackground(Void... params) {
            try {
                determineSource();
                storeNewPriceValue();
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "FML";
        }

        private void showProgressDialog() {
            pd = ProgressDialog.show(MainActivity.this,"","Loading Source Data...",false);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            initializeSourceId();
            updateSourceText();
            setCurrentPrice();
            setQuantity();
            setPurchasePrice();
            setCurrentTotal();
            setGrowth();
            pd.dismiss();
        }

    }
}

