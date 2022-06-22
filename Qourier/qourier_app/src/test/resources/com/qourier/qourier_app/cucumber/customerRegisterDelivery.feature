Feature: Register delivery

  Scenario: Register delivery as a Customer
    Given I am logged in as a Customer
    * I have already been accepted to the platform
    * my account is not suspended
    When I go to the Deliveries section
    And I fill the delivery details
    And I register the delivery
    Then the delivery job is up for bidding
    And the delivery registration form is empty