package com.starbank.recommendation_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Убедитесь, что таблица в H2 называется 'users'
public class UserH2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // H2 может генерировать ID
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // Ссылка на ID пользователя в основной системе (из транзакций)
    @Column(name = "user_id", nullable = false) // Используем UUID как строку или как UUID
    private String userId; // Можно хранить как String или UUID

    // Конструкторы
    public UserH2() {}

    public UserH2(String username, String firstName, String lastName, String userId) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}