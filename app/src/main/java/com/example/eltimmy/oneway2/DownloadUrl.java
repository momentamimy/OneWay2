package com.example.eltimmy.oneway2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by eltimmy on 3/16/2018.
 */

public class DownloadUrl {
    public String readUrl(String myUrl) throws IOException {
        String data="";
        InputStream inputStream=null;
        HttpURLConnection uRLConnection=null;
        try {
            URL url=new URL(myUrl);
            uRLConnection=(HttpURLConnection) url.openConnection();
            uRLConnection.connect();

            inputStream=uRLConnection.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb=new StringBuffer();

            String line="";
            while ((line=br.readLine())!=null)
            {
                sb.append(line);
            }
            data=sb.toString();
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            uRLConnection.disconnect();
        }
        return data;
    }
}
