package lld.urlshortener.strategy;

import java.util.concurrent.atomic.AtomicLong;

public class Base62Generator implements ShortCodeGenerator {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final AtomicLong atomicLong = new AtomicLong(1);

    @Override
    public String generate() {
        return encode(atomicLong.getAndIncrement());
    }

    public String encode(long n){
        if (n == 0)return "";
        StringBuilder encoded = new StringBuilder();
        while (n>0){
            encoded.append(ALPHABET.charAt((int)(n%62)));
            n/=62;
        }
        return encoded.reverse().toString();
    }
}
