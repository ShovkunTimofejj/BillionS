import SwiftUI
import UserNotifications
import ComposeApp

@main
struct iOSApp: App {

    init() {
        UNUserNotificationCenter.current().requestAuthorization(
            options: [.alert, .sound, .badge]
        ) { granted, error in
            print("Notifications granted: \(granted)")
        }

        KoinStarter.shared.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}

