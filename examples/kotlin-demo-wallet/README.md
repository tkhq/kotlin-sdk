# Turnkey Kotlin Demo App

Welcome to the Turnkey **Kotlin** demo app! This example app serves the purpose of providing a full flow example of how you can integrate or build with Turnkey's Kotlin SDK.

---

## Quick start

In order to get the example app running, there are a few steps you have to take beforehand.

**0) First and foremost, ensure you have an IDE that can simulate Android apps**
* This can include [Android Studio (recommended)](https://developer.android.com/studio), [IntelliJ IDEA](https://www.jetbrains.com/idea/download), or another IDE of your choice

**1) Add `local.properties` to demo directory**
* Head over to our [dashboard](https://app.turnkey.com/dashboard) and get a test org set up
* Fill in the keys below in your `local.properties` file:
```properties
API_BASE_URL=https://api.turnkey.com
AUTH_PROXY_BASE_URL=https://authproxy.turnkey.com
AUTH_PROXY_CONFIG_ID=<your-auth-proxy-config-id>
ORGANIZATION_ID=<your-parent-org-id>
APP_SCHEME=kotlindemoapp
RP_ID=<your-rp-id>
```
> To learn more about how to set up an rpId, head over to our [relying party ID (rpId)](https://turnkey-0e7c1f5b-ethan-kotlin-docs.mintlify.app/sdks/kotlin/authentication/rp-id-setup) setup guide.

**2) Build and run the demo app and play around!**
* The demo app should build and open up on your Android simulator.

> Note:
> If you are testing on a free org, you may run into signing caps. All errors are logged in Logcat (for Android Studio) so if any button is not doing what its supposed to, or doesn't seem to be working, check your logs to ensure everything is set up fine.

![Demo](../../assets/demo.gif)
