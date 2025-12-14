package com.nhn.controller.book;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nhn.annotation.SysAuthorize;
import com.nhn.constant.AppMsg;
import com.nhn.constant.SysRole;
import com.nhn.model.dto.request.book.category.BookCategoryCreateRequest;
import com.nhn.model.dto.request.book.category.BookCategorySearchRequest;
import com.nhn.model.dto.request.book.category.BookCategoryUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.service.book.BookCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/book-categories")
@RequiredArgsConstructor
public class BookCategoryController {
    private final BookCategoryService bookCategoryService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@Valid final BookCategorySearchRequest request) {
        return ResponseEntity.ok()
                             .body(this.bookCategoryService.search(request));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok().body(this.bookCategoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok().body(this.bookCategoryService.findById(id));
    }

    @PostMapping
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_CATEGORY_CREATE"})
    public ResponseEntity<BaseResponse> create(@Valid @RequestPart("data") final BookCategoryCreateRequest request,
                                               @RequestPart(required = false, name = "file") final MultipartFile file) {
        return ResponseEntity.ok()
                             .body(this.bookCategoryService.createCategory(request,
                                                                           file));
    }

    @PutMapping("/{id}")
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_CATEGORY_EDIT"})
    public ResponseEntity<BaseResponse> update(@PathVariable final UUID id,
                                               @Valid @RequestPart("data") final BookCategoryUpdateRequest request,
                                               @RequestPart(required = false, name = "file") final MultipartFile file) {
        return ResponseEntity.ok()
                             .body(this.bookCategoryService.updateById(id,
                                                                       request,
                                                                       file));
    }

    @DeleteMapping("/{id}")
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_CATEGORY_DELETE"})
    public ResponseEntity<?> delete(@PathVariable final UUID id) {
        this.bookCategoryService.delete(id);
        return ResponseEntity.ok().body(AppMsg.FUNC_SUCCESS_MSG);
    }
}
