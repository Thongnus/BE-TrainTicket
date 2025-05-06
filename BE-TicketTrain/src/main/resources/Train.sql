
-- Stations -> Routes: Một ga có thể là ga đi hoặc ga đến của nhiều tuyến đường (1:N).
-- Routes -> Schedules: Một tuyến đường có nhiều lịch trình (1:N).
-- Trains -> Coaches: Một đoàn tàu có nhiều toa (1:N).
-- Coaches -> Seats: Một toa có nhiều ghế (1:N).
-- SeatTypes -> Seats: Một loại ghế áp dụng cho nhiều ghế (1:N).
-- Schedules -> Bookings: Một lịch trình có nhiều đặt vé (1:N).
-- Schedules -> PriceHistory: Một lịch trình có nhiều lịch sử giá (1:N).
-- Passengers -> Bookings: Một hành khách có nhiều đặt vé (1:N).
-- Discounts -> Bookings: Một mã giảm giá có thể áp dụng cho nhiều đặt vé (1:N).
-- Bookings -> Tickets: Một đặt vé tạo ra một vé điện tử (1:1).
-- Bookings -> Payments: Một đặt vé có một thanh toán (1:1).
-- Bookings -> Feedbacks: Một đặt vé có thể có một phản hồi (1:1).
-- Passengers -> Feedbacks: Một hành khách có thể gửi nhiều phản hồi (1:N).
-- Stations -> StationStaff: Một ga có nhiều nhân viên (1:N).
-- RevenueReports: Không có mối quan hệ trực tiếp, dùng để lưu báo cáo tổng hợp.

-- Tạo cơ sở dữ liệu
CREATE DATABASE train_booking;
USE train_booking;

