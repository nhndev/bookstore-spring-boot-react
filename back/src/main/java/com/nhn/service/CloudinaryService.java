package com.nhn.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nhn.exception.CloudinaryErrorException;
import com.nhn.model.dto.response.CloudinaryResponse;
import com.nhn.util.ErrorMsgUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @Value("${spring.application.name}")
    private String appName;

    private static final String PUBLIC_ID = "public_id";

    private static final String SECURE_URL = "secure_url";

    private static final String PATH_FORMAT = "%s/%s/%s";

    @Transactional
    public CloudinaryResponse saveAs(final MultipartFile file,
                                     final String folder,
                                     final String fileName) {
        try {
            final Map    result   = this.cloudinary.uploader()
                                                   .upload(file.getBytes(),
                                                           ObjectUtils.asMap(PUBLIC_ID,
                                                                             String.format(PATH_FORMAT,
                                                                                           this.appName,
                                                                                           folder,
                                                                                           fileName)));
            final String url      = (String) result.get(SECURE_URL);
            final String publicId = (String) result.get(PUBLIC_ID);
            return CloudinaryResponse.builder().url(url).publicId(publicId)
                                     .build();
        } catch (final Exception e) {
            log.error("Failed to upload Cloudinary: ", e);
            throw new CloudinaryErrorException(ErrorMsgUtil.createCloudinaryUploadErrorResponse());
        }
    }

    public void delete(final String publicId) {
        try {
            this.cloudinary.uploader().destroy(publicId,
                                               ObjectUtils.emptyMap());
        } catch (final Exception e) {
            log.error("Failed to delete Cloudinary: ", e);
            throw new CloudinaryErrorException(ErrorMsgUtil.createCloudinaryDeleteErrorResponse());
        }
    }
}
