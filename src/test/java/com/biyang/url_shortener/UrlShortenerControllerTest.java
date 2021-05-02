package com.biyang.url_shortener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.biyang.url_shortener.controller.UrlShortenerController;
import com.biyang.url_shortener.model.Url;
import com.biyang.url_shortener.service.UrlService;

@SpringBootTest
public class UrlShortenerControllerTest {

    private UrlShortenerController urlShortenerController;

    @Mock
    private UrlService mockUrlService;

    @Value("${server.hostname}")
    private String hostname;

    @Value("${server.port}")
    private int port;

    @Test
    public void testCreate() {
        String longUrl1 = "http://www.a.com";
        String shortUrl1 = "aab";
        Url shortUrlObj = new Url(1, longUrl1, shortUrl1);

        urlShortenerController = new UrlShortenerController(mockUrlService, hostname, port);
        Mockito.when(mockUrlService.convertAndSaveUrl(longUrl1)).thenReturn(shortUrlObj);

        String res = String.format("%s:%d/get/%s", hostname, port, shortUrl1);
        assertEquals(res, urlShortenerController.create(longUrl1));

        String longUrl2 = "www.a.com";
        String res2 = "Invalid URL! no protocol: " + longUrl2;
        assertEquals(res2, urlShortenerController.create(longUrl2));
    }

    @Test
    public void testLookup() {
        String longUrl1 = "http://www.a.com";
        String shortUrl1 = "aab";

        urlShortenerController = new UrlShortenerController(mockUrlService, hostname, port);
        Mockito.when(mockUrlService.getOriginalUrl(shortUrl1)).thenReturn(longUrl1);

        ResponseEntity<Void> res1 = urlShortenerController.lookup(shortUrl1);
        assertEquals(HttpStatus.FOUND, res1.getStatusCode());
        HttpHeaders headers = res1.getHeaders();
        assertEquals(1, headers.size());
        assertEquals(longUrl1, headers.get("Location").get(0));

        String shortUrl2 = "xxy";
        Mockito.when(mockUrlService.getOriginalUrl(shortUrl2)).thenReturn(null);
        ResponseEntity<Void> res2 = urlShortenerController.lookup(shortUrl2);
        assertEquals(HttpStatus.NOT_FOUND, res2.getStatusCode());
        assertEquals(0, res2.getHeaders().size());
    }
}
