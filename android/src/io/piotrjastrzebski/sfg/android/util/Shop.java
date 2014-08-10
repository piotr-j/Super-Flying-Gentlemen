/*
 * Super Flying Gentlemen
 * Copyright (C) 2014  Piotr Jastrzębski <me@piotrjastrzebski.io>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.piotrjastrzebski.sfg.android.util;

import android.content.Intent;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import io.piotrjastrzebski.sfg.ActionListener;
import io.piotrjastrzebski.sfg.SFGApp;
import io.piotrjastrzebski.sfg.android.AndroidActionResolver;
import io.piotrjastrzebski.sfg.android.AndroidLauncher;
import io.piotrjastrzebski.sfg.android.R;
import io.piotrjastrzebski.sfg.android.util.billing.IabHelper;
import io.piotrjastrzebski.sfg.android.util.billing.IabResult;
import io.piotrjastrzebski.sfg.android.util.billing.Inventory;
import io.piotrjastrzebski.sfg.android.util.billing.Purchase;

public class Shop implements IabHelper.OnIabSetupFinishedListener, IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener {
    private static final int RC_REQUEST = 31337;
    private static final String SKU_PREMIUM = "sfg_premium";
    // TODO move to resources
    private String pk1 = "V4BpRm1ORNuh;EQ;CKgCBIIM;8Q;CO;;FEQ;B0w9GikhqkgBN;jIBIIM";
    private String pk3 = "YkFeCjlR1CaxXzJByMc+UwqrMFW5kwJxc6lMJp6wg0j/lbxp/bSuhWgX";
    private String pk4 = "g+Cf59OifMphT;nDbRJ9Bu0xEh04e584Hr5TqeJhU9bpk1;cCXyo3P;L";
    private String pk5 = "gvdXMcVnP0qxqvyopJsqjc01iW8GzmvgqM66ppenJ0bqJ0pp6E/R5obA";
    private String pk7 = "lGbWiwIvwcFW7EI3VeWDDlWtM6fgC00+COh2wq/nvd/Yy1cmbwIDAQAB";

    private final AndroidLauncher activity;
    private final AndroidActionResolver actionResolver;

    private IabHelper iapHelper;
    private boolean isPremium;
    private Preferences preferences;

    public Shop(AndroidLauncher activity, AndroidActionResolver actionResolver){
        this.activity = activity;
        this.actionResolver = actionResolver;
    }

    public void init(){
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                setPremium(false);
                pk1 = new StringBuilder(pk1).reverse().toString();
                String publicKey = pk1 + "7vR0gifq453Vj6PcEBD0/8ZvViDNBFXWUHM25anN9NW3nJ8lDgeb/;G;" + pk3 + pk4;
                pk5 = new StringBuilder(pk5).reverse().toString();
                publicKey = publicKey.replace(";", "A");
                publicKey += pk5 + "WuINdus/fLvx/dNJ7mrML4gzh1jJ40pSaOYaE6KVw0kAQM7woNGkNO/P" + pk7;
                publicKey = publicKey.replace("'", "W");

                iapHelper = new IabHelper(activity, publicKey);
                if(SFGApp.DEBUG_IAP)
                    iapHelper.enableDebugLogging(true);
                iapHelper.startSetup(Shop.this);

                preferences = Gdx.app.getPreferences(SFGApp.PREFS);
            }
        });
    }

    public void onIabSetupFinished(IabResult result) {
        if (!result.isSuccess()) {
            // Oh noes, there was a problem.
            return;
        }

        // Have we been disposed of in the meantime? If so, quit.
        if (iapHelper == null) return;

        // IAB is fully set up. Now, let's get an inventory of stuff we own.
        iapHelper.queryInventoryAsync(this);
    }

    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        // Have we been disposed of in the meantime? If so, quit.
        if (iapHelper == null) return;
        // Is it a failure?
        if (result.isFailure()) {
            // general error, hopefully network
            if (result.getResponse() == 6){
                setPremium(preferences.getString("DIFF", "HARD").equals("BTRAPAL".replace("TRAP", "RAT".replace("A", "U"))));
            }
            return;
        }

        // Do we have the premium upgrade?
        Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
        boolean enabled = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
        setPremium(enabled);
        preferences.flush();
    }

    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        // if we were disposed of in the meantime, quit.
        if (iapHelper == null) return;

        if (result.isFailure()) {
            return;
        }
        if (!verifyDeveloperPayload(purchase)) {
            return;
        }
        if (purchase.getSku().equals(SKU_PREMIUM)) {
            // bought the premium upgrade!
            setPremium(true);
        }
    }

    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        return true;
    }

    public boolean onActivityResult(int request, int response, Intent data) {
        return iapHelper.handleActivityResult(request, response, data);
    }

    private void setPremium(boolean enabled){
        if (preferences!=null)preferences.putString("DIFF", enabled?"BRUTAL":"HARD");
        isPremium = enabled;
        if (isPremium){
            actionResolver.sendEvent(ActionListener.PREMIUM_ENABLED);
        } else {
            actionResolver.sendEvent(ActionListener.PREMIUM_DISABLED);
        }
    }

    private String generatePayload(){
        return "should_be_per_user_or_something";
    }

    public void buyPremium() {
        iapHelper.flagEndAsync();
        iapHelper.launchPurchaseFlow(activity, SKU_PREMIUM, RC_REQUEST,
                this, generatePayload());
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void restorePurchase() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iapHelper.flagEndAsync();
                iapHelper.queryInventoryAsync(Shop.this);
                Toast.makeText(activity, R.string.purchase_restored, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onDestroy() {
        if (iapHelper != null) {
            iapHelper.dispose();
            iapHelper = null;
        }
    }
}
