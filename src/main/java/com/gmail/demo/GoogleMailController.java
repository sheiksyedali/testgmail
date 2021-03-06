package com.gmail.demo;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.google.api.services.gmail.model.WatchRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
/**
 * Author: Sheik Syed Ali
 * Date: 20 Nov 2021
 */
@Controller
@RestController
public class GoogleMailController {
    private static final String APPLICATION_NAME = "GmailAlexa";
//    private static HttpTransport httpTransport;
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static com.google.api.services.gmail.Gmail client;
//
//    GoogleClientSecrets clientSecrets;
//    GoogleAuthorizationCodeFlow flow;
//    Credential credential;

    @Value("${gmail.client.clientId}")
    private String clientId;

    @Value("${gmail.client.clientSecret}")
    private String clientSecret;

    @Value("${gmail.client.redirectUri}")
    private String redirectUri;


    @RequestMapping(value = "/login/gmail", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
        String redirect = GmailHandler.getInstance().authorize(clientId, clientSecret, redirectUri);
        return new RedirectView(redirect);
    }

    @RequestMapping(value = "/gmail/callback", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {

        JSONObject json = GmailHandler.getInstance().callback(code,redirectUri, APPLICATION_NAME);

        // System.out.println("code->" + code + " userId->" + userId + "
        // query->" + query);

//        JSONObject json = new JSONObject();
//        JSONArray arr = new JSONArray();
//
//        // String message;
//        try {
//            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
//            credential = flow.createAndStoreCredential(response, "userID");
//
//            client = new com.google.api.services.gmail.Gmail.Builder(httpTransport, JSON_FACTORY, credential)
//                    .setApplicationName(APPLICATION_NAME).build();
//
//            /*
//             * Filter filter = new Filter().setCriteria(new
//             * FilterCriteria().setFrom("a2cart.com@gmail.com"))
//             * .setAction(new FilterAction()); Filter result =
//             * client.users().settings().filters().create("me",
//             * filter).execute();
//             *
//             * System.out.println("Created filter " + result.getId());
//             */
//
////            client.users().watch()
//            String userId = "me";
////            String query = "subject:'Welcome to A2Cart'";
////            ListMessagesResponse MsgResponse = client.users().messages().list(userId).setQ(query).execute();
//            ListMessagesResponse MsgResponse = client.users().messages().list(userId).setQ("is:unread").execute();
//
//            List<Message> messages = new ArrayList<>();
//
//            System.out.println("message length:" + MsgResponse.getMessages().size());
//
//            for (Message msg : MsgResponse.getMessages()) {
//
//                messages.add(msg);
//
//                Message message = client.users().messages().get(userId, msg.getId()).execute();
//                System.out.println("snippet :" + message.getSnippet());
//
//                arr.put(message.getSnippet());
//
//                /*
//                 * if (MsgResponse.getNextPageToken() != null) { String
//                 * pageToken = MsgResponse.getNextPageToken(); MsgResponse =
//                 * client.users().messages().list(userId).setQ(query).
//                 * setPageToken(pageToken).execute(); } else { break; }
//                 */
//            }
//            json.put("response", arr);
//
//            for (Message msg : messages) {
//
//                System.out.println("msg: " + msg.toPrettyString());
//            }
//
//        } catch (Exception e) {
//
//            System.out.println("exception cached ");
//            e.printStackTrace();
//        }

        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }

//    private String authorize() throws Exception {
//        AuthorizationCodeRequestUrl authorizationUrl;
//        if (flow == null) {
//            Details web = new Details();
//            web.setClientId(clientId);
//            web.setClientSecret(clientSecret);
//            clientSecrets = new GoogleClientSecrets().setWeb(web);
//            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
//                    Collections.singleton(GmailScopes.GMAIL_READONLY)).build();
//        }
//        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
//
//        System.out.println("gamil authorizationUrl ->" + authorizationUrl);
//        return authorizationUrl.build();
//    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String testMe(HttpServletRequest request) throws Exception {
        return "Working!!";
    }

//    @RequestMapping(value = "/testme2", method = RequestMethod.POST,
//            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//            produces = {MediaType.APPLICATION_JSON_VALUE})
//    public Map<String, Object> signup1(@RequestParam Map<String, Object> test){
//        System.out.println("Hai Sheik, im in signup1");
//        for (Map.Entry<String,Object> entry : test.entrySet()){
//            System.out.println("[Sheik-mail-input]Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }
//        Map<String,Object> resp = new HashMap<>();
//        resp.put("status", "success");
//        return resp;
//    }

    @RequestMapping(
            value = "/testme1",
            method = RequestMethod.POST)
    public Map<String, Object> process(@RequestBody Map<String, Object> payload)
            throws Exception {
        System.out.println("======================");
        for (Map.Entry<String,Object> entry : payload.entrySet()){
            System.out.println("[Sheik-mail-input]Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println("======================");
        Map<String,Object> resp = new HashMap<>();
        resp.put("status", "success");
        return resp;
    }

    @RequestMapping(value = "/watch", method = RequestMethod.GET)
    public Map<String, Object> watch(){
        return GmailHandler.getInstance().watch();
    }
}
