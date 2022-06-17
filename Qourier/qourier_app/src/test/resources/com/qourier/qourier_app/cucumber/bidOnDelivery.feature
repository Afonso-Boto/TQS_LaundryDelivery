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

  Scenario: Delivery accepted and notified
    Given the following accounts exist:
      | email | role | state |
      | customer@gmail.com | customer | active |
      | rider1@hotmail.com | rider | active |
      | rider2@hotmail.com | rider | active |
      | rider3@hotmail.com | rider | active |
    * the following deliveries are up:
      | customer | latitude | longitude | state |
      | customer@gmail.com | 40 | 40 | bidding |
    * the following bids have been done:
      | customer            | delivery_lat | delivery_lon | rider              |
      | customer@gmail.com  | 40           | 40           | rider2@hotmail.com |
    And I am logged in as 'rider1@hotmail.com'
    * I made a bid for the 'customer@gmail.com' delivery at (40, 40), for which I'm the most apt candidate
    When I go to the Deliveries section
    And I wait for the auction to end
    Then I should receive a notification indicating that I have been accepted
    * I should be the assigned Rider for the delivery
    * I can not bid for another delivery
    * the delivery job is not up for bidding

  Scenario: Delivery not accepted, and therefore not notified
    Given the following accounts exist:
      | email | role | state |
      | customer@gmail.com | customer | active |
      | rider1@hotmail.com | rider | active |
      | rider2@hotmail.com | rider | active |
      | rider3@hotmail.com | rider | active |
    * the following deliveries are up:
      | customer | latitude | longitude | state |
      | customer@gmail.com | 40 | 40 | bidding |
    * the following bids have been done:
      | customer            | delivery_lat | delivery_lon | rider              |
      | customer@gmail.com  | 40           | 40           | rider2@hotmail.com |
    And I am logged in as 'rider1@hotmail.com'
    * I made a bid for the 'customer@gmail.com' delivery at (40, 40), for which I'm the least apt candidate
    When I go to the Deliveries section
    And I wait for the auction to end
    Then I should not receive a notification indicating that I have been accepted
    * I should not be the assigned Rider for the delivery
    * I can bid for another delivery
    * the delivery job is up for bidding
