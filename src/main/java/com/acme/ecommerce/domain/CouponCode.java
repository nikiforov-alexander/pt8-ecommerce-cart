package com.acme.ecommerce.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;

@Component
@Scope("session")
public class CouponCode {

    @Size(min = 5, max = 10, message = "Coupon code should be between 5 and 10 characters")
	private String code;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
