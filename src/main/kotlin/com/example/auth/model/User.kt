package com.example.auth.model

import jakarta.persistence.*


@Entity
@Table(name = "user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name", unique = true)
    val name: String,
    @Column(name = "password")
    val password: String,
)
