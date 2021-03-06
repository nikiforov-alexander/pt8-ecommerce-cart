package com.acme.ecommerce.controller;

import com.acme.ecommerce.domain.Product;
import com.acme.ecommerce.domain.ProductPurchase;
import com.acme.ecommerce.exception.NotFoundException;
import com.acme.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
@RequestMapping("/product")
@ComponentScan("com.acme.ecommerce")
public class ProductController {
	
	final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	private static final int INITIAL_PAGE = 0;
	private static final int PAGE_SIZE = 5;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	HttpSession session;
	
	@Value("${imagePath:/images/}")
	String imagePath;
	
    @RequestMapping("/")
    public String index(Model model, @RequestParam(value = "page", required = false) Integer page) {
    	logger.debug("Getting Product List");
    	logger.debug("Session ID = " + session.getId());
    	
		// Evaluate page. If requested parameter is null or less than 0 (to
		// prevent exception), return initial size. Otherwise, return value of
		// param. decreased by 1.
		int evalPage = (page == null || page < 1) ? INITIAL_PAGE : page - 1;
    	
    	Page<Product> products = productService.findAll(new PageRequest(evalPage, PAGE_SIZE));
    	
		model.addAttribute("products", products);

        return "index";
    }

	// Task #7:
	// Enhancement:
	// Detect when a product’s detail view is requested,
	// but the id requested isn’t found in the database.
	// The rendered view should display a message saying
	// that the product wasn’t found.
	// I throw an NotFoundException, handled in @ExceptionHandler
	// exceptionHandler method at the bottom
    // Also Task #10,11
    @RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
    public String productDetail(@PathVariable long id, Model model) {
    	logger.debug("Details for Product " + id);
        // if product not found, exception is thrown
    	Product returnProduct = productService.findById(id);

        model.addAttribute("product", returnProduct);
        ProductPurchase productPurchase = new ProductPurchase();
        productPurchase.setProduct(returnProduct);
        productPurchase.setQuantity(1);
        model.addAttribute("productPurchase", productPurchase);

        return "product_detail";
    }
    
    @RequestMapping(path="/{id}/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> productImage(@PathVariable long id) throws FileNotFoundException {
    	MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
    	
    	logger.debug("Product Image Request for " + id);
    	logger.info("Using imagePath [" + imagePath + "]");
    	
    	Product returnProduct = productService.findById(id);
    	String imageFilePath = null;
    	if (returnProduct != null) {
    		if (!imagePath.endsWith("/")) {
    			imagePath = imagePath + "/";
    		}
    		imageFilePath = imagePath + returnProduct.getFullImageName();
    	} 
    	File imageFile = new File(imageFilePath);
    	
    	return ResponseEntity.ok()
                .contentLength(imageFile.length())
                .contentType(MediaType.parseMediaType(mimeTypesMap.getContentType(imageFile)))
                .body(new InputStreamResource(new FileInputStream(imageFile)));
    }
    
    @RequestMapping(path = "/about")
    public String aboutCartShop() {
    	logger.warn("Happy Easter! Someone actually clicked on About.");
    	return("about");
    }

    // Task #7: Here NotFoundException is handled, "error.html" is rendered
	// and "exception" is added to model.
	// Task #10, #11: response status is set to 404 not found
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public String exceptionHandler(Model model, Exception exception){
    	model.addAttribute("exception", exception);
		return "/error";
	}
}
