#!/bin/bash
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
mkdir -p "$ANDROID_SDK_ROOT"

# Use sdkmanager with --sdk_root parameter
echo "Accepting Android SDK licenses..."
/opt/homebrew/bin/sdkmanager --sdk_root="$ANDROID_SDK_ROOT" --licenses << EOF
y
y
y
y
y
y
y
EOF

echo "Installing required SDK components..."
/opt/homebrew/bin/sdkmanager --sdk_root="$ANDROID_SDK_ROOT" \
  "platform-tools" \
  "platforms;android-34" \
  "build-tools;34.0.0"

echo "Updating local.properties..."
echo "sdk.dir=$ANDROID_SDK_ROOT" > /Users/driversti/Projects/ai/sudoku-android/local.properties

echo "Done! Android SDK is ready."
