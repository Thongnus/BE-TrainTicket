package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.*;
import com.example.betickettrain.service.ExcelReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportServiceImpl implements ExcelReportService {
    @Override
    public Path generateReportFromClient(ReportPayloadDTO dto) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Sheet: Tổng quan
        createOverviewSheet(workbook, dto.getOverview());

        // Sheet: Doanh thu theo ngày
        createDailyRevenueSheet(workbook, dto.getDailyRevenue());

        // Sheet: Tuyến phổ biến
        createPopularRoutesSheet(workbook, dto.getPopularRoutes());

        // Sheet: Phân bổ loại vé theo toa
        createTicketDistributionSheet(workbook, dto.getTicketDistribution());

        // Sheet: Phân tích doanh thu
        createRevenueAnalysisSheet(workbook, dto.getRevenueAnalysis());

        // Ghi file
        String filename = String.format("dashboard-report-%d-%02d.xlsx", dto.getYear(), dto.getMonth());
        Path path = Paths.get("reports", filename);
        Files.createDirectories(path.getParent());

        try (FileOutputStream out = new FileOutputStream(path.toFile())) {
            workbook.write(out);
        }
        workbook.close();
        return path;
    }

    private void createOverviewSheet(XSSFWorkbook workbook, DashboardOverviewResponse overview) {
        XSSFSheet sheet = workbook.createSheet("Overview");
        int rowIdx = 0;

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Tổng doanh thu");
        sheet.getRow(0).createCell(1).setCellValue(overview.getTotalRevenue());

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Tăng trưởng doanh thu");
        sheet.getRow(1).createCell(1).setCellValue(overview.getRevenueGrowth());

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Tổng số vé");
        sheet.getRow(2).createCell(1).setCellValue(overview.getTotalTickets());

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Số vé 24h qua");
        sheet.getRow(3).createCell(1).setCellValue(overview.getTicketsLast24h());

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Tỷ lệ huỷ");
        sheet.getRow(4).createCell(1).setCellValue(overview.getCancellationRate());

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Thay đổi huỷ");
        sheet.getRow(5).createCell(1).setCellValue(overview.getCancellationRateChange());

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Chuyến đang chạy");
        sheet.getRow(6).createCell(1).setCellValue(overview.getActiveTrips());

        sheet.createRow(rowIdx++).createCell(0).setCellValue("Thay đổi số chuyến");
        sheet.getRow(7).createCell(1).setCellValue(overview.getTripsChange());
    }

    private void createDailyRevenueSheet(XSSFWorkbook workbook, DailyRevenueResponse daily) {
        XSSFSheet sheet = workbook.createSheet("Daily Revenue");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Ngày");
        header.createCell(1).setCellValue("Doanh thu");
        header.createCell(2).setCellValue("Số vé bán");

        for (int i = 0; i < daily.getDates().size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(daily.getDates().get(i));
            row.createCell(1).setCellValue(daily.getRevenue().get(i));
            row.createCell(2).setCellValue(daily.getTickets().get(i));
        }
    }

    private void createPopularRoutesSheet(XSSFWorkbook workbook, List<PopularRouteDTO> routes) {
        XSSFSheet sheet = workbook.createSheet("Popular Routes");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Tên tuyến");
        header.createCell(2).setCellValue("Lượt đặt");
        header.createCell(3).setCellValue("Doanh thu");

        for (int i = 0; i < routes.size(); i++) {
            PopularRouteDTO r = routes.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getName());
            row.createCell(2).setCellValue(r.getBookings());
            row.createCell(3).setCellValue(r.getRevenue());
        }
    }

    private void createTicketDistributionSheet(XSSFWorkbook workbook, List<TicketCarriageDistributionDTO> distribution) {
        XSSFSheet sheet = workbook.createSheet("Ticket Distribution");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Loại toa");
        header.createCell(1).setCellValue("Số vé");
        header.createCell(2).setCellValue("Tỷ lệ (%)");

        for (int i = 0; i < distribution.size(); i++) {
            TicketCarriageDistributionDTO d = distribution.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(d.getName());
            row.createCell(1).setCellValue(d.getCount());
            row.createCell(2).setCellValue(d.getPercentage());
        }
    }

    private void createRevenueAnalysisSheet(XSSFWorkbook workbook, RevenueAnalysisResponse analysis) {
        XSSFSheet sheet = workbook.createSheet("Revenue Analysis");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tháng");
        header.createCell(1).setCellValue("Doanh thu");
        header.createCell(2).setCellValue("Tăng trưởng (%)");
        header.createCell(3).setCellValue("Giá vé TB");

        for (int i = 0; i < analysis.getPeriods().size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(analysis.getPeriods().get(i));
            row.createCell(1).setCellValue(analysis.getRevenue().get(i));
            row.createCell(2).setCellValue(analysis.getGrowth().get(i));
            row.createCell(3).setCellValue(analysis.getAverageTicketPrice().get(i));
        }
    }
}
