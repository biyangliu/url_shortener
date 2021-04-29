package com.biyang.url_shortener.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.biyang.url_shortener.model.Url;

@Repository
public interface UrlRepository extends MongoRepository<Url, Long> {
}
