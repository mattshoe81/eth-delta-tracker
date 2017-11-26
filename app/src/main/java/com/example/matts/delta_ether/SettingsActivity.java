package com.example.matts.delta_ether;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.media.audiofx.BassBoost;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class SettingsActivity extends AppCompatActivity {

    public static Double purchasePrice = 0.0;
    public TextView currentPurchasePrice;
    public TextView currentQuantity;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public static double quantityPurchased;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private RadioGroup radioGroup;
    public static final int POLONIEX_ID = R.id.poloniexButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioGroup = (RadioGroup) findViewById(R.id.sourceGroup);
        currentPurchasePrice = (TextView) findViewById(R.id.currentPrice);
        currentQuantity = (TextView) findViewById(R.id.currentQuantityText);
        sp = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        editor = sp.edit();
        configureSourceButtons();
        setPurchasePrice();
        setQuantityPurchased();
        setPurchasePriceText();
        setQuantityPurchasedText();
    }


    public void configureSourceButtons() {
        initializeRadioListener();
        checkCurrentSourceButton();
    }
    public void checkCurrentSourceButton() {
        radioGroup.check(sp.getInt("sourceId",R.id.poloniexButton));
    }

    public void initializeRadioListener() {
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                editor.putInt("sourceId", checkedId);
                editor.apply();
            }
        });
    }

    public void setPurchasePriceText() {
        if (purchasePrice == 0) {
            currentPurchasePrice.setText("$ --");
        }
        else {
            currentPurchasePrice.setText(currencyFormat.format(purchasePrice));
        }
    }

    public void setQuantityPurchasedText() {
        if (quantityPurchased == 0) {
            currentQuantity.setText("--");
        }
        else {
            currentQuantity.setText(String.format(getResources().getConfiguration().locale,"%.2f",quantityPurchased));
        }
    }

    public void setPurchasePrice() {
        SettingsActivity.purchasePrice = getPurchasePrice();
    }

    public void setQuantityPurchased() {
        SettingsActivity.quantityPurchased = getQuantityPurchased();
    }

    public static double getPurchasePrice() {
        purchasePrice = Double.parseDouble(sp.getString("purchasePriceString", "0"));
        return purchasePrice;
    }

    public static double getQuantityPurchased() {
        quantityPurchased = Double.parseDouble(sp.getString("quantityString", "0"));
        return quantityPurchased;
    }

    public void savePurchasePrice(View view) {
        EditText purchasePriceText = (EditText) findViewById(R.id.purchasePriceTextBox);
        String text = purchasePriceText.getText().toString();
        if (!text.equals("")) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            purchasePrice = Double.parseDouble(purchasePriceText.getText().toString());
            purchasePriceText.setText("");
            purchasePriceText.setHint("Saved!");
            if (purchasePrice == 0) {
                currentPurchasePrice.setText("--");
            } else {
                currentPurchasePrice.setText(NumberFormat.getCurrencyInstance().format(purchasePrice));
            }


            SharedPreferences sp = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("purchasePriceString", Double.toString(purchasePrice));
            editor.apply();
        }
    }

    public void saveQuantityPurchased(View view) {
        EditText quantityText = (EditText) findViewById(R.id.quantityText);
        String text = quantityText.getText().toString();
        if (!text.equals("")) {

            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            quantityPurchased = Double.parseDouble(quantityText.getText().toString());
            quantityText.setText("");
            quantityText.setHint("Saved!");

            if (quantityPurchased == 0) {
                currentQuantity.setText("--");
            } else {
                currentQuantity.setText(String.format(getResources().getConfiguration().locale,"%.2f",quantityPurchased));
            }

            SharedPreferences sp = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("quantityString", Double.toString(quantityPurchased));
            editor.apply();
        }
    }
}
