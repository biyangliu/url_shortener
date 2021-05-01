package com.biyang.url_shortener.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biyang.url_shortener.model.Url;
import com.biyang.url_shortener.repository.UrlRepository;

@Service
public class UrlService {

	@Autowired
	private UniqueIdGenerator idGenerator;

	@Autowired
	private UrlRepository urlRepository;

	@Autowired
	private UrlEncoder urlEncoder;


	public String convertAndSaveUrl(String longUrl) {
		long urlId = idGenerator.getUniqueId();
		String shortUrl = urlEncoder.encode(urlId);
		Url url = new Url(urlId, longUrl, shortUrl);
		urlRepository.save(url);

		return shortUrl;
	}

	public String getOriginalUrl(String shortUrl) {
		long id = urlEncoder.decode(shortUrl);
		System.out.println("The decoded ID: " + id);
		Optional<Url> entity = urlRepository.findById(id);

		if (entity.isEmpty()) return null;
		return entity.get().getLongUrl();
	}
}
