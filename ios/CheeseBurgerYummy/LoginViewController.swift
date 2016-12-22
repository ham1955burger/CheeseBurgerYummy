//
//  LoginViewController.swift
//  CheeseBurgerYummy
//
//  Created by ouniwang on 12/21/16.
//  Copyright © 2016 ouniwang. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
     
        /*
        let loginButton: FBSDKLoginButton = FBSDKLoginButton()
        loginButton.center = self.view.center
        self.view.addSubview(loginButton)*/
        
    }
    
    @IBAction func actionFacebook(_ sender: Any) {
        if let _ = FBSDKAccessToken.current() {
            // User is logged in, do work such as go to next view controller.
        } else {
            let login: FBSDKLoginManager = FBSDKLoginManager()
            // 기본은 3가지, 나머지는 승인을 받아야함
            login.logIn(withReadPermissions: ["public_profile", "email", "user_friends"], from: self, handler: { (result, error) in
                if let error = error {
                    print("Process Error \(error)")
                } else if (result?.isCancelled)! {
                    print("Cancelled")
                } else {
                    // Login
                    // 가장 기본적인 정보는 id(이상한 숫자)랑 이름만 가져옴.
                    print("Login : \(result)")
                    
                    // https://developers.facebook.com/docs/graph-api/reference/user
                    let parameters: [String: String] = [
                        "fields": "email, name, age_range, cover, locale, gender"
                    ]
                    
                    FBSDKGraphRequest(graphPath: "me", parameters: parameters).start(completionHandler: { (connection, result, error) in
                        if error == nil {
                            
                            // facebook 정책에 따라 값이 있고, 없을 수 있으므로 예외처리 필요
                            print(result!)
                            
                            let resultDic = result! as! Dictionary<String, AnyObject>
                            
                            let storyboard: UIStoryboard = UIStoryboard.init(name: "Main", bundle: nil)
                            let mainNavigationController: UINavigationController = storyboard.instantiateViewController(withIdentifier: "MainNavigationController") as! UINavigationController
                            
                            let mainViewController: MainViewController = mainNavigationController.childViewControllers.first as! MainViewController
                            mainViewController.resultDic = resultDic
                            UIApplication.shared.keyWindow?.rootViewController = mainNavigationController
                        }
                    })
                }
            })
        }
    }
    
    @IBAction func actionInstagram(_ sender: Any) {
    }
}
