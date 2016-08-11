package com.acme.ecommerce.controller;

import com.acme.ecommerce.Application;
import com.acme.ecommerce.config.PersistenceConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, PersistenceConfig.class})
@WebIntegrationTest
public class TestProductControllerIT {

	@Autowired
	WebApplicationContext context;

	private static final String PRODUCT_ID = "1";
	private static final String PRODUCT_NAME = "Corkscrew";
	private static final String PRODUCT_DESC = "A screw for corks";
	private static final BigDecimal PRODUCT_PRICE = new BigDecimal(189.79);
	private static final Integer ORDER_QUANTITY = 3;

	WebClient webClient;

	// here we set server.port property to 8090, so that just in case we
	// have our app running on port 8080, this test won't fail
	static {
		System.setProperty("properties.home", "properties");
		System.setProperty("server.port", "8090");
	}

	@Before
	public void setUp() throws Exception {
		webClient = MockMvcWebClientBuilder
				.webAppContextSetup(context)
				.build();
	}

	@After
	public void cleanup() {
		this.webClient.close();
	}

	// Task #4: Enhancement:
	// Add the cart subtotal to the page header,
	// as part of the “View Cart” link.
	// The subtotal should display only if the cart is not empty,
	// and the number should be formatted with a dollar sign,
	// as well as a comma for the thousands separator.
	// Add a unit test to verify that the rendered view contains the subtotal.
	@Test
	public void ProductDetailAddItemIntegrationTest() throws Exception {
		// I changed here a bit, so that we use another port defined in
		// setUp above, and do not interfere with possible running app
		HtmlPage productPage = webClient.getPage("http://localhost:" +
				System.getProperty("server.port") +
				"/product/detail/" + PRODUCT_ID);
		String productName = productPage.getHtmlElementById("productName").getTextContent();
		String productPrice = productPage.getHtmlElementById("productPrice").getTextContent();
		String productDesc = productPage.getHtmlElementById("productDescription").getTextContent();
		assertThat(productName).isEqualTo(PRODUCT_NAME);
		assertThat(productPrice).isEqualTo("$" + new DecimalFormat("#0.##").format(PRODUCT_PRICE));
		assertThat(productDesc).isEqualTo(PRODUCT_DESC);

		HtmlForm form = productPage.getFormByName("detailCartForm");
		HtmlTextInput quantityInput = form.getInputByName("quantity");
		HtmlAnchor formAnchor = productPage.getAnchorByName("detailButton");
		quantityInput.setValueAttribute(ORDER_QUANTITY.toString());
		HtmlPage productListPage = formAnchor.click();

		assertThat(productListPage.getUrl().toString()).endsWith("/product/");
		HtmlAnchor cartAnchor = productListPage.getAnchorByName("cartButton");
		HtmlPage cartPage = cartAnchor.click();

		assertThat(cartPage.getUrl().toString()).endsWith("/cart");

		String productCartName = cartPage.getAnchorByName("productName" + PRODUCT_ID).getTextContent();
		String productCartPrice = cartPage.getHtmlElementById("productPrice" + PRODUCT_ID).getTextContent();
		String productCartSubtotal = cartPage.getHtmlElementById("subtotal").getTextContent();
		// getting product cart subtotal in header
		String productCartSubtotalInHeader =
				cartPage.getHtmlElementById("subtotal-in-header")
						.getTextContent();

		assertThat(productCartName).isEqualTo(PRODUCT_NAME);
		assertThat(productCartPrice).isEqualTo("$" + new DecimalFormat("#0.##").format(PRODUCT_PRICE));
		assertThat(productCartSubtotal).isEqualTo("$" + new DecimalFormat("#0.##").format(PRODUCT_PRICE.multiply(new BigDecimal(ORDER_QUANTITY))));
		// asserting that product cart subtotal in header is equal to
		// product cart subtotal above
		assertThat(productCartSubtotal).isEqualTo(productCartSubtotalInHeader);

	}
}
