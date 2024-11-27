package com.document.upload.controller;

import com.document.upload.entity.UsersEntity;
import com.document.upload.repository.UsersRepository;
import com.document.upload.util.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value= UrlConstants.USERS)
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;
    @GetMapping
    public List<UsersEntity> getAllUsers(){
        return usersRepository.findAll();
    }

}
