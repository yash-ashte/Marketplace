FOR ALL TESTS ASSUME CONNECTION BETWEEN SERVER AND CLIENT IS ALREADY ESTABLISHED


TESTS FOR ALL ACCOUNTS

Test 1: User log in

    1. User runs program.
    2. User clicks on email text field and enters email.
    3. User clicks on password text field and enters password. 
    4. User clicks on log in button. 

    Expected Result: Email and password are verified and a user logs in to their respective account. 

    Test Status: Passed.

Test 2: User sign up

    1. User runs program.
    2. User selects sign up button. 
    3. User clicks on name text field and enters name.
    4. User clicks on email text field and enters email.
    5. User clicks on password text field and enters password. 
    6. User clicks on confirm password text field and re-enters password.
    7. User clicks on sign up button.
    8. User clicks on seller or customer button (depending on account type).

    Expected Result: New seller or customer account is created with the provided information.

    Test Status: Passed. 

Test 3: User manage account

    1. User logs in.
    2. User clicks on edit profile button.
    3. User clicks on text fields to change and enters desired changes. 
    4. User clicks on update profile button.

    Expected Result: The current user's information is updated to reflect their desired changes.

    Test Status: Passed.

Test 4: User delete account

    1. User logs in.
    2. User clicks on edit profile button.
    3. User clicks on delete profile button.
    4. User clicks on ok button.

    Expected Result: The current user account is deleted. 

    Test Status: Passed


TESTS FOR SELLER ACCOUNTS

Test 5: Seller add store

    1. Seller logs in.
    2. Seller clicks on add store button.
    3. Seller clicks on text field and enters store name.
    4. Seller clicks on ok button.

    Expected Result: A store is created with the store name that was entered. 

    Test Status: Passed.

Test 6: Seller add product to store

    1. Seller logs in.
    2. Seller clicks on view store button next to store to add products to.
    3. Seller clicks on add product button.
    4. Seller clicks on text fields and enters information for the product being added.
    5. Seller clicks on add product button

    Expected Result: A product is created and added to the store.

    Test Status: Passed. 

Test 7: Seller view sale history

    1. Seller logs in.
    2. Seller clicks on view store button next to store to view sale history for.
    3. Seller clicks on view sales history button.

    Expected Result: All sales from the store are displayed for the seller to view.

    Test Status: Passed. 

Test 8: Seller delete store

    1. Seller logs in.
    2. Seller clicks on view store button next to store to delete.
    3. Seller clicks on delete store button.

    Expected Result: The selected store and all of its products are removed.

    Test Status: Passed. 

Test 9: Seller view dashboard

    1. Seller logs in.
    2. Seller clicks on view store button next to store to view dashboard for.
    3. Seller clicks on view statistics dashboard button. 
    4. Seller selects from sort lists to sort the visible lists, if they wish.

    Expected Result: Two separate lists are displayed. One for customers who pruhcased form the store and the number of products bough. One for products purchased from the store and how much were bought. The sort allows these lists to be sorted from quantity high-low or low-high.

    Test Status: Passed. 

Test 10: Seller manage product

    1. Seller logs in.
    2. Seller clicks on view store button next to store to manage product for.
    3. Seller clicks on manage product button next to product to be managed. 
    4. Seller clicks on texts fields and enters information to be changed for selected product.
    5. Seller clicks on confirm edits to product button. 

    Expected Result: The selected product is edited according to the information entered into the text fields.

    Test Status: Passed. 

Test 11: Seller delete product

    1. Seller logs in.
    2. Seller clicks on view store button next to store to delete product for.
    3. Seller clicks on manage product button next to product to be deleted.
    4. Seller clicks on delete this product button.
    5. Seller clicks on yes button.

    Expected Result: The selected product is removed from the store.

    Test Status: Passed. 

Test 12: Seller export products

    1. Seller logs in.
    2. Seller clicks on view store button next to store to export products from.
    3. Seller clicks on export products text field and enter filename for export.
    4. Seller clicks on export products button.

    Expected Result: The list of products from the current store is written to a file for the seller to view.

    Test Status: Passed. 

Test 13: Seller view shopping cart information

    1. Seller logs in.
    2. Seller clicks on view shopping cart metrics.

    Expected Result: Information for total products in carts and how much of each product are within carts is displayed.

    Test Status: Passed. 

Test 14: Seller import products

    1. Seller logs in.
    2. Seller clicks on view store next to store to import products to. 
    3. Seller selects import products text field and enters the filename for products to import.
    4. Seller clicks on import product button. 

    Expected Result: Products from a file are taken and add to the current stores available procuts. 

    Test Status: Passed.


TESTS FOR CUSTOMER ACCOUNTS

Test 15: Customer add product to cart

    1. Customer logs in.
    2. Customer clicks on view product button next to product to add to cart.
    3. Customer clicks on add to cart button.

    Expected Result: The selected product is added to the customer's cart.

    Test Status: Passed.

Test 16: Customer search for product

    1. Customer logs in.
    2. Customer clicks on search text field and enters desired search.
    3. Customer clicks on search button.

    Expected Result: Any product with the search keyword in their name, store, or description is displayed. 

    Test Status: Passed. 

Test 17: Customer sort products

    1. Customer logs in.
    2. Customer clicks on dropdown menu for sort in top right corner. 
    3. Customer selects desired sort for products.

    Expected Result: The products currently available on the market are displayed in the desired sort order. 

    Test Status: Passed. 

Test 18: Customer view purchase history

    1. Customer logs in.
    2. Customer clicks on view purchase history button. 

    Expected Result: The customer's previous purchase are displayed.

    Test Status: Passed. 

Test 19: Customer remove product from cart

    1. Customer logs in.
    2. Customer clicks on manage cart button.
    3. Customer clicks on remove product button next to product to be removed. 

    Expected Result: The product selected is removed from the current customer's cart.

    Test Status: Passed.

Test 20: Customer purchase cart

    1. Customer logs in.
    2. Customer clicks on manage cart button.
    3. Customer clicks on purhcase all button.

    Expected Result: The products currently in the customer's cart are purchased.

    Test Status: Passed. 

Test 21: Customer export purchase history

    1. Customer logs in.
    2. Customer clicks on view purchase history button.
    3. Customer clicks on export text field and enters filename for export. 
    4. Customer clicks on export button.

    Expected Result: A file is created with the current customer's purchase history for them to view.

    Test Status: Passed. 

Test 22: Customer view dashboard

    1. Customer logs in.
    2. Customer clicks on view stores.
    3. Customer selects desired sort from dropdown menu, if necessary.

    Expected Result: A list of stores with products purchased from the stores, either by current customer or in general depending on sort, is displayed.

    Test Status: Passed. 