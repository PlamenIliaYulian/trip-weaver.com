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

import static com.tripweaver.services.helpers.ConstantHelper.*;

public class TestHelpers {

    public static MultipartFile createMockMultipartFile() {
        return new MockMultipartFile("String", new byte[10]);
    }

    public static CarPicture createMockCarPicture() {
        CarPicture mockCarPicture = new CarPicture();
        mockCarPicture.setCarPictureId(DEFAULT_CAR_PICTURE_ID);
        mockCarPicture.setCarPictureUrl("URL");
        return mockCarPicture;
    }
    public static Avatar createMockAvatar() {
        Avatar mockAvatar = new Avatar();
        mockAvatar.setAvatarId(DEFAULT_AVATAR_ID);
        mockAvatar.setAvatarUrl("URL");
        return mockAvatar;
    }

    public static Role createMockRoleAdmin() {
        Role mockRole = new Role();
        mockRole.setRoleId(ADMIN_ID);
        mockRole.setRoleName("ADMIN");
        return mockRole;
    }

    public static Role createMockRoleMember() {
        Role mockRole = new Role();
        mockRole.setRoleId(ROLE_MEMBER_ID);
        mockRole.setRoleName("MEMBER");
        return mockRole;
    }

    public static TravelStatus createMockTravelStatusCreated() {
        TravelStatus travelStatus = new TravelStatus();
        travelStatus.setTravelStatusId(TRAVEL_STATUS_CREATED_ID);
        travelStatus.setStatusName("CREATED");
        return travelStatus;
    }

    public static TravelStatus createMockTravelStatusCanceled() {
        TravelStatus mockTravelStatus = new TravelStatus();
        mockTravelStatus.setTravelStatusId(TRAVEL_STATUS_CANCEL_ID);
        mockTravelStatus.setStatusName("CANCELED");
        return mockTravelStatus;
    }

    public static TravelStatus createMockTravelStatusCompleted() {
        TravelStatus mockTravelStatus = new TravelStatus();
        mockTravelStatus.setTravelStatusId(TRAVEL_STATUS_COMPLETED);
        mockTravelStatus.setStatusName("COMPLETED");
        return mockTravelStatus;
    }

    public static Travel createMockTravel1() {
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
        mockTravel.setDriver(createMockNonAdminUser1());
        mockTravel.setComment("MockComment");
        mockTravel.setRideDurationInMinutes(30);
        mockTravel.setEstimatedArrivalTime(LocalDateTime.now().plusDays(10).plusMinutes(30));
        mockTravel.setDistanceInKm(50);
        mockTravel.setStatus(createMockTravelStatusCreated());
        mockTravel.setUsersAppliedForTheTravel(new HashSet<>());

        Set<User> approvedUsersSet = new HashSet<>();
        approvedUsersSet.add(createMockNonAdminUser2());
        mockTravel.setUsersApprovedForTheTravel(approvedUsersSet);

        return mockTravel;
    }

    public static Travel createMockTravel2() {
        Travel mockTravel = createMockTravel1();
        mockTravel.setTravelId(2);
        mockTravel.setDriver(createMockNonAdminUser2());
        mockTravel.setUsersAppliedForTheTravel(new HashSet<>());

        Set<User> approvedUsersSet = new HashSet<>();
        approvedUsersSet.add(createMockNonAdminUser1());
        mockTravel.setUsersApprovedForTheTravel(approvedUsersSet);

        return mockTravel;
    }

    public static Feedback createMockFeedbackForUser1ForDriver() {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setFeedbackId(1);
        mockFeedback.setAuthor(createMockNonAdminUser2());
        mockFeedback.setReceiver(createMockNonAdminUser1());
        mockFeedback.setRating(3);
        mockFeedback.setFeedbackType(FeedbackType.FOR_DRIVER);
        mockFeedback.setCreated(LocalDateTime.now());
        mockFeedback.setContent("MockContent");
        mockFeedback.setTravel(createMockTravel1());
        return mockFeedback;
    }

    public static Feedback createMockFeedbackForUser1ForPassenger() {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setFeedbackId(3);
        mockFeedback.setAuthor(createMockNonAdminUser2());
        mockFeedback.setReceiver(createMockNonAdminUser1());
        mockFeedback.setRating(3);
        mockFeedback.setFeedbackType(FeedbackType.FOR_PASSENGER);
        mockFeedback.setCreated(LocalDateTime.now());
        mockFeedback.setContent("MockContent");
        mockFeedback.setTravel(createMockTravel2());
        return mockFeedback;

    }

    public static User createMockNonAdminUser2() {
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
        mockNonAdminUser.setAvatar(createMockAvatar());
        mockNonAdminUser.setCarPicture(createMockCarPicture());
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(createMockRoleMember());
        mockNonAdminUser.setRoles(roleSet);
        mockNonAdminUser.setFeedback(new HashSet<>());
        return mockNonAdminUser;
    }

    public static TravelFilterOptions createMockTravelFilterOptions() {
        return new TravelFilterOptions(
                "City",
                "City",
                String.valueOf(LocalDateTime.now().plusDays(2)),
                String.valueOf(LocalDateTime.now()),
                4,
                "Driver",
                "Comment",
                TRAVEL_STATUS_CREATED_ID,
                "driver",
                "desc");
    }

    public static UserFilterOptions createMockUserFilterOptions() {
        return new UserFilterOptions(
                "Username",
                "Email",
                "0888123456",
                "username",
                "desc");
    }

    public static User createMockNonAdminUser1() {
        User user = new User();
        user.setUserId(1);
        user.setAvatar(createMockAvatar());
        user.setCarPicture(createMockCarPicture());
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
        user.setFeedback(new HashSet<>());
        roles.add(createMockRoleMember());
        user.setRoles(roles);
        return user;
    }
}
