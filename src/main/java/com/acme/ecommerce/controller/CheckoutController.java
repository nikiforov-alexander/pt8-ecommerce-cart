package com.acme.ecommerce.controller;

import com.acme.ecommerce.domain.*;
import com.acme.ecommerce.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static com.acme.ecommerce.controller.WebConstants.*;


@SuppressWarnings("SpringJavaAutowiringInspection")
@Controller
@RequestMapping("/checkout")
@Scope("request")
public class CheckoutController {
	final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
	
	@Autowired 
    private TemplateEngine templateEngine;
	
	@Autowired
	private ShoppingCart sCart;
	
	@Autowired
	PurchaseService purchaseService;
	
	@RequestMapping("/coupon")
	String checkoutCoupon(Model model) {
    	Purchase purchase = sCart.getPurchase();
    	BigDecimal subTotal = new BigDecimal(0);
    	CouponCode couponCode = sCart.getCouponCode();
    	
    	model.addAttribute("purchase", purchase);
    	if (purchase != null) {
    		
    		if (couponCode == null) {
    			couponCode = new CouponCode();
    		}
    		
    		subTotal = computeSubtotal(purchase, couponCode);
    		
    		model.addAttribute("subTotal", subTotal);
            // if user typed wrong coupon code, then this wrong one will be
            // in model, so that we don't need to add one
            if (!model.containsAttribute("couponCode")) {
                model.addAttribute("couponCode", couponCode);
            }
    	} else {
    		logger.error("No purchases Found!");
    		return("redirect:/error");
    	}
		return "checkout_1";
	}

    // Bug fix: Add form validation to the coupon code field in the first
    // step of the checkout process.
    // A coupon code will be considered valid if
    // it contains between 5 and 10 characters.
    // A unit test should also be added to verify that
    // the added validation is working.
	@RequestMapping(path="/coupon", method = RequestMethod.POST)
	String postCouponCode( @ModelAttribute(value="couponCode") @Valid
                          CouponCode couponCode,
                          BindingResult result,
                          RedirectAttributes redirectAttributes
                          ) {
	    // if coupon code length is not valid
        if (result.hasErrors()) {
            // print error to logger: took from postShipping method
            logger.error("Errors on fields: " + result.getFieldErrorCount());
            // add coupon code flash attribute, so that user continue where he
            // left off
            redirectAttributes.addFlashAttribute("couponCode", couponCode);
            // add flash message to appear close to the problematic field
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.couponCode",
                    result);
            // set flash message on top? I don't think so. It is not in the
            // code
            // redirect back to checkout first stage: ("/coupon")
            return "redirect:coupon";
        }
    	sCart.setCouponCode(couponCode);
   	
