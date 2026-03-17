---
name: review-pr
description: This skill should be used when the user asks to "review a PR", "revisar un PR", "check pull request", "analizar pull request", "review pull request", or mentions code review of a GitHub PR. Reviews code changes for quality, bugs, security issues, and adherence to project conventions.
version: 1.0.0
---

# PR Review Skill

This skill provides a structured approach to reviewing Pull Requests in this project.

## When This Skill Applies

Activate when the user wants to:
- Review an open or recently merged PR
- Analyze code changes in a pull request
- Get feedback on a PR before merging
- Check if a PR follows project conventions

## Review Process

### 1. Gather PR Information

Use `gh` CLI to fetch PR details:

```bash
gh pr view <PR_NUMBER>                    # PR description and metadata
gh pr diff <PR_NUMBER>                    # Full diff
gh pr checks <PR_NUMBER>                  # CI status
gh pr review <PR_NUMBER> --comments       # Existing review comments
```

### 2. Review Checklist

#### Code Quality
- [ ] Logic is correct and handles edge cases
- [ ] No unnecessary complexity (YAGNI)
- [ ] No duplicated code
- [ ] Methods/classes have a single responsibility

#### Java / Spring Boot Conventions (this project)
- [ ] `final` keyword used on all parameters and local variables in concrete classes
- [ ] Spring AI advisors wired correctly if touching `MyAdoptionDog.java`
- [ ] Kafka producer flushes/closes gracefully if modified
- [ ] Vector store dimensions remain 1536 if embedding config changed

#### Security
- [ ] No credentials or secrets hardcoded
- [ ] No `.env` files committed
- [ ] Input validation at system boundaries (user input, external APIs)
- [ ] No SQL injection, XSS, or command injection risks

#### Tests
- [ ] New functionality has corresponding unit tests
- [ ] Tests do not use mocks for the database (use real DB — past incident with mock/prod divergence)
- [ ] Tests cover happy path and error cases

#### Documentation
- [ ] CLAUDE.md updated if architecture or commands changed
- [ ] Bruno collection updated if API endpoints changed (`bruno/MyBotDog/`)

### 3. Provide Structured Feedback

Organize feedback by severity:

- **Blocker**: Must fix before merge (security issue, broken logic, missing tests)
- **Major**: Should fix (convention violations, code quality issues)
- **Minor**: Nice to have (style suggestions, small improvements)
- **Praise**: Highlight what was done well

### 4. Post Review (if requested)

```bash
gh pr review <PR_NUMBER> --approve -b "LGTM message"
gh pr review <PR_NUMBER> --request-changes -b "Feedback here"
gh pr review <PR_NUMBER> --comment -b "General comment"
```

## Example Usage

> "Revisa el PR #42"
> "Review PR number 15 before I merge"
> "Check if the latest PR follows our conventions"
