import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    let hasValidServerConfiguration: Bool

    func makeUIViewController(context: Self.Context) -> UIViewController {
        MainViewControllerKt.MainViewController(hasValidServerConfiguration: hasValidServerConfiguration)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    @State private var hasValidServerConfiguration: Bool?

    var body: some View {
        Group {
            if let hasValidServerConfiguration {
                ComposeView(hasValidServerConfiguration: hasValidServerConfiguration)
                    .ignoresSafeArea()
            } else {
                SplashView()
            }
        }
    }
}

private struct SplashView: View {
    var body: some View {
        ZStack {
            Color(red: 16 / 255, green: 16 / 255, blue: 20 / 255)
                .ignoresSafeArea()

            RoundedRectangle(cornerRadius: 28)
                .fill(Color(red: 38 / 255, green: 36 / 255, blue: 43 / 255))
                .frame(width: 128, height: 88)
                .overlay(
                    RoundedRectangle(cornerRadius: 28)
                        .stroke(Color(red: 1, green: 177 / 255, blue: 92 / 255), lineWidth: 2)
                )
                .overlay(
                    HStack(spacing: 28) {
                        Circle()
                            .fill(Color(red: 16 / 255, green: 16 / 255, blue: 20 / 255))
                            .frame(width: 28, height: 28)
                        Circle()
                            .fill(Color(red: 16 / 255, green: 16 / 255, blue: 20 / 255))
                            .frame(width: 28, height: 28)
                    }
                )
        }
    }
}
