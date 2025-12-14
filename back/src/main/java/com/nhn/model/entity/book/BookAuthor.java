package com.nhn.model.entity.book;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;

import com.nhn.model.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bs_author_book")
@DynamicUpdate
@IdClass(BookAuthor.PrimaryKeys.class)
public class BookAuthor extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 7942221135382414829L;

    @Data
    public static class PrimaryKeys implements Serializable {
        @Serial
        private static final long serialVersionUID = -7445330363399470029L;

        private UUID authorId;

        private UUID bookId;
    }

    @Id
    @Column(name = "author_id", nullable = false, length = 36)
    private UUID authorId;

    @Id
    @Column(name = "book_id", nullable = false, length = 36)
    private UUID bookId;

    @Column(name = "is_main", nullable = false, columnDefinition = "boolean default false")
    private Boolean isMain;
}
