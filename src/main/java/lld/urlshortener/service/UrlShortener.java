package lld.urlshortener.service;

import lld.urlshortener.strategy.ShortCodeGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlShortener {

    private final ShortCodeGenerator shortCodeGenerator;
    private final Map<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final Map<String, String> urlToCode = new ConcurrentHashMap<>();
    private final String baseUrl;

    public UrlShortener(ShortCodeGenerator shortCodeGenerator, String baseUrl) {
        this.shortCodeGenerator = shortCodeGenerator;
        this.baseUrl = baseUrl;
    }

    public String shorten(String longUrl) {
        String code = urlToCode.computeIfAbsent(longUrl, url -> {
            String c = shortCodeGenerator.generate();
            codeToUrl.put(c, url);
            return c;
        });
        return baseUrl + code;
    }

    public String shortenWithAlias(String longUrl, String alias) {
        if (codeToUrl.putIfAbsent(alias, longUrl) != null) {
            throw new IllegalArgumentException("Alias already taken: " + alias);
        }
        urlToCode.put(longUrl, alias);
        return baseUrl + alias;
    }

    public String expand(String shortUrl) {
        String code = shortUrl.startsWith(baseUrl)
                ? shortUrl.substring(baseUrl.length())
                : shortUrl;
        String longUrl = codeToUrl.get(code);
        if (longUrl == null) {
            throw new IllegalArgumentException("Unknown short URL: " + shortUrl);
        }
        return longUrl;
    }
}
