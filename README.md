# SplitCrew

### Split bills fairly. Settle up simply.

A modern Android group expense-splitting application that makes managing shared expenses, tracking balances, and settling payments simple, transparent, and reliable.

---

## Badges

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Room-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-26%20(Android%208.0)-green?style=for-the-badge)

---

## Overview

**SplitCrew** is an Android application designed to simplify group expense management with accurate calculations and reliable settlement tracking.

Each expense is shared only among the members who were active in the group when that expense was recorded. Balances are calculated using exact integer cents, with fair distribution of any leftover cents to prevent rounding errors. The settlement algorithm generates a plan using the fewest possible payments, reducing unnecessary transactions between members. Once a payment is confirmed, it is locked into the payment history to preserve an accurate record of completed settlements.

---

## Screenshots

| Welcome Screen | Groups Screen | Group Screen |
|:---:|:---:|:---:|
| **Welcome-screen** | **Groups-screen** | **Group-screen** |
| ![Welcome Screen](Assets/Images/Welcome-screen.png) | ![Groups Screen](Assets/Images/Groups-screen.png) | ![Group Screen](Assets/Images/Group-screen.png) |
| **Members-screen** | **Member-info-screen** | **Expenses-screen** |
| ![Members Screen](Assets/Images/Members-screen.png) | ![Member Info Screen](Assets/Images/Member-info-screen.png) | ![Expenses Screen](Assets/Images/Expenses-screen.png) |
| **Settlement-screen** | **History-screen** | **Database** |
| ![Settlement Screen](Assets/Images/Settlement-screen.png) | ![History Screen](Assets/Images/History-screen.png) | ![Database](Assets/Images/Database.png) |

---

## Features

### Group Management
- Create and manage multiple expense-sharing groups.
- Organize members and expenses separately for each group.

### Member Management
- Add new members to a group.
- Rename existing members.
- Remove members without losing historical expense records.
- Restore previously removed members.

### Expense Management
- Add new group expenses.
- Edit existing expenses.
- Delete expenses when necessary.
- Maintain expense participation based on members active when the expense was recorded.

### Accurate Balance Calculation
- Integer-based monetary calculations for precision.
- Fair leftover-cent distribution.
- Accurate individual balances without floating-point rounding errors.

### Smart Settlement
- Automatically calculate who owes whom.
- Generate an optimized settlement plan using the fewest possible payments.
- Simplify transactions between group members.

### Payment History
- Track completed settlements.
- Confirm payments using the **Done** action.
- Reverse a confirmation using **Undo**.
- Preserve confirmed payments in the settlement history.

### Additional Features
- Detailed member information.
- Light and dark theme support.
- Custom splash screen.
- Persistent local database storage.

---

## Architecture

SplitCrew follows the **MVVM (Model-View-ViewModel)** architecture with a Repository layer to maintain separation of concerns, improve maintainability, and support testable business logic.

### Architecture Components

| Component | Responsibility |
|---|---|
| UI Layer | Jetpack Compose screens and user interactions |
| ViewModel | Manages UI state and application logic |
| Repository | Acts as an abstraction between data sources and the domain layer |
| Data Layer | Handles local data operations using Room |
| Domain Layer | Contains pure Kotlin business rules and expense calculations |
| StateFlow | Provides reactive and lifecycle-aware UI state updates |
| Room Database | Provides persistent local storage |

### Architecture Flow

```text
        Jetpack Compose UI
                |
                v
            ViewModel
                |
                v
            Repository
                |
          +-----+-----+
          |           |
          v           v
       Room DB    Domain Layer
                     |
                     v
              Business Logic
          (Balances & Settlement)
```

The domain layer uses pure Kotlin logic, keeping critical financial calculations independent of Android framework components.

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM + Repository Pattern |
| Persistence | Room Database |
| Asynchronous Programming | Kotlin Coroutines & StateFlow |
| Testing | JUnit and Kotlin Unit Testing |

---

## Project Structure

The project follows a modular package organization that separates data handling, business logic, and UI components.

```text
SplitCrew/
│
├── app/
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/
│       │           └── dhirajmane/
│       │               └── splitcrew/
│       │                   │
│       │                   ├── MainActivity.kt
│       │                   ├── AppContainer.kt
│       │                   │
│       │                   ├── data/
│       │                   │   ├── database/
│       │                   │   ├── dao/
│       │                   │   ├── entity/
│       │                   │   └── repository/
│       │                   │
│       │                   ├── domain/
│       │                   │   ├── model/
│       │                   │   ├── usecase/
│       │                   │   └── settlement/
│       │                   │
│       │                   └── ui/
│       │                       ├── groups/
│       │                       └── theme/
│       │
│       └── test/
│
├── Assets/
│   └── Images/
│       ├── Welcome-screen.png
│       ├── Groups-screen.png
│       ├── Group-screen.png
│       ├── Members-screen.png
│       ├── Member-info-screen.png
│       ├── Expenses-screen.png
│       ├── Settlement-screen.png
│       ├── History-screen.png
│       └── Database.png
│
└── README.md
```

---

## Getting Started

Follow these steps to run SplitCrew locally.

### Prerequisites

- Android Studio (latest stable version recommended).
- Android SDK.
- Android device or emulator running Android 8.0 (API 26) or higher.
- Git installed on your system.

### 1. Clone the Repository

```bash
git clone https://github.com/Dhiraj1574/SplitCrew.git
```

### 2. Open in Android Studio

1. Launch Android Studio.
2. Select **Open**.
3. Navigate to the cloned `SplitCrew` project directory.
4. Allow Gradle to sync and download the required dependencies.

### 3. Run the Application

1. Connect an Android device or start an emulator.
2. Ensure the device runs Android 8.0 or higher.
3. Select the `app` configuration.
4. Click **Run** to build and launch SplitCrew.

---

## Author

**Dhiraj Mane**

Android Developer | Kotlin | Jetpack Compose

- GitHub: [@Dhiraj1574](https://github.com/Dhiraj1574)

---

*SplitCrew — Split bills fairly. Settle up simply.*