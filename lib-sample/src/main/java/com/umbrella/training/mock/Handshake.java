package com.umbrella.training.mock;

import android.content.Context;
import android.widget.Toast;

public class Handshake {

    private volatile static Handshake INSTANCE;

    private Handshake() {

    }

    public static Handshake getInstance() {
        if (INSTANCE == null) {
            synchronized (Handshake.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Handshake();
                }
            }
        }
        return INSTANCE;
    }

    public void sayHi(Context context) {
        Toast.makeText(context, "Hi, I'm java.", Toast.LENGTH_SHORT).show();
    }
}
