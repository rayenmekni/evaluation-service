# Implementation Plan: CI/CD Pipeline Integration

## Overview

This implementation plan breaks down the CI/CD pipeline integration into discrete, sequential tasks. The implementation follows a phased approach: Maven configuration → Dockerfile creation → GitHub Actions workflow → integration and verification. Each task builds on previous work and includes specific file modifications with clear acceptance criteria.

## Tasks

- [x] 1. Configure Maven build with JaCoCo plugin
  - Modify `pom.xml` to add JaCoCo plugin version 0.8.11
  - Configure three executions: prepare-agent, report, and check
  - Set coverage thresholds: 80% line coverage, 70% branch coverage
  - Configure report generation in `target/site/jacoco/` directory
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 11.1, 11.2, 11.3_

- [ ]* 1.1 Write unit tests to verify JaCoCo configuration
  - Test that JaCoCo plugin is present in pom.xml
  - Test that coverage thresholds are correctly configured
  - Verify report output directory configuration
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 2. Configure Maven build with SonarQube plugin
  - Add SonarQube Maven plugin version 3.10.0.2594 to `pom.xml`
  - Add SonarQube properties to `<properties>` section
  - Configure `sonar.coverage.jacoco.xmlReportPaths` to point to JaCoCo XML report
  - Set `sonar.java.source` to 21
  - _Requirements: 4.1, 4.2, 4.7, 12.7_

- [x] 3. Test Maven configuration locally
  - Run `mvn clean verify` to ensure build completes successfully
  - Verify JaCoCo reports are generated in `target/site/jacoco/`
  - Check that coverage thresholds are enforced (build should fail if coverage is low)
  - Verify XML report exists for SonarQube integration
  - _Requirements: 1.2, 1.3, 1.6_

- [ ] 4. Create multi-stage Dockerfile
  - [x] 4.1 Create Dockerfile in project root
    - Implement Stage 1 (builder): Use eclipse-temurin:21-jdk-alpine base image
    - Copy Maven wrapper and pom.xml first for dependency caching
    - Run `./mvnw dependency:go-offline -B` to download dependencies
    - Copy source code and build with `./mvnw clean package -DskipTests -B`
    - _Requirements: 5.1, 5.2, 13.1, 13.2, 13.5_

  - [x] 4.2 Implement runtime stage in Dockerfile
    - Use eclipse-temurin:21-jre-alpine as base image
    - Create non-root user (spring:spring)
    - Copy JAR from builder stage
    - Set ownership to non-root user and switch to that user
    - _Requirements: 5.2, 5.3, 5.6, 13.3_

  - [x] 4.3 Configure Dockerfile runtime settings
    - Expose port 8089
    - Add HEALTHCHECK with 30s interval pointing to /actuator/health
    - Define environment variables: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD, EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE, SERVER_PORT
    - Set ENTRYPOINT to run the JAR file
    - _Requirements: 5.4, 5.5, 5.7, 9.2, 9.5_

- [ ]* 4.4 Write Docker container integration tests
  - Test that Docker image builds successfully
  - Test that container starts and listens on port 8089
  - Test that health check endpoint returns 200 OK
  - Test that container runs as non-root user
  - Test that environment variables override defaults
  - _Requirements: 5.1, 5.4, 5.5, 5.6_

- [x] 5. Test Dockerfile locally
  - Build Docker image with `docker build -t evaluation-service:local .`
  - Verify image size is less than 300MB
  - Run container locally and verify application starts
  - Test health check endpoint with curl or wget
  - Verify non-root user with `docker exec <container> whoami`
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 13.4_

- [-] 6. Checkpoint - Verify local build and Docker setup
  - Ensure `mvn clean verify` passes with coverage reports
  - Ensure Docker image builds and runs successfully
  - Ensure all tests pass, ask the user if questions arise

