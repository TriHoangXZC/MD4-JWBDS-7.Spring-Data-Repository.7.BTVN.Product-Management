package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
public class ProductController {
    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Value("${file-upload}")
    private String uploadPath;

    @GetMapping("/products/list")
    public ModelAndView showListProduct(@RequestParam (name = "q") Optional<String> q){
        ModelAndView modelAndView = new ModelAndView("/product/list");
        Iterable<Product> products = productService.findAll();
        if (q.isPresent()){
            products = productService.findProductByNameContaining(q.get());
        }
        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("products", products);
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/products/create")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/product/create");
        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("product", new ProductForm());
        return modelAndView;
    }

    @PostMapping("/products/create")
    public ModelAndView createProduct(@ModelAttribute ProductForm productForm){
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        long currentTime = System.currentTimeMillis();
        fileName = currentTime + fileName;
        try {
            FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), productForm.getDescription(), fileName, productForm.getCategory());
        productService.save(product);
        return new ModelAndView("redirect:/products/list");
    }

    @GetMapping("/products/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        ModelAndView modelAndView = new ModelAndView("/product/edit");
        modelAndView.addObject("product", productOptional.get());
        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @PostMapping("/products/edit/{id}")
    public ModelAndView editProduct(@PathVariable Long id, @ModelAttribute ProductForm productForm){
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        Product oldProduct = productOptional.get();
        MultipartFile multipartFile = productForm.getImage();
        if (multipartFile.getSize() != 0) {
            String fileName = multipartFile.getOriginalFilename();
            long currentTime = System.currentTimeMillis();
            fileName = currentTime + fileName;
            oldProduct.setImage(fileName);
            try {
                FileCopyUtils.copy(multipartFile.getBytes(), new File(uploadPath + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        oldProduct.setName(productForm.getName());
        oldProduct.setPrice(productForm.getPrice());
        oldProduct.setDescription(productForm.getDescription());
        oldProduct.setCategory(productForm.getCategory());
        productService.save(oldProduct);
        return new ModelAndView("redirect:/products/list");
    }

    @GetMapping("/products/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        ModelAndView modelAndView = new ModelAndView("/product/delete");
        modelAndView.addObject("product", productOptional.get());
        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @PostMapping("/products/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return new ModelAndView("/error-404");
        }
        productService.remove(id);
        return new ModelAndView("redirect:/products/list");
    }

//    @GetMapping("/products/{id}")
//    public ModelAndView showDetailProduct(@PathVariable Long id) {
//        Optional<Product> productOptional = productService.findById(id);
//        if (!productOptional.isPresent()) {
//            return new ModelAndView("/error-404");
//        }
//        ModelAndView modelAndView = new ModelAndView("/product/view");
//        modelAndView.addObject("product", productOptional.get());
//        Iterable<Category> categories = categoryService.findAll();
//        modelAndView.addObject("categories", categories);
//        return new ModelAndView();
//    }
}
