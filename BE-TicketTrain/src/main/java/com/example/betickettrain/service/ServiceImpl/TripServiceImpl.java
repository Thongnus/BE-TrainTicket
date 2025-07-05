package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.*;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.exceptions.BusinessException;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.TripMapper;
import com.example.betickettrain.repository.*;
import com.example.betickettrain.service.*;
import com.example.betickettrain.util.Constants;
import com.example.betickettrain.util.utils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService  {

    private final TripRepository tripRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final CustomTripRepository customTripRepository;
    private final GenericCacheService cacheService;
    private final TripMapper tripMapper;
    private final TripScheduleRepository tripScheduleRepository;
    private final RouteStationRepository routeStationRepository;
    private final SystemLogRepository systemLogRepository;
    private static final String ALL_TRIPS_KEY = "all";
    private final HttpServletRequest request;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRequestRepository refundRequestRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final BookingPromotionRepository bookingPromotionRepository;

    @Override
    @LogAction(action = Constants.Action.CREATE,entity = "Trip", description = " Create a trip")
    @Transactional
    public TripDto createTrip(TripDto dto) {
        Train train = trainRepository.findById(dto.getTrain().getTrainId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRAIN_NOT_FOUND.message));
        Route route = routeRepository.findById(dto.getRoute().getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.message));

        if (tripRepository.existsByTripCode(dto.getTripCode())) {
            throw new RuntimeException("Mã chuyến đã tồn tại");
        }

        Trip trip = tripMapper.toEntity(dto);
        trip.setTrain(train);
        trip.setRoute(route);
        trip.setStatus(dto.getStatus() == null ? Trip.Status.scheduled : dto.getStatus());
        trip.setDelayMinutes(dto.getDelayMinutes() == null ? 0 : dto.getDelayMinutes());

        Trip saved = tripRepository.save(trip);
        generateTripSchedulesFromRoute(saved);
        cacheService.clearCache(Constants.Cache.CACHE_TRIP);
        return tripMapper.toDto(saved);
    }

    @Override
    @LogAction(action = Constants.Action.UPDATE,entity = "NewFeed", description = " Update a trip")
    public TripDto updateTrip(Integer id, TripDto dto) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id));

        if (!trip.getTripCode().equals(dto.getTripCode()) &&
                tripRepository.existsByTripCode(dto.getTripCode())) {
            throw new RuntimeException("Mã chuyến đã tồn tại");
        }

        Train train = trainRepository.findById(dto.getTrain().getTrainId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tàu"+id));
        Route route = routeRepository.findById(dto.getRoute().getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tuyến đường"+id));

        tripMapper.partialUpdate(dto, trip);
        trip.setTrain(train);
        trip.setRoute(route);

        Trip updated = tripRepository.save(trip);
        cacheService.remove(Constants.Cache.CACHE_TRIP, id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        return tripMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TripDto getTrip(Integer id) {
        TripDto cached = cacheService.get(Constants.Cache.CACHE_TRIP, id, TripDto.class);
        if (cached != null) return cached;
        log.info(" ️️Lấy thông tin chuyến tàu từ DB");
        TripDto dto = tripRepository.findById(id)
                .map(tripMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id));

        cacheService.put(Constants.Cache.CACHE_TRIP, id, dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDto> getAllTrips() {
        List<TripDto> cached = cacheService.get(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        if (cached != null) return cached;
        log.info(" ️️Lấy thông tin chuyến tàu từ DB");
        List<TripDto> dtos = tripRepository.findAll().stream()
                .map(tripMapper::toDto)
                .toList();

        cacheService.put(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY, dtos);
        return dtos;
    }

    @Override
    @LogAction(action = Constants.Action.UPDATE,entity = "Trip", description = " Update status of trip")
    public TripDto updateTripStatus(Integer id, Trip.Status status) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id));

        trip.setStatus(status);
        Trip updated = tripRepository.save(trip);
        cacheService.remove(Constants.Cache.CACHE_TRIP, id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        return tripMapper.toDto(updated);
    }

    @Override
    @LogAction(action = Constants.Action.DELETE,entity = "Trip", description = " Delete a trip")
    public void deleteTrip(Integer id) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy chuyến tàu"+id);
        }

        tripRepository.deleteById(id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, id);
        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
    }

    @Override
    public List<TripSearchResult> searchTrips(Integer originStationId, Integer destinationStationId, LocalDate departureDate, Integer passengers) {
        return customTripRepository.searchTrips(originStationId, destinationStationId, departureDate, passengers);
    }
    @Override
    public Map<String, List<TripSearchResult>> searchRoundTrip(
            Integer departureId, Integer destinationId,
            LocalDate departureDate, LocalDate returnDate,
            Integer passengers) {

        List<TripSearchResult> departureTrips = customTripRepository.searchTrips(
                departureId, destinationId, departureDate, passengers);

        List<TripSearchResult> returnTrips = customTripRepository.searchTrips(
                destinationId, departureId, returnDate, passengers);

        Map<String, List<TripSearchResult>> result = new HashMap<>();
        result.put("departureTrips", departureTrips);
        result.put("returnTrips", returnTrips);

        return result;
    }

    @Override
    public List<TrainRouteDto> findPopularRoutes(int limit) {

        List<TrainRouteProjection> projections = tripRepository.findPopularRoutes(limit);
        return projections.stream()
                .map(TrainRouteDto::new) // Ánh xạ từ TrainRouteProjection sang TrainRouteDto
                .collect(Collectors.toList());
    }



    private void generateTripSchedulesFromRoute(Trip trip) {
        List<RouteStation> stops = routeStationRepository.findByRouteRouteIdOrderByStopOrderAsc(
                trip.getRoute().getRouteId()
        );

        List<TripSchedule> schedules = stops.stream()
                .map(stop -> TripSchedule.builder()
                        .trip(trip)
                        .station(stop.getStation())
                        .scheduledArrival(trip.getDepartureTime().plusMinutes(stop.getArrivalOffset()))
                        .scheduledDeparture(trip.getDepartureTime().plusMinutes(stop.getDepartureOffset()))
                        .status(TripSchedule.Status.scheduled)
                        .build())
                .collect(Collectors.toList());


        tripScheduleRepository.saveAll(schedules);
    }

    @Transactional
    @Override
    public void markTripDelayed(Integer tripId,Integer delayInMinutes,String delayReason) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến tàu"));

        if (!trip.getStatus().equals(Trip.Status.scheduled)) {
            throw new IllegalStateException("Chỉ chuyến scheduled mới được đánh dấu trễ");
        }

        trip.setStatus(Trip.Status.delayed);
        trip.setDelayMinutes(delayInMinutes);
        trip.setDelayReason(delayReason);
       Trip saveTrip= tripRepository.save(trip);
        // Ghi log hệ thống
        SystemLog logg = SystemLog.builder()
                .user(utils.getUser())
                .action(Constants.Action.UPDATE)
                .entityType("trip")
                .entityId( tripId)
                .description("Đánh dấu trễ " + trip.getTripCode() + " (" + delayInMinutes + " phút)")
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                //   .logTime(LocalDateTime.now())
                .build();
        systemLogRepository.save(logg);
        List<String> userEmails = tripRepository.findEffectiveEmailsByTripId(tripId);
        if (userEmails.isEmpty()) {
            log.warn("Không tìm thấy người dùng nào đã đặt vé trên chuyến tàu {}", trip.getTripCode());
            return;
        }
        cacheService.remove(Constants.Cache.CACHE_TRIP, tripId);
        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        notificationService.notifyUsers(saveTrip,userEmails);

        // cần add thêm batch job đ ể thông báo đến người dùng đã đặt vé trên chuyến tàu này
    }
    @Transactional
    @Override
    public void markTripCancel(Integer tripId, String cancelReason) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến tàu"));

        trip.setStatus(Trip.Status.cancelled);
        trip.setCancelledReason(cancelReason);
        Trip savedTrip = tripRepository.save(trip);

        // 1. Cập nhật tất cả vé liên quan sang trạng thái pending_refund
        List<Ticket> affectedTickets = ticketRepository.findAllByTrip_TripId(tripId);
        for (Ticket ticket : affectedTickets) {
            ticket.setStatus(Ticket.Status.pending_refund);
        }
        ticketRepository.saveAll(affectedTickets);

        // 2. Gom các Booking bị ảnh hưởng
        Set<Booking> affectedBookings = affectedTickets.stream()
                .map(Ticket::getBooking)
                .collect(Collectors.toSet());

        List<Payment> paymentsToUpdate = new ArrayList<>();

        for (Booking booking : affectedBookings) {
            List<Ticket> bookingTickets = ticketRepository.findByBookingBookingId(booking.getBookingId());
            double totalTicketPrice = bookingTickets.stream().mapToDouble(Ticket::getTicketPrice).sum();

            // Tính giảm giá nếu có
            double totalDiscount = 0;
            double netAmount = totalTicketPrice;

            BookingPromotion promotion = bookingPromotionRepository.findBookingPromotionByBooking_BookingId(booking.getBookingId());
            if (promotion != null) {
                totalDiscount = promotion.getDiscountAmount();
                if (totalDiscount < 0 || totalDiscount > totalTicketPrice) {
                    throw new BusinessException(ErrorCode.INVALID_DISCOUNT.code, "Giảm giá không hợp lệ.");
                }
                netAmount -= totalDiscount;
            }

            // Tìm chuyến sớm nhất để tính giờ cách giờ khởi hành
            LocalDateTime departureTime = bookingTickets.stream()
                    .map(t -> t.getTrip().getDepartureTime())
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            long hoursLeft = Duration.between(LocalDateTime.now(), departureTime).toHours();

            RefundPolicy policy = refundPolicyRepository.findAllByOrderByHoursBeforeDepartureDesc()
                    .stream()
                    .filter(p -> hoursLeft >= p.getHoursBeforeDeparture())
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_POLICY.code, ErrorCode.REFUND_POLICY.message));

            double refundAmount = netAmount * (policy.getRefundPercent() / 100.0);

            // Cập nhật trạng thái booking và payment
            booking.setBookingStatus(Booking.BookingStatus.refund_processing);

            Payment payment = paymentRepository.findByBooking(booking);
            if (payment.getStatus() == Payment.Status.completed) {
                payment.setStatus(Payment.Status.refund_pending);
                paymentsToUpdate.add(payment);
            }

            // Tạo bản ghi RefundRequest
            RefundRequest refundRequest = RefundRequest.builder()
                    .booking(booking)
                    .payment(payment)
                    .refundPolicy(policy)
                    .originalAmount(totalTicketPrice)
                    .discountAmount(totalDiscount)
                    .netAmount(netAmount)
                    .refundAmount(refundAmount)
                    .refundPercentage(policy.getRefundPercent())
                    .hoursBeforeDeparture(hoursLeft)
                    .status(RefundRequest.RefundStatus.pending)
                    .build();

            refundRequestRepository.save(refundRequest);
        }

        bookingRepository.saveAll(affectedBookings);
        paymentRepository.saveAll(paymentsToUpdate);

        // Ghi log hệ thống
        SystemLog logg = SystemLog.builder()
                .user(utils.getUser())
                .action(Constants.Action.UPDATE)
                .entityType("trip")
                .entityId(tripId)
                .description("Đánh dấu cancelled " + trip.getTripCode() + " (" + cancelReason + ")")
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build();
        systemLogRepository.save(logg);

        // Gửi thông báo
        List<String> userEmails = tripRepository.findEffectiveEmailsByTripId(tripId);
        if (userEmails.isEmpty()) {
            log.warn("Không tìm thấy người dùng nào đã đặt vé trên chuyến tàu {}", trip.getTripCode());
            return;
        }

