Feature: Login with user test

  Scenario: Login
    When I navigate to "http://localhost:81/"
    And I set the username as "test"
    And I set the password as "123"
    And I click the login button
    Then I should see the index page