package com.example.scrapetok.application;

import com.example.scrapetok.application.emailservice.AlertEmailEvent;
import com.example.scrapetok.application.emailservice.WelcomeEmailEvent;
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
import java.util.ArrayList;
import java.util.List;

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

        body.append("<div style=\"font-family: 'Segoe UI', sans-serif; max-width: 600px; margin: auto; padding: 20px; background-color: #ffffff; color: #333; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">");

        body.append("<h2 style=\"color: #7e22ce; text-align: center;\">🎉 ¡Bienvenido a ScrapeTok!</h2>");
        body.append("<p>Hi there 😊,</p>");
        body.append("<p>We’re thrilled to have you on board! 🚀 <strong>Welcome to ScrapeTok</strong>, where uncovering actionable TikTok insights is as easy as a scroll.</p>");

        body.append("<p>Here’s what you can look forward to in this <strong style='color: #16a34a;'>DEMO version (100% free, on us!)</strong> 💯:</p>");

        body.append("<ul style=\"padding-left: 1rem;\">");
        body.append("<li><strong>📊 Instant Analytics:</strong><br/>Dive into dashboards that surface trending creators, hashtags, and metrics—no manual digging.</li><br/>");
        body.append("<li><strong>🌟 General Scrape Feature (“Scrapeo General”):</strong><br/>See the top viral trends of the day, all in one place.</li><br/>");
        body.append("<li><strong>🔍 Flexible Apify Scraping:</strong><br/>Filter by profile, hashtag, or keyword—get the TikTok content you need.</li><br/>");
        body.append("<li><strong>💾 Data Export & Downloadable Charts:</strong><br/>Export your raw data as CSV and download charts for your reports.</li><br/>");
        body.append("<li><strong>🛠️ Technical Support (Q&A with Admin):</strong><br/>Send support requests directly to our admins for fast, friendly help.</li>");
        body.append("</ul>");

        body.append("<p><strong>Ready to get started?</strong></p>");
        body.append("<ul style=\"padding-left: 1rem;\">");
        body.append("<li>✅ Log in to your dashboard</li>");
        body.append("<li>📖 Check out our Quickstart Guide for tips</li>");
        body.append("<li>💬 Join our community for best practices & direct support</li>");
        body.append("</ul>");

        body.append("<p>If you have any questions, feedback, or feature requests, just hit reply. We’re here to help you make the most of your TikTok data—<strong>entirely free during this demo</strong>! 🎁</p>");

        body.append("<p style=\"margin-top: 30px;\">Happy scraping! 🥳</p>");

        body.append("<p style=\"margin-top: 20px; font-weight: bold;\">— The ScrapeTok Team</p>");
        body.append("<p style=\"color: #666; font-size: 0.9rem;\">support@scrapetok.com</p>");
        body.append("</div>");


        try {
            GeneralAccount saved = generalAccountRepository.save(newUser);
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
            applicationEventPublisher.publishEvent(
                    new WelcomeEmailEvent(this, request.getEmail(), subject, body.toString())
            );
            UserSignUpResponseDTO dto = modelMapper.map(saved, UserSignUpResponseDTO.class);
            dto.setToken(token);
            dto.setRole(role);
            return dto;
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

    public List<VisualizeAllUsersDTO> getAllUsers() {
        List<GeneralAccount> user = generalAccountRepository.findAll();
        List<VisualizeAllUsersDTO> dtos = new ArrayList<>();
        for (GeneralAccount usuario : user) {
            VisualizeAllUsersDTO dto =  modelMapper.map(usuario,VisualizeAllUsersDTO.class);
            dtos.add(dto);
        }
        return dtos;
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
            GeneralAccount usuario = generalAccountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User with id " + request.getEmail() + " not found"));
            LoginResponseDTO dto = modelMapper.map(usuario,LoginResponseDTO.class);
            dto.setToken(token);
            dto.setRole(role);
            return dto;
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
