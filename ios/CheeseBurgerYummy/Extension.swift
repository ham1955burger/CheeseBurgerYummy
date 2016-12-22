//
//  extension.swift
//  PhotoTest
//
//  Created by ouniwang on 8/12/16.
//  Copyright © 2016 ouniwang. All rights reserved.
//

import Foundation

extension UIColor {
    class func stringToColor(stringColor: String) -> UIColor {
        var hexInt: UInt32 = 0
        let scanner = Scanner(string: stringColor)
        scanner.scanHexInt32(&hexInt)
        let color = UIColor(
            red: CGFloat((hexInt & 0xFF0000) >> 16)/225,
            green: CGFloat((hexInt & 0xFF00) >> 8)/225,
            blue: CGFloat((hexInt & 0xFF))/225,
            alpha: 1)
        
        return color
    }
}

extension String {
    func stateHABString() -> String {
        switch self {
            case "receive":
            return "입금"
            
            case "pay":
            return "출금"
            
        default:
            return ""
        }
    }
    
    func categoryToString() -> String {
        switch self {
            case "salary":
            return "월급"
            
            case "foodExpenses":
            return "식비"
            
            case "default":
            return "기타"
        
        default:
            return ""
        }
    }
    
    func stringToCategory() -> String {
        switch self {
            case "월급":
                return "salary"
            case "식비":
                return "foodExpenses"
            case "기타":
                return "default"
        default:
            return ""
        }
    }
}


extension UIViewController {
    func oneButtonAlert(_ message: String, handler: ((UIAlertAction) -> Void)? = nil) {
        
        /*
        let attributedString = NSAttributedString(string: message, attributes: [
            NSFontAttributeName : UIFont.systemFont(ofSize: 15),
            NSForegroundColorAttributeName : UIColor.red])*/
        
        let attributedString = NSAttributedString(string: message, attributes:
            [NSFontAttributeName : UIFont.systemFont(ofSize: 15)])
        
        let alert = UIAlertController.init(title: "", message: "", preferredStyle: .alert)
        
//        alert.setValue(attributedString, forKey: "attributedTitle")
        alert.setValue(attributedString, forKey: "attributedMessage")
        
        let confirmButton = UIAlertAction.init(title: "확인", style: .default, handler: nil)
        let confirmButtonWithAction = UIAlertAction.init(title: "확인", style: .default, handler: handler)
        
        if handler != nil {
            alert.addAction(confirmButtonWithAction)
        } else {
            alert.addAction(confirmButton)
        }
        
        self.present(alert, animated: true, completion: nil)
    }
}
