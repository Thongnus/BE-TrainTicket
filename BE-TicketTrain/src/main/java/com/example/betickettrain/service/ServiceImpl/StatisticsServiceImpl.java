package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.MonthlyStatisticsProjection;
import com.example.betickettrain.repository.StatisticsRepository;
import com.example.betickettrain.service.GenericCacheService;
import com.example.betickettrain.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.betickettrain.util.Constants.Cache.CACHE_STATISTICS;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

   // private static final String CURRENT_MONTH_STATS_KEY = "monthly_stats_current";
    private final StatisticsRepository statisticsRepository;
   // private final GenericCacheService cacheService;

    @Override
    public MonthlyStatisticsProjection getStats() {

        //  cacheService.put(CACHE_STATISTICS, CURRENT_MONTH_STATS_KEY, stats);

        return statisticsRepository.getMonthlyStatistics();
    }
}
