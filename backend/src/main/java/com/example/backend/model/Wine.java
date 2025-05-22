package com.example.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Table(name = "wines")
@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE wines SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int year;
    @Enumerated(EnumType.STRING)
    private Types type;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private String producer;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String ownerDescription;
    @Column(nullable = false)
    private BigDecimal rate;
    @Column(nullable = false)
    private String agingMethod;
    @Column(nullable = false)
    private String sweetness;
    @Column(nullable = false)
    private String region;
    @Column(nullable = false)
    private String variety;
    @Column(nullable = false)
    private BigDecimal percentage;
    @Column(nullable = false)
    private LocalDateTime dateAdded;
    @Column(name="image_url",
            nullable=false,
            insertable=false,
            columnDefinition="varchar(255) default ''")
    private String imageUrl;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
