//
//  MainViewController.swift
//  PhotoTest
//
//  Created by ouniwang on 8/12/16.
//  Copyright © 2016 ouniwang. All rights reserved.
//

import UIKit

class MainViewController: UIViewController {
    @IBOutlet weak var welcomLabel: UILabel!
    @IBOutlet weak var coverImageView: UIImageView!
    @IBOutlet weak var habButton: UIButton!
    @IBOutlet weak var photoButton: UIButton!
    
    var resultDic: Dictionary<String, AnyObject>!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setCoverImage()
        self.setWelcomLabel()
        self.setButtonBorderColor()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier! {
            case "main_to_HAB_list":
                segue.destination.navigationItem.title = "HAB"
            case "main_to_photo_list":
                segue.destination.navigationItem.title = "Photo"
            case "main_to_login_test":
                segue.destination.navigationItem.title = "Login Test"
            default:
                return
        }
    }
    
    // MARK: - actions
    
    @IBAction func actinoInvite(_ sender: Any) {
        let content: FBSDKAppInviteContent = FBSDKAppInviteContent()
        
        // app link 아니면 빠꾸먹음. test 실패
        content.appLinkURL = URL(string: "http://www.naver.com")
        content.appInvitePreviewImageURL = URL(string: "http://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Facebook_Headquarters_Menlo_Park.jpg/2880px-Facebook_Headquarters_Menlo_Park.jpg")
        
        // 초대는 무조건 safari로 감. mode 설정이 없는듯
        FBSDKAppInviteDialog.show(from: self, with: content, delegate: self)
    }
    
    
    private func setButtonBorderColor() {
        habButton.layer.borderColor = UIColor.stringToColor(stringColor: "404040").cgColor
        photoButton.layer.borderColor = UIColor.stringToColor(stringColor: "404040").cgColor
    }
    
    private func setCoverImage() {
        //cover가 없을 경우 nil이므로 예외처리 필요함
        let cover = resultDic["cover"] as! Dictionary<String, AnyObject>
        
        Alamofire.request(cover["source"]!.debugDescription).response { (response) in
            self.coverImageView.image = UIImage(data: response.data!, scale: 1)
        }
    }
    
    private func setWelcomLabel() {
        let welcomStr: NSString = NSString(format: "안녕하세요, %@님!", resultDic["name"]!.debugDescription)
        let nameRange = welcomStr.range(of: resultDic["name"]!.debugDescription)
        let mutableStr = NSMutableAttributedString(string: welcomStr as String, attributes: [NSFontAttributeName:UIFont.systemFont(ofSize: 17)])
        mutableStr.addAttribute(NSFontAttributeName, value: UIFont.boldSystemFont(ofSize: 17), range: nameRange)
        self.welcomLabel.attributedText = mutableStr
    }
}

extension MainViewController: FBSDKAppInviteDialogDelegate {
    func appInviteDialog(_ appInviteDialog: FBSDKAppInviteDialog!, didCompleteWithResults results: [AnyHashable : Any]!) {
    
    }
    
    func appInviteDialog(_ appInviteDialog: FBSDKAppInviteDialog!, didFailWithError error: Error!) {
    }
}

