Feature: Check Riders' progress

  Scenario: Check Riders' progress on concurrent deliveries as an Admin
    Given the following accounts exist:
      | email | role | state |
      | jacoco@mail.com | admin | active |
    And I am logged in as 'jacoco@mail.com'
    When I go to the Monitor section
    Then graphs of the current metrics is shown