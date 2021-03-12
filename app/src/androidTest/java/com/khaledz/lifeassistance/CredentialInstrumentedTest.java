package com.khaledz.lifeassistance;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class CredentialInstrumentedTest {

    private static final String CLIENT_SECRETS = "client_secret.json";
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
    }

    // TODO Finish the test function
    @Test
    public void testFileAccess() {
        try {
            InputStream in = context.getAssets().open(CLIENT_SECRETS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
