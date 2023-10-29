package com.example.gamefusion.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "status")
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;
}
