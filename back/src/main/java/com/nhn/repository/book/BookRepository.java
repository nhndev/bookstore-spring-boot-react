package com.nhn.repository.book;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.book.Book;


@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
//    @Query(value = """
//        SELECT p FROM Product p
//        INNER JOIN ProductCategory as pc on p.productCategory.id = pc.id
//        WHERE (
//        lower(unaccent(p.name)) LIKE  lower(concat('%', unaccent(:keyword), '%')) OR
//        lower(unaccent(pc.name)) LIKE  lower(concat('%', unaccent(:keyword), '%'))
//        )
//        """)
//    Page<Book> findAll(String keyword, Pageable pageable);
//
//    @Query(value = """
//        SELECT p FROM Product p
//        WHERE p.productCategory.id in (?1)
//        """)
//    Page<Book> filterByCategoryList(List<UUID> uuids, Pageable pageable);
//
//    @Query(value = """
//        SELECT p FROM Product p
//        INNER JOIN ProductCategory as pc on p.productCategory.id = pc.id
//        INNER JOIN ProductBrand as pb on p.productBrand.id = pb.id
//        WHERE (
//        lower(unaccent(p.name)) LIKE  lower(concat('%', unaccent(:keyword), '%')) OR
//        lower(unaccent(pc.name)) LIKE  lower(concat('%', unaccent(:keyword), '%')) OR
//        lower(unaccent(pb.name)) LIKE  lower(concat('%', unaccent(:keyword), '%'))
//        )
//        """)
//    Page<Book> searchProduct(String keyword, Pageable pageable);
//
//    boolean existsBySlug(String slug);

    Optional<Book> findBySlug(String slug);
}
