package com.ksyun.ks3.model;

import com.ksyun.ks3.services.request.tag.ObjectTagging;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author TANGLUO
 */
public class ObjectMetadata {
    public static enum Meta {
        ContentType(HttpHeaders.ContentType), CacheControl(
                HttpHeaders.CacheControl), ContentLength(
                HttpHeaders.ContentLength), ContentDisposition(
                HttpHeaders.ContentDisposition), ContentEncoding(
                HttpHeaders.ContentEncoding), Expires(HttpHeaders.Expires), LastModified(HttpHeaders.LastModified),
        Etag(HttpHeaders.ETag), ContentMD5(HttpHeaders.ContentMD5), ContentLanguage(HttpHeaders.ContentLanguage),  XKssObjectTagCount(HttpHeaders.XKssObjectTagCount),
        STORAGE_CLASS(HttpHeaders.ContentLanguage);
        private HttpHeaders header;

        public HttpHeaders getHeader() {
            return this.header;
        }

        private Meta(HttpHeaders header) {
            this.header = header;
        }

        public String toString() {
            return header.toString();
        }
    }

    public static final String userMetaPrefix = "x-kss-meta-";
    private Map<String, String> userMetadata = new HashMap<String, String>();
    private Map<Meta, String> metadata = new HashMap<Meta, String>();

    @Override
    public String toString() {
        return "ObjectMetadata[metadata=" + this.metadata + ";userMetadata="
                + this.userMetadata + "]";
    }

    public Map<Meta, String> getMetadata() {
        return metadata;
    }

    public void setContentType(String s) {
        this.metadata.put(Meta.ContentType, s);
    }

    public void setCacheControl(String s) {
        this.metadata.put(Meta.CacheControl, s);
    }

    public void setContentDisposition(String s) {
        this.metadata.put(Meta.ContentDisposition, s);
    }

    public void setContentEncoding(String s) {
        this.metadata.put(Meta.ContentEncoding, s);
    }

    public void setExpires(String s) {
        this.metadata.put(Meta.Expires, s);
    }

    public void setContentLength(String s) {
        this.metadata.put(Meta.ContentLength, s);
    }

    public Long getContentLength() {
        Long contentLength = 0L;
        String contentLengthStr = this.metadata.get(HttpHeaders.ContentLength);
        if (contentLengthStr != null) {
            contentLength = Long.parseLong(contentLengthStr);
        }
        return contentLength;
    }

    public String getContentMD5() {
        return this.metadata.get(Meta.ContentMD5);
    }

    public String getContentEtag() {
        return this.metadata.get(Meta.Etag);
    }

    public ObjectTagging getTagging() {
        return tagging;
    }

    public void setTagging(ObjectTagging tagging) {
        this.tagging = tagging;
    }

    private ObjectTagging tagging;

    public ObjectTagging getObjectTagging() {
        return tagging;
    }

    public void setObjectTagging(ObjectTagging tagging) {
        this.tagging = tagging;
    }

    public void addOrEditUserMeta(String key, String value) {
        if (!key.startsWith(ObjectMetadata.userMetaPrefix))
            throw new IllegalArgumentException("key should be start with:"
                    + ObjectMetadata.userMetaPrefix);
        this.userMetadata.put(key, value);
    }

    public void addOrEditMeta(Meta key, String value) {
        this.metadata.put(key, value);
    }

    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    public void setContentLength(long length) {
        // TODO Auto-generated method stub
        metadata.put(Meta.ContentLength, String.valueOf(length));
    }

    public String getContentType() {
        return metadata.get(HttpHeaders.ContentType.toString());
    }


    public String getObjectTagCount() {
        return metadata.get(HttpHeaders.XKssObjectTagCount.toString());
    }
    public void setObjectTagCount(String count) {
        metadata.put(Meta.XKssObjectTagCount, count);
    }
}