- [ ] 7. Create GitHub Actions workflow file
  - [~] 7.1 Create workflow file structure
    - Create `.github/workflows/ci-cd.yml` file
    - Define workflow name: "CI/CD Pipeline"
    - Configure triggers: push to all branches, pull_request to all branches
    - Define environment variables: JAVA_VERSION=21, MAVEN_VERSION=3.9.6, DOCKER_IMAGE_NAME
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

  - [~] 7.2 Implement build job
    - Create job named "build" running on ubuntu-latest
    - Add step to checkout code with actions/checkout@v4
    - Add step to set up JDK 21 with actions/setup-java@v4 (temurin distribution, Maven cache enabled)
    - Add step to build with Maven: `mvn clean compile -DskipTests`
    - Add step to upload build artifacts with actions/upload-artifact@v4 (30-day retention)
    - _Requirements: 2.5, 2.6, 8.1, 8.4, 8.5, 14.1_

  - [~] 7.3 Implement test job
    - Create job named "test" with dependency on build job (needs: build)
    - Add steps to checkout code and set up JDK 21 with Maven cache
    - Add step to run tests with coverage: `mvn verify`
    - Add step to upload test reports (always run, even on failure)
    - Add step to upload coverage reports as artifacts
    - Add step to generate test summary in $GITHUB_STEP_SUMMARY
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 8.2, 8.3_

  - [~] 7.4 Implement quality-analysis job
    - Create job named "quality-analysis" with dependency on test job
    - Add condition: only run on pull_request or push to main branch
    - Add step to checkout code with fetch-depth: 0 for better analysis
    - Add step to set up JDK 21 with Maven cache
    - Add step to cache SonarQube packages with actions/cache@v3
    - Add step to run SonarQube analysis with Maven (use SONAR_TOKEN and SONAR_HOST_URL secrets)
    - Add step to check quality gate with sonarsource/sonarqube-quality-gate-action@master (5-minute timeout)
    - _Requirements: 4.2, 4.3, 4.4, 4.5, 4.6, 9.1, 14.3_

  - [~] 7.5 Implement docker-build job
    - Create job named "docker-build" with dependency on quality-analysis job
    - Add condition: only run on push to main branch
    - Add steps to checkout code and set up JDK 21
    - Add step to build JAR: `mvn clean package -DskipTests`
    - Add step to set up Docker Buildx with docker/setup-buildx-action@v3
    - Add step to log in to Docker Hub with docker/login-action@v3 (use DOCKER_USERNAME and DOCKER_PASSWORD secrets)
    - Add step to extract metadata with docker/metadata-action@v5 (tags: sha with branch prefix, latest for main, ref for tags)
    - Add step to build and push Docker image with docker/build-push-action@v5 (enable GitHub Actions cache)
    - Add step to generate Docker summary in $GITHUB_STEP_SUMMARY
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 9.1, 14.2, 15.1, 15.2, 15.3, 15.4, 15.5_

- [ ]* 7.6 Write workflow validation tests
  - Test that workflow YAML is valid and parseable
  - Test that all required jobs are defined (build, test, quality-analysis, docker-build)
  - Test that job dependencies are correctly configured
  - Test that required secrets are referenced in workflow
  - _Requirements: 2.1, 7.1, 7.2, 7.3, 7.4, 7.5, 7.6_

- [~] 8. Configure GitHub repository secrets
  - Create documentation file: `.github/SECRETS.md`
  - Document required secrets: SONAR_TOKEN, SONAR_HOST_URL, DOCKER_USERNAME, DOCKER_PASSWORD
  - Provide instructions for obtaining each secret (SonarQube token generation, Docker Hub access token)
  - Include example values (sanitized) and security best practices
  - Add note about configuring secrets in GitHub repository settings (Settings → Secrets and variables → Actions)
  - _Requirements: 4.4, 6.3, 9.1, 9.4_

- [ ] 9. Update README with CI/CD documentation
  - [~] 9.1 Add CI/CD status badge to README
    - Add GitHub Actions workflow status badge at top of README
    - Use format: `![CI/CD Pipeline](https://github.com/{owner}/{repo}/actions/workflows/ci-cd.yml/badge.svg)`
    - _Requirements: 10.2_

  - [~] 9.2 Add CI/CD Pipeline section to README
    - Document pipeline stages: Build → Test → Quality Analysis → Docker Build
    - Explain trigger conditions for each stage
    - Document required GitHub secrets and link to `.github/SECRETS.md`
    - Add instructions for running pipeline locally (Maven commands, Docker commands)
    - Document Docker image tagging strategy
    - Add troubleshooting section with common issues and solutions
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6_

