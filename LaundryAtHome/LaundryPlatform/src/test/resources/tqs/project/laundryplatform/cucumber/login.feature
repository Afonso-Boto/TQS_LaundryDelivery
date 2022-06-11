Feature: Login with user test

  Scenario: Register
    When I navigate to "http://localhost:8080/register"
    And I set the your name as "test"
    And I set the username as "test"
    And I set the phone number as ""
    And I set the email as "test@ua.pt"
    And I set the password as "123"
    And I click the register button
    Then I should see the index page

  Scenario: Login
    When I navigate to "http://localhost:8080/login"
    And I set the username as "test"
    And I set the password as "123"
    And I click the login button
    Then I should see the index page