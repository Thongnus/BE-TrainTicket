package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.repository.BookingRepository;
import com.example.betickettrain.repository.TicketRepository;
import com.example.betickettrain.repository.TripRepository;
import com.example.betickettrain.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final TicketRepository ticketRepository;

    @Override
    public DashboardOverviewResponse getOverview() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime lastMonth = now.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime last24h = now.minusHours(24);

        double totalRevenue = bookingRepository.sumRevenue(startOfMonth, now);
        double revenueLastMonth = bookingRepository.sumRevenue(lastMonth, startOfMonth);
        double revenueGrowth = (revenueLastMonth > 0) ? ((totalRevenue - revenueLastMonth) / revenueLastMonth) * 100 : 0;

        int totalTickets = bookingRepository.countTickets(startOfMonth, now);
        int ticketsLast24h = bookingRepository.countTickets(last24h, now);

        int cancelledTickets = bookingRepository.countCancelledTickets(startOfMonth, now);
        double cancellationRate = totalTickets > 0 ? (cancelledTickets * 100.0 / totalTickets) : 0;

        int cancelledLastMonth = bookingRepository.countCancelledTickets(lastMonth, startOfMonth);
        int totalTicketsLastMonth = bookingRepository.countTickets(lastMonth, startOfMonth);
        double cancellationRateLastMonth = totalTicketsLastMonth > 0 ? (cancelledLastMonth * 100.0 / totalTicketsLastMonth) : 0;
        double cancellationRateChange = cancellationRateLastMonth > 0 ? ((cancellationRate - cancellationRateLastMonth) / cancellationRateLastMonth) * 100 : 0;

        int completedTripsTrips = tripRepository.countCompletedTrips();
        int completedTripsLastMonth = tripRepository.counttCompletedTripsBetween(lastMonth, startOfMonth);
        int tripsChange = completedTripsLastMonth > 0 ? (completedTripsTrips - completedTripsLastMonth) : completedTripsTrips;

        return DashboardOverviewResponse.builder().totalRevenue(totalRevenue).revenueGrowth(Math.round(revenueGrowth * 100.0) / 100.0).totalTickets(totalTickets).ticketsLast24h(ticketsLast24h).cancellationRate(Math.round(cancellationRate * 100.0) / 100.0).cancellationRateChange(Math.round(cancellationRateChange * 100.0) / 100.0).activeTrips(completedTripsTrips).tripsChange(tripsChange).build();
    }

    @Override
    public DailyRevenueResponse getDailyRevenue() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        int daysInMonth = currentMonth.lengthOfMonth();

        List<String> dates = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        List<Integer> tickets = new ArrayList<>();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            double dailyRevenue = bookingRepository.sumRevenue(start, end);
            int dailyTickets = ticketRepository.countTicketsBetween(start, end);

            dates.add(date.toString());
            revenue.add(dailyRevenue);
            tickets.add(dailyTickets);
        }

        return DailyRevenueResponse.builder().dates(dates).revenue(revenue).tickets(tickets).build();
    }

    @Override
    public List<PopularRouteDTO> getPopularRoutes() {
        List<PopularRouteProjection> projections = ticketRepository.findPopularRoutes();

        return projections.stream().map(p -> new PopularRouteDTO(p.getId(), p.getName(), p.getBookings(), p.getRevenue())).collect(Collectors.toList());
    }


    @Override
    public List<TicketCarriageDistributionDTO> getTicketDistributionByCarriage() {
        List<Map<String, Object>> raw = ticketRepository.getTicketDistributionRaw();

        long total = raw.stream().mapToLong(r -> ((Number) r.get("count")).longValue()).sum();

        return raw.stream().map(r -> new TicketCarriageDistributionDTO() {
            public String getName() {
                return (String) r.get("name");
            }

            public Long getCount() {
                return ((Number) r.get("count")).longValue();
            }

            public Double getPercentage() {
                return total > 0 ? Math.round(((Number) r.get("count")).doubleValue() * 10000.0 / total) / 100.0 : 0.0;
            }
        }).collect(Collectors.toCollection(ArrayList::new));
    }


    @Override
    public RevenueAnalysisResponse getRevenueAnalysis() {
        List<Map<String, Object>> raw = ticketRepository.getRevenueLast6Months();

        List<String> periods = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        List<Double> growth = new ArrayList<>();
        List<Double> avgPrices = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {
            Map<String, Object> row = raw.get(i);
            String period = (String) row.get("period");
            double rev = ((Number) row.get("revenue")).doubleValue();
            long tickets = ((Number) row.get("totalTickets")).longValue();

            periods.add(period);
            revenue.add(rev);
            avgPrices.add(tickets > 0 ? Math.round((rev / tickets) * 100.0) / 100.0 : 0.0);

            if (i == 0) {
                growth.add(0.0); // tháng đầu không có tăng trưởng
            } else {
                double prev = revenue.get(i - 1);
                double gr = prev > 0 ? ((rev - prev) / prev) * 100.0 : 0.0;
                growth.add(Math.round(gr * 100.0) / 100.0);
            }
        }

        return RevenueAnalysisResponse.builder().periods(periods).revenue(revenue).growth(growth).averageTicketPrice(avgPrices).build();
    }

}
