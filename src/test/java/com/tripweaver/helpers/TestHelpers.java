package com.tripweaver.helpers;

import com.tripweaver.models.*;
import com.tripweaver.models.enums.FeedbackType;
import com.tripweaver.models.filterOptions.TravelFilterOptions;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.tripweaver.models.*;
import com.tripweaver.models.enums.FeedbackType;
import com.tripweaver.models.filterOptions.TravelFilterOptions;

import java.time.LocalDateTime;
import java.util.HashSet;

public class TestHelpers {

    public static MultipartFile createMockMultipartFile$Ilia() {
        return new MockMultipartFile("String", new byte[10]);
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

    public static TravelFilterOptions createMockTravelFilterOptions$Ilia() {
        return new TravelFilterOptions(
                "City",
                "City",
                String.valueOf(LocalDateTime.now().plusDays(2)),
                String.valueOf(LocalDateTime.now()),
                4,
                "Driver",
                "Comment",
                1,
                "driver",
                "desc");
    }

    public static UserFilterOptions createMockUserFilterOptions$Ilia() {
        return new UserFilterOptions(
                "Username",
                "Email",
                "0888123456",
                "username",
                "desc");
    }
    public static Avatar createAvatarPlamen(){
        Avatar avatar = new Avatar();
        avatar.setAvatarId(1);
        return avatar;
    }

    public static Feedback createFeedbackPlamen(){
        Feedback feedback = new Feedback();
        Travel travel = new Travel();
        User receiver = new User();
        User author = new User();

        feedback.setFeedbackId(1);
        feedback.setRating(2);
        feedback.setFeedbackType(FeedbackType.FOR_DRIVER);
        feedback.setTravel(travel);
        feedback.setCreated(LocalDateTime.now());
        feedback.setReceiver(receiver);
        feedback.setAuthor(author);
        return feedback;
    }

    public static Role createMockRolePlamen() {
        Role mockRole = new Role();
        mockRole.setRoleId(2);
        mockRole.setRoleName("MEMBER");
        return mockRole;
    }

    public static User createMockUserPlamen(){
        User user = new User();
        user.setUserId(1);
        user.setAvatar(createAvatarPlamen());
        user.setBlocked(false);
        user.setDeleted(false);
        user.setVerified(true);
        user.setFirstName("Plamen");
        user.setLastName("Ivanov");
        user.setUsername("Fribble");
        user.setAverageDriverRating(2);
        user.setAveragePassengerRating(2);
        user.setCreated(LocalDateTime.now());
        user.setEmail("plamen@email.com");
        user.setPhoneNumber("08888181818");
        HashSet<Role> roles = new HashSet<>();
        roles.add(createMockRolePlamen());
        user.setRoles(roles);
        return user;
    }

    public static TravelStatus createMockTravelStatusPlamen(){
        TravelStatus travelStatus = new TravelStatus();
        travelStatus.setTravelStatusId(1);
        return travelStatus;
    }

    public static Travel createMockTravelPlamen(){
        Travel travel = new Travel();
        travel.setTravelId(1);
        travel.setCreatedOn(LocalDateTime.now());
        travel.setDepartureTime(LocalDateTime.now().plusMinutes(20));
        travel.setEndingPoint("EndPoint");
        travel.setStartingPoint("StartPoint");
        travel.setStatus(createMockTravelStatusPlamen());
        travel.setDriver(createMockUserPlamen());
        HashSet<User> usersApplied = new HashSet<>();
        HashSet<User> usersApproved = new HashSet<>();
        travel.setUsersAppliedForTheTravel(usersApplied);
        travel.setUsersApprovedForTheTravel(usersApproved);
        return travel;
    }

    public static TravelFilterOptions createMockTravelFilterOptionsPlamen(){
        TravelFilterOptions travelFilterOptions = new TravelFilterOptions(
                "staringPoint",
                "endingPoint",
                "2024-01-31 00:00:00",
                "2024-03-31 00:00:00",
                5,
                "username",
                "comment",
                2,
                null,
                null
        );
        return travelFilterOptions;
    }
}
