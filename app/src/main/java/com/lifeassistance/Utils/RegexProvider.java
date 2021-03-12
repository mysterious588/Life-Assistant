package com.lifeassistance.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexProvider {

    private static final String TAG = "Regex Provider";

    private static final Pattern youTubeLinkPattern = Pattern.compile("^(https?://)?((www\\.)?youtube\\.com|youtu\\.?be)/.+$");
    private static final Pattern youtubePlaylistIdPattern = Pattern.compile("(?:http|https|)(?::\\/\\/|)(?:www.|)(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{12,})[a-z0-9;:@#?&%=+\\/\\$_.-]*", Pattern.CASE_INSENSITIVE);

    public static String extractYouTubePlaylistId(String link) {
        Matcher matcher = youtubePlaylistIdPattern.matcher(link);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static boolean isLinkValid(String link) {
        return youTubeLinkPattern.matcher(link).matches();
    }
}
