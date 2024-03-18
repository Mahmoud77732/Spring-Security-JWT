/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.springsecurity.app.config;

import com.springsecurity.app.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author mm887
 */
/*
. OncePerRequestFilter implements Filter
. you can implements Filter directly
. but we use OncePerRequestFilter which is iverrided by spring

@RequiredArgsConstructor : 
    will create constructor for each final field we create

 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticatedFilter extends OncePerRequestFilter {
    
    // injected by @RequiredArgsConstructor from lombok
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException 
    {
        // the token will be passed in the header variable called "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // jwt token will always start with "Bearer"
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return; // token not found or not correct
        }
        
        // 7 -> after "Bearer "
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        
        if(userEmail != null 
                &&
           SecurityContextHolder.getContext().getAuthentication() == null)
        {
            // now user is not connected
            // you can use your User class instead of UserDetails
            UserDetails userDetails = 
                    this.userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(jwt, userDetails)){
                // this obj is needed by spring security holder in order to update our security context 
                UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
