package com.github.mwiede;

import java.net.URL;

public interface Downloader {

    WebPage download(URL url);
}
