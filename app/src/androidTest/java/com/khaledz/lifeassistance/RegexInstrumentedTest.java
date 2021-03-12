package com.khaledz.lifeassistance;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lifeassistance.Utils.RegexProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RegexInstrumentedTest {

    private static final String TAG = "Regex Instrumented Test";

    @Test
    public void useYouTubeRegex() {
        final String validLink1 = "https://www.youtube.com/watch?v=sXQxhojSdZM&ab_channel=Fireship";
        final String validLink2 = "www.youtube.com/watch?v=sXQxhojSdZM&ab_channel=Fireship";
        final String validLink3 = "youtube.com/watch?v=sXQxhojSdZM&ab_channel=Fireship";
        final String validLink4 = "https://youtube.com/watch?v=sXQxhojSdZM&ab_channel=Fireship";

        final String invalidLink1 = "https://www.google.com";
        final String invalidLink2 = "www.google.com";
        final String invalidLink3 = "google.com";
        final String invalidLink4 = "https://google.com";
        final String invalidLink5 = "fanoaffa";

        assertTrue(RegexProvider.isLinkValid(validLink1));
        assertTrue(RegexProvider.isLinkValid(validLink2));
        assertTrue(RegexProvider.isLinkValid(validLink3));
        assertTrue(RegexProvider.isLinkValid(validLink4));

        assertFalse(RegexProvider.isLinkValid(invalidLink1));
        assertFalse(RegexProvider.isLinkValid(invalidLink2));
        assertFalse(RegexProvider.isLinkValid(invalidLink3));
        assertFalse(RegexProvider.isLinkValid(invalidLink4));
        assertFalse(RegexProvider.isLinkValid(invalidLink5));
    }

    //TODO fix Regex of all playlists
    @Test
    public void testYouTubeLinkExtractorRegex() {
        final String validlink = "https://www.youtube.com/watch?v=8NOfw32gotA&list=PLCZPUiJ5kQaHQG9LN3WjxxUmCOKV_heOF&index=3&ab_channel=AhmedElrefa3y";
        final String validlink2 = "https://www.youtube.com/watch?v=8NOfw32gotA&list=PLCZPUiJ5kQaHQG9LN3WjxxUmCOKV_heOF";

        final String validatedLink1 = RegexProvider.extractYouTubePlaylistId(validlink);
        final String validatedLink2 = RegexProvider.extractYouTubePlaylistId(validlink2);

        assertEquals(validatedLink1, "PLCZPUiJ5kQaHQG9LN3WjxxUmCOKV_heOF");
        assertEquals(validatedLink2, "PLCZPUiJ5kQaHQG9LN3WjxxUmCOKV_heOF");

    }
}