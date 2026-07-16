---
name: jetpack-compose-ui
description: Use when creating, refactoring, or reviewing Jetpack Compose UI, Compose screens, UI state, events, previews, strings, themes, and feature UI architecture in Android/Kotlin projects.
---

# Jetpack Compose UI

Use this skill when building or reviewing Jetpack Compose UI in an Android/Kotlin codebase.

## First Steps

- Inspect the existing feature before editing: package structure, naming, visibility, theme, resources, previews, ViewModel, UiState, and Event conventions.
- Reuse the local architecture already present in the module instead of introducing a new pattern.
- Prefer the smallest correct change that fits the existing design system.
- Check whether the project requires `internal` visibility for feature APIs before exposing anything public.

## Feature Structure

- Keep one primary screen composable per `Screen.kt` file, plus its preview if needed.
- Put reusable or extracted composables in a `core` package inside the same feature, unless the project already uses another local convention.
- Keep ViewModel, UiState, Event, validators, and UI components close to the feature unless they are genuinely reused across multiple features.
- Do not move UI code into a lower-level module unless that module is already configured and intended for UI.
- Avoid creating generic shared components prematurely. Extract only when there is real reuse or when the screen file becomes hard to read.

## Visibility

- Default feature UI APIs to `internal`: screen composables, child composables, ViewModel, UiState, Event, and feature-only helper types.
- Use `private` for helpers only used in a single file.
- Use public visibility only when another module must access the API.

## State And Events

- Model the UI with an immutable `data class` UiState that implements the project `UiState` interface (`presentation.core.mvi.UiState`).
- Use a sealed interface for UI events that implements the project `Event` interface (`presentation.core.mvi.Event`).
- Keep event names action-oriented and user/interface focused, for example `OnUsernameChanged`, `OnSubmitClicked`, `OnBackClicked`.
- Prefer derived state in `UiState` for simple synchronous rules, such as `canSubmit`, `isUrlValid`, or `hasError`.
- Keep business logic out of composables. Composables should render state and emit events.
- Use the project `BaseViewModel<S, E>` (`presentation.core.mvi.BaseViewModel`) for feature ViewModels instead of creating standalone `ViewModel`/`MutableStateFlow` plumbing.
- Pass `viewModelName` (Name of viewmodel), the initial `UiState`, and `Logger` to `BaseViewModel`; handle user actions by overriding `handleEvent(event)` and updating state through `updateState { ... }`.

## Strings And Resources

- Never hardcode user-facing strings in composables.
- Put all labels, placeholders, button text, helper text, errors, and accessibility strings in `res/values/strings.xml`.
- Use `stringResource(...)` from composables.
- Keep preview-only sample data in Kotlin when it is not user-facing UI copy.
- Escape XML-sensitive characters, for example `&amp;` for ampersands in string resources.
- Use consistent resource names scoped by feature, for example `server_configuration_username_label`.

## Composable Design

- A composable should either represent a screen, a section, a row, or a focused reusable UI element.
- Avoid large screen files with many child composables. Split child composables into separate files when the screen has multiple sections.
- Prefer stateless composables that receive explicit values and callbacks.
- Pass `Modifier` to reusable UI components when external layout control is useful.
- Do not pass ViewModels into child composables. Pass `uiState` slices and event lambdas instead.
- Keep layout decisions near the composable that owns the visual structure.

## Compose Performance

- Avoid unnecessary `remember`, `derivedStateOf`, `LaunchedEffect`, `rememberCoroutineScope`, `useMemo`-style patterns, or callback stabilization unless there is an actual need.
- Do not add `remember` around simple values or string resources.
- Use stable immutable data structures and simple parameters where possible.
- Use lazy layouts for long or unbounded lists. Use regular `Column` for short forms and static content.
- Use `key` or stable IDs for dynamic repeated UI when item identity matters.

## Forms And Validation

- Validate text input live when the feature requires immediate feedback.
- Keep validation logic outside the composable, usually in a validator object, UiState derived property, or ViewModel depending on complexity.
- Show errors only when they are actionable and contextual.
- Use correct keyboard options for input types: URI, email, password, number, capitalization disabled when appropriate.
- Keep submit button enabled state derived from UiState rather than duplicated in the composable.

## Theming And Styling

- Use `MaterialTheme.colorScheme`, `MaterialTheme.typography`, and the project theme tokens.
- Do not introduce one-off colors, typography, or dimensions unless the existing design system has no suitable option.
- Preserve the existing visual language of the app.
- Prefer Material components already used in the project.
- Avoid over-styled generic UI that does not match the codebase.

## Previews

- Add previews for new screens or reusable visual components when the project already uses previews.
- Use `@PreviewLightDark` for previews so light and dark themes are covered.
- Wrap previews in the project theme.
- Keep preview sample data realistic and minimal.
- Previews are the exception to the one-composable-per-file rule when they preview the composable in that same file.

## Accessibility

- Add meaningful content descriptions for icons that convey information or trigger actions.
- Use `null` content descriptions for purely decorative icons.
- Ensure touch targets are large enough for interactive elements.
- Do not rely on color alone to communicate errors or warnings.

## Navigation And Side Effects

- Keep navigation and one-off side effects out of pure UI components when possible.
- Emit events from composables and let the ViewModel or caller decide what happens.
- Use `LaunchedEffect` only for actual composition-driven side effects.
- Key effects carefully; avoid `LaunchedEffect(Unit)` unless it is intentionally run once for that composable instance.

## Verification

- Build the smallest affected module first, for example `./gradlew :presentation:build`.
- Build the app when Gradle wiring or public API usage may be affected, for example `./gradlew :app:assembleDebug`.
- Search for hardcoded UI strings in the new feature before finishing.
- Check that no unintended files outside the requested scope were modified.

## Review Checklist

- The UI follows the existing package and architecture conventions.
- User-facing strings are in resources.
- Feature-only APIs are `internal` or `private`.
- Feature `UiState` and `Event` types implement their corresponding project MVI interfaces.
- Each file has at most one production composable, except previews.
- Child composables live in the feature-local `core` package when extracted.
- Composables are stateless where practical.
- UiState owns derived display/validation state.
- The screen is previewable and themed.
- Previews use `@PreviewLightDark`.
- The affected Gradle tasks pass.
