package com.nhn.controller.book;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhn.annotation.SysAuthorize;
import com.nhn.constant.AppMsg;
import com.nhn.constant.SysRole;
import com.nhn.model.dto.request.book.author.AuthorCreateRequest;
import com.nhn.model.dto.request.book.author.AuthorSearchRequest;
import com.nhn.model.dto.request.book.author.AuthorUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.PaginationResponse;
import com.nhn.service.book.AuthorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/book-authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<PaginationResponse> search(@Valid final AuthorSearchRequest request) {
        return ResponseEntity.ok().body(this.authorService.search(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok().body(this.authorService.findById(id));
    }

    @PostMapping
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_AUTHOR_CREATE"})
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody final AuthorCreateRequest request) {
        return ResponseEntity.ok()
                             .body(this.authorService.createAuthor(request));
    }

    @PutMapping("/{id}")
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_AUTHOR_EDIT"})
    public ResponseEntity<BaseResponse> update(@PathVariable final UUID id,
                                               @Valid @RequestBody final AuthorUpdateRequest request) {
        return ResponseEntity.ok()
                             .body(this.authorService.updateById(id, request));
    }

    @DeleteMapping("/{id}")
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_AUTHOR_DELETE"})
    public ResponseEntity<?> delete(@PathVariable final UUID id) {
        this.authorService.delete(id);
        return ResponseEntity.ok().body(AppMsg.FUNC_SUCCESS_MSG);
    }
}
