---
name: port-inspections
description: Principles for porting a group of IntelliJ inspections to detekt rules. Invoke when the user asks to port, add or review the rules of an inspection group, or to decide which inspections to port, keep inactive or drop. Use it together with the code-conventions skill.
---

# Port inspections

Principles for turning a group of IntelliJ inspections into detekt rules. They are judgment aids,
not a checklist. If a principle does not fit a case, do what serves a detekt user in CI, and tell
the user why. For code, tests and Gradle, obey the code-conventions skill.

## IDE inspection versus lint rule

An inspection shows a hint next to the caret, and the developer can ignore it. A detekt rule adds a
finding to a report or fails a build. Keep this difference in mind for each decision below.

## Start from upstream

- Read the registration, the implementation and the test data of each inspection in
  intellij-community. The test data shows what the rule must report and what it must not report.
- Read the defaults from the registration: `enabledByDefault` and `level`. Read them from the
  pinned upstream version, so that a later sync can find drift.
- Inspections written for Kotlin, or once for Java and Kotlin (UAST), port best. Java-only
  inspections and inspections for other languages or file formats usually do not port.

## Give each inspection a result

Record each result with a short reason, where the project keeps inspection status and in the README:

- **Ported:** a rule exists. If the rule covers only part of the inspection, write the gap in the
  PR body.
- **Covered:** the Kotlin compiler, detekt core or ktlint already reports it. Do not add a
  duplicate, because a user then gets two findings for one problem.
- **Not portable:** name the blocker. Frequent blockers are a whole-project search (detekt sees one
  file at a time), IDE state (test results, source roots, module structure), IntelliJ's data flow
  engine, a compiler flag or an experimental language feature, and external data such as JDK API
  versions.
- **Not useful as a lint rule:** see "Question the new rules".

A partial port is better than no port when the missing part needs data that detekt does not have.
Limit the rule to the cases that it can prove, for example calls in the same file.

## Default state

- If upstream turns an inspection off by default, make its rule inactive by default. Do not make
  an exception because a rule seems valuable. Propose the exception to the user instead.
- Set the default in the config that each rule set jar ships, and keep the project's own detekt
  config in step with it.
- An inspection at INFORMATION level is on upstream, but it highlights nothing. Treat it as a
  candidate to be inactive or to drop, and tell the user.

## Opposite rules

- Some inspections ask for opposite changes, for example explicit and implicit `this`. Mark each
  such pair with the opposite-rule annotation and say "Opposite of" in each rule's description.
- Make exactly one rule of a pair active by default: the one that is more idiomatic in Kotlin. If
  the choice is not clear, choose one, give the reason, and let the user confirm.
- The annotation can only name a rule in the same module. Document a pair across modules in the
  README only.
- Look for opposites in the rules of earlier groups too, not only in the new group.

## Question the new rules

After the port, read the new rules again as a detekt user would, and find the rules that do not
make much sense as lint rules. Typical signs:

- It only suggests a refactoring or a migration ("can be converted to ...").
- Upstream it only changes how the text looks, and it is not a warning.
- It uses a fuzzy heuristic that is noisy in a report, or it needs other files to be accurate.
- It flags deliberate code as often as it flags mistakes.
- It duplicates a rule of detekt core.

Give the user a short list with a recommendation for each rule (drop, inactive, or keep) and let
the user decide. Keep a rule that finds real bugs, also when it is niche. A rule costs nothing when
nobody uses the API it checks.

## Fit the rest of the project

- A new group gets its rule set modules, a shipped default config, and a place in its bundle. Make
  sure that the bundle includes the new rule sets.
- Keep the README current: the rule tables, the "inactive by default" marks, the opposite pairs,
  and the inspections that are not ported, with their reasons.
- Turn the new rules on in the project's own detekt config where they fit, and fix what they find.
- If an upstream option name does not match the project's style, rename it. Write the upstream
  name in the PR body.

## Report

In the PR body and in the reply to the user, give the counts and names of the ported, covered and
not ported inspections, the gaps of partial ports, the rules that are inactive by default, and the
opposite pairs with the active rule of each.
