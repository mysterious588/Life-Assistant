package com.lifeassistance.Apis;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

public class YouTubeAPI {
    private static final String CLIENT_SECRETS = "credential_service_account.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

    private static final String APPLICATION_NAME = "Life Assistant";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Context mContext;

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static GoogleCredential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = mContext.getAssets().open(CLIENT_SECRETS);
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES).build();
        GoogleCredential credential = GoogleCredential.fromStream(in, httpTransport, JSON_FACTORY).createScoped(SCOPES);
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {

        final NetHttpTransport httpTransport = new NetHttpTransport();
        GoogleCredential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @return PlaylistItemListResponse
     */
    public static PlaylistItemListResponse getData(String playListId, Context context) throws Exception {
        mContext = context;
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.PlaylistItems.List request = youtubeService.playlistItems().list("snippet");
        PlaylistItemListResponse response = request.setPlaylistId(playListId).setFields("items/snippet(title)").setMaxResults(200L).execute();
        return response;
    }
}
