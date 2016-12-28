//
//  PhotoDetailViewController.swift
//  PhotoTest
//
//  Created by ouniwang on 8/16/16.
//  Copyright © 2016 ouniwang. All rights reserved.
//

import UIKit

class PhotoDetailViewController: UIViewController {
    
    @IBOutlet weak var imageButton: UIButton!
    @IBOutlet weak var bodyTextField: UITextField!
    @IBOutlet weak var doneButton: UIButton!
    @IBOutlet weak var deleteButton: UIButton!
    @IBOutlet weak var createdLabel: UILabel!
    @IBOutlet weak var shareButton: UIButton!
    
    var imagePicker: UIImagePickerController!
    var image: UIImage?
    var viewType: ViewType = .add
    var info: JSON?
    
    override func viewDidLoad() {
        self.imagePicker = UIImagePickerController()
        self.imagePicker.delegate = self
        self.imagePicker.allowsEditing = true
        
        if self.viewType == .add {
            //add
            self.imageButton.setTitle("사진 가져오기", for: UIControlState())
            self.doneButton.setTitle("등록", for: UIControlState())
            self.shareButton.isHidden = true
        } else {
            //edit
            self.imageButton.setTitle("", for: UIControlState())
            self.imageButton.setBackgroundImage(self.image!, for: UIControlState())
            self.bodyTextField.text = self.info!["description"].stringValue
            
            let str = self.info!["created_at"].stringValue as NSString
//            str.substringWithRange(Range<String.Index>(start: str.startIndex.advancedBy(2), end: str.endIndex.advancedBy(-1))) //"llo, playgroun"
            self.createdLabel.text = str.substring(with: NSRange(location: 0, length: 10))
            self.createdLabel.isHidden = false
            self.deleteButton.isHidden = false
            self.doneButton.setTitle("수정", for: UIControlState())
        }
    }
    
    @IBAction func actionImageButton(_ sender: AnyObject) {
        let actionSheet = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        actionSheet.addAction(UIAlertAction(title: "카메라에서 가져오기", style: .default, handler: { (UIAlertAction) in
            if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera) {
                self.imagePicker.sourceType = UIImagePickerControllerSourceType.camera
                self.present(self.imagePicker, animated: true, completion: nil)
            } else {
                // not allow
                print("not allow camera!")
            }
        }))
        actionSheet.addAction(UIAlertAction(title: "라이브러리에서 가져오기", style: .default, handler: { (UIAlertAction) in
            if UIImagePickerController.isSourceTypeAvailable(.photoLibrary){
                self.imagePicker.sourceType = .photoLibrary
                self.present(self.imagePicker, animated: true, completion: nil)
            } else {
                // not allow
                print("not allow library!")
               
            }
        }))
        actionSheet.addAction(UIAlertAction(title: "취소", style: .destructive, handler: nil))
        
        self.present(actionSheet, animated: true, completion: nil)
    }
    
    @IBAction func actionCloseButton(_ sender: AnyObject) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func actionDoneButton(_ sender: AnyObject) {
        if self.viewType == .add {
            self.uploadWithAlamofire("http://192.168.0.9:8080/photo", state: true)
        } else {
            //edit
            self.uploadWithAlamofire("http://192.168.0.9:8080/photo/detail/\(self.info!["pk"])", state: false)
        }
        
    }
    
    @IBAction func actionDeleteButton(_ sender: AnyObject) {
        Alamofire.request("http://192.168.0.9:8080/photo/detail/\(self.info!["pk"])", method: .delete, parameters: nil, encoding: JSONEncoding.default)
            .validate()
            .responseJSON { response in
                switch response.result {
                case .success(let data):
                    print(data)
                    self.oneButtonAlert(String("삭제되었습니다")){ (UIAlertAction) in
                        self.dismiss(animated: true, completion: nil)
                    }
                    
                case .failure(let error):
                    print(error)
                    self.oneButtonAlert(String(describing: error))
                }
        }
    }
    
    @IBAction func actionShare(_ sender: Any) {
        // https://developers.facebook.com/docs/sharing/ios/share-button
       
        let contentURL: URL = URL(string: "http://en.wikipedia.org/wiki/Facebook")!
        let imageURL: URL = URL(string: "http://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Facebook_Headquarters_Menlo_Park.jpg/2880px-Facebook_Headquarters_Menlo_Park.jpg")!
        
        /*
        let contentURL: URL = URL(string: "http://192.168.0.9:8080/scheme")!
        let imageURL: URL = URL(string: "http://192.168.0.9:8080\(info!["image_thumb_file"].stringValue)")!*/
        
        let shareLink = FBSDKShareLinkContent()
        shareLink.contentURL = contentURL
        shareLink.imageURL = imageURL
        shareLink.contentTitle = "CheeseBurgerYummy"
        shareLink.contentDescription = "\(info!["description"].stringValue)"
        
//        FBSDKShareDialog.show(from: self, with: shareLink, delegate: self)
        
        let dialog : FBSDKShareDialog = FBSDKShareDialog()
        dialog.delegate = self
        dialog.fromViewController = self
        dialog.shareContent = shareLink
        
        let facebookURL = URL(string: "fbauth2://app")
        if UIApplication.shared.canOpenURL(facebookURL!) {
            dialog.mode = FBSDKShareDialogMode.native
        } else {
            dialog.mode = FBSDKShareDialogMode.feedWeb
        }
        dialog.show()
    }
}

