package com.biyang.url_shortener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.biyang.url_shortener.service.UrlEncoder;

@SpringBootTest
public class UrlEncoderTest {

	@Autowired
    private UrlEncoder urlEncoder;

    @Test
    public void encode_lessThan62() {
        assertEquals("aak", urlEncoder.encode(10));
    }

    @Test
    public void encode_moreThan62() {
        assertEquals("abq", urlEncoder.encode(78));
    }

    @Test
    public void decode_singleCharacter() {
        assertEquals(11, urlEncoder.decode("l"));
    }

    @Test
    public void decode_threeCharacter() {
        assertEquals(11, urlEncoder.decode("aal"));
    }
}