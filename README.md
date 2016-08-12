# Techdegree project 8
### Fix and Enhance an E-Commerce Shopping Cart
<hr>
### Table of Contents
### Installation instructions
* [Eclipse installation instructions.] (#eclipse)

<hr>

### Misc
- [Structure of the project] (#structure)
- [Quick Links to files and directories] (#links)

<hr>

### Tasks
* [1.] (#task-1) 
    Bug fix: Add form validation to the coupon code field in the first 
    step of the checkout process. 
    A coupon code will be considered valid if it contains between 
    5 and 10 characters. 
    A unit test should also be added to verify that the added 
    validation is working.
    <hr>
* [2.] (#task-2) 
    Bug fix: Ensure that enough products are in stock before 
    adding to the shopping cart. Whether adding products to the cart 
    from product detail pages or updating an product’s quantity from 
    the cart view, more products than are in stock can be added to the 
    cart. Fix this issue and add a unit test to cover this scenario.
    <hr>
* [3.] (#task-3) 
    Bug fix: Update the order confirmation view template to mask all 
    but the last 4 digits of the credit card number.
    <hr>
* [4.] (#task-4) 
    Bug fix: Update the order confirmation email template to remove the
    billing address and all payment info. Note that the email feature 
    is not fully implemented since it would require an SMTP server, 
    so this is implemented instead as a file download on the confirmation 
    page. The HTML of this file would be sent as the content of the 
    confirmation email in a fully implemented version.
    <hr>
* [5.] (#task-5) 
    Enhancement:
    Add the cart subtotal to the page header, 
    as part of the “View Cart” link.
    The subtotal should display only if the cart is not empty, 
    and the number should be formatted with a dollar sign, 
    as well as a comma for the thousands separator. 
    Add a unit test to verify that the rendered view contains the subtotal.
    <hr>
* [6.] (#task-6)
    Enhancement:
    Add flash messaging to the application for adding, updating,
    and removing products from the cart,
    and for emptying the cart.
    Don’t forget the case for when a user tries to request a
    product quantity that exceeds its amount in stock.
    Include unit tests to verify that flash messages work correctly.
    <hr>

<!--Links-->
<!-- settings files -->
[eCommerce.properties]:
    ./properties/eCommerce.properties "./properties/eCommerce.properties"
[application.properties]:
    src/main/resources/application.properties "Spring application properties file: application.properties"
[build.gradle]:
    build.gradle "Gradle configuration file: build.gradle"

<!-- static and resources -->
[resources]:
    src/main/resources "directory with static resources, application properties file and hibernate configuration file: src/main/resources"
[templates]:
    src/main/resources/templates "Thymeleaf templates directory: src/main/resources/templates"
[static]:
    src/main/resources/static "Static assets directory: src/main/resources/static"
[css]:
    src/main/resources/static/css "Directory with CSS files: src/main/resources/static/css"
[initial_project_files]:
    initial-project-files "directory with initial project files from Treeshouse"
[site.js]:
    src/main/resources/static/js/site.js "JavaScript file with all JavaScript functions used: src/main/resources/static/js/site.js"
[favicon.ico]:
    src/main/resources/static/favicon.ico "Icon used in tabs of the website: src/main/resources/static/favicon.ico"
<!--CSS files-->
[normalize.css]:
    src/main/resources/static/css/normalize.css "Normalize CSS, unchanged: src/main/resources/static/css/normalize.css"
[site.css]:
    src/main/resources/static/css/site.css "Main CSS file with custom styles added by me: src/main/resources/static/css/site.css"

<!-- Thymeleaf Templates -->
[layout.html]:
    ./src/main/resources/templates/layout.html "./src/main/resources/templates/layout.html"
[checkout_3.html]:
    ./src/main/resources/templates/checkout_3.html "./src/main/resources/templates/checkout_3.html"
[error.html]:
    ./src/main/resources/templates/error.html "./src/main/resources/templates/error.html"
[email_confirmation.html]:
    ./src/main/resources/templates/email_confirmation.html "./src/main/resources/templates/email_confirmation.html"
[about.html]:
    ./src/main/resources/templates/about.html "./src/main/resources/templates/about.html"
[checkout_1.html]:
    ./src/main/resources/templates/checkout_1.html "./src/main/resources/templates/checkout_1.html"
[order_confirmation.html]:
    ./src/main/resources/templates/order_confirmation.html "./src/main/resources/templates/order_confirmation.html"
[index.html]:
    ./src/main/resources/templates/index.html "./src/main/resources/templates/index.html"
[welcome.html]:
    ./src/main/resources/templates/welcome.html "./src/main/resources/templates/welcome.html"
[product_detail.html]:
    ./src/main/resources/templates/product_detail.html "./src/main/resources/templates/product_detail.html"
[checkout_2.html]:
    ./src/main/resources/templates/checkout_2.html "./src/main/resources/templates/checkout_2.html"
[cart.html]:
    ./src/main/resources/templates/cart.html "./src/main/resources/templates/cart.html"

<!-- Java files -->

[TestPurchaseIT]:
    ./src/test/java/com/acme/ecommerce/TestPurchaseIT.java "./src/test/java/com/acme/ecommerce/TestPurchaseIT.java"
[TestProductControllerIT]:
    ./src/test/java/com/acme/ecommerce/controller/TestProductControllerIT.java "./src/test/java/com/acme/ecommerce/controller/TestProductControllerIT.java"
[ProductControllerTest]:
    ./src/test/java/com/acme/ecommerce/controller/ProductControllerTest.java "./src/test/java/com/acme/ecommerce/controller/ProductControllerTest.java"
[CheckoutControllerTest]:
    ./src/test/java/com/acme/ecommerce/controller/CheckoutControllerTest.java "./src/test/java/com/acme/ecommerce/controller/CheckoutControllerTest.java"
[CartControllerTest]:
    ./src/test/java/com/acme/ecommerce/controller/CartControllerTest.java "./src/test/java/com/acme/ecommerce/controller/CartControllerTest.java"
[IndexRedirectControllerTest]:
    ./src/test/java/com/acme/ecommerce/controller/IndexRedirectControllerTest.java "./src/test/java/com/acme/ecommerce/controller/IndexRedirectControllerTest.java"
[TestProductIT]:
    ./src/test/java/com/acme/ecommerce/TestProductIT.java "./src/test/java/com/acme/ecommerce/TestProductIT.java"
[TestAddressIT]:
    ./src/test/java/com/acme/ecommerce/TestAddressIT.java "./src/test/java/com/acme/ecommerce/TestAddressIT.java"
[ProductService]:
    ./src/main/java/com/acme/ecommerce/service/ProductService.java "./src/main/java/com/acme/ecommerce/service/ProductService.java"
[PurchaseServiceImpl]:
    ./src/main/java/com/acme/ecommerce/service/PurchaseServiceImpl.java "./src/main/java/com/acme/ecommerce/service/PurchaseServiceImpl.java"
[PurchaseService]:
    ./src/main/java/com/acme/ecommerce/service/PurchaseService.java "./src/main/java/com/acme/ecommerce/service/PurchaseService.java"
[ProductServiceImpl]:
    ./src/main/java/com/acme/ecommerce/service/ProductServiceImpl.java "./src/main/java/com/acme/ecommerce/service/ProductServiceImpl.java"
[Application]:
    ./src/main/java/com/acme/ecommerce/Application.java "./src/main/java/com/acme/ecommerce/Application.java"
[PurchaseRepository]:
    ./src/main/java/com/acme/ecommerce/repository/PurchaseRepository.java "./src/main/java/com/acme/ecommerce/repository/PurchaseRepository.java"
[AddressRepository]:
    ./src/main/java/com/acme/ecommerce/repository/AddressRepository.java "./src/main/java/com/acme/ecommerce/repository/AddressRepository.java"
[ProductPurchaseRepository]:
    ./src/main/java/com/acme/ecommerce/repository/ProductPurchaseRepository.java "./src/main/java/com/acme/ecommerce/repository/ProductPurchaseRepository.java"
[ProductRepository]:
    ./src/main/java/com/acme/ecommerce/repository/ProductRepository.java "./src/main/java/com/acme/ecommerce/repository/ProductRepository.java"
[FlashMessage]:
    ./src/main/java/com/acme/ecommerce/web/FlashMessage.java "./src/main/java/com/acme/ecommerce/web/FlashMessage.java"
[NotFoundException]:
    ./src/main/java/com/acme/ecommerce/exception/NotFoundException.java "./src/main/java/com/acme/ecommerce/exception/NotFoundException.java"
[CombinedBilling]:
    ./src/main/java/com/acme/ecommerce/domain/CombinedBilling.java "./src/main/java/com/acme/ecommerce/domain/CombinedBilling.java"
[Purchase]:
    ./src/main/java/com/acme/ecommerce/domain/Purchase.java "./src/main/java/com/acme/ecommerce/domain/Purchase.java"
[Address]:
    ./src/main/java/com/acme/ecommerce/domain/Address.java "./src/main/java/com/acme/ecommerce/domain/Address.java"
[ProductPurchase]:
    ./src/main/java/com/acme/ecommerce/domain/ProductPurchase.java "./src/main/java/com/acme/ecommerce/domain/ProductPurchase.java"
[Product]:
    ./src/main/java/com/acme/ecommerce/domain/Product.java "./src/main/java/com/acme/ecommerce/domain/Product.java"
[ShoppingCart]:
    ./src/main/java/com/acme/ecommerce/domain/ShoppingCart.java "./src/main/java/com/acme/ecommerce/domain/ShoppingCart.java"
[CouponCode]:
    ./src/main/java/com/acme/ecommerce/domain/CouponCode.java "./src/main/java/com/acme/ecommerce/domain/CouponCode.java"
[WebConstants]:
    ./src/main/java/com/acme/ecommerce/controller/WebConstants.java "./src/main/java/com/acme/ecommerce/controller/WebConstants.java"
[IndexRedirectController]:
    ./src/main/java/com/acme/ecommerce/controller/IndexRedirectController.java "./src/main/java/com/acme/ecommerce/controller/IndexRedirectController.java"
[CheckoutController]:
    ./src/main/java/com/acme/ecommerce/controller/CheckoutController.java "./src/main/java/com/acme/ecommerce/controller/CheckoutController.java"
[ProductController]:
    ./src/main/java/com/acme/ecommerce/controller/ProductController.java "./src/main/java/com/acme/ecommerce/controller/ProductController.java"
[CartController]:
    ./src/main/java/com/acme/ecommerce/controller/CartController.java "./src/main/java/com/acme/ecommerce/controller/CartController.java"
[PersistenceConfig]:
    ./src/main/java/com/acme/ecommerce/config/PersistenceConfig.java "./src/main/java/com/acme/ecommerce/config/PersistenceConfig.java"
[ApplicationConfig]:
    ./src/main/java/com/acme/ecommerce/config/ApplicationConfig.java "./src/main/java/com/acme/ecommerce/config/ApplicationConfig.java"



### Eclipse Installation instructions
<hr> <a id="eclipse"></a>
Under construction...




### Tasks
1. <a id="task-1"></a>
    Bug fix: Add form validation to the coupon code field in the first 
    step of the checkout process. 
    A coupon code will be considered valid if it contains between 
    5 and 10 characters. 
    A unit test should also be added to verify that the added 
    validation is working.
    <hr>
    1. I added `@NotEmpty` annotation and `@Size`
        annotations to [CouponCode] `code` field, with appropriate
        messages, that will be displayed in case of error.
    2. [CheckoutController] method `postCouponCode` was changed
        so that coupon code is checked:
        - `@Valid` annotation was added to attribute "code"
        - `BindingResult` was added, to see errors
        - `RedirectAttributes` were added, to send with
            redirect wrong user's code and error in
            `BindingResult`, changed accordingly to account 
            for errors on coupon's code field
    3. I also changed code in `checkoutCoupon` coupon method
        to account for wrong user code, to be saved upon 
        unsuccessful POST request
    4. Thymeleaf template was changed accordingly, so that
        `<input>` tag is surrounded by `<div>` changing
        its class when types wrong code.
    5. Following unit tests were added to test functionality:
        - `postCouponTest`
        - `postingWrongCouponRedirectsBackToStepOnePage`
        - `postingCouponWithNoRequestParamsRedirectsBackToStepOnePage`
        - `postingEmptyCouponRedirectsBackToStepOnePage`

    Description can be found in each test
<hr>
2. <a id="task-2"></a>
    Bug fix: Ensure that enough products are in stock before 
    adding to the shopping cart. Whether adding products to the cart 
    from product detail pages or updating an product’s quantity from 
    the cart view, more products than are in stock can be added to the 
    cart. Fix this issue and add a unit test to cover this scenario.
    <hr>
    In order to fix the bug in [CartController] I modified 
    `addToCart` and `updateCart` methods:
    - in `addToCart` method I check quantity two times:
        1. When the user asks the quantity already more than
            we have in stock: e.g. in stock: 5, in cart 0,
            user asks 6.
            Test that checks this in [CartControllerTest] is called :
            `addToCartPostRequestWithQuantityMoreThanInDbFails` 
        2. When the user has some items in cart, and then he
            adds some more so that total amount is more than
            in stock: e.g. in stock: 5, user had 5 in cart,
            clicked add one more. Test that checks this in
            [CartControllerTest] is:
            `addToCartPostRequestWithQuantityPlusCartQuantityMoreThanInDbFails`
    
    When this "overflow" error happens, I redirect user back
    to products page, add Flash message on top, and also
    set logger error.
    - in `updateCart` I check only once: because there cannot
        be two scenarios like in `addToCart`. Test in 
        [CartControllerTest] is called:
        `updatingCartWithQuantityMoreThanInDbFails`
<hr>
3. <a id="task-3"></a>
    Bug fix: Update the order confirmation view template to mask all 
    but the last 4 digits of the credit card number.
    <hr>
    In order to do that I did not change thymeleaf view, I decided
    to change attribute that passed to model, so that when actual
    request is made we send already coded number that looks like
    "####-1234". Number of hash signs can be different, because 
    number of credit card characters can be from 13 to 19. 
    <br>
    In order to generate this "hidden" credit card number
    I implemented new method `makeCreditCardNumberHidden`
    in [CheckoutController].
    And in actual `checkoutConfirmation` method I just pass
    the return value from this method.
    <br>
    The method itself is tested in [CheckoutControllerTest],
    and it is called 
    `creditCardNumberShouldBeSuccessfullyHiddenByIntroducedMethod`
<hr>
4. <a id="task-4"></a>
    Bug fix: Update the order confirmation email template to remove the
    billing address and all payment info. Note that the email feature
    is not fully implemented since it would require an SMTP server,
    so this is implemented instead as a file download on the confirmation
    page. The HTML of this file would be sent as the content of the
    confirmation email in a fully implemented version.
    <hr>
    For now I just removed from [CheckoutController] method
    generating e-mail `getFile` billing
    address and credit card number. I did the same in thymeleaf
    template [email_confirmation.html]. E-mail feature is 
    supposed to be working like this I guess - at confirmation
    page automatically user is asked where to download file.
    Automatic feature does not work for me. I'll try to dig 
    into this later
<hr>
5. <a id="task-5"></a>
    Enhancement:
    Add the cart subtotal to the page header, 
    as part of the “View Cart” link.
    The subtotal should display only if the cart is not empty, 
    and the number should be formatted with a dollar sign, 
    as well as a comma for the thousands separator. 
    Add a unit test to verify that the rendered view contains the subtotal.
    <hr>
    I added 'div' with subtotal just like in 
    [checkout_1.html] template to the [layout.html] file
    in a way that if subtotal value is not null, it will
    be displayed in "header". I also changed styling
    of "nav" element in header that changes when
    `subtotal` is not null. Added couple of CSS 
    classes accordingly. NOTE: there was no problems
    with format, because I took already working 
    code from usual `subtotal` on checkout pages.
    <hr>
    In terms of code, there was nothing to be changed
    in controllers or elsewhere except testing classes.
    <br>
    In order to test that I needed to change integration
    test `ProductDetailAddItemIntegrationTest` in 
    [TestProductControllerIT]. There I test my `subtotal`
    in "header" just like usual `subtotal` in body:
    - get element by id
    - assert that this element is equal to subtotal
        in body
<hr>
6. <a id="task-6"></a>
    Enhancement:
    Add flash messaging to the application for adding, updating,
    and removing products from the cart,
    and for emptying the cart.
    Don’t forget the case for when a user tries to request a
    product quantity that exceeds its amount in stock.
    Include unit tests to verify that flash messages work correctly.
    <hr>
    1. add product to cart flash messaging:
       <br>
       Flash message is sent on successful addition of product to cart,
       as well as on the unsuccessful: when there are no products left in
       stock. They are both implemented in [CartController] method
       `addToCart`.
       Flash messaging itself is implemented with class [FlashMessage],
       that has `String message`, with user message and `enum status` with
       two statuses: `FlashMessage.Status.FAILURE` and
       `FlashMessage.Status.SUCCESS`. Exactly last ones are tested in
       unit tests implemented in [CartControllerTest] classes:
       See `addToCartTest` method for positive addition and
       [Task 2](#task-2) for failed addition to cart.
    2. Update number of items in cart:
        <br>
       Flash message is sent for both successful and unsuccessful update
       of number of items in cart. They are both implemented in
       [CartController] method `updateCart`. Success status of flash message
       is checked in `updateCartTest`, just like all flash messages.
       About failed update I've written in [Task 2](#task-2).
    3. Delete items from cart:
        <br>
       Flash message is sent upon sucessful delete product from cart. It is
       implemented in [CartController] method `removeFromCart` and tested in
       [CartControllerTest] method `removeFromCartTest`. I didn't include
        flash messaging for
        unsuccessful removal redirecting to "/error" page, because
       `@ExceptionHandler` is not set-up, and if set up it, unfortunately,
       I need
       to change a lot of tests, that check just redirect to error, and not
       the throwal of exception. This requires major refactoring of the
       project, which I'm not ready to do at this point.
    4. Emptying cart:
       <br>
       Flash message is sent upon successful emptying of the cart. It is
       implemented in [CartController] method `emptyCart` and tested
       in [CartControllerTest] method `emptyCartTest`. Again just like before
       no flashes sent upon unsuccessful removal in case of null and etc,
       because of the reason above (see description about 'delete' above).
       <hr>
7. <a id="task-7"></a>







<a id="links"></a>
### Quick Links
<!--Links-->

#### Settings files 
- [eCommerce.properties]
- [application.properties]
- [build.gradle]


#### Directories
- [resources]
- [templates]
- [static]
- [css]
- [initial_project_files]


#### Static and resources files
- [site.js]
- [favicon.ico]


#### CSS files
- [normalize.css]
- [site.css]


#### Thymeleaf Templates 
- [layout.html]
- [checkout_3.html]
- [error.html]
- [email_confirmation.html]
- [about.html]
- [checkout_1.html]
- [order_confirmation.html]
- [index.html]
- [welcome.html]
- [product_detail.html]
- [checkout_2.html]
- [cart.html]


#### Java files 

##### Unit testing files
- [TestPurchaseIT]
- [TestProductControllerIT]
- [ProductControllerTest]
- [CheckoutControllerTest]
- [CartControllerTest]
- [IndexRedirectControllerTest]
- [TestProductIT]
- [TestAddressIT]


##### Services and implementations
- [ProductService]
- [PurchaseService]
- [PurchaseServiceImpl]
- [ProductServiceImpl]


##### Main Application file
- [Application]


##### Configuration files
- [PersistenceConfig]
- [ApplicationConfig]


##### Repositories
- [PurchaseRepository]
- [AddressRepository]
- [ProductPurchaseRepository]
- [ProductRepository]


##### Helpful web classes
- [FlashMessage]
- [NotFoundException]
- [WebConstants]


##### Model or Domain classes
- [CombinedBilling]
- [Purchase]
- [Address]
- [ProductPurchase]
- [Product]
- [ShoppingCart]
- [CouponCode]


##### Controllers
- [IndexRedirectController]
- [CheckoutController]
- [ProductController]
- [CartController]


