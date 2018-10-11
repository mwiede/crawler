package com.github.mwiede;

import java.net.URL;

public class WebPage {

    private final URL url;
    private final String content;

    public WebPage(final URL url, final String content) {
        this.url = url;
        this.content = content;
    }

    public URL getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
