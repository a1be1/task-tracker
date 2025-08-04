package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.infrastructure.UserHashMapRepository;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreateUserServiceTest {

    @Autowired
    CreateUserService service;


    @Test
    void test() {
        //prepare service

        //prepare other stuff
        CreateUserApiRequest request = CreateUserApiRequest.builder().build();
        service.createUser(request);
        //...
    }
}