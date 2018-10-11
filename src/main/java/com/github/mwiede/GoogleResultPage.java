package com.github.mwiede;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleResultPage {

    private final String response;
    private final Set<URL> results = new HashSet<>();

    public GoogleResultPage(String content) {
        response = content;
        findPageLinks();
    }

    private void findPageLinks() {
        Matcher matcher = Pattern.compile("\\/url\\?q=([^&]+)&amp").matcher(response);

        while (matcher.find()) {
            try {
                results.add(new URL(matcher.group(1)));
            } catch (MalformedURLException e) {
                System.err.println("crawled an invalid url: " + matcher.group(1) + " -> skipping it.");
            }
        }
    }

    public int getResultCount() {
        return results.size();
    }

    public Set<URL> getResults() {
        return Collections.unmodifiableSet(this.results);
    }
}
