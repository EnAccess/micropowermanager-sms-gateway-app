# SMS Gateway

A bidirectional SMS communication Android application that enables seamless SMS handling and integration with Firebase Cloud Messaging.

## Overview

SMS Gateway is an Android application that facilitates bidirectional SMS communication between mobile devices and a server. The app can:
- Receive and process incoming SMS messages
- Send SMS messages based on Firebase Cloud Messaging (FCM) requests
- Monitor SIM card state changes
- Provide a dashboard interface for monitoring SMS operations

## Technology Stack

- **Primary Language**: Kotlin
- **Minimum SDK**: Android 5.0 (API Level 21)
- **Target SDK**: Android 10 (API Level 29)

## Features

- **Bidirectional SMS Communication**: Handle both incoming and outgoing SMS messages
- **Firebase Integration**: Uses Firebase Cloud Messaging for remote message handling
- **SIM Card Monitoring**: Tracks SIM card state changes
- **Dashboard Interface**: User-friendly interface for monitoring SMS operations
- **Secure Communication**: Implements secure communication protocols for message handling

## Technical Details

### Requirements

- Android 5.0 (API Level 21) or higher
- Google Play Services
- Active SIM card with SMS capabilities

### Permissions

The application requires the following permissions:
- `INTERNET`: For network communication
- `SEND_SMS`: To send SMS messages
- `RECEIVE_SMS`: To receive SMS messages
- `READ_SMS`: To process SMS messages
- `READ_PHONE_STATE`: To monitor SIM card state

### Dependencies

- Firebase Core & Messaging, Config, and Crashlytics
- Retrofit for API communication
- OkHttp for HTTP client
- Gson for JSON parsing

## Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build the project

## Architecture
- Firebase Cloud Messaging Service for push notifications
- Broadcast Receivers for SMS and SIM state monitoring
- Dashboard Activity for user interface
- Network communication layer using Retrofit