package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;


@Controller
public class CategoryController {
    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/categories/list")
    public ModelAndView showListCategory(){
        ModelAndView modelAndView = new ModelAndView("/category/list");
        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/categories/create")
    public ModelAndView showCreateForm() {
        return new ModelAndView("/category/create", "category", new Category());
    }

    @PostMapping("/categories/create")
    public ModelAndView createCategory(@ModelAttribute Category category){
        ModelAndView modelAndView = new ModelAndView("redirect:/categories/list");
        categoryService.save(category);
        return modelAndView;
    }

    @GetMapping("/categories/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        return new ModelAndView("/category/edit", "category", categoryOptional.get());
    }

    @PostMapping("/categories/edit/{id}")
    public ModelAndView editCategory(@PathVariable Long id, @ModelAttribute Category category){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        Category oldCategory = categoryOptional.get();
        oldCategory.setName(category.getName());
        categoryService.save(oldCategory);
        return new ModelAndView("redirect:/categories/list");
    }

    @GetMapping("/categories/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id){
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        return new ModelAndView("/category/delete", "category", categoryOptional.get());
    }

    @PostMapping("categories/delete/{id}")
    public ModelAndView deleteCategory(@PathVariable Long id) {
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        categoryService.remove(id);
        return new ModelAndView("redirect:/categories/list");
    }

    @GetMapping("categories/view/{id}")
    public ModelAndView showAllProductByCategory(@PathVariable Long id) {
        Optional<Category> categoryOptional = categoryService.findById(id);
        if (!categoryOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        Category category = categoryOptional.get();
        Iterable<Product> products = productService.findAllByCategory(category);
        return new ModelAndView("/category/view", "products", products);
    }
}