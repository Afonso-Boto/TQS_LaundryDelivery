Feature: Rider | Check profile and stats

  Scenario: See the profile's status as a newly registered Rider
    Given I am in the Login page
    When I go to Register myself as a Rider
    * I fill the Rider registration details
    * I register as a Rider
    Then my status is 'pending'
    * the details are the same as the ones in the registration form
    * there are no statistics

  Scenario: See the profile's status as a refused Rider
    Given I am logged in as a Rider
    And my application has been refused
    When I go to the Profile section
    Then my status is 'refused'
    And there are no statistics

  Scenario: See the profile's status as an active Rider
    Given I am logged in as a Rider
    * I have already been accepted to the platform
    * my account is not suspended
    When I go to the Profile section
    Then my status is 'active'
    And there are statistics

  Scenario: See the profile's status as a suspended Rider
    Given I am logged in as a Rider
    * I have already been accepted to the platform
    * my account is suspended
    When I go to the Profile section
    Then my status is 'suspended'
    And there are statistics