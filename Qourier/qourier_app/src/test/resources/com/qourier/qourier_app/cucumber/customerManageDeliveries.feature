Feature: Customer delivery management

  Scenario: Register delivery as a Customer
    Given I am logged in as a Customer
    * I have already been accepted to the platform
    * my account is not suspended
    When I go to the Deliveries section
    And I fill the delivery details
    And I register the delivery
    Then the delivery job is up for bidding
    And the delivery registration form is empty

  Scenario: Check progress of all deliveries
    Given the following accounts exist:
      | email | role | state |
      | the.first@sapo.pt | rider | active |
      | the.second@sapo.pt | rider | active |
      | the.third@sapo.pt | rider | active |
      | the.fourth@sapo.pt | rider | active |
      | the.fifth@sapo.pt | rider | active |
      | laundryathome@tqs.com  | customer | active |
    * the following deliveries are up:
      | customer | latitude | longitude | rider | state |
      | laundryathome@tqs.com | 1 | 99 | the.third@sapo.pt | fetching |
      | laundryathome@tqs.com | 50 | 132 | the.second@sapo.pt | shipped |
      | laundryathome@tqs.com | 5 | 6  | the.first@sapo.pt  | delivered |
      | laundryathome@tqs.com  | 50 | 50  | | bid check |
    And I am logged in as 'laundryathome@tqs.com'
    When I go to the Deliveries section
    Then a table with all requested, in progress and completed deliveries is shown
    And the deliveries in progress contain a progress bar
