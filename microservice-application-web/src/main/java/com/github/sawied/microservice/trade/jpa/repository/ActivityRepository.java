package com.github.sawied.microservice.trade.jpa.repository;

import com.github.sawied.microservice.trade.jpa.entity.ActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * code from oneport chinasoft team.
 */

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity,Integer> ,ActivityRepositoryCustom {

    @Query(value="select ae from ActivityEntity ae left join fetch ae.caseEntity ce where ae.name=:name",
    countQuery = "select count(1) from ActivityEntity ae where ae.name=:name")
    Page<ActivityEntity> queryActivityByUser(@Param("name") String name, Pageable pageable);

}
