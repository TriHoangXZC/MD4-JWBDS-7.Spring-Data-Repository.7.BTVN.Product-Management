package com.codegym.service.category;

import com.codegym.model.Category;
import com.codegym.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService extends IGeneralService<Category> {
    Page<Category> findAll(Pageable pageable);

    void deleteCategoryByProcedure(Long category_id);

    void deleteCategoryAndProductByProcedure(Long category_id);
}
