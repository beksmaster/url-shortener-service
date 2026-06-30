package kg.megalab.urlshortenerservice.service;

import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class Base62ShortCodeGenerator implements ShortCodeGenerator{

    private static final char[] BASE62 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private static final int CODE_LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        StringBuilder shortCode = new StringBuilder(CODE_LENGTH);

        for(int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(BASE62.length);
            shortCode.append(BASE62[index]);
        }

        return shortCode.toString();
    }

}