		return "redirect:shipping";
	}
	
	@RequestMapping(path="/shipping", method=RequestMethod.GET)
	String checkoutShipping(Model model) {
    	Purchase purchase = sCart.getPurchase();
    	BigDecimal subTotal = new BigDecimal(0);
    	CouponCode couponCode = sCart.getCouponCode();
    	
    	model.addAttribute("purchase", purchase);
    	if (purchase != null) {
    		subTotal = computeSubtotal(purchase, couponCode);
    		
    		if (!model.containsAttribute("shippingAddress")) { // so we don't overwrite any errors...
	    		Address modelAddress;
	    		if (purchase.getShippingAddress() != null) {
	    			modelAddress = purchase.getShippingAddress();
	    		} else {
	    			modelAddress = new Address();
	    		}
	    		model.addAttribute("shippingAddress", modelAddress);
    		}
    		model.addAttribute("subTotal", subTotal);
    		model.addAttribute("LIST_COUNTRIES", LIST_COUNTRIES);
    		model.addAttribute("LIST_STATES", LIST_STATES);
    	} else {
    		logger.error("No purchases Found!");
    		return("redirect:/error");
    	}
		return "checkout_2";
	}
	
	@RequestMapping(path="/shipping", method = RequestMethod.POST)
	String postShipping(
			@ModelAttribute(value="shippingAddress") @Valid
			Address shippingAddress,
			final BindingResult result,
			RedirectAttributes redirectAttributes) {

    	if(result.hasErrors()) {
    		logger.error("Errors on fields: " + result.getFieldErrorCount());
    		redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.shippingAddress", result);
    		redirectAttributes.addFlashAttribute("shippingAddress", shippingAddress);
    		return String.format("redirect:shipping");
		} else {
	    	Purchase purchase = sCart.getPurchase();
			if (purchase != null) {
				purchase.setShippingAddress(shippingAddress);
				sCart.setPurchase(purchaseService.save(purchase));
			} else {
	    		logger.error("No purchases Found!");
	    		return("redirect:/error");
	    	}
		}
   	
		return "redirect:billing";
	}
	
	@RequestMapping("/billing")
	String checkoutBilling(Model model) {
    	Purchase purchase = sCart.getPurchase();
    	BigDecimal subTotal = new BigDecimal(0);
    	BigDecimal shippingCost = new BigDecimal(0);
    	CouponCode couponCode = sCart.getCouponCode();
    	
    	model.addAttribute("purchase", purchase);
    	if (purchase != null) {
    		
    		if (!model.containsAttribute("billingObject")) { // so we don't overwrite any errors...
	    		CombinedBilling combinedBilling = new CombinedBilling();
	    		if (purchase.getBillingAddress() != null) {
	    			combinedBilling.setBillingAddressSame(purchase.isBillingAddressSame());
	    			combinedBilling.setFirstName(purchase.getBillingAddress().getFirstName());
	    			combinedBilling.setLastName(purchase.getBillingAddress().getLastName());
	    			combinedBilling.setStreetAddress(purchase.getBillingAddress().getStreetAddress());
	    			combinedBilling.setCity(purchase.getBillingAddress().getCity());
	    			combinedBilling.setState(purchase.getBillingAddress().getState());
	    			combinedBilling.setZipCode(purchase.getBillingAddress().getZipCode());
	    		}
	    		model.addAttribute("billingObject", combinedBilling);
    		}
    		
    		subTotal = computeSubtotal(purchase, couponCode);
    		shippingCost = computeShippingCost(purchase);
    		
    		BigDecimal orderTotal = subTotal.add(shippingCost);

    		model.addAttribute("shippingAddress", purchase.getShippingAddress());

    		model.addAttribute("subTotal", subTotal);
    		model.addAttribute("shippingCost", shippingCost);
    		model.addAttribute("orderTotal", orderTotal);
    		model.addAttribute("LIST_COUNTRIES", LIST_COUNTRIES);
    		model.addAttribute("LIST_STATES", LIST_STATES);
    		model.addAttribute("LIST_MONTHS", LIST_MONTHS);
    		model.addAttribute("LIST_YEARS", LIST_YEARS);
    	} else {
    		logger.error("No purchases Found!");
    		return("redirect:/error");
    	}
		return "checkout_3";
	}
	
	@RequestMapping(path="/billing", method = RequestMethod.POST)
	String postBilling(@ModelAttribute(value="billingObject") @Valid CombinedBilling combinedBilling, 
			 final BindingResult result, RedirectAttributes redirectAttributes) {

		if(result.hasErrors()) {
    		redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.billingObject", result);
    		redirectAttributes.addFlashAttribute("billingObject", combinedBilling);
    		return String.format("redirect:billing");
		} else {
			Purchase purchase = sCart.getPurchase();
			if (purchase != null) {
				if (!combinedBilling.isBillingAddressSame()) {
					Address billingAddress = new Address();
					billingAddress.setFirstName(combinedBilling.getFirstName());
					billingAddress.setLastName(combinedBilling.getLastName());
					billingAddress.setStreetAddress(combinedBilling.getStreetAddress());
					billingAddress.setCity(combinedBilling.getCity());
					billingAddress.setState(combinedBilling.getState());
					billingAddress.setZipCode(combinedBilling.getZipCode());
					billingAddress.setPhoneNumber(combinedBilling.getPhoneNumber());
					billingAddress.setEmail(combinedBilling.getEmail());
					billingAddress.setCountry(combinedBilling.getCountry());
					purchase.setBillingAddress(billingAddress);
				} else {
					if (purchase.getBillingAddress() != null) {
						purchase.setBillingAddress(null);
					}
				}
				purchase.setCreditCardNumber(combinedBilling.getCreditCardNumber());
				purchase.setCreditCardName(combinedBilling.getCreditCardName());
				purchase.setCreditCardExpMonth(combinedBilling.getCreditCardExpMonth());
				purchase.setCreditCardExpYear(combinedBilling.getCreditCardExpYear());
				purchase.setCreditCardCVC(combinedBilling.getCreditCardCVC());
				purchase.setOrderNumber(String.valueOf(ThreadLocalRandom.current().nextInt(1000000, 9999999)));
				sCart.setPurchase(purchaseService.save(purchase));
			} else {
	    		logger.error("No purchases Found!");
	    		return("redirect:/error");
	    	}
		}
   	
		return "redirect:confirmation";
	}

	// Bug fix #3: Update the order confirmation view template to mask
	// all but the last 4 digits of the credit card number.
    // Solution: I introduced private
	// method below is doing exactly that: it makes credit card number
	// hidden. It later sends this hidden number to the model.
    // access is package-private: to test the method: see
	// CheckoutControllerTest for more

	/**
	 * package-private method converting credit card number to hidden credit
	 * card number. Tested in CheckoutControllerTest
	 * @param creditCardNumber : should be more that 4 digits and not-null
	 *                         no checks here, because this method will not
	 *                         be used with wrong credit data, it is checked
	 *                         elsewhere, when user submits credit card number
	 * @return : credit card number with all characters hidden except for
	 *         4 last ones
	 */
	String makeCreditCardNumberHidden(String creditCardNumber) {
		// get index of fourth digit from the end, e.g
		// when we had number 12345 - length = 5,
		// then index will be 5 - 4 = 1, i.e. index of number "2".
		int indexOfFourthDigitFromTheEnd =
				creditCardNumber.length() - 4;
		// make char array: fill with chars of credit card number length - 4,
		// Example: creditCardNumber 12345, char array will be
		// of length 5-1 = 4
		char[] charArrayWithHiddenNumbers =
				new char[creditCardNumber.length() - 4];
		// fill char array with *
		Arrays.fill(charArrayWithHiddenNumbers, '*');
		// return String from char array + substring of creditCardNumber
		return new String(charArrayWithHiddenNumbers) +
				creditCardNumber.substring(
                indexOfFourthDigitFromTheEnd
        		);
	}

	@RequestMapping("/confirmation")
	String checkoutConfirmation(Model model) {
    	Purchase purchase = sCart.getPurchase();
    	BigDecimal subTotal = new BigDecimal(0);
       	BigDecimal shippingCost = new BigDecimal(0);
    	CouponCode couponCode = sCart.getCouponCode();
    	
    	model.addAttribute("purchase", purchase);
    	if (purchase != null) {
    		subTotal = computeSubtotal(purchase, couponCode);
    		shippingCost = computeShippingCost(purchase);
    		BigDecimal orderTotal = subTotal.add(shippingCost);

    		model.addAttribute("subTotal", subTotal);
    		model.addAttribute("shippingCost", shippingCost);
    		model.addAttribute("orderTotal", orderTotal);
    		
    		model.addAttribute("orderNumber", purchase.getOrderNumber());
    		model.addAttribute("shippingAddress", purchase.getShippingAddress());
    		model.addAttribute("billingAddress", purchase.getBillingAddress());
			// credit card number is send hidden, to ensure privacy
			model.addAttribute("creditCard",
					makeCreditCardNumberHidden(
							purchase.getCreditCardNumber()
					)
			);
    	} else {
    		logger.error("No purchases Found!");
    		return("redirect:/error");
    	}
    	
		return "order_confirmation";
	}


	@RequestMapping(value = "/email", method = RequestMethod.GET)
	public void getFile(HttpServletResponse response) {
		// simulating an email receipt
		try {
			// Prepare the Thymeleaf evaluation context
	        final Context ctx = new Context();

	    	Purchase purchase = sCart.getPurchase();
	    	BigDecimal subTotal = new BigDecimal(0);
	       	BigDecimal shippingCost = new BigDecimal(0);
	    	CouponCode couponCode = sCart.getCouponCode();
	    	
	    	ctx.setVariable("purchase", purchase);
	    	if (purchase != null) {
	    		subTotal = computeSubtotal(purchase, couponCode);
	    		shippingCost = computeShippingCost(purchase);
	    		BigDecimal orderTotal = subTotal.add(shippingCost);

	    		ctx.setVariable("subTotal", subTotal);
	    		ctx.setVariable("shippingCost", shippingCost);
	    		ctx.setVariable("orderTotal", orderTotal);
	    		
	    		ctx.setVariable("orderNumber", purchase.getOrderNumber());
	    		ctx.setVariable("shippingAddress", purchase.getShippingAddress());
	    		ctx.setVariable("billingAddress", purchase.getBillingAddress());
	    		ctx.setVariable("creditCard", purchase.getCreditCardNumber());
	    		
	    		final String htmlContent = this.templateEngine.process("email_confirmation", ctx);
			
		    	response.setHeader("Content-Disposition", "attachment; filename=email_receipt.html");
		    	
		    	response.setContentType("text/html");
		    	
		    	InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(htmlContent.getBytes()));
		    	
		    	FileCopyUtils.copy(inputStream, response.getOutputStream());
		    	
		    	response.flushBuffer();
	    	} else {
	    		logger.error("No purchases Found!");
	    	}
	    	
	    	//Order completed, reset in case user wants to order again
	    	sCart.setCouponCode(null);
	    	sCart.setPurchase(null);
	    } catch (IOException ex) {
	      logger.info("Error writing file to output stream. Filename was '{}'", "email_receipt.html", ex);
	      throw new RuntimeException("IOError writing file to output stream");
	    }
	}
	
	private BigDecimal computeSubtotal(Purchase purchase, CouponCode couponCode) {
		
		BigDecimal subTotal = new BigDecimal(0);
		
		for (ProductPurchase pp : purchase.getProductPurchases()) {
			logger.debug("cart has " + pp.getQuantity() + " of " + pp.getProduct().getName() + " at " + "$" + pp.getProduct().getPrice());
			subTotal = subTotal.add(pp.getProduct().getPrice().multiply(new BigDecimal(pp.getQuantity())));
		}
		
		if (couponCode.getCode() != null && !couponCode.getCode().isEmpty()) {
			logger.info("Applying discount for coupon");
			subTotal = subTotal.multiply(new BigDecimal(0.9));
		}
		
		return subTotal;
	}
	
	private BigDecimal computeShippingCost(Purchase purchase) {
		BigDecimal shippingCost = new BigDecimal(0);
		
		for (ProductPurchase pp : purchase.getProductPurchases()) {
			BigDecimal itemShippingCost = new BigDecimal(0).add(COST_PER_ITEM.multiply(new BigDecimal(pp.getQuantity())));
			logger.debug("cart has " + pp.getQuantity() + " of " + pp.getProduct().getName() + " shipping cost of " + itemShippingCost);
			shippingCost = shippingCost.add(itemShippingCost);
		}
		
		return shippingCost;
	}
}
