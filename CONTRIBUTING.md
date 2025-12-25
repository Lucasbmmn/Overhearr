# Contributing to Overhearr

Thanks for your interest in contributing! here is everything you need to know to make your first contribution.

## Development Setup

### Prerequisites
* Java JDK 25
* Node.js 24
* PostgreSQL 17 (Or use the provided Docker container)

### Getting started
1. Fork the repository on GitHub.
2. Clone your fork locally:
    ```bash
    git clone https://github.com/yourusername/projectname.git
    cd projectname/
    ```
3. Add the remote upstream:
    ```bash
    git remote add upstream https://github.com/lucasbmmn/overhearr.git
    ```
4. Create a new branch:
    ```bash
    git checkout -b BRANCH-NAME main
    ```
6. Install Dependencies:
* Backend:
  ```bash
  ./gradlew build
  ```
* Frontend:
  ```bash
  cd frontend && npm install
  ```

## How to Contribute

### Reporting Bugs
* **Search Existing Issues:** Check if the issue has already been reported.
* **Use the Template:** Open a [Bug Report](https://github.com/lucasbmmn/overhearr/issues/new?template=bug_report.yml) and provide logs, reproduction steps, and screenshots.

### Suggesting Enhancements
* Open a [Feature Request](https://github.com/lucasbmmn/overhearr/issues/new?template=feature_request.yml).
* Explain *why* this feature would be useful to other users.

### Pull Requests
1. Ensure your code passes local tests.
2. Update the `README.md` if you change configuration or architecture.
3. Open the PR against the `main` branch.
4. Link the Issue your PR fixes (e.g., `Fixes #123`).

## Style Guide

### Backend (Java)
* We follow **Standard Spring Boot** coding conventions.
* **Naming:** Use `PascalCase` for classes and `camelCase` for methods/variables.
* **Controller/Service:** Keep Controllers thin; business logic belongs in Services.

### Frontend (React/TypeScript)
* **Components:** Functional components with Hooks.
* **Styling:** Use **Tailwind CSS 4** utility classes. Avoid custom CSS files where possible.
* **Linting:** Run `npm run lint` before committing to ensure no ESLint errors.

### Commit Messages
We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

* `feat: add spotify search integration`
* `fix: resolve login cookie issue`
* `docs: update readme with docker instructions`
* `chore: bump spring boot version`
