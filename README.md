# SharingTestApp

This is an Android app project create with Android Studio Dolphin | 2021.3.1 Patch 1.
The app should implement sharing a file, which was created by the app.
If the file already exists, a toast shows: "Testfile already exists". Otherwise, it is created.

First option is to share the file with a FileProvider, which works as expected.

Second option is to choose the file with Intent.createChooser. You can find the testFile.json at Documents/SharingTestApp on internal storage if you select "Show internal storage" in the menu.