package com.nhn.service.book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nhn.constant.AppMsg;
import com.nhn.exception.FuncErrorException;
import com.nhn.mapper.BookCategoryMapper;
import com.nhn.mapstruct.BookMapping;
import com.nhn.model.dto.request.book.category.BookCategoryCreateRequest;
import com.nhn.model.dto.request.book.category.BookCategorySearchRequest;
import com.nhn.model.dto.request.book.category.BookCategoryUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.CloudinaryResponse;
import com.nhn.model.dto.response.PaginationResponse;
import com.nhn.model.dto.response.book.category.BookCategoryBasicInfo;
import com.nhn.model.dto.response.book.category.BookCategoryInfo;
import com.nhn.model.entity.book.BookCategory;
import com.nhn.repository.book.BookCategoryRepository;
import com.nhn.service.CloudinaryService;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.FileUploadUtil;
import com.nhn.util.StringUtil;
import com.nhn.util.UuidUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookCategoryService {
    private final static int MAX_DEPTH = 2;

    private final BookCategoryRepository categoryRepository;

    private final BookCategoryMapper bookCategoryMapper;

    private final BookMapping bookMapping;

    private final CloudinaryService cloudinaryService;

    public PaginationResponse<BookCategoryInfo> search(final BookCategorySearchRequest request) {
        final List<BookCategoryInfo> list       = this.bookCategoryMapper.search(request);
        final Integer                totalItems = this.bookCategoryMapper.count(request);
        return PaginationResponse.<BookCategoryInfo>builder().data(list)
                                 .totalItems(totalItems).build();
    }

    public BaseResponse<List<BookCategoryBasicInfo>> findAll() {
        final List<BookCategory>          list   = this.categoryRepository.findAll();
        final List<BookCategoryBasicInfo> result = list.stream()
                                                       .map(this::buildBookCategoryBasicInfo)
                                                       .toList();
        return BaseResponse.<List<BookCategoryBasicInfo>>builder().data(result)
                           .build();
    }

    public BaseResponse<BookCategoryInfo> findById(final UUID id) {
        final BookCategory entity = this.categoryRepository.findById(id)
                                                           .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookCategoryNotFoundErrorResponse()));
        return BaseResponse.<BookCategoryInfo>builder()
                           .data(this.buildBookCategory(entity)).build();
    }

    @Transactional
    public BaseResponse createCategory(final BookCategoryCreateRequest request,
                                       final MultipartFile file) {
        final UUID         parentId = request.getParentId();
        final BookCategory category = this.buildBookCategory(null,
                                                             request.getName(),
                                                             parentId);
        // upload image
        if (Objects.isNull(parentId) && Objects.nonNull(file)) {
            FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
            final String             fileName           = FileUploadUtil.getFileName(category.getSlug(),
                                                                                     1);
            final CloudinaryResponse cloudinaryResponse = this.cloudinaryService.saveAs(file,
                                                                                        FileUploadUtil.CLOUDINARY_BOOK_CATEGORY_FOLDER,
                                                                                        fileName);
            final String             imageUrl           = cloudinaryResponse.getUrl();
            category.setImageUrl(imageUrl);
        }

        this.categoryRepository.save(category);

        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    @Transactional
    public BaseResponse updateById(final UUID id,
                                   final BookCategoryUpdateRequest request,
                                   final MultipartFile file) {
        final UUID         parentId = request.getParentId();
        final BookCategory category = this.buildBookCategory(id,
                                                             request.getName(),
                                                             parentId);
        // upload image
        if (Objects.isNull(parentId) && Objects.nonNull(file)) {
            FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
            final String             fileName           = FileUploadUtil.getFileName(category.getSlug(),
                                                                                     1);
            final CloudinaryResponse cloudinaryResponse = this.cloudinaryService.saveAs(file,
                                                                                        FileUploadUtil.CLOUDINARY_BOOK_CATEGORY_FOLDER,
                                                                                        fileName);

            this.deleteCloudinaryImage(category.getImageUrl());
            category.setImageUrl(cloudinaryResponse.getUrl());
        }

        this.categoryRepository.save(category);
        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    @Transactional
    public void delete(final UUID id) {
        final BookCategory category = this.categoryRepository.findById(id)
                                                             .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookCategoryNotFoundErrorResponse()));
        final String       imageUrl = category.getImageUrl();
        this.categoryRepository.deleteById(category.getId());
        this.deleteCloudinaryImage(imageUrl);
    }

    private void deleteCloudinaryImage(final String imageUrl) {
        if (StringUtils.isNotBlank(imageUrl)) {
            final String publicId = FileUploadUtil.extractCloudinaryPublicId(imageUrl);
            if (StringUtils.isNotBlank(publicId)) {
                log.info("Delete Cloudinary image with publicId: {}", publicId);
                this.cloudinaryService.delete(publicId);
            }
        }
    }

    private BookCategory buildBookCategory(final UUID id, final String name,
                                           final UUID parentId) {
        BookCategory category = BookCategory.builder().build();
        if (Objects.nonNull(id)) {
            final BookCategory categoryUpdate = this.categoryRepository.findById(id)
                                                                       .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookCategoryNotFoundErrorResponse()));
            category = this.bookMapping.toBookCategory(categoryUpdate);
        }

        final String                 slug         = StringUtil.toSlug(name);
        final Optional<BookCategory> categoryInDB = this.categoryRepository.findBySlug(slug);
        if (categoryInDB.isPresent()
            && UuidUtil.notEquals(categoryInDB.get().getId(), id)) {
            throw new FuncErrorException(ErrorMsgUtil.createBookCategorySlugExistsErrorResponse(slug));
        }

        BookCategory categoryParent = null;
        if (Objects.nonNull(parentId)) {
            categoryParent = this.categoryRepository.findById(parentId)
                                                    .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookCategoryNotFoundErrorResponse()));
            if (Objects.nonNull(categoryParent)) {
                this.checkDepth(categoryParent);
            }
        }

        category.setName(name);
        category.setSlug(slug);
        category.setParent(categoryParent);
        return category;
    }

    private BookCategoryBasicInfo buildBookCategoryBasicInfo(final BookCategory entity) {
        return BookCategoryBasicInfo.builder().id(entity.getId())
                                    .name(entity.getName()).build();
    }

    private BookCategoryInfo buildBookCategory(final BookCategory entity) {
        final BookCategory parent = entity.getParent();
        return BookCategoryInfo.builder().id(entity.getId())
                               .name(entity.getName())
                               .imageUrl(entity.getImageUrl())
                               .parentId(Objects.nonNull(parent) ? parent.getId()
                                                                         .toString()
                                                                 : null)
                               .build();
    }

    private void checkDepth(final BookCategory categoryParent) {
        if (Objects.nonNull(categoryParent.getParent())) {
            throw new FuncErrorException(ErrorMsgUtil.createBookCategoryInvalidDepthErrorResponse(MAX_DEPTH));
        }
    }
}
