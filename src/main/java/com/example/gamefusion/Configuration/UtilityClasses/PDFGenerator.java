package com.example.gamefusion.Configuration.UtilityClasses;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import com.example.gamefusion.Entity.Address;
import com.example.gamefusion.Entity.OrderMain;
import com.lowagie.text.*;
import java.io.IOException;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.example.gamefusion.Entity.OrderSub;
import com.lowagie.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;

public class PDFGenerator {

    public static void generate(HttpServletResponse response, List<OrderSub> productDtoList,
                                OrderMain orderMainDto){

        try (Document document = new Document(PageSize.A4)) {

            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
            fontTitle.setSize(20);
            fontTitle.setColor(Color.darkGray);
            Font fontParagraph = FontFactory.getFont("Monospaced");
            fontParagraph.setSize(12);
            fontParagraph.setColor(Color.darkGray);
            Font fontTableTitle = FontFactory.getFont(FontFactory.COURIER_BOLDOBLIQUE);
            fontTableTitle.setSize(10);
            fontTableTitle.setColor(Color.darkGray);

            Paragraph paragraph = new Paragraph("Invoice\n", fontTitle);
            paragraph.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(paragraph);

            document.add(new Paragraph("\n"));
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(Color.GRAY);
            lineSeparator.setLineWidth(5);
            document.add(lineSeparator);
            document.add(new Paragraph("\n"));

            String str  ="OrderId Number: "+orderMainDto.getOrderId()+"\nInvoice Date: "+LocalDate.now()+"\nOrder Date: "+orderMainDto.getDate();
            Address addressE = orderMainDto.getAddress();
            String address = addressE.getStreetAddress()+"\n"+addressE.getDistrict()+", "+addressE.getState()+"\n"+
                             addressE.getCountry()+"\nPin Code : "+addressE.getPinCode()+"\nPhone : "+addressE.getPhone();

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.setWidths(new int[] { 6,4 });
            PdfPCell cell1 = new PdfPCell(new Paragraph(str, fontParagraph));
            PdfPCell cell2 = new PdfPCell(new Paragraph(address, fontParagraph));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            document.add(table);

            document.add(new Paragraph("\n"));
            lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(Color.GRAY);
            lineSeparator.setLineWidth(5);
            document.add(lineSeparator);
            document.add(new Paragraph("\n"));

            table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new int[] { 10,2,3,3 });

            PdfPCell cell = new PdfPCell();
            cell.setPadding(5);
            cell.setBorderColor(Color.GRAY);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

            cell.setPhrase(new Paragraph("Product",fontTableTitle));
            table.addCell(cell);
            cell.setPhrase(new Paragraph("Quantity",fontTableTitle));
            table.addCell(cell);
            cell.setPhrase(new Paragraph("Unit Price",fontTableTitle));
            table.addCell(cell);
            cell.setPhrase(new Paragraph("Sub Total",fontTableTitle));
            table.addCell(cell);

            for (OrderSub product: productDtoList) {
                cell.setPhrase(new Phrase(product.getProduct().getName(),fontTableTitle));
                table.addCell(cell);
                cell.setPhrase(new Phrase(String.valueOf(product.getQty()),fontTableTitle));
                table.addCell(cell);
                cell.setPhrase(new Phrase(String.valueOf(product.getProduct().getDiscountPrice()),fontTableTitle));
                table.addCell(cell);
                cell.setPhrase(new Phrase(String.valueOf(product.getQty()*product.getProduct().getDiscountPrice()),fontTableTitle));
                table.addCell(cell);
            }
            document.add(table);

            String paymentMethod;
            if (orderMainDto.getPaymentMethod().equals(String.valueOf(PaymentMethodUtil.CASH_ON_DELIVERY)))
                paymentMethod = "Cash On Delivery";
            if (orderMainDto.getPaymentMethod().equals(String.valueOf(PaymentMethodUtil.ONLINE_PAYMENT)))
                paymentMethod = "Online Payment";
            else
                paymentMethod = "Wallet Payment";

            fontTitle.setSize(15);
            paragraph = new Paragraph("Payment Method :\t"+paymentMethod, fontTitle);
            paragraph.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(paragraph);


            paragraph = new Paragraph("Total :\t"+orderMainDto.getAmount(), fontTitle);
            paragraph.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(paragraph);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
