<div align="center">

# SplitCrew

**Split bills fairly. Settle up simply.**

An Android expense splitter for friends, flatmates, and travel groups, built with Kotlin and Jetpack Compose.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Room-SQLite-0FA3A3)
![Min SDK](https://img.shields.io/badge/minSdk-26-2BB673)

</div>

---

## Overview

SplitCrew tracks shared expenses within a group and works out exactly who owes whom. Each expense is shared only among the members who were active when it was recorded, so removing or adding someone never rewrites past splits.

Balances are calculated in exact integer cents, leftover cents are distributed fairly, and the settle-up plan uses the fewest possible payments. Confirmed payments are locked in history, so they cannot be quietly changed.

## Screenshots

<table>
  <tr>
    <td style="text-align:center; padding:8px;"><p><b>Welcome-screen</b></p><img src="Assets/Images/Welcome-screen.png" alt="Welcome-screen" style="max-width:220px;"></td>
    <td style="text-align:center; padding:8px;"><p><b>Groups-screen</b></p><img src="Assets/Images/Groups-screen.png" alt="Groups-screen" style="max-width:220px;"></td>
    <td style="text-align:center; padding:8px;"><p><b>Group-screen</b></p><img src="Assets/Images/Group-screen.png" alt="Group-screen" style="max-width:220px;"></td>
  </tr>
  <tr>
    <td style="text-align:center; padding:8px;"><p><b>Members-screen</b></p><img src="Assets/Images/Members-screen.png" alt="Members-screen" style="max-width:220px;"></td>
    <td style="text-align:center; padding:8px;"><p><b>Member-info-screen</b></p><img src="Assets/Images/Member-info-screen.png" alt="Member-info-screen" style="max-width:220px;"></td>
    <td style="text-align:center; padding:8px;"><p><b>Expenses-screen</b></p><img src="Assets/Images/Expenses-screen.png" alt="Expenses-screen" style="max-width:220px;"></td>
  </tr>
  <tr>
    <td style="text-align:center; padding:8px;"><p><b>Settlement-screen</b></p><img src="Assets/Images/Settlement-screen.png" alt="Settlement-screen" style="max-width:220px;"></td>
    <td style="text-align:center; padding:8px;"><p><b>History-screen</b></p><img src="Assets/Images/History-screen.png" alt="History-screen" style="max-width:220px;"></td>
    <td style="text-align:center; padding:8px;"><p><b>Database</b></p><img src="Assets/Images/Database.png" alt="Database" style="max-width:220px;"></td>
  </tr>
</table>

## Features

- **Groups:** create, rename, and delete groups
- **Members:** add, rename, remove, and restore members with their original balances
- **Expenses:** add, edit, and delete expenses with a payer and equal split
- **Balances:** exact cent-level accuracy with fair distribution of leftover cents
- **Settle up:** the fewest payments needed to clear every balance
- **History:** record payments as Paid, lock them with Done, or Undo while pending
- **Member info:** each member's expenses, presence, and remaining settlement
- **Experience:** light and dark themes, animated splash screen, rotation-safe navigation

## Architecture

SplitCrew follows MVVM with a clear separation of layers:

- **UI layer:** Jetpack Compose screens observe state from the ViewModel
- **ViewModel:** holds screen state and exposes data as `StateFlow`
- **Repository:** single access point to the database
- **Data layer:** Room entities, DAO, and database
- **Domain layer:** pure Kotlin balance and settlement logic, independent of Android

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, Repository pattern |
| Persistence | Room (SQLite) |
| Async and state | Kotlin Coroutines, Flow, StateFlow |
| Testing | JUnit |

## Project Structure

- `app/src/main/java/com/dhirajmane/splitcrew/`
  - `MainActivity.kt`: entry point and screen navigation
  - `AppContainer.kt`: manual dependency setup
  - `data/`: Room entities, DAO, and repository
  - `domain/`: balance and settlement calculations
  - `ui/groups/`: Compose screens and ViewModel
  - `ui/theme/`: colors, typography, and shared components
- `app/src/test/`: unit tests for balance and settlement logic
- `Assets/Images/`: screenshots used in this README

## Getting Started

1. Clone the repository
2. Open the project in Android Studio (Ladybug or newer)
3. Run the app on an emulator or a device running Android 8.0 (API 26) or higher

## Author

**Dhiraj Mane**
GitHub: [@Dhiraj1574](https://github.com/Dhiraj1574)

---

<div align="center">
If you found this project useful, consider giving it a star.
</div>