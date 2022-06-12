Feature: Accept/Refuse Rider/Customer applications

  Background:
    Given the following accounts exist:
      | email | role | state |
      | jacoco@mail.com | admin | active |
      | riderino@hotmail.com | rider | pending |
      | kustomer@kustom.com  | customer | pending |
      | another.rider@gmail.com | rider | refused |
    And I am logged in as 'jacoco@mail.com'

  Scenario: Accept Rider application
    When I go to the Applications section
    * I filter for pending applications
    * I apply the filters
    * I open the 'riderino@hotmail.com' application
    * I accept their application
    Then the status of 'riderino@hotmail.com' is active

  Scenario: Refuse Customer application
    When I go to the Applications section
    * I filter for pending applications
    * I filter for Customer applications
    * I apply the filters
    * I open the 'kustomer@kustom.com' application
    * I refuse their application
    Then the status of 'kustomer@kustom.com' is refused

  Scenario: Reconsider Rider application
    When I go to the Applications section
    * I filter for refused applications
    * I apply the filters
    * I open the 'another.rider@gmail.com' application
    * I reconsider their application
    Then the status of 'another.rider@gmail.com' is pending