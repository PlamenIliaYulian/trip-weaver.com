package com.tripweaver.services.helpers;

public class ConstantHelper {
    public static final String KEY_COORDINATES = "coordinates";
    public static final String KEY_CITY = "city";
    public static final String KEY_TRAVEL_DISTANCE = "travelDistance";
    public static final String KEY_TRAVEL_DURATION = "travelDuration";
    public static final int TRAVEL_STATUS_CREATED_ID = 1;
    public static final int ADMIN_ID = 1;
    public static final int COMPLETED_STATUS = 3;
    public static final String UNAUTHORIZED_OPERATION_BLOCKED = "Unauthorized operation. User blocked.";
    public static final String UNAUTHORIZED_OPERATION_NOT_VERIFIED = "Unauthorized operation. User not verified.";
    public static final String UNAUTHORIZED_OPERATION_NOT_DRIVER = "Unauthorized operation. User not driver of the travel.";
    public static final String INVALID_OPERATION_NOT_DRIVER = "Invalid operation. User not driver of the travel.";
    public static final String UNAUTHORIZED_OPERATION_ALREADY_APPLIED = "Unauthorized operation. User already in waiting list.";
    public static final String USER_NOT_IN_TRAVEL_LISTS = "The user is neither in the waiting list nor in the approved list.";
    public static final int TRAVEL_STATUS_CANCEL_ID = 2;
    public static final int TRAVEL_STATUS_COMPLETE_ID = 3;
    public static final String TRAVEL_NOT_AVAILABLE = "Travel not available";
    public static final String INVALID_OPERATION = "User has not applied for this travel";
    public static final String INVALID_OPERATION_DRIVER = "User is the driver, so could not leaver Driver's feedback";
    public static final String INVALID_DEPARTURE_TIME = "Departure time cannot be before current moment.";
    public static final String UNAUTHORIZED_OPERATION_NOT_ADMIN = "Unauthorized operation. User not admin.";
    public static final String UNAUTHORIZED_OPERATION_NOT_SAME_USER = "Unauthorized operation. Not same user.";
    public static final String TRAVEL_NOT_COMPLETED_CANNOT_LEAVE_FEEDBACK = "Travel not completed and cannot leave feedback.";
    public static final String USER_NOT_IN_APPROVED_LIST = "The user is not in the approved list.";
    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    public static final String YOU_HAVE_ALREADY_LEFT_FEEDBACK_FOR_THIS_RIDE = "You have already left feedback for this ride.";

    public static final String NO_FREE_SEATS_ARE_AVAILABLE_MESSAGE ="No free seats are available.";

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    public static final String LOGGED_USER_ERROR = "No user logged in.";

}
