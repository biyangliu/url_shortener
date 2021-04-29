package com.biyang.url_shortener.lib;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.biyang.url_shortener.service.UrlEncoder;

@RunWith(MockitoJUnitRunner.class)
public class UrlEncoderTest {

    private UrlEncoder urlEncoder = new UrlEncoder();

    @Test
    public void encode_lessThan62() {
        assertEquals("k", urlEncoder.encode(10));
    }

    @Test
    public void encode_moreThan62() {
        assertEquals("bq", urlEncoder.encode(78));
    }

    @Test
    public void decode_singleCharacter() {
        assertEquals(11, urlEncoder.decode("l"));
    }
}