package com.biyang.url_shortener.controller;

import java.net.URI;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.biyang.url_shortener.model.Url;
import com.biyang.url_shortener.service.UrlService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UrlShortenerController {

    private UrlService urlService;
    private String hostname;
    private int port;

    @Autowired
    public UrlShortenerController(UrlService urlService, @Value("${server.hostname}") String hostname,
            @Value("${server.port}") int port) {
        this.urlService = urlService;
        this.hostname = hostname;
        this.port = port;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public String create(@RequestParam("url") String longUrl) {
        log.info("API Called: CREATE: {}", longUrl);
        try {
            new URL(longUrl).toURI();
        } catch (Exception e) {
            log.info("Invalid URL {}", longUrl);
            return "Invalid URL! " + e.getMessage();
        }

        Url shortUrl = urlService.convertAndSaveUrl(longUrl);
        log.info("Long URL {} converted short URL {}", longUrl, shortUrl.getShortUrl());

        StringBuilder sb = new StringBuilder();
        sb.append(hostname);
        sb.append(':');
        sb.append(port);
        sb.append("/get/");
        sb.append(shortUrl.getShortUrl());
        return sb.toString();
    }

    @RequestMapping(value = "/get/{shortUrl}", method = RequestMethod.GET)
    public ResponseEntity<Void> lookup(@PathVariable String shortUrl) {
        log.info("API Called: LOOKUP: {}", shortUrl);
        if (shortUrl == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String url = urlService.getOriginalUrl(shortUrl);
        log.info("Short URL: {}  Found URL: {}", shortUrl, url);
        if (url == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }
}
