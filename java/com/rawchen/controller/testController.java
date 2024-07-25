package com.rawchen.controller;

import com.rawchen.domain.Content;
import com.rawchen.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class testController extends BaseController
{
    @RequestMapping("/test")
    public String test(Model model)
    {
        List<Content> content = contentService.selectContentWithCategory(2);
        System.out.println(content.size());

        model.addAttribute("content", content);

        return "test";
    }

    @Autowired
    private ContentService contentService;

    @PostMapping("/updateContent")
    @ResponseBody
    public String updateContent(@RequestBody Content content) {
        int rowsAffected = contentService.updateContent(content);
        if (rowsAffected > 0) {
            return "更新成功";
        } else {
            return "更新失败";
        }
    }

}
