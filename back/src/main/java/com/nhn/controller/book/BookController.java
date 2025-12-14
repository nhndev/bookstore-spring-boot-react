package com.nhn.controller.book;


import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nhn.annotation.SysAuthorize;
import com.nhn.constant.SysRole;
import com.nhn.model.dto.request.book.BookCreateRequest;
import com.nhn.model.dto.request.book.BookUpdateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.service.book.BookImageService;
import com.nhn.service.book.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    private final BookImageService bookImageService;


    //    @GetMapping
    //    public ResponseEntity<?> findAllWithPagination(
    //            @RequestParam(name = "keyword", defaultValue = "") final String keyword,
    //            @RequestParam(name = "page", defaultValue = "0") final int page, //page number
    //            @RequestParam(name = "limit", defaultValue = "20") final int limit, //page size
    //            @RequestParam(name = "orderBy", defaultValue = "name") final String orderBy, //database field
    //            @RequestParam(name = "sortBy", defaultValue = "asc") final String sortBy
    //    ) {
    //        final Sort sort = Sort.by(sortBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy);
    //        final Pageable pageable = PageRequest.of(page, limit, sort);
    //
    //        return ResponseEntity.ok().body(this.productService.findAll(keyword, pageable));
    //    }

    //    @GetMapping("/filter/category")
    //    @Operation(summary = "Filter products by category list")
    //    @ApiResponses(value = {
    //            @ApiResponse(responseCode = "200", description = "Filter products by category list successfully", content = @Content(mediaType = "application/json",
    //                    schema = @Schema(implementation = ResponsePageDTO.class))),
    //
    //    })
    //    public ResponseEntity<?> filterByCategoryList(
    //            @RequestParam(name = "categoryList") final List<UUID> uuids,
    //            @RequestParam(name = "page", defaultValue = "0") final int page, //page number
    //            @RequestParam(name = "limit", defaultValue = "20") final int limit, //page size
    //            @RequestParam(name = "orderBy", defaultValue = "name") final String orderBy, //database field
    //            @RequestParam(name = "sortBy", defaultValue = "asc") final String sortBy
    //    ) {
    //        final Sort sort = Sort.by(sortBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy);
    //        final Pageable pageable = PageRequest.of(page, limit, sort);
    //
    //        return ResponseEntity.ok().body(this.productService.filterByCategoryList(uuids, pageable));
    //    }

    //    @GetMapping("/search")
    //    @Operation(summary = "Search products")
    //    @ApiResponses(value = {
    //            @ApiResponse(responseCode = "200", description = "Search products successfully", content = @Content(mediaType = "application/json",
    //                    schema = @Schema(implementation = ResponsePageDTO.class))),
    //
    //    })
    //    public ResponseEntity<?> searchProduct(
    //            @RequestParam(name = "keyword", defaultValue = "") final String keyword,
    //            @RequestParam(name = "page", defaultValue = "0") final int page, //page number
    //            @RequestParam(name = "limit", defaultValue = "20") final int limit, //page size
    //            @RequestParam(name = "orderBy", defaultValue = "name") final String orderBy, //database field
    //            @RequestParam(name = "sortBy", defaultValue = "asc") final String sortBy
    //    ) {
    //        final Sort sort = Sort.by(sortBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy);
    //        final Pageable pageable = PageRequest.of(page, limit, sort);
    //
    //        return ResponseEntity.ok().body(this.productService.searchProduct(keyword, pageable));
    //    }

    //    @GetMapping("/{id}")
    //    @Operation(summary = "Find a product")
    //    @ApiResponses(value = {
    //            @ApiResponse(responseCode = "200", description = "Find a product successfully", content = @Content(mediaType = "application/json",
    //                    schema = @Schema(implementation = BaseResponse.class))),
    //
    //    })
    //    public ResponseEntity<?> findById(@PathVariable final UUID id) throws NotFoundException {
    //
    //        return ResponseEntity.ok().body(this.productService.findById(id));
    //    }

    @PostMapping()
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_CREATE"})
    public ResponseEntity<?> create(@Valid @RequestBody final BookCreateRequest request) {
        return ResponseEntity.ok()
                             .body(this.bookService.createBook(request));
    }

    @PutMapping("/{id}")
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_EDIT"})
    public ResponseEntity<?> updateById(@PathVariable final UUID id,
                                        @Valid @RequestBody final BookUpdateRequest request) {
        return ResponseEntity.ok()
                             .body(this.bookService.updateById(id, request));
    }

    @PostMapping(value = "/image/{bookId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @SysAuthorize(role = SysRole.SUPER_ADMIN, permissions = {"BOOK_CREATE",
                                                             "BOOK_EDIT"})
    public ResponseEntity<BaseResponse> uploadImages(@PathVariable final UUID bookId,
                                                     @RequestPart final List<MultipartFile> files) {
        return ResponseEntity.ok()
                             .body(this.bookImageService.uploadImages(bookId,
                                                                      files));
    }

    //    @DeleteMapping("/{id}")
    //    @Operation(summary = "Delete a product")
    //    @ApiResponses(value = {
    //            @ApiResponse(responseCode = "200", description = "Delete a product successfully", content = @Content(mediaType = "application/json",
    //                    schema = @Schema(implementation = BaseResponse.class))),
    //            @ApiResponse(responseCode = "404", description = "Not Found")
    //
    //    })
    //    @PreAuthorize("hasAuthority('SUPER_ADMIN') || hasPermission('PRODUCT', 'PRODUCT_DELETE')")
    //    public ResponseEntity<?> delete(@PathVariable final UUID id) throws JsonProcessingException, NotFoundException {
    //        this.productService.softDeleteById(id);
    //
    //        final ObjectMapper mapper = new ObjectMapper();
    //        final JsonNode json = mapper.readTree("{\"message\":\"Delete product successfully\"}");
    //
    //        return ResponseEntity.ok().body(json);
    //    }
}
