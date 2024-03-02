package com.tripweaver.services;

import com.tripweaver.repositories.contracts.FeedbackRepository;
import com.tripweaver.services.contracts.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTests {

    @Mock
    FeedbackRepository feedbackRepository;
    @InjectMocks
    FeedbackServiceImpl feedbackService;

    /*Ilia*/
    @Test
    public void getFeedbackForPassengerById_Should_CallRepository() {
        feedbackService.getFeedbackForPassengerById(Mockito.anyInt());

        Mockito.verify(feedbackRepository,Mockito.times(1))
                .getFeedbackForPassengerById(Mockito.anyInt());
    }
}