//        cacheService.remove(Constants.Cache.CACHE_TRIP, tripId);
//        cacheService.remove(Constants.Cache.CACHE_TRIP, ALL_TRIPS_KEY);
        cacheService.clearCache(Constants.Cache.CACHE_TRIP);
        cacheService.clearCache(Constants.Cache.CACHE_BOOKING);
        cacheService.clearCache(Constants.Cache.CACHE_PAYMENT);

        notificationService.notifyUsers(savedTrip, userEmails);
    }


    @Override
    public Page<TripDto> findTrips(String search, String status, Pageable pageable) {
        Specification<Trip> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";

                // Join đến route và train để search theo tên
                Join<Object, Object> routeJoin = root.join("route");
                Join<Object, Object> trainJoin = root.join("train");

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tripCode")), searchPattern),
                        cb.like(cb.lower(routeJoin.get("routeName")), searchPattern),
                        cb.like(cb.lower(trainJoin.get("trainName")), searchPattern)
                ));
            }

            if (status != null && !status.equalsIgnoreCase("all")) {
                try {
                    Trip.Status tripStatus = Trip.Status.valueOf(status.toLowerCase());
                    predicates.add(cb.equal(root.get("status"), tripStatus));
                } catch (IllegalArgumentException e) {
//                    throw  new BusinessException()
                    log.warn("Trạng thái chuyến tàu không hợp lệ: " + status);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return tripRepository.findAll(spec, pageable).map(tripMapper::toDto);
    }



}
