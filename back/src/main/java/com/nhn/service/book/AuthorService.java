package com.nhn.service.book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhn.exception.FuncErrorException;
import com.nhn.mapper.AuthorMapper;
import com.nhn.mapstruct.BookMapping;
import com.nhn.model.dto.request.book.author.AuthorCreateRequest;
import com.nhn.model.dto.request.book.author.AuthorSearchRequest;
import com.nhn.model.dto.request.book.author.AuthorUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.PaginationResponse;
import com.nhn.model.dto.response.author.AuthorInfo;
import com.nhn.model.entity.book.Author;
import com.nhn.repository.book.AuthorRepository;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.SlugUtil;
import com.nhn.util.UuidUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    private final BookMapping bookMapping;

    public PaginationResponse<Author> search(final AuthorSearchRequest request) {
        final List<Author> list       = this.authorMapper.search(request);
        final Integer      totalItems = this.authorMapper.count(request);
        return PaginationResponse.<Author>builder().data(list)
                                 .totalItems(totalItems).build();
    }

    public BaseResponse findById(final UUID id) {
        final Author author = this.authorRepository.findById(id)
                                                   .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createAuthorNotFoundErrorResponse()));
        return BaseResponse.builder().data(this.buildAuthorInfo(author))
                           .build();
    }

    @Transactional
    public BaseResponse createAuthor(final AuthorCreateRequest request) {
        final Author author = this.buildAuthor(null, request.getName());
        final Author entity = this.authorRepository.save(author);
        return BaseResponse.builder().data(this.buildAuthorInfo(entity))
                           .build();
    }

    @Transactional
    public BaseResponse updateById(final UUID id,
                                   final AuthorUpdateRequest request) {
        final Author author = this.buildAuthor(id, request.getName());
        final Author entity = this.authorRepository.save(author);
        return BaseResponse.builder().data(this.buildAuthorInfo(entity))
                           .build();
    }

    @Transactional
    public void delete(final UUID id) {
        this.authorRepository.deleteById(id);
    }

    private Author buildAuthor(final UUID id, final String name) {
        Author author = Author.builder().build();
        if (Objects.nonNull(id)) {
            final Author authorUpdate = this.authorRepository.findById(id)
                                                             .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createAuthorNotFoundErrorResponse()));
            author = this.bookMapping.toAuthor(authorUpdate);
        }

        final String           slug       = SlugUtil.toSlug(name);
        final Optional<Author> authorInDB = this.authorRepository.findBySlug(slug);
        if (authorInDB.isPresent()
            && UuidUtil.notEquals(authorInDB.get().getId(), id)) {
            throw new FuncErrorException(ErrorMsgUtil.createAuthorSlugExistsErrorResponse(slug));
        }

        author.setName(name);
        author.setSlug(slug);
        return author;
    }

    private AuthorInfo buildAuthorInfo(final Author entity) {
        return AuthorInfo.builder().id(entity.getId()).name(entity.getName())
                         .slug(entity.getSlug()).build();
    }
}
