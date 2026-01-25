package com.Authentication.demo.Controller;

import com.Authentication.demo.Util.jwtUtil;
import com.Authentication.demo.io.AuthRequest;
import com.Authentication.demo.io.AuthResponse;
import com.Authentication.demo.service.AppUserService;
import com.Authentication.demo.service.ProfileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final jwtUtil jwtUtil;
    private final ProfileService profileService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticate(request.getEmail(), request.getPassword());
            final UserDetails userDetails = appUserService.loadUserByUsername(request.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .secure(false)
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(), jwtToken));

        } catch (BadCredentialsException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Email or password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } catch (DisabledException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "User Account Disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Authorization has failed: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name")String email)
    {
        return ResponseEntity.ok(email != null);

    }

    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email)
    {
        try{
            profileService.sendResetOtp(email);

        } catch (Exception e)
        {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }


    }

    @PostMapping("/send-otp")
    public void sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name")String email)
    {
           try{
               profileService.sendResetOtp(email);
           } catch (Exception e) {
                       throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
           }
    }

//    @PostMapping("/verify-otp")
//    public void verifyEmail(@RequestBody Map<String, Object> request, @CurrentSecurityContext(expression = "authentication?.name")String email) {
//
//        if(request.get("otp").toString() == null)
//        {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing Datails");
//        }
//        try{
//
//            profileService.verifyOtp(email,request.get("otp").toString());
//
//
//        }catch (Exception e)
//        {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//
//        }
//
//
//    }

//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
//
//        String email = request.get("email");
//        String otp = request.get("otp");
//
//        if (email == null || otp == null) {
//            return ResponseEntity.badRequest().body(
//                    Map.of("message", "Email and OTP are required")
//            );
//        }
//
//        profileService.verifyOtp(email, otp);
//
//        return ResponseEntity.ok(
//                Map.of("message", "OTP verified successfully")
//        );
//    }

//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
//
//        String email = request.get("email");
//        String otp = request.get("otp");
//
//        if (email == null || otp == null) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of(
//                            "success", false,
//                            "message", "Email and OTP are required"
//                    ));
//        }
//
//        try {
//            profileService.verifyOtp(email, otp);
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "message", "OTP verified successfully"
//            ));
//        } catch (RuntimeException ex) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of(
//                            "success", false,
//                            "message", ex.getMessage()
//                    ));
//        }
//    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyEmail(
            @RequestBody Map<String, Object> request,
            @CurrentSecurityContext(expression = "authentication?.name") String email) {

        // Validate input
        if (email == null || request.get("otp") == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email and OTP are required"
            );
        }

        try {
            profileService.verifyOtp(email, request.get("otp").toString());

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "OTP verified successfully"
                    )
            );

        } catch (ResponseStatusException e) {
            throw e; // keep exact error

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }




}




