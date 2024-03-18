/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.springsecurity.app.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mm887
 */
/*
@Data
- from lombok
- generates :
    getters/setters for all fields
    , toString(), hashCode(), equals() 
    implementations as well as a constructor

@Builder
- help me build my object in easy way to use design pattern

with Spring Security you must have UserDetails obj
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    
    private String token;
    
}
