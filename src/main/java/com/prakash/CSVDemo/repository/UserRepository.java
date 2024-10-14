package com.prakash.CSVDemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prakash.CSVDemo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
