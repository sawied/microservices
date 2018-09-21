package com.github.sawied.microservice.oauth2.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.sawied.microservice.oauth2.jpa.entity.Account;

@Repository
public interface AccountRepository extends AccountRepositoryCustom,CrudRepository<Account, Integer>{

}
