package com.codegym.service.product;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.IGeneralService;

import java.util.Optional;

public interface IProductService extends IGeneralService<Product> {
    Iterable<Product> findProductByNameContaining(String name);

    Iterable<Product> findAllByCategory(Category category);
}
