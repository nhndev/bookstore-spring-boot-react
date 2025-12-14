package com.nhn.service.book;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nhn.constant.AppMsg;
import com.nhn.exception.FuncErrorException;
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

@Service
@RequiredArgsConstructor
public class BookImageService {
    private final BookRepository bookRepository;

    private final BookImageRepository bookImageRepository;

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

        final List<BookImage> bookImages = new ArrayList<>();
        for (final CloudinaryRequest item : validImage) {
            final CloudinaryResponse cloudinaryResponse = this.cloudinaryService.saveAs(item.getFile(),
                                                                                        FileUploadUtil.CLOUDINARY_BOOK_FOLDER,
                                                                                        item.getFileName());
            final String             url                = cloudinaryResponse.getUrl();
            final BookImage          bookImage          = BookImage.builder()
                                                                   .url(url)
                                                                   .publicId(cloudinaryResponse.getPublicId())
                                                                   .book(book)
                                                                   .build();
            bookImages.add(bookImage);
            if (item.isMain()) {
                book.setImageUrl(url);
            }
        }
        this.bookImageRepository.saveAll(bookImages);
        this.bookRepository.save(book);
        return BaseResponse.builder().data(AppMsg.FUNC_UPLOAD_IMAGE_SUCCESS_MSG)
                           .build();
    }
}
