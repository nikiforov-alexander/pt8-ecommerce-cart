package com.acme.ecommerce.controller;

import com.acme.ecommerce.domain.Product;
import com.acme.ecommerce.domain.ProductPurchase;
import com.acme.ecommerce.domain.Purchase;
import com.acme.ecommerce.domain.ShoppingCart;
import com.acme.ecommerce.exception.NotEnoughProductsException;
import com.acme.ecommerce.service.ProductService;
import com.acme.ecommerce.service.PurchaseService;
import com.acme.ecommerce.web.FlashMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

import static com.acme.ecommerce.web.FlashMessage.Status.FAILURE;

@Controller
@RequestMapping("/cart")
@Scope("request")
@ComponentScan("com.teamtreehouse.service")
public class CartController {
	final Logger logger = LoggerFactory.getLogger(CartController.class);
	
	@Autowired
	PurchaseService purchaseService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ShoppingCart sCart;
	
	@Autowired
	private HttpSession session;
	
    @RequestMapping("")
    public String viewCart(Model model) {
    	logger.debug("Getting Product List");
    	logger.debug("Session ID = " + session.getId());
    	
    	Purchase purchase = sCart.getPurchase();
    	BigDecimal subTotal = new BigDecimal(0);
    	
    	model.addAttribute("purchase", purchase);
    	if (purchase != null) {
    		for (ProductPurchase pp : purchase.getProductPurchases()) {
    			logger.debug("cart has " + pp.getQuantity() + " of " + pp.getProduct().getName());
    			subTotal = subTotal.add(pp.getProduct().getPrice().multiply(new BigDecimal(pp.getQuantity())));
    		}
    		
    		model.addAttribute("subTotal", subTotal);
    	} else {
    		logger.error("No purchases Found for session ID=" + session.getId());
    		return "redirect:/error";
    	}
        return "cart";
    }

	//   Bug fix #2:
	//   Ensure that enough products are in stock before adding
	//   to the shopping cart.
	//   Whether adding products to the cart from product detail pages
	// 	 or updating an product’s quantity from the cart view,
	//   more products than are in stock can be added to the cart.
	//   Fix this issue and add a unit test to cover this scenario.
	//   This is exactly the place to fix stuff ?
	//   Task #6: Enhancement.
	//   adding successful flash message in case of successful add
    //
    // Task #9:
    // Throw exceptions in the service layer for the case
    // when an product’s requested quantity exceeds
    // the quantity in stock,
    // instead of checking the quantity in the controller.
    @RequestMapping(path="/add", method = RequestMethod.POST)
    public RedirectView addToCart(
    		@ModelAttribute(value="productId") long productId,
			@ModelAttribute(value="quantity") int quantity,
			RedirectAttributes redirectAttributes) {
    	boolean productAlreadyInCart = false;
    	RedirectView redirect = new RedirectView("/product/");
		redirect.setExposeModelAttributes(false);
    	
    	Product addProduct = productService.findById(productId);

		if (addProduct != null) {
	    	logger.debug("Adding Product: " + addProduct.getId());

			// Before we get into cycle of purchase, lets check whether
			// quantity
			// specified is less than we have in database
			// here we set number of products available
            productService.checkIfThereAreEnoughProductsInStock(
                    quantity,
                    addProduct.getQuantity()
            );

    		Purchase purchase = sCart.getPurchase();
    		if (purchase == null) {
    			purchase = new Purchase();
    			sCart.setPurchase(purchase);
    		} else {
    			for (ProductPurchase pp : purchase.getProductPurchases()) {
    				if (pp.getProduct() != null) {
    					if (pp.getProduct().getId().equals(productId)) {
							// here we check situation when quantity user
							// specified
							// was less than in db (like 1), but when added with
							// already existing amount it becomes > than in db.
							// Example: 2 Items in database: 1 is quantity user
							// 	 added, 2 items were already in his cart, 2+1>3
							// 	 ->	 failure
                            productService.checkIfThereAreEnoughProductsInStock(
                                    pp.getQuantity() + quantity,
                                    addProduct.getQuantity()
                            );
							pp.setQuantity(pp.getQuantity() + quantity);
							productAlreadyInCart = true;
    						break;
    					}
    				}
    			}
    		}
    		if (!productAlreadyInCart) {
    			ProductPurchase newProductPurchase = new ProductPurchase();
    			newProductPurchase.setProduct(addProduct);
    			newProductPurchase.setQuantity(quantity);
    			newProductPurchase.setPurchase(purchase);
        		purchase.getProductPurchases().add(newProductPurchase);
    		}
    		logger.debug("Added " + quantity + " of " + addProduct.getName() + " to cart");
    		sCart.setPurchase(purchaseService.save(purchase));
			// add successful flash message when product is added
			redirectAttributes.addFlashAttribute("flash",
					new FlashMessage(
							"'" + addProduct.getName() +
							"' is successfully added to cart!",
							FlashMessage.Status.SUCCESS
					));
		} else {
			logger.error("Attempt to add unknown product: " + productId);
			redirect.setUrl("/error");
		}

    	return redirect;
    }

