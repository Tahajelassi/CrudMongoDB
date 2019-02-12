package com.solution.app.service;

import com.solution.app.dao.CategoryRepository;
import com.solution.app.dao.ProductRepository;
import com.solution.app.models.Category;
import com.solution.app.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void deleteCategory(String id) {
        if (this.categoryRepository.findById(id).isPresent()) {
            Category c = this.categoryRepository.findById(id).get();
            for (Product p : this.productRepository.findAll()) {
                if (p.getCategory().equals(c)) {
                    p.setCategory(null);
                }
            }
        }
        this.categoryRepository.deleteById(id);
    }
}
