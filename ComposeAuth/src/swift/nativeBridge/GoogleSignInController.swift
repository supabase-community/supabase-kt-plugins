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
                completion(idToken, nil, false)
            }
        }

    @objc public static func signOutGoogle() {
        GoogleSignIn.GIDSignIn.sharedInstance.signOut()
    }
}