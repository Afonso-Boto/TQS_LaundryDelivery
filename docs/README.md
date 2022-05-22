# Documentation

Documents detailing the project's various aspects, including the development workflow followed.

# Development conduct for QA

*Also detailed in the [QA Manual](qa_manual.pdf).*

The development of the project's increments followed a defined conduct in order to maintain code quality throughout the development cycle.
Below are the main points that were defined:
- Develop each feature (user story) on a separate branch, which will be later merged back into the development branch (`dev`)
    - The first commits to these feature branches should only contain tests, incorporating the acceptance criteria defined in the respective user story. At maximum, the necessary project classes to use in the tests should only include the basic interface, without any implementation. Therefore, **the necessary files in these commits are**:
        - One Cucumber feature file incorporating the user story details in the project's test package directory, in a `cucumber` folder
        - Any Java unit and integration tests for the classes involved in the realization of the user story
    - The merges to the development branch are done using Pull Requests (PRs), which will run an automated QA analysis to determine whether the increment meets the quality requirements. In case the analysis fails, the code should be fixed to pass the quality gates defined. The code will also be manually reviewed by the Product Owner
- The end of every development iteration/sprint should have a Pull Request from the development branch (`dev`) into the main branch (`main`), which will also run a QA analysis
