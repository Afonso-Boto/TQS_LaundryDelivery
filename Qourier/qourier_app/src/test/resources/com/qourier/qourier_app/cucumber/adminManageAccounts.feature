Feature: Manage accounts of Riders/Customers

  Background:
    Given the following accounts exist:
      | email | role | state |
      | jacoco@mail.com | admin | active |
      | riderino@hotmail.com | rider | active |
      | kustomer@kustom.com  | customer | suspended |
      | another.rider@gmail.com | rider | suspended |
      | another.customer@mail.com | customer | active |
    And I am logged in as 'jacoco@mail.com'

  Scenario: Suspend Rider account
    When I go to the Accounts section
    * I filter for active accounts
    * I apply the filters
    * I go to the 'riderino@hotmail.com' profile
    * I suspend their account
    Then the status of 'riderino@hotmail.com' on the profile is suspended
    And I can activate their account

  Scenario: Activate Customer account
    When I go to the Accounts section
    * I filter for suspended accounts
    * I filter for Customer accounts
    * I apply the filters
    * I go to the 'kustomer@kustom.com' profile
    * I activate their account
    Then the status of 'kustomer@kustom.com' on the profile is active
    And I can suspend their account

  Scenario: Check Customer profile
    When I go to the Accounts section
    * I filter for active accounts
    * I filter for Customer accounts
    * I apply the filters
    * I go to the 'another.customer@mail.com' profile
    Then I can see the profile of 'another.customer@mail.com' with all details inputted in their registration
    And I can see statistics about the number of deliveries requested

  Scenario: Check Rider profile
    When I go to the Accounts section
    And I go to the 'another.rider@gmail.com' profile
    Then I can see the profile of 'another.rider@gmail.com' with all details inputted in their registration
    And I can see statistics about the number of deliveries done