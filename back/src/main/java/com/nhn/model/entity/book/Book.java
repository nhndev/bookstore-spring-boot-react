package com.nhn.model.entity.book;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;

import com.nhn.model.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity
@DynamicUpdate
@Table(name = "bs_books")
public class Book extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -1445828871313011498L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "discount")
    private int discount;

    @Column(name = "quantity", nullable = false, columnDefinition = "int default 0")
    private int quantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "publish_year")
    private Integer publishYear;

    private String language;

    private Integer weight;

    private String size;

    @Column(name = "page_count")
    private Integer pageCount;

    private String layout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BookCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private BookPublisher publisher;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    private List<BookImage> images;
}
