package com.example.betickettrain.service;

import com.example.betickettrain.entity.Trip;

import java.util.List;

public interface NotificationService {
    void notifyUsers(Trip trip, List<String> userEmails);
}
