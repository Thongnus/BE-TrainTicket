package com.example.betickettrain.util;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.TicketDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TemplateMail {
    public static String buildEmailContent(BookingDto booking, List<TicketDto> tickets) {
        StringBuilder content = new StringBuilder();
        content.append("Kính chào Quý Khách hàng  \n ");
        content.append("Cảm ơn bạn đã sử dụng dịch vụ đặt vé tàu của chúng tôi.\n");
        content.append("Thông tin đặt vé của bạn:\n\n");
        content.append("Mã booking: ").append(booking.getBookingCode()).append("\n");
        content.append("Ngày đặt: ").append(booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", booking.getTotalAmount())).append("\n");
        content.append("Trạng thái: Đã thanh toán\n\n");

        content.append("Chi tiết vé:\n");
        for (TicketDto ticket : tickets) {
            content.append("- Hành khách: ").append(ticket.getPassengerName()).append("\n");
            content.append("  Chuyến: ").append(ticket.getTrip().getTripCode()).append("\n");
            content.append("  Tàu: ").append(ticket.getTrip().getTrain().getTrainNumber()).append("\n");
            content.append("  Ghế: ").append(ticket.getSeat().getSeatNumber()).append("\n");
            content.append("  Toa: ").append(ticket.getSeat().getCarriage().getCarriageNumber()).append("\n");
            content.append("  Giá vé: ").append(String.format("%,.0f VNĐ", ticket.getTicketPrice())).append("\n");
            content.append("  Mã vé: ").append(ticket.getTicketCode()).append("\n\n");
        }

        content.append("Vui lòng lưu lại email này để làm thủ tục lên tàu.\n");
        content.append("Trân trọng,\n");
        content.append("Đội ngũ hỗ trợ khách hàng");

        return content.toString();
    }

    public static String buildEmailHtmlContent(BookingDto booking, List<TicketDto> tickets) {
        StringBuilder html = new StringBuilder();

        html.append("<div style='font-family: Arial,sans-serif; padding: 20px; max-width:600px; margin:auto;'>");
        html.append("<h2 style='text-align:center;'>Thông tin vé tàu của bạn</h2>");
        html.append("<p><strong>Mã booking:</strong> ").append(booking.getBookingCode()).append("</p>");
        html.append("<p><strong>Ngày đặt:</strong> ").append(booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</p>");
        html.append("<p><strong>Tổng tiền:</strong> ").append(String.format("%,.0f VNĐ", booking.getTotalAmount())).append("</p>");
        html.append("<p><strong>Trạng thái:</strong> Đã thanh toán</p>");

        html.append("<hr><h3>Chi tiết vé</h3>");
        for (TicketDto ticket : tickets) {
            html.append("<div style='margin-bottom:15px;'>");
            html.append("<p><strong>Hành khách:</strong> ").append(ticket.getPassengerName()).append("</p>");
            html.append("<p>Chuyến: ").append(ticket.getTrip().getTripCode()).append(" | Tàu: ").append(ticket.getTrip().getTrain().getTrainNumber()).append("</p>");
            html.append("<p>Toa: ").append(ticket.getSeat().getCarriage().getCarriageNumber())
                    .append(" | Ghế: ").append(ticket.getSeat().getSeatNumber()).append("</p>");
            html.append("<p>Giá vé: ").append(String.format("%,.0f VNĐ", ticket.getTicketPrice())).append("</p>");
            html.append("<p><strong>Mã vé:</strong> ").append(ticket.getTicketCode()).append("</p>");
            html.append("</div>");
        }

        html.append("<div style='text-align:center;margin-top:30px;'>");
        html.append("<p>Vui lòng xuất trình mã QR dưới đây khi lên tàu</p>");
        html.append("<img src='cid:qrCodeImage' width='180' height='180' alt='QR Code'/>");
        html.append("</div>");

        html.append("<p style='margin-top:20px;'>Trân trọng,<br>Đội ngũ hỗ trợ khách hàng</p>");
        html.append("</div>");

        return html.toString();
    }
    public static String buildTripDelayHtml(String tripCode, LocalDateTime departureTime, int delayMinutes, String reason) {
        String time = departureTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return """
        <div style='font-family:Arial,sans-serif;line-height:1.6;padding:20px;max-width:600px;margin:auto;border:1px solid #ccc;border-radius:8px;'>
            <h2 style='color:#d35400;'>THÔNG BÁO TRỄ CHUYẾN</h2>
            <p>Chuyến tàu <strong>%s</strong> dự kiến khởi hành lúc <strong>%s</strong> đã bị <strong>trễ %d phút</strong>.</p>
            <p><strong>Lý do:</strong> %s</p>
            <p>Chúng tôi thành thật xin lỗi vì sự bất tiện này và mong Quý khách thông cảm.</p>
            <hr>
            <p>Trân trọng,<br>Đội ngũ hỗ trợ khách hàng</p>
        </div>
    """.formatted(tripCode, time, delayMinutes, reason);
    }

    public static String buildTripCancelHtml(String tripCode, LocalDateTime departureTime, String reason) {
        String time = departureTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return """
        <div style='font-family:Arial,sans-serif;line-height:1.6;padding:20px;max-width:600px;margin:auto;border:1px solid #ccc;border-radius:8px;'>
            <h2 style='color:#c0392b;'>THÔNG BÁO HUỶ CHUYẾN</h2>
            <p>Chuyến tàu <strong>%s</strong> dự kiến khởi hành lúc <strong>%s</strong> đã bị <strong>huỷ</strong>.</p>
            <p><strong>Lý do:</strong> %s</p>
            <p>Chúng tôi sẽ hoàn tiền cho bạn trong thời gian sớm nhất.</p>
            <hr>
            <p>Trân trọng,<br>Đội ngũ hỗ trợ khách hàng</p>
        </div>
    """.formatted(tripCode, time, reason);
    }

}
