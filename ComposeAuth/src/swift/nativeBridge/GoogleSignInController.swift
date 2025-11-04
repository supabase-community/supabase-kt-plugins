import Foundation
import GoogleSignIn
import UIKit // Needed for UIViewController

// Define a public typealias for the completion handler closure
public typealias GoogleSignInCompletionHandler = (String?, String?, Bool) -> Void

@objcMembers public class GoogleSignInController: NSObject {

    public override init() {
        super.init()
    }

    public func signIn(
        completion: @escaping GoogleSignInCompletionHandler,
        nonce: String? = nil
        ) {
            guard let presentingViewController = UIApplication.shared.keyWindow?.rootViewController else {
                completion(nil, "No root view controller found", false)
                return
            }
            GoogleSignIn.GIDSignIn.sharedInstance.signIn(
                withPresenting: presentingViewController,
                hint: nil,
                additionalScopes: nil,
                nonce: nonce
            ) { result, error in
                if let error = error {
                    if let nsError = error as NSError?, nsError.code == -5 {
                        completion(nil, nil, true)
                    } else {
                        completion(nil, error.localizedDescription, false)
                    }
                    return
                }

                guard let idToken = result?.user.idToken?.tokenString else {
                    completion(nil, "No ID token returned", false)
                    return
                }

                if let nonceFromToken = self.decodeNonce(fromJWT: idToken) {
                    if nonceFromToken != nonce {
                        print("Nonce from idToken does not match, random nonce is used from AppAuth")
                        completion(idToken, nil, false)
                        return
                    }
                } else {
                    completion(nil, "Unknown sign-in error or no ID token", false)
                }
                completion(idToken, nil, false)
            }
        }

    @objc public static func signOutGoogle() {
        GoogleSignIn.GIDSignIn.sharedInstance.signOut()
    }
}

// MARK: - Private JWT Decoding Helpers
private extension GoogleSignInController {

    func decodeNonce(fromJWT jwt: String) -> String? {
        let segments = jwt.components(separatedBy: ".")
        guard segments.count > 1 else { return nil }
        return decodeJWTSegment(segments[1])?["nonce"] as? String
    }

    func decodeJWTSegment(_ segment: String) -> [String: Any]? {
        guard let data = base64UrlDecode(segment),
              let json = try? JSONSerialization.jsonObject(with: data, options: []),
              let payload = json as? [String: Any] else {
            return nil
        }
        return payload
    }

    func base64UrlDecode(_ value: String) -> Data? {
        var base64 = value
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")

        let length = Double(base64.lengthOfBytes(using: .utf8))
        let requiredLength = 4 * ceil(length / 4.0)
        let paddingLength = requiredLength - length
        if paddingLength > 0 {
            let padding = String(repeating: "=", count: Int(paddingLength))
            base64 += padding
        }
        return Data(base64Encoded: base64, options: .ignoreUnknownCharacters)
    }
}
