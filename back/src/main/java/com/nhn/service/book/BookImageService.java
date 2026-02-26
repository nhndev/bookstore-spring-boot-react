package com.nhn.service.book;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nhn.constant.AppMsg;
import com.nhn.exception.FuncErrorException;
import com.nhn.mapper.BookImageMapper;
import com.nhn.model.dto.request.CloudinaryRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.CloudinaryResponse;
import com.nhn.model.entity.book.Book;
import com.nhn.model.entity.book.BookImage;
import com.nhn.repository.book.BookImageRepository;
import com.nhn.repository.book.BookRepository;
import com.nhn.service.CloudinaryService;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookImageService {
    private final BookRepository bookRepository;

    private final BookImageRepository bookImageRepository;

    private final BookImageMapper bookImageMapper;

    private final CloudinaryService cloudinaryService;

    @Transactional
    public BaseResponse uploadImages(final UUID bookId,
                                     final List<MultipartFile> fileList) {
        final Book                    book       = this.bookRepository.findById(bookId)
                                                                      .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookNotFoundErrorResponse()));
        final String                  slug       = book.getSlug();
        final List<CloudinaryRequest> validImage = new ArrayList<>();
        int                           count      = 1;
        for (final MultipartFile file : fileList) {
            FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
            final String fileName = FileUploadUtil.getFileName(slug, count);
            validImage.add(CloudinaryRequest.builder().file(file)
                                            .fileName(fileName)
                                            .isMain(count == 1).build());
            count++;
        }

        final List<BookImage> bookImages        = new ArrayList<>();
        final List<String>    uploadedPublicIds = new ArrayList<>();
        try {
            for (final CloudinaryRequest item : validImage) {
                final CloudinaryResponse cloudinaryResponse = this.cloudinaryService.saveAs(item.getFile(),
                                                                                            FileUploadUtil.CLOUDINARY_BOOK_FOLDER,
                                                                                            item.getFileName());
                final String             publicId           = cloudinaryResponse.getPublicId();
                uploadedPublicIds.add(publicId);
                final String    url       = cloudinaryResponse.getUrl();
                final BookImage bookImage = BookImage.builder().url(url)
                                                     .publicId(publicId)
                                                     .book(book).build();
                bookImages.add(bookImage);
                if (item.isMain()) {
                    book.setImageUrl(url);
                }
            }
        } catch (final Exception e) {
            log.error("Error uploading images for bookId {}:", bookId, e);
            for (final String publicId : uploadedPublicIds) {
                if (StringUtils.isNotBlank(publicId)) {
                    try {
                        this.cloudinaryService.delete(publicId);
                    } catch (final Exception deleteEx) {
                        log.error("Error deleting image with publicId {} after upload failure for bookId {}:",
                                  publicId, bookId, deleteEx);
                    }
                }
            }
            throw e;
        }
        this.bookImageRepository.saveAll(bookImages);
        this.bookRepository.save(book);
        return BaseResponse.builder().data(AppMsg.FUNC_UPLOAD_IMAGE_SUCCESS_MSG)
                           .build();
    }

    @Transactional
    public void syncImages(final UUID bookId, final List<UUID> keepImageIds,
                           final List<MultipartFile> newFiles) {
        final boolean noKeepList = !CollectionUtils.isNotEmpty(keepImageIds);
        final boolean noNewFiles = !CollectionUtils.isNotEmpty(newFiles);
        if (noKeepList && noNewFiles) {
            return;
        }

        final Book            book     = this.bookRepository.findById(bookId)
                                                            .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookNotFoundErrorResponse()));
        final List<BookImage> existing = this.bookImageMapper.getAllByBookId(bookId);
        final Set<UUID>       keepSet  = noKeepList ? new HashSet<>(existing.stream()
                                                                            .map(BookImage::getId)
                                                                            .toList())
                                                    : new HashSet<>(keepImageIds);

        for (final BookImage img : existing) {
            if (!keepSet.contains(img.getId())) {
                final String publicId = img.getPublicId();
                if (StringUtils.isNotBlank(publicId)) {
                    this.cloudinaryService.delete(publicId);
                }
                this.bookImageRepository.delete(img);
            }
        }

        final List<BookImage> added = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(newFiles)) {
            final String slug  = book.getSlug();
            int          count = existing.stream()
                                         .filter(e -> keepSet.contains(e.getId()))
                                         .toList().size();
            for (final MultipartFile file : newFiles) {
                FileUploadUtil.assertAllowed(file,
                                             FileUploadUtil.IMAGE_PATTERN);
                count++;
                final String             fileName           = FileUploadUtil.getFileName(slug,
                                                                                         count);
                final CloudinaryResponse cloudinaryResponse = this.cloudinaryService.saveAs(file,
                                                                                            FileUploadUtil.CLOUDINARY_BOOK_FOLDER,
                                                                                            fileName);
                final BookImage          bookImage          = BookImage.builder()
                                                                       .url(cloudinaryResponse.getUrl())
                                                                       .publicId(cloudinaryResponse.getPublicId())
                                                                       .book(book)
                                                                       .build();
                added.add(this.bookImageRepository.save(bookImage));
            }
        }

        String mainUrl = null;
        if (CollectionUtils.isNotEmpty(keepImageIds)) {
            final BookImage firstKept = this.bookImageRepository.findById(keepImageIds.get(0))
                                                                .orElse(null);
            if (Objects.nonNull(firstKept)) {
                mainUrl = firstKept.getUrl();
            }
        }
        if (StringUtils.isBlank(mainUrl) && CollectionUtils.isNotEmpty(added)) {
            mainUrl = added.get(0).getUrl();
        }
        if (StringUtils.isBlank(mainUrl)) {
            final BookImage kept = this.bookImageMapper.getFirstByBookId(bookId);
            if (Objects.nonNull(kept)) {
                mainUrl = kept.getUrl();
            }
        }
        book.setImageUrl(mainUrl);
        this.bookRepository.save(book);
    }
}
