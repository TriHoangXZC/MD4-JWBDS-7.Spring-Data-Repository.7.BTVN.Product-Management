package com.codegym.service.product;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IProductService extends IGeneralService<Product> {
    Page<Product> findProductByNameContaining(String name, Pageable pageable);

    Page<Product> findAllByCategory(Category category, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    Iterable<Product> searchProductBetweenPrice(Double min, Double max);
}
