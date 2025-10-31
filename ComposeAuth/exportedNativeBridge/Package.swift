// swift-tools-version: 5.9
import PackageDescription

let package = Package(
  name: "exportedNativeBridge",
  platforms: [.iOS("12.0"), .macOS("10.13"), .tvOS("12.0"), .watchOS("4.0")],
  products: [
    .library(
      name: "exportedNativeBridge",
      type: .static,
      targets: ["exportedNativeBridge"])
  ],
  dependencies: [
    .package(url: "https://github.com/google/GoogleSignIn-iOS.git", exact: "9.0.0")
  ],
  targets: [
    .target(
      name: "exportedNativeBridge",
      dependencies: [
        .product(name: "GoogleSignIn", package: "GoogleSignIn-iOS")
      ],
      path: "Sources"

    )

  ]
)
