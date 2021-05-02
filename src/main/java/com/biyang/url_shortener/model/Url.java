package com.biyang.url_shortener.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * URL model class to be persisted in database.
 *
 * @author byliu
 */
@Document(collection = "urls")
public class Url implements Serializable {

    private static final long serialVersionUID = -2834217891251633143L;

    /** The ID for the URL. */
    @Id
    private long urlId;

    /** Long URL. */
    private String longUrl;

    /** Short URL. */
    private String shortUrl;

    /** Created date. */
    private Date created;

    public Url(long urlId, String longUrl, String shortUrl) {
        this.urlId = urlId;
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        created = new Date();
    }

    /**
     * Get String representation of the object.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format("URL: [id=%d, long=%s, short=%s, created=%s]", urlId, longUrl, shortUrl, created);
    }

    public long getUrlId() {
        return urlId;
    }

    public void setUrlId(long urlId) {
        this.urlId = urlId;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
