package com.acme.ecommerce.service;

import com.acme.ecommerce.domain.Product;
import com.acme.ecommerce.exception.NotEnoughProductsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

	Iterable<Product> findAll();
	
	Page<Product> findAll(Pageable pageable);
	
	Product findById(Long id);

	void checkIfThereAreEnoughProductsInStock(
			int newQuantity, int numberOfProductsInStock
	) throws NotEnoughProductsException;
}
