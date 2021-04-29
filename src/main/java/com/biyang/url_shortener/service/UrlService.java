package com.biyang.url_shortener.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.biyang.url_shortener.lib.UrlEncoder;
import com.biyang.url_shortener.model.Url;
import com.biyang.url_shortener.repository.UrlRepository;

@Service
public class UrlService {

	private static AtomicLong urlIdGenerator = new AtomicLong();

	private final UrlRepository urlRepository;
	private final UrlEncoder urlEncoder;

	public UrlService(UrlRepository urlRepository, UrlEncoder urlEncoder) {
		this.urlRepository = urlRepository;
		this.urlEncoder = urlEncoder;
	}

	public String convertAndSaveUrl(String longUrl) {
		long urlId = urlIdGenerator.getAndIncrement();
		String shortUrl = urlEncoder.encode(urlId);
		Url url = new Url(urlId, longUrl, shortUrl);
		urlRepository.save(url);

		return shortUrl;
	}

	public String getOriginalUrl(String shortUrl) {
		long id = urlEncoder.decode(shortUrl);
		Optional<Url> entity = urlRepository.findById(id);

		if (entity.isEmpty()) return null;
		return entity.get().getLongUrl();
	}
}
