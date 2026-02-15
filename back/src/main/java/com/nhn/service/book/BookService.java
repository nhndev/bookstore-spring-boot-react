package com.nhn.service.book;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhn.exception.FuncErrorException;
import com.nhn.mapstruct.BookMapping;
import com.nhn.model.dto.request.book.BookCreateRequest;
import com.nhn.model.dto.request.book.BookUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.entity.book.Book;
import com.nhn.model.entity.book.BookAuthor;
import com.nhn.model.entity.book.BookCategory;
import com.nhn.model.entity.book.BookPublisher;
import com.nhn.repository.book.*;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.StringUtil;
import com.nhn.util.UuidUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;

    private final BookCategoryRepository bookCategoryRepository;

    private final BookPublisherRepository bookPublisherRepository;

    private final AuthorRepository authorRepository;

    private final BookAuthorRepository bookAuthorRepository;

    private final BookMapping bookMapping;

    @Transactional
    public BaseResponse createBook(final BookCreateRequest request) {
        final Book                                      book                  = this.bookRepository.save(this.buildBookEntity(null,
                                                                                                                              request));
        final List<BookCreateRequest.BookAuthorRequest> bookAuthorRequestList = request.getAuthors();
        if (CollectionUtils.isNotEmpty(bookAuthorRequestList)) {
            this.saveAuthors(false, book.getId(), bookAuthorRequestList);
        }
        return BaseResponse.builder()
                           .data(this.bookMapping.toBookBasicInfo(book))
                           .build();
    }

    @Transactional
    public BaseResponse updateById(final UUID id,
                                   final BookUpdateRequest request) {
        final Book                                      book                  = this.bookRepository.save(this.buildBookEntity(id,
                                                                                                                              request));
        final List<BookCreateRequest.BookAuthorRequest> bookAuthorRequestList = request.getAuthors();
        if (CollectionUtils.isNotEmpty(bookAuthorRequestList)) {
            this.saveAuthors(true, book.getId(), bookAuthorRequestList);
        }
        return BaseResponse.builder()
                           .data(this.bookMapping.toBookBasicInfo(book))
                           .build();
    }

    private void saveAuthors(final boolean isUpdate, final UUID bookId,
                             final List<BookCreateRequest.BookAuthorRequest> bookAuthorRequestList) {
        final List<BookAuthor> bookAuthorList = new ArrayList<>();
        // Check if authorIds in request exist in DB
        final List<UUID> authorIdsRequest = bookAuthorRequestList.stream()
                                                                 .map(BookCreateRequest.BookAuthorRequest::getAuthorId)
                                                                 .toList();
        final List<UUID> authorIdsInDB    = this.authorRepository.findAuthorIdsIn(authorIdsRequest);
        if (!ArrayUtils.isSameLength(authorIdsRequest.toArray(),
                                     authorIdsInDB.toArray())) {
            final List<UUID> nonExistentIds = authorIdsRequest.stream()
                                                              .filter(id -> !authorIdsInDB.contains(id))
                                                              .toList();
            log.error("[Save authors] non-existent authorIds: {}",
                      nonExistentIds);
            throw new FuncErrorException(ErrorMsgUtil.createAuthorNotFoundErrorResponse());
        }

        // Save book authors
        boolean flag = false;
        for (final BookCreateRequest.BookAuthorRequest bookAuthor : bookAuthorRequestList) {
            final Boolean isMainAuthor = bookAuthor.getIsMain();
            if (Objects.nonNull(isMainAuthor)
                && BooleanUtils.compare(isMainAuthor, Boolean.TRUE) == 0) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            bookAuthorRequestList.get(0).setIsMain(true);
        }

        bookAuthorRequestList.forEach(bookAuthorRequest -> {
            final UUID authorId = bookAuthorRequest.getAuthorId();
            bookAuthorList.add(BookAuthor.builder().bookId(bookId)
                                         .isMain(bookAuthorRequest.getIsMain())
                                         .authorId(authorId).build());
        });
        if (isUpdate) {
            this.bookAuthorRepository.deleteByBookId(bookId);
        }
        this.bookAuthorRepository.saveAll(bookAuthorList);
    }

    private Book buildBookEntity(final UUID id,
                                 final BookCreateRequest request) {
        Book bookEntity = Book.builder().build();
        if (Objects.nonNull(id)) {
            final Book bookUpdate = this.bookRepository.findById(id)
                                                       .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookNotFoundErrorResponse()));
            bookEntity = this.bookMapping.toBook(bookUpdate);
        } else {
            bookEntity = this.bookMapping.toBook(request);
        }

        final String         slug     = StringUtil.toSlug(request.getName());
        final Optional<Book> bookInDB = this.bookRepository.findBySlug(slug);
        if (bookInDB.isPresent()
            && UuidUtil.notEquals(bookInDB.get().getId(), id)) {
            throw new FuncErrorException(ErrorMsgUtil.createBookSlugExistsErrorResponse(slug));
        }

        final BookCategory  bookCategory  = this.bookCategoryRepository.findById(request.getCategoryId())
                                                                       .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookCategoryNotFoundErrorResponse()));
        final BookPublisher bookPublisher = this.bookPublisherRepository.findById(request.getPublisherId())
                                                                        .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookPublisherNotFoundErrorResponse()));
        bookEntity.setSlug(slug);
        bookEntity.setCategory(bookCategory);
        bookEntity.setPublisher(bookPublisher);
        return bookEntity;
    }

    //    @Override
    //    public ResponsePageDTO findAll(final String keyword, final Pageable pageable) {
    //        final Page<Product> productPage = this.bookRepository.findAll(keyword, pageable);
    //
    //        final List<ProductFilterDetailDTO> productBasicDTOS = new ArrayList<>();
    //
    //        for(final Product product : productPage.getContent()) {
    //            final ProductFilterDetailDTO productFilterDetailDTO = this.productMapper.fromEntityToFilterDetail(product);
    //            productFilterDetailDTO.setProductCategory(this.productCategoryMapper.fromEntityToBasic(product.getProductCategory()));
    //            productFilterDetailDTO.setProductBrand(this.productBrandMapper.fromEntityToDetail(product.getProductBrand()));
    //
    //            productBasicDTOS.add(productFilterDetailDTO);
    //        }
    //
    //        return ResponsePageDTO.builder()
    //                .data(productBasicDTOS)
    //                .limit(productPage.getSize())
    //                .currentPage(productPage.getNumber())
    //                .totalItems(productPage.getTotalElements())
    //                .totalPages(productPage.getTotalPages())
    //                .build();
    //    }
    //
    //    @Override
    //    public ResponsePageDTO filterByCategoryList(final List<UUID> uuids, final Pageable pageable) {
    //        final Page<Product> productPage = this.bookRepository.filterByCategoryList(uuids, pageable);
    //
    //        final List<ProductFilterBasicDTO> productFilterBasicDTOS = new ArrayList<>();
    //
    //        for(final Product product : productPage.getContent()) {
    //            final ProductFilterBasicDTO productFilterBasicDTO = this.productMapper.fromEntityToFilterBasic(product);
    //            productFilterBasicDTOS.add(productFilterBasicDTO);
    //        }
    //
    //        return ResponsePageDTO.builder()
    //                .data(productFilterBasicDTOS)
    //                .limit(productPage.getSize())
    //                .currentPage(productPage.getNumber())
    //                .totalItems(productPage.getTotalElements())
    //                .totalPages(productPage.getTotalPages())
    //                .build();
    //    }
    //
    //    @Override
    //    public ResponsePageDTO searchProduct(final String keyword, final Pageable pageable) {
    //        final Page<Product> productPage = this.bookRepository.searchProduct(keyword, pageable);
    //
    //        final List<ProductFilterBasicDTO> productFilterBasicDTOS = new ArrayList<>();
    //
    //        for(final Product product : productPage.getContent()) {
    //            final ProductFilterBasicDTO productFilterBasicDTO = this.productMapper.fromEntityToFilterBasic(product);
    //            productFilterBasicDTOS.add(productFilterBasicDTO);
    //        }
    //
    //        return ResponsePageDTO.builder()
    //                .data(productFilterBasicDTOS)
    //                .limit(productPage.getSize())
    //                .currentPage(productPage.getNumber())
    //                .totalItems(productPage.getTotalElements())
    //                .totalPages(productPage.getTotalPages())
    //                .build();
    //    }
    //
    //    @Override
    //    public BaseResponse findById(final UUID id) throws NotFoundException {
    //        final Product product = this.bookRepository.findById(id)
    //                .orElseThrow(() -> new NotFoundException("Not found product with id: " + id));
    //
    //        final ProductDetailDTO productDetailDTO = this.productMapper.fromEntityToDetail(product);
    //        productDetailDTO.setProductCategory(this.productCategoryMapper.fromEntityToBasic(product.getProductCategory()));
    //        productDetailDTO.setProductBrand(this.productBrandMapper.fromEntityToDetail(product.getProductBrand()));
    //
    //        final List<ProductImageBasicDTO> productImageBasicDTOS = new ArrayList<>();
    //        for(final ProductImage productImage : product.getProductImages()) {
    //            productImageBasicDTOS.add(this.productImageMapper.fromEntityToBasic(productImage));
    //        }
    //
    //        productDetailDTO.setImages(productImageBasicDTOS);
    //
    //        return BaseResponse.builder()
    //                .data(productDetailDTO)
    //                .isSuccess(true)
    //                .build();
    //    }
    //
    //    @Override

    //
    //        product.setName(productUpdateDTO.getName());
    //        product.setSlug(slug);
    //        product.setDescription(productUpdateDTO.getDescription());
    //        product.setPrice(productUpdateDTO.getPrice());
    //        product.setDiscount(productUpdateDTO.getDiscount());
    //        product.setQuantity(productUpdateDTO.getQuantity());
    //        product.setProductCategory(productCategory);
    //        product.setProductBrand(productBrand);
    //
    //        this.bookRepository.save(product);
    //
    //        final ProductBasicDTO productBasicDTO = this.productMapper.fromEntityToBasic(product);
    //        productBasicDTO.setCategoryId(productCategoryId);
    //        productBasicDTO.setBrandId(productBrandId);
    //
    //        return BaseResponse.builder()
    //                .data(productBasicDTO)
    //                .isSuccess(true)
    //                .build();
    //    }
}