- [~] 10. Checkpoint - Verify workflow configuration
  - Review all workflow jobs and dependencies
  - Verify all required secrets are documented
  - Ensure README documentation is complete and accurate
  - Ask the user if questions arise before testing the pipeline

- [~] 11. Test pipeline with feature branch
  - Create test feature branch: `test/cicd-pipeline`
  - Push branch to trigger build and test jobs
  - Verify build job completes successfully
  - Verify test job runs and uploads artifacts
  - Check that quality-analysis and docker-build jobs are skipped (not on main branch)
  - Review GitHub Actions UI for job execution and logs
  - _Requirements: 2.2, 2.3, 7.1, 7.2, 7.3, 14.4_

- [~] 12. Test pipeline with pull request
  - Create pull request from test branch to main
  - Verify build, test, and quality-analysis jobs execute
  - Verify docker-build job is skipped (not merged to main yet)
  - Check that SonarQube analysis results are posted
  - Verify quality gate status is displayed in PR checks
  - Review uploaded artifacts (build artifacts, test reports, coverage reports)
  - _Requirements: 2.3, 4.5, 4.6, 7.4, 8.1, 8.2, 8.3, 10.6_

- [~] 13. Test pipeline with main branch push
  - Merge pull request to main branch
  - Verify all four jobs execute: build → test → quality-analysis → docker-build
  - Verify Docker image is built and pushed to Docker Hub
  - Check Docker image tags: latest and main-{commit-sha}
  - Pull Docker image from Docker Hub and verify it runs
  - Verify pipeline completes in under 5 minutes
  - _Requirements: 2.4, 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 14.4, 15.1, 15.2, 15.3_

- [ ]* 13.1 Write end-to-end integration tests
  - Test complete pipeline execution on sample commit
  - Test that Docker image is pullable from registry
  - Test that SonarQube project has analysis results
  - Test that coverage reports are uploaded as artifacts
  - Test that pipeline fails appropriately on test failures
  - Test that pipeline fails on quality gate failures
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 4.5, 6.1, 6.4_

- [~] 14. Verify error handling and failure scenarios
  - Test pipeline behavior when tests fail (should stop at test job)
  - Test pipeline behavior when coverage is below threshold (should fail at test job)
  - Test pipeline behavior when quality gate fails (should stop at quality-analysis job)
  - Verify error messages are clear and actionable
  - Verify commit status is updated correctly on failures
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 10.1, 10.3_

- [~] 15. Final checkpoint and documentation review
  - Verify all requirements are implemented and tested
  - Review all documentation (README, SECRETS.md) for accuracy
  - Verify Docker image size is under 300MB
  - Verify pipeline performance meets 5-minute target
  - Ensure all artifacts are properly uploaded and retained
  - Confirm status badge displays correctly in README
  - Ask the user for final review and approval

## Notes

- Tasks marked with `*` are optional testing tasks and can be skipped for faster implementation
- Each task references specific requirements for traceability
- Checkpoints ensure validation at key milestones
- Local testing (tasks 3, 5) should be completed before pushing to GitHub
- GitHub secrets must be configured before testing the pipeline (task 8)
- Pipeline testing follows a progressive approach: feature branch → pull request → main branch
- All configuration files (pom.xml, Dockerfile, workflow YAML) should be validated locally before committing

## Implementation Sequence

1. **Phase 1 (Tasks 1-3)**: Maven configuration and local testing
2. **Phase 2 (Tasks 4-6)**: Dockerfile creation and local Docker testing
3. **Phase 3 (Tasks 7-10)**: GitHub Actions workflow and documentation
4. **Phase 4 (Tasks 11-15)**: Pipeline testing and verification

## Success Criteria

- ✓ Maven build generates JaCoCo coverage reports
- ✓ Coverage thresholds (80% line, 70% branch) are enforced
- ✓ Docker image builds successfully and is under 300MB
- ✓ GitHub Actions workflow executes all jobs in correct order
- ✓ SonarQube analysis runs and quality gate is enforced
- ✓ Docker image is pushed to Docker Hub with correct tags
- ✓ Pipeline completes in under 5 minutes
- ✓ Status badge displays in README
- ✓ All documentation is complete and accurate
