package de.htwb.ai.kbe.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthUtils {

    public static String authorize(String token) {
        if (token.equals("")) {
            return null;
        } else {
            try {
                String url = "http://localhost:8080/auth";
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", token);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader inputReader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = inputReader.readLine()) != null) {
                        response.append(inputLine);
                    }

                    inputReader.close();

                    return String.valueOf(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
