Feature: Bid on delivery and get assigned to it

  Scenario: Bid on delivery that was already created
    Given I am logged in as a Rider
    * I have already been accepted to the platform
    * my account is not suspended
    * a delivery was already created
    When I go to the Deliveries section
    And I click the check button on the line of the first delivery presented
    And I click confirm
    And I wait for the auction to end
    And I go to check deliveries status
    Then My id should be assigned to the delivery

  Scenario: Delivery accepted and notified
    Given the following accounts exist:
      | email | role | state |
      | customer@gmail.com | customer | active |
      | rider1@hotmail.com | rider | active |
      | rider2@hotmail.com | rider | active |
      | rider3@hotmail.com | rider | active |
    * the following deliveries are up:
      | customer | latitude | longitude |
      | customer@gmail.com | 40 | 40 |
    And I am logged in as 'rider1@hotmail.com'
    When I go to the Deliveries section
    * I click the check button on the line of the first delivery presented
    * I click confirm
    * I wait for the auction to end
    Then a rider assignment notification should have been sent
    * I should receive a notification indicating that I have been accepted
    * I should be presented with the delivery's details
    * I should be the assigned Rider for the delivery
    * I can not bid for another delivery
    * the delivery job is not up for bidding
