package com.example.scrapetok.application;

import com.example.scrapetok.application.emailservice.AlertEmailEvent;
import com.example.scrapetok.domain.AdminProfile;
import com.example.scrapetok.domain.DTO.*;
import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.domain.UserApifyCallHistorial;
import com.example.scrapetok.domain.enums.Role;
import com.example.scrapetok.exception.EmailAlreadyInUseException;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.repository.AdminProfileRepository;
import com.example.scrapetok.repository.GeneralAccountRepository;
import com.example.scrapetok.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Transactional
public class AuthorizationService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private GeneralAccountRepository generalAccountRepository;
    @Autowired
    private AdminProfileRepository adminProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    public UserSignUpResponseDTO createUser(UserSignUpRequestDTO request) {
        GeneralAccount newUser = modelMapper.map(request, GeneralAccount.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        ZonedDateTime zonedDateTime = obtenerFechaPeru();
        newUser.setCreationDate(zonedDateTime.toLocalDate());
        UserApifyCallHistorial historial = new UserApifyCallHistorial();
        historial.setUser(newUser);
        newUser.setHistorial(historial);

        // Generar mensaje de email
        String subject = "👋 Welcome to ScrapeTok—Let's Kick Off Your TikTok Data Adventure! 🎉";

        StringBuilder body = new StringBuilder();
        body.append("Hi there 😊,").append("\n\n");
        body.append("We’re thrilled to have you on board! 🚀 Welcome to ScrapeTok, where uncovering actionable TikTok insights is as easy as a scroll.").append("\n\n");
        body.append("Here’s what you can look forward to in this ").append("**DEMO version (100% free, on us!)**").append(" 💯:").append("\n\n");
        body.append("1. 📊 **Instant Analytics**").append("\n");
        body.append("   Dive into comprehensive dashboards that surface trending creators, hashtag performance, and engagement metrics—no manual digging required.").append("\n\n");
        body.append("2. 🌟 **General Scrape Feature (“Scrapeo General”)**").append("\n");
        body.append("   See the top viral trends of the day, all in one place.").append("\n\n");
        body.append("3. 🔍 **Flexible Apify Scraping**").append("\n");
        body.append("   Filter and scrape by profile, hashtag, or keyword—so you get exactly the TikTok content you need.").append("\n\n");
        body.append("4. 💾 **Data Export & Downloadable Charts**").append("\n");
        body.append("   Export your raw data as CSV files, and download any generated chart directly for your reports.").append("\n\n");
        body.append("5. 🛠️ **Technical Support (Q&A with Admin)**").append("\n");
        body.append("   Have questions or need help? Send your support requests straight to our admins for fast, friendly assistance.").append("\n\n");
        body.append("**Ready to get started?**").append("\n");
        body.append("• Log in to your dashboard 🔗").append("\n");
        body.append("• Check out our Quickstart Guide for tips on using “Scrapeo General” and other features 📖").append("\n");
        body.append("• Join our community for best practices, use-cases, and direct support 💬").append("\n\n");
        body.append("If you have any questions, feedback, or feature requests, just hit reply. We’re here to help you make the most of your TikTok data—entirely free during this demo! 🎁").append("\n\n");
        body.append("Happy scraping! 🥳").append("\n\n");
        body.append("—").append("\n");
        body.append("The ScrapeTok Team").append("\n");
        body.append("support@scrapetok.com").append("\n\n");

        try {
            GeneralAccount saved = generalAccountRepository.save(newUser);
            applicationEventPublisher.publishEvent(new AlertEmailEvent(this, request.getEmail(), subject, body.toString()));
            return modelMapper.map(saved, UserSignUpResponseDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }
    }



    public AdminSystemResponseDTO createAdmin(UserSignUpRequestDTO request) {
        GeneralAccount newUser = modelMapper.map(request, GeneralAccount.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(Role.ADMIN);
        ZonedDateTime zonedDateTime = obtenerFechaPeru();
        newUser.setCreationDate(zonedDateTime.toLocalDate());
        UserApifyCallHistorial historial = new UserApifyCallHistorial();
        historial.setUser(newUser);
        newUser.setHistorial(historial);

        AdminProfile adminProfile = new AdminProfile();
        adminProfile.setUser(newUser);
        adminProfile.setAdmisionToAdminDate(zonedDateTime.toLocalDate());
        adminProfile.setAdmisionToAdminTime(zonedDateTime.toLocalTime());
        adminProfile.setIsActive(true);
        adminProfile.setTotalQuestionsAnswered(0);
        GeneralAccount savedUser = generalAccountRepository.save(newUser);
        AdminProfile savedAdmin = adminProfileRepository.save(adminProfile);
        AdminSystemResponseDTO requestDTO = modelMapper.map(savedUser, AdminSystemResponseDTO.class);
        modelMapper.map(savedAdmin,requestDTO);
        return requestDTO;
    }


    public UpgradeToAdminResponseDTO upgrade(UpgradeToAdminRequestDTO request) {
        // Verifica que el admin que está promoviendo exista
        AdminProfile admin = adminProfileRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin with id " + request.getAdminId() + " not found"));

        // Verifica que el usuario a promover exista
        GeneralAccount user = generalAccountRepository.findById(request.getUserid())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + request.getUserid() + " not found"));

        if (user.getRole() == Role.ADMIN)
            throw new IllegalStateException("⚠️ This user is already an admin.");

        // Crear nuevo perfil de admin
        AdminProfile nuevoAdmin = new AdminProfile();
        nuevoAdmin.setUser(user);
        ZonedDateTime zonedDateTime = obtenerFechaPeru();
        nuevoAdmin.setAdmisionToAdminDate(zonedDateTime.toLocalDate());
        nuevoAdmin.setAdmisionToAdminTime(zonedDateTime.toLocalTime());
        user.setRole(Role.ADMIN);
        adminProfileRepository.save(nuevoAdmin);
        generalAccountRepository.save(user);
        UpgradeToAdminResponseDTO responseDTO = new UpgradeToAdminResponseDTO();
        modelMapper.map(user, responseDTO);
        modelMapper.map(nuevoAdmin, responseDTO);
        return responseDTO;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No role found"))
                    .getAuthority()
                    .replace("ROLE_", "");

            String token = jwtUtil.generateToken(userDetails.getUsername(), role);
            return new LoginResponseDTO(token, role);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        } catch (Exception e) {
            throw new RuntimeException("Error interno al autenticar: " + e.getMessage());
        }
    }


    private ZonedDateTime obtenerFechaPeru() {
        return ZonedDateTime.now(ZoneId.of("America/Lima"));
    }
}
