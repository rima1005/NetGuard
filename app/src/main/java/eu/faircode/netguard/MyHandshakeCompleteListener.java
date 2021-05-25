package eu.faircode.netguard;

import android.util.Log;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import java.util.ArrayList;
import java.util.List;


public class MyHandshakeCompleteListener implements HandshakeCompletedListener {
    public static String TAG = "NetGuard.Service.HandshakeComplete";

    private String suit;

    public MyHandshakeCompleteListener(String Suit)
    {
        suit = Suit;
    }

    @Override
    public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
        Log.i(TAG, "Finished Handshake with " + suit);
        successfulHandshake.add(suit);
    }

    public static List<String> successfulHandshake = new ArrayList<>();

    public static void clearHandshakeList()
    {
        successfulHandshake.clear();
    }
}