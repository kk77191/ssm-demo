package com.lab.cms.controller;
import com.lab.cms.service.CmsArticleService;
import com.lab.cms.service.CmsCategoryService;
import com.lab.cms.service.CmsTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @Autowired
    private CmsCategoryService categoryService;
    @Autowired
    private CmsArticleService articleService;
    @Autowired
    private CmsTagService tagService;

    @GetMapping("/category")
    public String category(Model model){
        model.addAttribute("categories", categoryService.list());
        return "/category.jsp";
    }

    @GetMapping("/article")
    public String article(Model model){
        model.addAttribute("articles", articleService.list());
        model.addAttribute("categories", categoryService.list());
        return "/article.jsp";
    }

    @GetMapping("/tag")
    public String tag(Model model){
        model.addAttribute("tags", tagService.list());
        return "/tag.jsp";
    }
}