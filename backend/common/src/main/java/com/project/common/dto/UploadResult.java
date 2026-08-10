package com.project.common.dto;

public class UploadResult {

    private String url;
    private String publicId;

    public UploadResult(String url, String publicId) {
        this.url = url;
        this.publicId = publicId;
    }

    public String getUrl() {
        return url;
    }

    public String getPublicId() {
        return publicId;
    }
}