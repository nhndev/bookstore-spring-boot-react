package com.nhn.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.nhn.exception.FileErrorException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUploadUtil {
    public static final long DEFAULT_MAX_SIZE = 2 * 1024 * 1024;

    public static final String IMAGE_PATTERN = "^(gif|jpe?g|tiff?|png|webp|bmp)$";

    public static final String DATE_FORMAT = "yyyyMMddHHmmssSSS";

    public static final String CLOUDINARY_BOOK_FOLDER = "book";

    public static final String CLOUDINARY_BOOK_CATEGORY_FOLDER = "category";

    public static final String FILE_NAME_FORMAT = "%s_%s_%s";

    public static boolean isAllowedExtension(final String extension,
                                             final String pattern) {
        final Matcher matcher = Pattern.compile(pattern,
                                                Pattern.CASE_INSENSITIVE)
                                       .matcher(extension);
        return matcher.matches();
    }

    public static void assertAllowed(final MultipartFile file,
                                     final String pattern) {
        final long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE) {
            throw new FileErrorException(ErrorMsgUtil.createValidationImageSizeErrorResponse(DEFAULT_MAX_SIZE /
                                                                                             1024 /
                                                                                             1024));
        }

        final String fileName  = file.getOriginalFilename();
        final String extension = FilenameUtils.getExtension(fileName);
        if (!isAllowedExtension(extension, pattern)) {
            throw new FileErrorException(ErrorMsgUtil.createValidationImageExtensionErrorResponse(extension));
        }
    }

    public static String getFileName(final String slug, final int count) {
        final DateFormat dateFormatter   = new SimpleDateFormat(DATE_FORMAT);
        final String     currentDateTime = dateFormatter.format(new Date());
        return String.format(FILE_NAME_FORMAT, slug, currentDateTime, count);
    }

    public static String extractCloudinaryPublicId(final String url) {
        final String  regex   = "upload/(?:v\\d+/)?([^\\.]+)";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
