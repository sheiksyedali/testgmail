package com.gmail.demo;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Author: Sheik Syed Ali
 * Date: 20 Nov 2021
 */
public class GmailHandler {
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.gmail.Gmail client;

    private GoogleClientSecrets clientSecrets;
    private GoogleAuthorizationCodeFlow flow;
    private Credential credential;

    private static GmailHandler gmailHandler = null;

    private GmailHandler(){

    }

    public JSONObject callback(String code, String redirectUri, String applicationName){
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();

        // String message;
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            credential = flow.createAndStoreCredential(response, "userID");

            client = new com.google.api.services.gmail.Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(applicationName).build();

            /*
             * Filter filter = new Filter().setCriteria(new
             * FilterCriteria().setFrom("a2cart.com@gmail.com"))
             * .setAction(new FilterAction()); Filter result =
             * client.users().settings().filters().create("me",
             * filter).execute();
             *
             * System.out.println("Created filter " + result.getId());
             */

//            client.users().watch()
            String userId = "me";
//            String query = "subject:'Welcome to A2Cart'";
//            ListMessagesResponse MsgResponse = client.users().messages().list(userId).setQ(query).execute();
            ListMessagesResponse MsgResponse = client.users().messages().list(userId).setQ("is:unread").execute();

            List<Message> messages = new ArrayList<>();

            System.out.println("message length:" + MsgResponse.getMessages().size());

            for (Message msg : MsgResponse.getMessages()) {

                messages.add(msg);

                Message message = client.users().messages().get(userId, msg.getId()).execute();
                System.out.println("snippet :" + message.getSnippet());

                arr.put(message.getSnippet());

                /*
                 * if (MsgResponse.getNextPageToken() != null) { String
                 * pageToken = MsgResponse.getNextPageToken(); MsgResponse =
                 * client.users().messages().list(userId).setQ(query).
                 * setPageToken(pageToken).execute(); } else { break; }
                 */
            }
            json.put("response", arr);

            for (Message msg : messages) {

                System.out.println("msg: " + msg.toPrettyString());
            }

        } catch (Exception e) {

            System.out.println("exception cached ");
            e.printStackTrace();
        }
        return json;
    }

    public String authorize(String clientId, String clientSecret, String redirectUri) throws Exception {

        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(GmailScopes.GMAIL_READONLY)).build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

        System.out.println("gamil authorizationUrl ->" + authorizationUrl);
        return authorizationUrl.build();
    }

    public Map<String, Object> watch(){
        Map<String, Object> watchResponse = new HashMap<>();
        try{
            WatchRequest watchRequest = new WatchRequest();
            watchRequest.setTopicName("projects/mytestmail-332418/topics/gmailpush-topic");
            List<String> labels = new ArrayList<>();
            labels.add("INBOX");
            watchRequest.setLabelIds(labels);
            WatchResponse response = client.users().watch("me", watchRequest).execute();

            watchResponse.put("mHistroyId", response.getHistoryId());
            for(String key : response.keySet()){
                watchResponse.put(key, response.get(key));
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
        return watchResponse;
    }

    public static GmailHandler getInstance(){
        return (gmailHandler == null) ? gmailHandler = new GmailHandler() : gmailHandler;
    }
}