// MARK: - FBSDKSharingDelegate

extension PhotoDetailViewController: FBSDKSharingDelegate {
    func sharer(_ sharer: FBSDKSharing!, didCompleteWithResults results: [AnyHashable : Any]!) {
        print("FB: SHARE RESULTS=\(results.debugDescription)")
        // 공유했을 경우 result로 postId가 날라옴 /automatic일 경우 공유 안하고 나왔을 경우 이 함수를 타며, results엔 값이 없음
        if results["postId"] != nil {
            self.oneButtonAlert(String("공유되었습니다."))
        }
    }
    
    func sharer(_ sharer: FBSDKSharing!, didFailWithError error: Error!) {
        print("FB: ERROR=\(error)")
    }
    
    func sharerDidCancel(_ sharer: FBSDKSharing!) {
        // FBSDKShareDialogMode를 정해주지 않으면 default는 automatic.
        // automatic일 경우 이 함수를 타지 않음
        print("ddddddd")
    }
}

// MARK: - UIImagePickerControllerDelegate, UINavigationControllerDelegate

extension PhotoDetailViewController: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        if let pickedImage = info[UIImagePickerControllerEditedImage] as? UIImage {
            self.imageButton.setBackgroundImage(pickedImage, for: UIControlState())
            self.imageButton.setTitle("", for: UIControlState())
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        self.dismiss(animated: true, completion: nil)
    }
}

extension PhotoDetailViewController {
    // import Alamofire
    func uploadWithAlamofire(_ url: String, state: Bool) {
        
        // define parameters
        let parameters = [
            "description": self.bodyTextField.text!,
        ]
        
        if state {
            //add
            // Begin upload
            Alamofire.upload(multipartFormData: { (multipartFormData) in
                // import image to request
                if let imageData = UIImageJPEGRepresentation(self.imageButton.currentBackgroundImage!, 0.8) {
                    multipartFormData.append(imageData, withName: "image_file", fileName: "myImage.png", mimeType: "image/png")
                }
                
                // import parameters
                for (key, value) in parameters {
                    multipartFormData.append(value.data(using: String.Encoding.utf8)!, withName: key)
                }

            }, to: url, encodingCompletion: { (encodingResult) in
                switch encodingResult {
                case .success(let upload, _, _):
                                        upload.responseJSON { response in
                                            debugPrint(response)
                                        }
                    self.oneButtonAlert(String("사진 등록 완료!!")){ (UIAlertAction) in
                        self.dismiss(animated: true, completion: nil)
                    }
                    
                case .failure(let encodingError):
                    print(encodingError)
                }
            })
        } else {
            //edit
            // Begin upload
            
            if self.image != self.imageButton.currentBackgroundImage {
                //changed image
                
                Alamofire.upload(multipartFormData: { (multipartFormData) in
                    // import image to request
                    if let imageData = UIImageJPEGRepresentation(self.imageButton.currentBackgroundImage!, 0.8) {
                        multipartFormData.append(imageData, withName: "image_file", fileName: "myImage.png", mimeType: "image/png")
                    }
                    
                    // import parameters
                    for (key, value) in parameters {
                        multipartFormData.append(value.data(using: String.Encoding.utf8)!, withName: key)
                    }
                    
                    }, to: url, encodingCompletion: { (encodingResult) in
                        switch encodingResult {
                        case .success(let upload, _, _):
                            //                    upload.responseJSON { response in
                            //                        debugPrint(response)
                            //                    }
                            self.oneButtonAlert(String("수정 완료!!")){ (UIAlertAction) in
                                self.dismiss(animated: true, completion: nil)
                            }
                            
                        case .failure(let encodingError):
                            print(encodingError)
                        }
                })
            } else {
                //not changed image
                Alamofire.request(url, method: .put, parameters: parameters, encoding: JSONEncoding.default)
                .validate()
                .responseJSON { response in
                    switch response.result {
                        case .success(let data):
                            self.oneButtonAlert(String("수정 완료!!")){ (UIAlertAction) in
                                self.dismiss(animated: true, completion: nil)
                            }
                        case .failure(let error):
                            print(error)
                    }
                }
            }
        }
    }
}
