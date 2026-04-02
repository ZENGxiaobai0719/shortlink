package com.bian.shortlink.admin.controller;

import com.bian.shortlink.admin.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class GroupController {
    private GroupService groupService;
}
