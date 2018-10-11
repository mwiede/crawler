package com.github.mwiede;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloaderImpl implements Downloader {

    @Override
    public WebPage download(final URL url) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            return new WebPage(url, download(con));
        } catch (IOException e) {
            System.err.println(String.format("Error downloading %s : %s", url.toString(), e.getMessage()));
            return new WebPage(url, "");
        } finally {
            con.disconnect();
        }
    }

    /**
     * @param con
     * @return the content of the page or an empty String if page cannot be found.
     * @throws IOException
     */

    private String download(final HttpURLConnection con) throws IOException {

        System.out.println(Thread.currentThread().getName() + " downloading " + con.getURL());

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (MSIE; Windows 10)");

        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);
        con.setInstanceFollowRedirects(true);

        final int status = con.getResponseCode();
        if (status == 200) {

            final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } else {
            return "";
        }
    }

}
