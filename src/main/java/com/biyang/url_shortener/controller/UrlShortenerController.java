package com.biyang.url_shortener.controller;

import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.biyang.url_shortener.service.UrlService;

@Controller
public class UrlShortenerController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UrlService urlService;

	@Value("${server.hostname}")
	private String hostname;

	@Value("${server.port}")
	private int port;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public String create(@RequestParam("url") String longUrl) throws UnknownHostException {
		logger.info("API Called: CREATE: {}", longUrl);
		try {
			new URL(longUrl).toURI();
		} catch (Exception e) {
			logger.info("Invalid URL {}", longUrl);
			return "Invalid URL! " + e.getMessage();
		}

		String shortUrl = urlService.convertAndSaveUrl(longUrl);
		StringBuilder sb = new StringBuilder();
		sb.append(hostname);
		sb.append(':');
		sb.append(port);
		sb.append("/get/");
		sb.append(shortUrl);
		logger.info("Short URL created: {}", sb.toString());
		return sb.toString();
    }

	@RequestMapping(value = "/get/{shortUrl}", method = RequestMethod.GET)
    @Cacheable(value = "urls", key = "#shortUrl", sync = true)
	public ResponseEntity<Void> lookup(@PathVariable String shortUrl) {
		logger.info("API Called: LOOKUP: {}", shortUrl);
		if (shortUrl == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
        String url = urlService.getOriginalUrl(shortUrl);
		logger.info("Short URL: {}  Found URL: {}", shortUrl, url);
		if (url == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}
