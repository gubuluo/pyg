package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller) {
        try {
            String encode = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(encode);
            sellerService.save(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
