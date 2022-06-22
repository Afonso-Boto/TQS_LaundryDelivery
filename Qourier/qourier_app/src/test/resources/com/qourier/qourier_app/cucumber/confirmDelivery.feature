Feature: Confirm a delivery job

  Background:
    Given the following accounts exist:
      | email | role | state |
      | customer@gmail.com | customer | active |
      | rider1@hotmail.com | rider | active |
    And I am logged in as 'rider1@hotmail.com'

  Scenario: Confirm a delivery job as a Rider
    Given the following deliveries are up:
      | customer | latitude | longitude | rider | state |
      | customer@gmail.com | 40 | 40 | rider1@hotmail.com | shipped |
    When I go to the Deliveries section
    And I mark the delivery as being done
    Then I can bid for another delivery
    And the delivery job is registered as done

  Scenario: Mark a delivery job as being shipped a Rider
    Given the following deliveries are up:
      | customer | latitude | longitude | rider | state |
      | customer@gmail.com | 40 | 40 | rider1@hotmail.com | fetching |
    When I go to the Deliveries section
    And I indicate that I picked up the delivery
    Then I can not bid for another delivery
    And the delivery job is registered as shipped