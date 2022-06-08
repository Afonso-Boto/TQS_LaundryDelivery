Feature: Make order from customer

  Scenario: Make Order
    When I navigate to "http://localhost:81/service"
    And I click the "Wash" option
    Then I select the "Camisa" for the type
    And I select the "Escuras" for the color
    And I select "10" as the number
    Then I click the "Add" button
    Then I should see the "Camisa" in the table
    And I should see the "Escuras" in the table
    And I should see "10" in the table
    Then I select the "Casaco" for the typeCucum
    And I select the "Claras" for the color
    And I select "2" as the number
    Then I click the "Add" button
    Then I should see the "Casaco" in the table
    And I should see the "Claras" in the table
    And I should see "2" in the table
    Then I click the "Make Order" button
