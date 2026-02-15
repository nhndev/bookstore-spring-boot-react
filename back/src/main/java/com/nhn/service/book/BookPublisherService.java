package com.nhn.service.book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhn.exception.FuncErrorException;
import com.nhn.mapper.BookPublisherMapper;
import com.nhn.mapstruct.BookMapping;
import com.nhn.model.dto.request.book.publisher.BookPublisherCreateRequest;
import com.nhn.model.dto.request.book.publisher.BookPublisherSearchRequest;
import com.nhn.model.dto.request.book.publisher.BookPublisherUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.PaginationResponse;
import com.nhn.model.dto.response.book.publisher.BookPublisherInfo;
import com.nhn.model.entity.book.BookPublisher;
import com.nhn.repository.book.BookPublisherRepository;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.SlugUtil;
import com.nhn.util.UuidUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookPublisherService {
    private final BookPublisherRepository publisherRepository;

    private final BookPublisherMapper bookPublisherMapper;

    private final BookMapping bookMapping;

    public PaginationResponse<BookPublisher> search(final BookPublisherSearchRequest request) {
        final List<BookPublisher> list       = this.bookPublisherMapper.search(request);
        final Integer             totalItems = this.bookPublisherMapper.count(request);
        return PaginationResponse.<BookPublisher>builder().data(list)
                                 .totalItems(totalItems).build();
    }

    @Transactional
    public BaseResponse createPublisher(final BookPublisherCreateRequest request) {
        final BookPublisher publisher = this.buildBookPublisher(null,
                                                                request.getName());
        final BookPublisher entity    = this.publisherRepository.save(publisher);
        return BaseResponse.builder().data(this.buildBookPublisherInfo(entity))
                           .build();
    }

    @Transactional
    public BaseResponse updateById(final UUID id,
                                   final BookPublisherUpdateRequest request) {
        final BookPublisher publisher = this.buildBookPublisher(id,
                                                                request.getName());
        final BookPublisher entity    = this.publisherRepository.save(publisher);
        return BaseResponse.builder().data(this.buildBookPublisherInfo(entity))
                           .build();
    }

    @Transactional
    public void delete(final UUID id) {
        this.publisherRepository.deleteById(id);
    }

    private BookPublisher buildBookPublisher(final UUID id, final String name) {
        BookPublisher publisher = BookPublisher.builder().build();
        if (Objects.nonNull(id)) {
            final BookPublisher publisherUpdate = this.publisherRepository.findById(id)
                                                                          .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createBookPublisherNotFoundErrorResponse()));
            publisher = this.bookMapping.toBookPublisher(publisherUpdate);
        }

        final String                  slug          = SlugUtil.toSlug(name);
        final Optional<BookPublisher> publisherInDB = this.publisherRepository.findBySlug(slug);
        if (publisherInDB.isPresent()
            && UuidUtil.notEquals(publisherInDB.get().getId(), id)) {
            throw new FuncErrorException(ErrorMsgUtil.createBookPublisherSlugExistsErrorResponse(slug));
        }

        publisher.setName(name);
        publisher.setSlug(slug);
        return publisher;
    }

    private BookPublisherInfo buildBookPublisherInfo(final BookPublisher entity) {
        return BookPublisherInfo.builder().id(entity.getId())
                                .name(entity.getName()).slug(entity.getSlug())
                                .build();
    }
}
