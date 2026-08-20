 VibeTalk 📱

VibeTalk is a real-time video calling Android application built using **Kotlin**, **Android SDK**, and the **ZEGOCLOUD RTC SDK**.

🚀 Features

- Real-time video calling
- Local camera preview
- Remote video stream
- Room-based communication using ZEGOCLOUD
- Runtime camera and microphone permissions
- Event-driven stream and user updates
- ViewModel-based UI state management
- StateFlow for observing call state

 🛠️ Technologies Used

- **Kotlin**
- **Android SDK**
- **ZEGOCLOUD Express SDK**
- **ViewModel**
- **StateFlow**
- **Coroutines**
- **Android Runtime Permissions**

 🏗️ Architecture

The project follows a simple layered approach:

```
UI (MainActivity)
       ↓
CallViewModel
       ↓
RTCManager
       ↓
ZegoExpressEngine
       ↓
ZEGOCLOUD RTC
```

🔐 ZEGOCLOUD Configuration

This project requires a ZEGOCLOUD App ID and App Sign.

**Do not commit your App Sign or other private credentials to GitHub.**

Configure your credentials locally before running the application.

▶️ Getting Started

1. Clone this repository.
2. Open the project in Android Studio.
3. Configure your ZEGOCLOUD credentials locally.
4. Sync the Gradle project.
5. Run the application on an Android device or emulator.
6. Grant camera and microphone permissions.
7. Join the same room from another device to test video calling.

 📚 Learning Project

VibeTalk was built as a practical Android learning project to understand:

- Real-time communication
- ZEGOCLOUD RTC
- Rooms, users, and streams
- SDK callbacks and events
- ViewModel and StateFlow
- Android runtime permissions
- Local and remote video rendering

👨‍💻 Status

🚧 Currently in development

VibeTalk currently supports one-to-one real-time video calling using ZEGOCLOUD RTC.

More features and improvements will be added as development continues.
is this descrption is correct for readme file
