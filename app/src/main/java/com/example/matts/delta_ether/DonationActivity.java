package com.example.matts.delta_ether;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ebookfrenzy.inappbilling.util.IabHelper;
import com.ebookfrenzy.inappbilling.util.IabResult;
import com.ebookfrenzy.inappbilling.util.Inventory;
import com.ebookfrenzy.inappbilling.util.Purchase;

import java.text.NumberFormat;
import java.util.ArrayList;

public class DonationActivity extends AppCompatActivity {
    private RadioButton button1;
    private RadioButton button5;
    private RadioButton button10;
    private RadioButton button15;
    private RadioButton button20;
    private RadioButton button25;
    private RadioButton button50;
    private RadioButton button100;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private final String TAG = "troubleshoot";
    private IabHelper mHelper;
    private String ITEM_SKU;
    private ArrayList<RadioButton> radioButtonList;
    private boolean buttonChecked = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        initializeRadioButtons();
        setupDonationBillingProcess();
    }

    public void setupDonationBillingProcess() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoyOxxKab2Npu3WAC/L49Xh/cNvQl+Ir6f1Mst/kAMb7hLuQrLMZPhejGIIQCCCB9T1ymU4enGE3Jfy9F/8TGcBAJKXTuCQDTQXNQZ0thSPzkqmpWweKvSemWcSemIW+/oMc7GTYyqXPQIs0lEIlaKN5yenxtTwFDwwEhoNz6nhR2QnVpR822ooUOl8Bydb9LgkcRJgxt5bTT1mvzLt8U+82G3oXtrQbHogKTfVoGfLsNAYQXpVI4li4zR4vTQrlImUf38ufY8StiDvKrvURj/dIfzHddDySEHJ1ILJt++o1G9+DtXHJ6xWh+Xtjth2+xkIllwcR3/qnF9HmkyHovUwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
    }

    /**
     * Initializes all of the radio buttons in activity by initializing their references
     * and adding them to the arrayList used to manipulate them as a group (radioButtonList)
     */
    public void initializeRadioButtons() {
        radioButtonList = new ArrayList<RadioButton>();
        button1 = (RadioButton) findViewById(R.id.donate1);
        button5 = (RadioButton) findViewById(R.id.donate5);
        button10 = (RadioButton) findViewById(R.id.donate10);
        button15 = (RadioButton) findViewById(R.id.donate15);
        button20 = (RadioButton) findViewById(R.id.donate20);
        button25 = (RadioButton) findViewById(R.id.donate25);
        button50 = (RadioButton) findViewById(R.id.donate50);
        button100 = (RadioButton) findViewById(R.id.donate100);
        radioButtonList.add(button1);
        radioButtonList.add(button5);
        radioButtonList.add(button10);
        radioButtonList.add(button15);
        radioButtonList.add(button20);
        radioButtonList.add(button25);
        radioButtonList.add(button50);
        radioButtonList.add(button100);
    }


    /**
     * Handles the actions taken when one of the radio buttons are clicked.
     * It unchecks all other radio buttons in the radioButtonList and changes the
     * item_sku, used to access proper billing item, It then updates the total
     * for the donation to reflect the clicked button.
     *
     * @param clickedButton the button that was clicked
     */
    public void onRadioButtonClicked(View clickedButton) {
        buttonChecked = true;
        switch (clickedButton.getId()) {
            case R.id.donate1:
                setCheckedButton(button1);
                setItemSku("donation1");
                updateTotal(1);
                break;
            case R.id.donate5:
                setCheckedButton(button5);
                setItemSku("donation5");
                updateTotal(5);
                break;
            case R.id.donate10:
                setCheckedButton(button10);
                setItemSku("donation10");
                updateTotal(10);
                break;
            case R.id.donate15:
                setCheckedButton(button15);
                setItemSku("donation15");
                updateTotal(15);
                break;
            case R.id.donate20:
                setCheckedButton(button20);
                setItemSku("donation20");
                updateTotal(20);
                break;
            case R.id.donate25:
                setCheckedButton(button25);
                setItemSku("donation25");
                updateTotal(25);
                break;
            case R.id.donate50:
                setCheckedButton(button50);
                setItemSku("donation50");
                updateTotal(50);
                break;
            case R.id.donate100:
                setCheckedButton(button100);
                setItemSku("donation100");
                updateTotal(100);
                break;
        }
    }

    /**
     * Updates the instance variable holding the sku for the billing item that
     * is to be billed.
     *
     * @param sku string value of sku
     */
    private void setItemSku(String sku) {
        ITEM_SKU = sku;
    }

    /**
     * Updates the total on the UI display with the value given.
     * @param total
     */
    private void updateTotal(int total) {
        TextView totalDonationView = (TextView) findViewById(R.id.totalDonationAmount);
        totalDonationView.setText(currencyFormat.format(total));
    }

    public void setCheckedButton(RadioButton button) {
        for (int k = 0; k < radioButtonList.size(); k++) {
            if (button.equals(radioButtonList.get(k))) {
                radioButtonList.get(k).setChecked(true);
            }
            else {
                radioButtonList.get(k).setChecked(false);
            }
        }
    }

    /**
     * Submits the donation when the donate button is clicked.
     *
     * @param view button that is clicked
     */
    public void onDonateButtonClicked(View view) {
        makeDonation();
    }

    /**
     * If a radio button is checked, the donation is submitted to the in-app billing for approval,
     * otherwise a toast is displayed to notify that an amount must be selected
     */
    public void makeDonation() {
        if (buttonChecked) {
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
        }
        else {
            Toast.makeText(getApplicationContext(), "Please choose an amount to donate", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Instance variable to listen for completed purchase
     */
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
            }

        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                    } else {
                        // handle error
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
