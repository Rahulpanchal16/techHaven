package com.tech.haven.helpers;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DottedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.tech.haven.exceptions.ResourceNotFoundException;
import com.tech.haven.models.Order;
import com.tech.haven.models.OrderItem;
import com.tech.haven.repositories.OrderRepository;

@Service
public class InvoiceGeneration {

        @Autowired
        private OrderRepository orderRepo;

        private Double totalBillAmount = 0.0;

        public void createPdf(String orderId) throws IOException {

                Order order = orderRepo.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

                String emailId = order.getUser().getEmail();
                String filePath = "E:\\PDF\\" + emailId;
                File directory = new File(filePath);
                if (!directory.exists()) {
                        boolean dirCreated = directory.mkdirs();
                        if (!dirCreated) {
                                throw new RuntimeException("Failed to create directory");
                        }
                }

                PdfWriter writer = new PdfWriter(directory + "//invoice-" + orderId + ".pdf");
                PdfDocument document = new PdfDocument(writer);
                document.setDefaultPageSize(PageSize.A4);
                Document pdf = new Document(document);

                pdf.setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN));
                float colThird = 190f;

                float col1 = 285f;
                float col2 = col1 + 150f;
                float totalColWidth[] = { col2, col1 };
                float[] fullWidth = { colThird * 3 };

                Table table = new Table(totalColWidth);
                table.addCell(new Cell().add("Order Invoice").setBorder(Border.NO_BORDER).setBold().setFontSize(20f));
                Table nestedTable = new Table(new float[] { col1 / 2, col1 / 2 });

                String substring = order.getOrderId().substring(0, 8);
                String invoiceNo = ("OR" + substring).toUpperCase();

                nestedTable.addCell(getHeaderTextCell("Invoice no:"));
                nestedTable.addCell(getHeaderTextCellvalue(invoiceNo));
                nestedTable.addCell(getHeaderTextCell("Invoice date:"));
                LocalDateTime dateTime = LocalDateTime.now();
                int day = dateTime.getDayOfMonth();
                int month = dateTime.getMonthValue();
                int year = dateTime.getYear();
                String finalDate = day + "-" + month + "-" + year;
                nestedTable.addCell(getHeaderTextCellvalue(finalDate));

                table.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));
                Border gb = new SolidBorder(Color.GRAY, 2f);
                Table divider = new Table(fullWidth);
                divider.setBorder(gb).setMarginBottom(15f);

                pdf.add(table);
                // pdf.add(newBlankLine);
                pdf.add(divider);

                float oneCol = 285f;
                float twoCol = 285f;
                float twoColWidth[] = { oneCol, twoCol };
                Table twoColumnTable = new Table(twoColWidth);
                twoColumnTable.addCell(getBillingAndShippingInfo("Billing Information"));
                twoColumnTable.addCell(getBillingAndShippingInfo("Shipping Information"));
                pdf.add(twoColumnTable.setMarginBottom(10f));

                Table twoColTable2 = new Table(twoColWidth);
                twoColTable2.addCell(getCell10Left("Company", true));
                twoColTable2.addCell(getCell10Left("Name", true));
                twoColTable2.addCell(getCell10Left("TechHaven pvt ltd.", false));
                twoColTable2.addCell(getCell10Left(order.getUser().getName(), false));
                pdf.add(twoColTable2);

                Table twoColTable3 = new Table(twoColWidth);
                twoColTable3.addCell(getCell10Left("Name", true));
                twoColTable3.addCell(getCell10Left("Address", true));
                twoColTable3.addCell(getCell10Left("Rahul Panchal", false));
                twoColTable3.addCell(getCell10Left(order.getDeliveryAddress(), false));
                pdf.add(twoColTable3);

                Table twoColTable4 = new Table(twoColWidth);
                twoColTable4.addCell(getCell10Left("Contact: ", true));
                twoColTable4.addCell(getCell10Left("Contact: ", true));
                twoColTable4.addCell(getCell10Left("1234567", false));
                twoColTable4.addCell(getCell10Left(Long.toString(order.getContactNumber()), false));
                pdf.add(twoColTable4);

                Table fullWidthTable = new Table(fullWidth);
                fullWidthTable.addCell(getCell10Left("Address", true));
                fullWidthTable.addCell(getCell10Left("Flat no - 02, Gokul Garden, Baner, Pune", false));
                fullWidthTable.addCell(getCell10Left("Email: " + "panchalrahul180@gmail.com", false));
                pdf.add(fullWidthTable.setMarginBottom(10f));

                Table dashedDivider = new Table(fullWidth);
                Border dashedBorder = new DottedBorder(Color.BLACK, 1f);
                dashedDivider.setBorder(dashedBorder);
                pdf.add(dashedDivider.setMarginBottom(5f));

                Paragraph prod = new Paragraph();
                prod.add("Products").setFontSize(13f).setTextAlignment(TextAlignment.LEFT).setBold();
                pdf.add(prod);

                float oneCOlOne = 114f;
                float fiveColWidth[] = { oneCOlOne, oneCOlOne, oneCOlOne, oneCOlOne, oneCOlOne };
                Table threeColTable = new Table(fiveColWidth);
                threeColTable.addCell(getHeaderTextCell("Description").setFontColor(Color.WHITE)
                                .setBackgroundColor(Color.DARK_GRAY).setTextAlignment(TextAlignment.LEFT));
                threeColTable.addCell(
                                getHeaderTextCell("Cost").setFontColor(Color.WHITE).setBackgroundColor(Color.DARK_GRAY)
                                                .setTextAlignment(TextAlignment.LEFT));
                threeColTable.addCell(getHeaderTextCell("Quantity").setFontColor(Color.WHITE)
                                .setBackgroundColor(Color.DARK_GRAY).setTextAlignment(TextAlignment.LEFT));
                threeColTable.addCell(getHeaderTextCell("Discount").setFontColor(Color.WHITE)
                                .setBackgroundColor(Color.DARK_GRAY).setTextAlignment(TextAlignment.LEFT));
                threeColTable.addCell(getHeaderTextCell("Total").setFontColor(Color.WHITE)
                                .setBackgroundColor(Color.DARK_GRAY).setTextAlignment(TextAlignment.LEFT));
                DecimalFormat dc = new DecimalFormat("0.00");
                List<OrderItem> orderItems = order.getOrderItems();
                orderItems.stream().forEach(orderItem -> {
                        String productTitle = orderItem.getProduct().getTitle();
                        double productDiscount = orderItem.getProduct().getDiscount();
                        double prodPrice = orderItem.getProduct().getPrice();
                        int quantityOrdered = orderItem.getQuantityOrdered();
                        double subTotal = prodPrice * quantityOrdered;
                        double discountAmount = subTotal * (productDiscount / 100.00);
                        double totalProdPrice = subTotal - discountAmount;

                        threeColTable.addCell(getHeaderTextCellvalue(productTitle));
                        threeColTable.addCell(getHeaderTextCellvalue(Double.toString(prodPrice)));
                        threeColTable.addCell(getHeaderTextCellvalue(Integer.toString(quantityOrdered)));
                        threeColTable.addCell(getHeaderTextCellvalue((Double.toString(productDiscount)) + "%"));
                        threeColTable.addCell(getHeaderTextCellvalue(dc.format(totalProdPrice)));
                        totalBillAmount += totalProdPrice;
                });
                pdf.add(threeColTable);

                pdf.add(dashedDivider);

                Table totalBillTable = new Table(fiveColWidth);
                totalBillTable.addCell(getHeaderTextCellvalue(""));
                totalBillTable.addCell(getHeaderTextCellvalue(""));
                totalBillTable.addCell(getHeaderTextCellvalue(""));
                totalBillTable.addCell(getBillingAndShippingInfo("Total"));
                totalBillTable.addCell(getHeaderTextCellvalue("Rs." + dc.format(totalBillAmount)));
                pdf.add(totalBillTable.setBorder(Border.NO_BORDER));

                pdf.add(dashedDivider);

                pdf.add(divider.setMarginTop(5f));

                Paragraph terms = new Paragraph();
                terms.add("Terms & Conditions").setBold().setFontSize(13f).setTextAlignment(TextAlignment.LEFT);
                pdf.add(terms);

                Paragraph tnc = new Paragraph();
                tnc.add("1. All sales are final. No refunds or exchanges unless otherwise stated in writing.\n")
                                .setFontSize(8f);
                tnc.add("2. All charges listed on this bill are subject to applicable taxes and fees.\n")
                                .setFontSize(8f);
                tnc.add("3. Any disputes regarding charges on this bill must be brought to our attention within 30 days of receipt.")
                                .setFontSize(8f);
                pdf.add(tnc);

                pdf.close();
        }

        static Cell getHeaderTextCell(String value) {
                return new Cell().add(value).setBorder(Border.NO_BORDER).setBold().setTextAlignment(TextAlignment.RIGHT)
                                .setFontSize(10f);
        }

        static Cell getHeaderTextCellvalue(String value) {
                return new Cell().add(value).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        }

        static Cell getBillingAndShippingInfo(String value) {
                return new Cell().add(value).setFontSize(12f).setBold().setBorder(Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.LEFT);
        }

        static Cell getCell10Left(String value, boolean isBold) {
                Cell myCell = new Cell().add(value).setFontSize(10f).setBorder(Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.LEFT);
                return isBold ? myCell.setBold() : myCell;
        }
}
