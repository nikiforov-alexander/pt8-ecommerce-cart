package com.acme.ecommerce.service;

import com.acme.ecommerce.domain.Product;
import com.acme.ecommerce.exception.NotEnoughProductsException;
import com.acme.ecommerce.exception.NotFoundException;
import com.acme.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository repository;
	
	@Autowired
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

	@Transactional
	@Override
	public Iterable<Product> findAll() {
		return repository.findAll();
	}
	
	@Override
	public Page<Product> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	// Task #10-11:
    // We throw NotFoundException if product is not found on service
    // layer. Exception is handled in controller
	@Override
	public Product findById(Long id) {
		Product product = repository.findOne(id);
		if (product == null) {
			throw new NotFoundException("Product Not Found");
		} else {
            return product;
        }
	}

	// This method is created to solve Task #9:
    // Throw exceptions in the service layer for the case
    // when an product’s requested quantity exceeds the quantity in stock,
    // instead of checking the quantity in the controller.
    // It looks simple. and I feel like I could put it into
    // controller, but whatever: task is task. The one reason that I
    // come up with is mocking made easy, I guess...
    /**
     *
     * @param newQuantity
     * @param numberOfProductsInStock
     * @throws NotEnoughProductsException
     *         if newQuantity > numberOfProductsInStock
     *         otherwise does just nothing, used in
     *         @see com.acme.ecommerce.controller.CartController
     */
	@Override
	public void checkIfThereAreEnoughProductsInStock(
			int newQuantity,
			int numberOfProductsInStock) {
        if (newQuantity > numberOfProductsInStock) {
            throw new NotEnoughProductsException();
        }
	}

}
