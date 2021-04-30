package com.biyang.url_shortener.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Encode and decode the IDs of URLs. Do NOT apply directly on the URLs.
 * It uses a-z A-Z 0-9 as the base characters.
 * 
 * @author byliu
 */
@Service
public class UrlEncoder {
	
	private static final String BASE_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final char[] BASE_CHARACTERS = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	};
    private static final int BASE_LENGTH = BASE_CHARACTERS.length;
    
	@Value("${shorturl.length}")
	private int SHORTURL_LENGTH;

    public String encode(long input) {
        StringBuilder encodedString = new StringBuilder();

        for (int i = 0; i < SHORTURL_LENGTH; i++) {
        	encodedString.append(BASE_CHARACTERS[(int) (input % BASE_LENGTH)]);
        	input = input / BASE_LENGTH;
        }

        return encodedString.reverse().toString();
    }

    public long decode(String input) {
        char[] characters = input.toCharArray();
        int length = characters.length;

        int decoded = 0;

        //counter is used to avoid reversing input string
        int counter = 1;
        for (int i = 0; i < length; i++) {
            decoded += BASE_STRING.indexOf(characters[i]) * Math.pow(BASE_LENGTH, length - counter);
            counter++;
        }
        return decoded;
    }
}
