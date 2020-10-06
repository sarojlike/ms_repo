package com.saroj.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saroj.test.entities.User;



@Repository("userRepo")
public interface UserRepository extends JpaRepository<User, Long> {

}
