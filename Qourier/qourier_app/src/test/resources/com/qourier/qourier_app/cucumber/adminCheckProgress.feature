Feature: Check Riders' progress

  Scenario: Check Riders' progress on concurrent deliveries as an Admin
    Given the following accounts exist:
      | email | role | state |
      | jacoco@mail.com | admin | active |
      | the.first@sapo.pt | rider | active |
      | the.second@sapo.pt | rider | active |
      | the.third@sapo.pt | rider | active |
      | the.fourth@sapo.pt | rider | active |
      | the.fifth@sapo.pt | rider | active |
      | laundryathome@tqs.com  | customer | active |
      | another.kustomer@tqs.com  | customer | active |
    * the following deliveries are up:
      | customer | latitude | longitude | rider | state |
      | laundryathome@tqs.com | 40 | 40 | the.first@sapo.pt | fetching |
      | another.kustomer@tqs.com | 50 | 132 | the.second@sapo.pt | shipped |
      | another.kustomer@tqs.com | 10 | 10  | the.third@sapo.pt  | delivered |
      | laundryathome@tqs.com  | 90 | 10  | the.fourth@sapo.pt | bid check |
    And I am logged in as 'jacoco@mail.com'
    When I go to the Progress section
    Then a table of the currently participating Riders' progress is shown
