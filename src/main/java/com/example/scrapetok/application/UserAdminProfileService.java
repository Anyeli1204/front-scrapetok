package com.example.scrapetok.application;

import com.example.scrapetok.domain.*;
import com.example.scrapetok.domain.DTO.AdminProfileResponseDTO;
import com.example.scrapetok.domain.DTO.UserProfileResponseDTO;
import com.example.scrapetok.domain.enums.ApifyRunStatus;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.repository.GeneralAccountRepository;
import com.example.scrapetok.repository.UserTiktokMetricsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserAdminProfileService {
    @Autowired
    private GeneralAccountRepository generalAccountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserTiktokMetricsRepository userTiktokMetricsRepository;

    public UserProfileResponseDTO getUserProfile(Long userId) throws ResourceNotFoundException {
        GeneralAccount user = generalAccountRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User with id " + " Not Found"));
        UserProfileResponseDTO userProfile = modelMapper.map(user, UserProfileResponseDTO.class);

        UserApifyCallHistorial userHistorial = user.getHistorial();
        if (userHistorial == null) {
            userProfile.setAmountScrappedAccount(0);
            userProfile.setFilters(new ArrayList<>());
            userProfile.setTiktokUsernameScraped(Collections.emptySet());
            throw new ResourceNotFoundException("Historial not found for user " + userId);
        }

        List<Map<Object,Object>> filterMatrix = new ArrayList<>();
        List<UserApifyFilters> historialFilters = userHistorial.getFiltros();
        for (UserApifyFilters filter: historialFilters) {
            if (filter.getApifyRunStatus() == ApifyRunStatus.COMPLETED) {
                Map<Object,Object> filterHashmap= new HashMap<>();
                filterHashmap.put("Hashtags", filter.getHashtags());
                filterHashmap.put("Date From", filter.getDateFrom());
                filterHashmap.put("Date to", filter.getDateTo());
                filterHashmap.put("Key Word", filter.getKeyWords());
                filterHashmap.put("N Last Post By Hashtags", filter.getNlastPostByHashtags());
                filterHashmap.put("Tiktok Usernames", filter.getTiktokAccount());
                filterHashmap.put("Execution Time", filter.getExecutionTime());
                filterMatrix.add(filterHashmap);
            }
        }
        userProfile.setFilters(filterMatrix);

        Set<String> usernames = new HashSet<>();
        List<UserTiktokMetrics> getUsernames = userTiktokMetricsRepository.findUsernameTiktokAccountByUserId(userId);
        if (!getUsernames.isEmpty()) {
            for (UserTiktokMetrics userMetrics : getUsernames) {
                usernames.add(userMetrics.getUsernameTiktokAccount());
            }
            userProfile.setTiktokUsernameScraped(usernames);
        }
        userProfile.setAmountScrappedAccount(usernames.size());
        return userProfile;
    }


    public AdminProfileResponseDTO getAdminProfile(Long adminId) throws ResourceNotFoundException  {
        GeneralAccount user = generalAccountRepository.findById(adminId).orElseThrow(()-> new ResourceNotFoundException ("User with id " + " Not Found"));
        AdminProfileResponseDTO userProfile = modelMapper.map(user, AdminProfileResponseDTO.class);
        AdminProfile admin = user.getAdmin();
        if (admin == null) {
            return userProfile;
        }
        modelMapper.map(admin,userProfile);

        List<Map<String,String>> questionAnswers = null;
        if (admin.getAnswers() != null) {
            questionAnswers = new ArrayList<>();
            for (QuestAndAnswer qa : admin.getAnswers()) {
                Map<String,String> map = new HashMap<>();
                map.put(qa.getQuestionDescription(), qa.getAnswerDescription());
                questionAnswers.add(map);
            }
        }
        userProfile.setQuestionAndAnswer(questionAnswers);

        List<Map<Long,String>> dailyAlerts = null;
        if (admin.getAlert() != null) {
            dailyAlerts = new ArrayList<>();
            for (DailyAlerts alert : admin.getAlert()) {
                Map<Long,String> map = new HashMap<>();
                map.put(alert.getId(), alert.getPostedDate() != null
                        ? alert.getPostedDate().toString()
                        : null);
                dailyAlerts.add(map);
            }
        }
        userProfile.setEmmitedAlerts(dailyAlerts);
        return userProfile;
    }
}
