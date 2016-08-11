package com.acme.ecommerce.controller;

import com.acme.ecommerce.Application;
import com.acme.ecommerce.domain.Product;
import com.acme.ecommerce.domain.ProductPurchase;
import com.acme.ecommerce.domain.Purchase;
import com.acme.ecommerce.domain.ShoppingCart;
import com.acme.ecommerce.service.ProductService;
import com.acme.ecommerce.service.PurchaseService;
import com.acme.ecommerce.web.FlashMessage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CartControllerTest {

	final String BASE_URL = "http://localhost:8080/";

	@Mock
	private MockHttpSession session;

	@Mock
	private ProductService productService;
	@Mock
	private PurchaseService purchaseService;
	@Mock
	private ShoppingCart sCart;
	@InjectMocks
	private CartController cartController;

	private MockMvc mockMvc;

	static {
		System.setProperty("properties.home", "properties");
	}

	@Before
	public void setup() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/");
		viewResolver.setSuffix(".html");

		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(cartController).setViewResolvers(viewResolver).build();
	}

	@Test
	public void viewCartTest() throws Exception {
		Product product = productBuilder();

		when(productService.findById(1L)).thenReturn(product);

		Purchase purchase = purchaseBuilder(product);

		when(sCart.getPurchase()).thenReturn(purchase);
		mockMvc.perform(MockMvcRequestBuilders.get("/cart")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("cart"));
	}

	@Test
	public void viewCartNoPurchasesTest() throws Exception {

		when(sCart.getPurchase()).thenReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.get("/cart")).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/error"));
	}

	// Task #5: Enhancement
	// In this successful test we added check that flash message with
	// was indeed sent with redirect attributes
	@Test
	public void addToCartTest() throws Exception {
		// Arrange Mock Behaviour
		// make product
		Product product = productBuilder();
		// when product service.findById will be called in controller,
		// we return product arranged above
		when(productService.findById(1L)).thenReturn(product);

		// Act and Assert:
		// When POST request to add new item to card is made with
		// valid parameters, Then:
		// - status should be of 3xx - redirect
		// - redirected URL should be "/product"
		// - flash message has to be found in flash attributes
		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/cart/add")
						.param("quantity", "1")
						.param("productId", "1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/product/"))
				.andExpect(
						flash().attribute(
							"flash",
							Matchers.hasProperty(
								"status",
								Matchers.equalTo(FlashMessage.Status.SUCCESS)
							)
						)
				);
	}
	// Bug fix: Ensure that enough products are in stock before adding to
    // the shopping cart.
    // Whether adding products to the cart from product detail pages or
    // updating an product’s quantity from the cart view,
    // more products than are in stock can be added to the cart.
    // Fix this issue and add a unit test to cover this scenario.

    // This test checks up if POST request adding more items that we
    // have in db of this type was done
	@Test
	public void addToCartPostRequestWithQuantityMoreThanInDbFails()
            throws Exception {
	    // Arrange product: first product with three items
		Product product = productBuilder();
        // Arrange product return by service: when service will be called
        // in controller: 1-st product will be returned
		when(productService.findById(1L)).thenReturn(product);

        // When POST request to add new product to cart ("/cart/add") is
        // made, with 10 products, and product id = 1
        // Then:
        // - status is 3xx - redirection
        // - url of redirected page is "/product"
        // - flash message has FAILURE status
		mockMvc.perform(
		        MockMvcRequestBuilders
                        .post("/cart/add")
                        .param("quantity", "10")
                        .param("productId", "1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/product/"))
                .andExpect(flash().attribute(
                        "flash",
                        Matchers.hasProperty("status",
                                Matchers.equalTo(FlashMessage.Status.FAILURE)
                            )
                        )
                );
	}
    // This test checks up if POST request adding more items that we
    // have in db of this type was done, but in a different manner. This
    // error is thrown when cart.quantity of this product plus
    // some quantity should be less than quantity in db.
    // Example:
    // - 3 products in stock
    // - cart has already 3
    // - user clicks add to cart: 3+1 > 3 -> error
    @Test
    public void addToCartPostRequestWithQuantityPlusCartQuantityMoreThanInDbFails()
            throws Exception {
        // Arrange product: first product with three items
        Product product = productBuilder();
        // Arrange product return by service: when service will be called
        // in controller: 1-st product will be returned
        when(productService.findById(1L)).thenReturn(product);

        // Arrange purchase: we build purchase with quantity equal to number
        // of items in stock. See below
        Purchase purchase =
                purchaseBuilderWithQuantityEqualToQuantityInDb(product);
        // Arrange returning this purchase when it will be called in controller
        when(sCart.getPurchase()).thenReturn(purchase);

        // When POST request to add new product to cart ("/cart/add")
        // with quantity equal to number of products in database is
        // made, and one last product will overfill cart
        // Then:
        // - status is 3xx - redirection
        // - url of redirected page is "/product"
        // - flash message has FAILURE status
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/cart/add")
                        .param("quantity", "1")
                        .param("productId", "1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/"))
                .andExpect(flash().attribute(
                        "flash",
                        Matchers.hasProperty("status",
                                Matchers.equalTo(FlashMessage.Status.FAILURE)
                        )
                    )
                );
    }

	@Test
	public void addUnknownToCartTest() throws Exception {
		when(productService.findById(1L)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/add").param("quantity", "1").param("productId", "1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/error"));
	}

	// Task #5: Enhancement
	// In this successful update test we added check that flash message with
	// was indeed sent with redirect attributes
	@Test
	public void updateCartTest() throws Exception {
		// Arrange:
		// Create test product
		Product product = productBuilder();
		// arrange service to return product above
		when(productService.findById(1L)).thenReturn(product);
		// create test purchase
		Purchase purchase = purchaseBuilder(product);
		// arrange cart to return purchase we simulated
		when(sCart.getPurchase()).thenReturn(purchase);

		// Act and Assert:
		// When POST request to update quantity is made with valid
		// parameters, Then:
		// - status should be 3xx - redirection
		// - redirected URL should be "/cart"
		// - flash attribute should be with success status
		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/cart/update")
						.param("newQuantity", "2")
						.param("productId", "1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cart"))
				.andExpect(
						flash().attribute(
							"flash",
							Matchers.hasProperty(
									"status",
									Matchers.equalTo(
											FlashMessage.Status.SUCCESS)
							)
						)
				);
	}

	// Bug fix #2 - updating with quantity more than there are in db fails
	@Test
	public void updatingCartWithQuantityMoreThanInDbFails()
			throws Exception {
		// Arrange product returned when findById is returned
		Product product = productBuilder();
		when(productService.findById(1L)).thenReturn(product);
        // Arrange purchase returned when cart.getPurchase is called
		Purchase purchase = purchaseBuilder(product);
		when(sCart.getPurchase()).thenReturn(purchase);

		// Act, and Assert:
		// When :
		// 	 POST request is made to update cart, with new quantity
		// 	 more than quantity of items in db,
		// Then:
		//   - status should be of 3xx - redirection
		//   - redirected URL should be back to cart
		//   - flash message with status FAILURE should be send with
		//     redirect attributes
		mockMvc.perform(
				MockMvcRequestBuilders
						.post("/cart/update")
						.param("newQuantity", "10")
						.param("productId", "1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cart"))
				.andExpect(
						flash().attribute(
							"flash", Matchers.hasProperty(
									"status", Matchers.equalTo(
											FlashMessage.Status.FAILURE)
							)
						)
				);
	}

	@Test
	public void updateUnknownCartTest() throws Exception {
		when(productService.findById(1L)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/update").param("newQuantity", "2").param("productId", "1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/error"));
	}

	@Test
	public void updateInvalidCartTest() throws Exception {

		when(sCart.getPurchase()).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/update").param("newQuantity", "2").param("productId", "1"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/error"));
	}

	@Test
	public void removeFromCartTest() throws Exception {
		Product product = productBuilder();

		Product product2 = productBuilder();
		product2.setId(2L);

		when(productService.findById(1L)).thenReturn(product);

		ProductPurchase pp = new ProductPurchase();
		pp.setProductPurchaseId(1L);
		pp.setQuantity(1);
		pp.setProduct(product);

		ProductPurchase pp2 = new ProductPurchase();
		pp2.setProductPurchaseId(2L);
		pp2.setQuantity(2);
		pp2.setProduct(product2);

		List<ProductPurchase> ppList = new ArrayList<ProductPurchase>();
		ppList.add(pp);
		ppList.add(pp2);

		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setProductPurchases(ppList);

		when(sCart.getPurchase()).thenReturn(purchase);

		when(purchaseService.save(purchase)).thenReturn(purchase);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/remove").param("productId", "1")).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cart"));
	}

	@Test
	public void removeUnknownCartTest() throws Exception {
		when(productService.findById(1L)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/remove").param("productId", "1")).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/error"));
	}

	@Test
	public void removeInvalidCartTest() throws Exception {

		when(sCart.getPurchase()).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/remove").param("productId", "1")).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/error"));
	}

	@Test
	public void removeLastFromCartTest() throws Exception {
		Product product = productBuilder();

		when(productService.findById(1L)).thenReturn(product);

		Purchase purchase = purchaseBuilder(product);

		when(sCart.getPurchase()).thenReturn(purchase);

		when(purchaseService.save(purchase)).thenReturn(purchase);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/remove").param("productId", "1")).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/product/"));
	}

	@Test
	public void emptyCartTest() throws Exception {
		Product product = productBuilder();

		Product product2 = productBuilder();
		product2.setId(2L);

		when(productService.findById(1L)).thenReturn(product);

		ProductPurchase pp = new ProductPurchase();
		pp.setProductPurchaseId(1L);
		pp.setQuantity(1);
		pp.setProduct(product);

		ProductPurchase pp2 = new ProductPurchase();
		pp2.setProductPurchaseId(2L);
		pp2.setQuantity(2);
		pp2.setProduct(product2);

		List<ProductPurchase> ppList = new ArrayList<ProductPurchase>();
		ppList.add(pp);
		ppList.add(pp2);

		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setProductPurchases(ppList);

		when(sCart.getPurchase()).thenReturn(purchase);

		when(purchaseService.save(purchase)).thenReturn(purchase);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/empty")).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/product/"));
	}

	@Test
	public void emptyInvalidCartTest() throws Exception {

		when(sCart.getPurchase()).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/cart/empty")).andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/error"));
	}

	private Product productBuilder() {
		Product product = new Product();
		product.setId(1L);
		product.setDesc("TestDesc");
		product.setName("TestName");
		product.setPrice(new BigDecimal(1.99));
		product.setQuantity(3);
		product.setFullImageName("imagename");
		product.setThumbImageName("imagename");
		return product;
	}

	private Purchase purchaseBuilder(Product product) {
		ProductPurchase pp = new ProductPurchase();
		pp.setProductPurchaseId(1L);
		pp.setQuantity(1);
		pp.setProduct(product);
		List<ProductPurchase> ppList = new ArrayList<>();
		ppList.add(pp);

		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setProductPurchases(ppList);
		return purchase;
	}

	// constructor used to model behaviour in method:
    //  addToCartPostRequestWithQuantityPlusCartQuantityMoreThanInDbFails
    //  the important line : to set quantity to product quantity
    private Purchase
    purchaseBuilderWithQuantityEqualToQuantityInDb(Product product) {
        ProductPurchase pp = new ProductPurchase();
        pp.setProductPurchaseId(1L);
        // important line: set quantity to product quantity, so that
        // when add one more product to cart, it should throw error
        pp.setQuantity(product.getQuantity());
        pp.setProduct(product);
        List<ProductPurchase> ppList = new ArrayList<>();
        ppList.add(pp);

        Purchase purchase = new Purchase();
        purchase.setId(1L);
        purchase.setProductPurchases(ppList);
        return purchase;
    }
}
