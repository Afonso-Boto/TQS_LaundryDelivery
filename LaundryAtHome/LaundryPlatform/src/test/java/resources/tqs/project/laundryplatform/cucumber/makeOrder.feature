Feature: Make order from customer

  Scenario: Login
    When I navigate to "http://localhost:8080/"
    And I click the login button
    And I set the email as "jonas@example.com
    And I set the password as "password"
    And I click login
    Then I should see the pattern "Login Successful"

  Scenario: Make Order
    When I navigate to "http://localhost:8080/order"