-- Bảng Stations: Lưu thông tin các ga tàu
CREATE TABLE Stations (
                          station_id INT PRIMARY KEY AUTO_INCREMENT,
                          station_name VARCHAR(100) NOT NULL,
                          city VARCHAR(100),
                          address VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Routes: Lưu thông tin tuyến đường
CREATE TABLE Routes (
                        route_id INT PRIMARY KEY AUTO_INCREMENT,
                        departure_station_id INT,
                        arrival_station_id INT,
                        distance INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (departure_station_id) REFERENCES Stations(station_id),
                        FOREIGN KEY (arrival_station_id) REFERENCES Stations(station_id),
                        UNIQUE (departure_station_id, arrival_station_id)
);

-- Bảng Trains: Lưu thông tin các đoàn tàu
CREATE TABLE Trains (
                        train_id INT PRIMARY KEY AUTO_INCREMENT,
                        train_number VARCHAR(20) UNIQUE NOT NULL,
                        train_name VARCHAR(100),
                        train_type ENUM('express', 'local', 'high_speed') NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Coaches: Lưu thông tin toa tàu
CREATE TABLE Coaches (
                         coach_id INT PRIMARY KEY AUTO_INCREMENT,
                         train_id INT,
                         coach_number VARCHAR(10) NOT NULL,
                         total_seats INT NOT NULL,
                         coach_type ENUM('sleeper', 'soft_seat', 'hard_seat', 'vip') NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (train_id) REFERENCES Trains(train_id),
                         UNIQUE (train_id, coach_number)
);

-- Bảng SeatTypes: Lưu thông tin loại ghế
CREATE TABLE SeatTypes (
                           seat_type_id INT PRIMARY KEY AUTO_INCREMENT,
                           seat_type_name VARCHAR(50) NOT NULL,
                           description VARCHAR(255),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Seats: Lưu thông tin ghế trên mỗi toa
CREATE TABLE Seats (
                       seat_id INT PRIMARY KEY AUTO_INCREMENT,
                       coach_id INT,
                       seat_number VARCHAR(10) NOT NULL,
                       seat_type_id INT,
                       is_available BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (coach_id) REFERENCES Coaches(coach_id),
                       FOREIGN KEY (seat_type_id) REFERENCES SeatTypes(seat_type_id),
                       UNIQUE (coach_id, seat_number)
);

-- Bảng Schedules: Lưu thông tin lịch trình tàu
CREATE TABLE Schedules (
                           schedule_id INT PRIMARY KEY AUTO_INCREMENT,
                           train_id INT,
                           route_id INT,
                           departure_time DATETIME NOT NULL,
                           arrival_time DATETIME NOT NULL,
                           duration INT NOT NULL,
                           price DECIMAL(10, 2) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (train_id) REFERENCES Trains(train_id),
                           FOREIGN KEY (route_id) REFERENCES Routes(route_id)
);

-- Bảng PriceHistory: Lưu lịch sử giá vé
CREATE TABLE PriceHistory (
                              price_history_id INT PRIMARY KEY AUTO_INCREMENT,
                              schedule_id INT,
                              price DECIMAL(10, 2) NOT NULL,
                              effective_date DATETIME NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (schedule_id) REFERENCES Schedules(schedule_id)
);

-- Bảng Passengers: Lưu thông tin hành khách
CREATE TABLE Passengers (
                            passenger_id INT PRIMARY KEY AUTO_INCREMENT,
                            full_name VARCHAR(100) NOT NULL,
                            email VARCHAR(100),
                            phone VARCHAR(20),
                            identity_number VARCHAR(20) UNIQUE,
                            date_of_birth DATE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Discounts: Quản lý mã giảm giá hoặc chương trình khuyến mãi
CREATE TABLE Discounts (
                           discount_id INT PRIMARY KEY AUTO_INCREMENT,
                           discount_code VARCHAR(20) UNIQUE NOT NULL,
                           description VARCHAR(255),
                           discount_type ENUM('percentage', 'fixed') NOT NULL,
                           discount_value DECIMAL(10, 2) NOT NULL,
                           start_date DATETIME NOT NULL,
                           end_date DATETIME NOT NULL,
                           max_usage INT, -- Số lần sử dụng tối đa
                           used_count INT DEFAULT 0,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Bookings: Lưu thông tin đặt vé
CREATE TABLE Bookings (
                          booking_id INT PRIMARY KEY AUTO_INCREMENT,
                          passenger_id INT,
                          schedule_id INT,
                          seat_id INT,
                          discount_id INT NULL,
                          booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          total_price DECIMAL(10, 2) NOT NULL,
                          status ENUM('pending', 'confirmed', 'cancelled') DEFAULT 'pending',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (passenger_id) REFERENCES Passengers(passenger_id),
                          FOREIGN KEY (schedule_id) REFERENCES Schedules(schedule_id),
                          FOREIGN KEY (seat_id) REFERENCES Seats(seat_id),
                          FOREIGN KEY (discount_id) REFERENCES Discounts(discount_id),
                          UNIQUE (schedule_id, seat_id)
);

-- Bảng Tickets: Lưu thông tin vé điện tử
CREATE TABLE Tickets (
                         ticket_id INT PRIMARY KEY AUTO_INCREMENT,
                         booking_id INT,
                         ticket_code VARCHAR(50) UNIQUE NOT NULL,
                         qr_code VARCHAR(255), -- Lưu đường dẫn hoặc mã QR
                         issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         status ENUM('valid', 'used', 'expired') DEFAULT 'valid',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id)
);

-- Bảng Payments: Lưu thông tin thanh toán
CREATE TABLE Payments (
                          payment_id INT PRIMARY KEY AUTO_INCREMENT,
                          booking_id INT,
                          payment_method ENUM('credit_card', 'bank_transfer', 'cash') NOT NULL,
                          payment_amount DECIMAL(10, 2) NOT NULL,
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          payment_status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id)
);

-- Bảng Feedbacks: Lưu phản hồi của hành khách
CREATE TABLE Feedbacks (
                           feedback_id INT PRIMARY KEY AUTO_INCREMENT,
                           booking_id INT,
                           passenger_id INT,
                           rating INT CHECK (rating >= 1 AND rating <= 5),
                           comment TEXT,
                           feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id),
                           FOREIGN KEY (passenger_id) REFERENCES Passengers(passenger_id)
);

-- Bảng StationStaff: Quản lý nhân viên ga
CREATE TABLE StationStaff (
                              staff_id INT PRIMARY KEY AUTO_INCREMENT,
                              station_id INT,
                              full_name VARCHAR(100) NOT NULL,
                              email VARCHAR(100) UNIQUE,
                              phone VARCHAR(20),
                              position ENUM('manager', 'ticket_staff', 'support') NOT NULL,
                              hire_date DATE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (station_id) REFERENCES Stations(station_id)
);

-- Bảng RevenueReports: Thống kê doanh thu
CREATE TABLE RevenueReports (
                                report_id INT PRIMARY KEY AUTO_INCREMENT,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                total_revenue DECIMAL(12, 2) NOT NULL,
                                total_bookings INT NOT NULL,
                                total_tickets_sold INT NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Users: Lưu thông tin người dùng
CREATE TABLE Users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100),
                       role ENUM('admin', 'staff', 'customer') DEFAULT 'customer',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chỉ số để tối ưu truy vấn
CREATE INDEX idx_schedule_train ON Schedules(train_id);
CREATE INDEX idx_schedule_route ON Schedules(route_id);
CREATE INDEX idx_seat_coach ON Seats(coach_id);
CREATE INDEX idx_booking_passenger ON Bookings(passenger_id);
CREATE INDEX idx_payment_booking ON Payments(booking_id);
CREATE INDEX idx_coach_train ON Coaches(train_id);
CREATE INDEX idx_ticket_booking ON Tickets(booking_id);
CREATE INDEX idx_feedback_booking ON Feedbacks(booking_id);
CREATE INDEX idx_price_history_schedule ON PriceHistory(schedule_id);
CREATE INDEX idx_staff_station ON StationStaff(station_id);



--SELECT  ghế trống
c.coach_number,
  st.seat_type_name,
  s.seat_number
FROM Seats s
JOIN Coaches c ON s.coach_id = c.id
JOIN SeatTypes st ON s.seat_type_id = st.id
WHERE s.id NOT IN (
  SELECT b.seat_id
  FROM Bookings b
  WHERE b.status = 'confirmed'  -- chỉ loại bỏ các ghế đã đặt thực sự
)
AND c.train_id = 1;
