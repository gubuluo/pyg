package com.pinyougou.shop.service;

import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


/** 用户认证服务类 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Seller seller = sellerService.findOne(username);
        /** 创建List集合封装角色 */
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        /** 添加角色 */
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        if(seller != null && "1".equals(seller.getStatus())){
            return new User(username, seller.getPassword(), grantedAuths);
        }
        return null;
    }
}
