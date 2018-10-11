package com.github.mwiede;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Crawler {

    private final Downloader downloader;

    public Crawler(Downloader downloader) {
        this.downloader = downloader;
    }

    public static void main(String[] args) {

        // 0) Read a string (search term) from standard input

        System.out.println("Please enter a search string:");
        Scanner scan = new Scanner(System.in);
        String query = scan.nextLine();

        if (query == null || query.length() < 3) {
            System.err.println("Query String too short (need at least 3 characters).");
            System.out.println("Goodbye");
            System.exit(1);
        }

        new Crawler(new DownloaderImpl()).crawl(query)
                .forEach(entry -> System.out.println(entry.getKey() + " = " + entry.getValue()));

    }

    Stream<Map.Entry<String, Long>> crawl(String query) {

        long startTime = new Date().getTime();

        try {
            // 1) Get a Google result page for the search term

            GoogleResultPage resultPage = null;
            try {
                resultPage = getResultPage(query);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Goodbye");
                System.exit(1);
            }
            if (resultPage.getResultCount() < 1) {
                System.err.println("Now result found from Google search.");
                System.out.println("Goodbye");
                System.exit(1);
            }

            // 2) Extract main result links from the page

            return resultPage.getResults().stream() //

                    // 3) Download the respective pages and extract the names of javascript libraries used in them
                    .parallel()// speed it up
                    .map(downloader::download)//
                    // filter pages without content
                    .filter(webPage -> webPage.getContent().length() > 0)//
                    .map(Crawler::findLibraries) //
                    .flatMap(Collection::stream) // connect all Sets to one
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))

                    // 4) Print top 5 most used libraries to standard output
                    .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))//
                    .limit(5);
        } finally {
            final Duration duration = Duration.ofMillis(new Date().getTime() - startTime);
            System.out.println("Duration was: " + duration.getSeconds() + " seconds (" + duration + ")");
        }
    }

    private GoogleResultPage getResultPage(final String query) throws MalformedURLException {
        final URL url = new URL(String.format("https://google.de/search?q=%s", URLEncoder.encode(query)));
        final WebPage page = downloader.download(url);
        return new GoogleResultPage(page.getContent());
    }

    private static Set<String> findLibraries(final WebPage webPage) {
        System.out.println(Thread.currentThread().getName() + " findLibs in " + webPage.getUrl());
        final Matcher matcher = Pattern.compile("([\\.\\w\\d+_-]+\\.js)[^\\w]+").matcher(webPage.getContent());
        final Set<String> libraries = new HashSet<>();
        while (matcher.find()) {
            final String libraryName = matcher.group(1);
            libraries.add(libraryName);
        }
        return libraries;
    }
}