	//   Task #6: Enhancement.
	//   adding successful flash message in case of successful update
    @RequestMapping(path="/update", method = RequestMethod.POST)
    public RedirectView updateCart(
    		@ModelAttribute(value="productId") long productId,
			@ModelAttribute(value="newQuantity") int newQuantity,
			RedirectAttributes redirectAttributes) {
    	logger.debug("Updating Product: " + productId + " with Quantity: " + newQuantity);
		RedirectView redirect = new RedirectView("/cart");
		redirect.setExposeModelAttributes(false);
    	
    	Product updateProduct = productService.findById(productId);
    	if (updateProduct != null) {
    		Purchase purchase = sCart.getPurchase();
            // Before we get into cycle of purchase, lets check whether
            // quantity
            // specified is less than we have in database
			// the method is very simple, but task is task:
            // we check if there are enough products on service layer
            // Task #9:
            // Throw exceptions in the service layer for the case
            // when an product’s requested quantity exceeds
            // the quantity in stock,
            // instead of checking the quantity in the controller.
            productService.checkIfThereAreEnoughProductsInStock(
                    newQuantity,
                    updateProduct.getQuantity()
            );
    		if (purchase == null) {
    			logger.error("Unable to find shopping cart for update");
    			redirect.setUrl("/error");
    		} else {
    			for (ProductPurchase pp : purchase.getProductPurchases()) {
    				if (pp.getProduct() != null) {
    					if (pp.getProduct().getId().equals(productId)) {
    						if (newQuantity > 0) {
    							pp.setQuantity(newQuantity);
    							logger.debug("Updated " + updateProduct.getName() + " to " + newQuantity);
    						} else {
    							purchase.getProductPurchases().remove(pp);
    							logger.debug("Removed " + updateProduct.getName() + " because quantity was set to " + newQuantity);
    						}
    						break;
    					}
    				}
    			}
    		}
    		sCart.setPurchase(purchaseService.save(purchase));
			// add successful flash message when product quantity is updated
			redirectAttributes.addFlashAttribute("flash",
					new FlashMessage(
							"Product quantity is updated to '"
									+ newQuantity +
									"' items ",
							FlashMessage.Status.SUCCESS
					));
    	} else {
    		logger.error("Attempt to update on non-existent product");
    		redirect.setUrl("/error");
    	}
    	
    	return redirect;
    }

	//   Task #6: Enhancement.
	//   adding successful flash message in case of successful remove
    @RequestMapping(path="/remove", method = RequestMethod.POST)
    public RedirectView removeFromCart(
    		@ModelAttribute(value="productId") long productId,
			RedirectAttributes redirectAttributes) {
    	logger.debug("Removing Product: " + productId);
		RedirectView redirect = new RedirectView("/cart");
		redirect.setExposeModelAttributes(false);
    	
    	Product updateProduct = productService.findById(productId);
    	if (updateProduct != null) {
    		Purchase purchase = sCart.getPurchase();
    		if (purchase != null) {
    			for (ProductPurchase pp : purchase.getProductPurchases()) {
    				if (pp.getProduct() != null) {
    					if (pp.getProduct().getId().equals(productId)) {
    						purchase.getProductPurchases().remove(pp);
   							logger.debug("Removed " + updateProduct.getName());
    						break;
    					}
    				}
    			}
    			purchase = purchaseService.save(purchase);
    			sCart.setPurchase(purchase);
				// add successful flash message when product is deleted
				redirectAttributes.addFlashAttribute("flash",
						new FlashMessage(
								"'" + updateProduct.getName() +
										"' is successfully removed from cart!",
								FlashMessage.Status.SUCCESS
						));
    			if (purchase.getProductPurchases().isEmpty()) {
        	    	//if last item in cart redirect to product else return cart
        			redirect.setUrl("/product/");
        		}
    		} else {
    			logger.error("Unable to find shopping cart for update");
    			redirect.setUrl("/error");
    		}
    	} else {
    		logger.error("Attempt to update on non-existent product");
    		redirect.setUrl("/error");
    	}

    	return redirect;
    }

	//   Task #6: Enhancement.
	//   adding successful flash message in case of successful emptying
    @RequestMapping(path="/empty", method = RequestMethod.POST)
    public RedirectView emptyCart(RedirectAttributes redirectAttributes) {
    	RedirectView redirect = new RedirectView("/product/");
		redirect.setExposeModelAttributes(false);
    	
    	logger.debug("Emptying Cart");
    	Purchase purchase = sCart.getPurchase();
		if (purchase != null) {
			purchase.getProductPurchases().clear();
			sCart.setPurchase(purchaseService.save(purchase));
			// add successful flash message when cart is emptied
			redirectAttributes.addFlashAttribute("flash",
					new FlashMessage(
							"Your cart was successfully emptied!",
							FlashMessage.Status.SUCCESS
					));
		} else {
			logger.error("Unable to find shopping cart for update");
			redirect.setUrl("/error");
		}
		
    	return redirect;
    }

    // Exception handler for not enough products
    // this code is taken from spring unit test weather app.
    // I will dig later to figure out how to use
    // ReferrerInterceptor class. For now I specify manually
    // redirect to httpServletRequest.getHeader("referer")
    // which is page from which POST request was made
    @ExceptionHandler(NotEnoughProductsException.class)
    public String notEnoughProductsRedirect(
            Exception exception,
            HttpServletRequest httpServletRequest) {
        // this part is not so understandable to me, why do we have to use
        // flash map. Anyway in order to see flash message on the page
        // where request was made, we put flash message to "flash" attribute
        // of this flash map
        FlashMap flashMap =
                RequestContextUtils.getOutputFlashMap(httpServletRequest);
        if(flashMap != null) {
            flashMap.put("flash",
                    new FlashMessage(exception.getMessage(), FAILURE));
        }
        // here we redirect to page from where request was made
        return "redirect:" + httpServletRequest.getHeader("referer");
    }

}
