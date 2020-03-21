package com.github.sawied.microservice.oauth2.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sawied.microservice.oauth2.jpa.entity.Group;

@Repository
public interface GroupRepository extends GroupRepositoryCustom,JpaRepository<Group, Integer>{

}
