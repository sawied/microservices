package com.github.sawied.microservice.trade.jpa;

import com.github.sawied.microservice.trade.jpa.entity.ActivityEntity;
import com.github.sawied.microservice.trade.jpa.repository.ActivityRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * code from oneport chinasoft team.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;
    @Test
    public void activityCreateSuccess(){
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setName("danan");
        activityRepository.save(activityEntity);
    }

    @Test
    public void activityNativeSearch(){
        Assert.assertNotNull(activityRepository);
        activityCreateSuccess();
        PageRequest pageRequest =PageRequest.of(10,10);
        Page<ActivityEntity> page = activityRepository.queryActivityByUser("danan", pageRequest);
        Assert.assertNotNull(page);
    }
}
