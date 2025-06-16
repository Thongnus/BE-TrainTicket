package com.example.betickettrain.service;

import com.example.betickettrain.dto.ReportPayloadDTO;

import java.io.IOException;
import java.nio.file.Path;

public interface ExcelReportService {
    Path generateReportFromClient(ReportPayloadDTO dto) throws IOException;
}
