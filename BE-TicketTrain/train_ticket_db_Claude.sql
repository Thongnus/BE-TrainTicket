-- Tạo cơ sở dữ liệu

-- Bảng lưu thông tin người dùng
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       phone VARCHAR(20) NOT NULL,
                       address VARCHAR(255),
                       id_card VARCHAR(20) NOT NULL UNIQUE,
                       date_of_birth DATE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       INDEX idx_username (username),
                       INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE roles (
                       role_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
                             user_id INT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users(user_id),
                             FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- Bảng lưu thông tin ga tàu
CREATE TABLE stations (
    station_id INT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    status ENUM('active', 'maintenance', 'closed') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_station_name (station_name),
    INDEX idx_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin tuyến đường
CREATE TABLE routes (
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    route_name VARCHAR(100) NOT NULL,
    origin_station_id INT NOT NULL,
    destination_station_id INT NOT NULL,
    distance FLOAT NOT NULL COMMENT 'Distance in kilometers',
    description TEXT,
    status ENUM('active', 'inactive', 'maintenance') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (origin_station_id) REFERENCES stations(station_id) ON DELETE RESTRICT,
    FOREIGN KEY (destination_station_id) REFERENCES stations(station_id) ON DELETE RESTRICT,
    INDEX idx_route_name (route_name),
    UNIQUE KEY unique_route (origin_station_id, destination_station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu các ga trung gian trên tuyến
CREATE TABLE route_stations (
    route_station_id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    station_id INT NOT NULL,
    stop_order INT NOT NULL COMMENT 'Order of the station in the route',
    arrival_offset INT NOT NULL COMMENT 'Minutes from route start time',
    departure_offset INT NOT NULL COMMENT 'Minutes from route start time',
    distance_from_origin FLOAT NOT NULL COMMENT 'Distance in kilometers from origin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations(station_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_station_in_route (route_id, station_id),
    UNIQUE KEY unique_order_in_route (route_id, stop_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin tàu
CREATE TABLE trains (
    train_id INT AUTO_INCREMENT PRIMARY KEY,
    train_number VARCHAR(20) NOT NULL UNIQUE,
    train_name VARCHAR(100),
    train_type ENUM('express', 'local', 'fast', 'sleeper') NOT NULL,
    capacity INT NOT NULL,
    status ENUM('active', 'maintenance', 'retired') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_train_number (train_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin toa tàu
CREATE TABLE carriages (
    carriage_id INT AUTO_INCREMENT PRIMARY KEY,
    train_id INT NOT NULL,
    carriage_number VARCHAR(10) NOT NULL,
    carriage_type ENUM('hard_seat', 'soft_seat', 'hard_sleeper', 'soft_sleeper', 'vip') NOT NULL,
    capacity INT NOT NULL,
    status ENUM('active', 'maintenance', 'retired') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (train_id) REFERENCES trains(train_id) ON DELETE CASCADE,
    UNIQUE KEY unique_carriage (train_id, carriage_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin chỗ ngồi/giường
CREATE TABLE seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    carriage_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    seat_type ENUM('window', 'aisle', 'middle', 'lower_berth', 'middle_berth', 'upper_berth') NOT NULL,
    status ENUM('active', 'maintenance', 'unavailable') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (carriage_id) REFERENCES carriages(carriage_id) ON DELETE CASCADE,
    UNIQUE KEY unique_seat (carriage_id, seat_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin chuyến tàu
CREATE TABLE trips (
    trip_id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    train_id INT NOT NULL,
    trip_code VARCHAR(20) NOT NULL UNIQUE,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    status ENUM('scheduled', 'delayed', 'cancelled', 'completed') NOT NULL DEFAULT 'scheduled',
    delay_minutes INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE RESTRICT,
    FOREIGN KEY (train_id) REFERENCES trains(train_id) ON DELETE RESTRICT,
    INDEX idx_departure_time (departure_time),
    INDEX idx_arrival_time (arrival_time),
    INDEX idx_trip_code (trip_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu lịch trình chi tiết cho các chuyến tàu
CREATE TABLE trip_schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    trip_id INT NOT NULL,
    station_id INT NOT NULL,
    scheduled_arrival DATETIME,
    scheduled_departure DATETIME,
    actual_arrival DATETIME,
    actual_departure DATETIME,
    status ENUM('scheduled', 'arrived', 'departed', 'cancelled', 'delayed') NOT NULL DEFAULT 'scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations(station_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_station_in_trip (trip_id, station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin giá vé
CREATE TABLE ticket_prices (
    price_id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    carriage_type ENUM('hard_seat', 'soft_seat', 'hard_sleeper', 'soft_sleeper', 'vip') NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    weekend_surcharge DECIMAL(10,2) DEFAULT 0,
    holiday_surcharge DECIMAL(10,2) DEFAULT 0,
    peak_hour_surcharge DECIMAL(10,2) DEFAULT 0,
    discount_rate DECIMAL(5,2) DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE,
    UNIQUE KEY unique_price (route_id, carriage_type, start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin đặt vé
CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('pending', 'paid', 'refunded', 'cancelled') NOT NULL DEFAULT 'pending',
    booking_status ENUM('pending', 'confirmed', 'cancelled', 'completed') NOT NULL DEFAULT 'pending',
    payment_method ENUM('credit_card', 'bank_transfer', 'e_wallet', 'cash') NOT NULL,
    payment_date DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_booking_code (booking_code),
    INDEX idx_booking_date (booking_date),
    INDEX idx_payment_status (payment_status),
    INDEX idx_booking_status (booking_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin chi tiết vé
CREATE TABLE tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    trip_id INT NOT NULL,
    seat_id INT NOT NULL,
    origin_station_id INT NOT NULL,
    destination_station_id INT NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    passenger_id_card VARCHAR(20) NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL,
    ticket_code VARCHAR(20) NOT NULL UNIQUE,
    ticket_status ENUM('booked', 'checked_in', 'cancelled', 'used', 'expired') NOT NULL DEFAULT 'booked',
    boarding_time DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE RESTRICT,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE RESTRICT,
    FOREIGN KEY (origin_station_id) REFERENCES stations(station_id) ON DELETE RESTRICT,
    FOREIGN KEY (destination_station_id) REFERENCES stations(station_id) ON DELETE RESTRICT,
    INDEX idx_ticket_code (ticket_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin thanh toán
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    payment_amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('credit_card', 'bank_transfer', 'e_wallet', 'cash') NOT NULL,
    transaction_id VARCHAR(100),
    payment_status ENUM('pending', 'completed', 'failed', 'refunded') NOT NULL DEFAULT 'pending',
    payment_details JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin hoàn vé/hủy vé
CREATE TABLE cancellations (
    cancellation_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    cancellation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    refund_amount DECIMAL(10,2),
    cancellation_fee DECIMAL(10,2),
    reason TEXT,
    status ENUM('pending', 'approved', 'rejected', 'refunded') NOT NULL DEFAULT 'pending',
    processed_by INT,
    processed_date DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_cancellation_date (cancellation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu lịch sử thay đổi vé
CREATE TABLE ticket_changes (
    change_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    old_trip_id INT,
    new_trip_id INT NOT NULL,
    old_seat_id INT,
    new_seat_id INT NOT NULL,
    change_fee DECIMAL(10,2) NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason TEXT,
    status ENUM('pending', 'processed', 'cancelled') NOT NULL DEFAULT 'pending',
    processed_by INT,
    processed_date DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE,
    FOREIGN KEY (old_trip_id) REFERENCES trips(trip_id) ON DELETE RESTRICT,
    FOREIGN KEY (new_trip_id) REFERENCES trips(trip_id) ON DELETE RESTRICT,
    FOREIGN KEY (old_seat_id) REFERENCES seats(seat_id) ON DELETE RESTRICT,
    FOREIGN KEY (new_seat_id) REFERENCES seats(seat_id) ON DELETE RESTRICT,
    FOREIGN KEY (processed_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_change_date (change_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu nhật ký hệ thống
CREATE TABLE system_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id INT,
    description TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_log_time (log_time),
    INDEX idx_action (action),
    INDEX idx_entity_type (entity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin khuyến mãi/giảm giá
CREATE TABLE promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_code VARCHAR(20) NOT NULL UNIQUE,
    promotion_name VARCHAR(100) NOT NULL,
    description TEXT,
    discount_type ENUM('percentage', 'fixed_amount') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    minimum_purchase DECIMAL(10,2) DEFAULT 0,
    maximum_discount DECIMAL(10,2),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    usage_limit INT,
    usage_count INT DEFAULT 0,
    status ENUM('active', 'inactive', 'expired') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_promotion_code (promotion_code),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- bảng lưu new feed
CREATE TABLE newfeed (
                         newfeed_id INT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- Bảng lưu thông tin sử dụng khuyến mãi
CREATE TABLE booking_promotions (
    booking_promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    promotion_id INT NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE CASCADE,
    UNIQUE KEY unique_booking_promotion (booking_id, promotion_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin đánh giá/feedback
CREATE TABLE feedbacks (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    trip_id INT,
    rating INT NOT NULL,
    comment TEXT,
    feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE,
    INDEX idx_feedback_date (feedback_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thông tin thông báo
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type ENUM('booking', 'payment', 'system', 'promotion', 'trip_update') NOT NULL,
    related_id INT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng lưu thiết lập hệ thống
CREATE TABLE settings (
    setting_id INT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    setting_group VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_setting_key (setting_key),
    INDEX idx_setting_group (setting_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE carriage_amenities (
                                    carriage_amenity_id INT AUTO_INCREMENT PRIMARY KEY,
                                    carriage_id INT NOT NULL,
                                    wifi BOOLEAN DEFAULT FALSE,
                                    power_plug BOOLEAN DEFAULT FALSE,
                                    food BOOLEAN DEFAULT FALSE,
                                    tv BOOLEAN DEFAULT FALSE,
                                    massage_chair BOOLEAN DEFAULT FALSE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    FOREIGN KEY (carriage_id) REFERENCES carriages(carriage_id) ON DELETE CASCADE,
                                    UNIQUE KEY unique_carriage_amenity (carriage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- Tạo View hiển thị số lượng ghế còn trống theo chuyến tàu
CREATE VIEW available_seats_view AS
SELECT 
    t.trip_id,
    t.trip_code,
    t.departure_time,
    t.arrival_time,
    r.route_name,
    tr.train_number,
    s.station_name AS origin_station,
    ds.station_name AS destination_station,
    c.carriage_type,
    COUNT(se.seat_id) AS total_seats,
    COUNT(se.seat_id) - COUNT(ti.ticket_id) AS available_seats
FROM trips t
JOIN routes r ON t.route_id = r.route_id
JOIN trains tr ON t.train_id = tr.train_id
JOIN stations s ON r.origin_station_id = s.station_id
JOIN stations ds ON r.destination_station_id = ds.station_id
JOIN carriages c ON c.train_id = tr.train_id
JOIN seats se ON se.carriage_id = c.carriage_id
LEFT JOIN tickets ti ON ti.trip_id = t.trip_id AND ti.seat_id = se.seat_id AND ti.ticket_status = 'booked'
WHERE t.departure_time > NOW() AND t.status = 'scheduled'
GROUP BY t.trip_id, c.carriage_type;

-- Tạo View cho lịch trình chuyến tàu chi tiết
CREATE VIEW trip_schedule_details AS
SELECT 
    ts.schedule_id,
    t.trip_id,
    t.trip_code,
    s.station_name,
    ts.scheduled_arrival,
    ts.scheduled_departure,
    ts.actual_arrival,
    ts.actual_departure,
    ts.status
FROM trip_schedules ts
JOIN trips t ON ts.trip_id = t.trip_id
JOIN stations s ON ts.station_id = s.station_id
ORDER BY t.trip_id, ts.scheduled_arrival;

-- Tạo View cho thống kê doanh thu
CREATE VIEW revenue_statistics AS
SELECT 
    DATE(b.booking_date) AS booking_day,
    COUNT(b.booking_id) AS total_bookings,
    SUM(b.total_amount) AS total_revenue,
    SUM(CASE WHEN b.payment_status = 'paid' THEN b.total_amount ELSE 0 END) AS paid_revenue,
    COUNT(DISTINCT b.user_id) AS unique_customers
FROM bookings b
GROUP BY DATE(b.booking_date);

-- Tạo View cho thống kê vé đã bán theo tuyến và loại tàu
CREATE VIEW ticket_sales_by_route AS
SELECT 
    r.route_name,
    tr.train_type,
    c.carriage_type,
    COUNT(t.ticket_id) AS tickets_sold,
    SUM(t.ticket_price) AS total_sales
FROM tickets t
JOIN trips tp ON t.trip_id = tp.trip_id
JOIN routes r ON tp.route_id = r.route_id
JOIN trains tr ON tp.train_id = tr.train_id
JOIN seats s ON t.seat_id = s.seat_id
JOIN carriages c ON s.carriage_id = c.carriage_id
WHERE t.ticket_status IN ('booked', 'checked_in', 'used')
GROUP BY r.route_name, tr.train_type, c.carriage_type;

-- Tạo Stored Procedure tìm kiếm chuyến tàu
DELIMITER //
DELIMITER //
CREATE PROCEDURE search_trips(
    IN p_origin_station_id INT,
    IN p_destination_station_id INT,
    IN p_departure_date DATE,
    IN p_passengers INT
)
BEGIN
SELECT
    t.trip_id,
    t.trip_code,
    t.departure_time,
    t.arrival_time,
    r.route_name,
    tr.train_number,
    tr.train_type,
    os.station_name AS origin_station,
    ds.station_name AS destination_station,
    TIMEDIFF(t.arrival_time, t.departure_time) AS duration,
    MIN(tp.base_price) AS min_price,
    MAX(tp.base_price) AS max_price,
    GROUP_CONCAT(
            CONCAT(
                    c.carriage_type, ': ',
                    'wifi=', IF(ca.wifi, 'true', 'false'), ', ',
                    'powerPlug=', IF(ca.power_plug, 'true', 'false'), ', ',
                    'food=', IF(ca.food, 'true', 'false'), ', ',
                    'tv=', IF(ca.tv, 'true', 'false'), ', ',
                    'massageChair=', IF(ca.massage_chair, 'true', 'false')
            ) SEPARATOR ' | '
    ) AS amenities
FROM trips t
         JOIN routes r ON t.route_id = r.route_id
         JOIN trains tr ON t.train_id = tr.train_id
         JOIN route_stations rs_origin ON rs_origin.route_id = r.route_id AND rs_origin.station_id = p_origin_station_id
         JOIN route_stations rs_dest ON rs_dest.route_id = r.route_id AND rs_dest.station_id = p_destination_station_id
         JOIN stations os ON p_origin_station_id = os.station_id
         JOIN stations ds ON p_destination_station_id = ds.station_id
         JOIN ticket_prices tp ON tp.route_id = r.route_id AND tp.start_date <= p_departure_date AND tp.end_date >= p_departure_date
         JOIN carriages c ON c.train_id = tr.train_id
         LEFT JOIN carriage_amenities ca ON c.carriage_id = ca.carriage_id
WHERE DATE(t.departure_time) = p_departure_date
  AND rs_origin.stop_order < rs_dest.stop_order
  AND t.status = 'scheduled'
GROUP BY t.trip_id
HAVING (
    SELECT MIN(av.available_seats)
    FROM available_seats_view av
    WHERE av.trip_id = t.trip_id
    ) >= p_passengers
ORDER BY t.departure_time;
END //
DELIMITER ;

-- Tạo Stored Procedure tạo đặt vé
DELIMITER //
CREATE PROCEDURE create_booking(
    IN p_user_id INT,
    IN p_trip_id INT,
    IN p_origin_station_id INT,
    IN p_destination_station_id INT,
    IN p_passengers JSON,
    IN p_payment_method VARCHAR(20),
    OUT p_booking_id INT,
    OUT p_booking_code VARCHAR(20)
)
BEGIN
    DECLARE v_total_amount DECIMAL(10,2) DEFAULT 0;
    DECLARE v_booking_code VARCHAR(20);
    DECLARE v_passenger_data JSON;
    DECLARE v_seat_id INT;
    DECLARE v_passenger_name VARCHAR(100);
    DECLARE v_passenger_id_card VARCHAR(20);
    DECLARE v_ticket_price DECIMAL(10,2);
    DECLARE v_ticket_code VARCHAR(20);
    DECLARE i INT DEFAULT 0;
    DECLARE v_passenger_count INT;
    
    -- Tạo booking code
    SET v_booking_code = CONCAT('BK', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(FLOOR(RAND() * 9999), 4, '0'));
    
    -- Tính tổng tiền
    SELECT SUM(tp.base_price) INTO v_total_amount
    FROM ticket_prices tp
    JOIN routes r ON tp.route_id = r.route_id
    JOIN trips t ON t.route_id = r.route_id
    JOIN carriages c ON c.train_id = t.train_id
    WHERE t.trip_id = p_trip_id
    AND JSON_CONTAINS_PATH(p_passengers, 'one', CONCAT('$[', i, '].carriage_type'))
    AND tp.carriage_type = JSON_UNQUOTE(JSON_EXTRACT(p_passengers, CONCAT('$[', i, '].carriage_type')));
    
    -- Bắt đầu transaction
    START TRANSACTION;
    
    -- Tạo booking
    INSERT INTO bookings (user_id, booking_code, total_amount, payment_method, booking_status, payment_status)
    VALUES (p_user_id, v_booking_code, v_total_amount, p_payment_method, 'pending', 'pending');
    
    SET p_booking_id = LAST_INSERT_ID();
    SET p_booking_code = v_booking_code;
    
    -- Đếm số lượng hành khách
    SET v_passenger_count = JSON_LENGTH(p_passengers);
    
    -- Tạo vé cho từng hành khách
    WHILE i < v_passenger_count DO
        -- Lấy thông tin hành khách
        SET v_passenger_name = JSON_UNQUOTE(JSON_EXTRACT(p_passengers, CONCAT('$[', i, '].name')));
        SET v_passenger_id_card = JSON_UNQUOTE(JSON_EXTRACT(p_passengers, CONCAT('$[', i, '].id_card')));
        
        -- Tìm ghế còn trống
        SELECT s.seat_id INTO v_seat_id
        FROM seats s
        JOIN carriages c ON s.carriage_id = c.carriage_id
        JOIN trains tr ON c.train_id = tr.train_id
        JOIN trips t ON t.train_id = tr.train_id
        WHERE t.trip_id = p_trip_id
        AND c.carriage_type = JSON_UNQUOTE(JSON_EXTRACT(p_passengers, CONCAT('$[', i, '].carriage_type')))
        AND NOT EXISTS (
            SELECT 1 FROM tickets 
            WHERE trip_id = p_trip_id 
            AND seat_id = s.seat_id 
            AND ticket_status IN ('booked', 'checked_in')
        )
        LIMIT 1;
        
        -- Lấy giá vé
        SELECT tp.base_price INTO v_ticket_price
        FROM ticket_prices tp
        JOIN routes r ON tp.route_id = r.route_id
        JOIN trips t ON t.route_id = r.route_id
        WHERE t.trip_id = p_trip_id
        AND tp.carriage_type = JSON_UNQUOTE(JSON_EXTRACT(p_passengers, CONCAT('$[', i, '].carriage_type')));
        
        -- Tạo mã vé
        SET v_ticket_code = CONCAT('TK', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(FLOOR(RAND() * 9999), 4, '0'));
        
        -- Tạo vé
        INSERT INTO tickets (booking_id, trip_id, seat_id, origin_station_id, destination_station_id, 
                            passenger_name, passenger_id_card, ticket_price, ticket_code, ticket_status)
        VALUES (p_booking_id, p_trip_id, v_seat_id, p_origin_station_id, p_destination_station_id, 
                v_passenger_name, v_passenger_id_card, v_ticket_price, v_ticket_code, 'booked');
                
        SET i = i + 1;
    END WHILE;
    
    -- Lưu transaction
    COMMIT;
    
    -- Ghi log
    INSERT INTO system_logs (user_id, action, entity_type, entity_id, description)
    VALUES (p_user_id, 'CREATE', 'booking', p_booking_id, CONCAT('Created booking with code: ', v_booking_code));

END //
DELIMITER ;

-- Tạo Stored Procedure cập nhật trạng thái thanh toán
DELIMITER //
CREATE PROCEDURE update_payment_status(
    IN p_booking_id INT,
    IN p_transaction_id VARCHAR(100),
    IN p_status VARCHAR(20),
    IN p_details JSON
)
BEGIN
    DECLARE v_payment_id INT;
    DECLARE v_booking_status VARCHAR(20);
    
    -- Bắt đầu transaction
    START TRANSACTION;
    
    -- Cập nhật trạng thái thanh toán trong booking
    UPDATE bookings 
    SET payment_status = p_status,
        payment_date = IF(p_status = 'paid', NOW(), payment_date),
        booking_status = IF(p_status = 'paid', 'confirmed', IF(p_status = 'cancelled', 'cancelled', booking_status))
    WHERE booking_id = p_booking_id;
    
    -- Kiểm tra nếu đã có bản ghi payment
    SELECT payment_id INTO v_payment_id 
    FROM payments 
    WHERE booking_id = p_booking_id 
    LIMIT 1;
    
    IF v_payment_id IS NOT NULL THEN
        -- Cập nhật payment hiện có
        UPDATE payments
        SET payment_status = p_status,
            transaction_id = p_transaction_id,
            payment_details = p_details,
            updated_at = NOW()
        WHERE payment_id = v_payment_id;
    ELSE
        -- Tạo bản ghi payment mới
        INSERT INTO payments (booking_id, payment_amount, payment_method, transaction_id, payment_status, payment_details)
        SELECT total_amount, payment_method, p_transaction_id, p_status, p_details
        FROM bookings
        WHERE booking_id = p_booking_id;
    END IF;
    
    -- Cập nhật trạng thái vé nếu thanh toán thành công
    IF p_status = 'paid' THEN
        UPDATE tickets
        SET ticket_status = 'booked'
        WHERE booking_id = p_booking_id;
    ELSEIF p_status = 'cancelled' OR p_status = 'refunded' THEN
        UPDATE tickets
        SET ticket_status = 'cancelled'
        WHERE booking_id = p_booking_id;
    END IF;
    
    -- Lưu transaction
    COMMIT;
    
    -- Ghi log
    INSERT INTO system_logs (action, entity_type, entity_id, description)
    VALUES ('UPDATE', 'payment', p_booking_id, CONCAT('Updated payment status to: ', p_status));
END //
DELIMITER ;

-- Tạo Stored Procedure hủy vé
DELIMITER //
CREATE PROCEDURE cancel_ticket(
    IN p_ticket_id INT,
    IN p_user_id INT,
    IN p_reason TEXT,
    OUT p_refund_amount DECIMAL(10,2)
)
BEGIN
    DECLARE v_ticket_price DECIMAL(10,2);
    DECLARE v_departure_time DATETIME;
    DECLARE v_cancellation_fee DECIMAL(10,2);
    DECLARE v_booking_id INT;
    DECLARE v_hours_difference INT;
    
    -- Lấy thông tin vé
    SELECT t.ticket_price, tr.departure_time, t.booking_id 
    INTO v_ticket_price, v_departure_time, v_booking_id
    FROM tickets t
    JOIN trips tr ON t.trip_id = tr.trip_id
    WHERE t.ticket_id = p_ticket_id;
    
    -- Tính thời gian còn lại đến khi khởi hành
    SET v_hours_difference = TIMESTAMPDIFF(HOUR, NOW(), v_departure_time);
    
    -- Tính phí hủy vé dựa trên thời gian còn lại
    IF v_hours_difference > 72 THEN
        -- Hủy trước 3 ngày: phí 10%
        SET v_cancellation_fee = v_ticket_price * 0.1;
    ELSEIF v_hours_difference > 48 THEN
        -- Hủy trước 2 ngày: phí 20%
        SET v_cancellation_fee = v_ticket_price * 0.2;
    ELSEIF v_hours_difference > 24 THEN
        -- Hủy trước 1 ngày: phí 30%
        SET v_cancellation_fee = v_ticket_price * 0.3;
    ELSEIF v_hours_difference > 6 THEN
        -- Hủy trước 6 giờ: phí 50%
        SET v_cancellation_fee = v_ticket_price * 0.5;
    ELSE
        -- Hủy trong vòng 6 giờ: phí 80%
        SET v_cancellation_fee = v_ticket_price * 0.8;
    END IF;
    
    -- Tính số tiền hoàn trả
    SET p_refund_amount = v_ticket_price - v_cancellation_fee;
    
    -- Bắt đầu transaction
    START TRANSACTION;
    
    -- Cập nhật trạng thái vé
    UPDATE tickets 
    SET ticket_status = 'cancelled' 
    WHERE ticket_id = p_ticket_id;
    
    -- Tạo bản ghi hủy vé
    INSERT INTO cancellations (ticket_id, refund_amount, cancellation_fee, reason, status)
    VALUES (p_ticket_id, p_refund_amount, v_cancellation_fee, p_reason, 'pending');
    
    -- Kiểm tra nếu tất cả vé trong booking đã bị hủy
    IF NOT EXISTS (SELECT 1 FROM tickets WHERE booking_id = v_booking_id AND ticket_status != 'cancelled') THEN
        -- Cập nhật trạng thái booking
        UPDATE bookings 
        SET booking_status = 'cancelled' 
        WHERE booking_id = v_booking_id;
    END IF;
    
    -- Lưu transaction
    COMMIT;
    
    -- Ghi log
    INSERT INTO system_logs (user_id, action, entity_type, entity_id, description)
    VALUES (p_user_id, 'CANCEL', 'ticket', p_ticket_id, CONCAT('Cancelled ticket with refund amount: ', p_refund_amount));
END //
DELIMITER ;

-- Tạo Stored Procedure đổi vé
DELIMITER //
CREATE PROCEDURE change_ticket(
    IN p_ticket_id INT,
    IN p_new_trip_id INT,
    IN p_new_seat_id INT,
    IN p_user_id INT,
    IN p_reason TEXT,
    OUT p_change_fee DECIMAL(10,2)
)
BEGIN
    DECLARE v_old_trip_id INT;
    DECLARE v_old_seat_id INT;
    DECLARE v_old_ticket_price DECIMAL(10,2);
    DECLARE v_new_ticket_price DECIMAL(10,2);
    DECLARE v_departure_time DATETIME;
    DECLARE v_hours_difference INT;
    
    -- Lấy thông tin vé hiện tại
    SELECT t.trip_id, t.seat_id, t.ticket_price 
    INTO v_old_trip_id, v_old_seat_id, v_old_ticket_price
    FROM tickets t
    WHERE t.ticket_id = p_ticket_id;
    
    -- Lấy giá vé mới
    SELECT tp.base_price INTO v_new_ticket_price
    FROM ticket_prices tp
    JOIN trips t ON t.route_id = tp.route_id
    JOIN seats s ON s.seat_id = p_new_seat_id
    JOIN carriages c ON s.carriage_id = c.carriage_id
    WHERE t.trip_id = p_new_trip_id
    AND tp.carriage_type = c.carriage_type;
    
    -- Lấy thông tin thời gian khởi hành của chuyến cũ
    SELECT departure_time INTO v_departure_time
    FROM trips
    WHERE trip_id = v_old_trip_id;
    
    -- Tính thời gian còn lại đến khi khởi hành
    SET v_hours_difference = TIMESTAMPDIFF(HOUR, NOW(), v_departure_time);
    
    -- Tính phí đổi vé
    IF v_hours_difference > 48 THEN
        -- Đổi trước 2 ngày: phí 5% + chênh lệch giá (nếu có)
        SET p_change_fee = v_old_ticket_price * 0.05;
    ELSEIF v_hours_difference > 24 THEN
        -- Đổi trước 1 ngày: phí 10% + chênh lệch giá (nếu có)
        SET p_change_fee = v_old_ticket_price * 0.1;
    ELSE
        -- Đổi trong vòng 24 giờ: phí 20% + chênh lệch giá (nếu có)
        SET p_change_fee = v_old_ticket_price * 0.2;
    END IF;
    
    -- Thêm chênh lệch giá nếu vé mới đắt hơn
    IF v_new_ticket_price > v_old_ticket_price THEN
        SET p_change_fee = p_change_fee + (v_new_ticket_price - v_old_ticket_price);
    END IF;
    
    -- Bắt đầu transaction
    START TRANSACTION;
    
    -- Tạo bản ghi đổi vé
    INSERT INTO ticket_changes (
        ticket_id, old_trip_id, new_trip_id, old_seat_id, new_seat_id, 
        change_fee, reason, status
    ) VALUES (
        p_ticket_id, v_old_trip_id, p_new_trip_id, v_old_seat_id, p_new_seat_id, 
        p_change_fee, p_reason, 'pending'
    );
    
    -- Cập nhật thông tin vé
    UPDATE tickets 
    SET trip_id = p_new_trip_id,
        seat_id = p_new_seat_id,
        ticket_price = v_new_ticket_price,
        updated_at = NOW()
    WHERE ticket_id = p_ticket_id;
    
    -- Lưu transaction
    COMMIT;
    
    -- Ghi log
    INSERT INTO system_logs (user_id, action, entity_type, entity_id, description)
    VALUES (p_user_id, 'CHANGE', 'ticket', p_ticket_id, CONCAT('Changed ticket with fee: ', p_change_fee));
END //
DELIMITER ;

-- Tạo Stored Procedure check-in vé
DELIMITER //
CREATE PROCEDURE check_in_ticket(
    IN p_ticket_id INT,
    IN p_ticket_code VARCHAR(20),
    OUT p_status VARCHAR(50)
)
BEGIN
    DECLARE v_ticket_status VARCHAR(20);
    DECLARE v_departure_time DATETIME;
    DECLARE v_hours_difference INT;
    
    -- Lấy thông tin vé
    SELECT t.ticket_status, tr.departure_time 
    INTO v_ticket_status, v_departure_time
    FROM tickets t
    JOIN trips tr ON t.trip_id = tr.trip_id
    WHERE t.ticket_id = p_ticket_id AND t.ticket_code = p_ticket_code;
    
    -- Kiểm tra trạng thái vé
    IF v_ticket_status IS NULL THEN
        SET p_status = 'INVALID_TICKET';
    ELSEIF v_ticket_status != 'booked' THEN
        SET p_status = CONCAT('TICKET_', v_ticket_status);
    ELSE
        -- Tính thời gian còn lại đến khi khởi hành
        SET v_hours_difference = TIMESTAMPDIFF(HOUR, NOW(), v_departure_time);
        
        -- Kiểm tra thời gian check-in
        IF v_hours_difference < 0 THEN
            SET p_status = 'TRAIN_DEPARTED';
        ELSEIF v_hours_difference > 24 THEN
            SET p_status = 'TOO_EARLY';
        ELSE
            -- Cập nhật trạng thái vé
            UPDATE tickets 
            SET ticket_status = 'checked_in',
                boarding_time = NOW()
            WHERE ticket_id = p_ticket_id;
            
            SET p_status = 'CHECKED_IN';
            
            -- Ghi log
            INSERT INTO system_logs (action, entity_type, entity_id, description)
            VALUES ('CHECK_IN', 'ticket', p_ticket_id, CONCAT('Ticket checked in: ', p_ticket_code));
        END IF;
    END IF;
END //
DELIMITER ;

-- Tạo Stored Procedure thống kê doanh thu theo khoảng thời gian
DELIMITER //
CREATE PROCEDURE get_revenue_statistics(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        DATE(b.booking_date) AS booking_date,
        COUNT(b.booking_id) AS total_bookings,
        SUM(b.total_amount) AS total_revenue,
        SUM(CASE WHEN b.payment_status = 'paid' THEN b.total_amount ELSE 0 END) AS paid_revenue,
        COUNT(DISTINCT b.user_id) AS unique_customers,
        COUNT(t.ticket_id) AS total_tickets,
        SUM(CASE WHEN t.ticket_status = 'cancelled' THEN 1 ELSE 0 END) AS cancelled_tickets,
        SUM(CASE WHEN t.ticket_status = 'cancelled' THEN c.refund_amount ELSE 0 END) AS refund_amount
    FROM bookings b
    LEFT JOIN tickets t ON b.booking_id = t.booking_id
    LEFT JOIN cancellations c ON t.ticket_id = c.ticket_id
    WHERE DATE(b.booking_date) BETWEEN p_start_date AND p_end_date
    GROUP BY DATE(b.booking_date)
    ORDER BY booking_date;
END //
DELIMITER ;

-- Tạo Stored Procedure lấy danh sách chuyến tàu phổ biến nhất
DELIMITER //
CREATE PROCEDURE get_popular_trips(
    IN p_limit INT
)
BEGIN
    SELECT 
        t.trip_id,
        t.trip_code,
        r.route_name,
        os.station_name AS origin_station,
        ds.station_name AS destination_station,
        COUNT(tk.ticket_id) AS total_tickets,
        AVG(f.rating) AS average_rating
    FROM trips t
    JOIN routes r ON t.route_id = r.route_id
    JOIN stations os ON r.origin_station_id = os.station_id
    JOIN stations ds ON r.destination_station_id = ds.station_id
    JOIN tickets tk ON t.trip_id = tk.trip_id
    LEFT JOIN feedbacks f ON t.trip_id = f.trip_id
    WHERE tk.ticket_status IN ('booked', 'checked_in', 'used')
    GROUP BY t.trip_id
    ORDER BY total_tickets DESC, average_rating DESC
    LIMIT p_limit;
END //
DELIMITER ;

-- Tạo Trigger tự động xóa các đặt vé tạm khi quá hạn thanh toán
DELIMITER //
CREATE TRIGGER cleanup_pending_bookings
BEFORE INSERT ON system_logs
FOR EACH ROW
BEGIN
    -- Xóa các booking chưa thanh toán sau 30 phút
    DELETE FROM bookings 
    WHERE payment_status = 'pending' 
    AND TIMESTAMPDIFF(MINUTE, created_at, NOW()) > 30;
END //
DELIMITER ;

-- Tạo Trigger cập nhật số lượng sử dụng khi áp dụng khuyến mãi
DELIMITER //
CREATE TRIGGER update_promotion_usage
AFTER INSERT ON booking_promotions
FOR EACH ROW
BEGIN
    UPDATE promotions
    SET usage_count = usage_count + 1,
        status = IF(usage_limit IS NOT NULL AND usage_count + 1 >= usage_limit, 'expired', status)
    WHERE promotion_id = NEW.promotion_id;
END //
DELIMITER ;

-- Tạo Trigger tạo thông báo khi có vé bị hủy
DELIMITER //
CREATE TRIGGER create_cancellation_notification
AFTER INSERT ON cancellations
FOR EACH ROW
BEGIN
    DECLARE v_user_id INT;
    DECLARE v_ticket_code VARCHAR(20);
    
    -- Lấy thông tin user và mã vé
    SELECT b.user_id, t.ticket_code INTO v_user_id, v_ticket_code
    FROM tickets t
    JOIN bookings b ON t.booking_id = b.booking_id
    WHERE t.ticket_id = NEW.ticket_id;
    
    -- Tạo thông báo
    INSERT INTO notifications (user_id, title, message, notification_type, related_id)
    VALUES (
        v_user_id, 
        'Vé của bạn đã bị hủy', 
        CONCAT('Vé có mã ', v_ticket_code, ' đã bị hủy. Số tiền hoàn trả: ', NEW.refund_amount),
        'booking',
        NEW.ticket_id
    );
END //
DELIMITER ;

-- Tạo Trigger tạo thông báo khi có cập nhật chuyến tàu
DELIMITER //
CREATE TRIGGER create_trip_update_notification
AFTER UPDATE ON trips
FOR EACH ROW
BEGIN
    IF NEW.status != OLD.status OR NEW.delay_minutes != OLD.delay_minutes THEN
        -- Tìm tất cả người dùng có vé cho chuyến tàu này
        INSERT INTO notifications (user_id, title, message, notification_type, related_id)
        SELECT DISTINCT b.user_id,
            CASE 
                WHEN NEW.status = 'delayed' THEN CONCAT('Chuyến tàu ', NEW.trip_code, ' bị trì hoãn')
                WHEN NEW.status = 'cancelled' THEN CONCAT('Chuyến tàu ', NEW.trip_code, ' đã bị hủy')
                ELSE CONCAT('Cập nhật cho chuyến tàu ', NEW.trip_code)
            END,
            CASE 
                WHEN NEW.status = 'delayed' THEN CONCAT('Chuyến tàu của bạn đã bị trì hoãn ', NEW.delay_minutes, ' phút')
                WHEN NEW.status = 'cancelled' THEN 'Chuyến tàu của bạn đã bị hủy. Vui lòng liên hệ bộ phận hỗ trợ khách hàng để được hỗ trợ'
                ELSE CONCAT('Thông tin chuyến tàu của bạn đã được cập nhật')
            END,
            'trip_update',
            NEW.trip_id
        FROM tickets t
        JOIN bookings b ON t.booking_id = b.booking_id
        WHERE t.trip_id = NEW.trip_id AND t.ticket_status IN ('booked', 'checked_in');
    END IF;
END //
DELIMITER ;

-- Chèn dữ liệu mẫu cho hệ thống

-- Bổ sung dữ liệu mẫu cho bảng users
INSERT INTO users (username, password, full_name, email, phone, address, id_card, date_of_birth, role)
VALUES
    ('user4', '$2a$12$5UMZ.fNH9UvD7REGlB7fmeWqQVAZVrKu2HLNJmYvLVZYADUUGTKMm', 'Pham Thi D', 'phamthid@gmail.com', '0987654324', 'Hue, Vietnam', '001234567895', '1990-09-10', 'customer'),
    ('user5', '$2a$12$5UMZ.fNH9UvD7REGlB7fmeWqQVAZVrKu2HLNJmYvLVZYADUUGTKMm', 'Hoang Van E', 'hoangvane@gmail.com', '0987654325', 'Nha Trang, Vietnam', '001234567896', '1985-12-20', 'customer'),
    ('staff2', '$2a$12$5UMZ.fNH9UvD7REGlB7fmeWqQVAZVrKu2HLNJmYvLVZYADUUGTKMm', 'Nguyen Thi F', 'staff2@railway.com', '0912345680', 'Da Nang, Vietnam', '001234567897', '1990-03-22', 'staff');

-- Bổ sung dữ liệu mẫu cho bảng stations
INSERT INTO stations (station_name, location, address, city, province, phone, status)
VALUES
    ('Ga Đồng Hới', 'Dong Hoi', '2 Lý Thường Kiệt, Đồng Phú', 'Đồng Hới', 'Quảng Bình', '02323821114', 'active'),
    ('Ga Quy Nhơn', 'Quy Nhon', 'Đường Lê Hồng Phong, Phường Lê Hồng Phong', 'Quy Nhơn', 'Bình Định', '02563821115', 'active');

-- Bổ sung dữ liệu mẫu cho bảng routes
INSERT INTO routes (route_name, origin_station_id, destination_station_id, distance, description, status)
VALUES
    ('Hà Nội - Vinh', 1, 6, 320, 'Tuyến đường sắt Hà Nội - Vinh', 'active'),
    ('Vinh - Hà Nội', 6, 1, 320, 'Tuyến đường sắt Vinh - Hà Nội', 'active');

-- Bổ sung dữ liệu mẫu cho bảng route_stations
INSERT INTO route_stations (route_id, station_id, stop_order, arrival_offset, departure_offset, distance_from_origin)
VALUES
-- Hà Nội - Vinh
(9, 1, 1, 0, 0, 0), -- Ga Hà Nội (xuất phát)
(9, 7, 2, 120, 130, 155), -- Ga Thanh Hóa
(9, 6, 3, 240, 240, 320), -- Ga Vinh (đích)

-- Vinh - Hà Nội
(10, 6, 1, 0, 0, 0), -- Ga Vinh (xuất phát)
(10, 7, 2, 110, 120, 165), -- Ga Thanh Hóa
(10, 1, 3, 240, 240, 320); -- Ga Hà Nội (đích)

-- Bổ sung dữ liệu mẫu cho bảng trains
INSERT INTO trains (train_number, train_name, train_type, capacity, status)
VALUES
    ('SE5', 'Tàu Thống Nhất 5', 'express', 450, 'active'),
    ('TN2', 'Tàu Thống Nhất Bắc Nam 2', 'sleeper', 400, 'active'),
    ('HP2', 'Hà Nội - Hải Phòng 2', 'fast', 300, 'active');

-- Bổ sung dữ liệu mẫu cho bảng carriages
INSERT INTO carriages (train_id, carriage_number, carriage_type, capacity, status)
VALUES
-- Tàu SE3 (bổ sung thêm toa)
(3, 'C3', 'soft_sleeper', 40, 'active'),
(3, 'C4', 'vip', 20, 'active'),
-- Tàu SE5
(7, 'C1', 'soft_seat', 60, 'active'),
(7, 'C2', 'hard_seat', 80, 'active'),
(7, 'C3', 'soft_sleeper', 40, 'active'),
-- Tàu TN2
(8, 'C1', 'hard_sleeper', 40, 'active'),
(8, 'C2', 'soft_sleeper', 40, 'active'),
-- Tàu HP2
(9, 'C1', 'soft_seat', 60, 'active'),
(9, 'C2', 'hard_seat', 80, 'active');

-- Bổ sung dữ liệu mẫu cho bảng seats
INSERT INTO seats (carriage_id, seat_number, seat_type, status)
VALUES
-- Toa C3 của tàu SE3 (soft_sleeper)
(12, 'B1', 'lower_berth', 'active'),
(12, 'B2', 'upper_berth', 'active'),
-- Toa C4 của tàu SE3 (vip)
(13, 'V1', 'window', 'active'),
(13, 'V2', 'aisle', 'active'),
-- Toa C1 của tàu SE5 (soft_seat)
(14, 'A1', 'window', 'active'),
(14, 'A2', 'aisle', 'active'),
-- Toa C1 của tàu TN2 (hard_sleeper)
(17, 'B1', 'lower_berth', 'active'),
(17, 'B2', 'middle_berth', 'active'),
-- Toa C1 của tàu HP2 (soft_seat)
(19, 'A1', 'window', 'active'),
(19, 'A2', 'aisle', 'active');

-- Bổ sung dữ liệu mẫu cho bảng trips
INSERT INTO trips (route_id, train_id, trip_code, departure_time, arrival_time, status, delay_minutes)
VALUES
-- Chuyến Hà Nội - Sài Gòn
(1, 7, 'SE5-20250510', '2025-05-10 12:00:00', '2025-05-11 16:00:00', 'scheduled', 0),
-- Chuyến Sài Gòn - Hà Nội
(2, 8, 'TN2-20250510', '2025-05-10 11:00:00', '2025-05-11 15:00:00', 'scheduled', 0),
-- Chuyến Hà Nội - Vinh
(9, 7, 'SE5-20250511', '2025-05-11 08:00:00', '2025-05-11 13:00:00', 'scheduled', 0),
-- Chuyến Hà Nội - Hải Phòng
(7, 9, 'HP2-20250510', '2025-05-10 09:00:00', '2025-05-10 11:00:00', 'scheduled', 0);

-- Bổ sung dữ liệu mẫu cho bảng trip_schedules
INSERT INTO trip_schedules (trip_id, station_id, scheduled_arrival, scheduled_departure, status)
    BOSUNG DU LIEU MAU CHO CAC BANG CON LAI
VALUES
-- Lịch trình cho chuyến SE5-20250510 (Hà Nội - Sài Gòn)
    (6, 1, '2025-05-10 12:00:00', '2025-05-10 12:00:00', 'scheduled'),
    (6, 7, '2025-05-10 14:00:00', '2025-05-10 14:10:00', 'scheduled'),
    (6, 6, '2025-05-10 16:00:00', '2025-05-10 16:10:00', 'scheduled'),
    (6, 4, '2025-05-10 20:00:00', '2025-05-10 20:10:00', 'scheduled'),
    (6, 3, '2025-05-10 22:00:00', '2025-05-10 22:10:00', 'scheduled'),
    (6, 5, '2025-05-11 03:00:00', '2025-05-11 03:10:00', 'scheduled'),
    (6, 2, '2025-05-11 16:00:00', '2025-05-11 16:00:00', 'scheduled'),
-- Lịch trình cho chuyến HP2-20250510 (Hà Nội - Hải Phòng)
    (9, 1, '2025-05-10 09:00:00', '2025-05-10 09:00:00', 'scheduled'),
    (9, 8, '2025-05-10 11:00:00', '2025-05-10 11:00:00', 'scheduled');

-- Bổ sung dữ liệu mẫu cho bảng ticket_prices
INSERT INTO ticket_prices (route_id, carriage_type, base_price, weekend_surcharge, holiday_surcharge, peak_hour_surcharge, discount_rate, start_date, end_date)
VALUES
-- Tuyến Hà Nội - Vinh
(9, 'hard_seat', 200000.00, 20000.00, 30000.00, 15000.00, 0.00, '2025-01-01', '2025-12-31'),
(9, 'soft_seat', 300000.00, 30000.00, 50000.00, 20000.00, 0.00, '2025-01-01', '2025-12-31'),
(9, 'soft_sleeper', 450000.00, 40000.00, 60000.00, 30000.00, 0.00, '2025-01-01', '2025-12-31'),
-- Tuyến Vinh - Hà Nội
(10, 'hard_seat', 200000.00, 20000.00, 30000.00, 15000.00, 0.00, '2025-01-01', '2025-12-31'),
(10, 'soft_seat', 300000.00, 30000.00, 50000.00, 20000.00, 0.00, '2025-01-01', '2025-12-31');

-- Bổ sung dữ liệu mẫu cho bảng bookings
INSERT INTO bookings (user_id, booking_code, total_amount, payment_status, booking_status, payment_method)
VALUES
    (5, 'BK202505090003', 600000.00, 'paid', 'confirmed', 'bank_transfer'),
    (4, 'BK202505090004', 450000.00, 'pending', 'pending', 'e_wallet'),
    (3, 'BK202505090005', 300000.00, 'paid', 'confirmed', 'credit_card');

-- Bổ sung dữ liệu mẫu cho bảng tickets
INSERT INTO tickets (booking_id, trip_id, seat_id, origin_station_id, destination_station_id, passenger_name, passenger_id_card, ticket_price, ticket_code, ticket_status)
VALUES
    (3, 6, 14, 1, 2, 'Hoang Van E', '001234567896', 600000.00, 'TK202505090004', 'booked'),
    (4, 8, 17, 1, 6, 'Tran Thi B', '001234567893', 450000.00, 'TK202505090005', 'pending'),
    (5, 9, 19, 1, 8, 'Nguyen Van A', '001234567892', 300000.00, 'TK202505090006', 'booked');

-- Bổ sung dữ liệu mẫu cho bảng payments
INSERT INTO payments (booking_id, payment_amount, payment_method, transaction_id, payment_status, payment_details)
VALUES
    (3, 600000.00, 'bank_transfer', 'TXN202505090001', 'completed', '{"bank_name": "Vietcombank", "account_number": "1234567890"}'),
    (5, 300000.00, 'credit_card', 'TXN202505090002', 'completed', '{"card_type": "Visa", "last_four": "1234"}');

-- Bổ sung dữ liệu mẫu cho bảng cancellations
INSERT INTO cancellations (ticket_id, refund_amount, cancellation_fee, reason, status, processed_by)
VALUES
    (5, 360000.00, 90000.00, 'Change of plans', 'pending', NULL);

-- Bổ sung dữ liệu mẫu cho bảng ticket_changes
INSERT INTO ticket_changes (ticket_id, old_trip_id, new_trip_id, old_seat_id, new_seat_id, change_fee, reason, status, processed_by)
VALUES
    (4, 6, 8, 14, 17, 60000.00, 'Change to earlier trip', 'pending', NULL);

-- Bổ sung dữ liệu mẫu cho bảng system_logs
INSERT INTO system_logs (user_id, action, entity_type, entity_id, description, ip_address, user_agent)
VALUES
    (3, 'CREATE', 'booking', 3, 'Created booking with code: BK202505090003', '192.168.1.1', 'Mozilla/5.0'),
    (5, 'CREATE', 'booking', 5, 'Created booking with code: BK202505090005', '192.168.1.2', 'Chrome/120.0');

-- Bổ sung dữ liệu mẫu cho bảng promotions
INSERT INTO promotions (promotion_code, promotion_name, description, discount_type, discount_value, minimum_purchase, maximum_discount, start_date, end_date, usage_limit, usage_count, status)
VALUES
    ('SUMMER25', 'Summer Discount', '25% off for summer trips', 'percentage', 25.00, 500000.00, 200000.00, '2025-06-01 00:00:00', '2025-08-31 23:59:59', 100, 0, 'active'),
    ('NEWUSER10', 'New User Discount', '10% off for new users', 'percentage', 10.00, 200000.00, 100000.00, '2025-01-01 00:00:00', '2025-12-31 23:59:59', 200, 0, 'active');

-- Bổ sung dữ liệu mẫu cho bảng booking_promotions
INSERT INTO booking_promotions (booking_id, promotion_id, discount_amount)
VALUES
    (3, 1, 150000.00),
    (5, 2, 30000.00);

-- Bổ sung dữ liệu mẫu cho bảng feedbacks
INSERT INTO feedbacks (user_id, trip_id, rating, comment, status)
VALUES
    (3, 6, 4, 'Comfortable trip, but slight delay', 'pending'),
    (5, 9, 5, 'Excellent service, on time', 'approved');

-- Bổ sung dữ liệu mẫu cho bảng notifications
INSERT INTO notifications (user_id, title, message, notification_type, related_id, is_read)
VALUES
    (3, 'Booking Confirmed', 'Your booking BK202505090003 has been confirmed', 'booking', 3, FALSE),
    (5, 'Ticket Cancellation Request', 'Your cancellation request for ticket TK202505090006 is being processed', 'booking', 5, FALSE),
    (4, 'Promotion Available', 'Use code SUMMER25 for 25% off your next trip', 'promotion', 1, FALSE);

-- Bổ sung dữ liệu mẫu cho bảng settings
INSERT INTO settings (setting_key, setting_value, setting_group, description)
VALUES
    ('max_tickets_per_booking', '6', 'booking', 'Maximum number of tickets allowed per booking'),
    ('payment_timeout_minutes', '30', 'payment', 'Time limit for completing payment in minutes'),
    ('cancellation_fee_percentage_72h', '10', 'cancellation', 'Cancellation fee percentage for cancellations more than 72 hours before departure');