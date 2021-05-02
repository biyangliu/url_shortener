package com.biyang.url_shortener.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.biyang.url_shortener.model.Url;
import com.biyang.url_shortener.repository.UrlRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UrlService {

	@Autowired
	private UniqueIdGenerator idGenerator;

	@Autowired
	private UrlRepository urlRepository;

	@Autowired
	private UrlEncoder urlEncoder;

	@Cacheable(value = "create-urls", key = "#longUrl", sync = true)
	public Url convertAndSaveUrl(String longUrl) {
		long urlId = idGenerator.getUniqueId();
		String shortUrl = urlEncoder.encode(urlId);
		Url url = new Url(urlId, longUrl, shortUrl);
		urlRepository.save(url);

		return url;
	}

	@Cacheable(value = "lookup-urls", key = "#shortUrl", sync = true)
	public String getOriginalUrl(String shortUrl) {
		long id = urlEncoder.decode(shortUrl);
		log.info("Decoded ID: {}", id);
		Optional<Url> entity = urlRepository.findById(id);

		if (entity.isEmpty()) return null;
		return entity.get().getLongUrl();
	}
}
