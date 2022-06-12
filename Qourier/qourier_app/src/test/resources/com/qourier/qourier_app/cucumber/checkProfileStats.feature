Feature: Check profile and stats (Rider and Customer)

  Scenario Outline: See the profile's status as a newly registered User
    Given I am in the Login page
    When I go to register myself as a <role>
    * I fill the registration details
    * I register
    And I go to the Profile section
    Then my status is pending
    * the details are the same as the ones in the registration form
    * there are not statistics

    Examples:
      | role |
      | Rider |
      | Customer |

  Scenario Outline: See the profile's status as a refused User
    Given I am logged in as a <role>
    And my application has been refused
    When I go to the Profile section
    Then my status is refused
    And there are not statistics

    Examples:
      | role |
      | Rider |
      | Customer |

  Scenario Outline: See the profile's status as an active User
    Given I am logged in as a <role>
    * I have already been accepted to the platform
    * my account is not suspended
    When I go to the Profile section
    Then my status is active
    And there are statistics

    Examples:
      | role |
      | Rider |
      | Customer |

  Scenario Outline: See the profile's status as a suspended User
    Given I am logged in as a <role>
    * I have already been accepted to the platform
    * my account is suspended
    When I go to the Profile section
    Then my status is suspended
    And there are statistics

    Examples:
      | role |
      | Rider |
      | Customer |