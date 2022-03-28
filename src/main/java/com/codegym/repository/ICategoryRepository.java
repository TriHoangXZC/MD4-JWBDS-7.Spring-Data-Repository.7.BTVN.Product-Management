package com.codegym.repository;

import com.codegym.model.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ICategoryRepository extends PagingAndSortingRepository<Category, Long> {
    //Xoa category => product giu nguyen + chuyen category_id = null
    @Modifying
    @Query(value = "call delete_category(?1)", nativeQuery = true)
    void deleteCategoryByProcedure(Long category_id);

    //Xoa category => xoa product
    @Modifying
    @Query(value = "call delete_category_and_product(?1)", nativeQuery = true)
    void deleteCategoryAndProductByProcedure(Long category_id);
}
