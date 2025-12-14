package com.nhn.controller.book;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhn.annotation.SysAuthorize;
import com.nhn.constant.SysRole;
import com.nhn.model.dto.request.book.publisher.BookPublisherCreateRequest;
import com.nhn.model.dto.request.book.publisher.BookPublisherUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.service.book.BookPublisherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/book-publishers")
@RequiredArgsConstructor
public class BookPublisherController {
    private final BookPublisherService bookPublisherService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(this.bookPublisherService.findAll());
    }

    @PostMapping
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_PUBLISHER_CREATE"})
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody final BookPublisherCreateRequest request) {
        return ResponseEntity.ok()
                             .body(this.bookPublisherService.createPublisher(request));
    }

    @PutMapping("/{id}")
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_PUBLISHER_EDIT"})
    public ResponseEntity<BaseResponse> update(@PathVariable final UUID id,
                                               @Valid @RequestBody final BookPublisherUpdateRequest request) {
        return ResponseEntity.ok()
                             .body(this.bookPublisherService.updateById(id,
                                                                        request));
    }
}
