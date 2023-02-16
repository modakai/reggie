package com.sakura.reggieApi.module.order.controller;

import com.sakura.reggieApi.common.anno.LogAnnotation;
import com.sakura.reggieApi.common.utils.JsonResponseResult;
import com.sakura.reggieApi.common.utils.TokenUtils;
import com.sakura.reggieApi.module.order.dto.OrderDto;
import com.sakura.reggieApi.module.order.pojo.Orders;
import com.sakura.reggieApi.module.order.service.OrdersService;
import com.sakura.reggieApi.utils.ExcelUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 * @className OrdersController
 * @createTime 2023/2/15
 */
@RestController
@RequestMapping("/backend/orders")
public class BackendOrdersController {

    private static final String HERDER_TOKEN_KEY = TokenUtils.REQUEST_HEADER_TOKEN_KEY;

    @Resource
    OrdersService ordersService;

    @LogAnnotation("下载订单报表")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PostMapping("/down")
    public void doDownReport(@RequestHeader(HERDER_TOKEN_KEY) String token,
                             HttpServletResponse response) throws IOException {
        List<Orders> ordersList = ordersService.list();

        HSSFWorkbook workbook = new HSSFWorkbook();//创建HSSFWorkbook对象,  excel的文档对象

        HSSFSheet sheet = workbook.createSheet("订单表"); //excel的表单
        sheet.setDefaultColumnWidth(25);
        // 设置表头字体格式
        HSSFFont headersFont = workbook.createFont();
        headersFont.setFontHeightInPoints((short) 14);
        headersFont.setBold(true);
        // 创建表头样式
        HSSFCellStyle headersStyle = workbook.createCellStyle();
        headersStyle.setBorderTop(BorderStyle.THIN);
        headersStyle.setBorderBottom(BorderStyle.THIN);
        headersStyle.setBorderLeft(BorderStyle.THIN);
        headersStyle.setBorderRight(BorderStyle.THIN);
        headersStyle.setFont(headersFont);
        // 表头内容对齐方式：居中
        headersStyle.setAlignment(HorizontalAlignment.CENTER);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String format = dateFormat.format(new Date());
        // 设置导出的名字
        String fileName = format + "-ordersTable" +  ".xls";
        int rowNum = 1;
        // 创建 表行头
        String[] headers = {"订单号", "订单状态", "下单用户id",
                "地址id", "下单时间", "结账时间",
                "支付方式", "实收金额", "备注",
                "用户名", "手机号", "地址", "收货人"};
        HSSFRow row = sheet.createRow(0);
        // 添加 excel 表头
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            cell.setCellStyle(headersStyle);
        }

        // 添加数据
        for (Orders orders : ordersList) {
            HSSFRow row1 = sheet.createRow(rowNum);

            ExcelUtils.modifyStyle(row1.createCell(0), orders.getNumber(), workbook);

            Integer status = orders.getStatus();
            String strStatus = "";
            switch (status){
                case 1:
                    strStatus = "待付款";
                    break;
                case 2:
                    strStatus = "待派送";
                    break;
                case 3:
                    strStatus = "已派送";
                    break;
                case 4:
                    strStatus = "已完成";
                    break;
                case 5:
                    strStatus = "已取消";
                    break;
            }
            row1.createCell(1).setCellValue(strStatus);

            // 会变成科学计数法
            ExcelUtils.modifyStyle(row1.createCell(2), orders.getUserId().toString(), workbook);

            // 变成科学计数法
            ExcelUtils.modifyStyle(row1.createCell(3), orders.getAddressBookId().toString(), workbook);
            ExcelUtils.modifyStyle(row1.createCell(4), orders.getOrderTime(), workbook);
            ExcelUtils.modifyStyle(row1.createCell(5), orders.getCheckoutTime(), workbook);

            ExcelUtils.modifyStyle(row1.createCell(7), orders.getAmount(), workbook);
            ExcelUtils.modifyStyle(row1.createCell(8), orders.getRemark(), workbook);
            ExcelUtils.modifyStyle(row1.createCell(9), orders.getUserName(), workbook);
            ExcelUtils.modifyStyle(row1.createCell(10), orders.getPhone(), workbook);
            ExcelUtils.modifyStyle(row1.createCell(11), orders.getAddress(), workbook);
            ExcelUtils.modifyStyle(row1.createCell(12), orders.getConsignee(), workbook);


            row1.createCell(6).setCellValue(orders.getPayMethod() == 1 ? "微信支付" : "支付宝支付");

        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @LogAnnotation("修改订单状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @PutMapping
    public String doUpdate(@RequestHeader(HERDER_TOKEN_KEY) String token,
                           @RequestBody OrderDto orderDto) {
        return ordersService.updateStatus(token, orderDto);
    }


    @LogAnnotation("查看订单详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/detail")
    public String doDetail(@RequestHeader(HERDER_TOKEN_KEY) String token,
                           @RequestBody OrderDto orderDto) {
        return ordersService.queryDetail(token, orderDto);
    }

    @LogAnnotation("订单条件查询")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/query")
    public String doConditionalQuery(@RequestHeader(HERDER_TOKEN_KEY) String token,
                                     @RequestBody OrderDto orderDto) {
        return ordersService.conditionalListPage(token, orderDto);
    }


    @LogAnnotation("分页查询订单")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPADMIN')")
    @GetMapping("/list/{curPage}")
    public String doList(@RequestHeader(HERDER_TOKEN_KEY) String token,
                         @PathVariable("curPage") Integer curPage) {
        return ordersService.listPage(token, curPage);
    }
}
