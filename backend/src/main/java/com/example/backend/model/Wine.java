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
    private int price;
    @Column(nullable = false)
    private String producer;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal rate;

    private boolean isDeleted = false;

    public enum Types {
        RED,
        WHITE,
        PINK,
        SPARKLING,
        DESSERT,
        PORTWEIN,
        ORANGE
    }

}
