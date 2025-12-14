package com.nhn.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloudinaryRequest {
    private MultipartFile file;

    private String fileName;

    private boolean isMain;
}
