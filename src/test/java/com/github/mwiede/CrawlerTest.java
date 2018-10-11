package com.github.mwiede;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CrawlerTest {

    private Downloader downloaderMock;

    @Before
    public void stubTheMock() {
        downloaderMock = Mockito.mock(Downloader.class);
        when(downloaderMock.download(any(URL.class))).then(invocation -> {

            URL url = invocation.getArgument(0);
            if (url.toString().startsWith("https://google.de/search")) {
                return new WebPage(url,//
                        ".../url?q=http://webpage1.com&amp......." //
                                + "/url?q=http://webpage2.com&amp......." //
                                + "/url?q=http://webpage3.com&amp......." //
                                + "/url?q=http://webpage4.com&amp....");
            } else {
                return new WebPage(url, " 'lib1.js' \"lib2.js\" xyz path/to/lib3.js ");
            }

        });

    }

    @Test
    public void testCrawling() {

        Stream<Map.Entry<String, Long>> xyz = new Crawler(downloaderMock).crawl("xyz");

        Set<Map.Entry<String, Long>> result = xyz.collect(Collectors.toSet());

        assertEquals(3, result.size());

        result.forEach(entry -> assertEquals(Long.valueOf(4), entry.getValue()));

        result.forEach(entry -> System.out.println(entry.getKey() + " = " + entry.getValue()));

    }

}