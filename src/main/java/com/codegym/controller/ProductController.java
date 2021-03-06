package com.codegym.controller;

import com.codegym.exception.NotFoundException;
import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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

    @ModelAttribute("categories")
    public Iterable<Category> categories() {
        return categoryService.findAll();
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView notFoundPage() {
        return new ModelAndView("/error-404");
    }

    @GetMapping("/products/list")
    public ModelAndView showListProduct(@RequestParam (name = "q") Optional<String> q, @PageableDefault (value = 5) Pageable pageable){
        ModelAndView modelAndView = new ModelAndView("/product/list");
        Page<Product> products = productService.findAll(pageable);
        if (q.isPresent()){
            modelAndView.addObject("q", q.get());
            products = productService.findProductByNameContaining(q.get(), pageable);
        }
//        Iterable<Category> categories = categoryService.findAll();
        modelAndView.addObject("products", products);
//        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/products/create")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/product/create");
//        Iterable<Category> categories = categoryService.findAll();
//        modelAndView.addObject("categories", categories);
        modelAndView.addObject("product", new ProductForm());
        return modelAndView;
    }

    @PostMapping("/products/create")
    public ModelAndView createProduct(@Valid @ModelAttribute("product") ProductForm productForm, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()){ //Tr??? v??? d??? li???u true n???u d??? li???u ng?????i d??ng nh???p v??o kh??ng h???p l???
            return new ModelAndView("/product/create");
        }
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
    public ModelAndView showEditForm(@PathVariable Long id) throws NotFoundException {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
//            return new ModelAndView("/error-404");
            throw new NotFoundException();
        }
        ModelAndView modelAndView = new ModelAndView("/product/edit");
        modelAndView.addObject("product", productOptional.get());
//        Iterable<Category> categories = categoryService.findAll();
//        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @PostMapping("/products/edit/{id}")
    public ModelAndView editProduct(@PathVariable Long id, @ModelAttribute ProductForm productForm) throws NotFoundException {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
//            return new ModelAndView("/error-404");
            throw new NotFoundException();
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
    public ModelAndView showDeleteForm(@PathVariable Long id) throws NotFoundException {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
//            return new ModelAndView("/error-404");
            throw new NotFoundException();
        }
        ModelAndView modelAndView = new ModelAndView("/product/delete");
        modelAndView.addObject("product", productOptional.get());
//        Iterable<Category> categories = categoryService.findAll();
//        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @PostMapping("/products/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable Long id) throws NotFoundException {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
//            return new ModelAndView("/error-404");
            throw new NotFoundException();
        }
        productService.remove(id);
        return new ModelAndView("redirect:/products/list");
    }

    @GetMapping("/products/{id}")
    public ModelAndView showDetailProduct(@PathVariable Long id) throws NotFoundException {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
//            return new ModelAndView("/error-404");
            throw new NotFoundException();
        }
        ModelAndView modelAndView = new ModelAndView("/product/detail");
        modelAndView.addObject("product", productOptional.get());
//        Iterable<Category> categories = categoryService.findAll();
//        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/products/search")
    public ModelAndView showListProductBetweenPrice(@RequestParam (name = "min") Double min, @RequestParam (name = "max") Double max) {
        Iterable<Product> products = productService.searchProductBetweenPrice(min, max);
        ModelAndView modelAndView = new ModelAndView("/product/list");
        modelAndView.addObject("products", products);
        return modelAndView;
    }
}
