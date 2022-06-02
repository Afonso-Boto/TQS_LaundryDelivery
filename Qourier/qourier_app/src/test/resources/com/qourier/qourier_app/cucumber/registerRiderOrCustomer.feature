Feature: Register non admin accounts

  Scenario: Register Rider
    When I navigate to "http://localhost:8080/"
    And I click the register account button for the type Rider
    And I set the Email as "example0@email.com"
    And I set the Password as "P@22w0rd"
    And I set the Name as "John Smith"
    And I set the Citizen ID as "0123456789"
    And I click the register button
    And I click the "Deliveries" tab
    Then I should see in the page body the pattern "Account permission to access resource pending"

  Scenario: Register Customer
    When I navigate to "http://localhost:8080/"
    And I click the register account button for the type Customer
    And I set the Email as "example1@email.com"
    And I set the Password as "P@22w0rd"
    And I set the Name as "Cristina Ferreira"
    And I set the Service Type as "Laundry"
    And I click the register button
    And I click the "Deliveries" tab
    Then I should see in the page body the pattern "Account permission to access resource pending"