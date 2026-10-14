package com.restaurant.restaurant_app.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.restaurant.restaurant_app.repository.RestaurantTableRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/manager/qr")
@RequiredArgsConstructor
public class QrController {

    private final RestaurantTableRepository tableRepository;

    @GetMapping
    public String qrPage(Model model, HttpServletRequest request) {
        model.addAttribute("tables", tableRepository.findAll(Sort.by("id")));
        String base = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443
                   ? "" : ":" + request.getServerPort());
        model.addAttribute("baseUrl", base);
        return "manager/qr";
    }

    @GetMapping("/image/{tableId}")
    @ResponseBody
    public ResponseEntity<byte[]> qrImage(@PathVariable Long tableId,
                                           HttpServletRequest request) throws WriterException, IOException {
        String url = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443
                   ? "" : ":" + request.getServerPort())
                + "/t/" + tableId + "/welcome";
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(out.toByteArray());
    }
}
