Feature: Bid on delivery and get assign to it

  Scenario: Bid on delivery that it's already created
    Given I am logged in as a Rider
    * I have already been accepted to the platform
    * my account is not suspended
    * a delivery was already created
    When I go to the Deliveries section
    And I click the check button on the line of the first delivery presented
    And I click confirm
    And I wait 2 seconds for the auction to end
    And I go to check deliveries status
    Then My id should be assigned to the delivery
