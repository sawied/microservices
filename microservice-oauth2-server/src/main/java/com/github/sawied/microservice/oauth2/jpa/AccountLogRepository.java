package com.github.sawied.microservice.oauth2.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.github.sawied.microservice.oauth2.jpa.entity.AccountLog;

@Repository
public interface AccountLogRepository extends AccountLogRepositoryCutom,CrudRepository<AccountLog, Integer>{

}
