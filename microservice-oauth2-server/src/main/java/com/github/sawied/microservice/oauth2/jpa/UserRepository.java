package com.github.sawied.microservice.oauth2.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.sawied.microservice.oauth2.jpa.entity.Account;
import com.github.sawied.microservice.oauth2.jpa.entity.User;

@Repository
public interface UserRepository extends UserRepositoryCustom,JpaRepository<User, Integer>{

}
