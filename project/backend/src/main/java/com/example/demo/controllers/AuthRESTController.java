package com.example.demo.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.message.request.LoginForm;
import com.example.demo.message.request.SignUpForm;
import com.example.demo.message.response.JwtResponse;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.models.Role;
import com.example.demo.models.RoleName;
import com.example.demo.models.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.services.UserPrinciple;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/auth")
public class AuthRESTController {

    private DaoAuthenticationProvider daoAuthenticationProvider;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;

    @Autowired
    public AuthRESTController(DaoAuthenticationProvider daoAuthenticationProvider,
            UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
        Authentication authentication = daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserPrinciple userDetails = (UserPrinciple) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken."),
                    HttpStatus.BAD_REQUEST);
        }

        // Create user account
        User user = new User(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Fail -> Cause: Student Role not found."));
            roles.add(studentRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "teacher":
                        Role teacherRole = roleRepository.findByName(RoleName.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Fail -> Cause: Teacher Role not found."));
                        roles.add(teacherRole);
                        break;
                    case "student":
                    default:
                        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                                .orElseThrow(() -> new RuntimeException("Fail -> Cause: Student Role not found."));
                        roles.add(studentRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("User registered successfully."), HttpStatus.OK);

    }

}
