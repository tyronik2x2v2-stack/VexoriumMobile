# VEXORIUM MOBILE — Android launcher

A small Russian-language Android companion launcher for VEXORIUM ROLEPLAY. The interface stores a nickname, displays the server address `ams-game-02.centnodes.net:40065`, copies it to the clipboard, and can open an already-installed Android app whose name appears to be a SA-MP client.

## Important limits of this first version

- This app is a launcher/helper, **not** the GTA San Andreas game or a multiplayer engine.
- It does not bundle GTA game files or download game data.
- It does not yet auto-fill the server address inside a third-party client. After opening the mobile client, you may need to add the server manually.
- Mobile-client compatibility with the VEXORIUM server (gamemode, plugins, assets and controls) still needs real-device testing.
- The community client page linked from the app is third-party and not an official Rockstar or SA-MP release. Review its source and risks before installing anything. Only use game files you are legally entitled to use.

## Build an APK automatically with GitHub Actions

1. Create a **new separate public GitHub repository**, e.g. `VexoriumMobile`. Do not mix this project with the existing Windows launcher repository.
2. Extract this ZIP on your computer.
3. Upload **the contents inside** the `VEXORIUM-MOBILE-ANDROID` folder (including `.github`, `app`, `build.gradle`, `settings.gradle`, and `gradle.properties`) to the root of the new repository. Make sure `.github/workflows/build-apk.yml` is uploaded too. If GitHub hides `.github`, create those folders through the GitHub file editor or upload via Git.
4. Commit the files to the `main` branch.
5. Open the repository's **Actions** tab and select **Build VEXORIUM MOBILE APK**. A push should start the workflow automatically; if not, use **Run workflow**.
6. When the run is green, open it and download the `VEXORIUM-MOBILE-APK` artifact. Extract the ZIP to get `app-debug.apk` and install it on your Android phone.

## Android compatibility

- Minimum Android version: Android 8.0 (API 26).
- Target/compile SDK: Android 15 (API 35).
- The artifact built by the included workflow is a **debug/test APK**, not a Play Store release. It is suitable for initial sideloaded testing.

## Server details

- Name: VEXORIUM ROLEPLAY
- Address: `ams-game-02.centnodes.net:40065`
