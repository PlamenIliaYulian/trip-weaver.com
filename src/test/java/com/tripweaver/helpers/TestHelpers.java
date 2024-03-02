package com.tripweaver.helpers;

import com.tripweaver.models.*;
import com.tripweaver.models.enums.FeedbackType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TestHelpers {

    public static MultipartFile createMockMultipartFile$Ilia() {
        return new MockMultipartFile("String",new byte[10]);
    }
    public static Avatar createMockAvatar$Ilia() {
        Avatar mockAvatar = new Avatar();
        mockAvatar.setAvatarId(1);
        mockAvatar.setAvatarUrl("URL");
        return mockAvatar;
    }
    public static Role createMockRoleAdmin$Ilia() {
        Role mockRole = new Role();
        mockRole.setRoleId(1);
        mockRole.setRoleName("ADMIN");
        return mockRole;
    }

    public static Role createMockRoleMember$Ilia() {
        Role mockRole = new Role();
        mockRole.setRoleId(2);
        mockRole.setRoleName("MEMBER");
        return mockRole;
    }
    public static TravelStatus createMockTravelStatusCreated$Ilia() {
        TravelStatus mockTravelStatus = new TravelStatus();
        mockTravelStatus.setTravelStatusId(1);
        mockTravelStatus.setStatusName("CREATED");
        return mockTravelStatus;
    }
    public static TravelStatus createMockTravelStatusCanceled$Ilia() {
        TravelStatus mockTravelStatus = new TravelStatus();
        mockTravelStatus.setTravelStatusId(2);
        mockTravelStatus.setStatusName("CANCELED");
        return mockTravelStatus;
    }
    public static TravelStatus createMockTravelStatusCompleted$Ilia() {
        TravelStatus mockTravelStatus = new TravelStatus();
        mockTravelStatus.setTravelStatusId(3);
        mockTravelStatus.setStatusName("COMPLETED");
        return mockTravelStatus;
    }

    public static Travel createMockTravel1$Ilia() {
        Travel mockTravel = new Travel();
        mockTravel.setTravelId(1);
        mockTravel.setStartingPoint("MockStartingPoint");
        mockTravel.setStartingPointCity("MockStartingPointCity");
        mockTravel.setStartingPointAddress("MockStartingPointAddress");
        mockTravel.setEndingPoint("MockEndingPoint");
        mockTravel.setEndingPointCity("MockEndingPointCity");
        mockTravel.setEndingPointAddress("MockEndingPointAddress");
        mockTravel.setDepartureTime(LocalDateTime.now().plusDays(10));
        mockTravel.setFreeSeats(4);
        mockTravel.setCreatedOn(LocalDateTime.now());
        mockTravel.setDriver(createMockNonAdminUser1$Ilia());
        mockTravel.setComment("MockComment");
        mockTravel.setRideDurationInMinutes(30);
        mockTravel.setEstimatedArrivalTime(LocalDateTime.now().plusDays(10).plusMinutes(30));
        mockTravel.setDistanceInKm(50);
        mockTravel.setStatus(createMockTravelStatusCreated$Ilia());
        mockTravel.setUsersAppliedForTheTravel(new HashSet<>());

        Set<User> approvedUsersSet = new HashSet<>();
        approvedUsersSet.add(createMockNonAdminUser2$Ilia());
        mockTravel.setUsersApprovedForTheTravel(approvedUsersSet);

        return mockTravel;
    }
    public static Travel createMockTravel2$Ilia() {
        Travel mockTravel = createMockTravel1$Ilia();
        mockTravel.setTravelId(2);
        mockTravel.setDriver(createMockNonAdminUser2$Ilia());
        mockTravel.setUsersAppliedForTheTravel(new HashSet<>());

        Set<User> approvedUsersSet = new HashSet<>();
        approvedUsersSet.add(createMockNonAdminUser1$Ilia());
        mockTravel.setUsersApprovedForTheTravel(approvedUsersSet);

        return mockTravel;
    }
    public static Feedback createMockFeedbackForUser1ForDriver$Ilia() {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setFeedbackId(1);
        mockFeedback.setAuthor(createMockNonAdminUser2$Ilia());
        mockFeedback.setReceiver(createMockNonAdminUser1$Ilia());
        mockFeedback.setRating(3);
        mockFeedback.setFeedbackType(FeedbackType.FOR_DRIVER);
        mockFeedback.setCreated(LocalDateTime.now());
        mockFeedback.setContent("MockContent");
        mockFeedback.setTravel(createMockTravel1$Ilia());
        return mockFeedback;
    }
    public static Feedback createMockFeedbackForUser2ForDriver$Ilia() {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setFeedbackId(2);
        mockFeedback.setAuthor(createMockNonAdminUser1$Ilia());
        mockFeedback.setReceiver(createMockNonAdminUser2$Ilia());
        mockFeedback.setRating(3);
        mockFeedback.setFeedbackType(FeedbackType.FOR_DRIVER);
        mockFeedback.setCreated(LocalDateTime.now());
        mockFeedback.setContent("MockContent");
        mockFeedback.setTravel(createMockTravel2$Ilia());
        return mockFeedback;

    }
    public static Feedback createMockFeedbackForUser1ForPassenger$Ilia() {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setFeedbackId(3);
        mockFeedback.setAuthor(createMockNonAdminUser2$Ilia());
        mockFeedback.setReceiver(createMockNonAdminUser1$Ilia());
        mockFeedback.setRating(3);
        mockFeedback.setFeedbackType(FeedbackType.FOR_PASSENGER);
        mockFeedback.setCreated(LocalDateTime.now());
        mockFeedback.setContent("MockContent");
        mockFeedback.setTravel(createMockTravel2$Ilia());
        return mockFeedback;

    }
    public static Feedback createMockFeedbackForUser2ForPassenger$Ilia() {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setFeedbackId(4);
        mockFeedback.setAuthor(createMockNonAdminUser1$Ilia());
        mockFeedback.setReceiver(createMockNonAdminUser2$Ilia());
        mockFeedback.setRating(3);
        mockFeedback.setFeedbackType(FeedbackType.FOR_PASSENGER);
        mockFeedback.setCreated(LocalDateTime.now());
        mockFeedback.setContent("MockContent");
        mockFeedback.setTravel(createMockTravel1$Ilia());
        return mockFeedback;
    }

    public static User createMockNonAdminUser1$Ilia() {
        User mockNonAdminUser = new User();
        mockNonAdminUser.setUserId(1);
        mockNonAdminUser.setUsername("MockUsername");
        mockNonAdminUser.setPassword("MockPassword");
        mockNonAdminUser.setFirstName("MockFirstName");
        mockNonAdminUser.setLastName("MockLastName");
        mockNonAdminUser.setEmail("mock@email.com");
        mockNonAdminUser.setPhoneNumber("08123456");
        mockNonAdminUser.setCreated(LocalDateTime.now());
        mockNonAdminUser.setDeleted(false);
        mockNonAdminUser.setVerified(true);
        mockNonAdminUser.setBlocked(false);
        mockNonAdminUser.setAveragePassengerRating(3.5);
        mockNonAdminUser.setAverageDriverRating(4.2);
        mockNonAdminUser.setAvatar(createMockAvatar$Ilia());
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(createMockRoleMember$Ilia());
        mockNonAdminUser.setRoles(roleSet);
        mockNonAdminUser.setFeedback(new HashSet<>());
        return mockNonAdminUser;
    }
    public static User createMockNonAdminUser2$Ilia() {
        User mockNonAdminUser = new User();
        mockNonAdminUser.setUserId(2);
        mockNonAdminUser.setUsername("MockUsername");
        mockNonAdminUser.setPassword("MockPassword");
        mockNonAdminUser.setFirstName("MockFirstName");
        mockNonAdminUser.setLastName("MockLastName");
        mockNonAdminUser.setEmail("mock@email.com");
        mockNonAdminUser.setPhoneNumber("08123456");
        mockNonAdminUser.setCreated(LocalDateTime.now());
        mockNonAdminUser.setDeleted(false);
        mockNonAdminUser.setVerified(true);
        mockNonAdminUser.setBlocked(false);
        mockNonAdminUser.setAveragePassengerRating(3.5);
        mockNonAdminUser.setAverageDriverRating(4.2);
        mockNonAdminUser.setAvatar(createMockAvatar$Ilia());
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(createMockRoleMember$Ilia());
        mockNonAdminUser.setRoles(roleSet);
        mockNonAdminUser.setFeedback(new HashSet<>());
        return mockNonAdminUser;
    }

}
