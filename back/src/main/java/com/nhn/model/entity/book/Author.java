package com.nhn.model.entity.book;

import java.io.Serial;
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
@DynamicUpdate
@Table(name = "bs_authors")
public class Author extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 7972297539983020271L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;
}
