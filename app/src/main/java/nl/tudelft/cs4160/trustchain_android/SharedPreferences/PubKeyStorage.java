package nl.tudelft.cs4160.trustchain_android.SharedPreferences;

import android.content.Context;

/**
 * Created by timbu on 18/12/2017.
 */

public class PubKeyStorage {

    final static String pubKeyStorageKey = "pubKeyStorageKey:";

    public static void addAddress(Context context, String pubkey, String ipAddress) {
        if(pubkey == null || ipAddress == null) {
            throw new RuntimeException("Pubkey and ip may not be null");
        }
        SharedPreferencesStorage.writeSharedPreferences(context, pubKeyStorageKey + pubkey, ipAddress);
    }

    public static String getAddress(Context context, String pubkey) {
        return SharedPreferencesStorage.readSharedPreferences(context, pubKeyStorageKey + pubkey);
    }
}
