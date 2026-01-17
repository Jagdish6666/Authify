package com.Authentication.demo.filter;

import com.Authentication.demo.Util.jwtUtil;
import com.Authentication.demo.service.AppUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class jwtrequestfilter extends OncePerRequestFilter {

    private final AppUserService appUserService;
    private final jwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of("/auth/login","/register","/auth/send-reset-otp", "/auth/reset-password","/auth/logout");


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

         String path = request.getServletPath();

         if(PUBLIC_URLS.contains(path))
         {
             filterChain.doFilter(request,response);
             return;

         }
         String jwt = null;
         String email = null;

         final String authrizationHeader = request.getHeader("Authorization");
         if(authrizationHeader != null && authrizationHeader.startsWith("Bearer ")){
                jwt = authrizationHeader.substring(7);

         }

         if(jwt == null)
         {
             Cookie[] cookies = request.getCookies();
             if(cookies != null)
             {
                 for(Cookie cookie: cookies)
                 {
                     if("jwt".equals(cookie.getName()))
                     {
                         jwt = cookie.getValue();
                         break;
                     }
                 }
             }
         }

         if(jwt != null)
         {
             email = jwtUtil.extractEmail(jwt);
             if(email != null && SecurityContextHolder.getContext().getAuthentication() == null )
             {
                   UserDetails userDetails = appUserService.loadUserByUsername(email);
                 if(jwtUtil.validateToken(jwt, userDetails))
                   {
                       UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                       authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                       SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                   }
             }
         }



         filterChain.doFilter(request,response);
    }
}
