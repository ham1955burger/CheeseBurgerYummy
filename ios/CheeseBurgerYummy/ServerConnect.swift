//
//  ServerConnect.swift
//  CheeseBurgerYummy
//
//  Created by ouniwang on 12/21/16.
//  Copyright © 2016 ouniwang. All rights reserved.
//

import Foundation

class ServerConnect {
    //TODO: 언젠간 alamofire 나눌꺼야..
    
    class func getHeader() -> HTTPHeaders {
        var header: HTTPHeaders = [
            //    "Authorization":"QQQQQQQQQQQQQQQQQQQQQQQQQ",
            "Accept": "application/json"
        ]
        
        if true {
            header["Authorization"] = "qqqqqqqqqqqqqqqqqq"
        }

        return header
    }
}
