package com.example.betickettrain.util;

public final class Constants {
    public static final String qrBaseUrl="https://localhost:8080/api/bookings/checkin?code=";
    public static final class Action {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String LOGIN = "LOGIN";


    }

    public static final class Role {
        public static final String ROLE_USER = "ROLE_USER";
        public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_MANAGER = "ROLE_MANAGER";
        public static final String ROLE_SUPER_ADMIN = "ROLE_STAFF";
    }

    public static final class User {
        public static final String USER_NOT_FOUND = "User not found";
        public static final String USER_ALREADY_EXISTS = "User already exists";
        public static final String USER_DELETED = "User deleted";
        public static final String USER_UPDATED = "User updated";
        public static final String USER_CREATED = "User created";
        public static final String USER_LOGIN_SUCCESS = "User login success";
        public static final String STATUS_ACTIVE = "active";
        public static final String STATUS_BAN = "banned";
        public static final String STATUS_INACTIVE = "inactive";
    }

    public static final class Cache {
        public static final String CACHE_NEWFEED = "newFeed";
        public static final String CACHE_FEED = "feed";
        public static final String CACHE_ROUTE = "route";
        public static final String CACHE_STATION = "station";
        public static final String CACHE_SEAT = "seat";
        public static final String CACHE_CARRIAGE = "carriage";
        public static final String CACHE_SETTING = "setting";
        public static final String CACHE_USER = "user";
        public static final String CACHE_FEEDBACK = "feedback";
        public static final String CACHE_PROMOTION = "promotion";
        public static final String CACHE_BOOKING = "booking";
        public static final String CACHE_BOOKING_PROMOTION = "bookingPromotion";
        public static final String CACHE_BOOKING_SEAT = "bookingSeat";
        public static final String CACHE_BOOKING_PAYMENT = "bookingPayment";
        public static final String CACHE_TRAIN = "train";
        public static final String CACHE_TRIP = "trip";
        public static final String CACHE_ROUTES_STATION = "routes_station";
        public static final String CACHE_STATISTICS = "CACHE_STATISTICS";


        public static final String CACHE_CARRIAGE_WITH_SEATS = "carriage_with_seats";
        public static final String CACHE_PAYMENT = "payment";
    }
    public static final class Ticket {
        public static final String TICKET_NOT_FOUND = "Ticket not found";
        public static final String TICKET_CREATED = "Ticket created";
        public static final String TICKET_UPDATED = "Ticket updated";
        public static final String TICKET_DELETED = "Ticket deleted";
        public static final String TICKET_STATUS_BOOKED = "booked";
        public static final String TICKET_STATUS_CHECKED_IN = "checked_in";
        public static final String TICKET_STATUS_USED = "used";
        public static final String TICKET_STATUS_CANCELLED = "cancelled";
    }
    public static final class Booking {
        public static final String BOOKING_NOT_FOUND = "Booking not found";
        public static final String BOOKING_CREATED = "Booking created";
        public static final String BOOKING_UPDATED = "Booking updated";
        public static final String BOOKING_DELETED = "Booking deleted";
        public static final String BOOKING_STATUS_PENDING = "pending";
        public static final String BOOKING_STATUS_CONFIRMED = "confirmed";
        public static final String BOOKING_STATUS_CANCELLED = "cancelled";
        public static final String BOOKING_STATUS_COMPLETED = "completed";
    }
    public static final String REASON_CANCELLED = "Hủy vé bởi quản lý do vi phạm quy định";
}
