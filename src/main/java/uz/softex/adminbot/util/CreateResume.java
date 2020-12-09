package uz.softex.adminbot.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import uz.softex.adminbot.model.Resume;

import java.io.*;

public class CreateResume {
    public static File printPdf(Resume resume){
        File file = new File("pdf/"+resume.getId()+".pdf");
        try (OutputStream outputStream = new FileOutputStream(file)) {
            BaseFont baseFont = BaseFont.createFont("font/time-roman_[allfont.net].ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Image image = Image.getInstance("pic/"+resume.getPicUrl());
            Image watermark = Image.getInstance("pdf/watermark.jpg");
            watermark.setAbsolutePosition(185,10);
            watermark.scalePercent(5f);
            image.scalePercent(25f);

            Font font1 = new Font(baseFont, 28, Font.NORMAL, new BaseColor(83,160,75));
            Font font2 = new Font(baseFont, 16, Font.NORMAL, BaseColor.BLACK);
            Font font3 = new Font(baseFont, 12, Font.NORMAL, BaseColor.BLACK);
            Font font5 = new Font(baseFont, 12, Font.BOLD, new BaseColor(83,160,75));
            Font font4 = new Font(baseFont, 12, Font.BOLD, BaseColor.BLACK);
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfTemplate t = pdfWriter.getDirectContent().createTemplate(500, 500);
            t.ellipse(0, 0, 500, 500);
            t.clip();
            t.newPath();
            t.addImage(image, 500, 0, 0, 500, 0, 0);
            Image clipped = Image.getInstance(t);
            clipped.scalePercent(25f);

            PdfContentByte contentByte = pdfWriter.getDirectContent();
            contentByte.addImage(watermark);

            // 1-paragraf boshlanishi
            float[] columnWidthh = {30f, 80f};
            PdfPTable tableh = new PdfPTable(columnWidthh);
            tableh.setWidthPercentage(100f);
            PdfPCell cellh1 = new PdfPCell(clipped);
            PdfPCell cellh2 = new PdfPCell(new Phrase(
                    resume.getFirstname()+" "+resume.getLastname()+" "+resume.getFathersName(), font1));
            PdfPCell cellh3 = new PdfPCell(new Phrase(
                    "Talaba", font2));
            PdfPCell cellh4 = new PdfPCell(new Phrase("SHAXSIY MA'LUMOTLAR",font4));
            PdfPCell cellh5 = new PdfPCell(new Phrase("SHAXSIY FAOLIYAT",font4));
            PdfPCell cellh6 = new PdfPCell(new Phrase("Tug'ilgan sanasi:",font5));
            PdfPCell cellh7 = new PdfPCell(new Phrase("Kasbiy faoliyati",font5));
            PdfPCell cellh8 = new PdfPCell(new Phrase(resume.getDateOfBirth(),font3));
            PdfPCell cellh9 = new PdfPCell(new Phrase(resume.getJobExperience(),font3));
            PdfPCell cellh10 = new PdfPCell(new Phrase("Manzil:",font5));
            PdfPCell cellh11 = new PdfPCell(new Phrase("Ma'lumoti",font5));
            PdfPCell cellh12 = new PdfPCell(new Phrase(resume.getAddress(),font3));
            PdfPCell cellh13 = new PdfPCell(new Phrase(resume.getEducation(),font3));
            PdfPCell cellh14 = new PdfPCell(new Phrase("Telefon:",font5));
            PdfPCell cellh15 = new PdfPCell(new Phrase("Bilgan tillari",font5));
            PdfPCell cellh16 = new PdfPCell(new Phrase(resume.getPhone(),font3));
            PdfPCell cellh17 = new PdfPCell(new Phrase(resume.getLanguage(),font3));
            PdfPCell cellh18 = new PdfPCell(new Phrase("",font5));
            PdfPCell cellh19 = new PdfPCell(new Phrase("Oilaviy holati:",font5));
            PdfPCell cellh20 = new PdfPCell(new Phrase(resume.getMaritalStatus(),font3));
            PdfPCell cellh21 = new PdfPCell(new Phrase("Maqsadi:",font5));
            PdfPCell cellh22 = new PdfPCell(new Phrase(resume.getPurpose(),font3));
            PdfPCell cellh23 = new PdfPCell(new Phrase("Shaxsiy xususiyatlari:",font5));
            PdfPCell cellh24 = new PdfPCell(new Phrase(resume.getPersonalProperty(),font3));
            PdfPCell cellh25 = new PdfPCell(new Phrase("Qo'shimcha bilimlari:",font5));
            PdfPCell cellh26 = new PdfPCell(new Phrase(resume.getAdditionalKnowledge(),font3));
            tableh.setHorizontalAlignment(Element.ALIGN_CENTER);

            cellh1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh1.setVerticalAlignment(Element.ALIGN_TOP);
            cellh1.setBorder(0);
            cellh1.setRowspan(2);
            cellh1.setFixedHeight(120);

            cellh2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh2.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cellh2.setBorder(0);
            cellh2.setFixedHeight(70);

            cellh3.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh3.setVerticalAlignment(Element.ALIGN_TOP);
            cellh3.setBorder(0);
            cellh3.setFixedHeight(50);

            cellh4.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh4.setVerticalAlignment(Element.ALIGN_TOP);
            cellh4.setBorder(0);
            cellh4.setFixedHeight(40);
            cellh4.setPaddingTop(10);

            cellh5.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh5.setVerticalAlignment(Element.ALIGN_TOP);
            cellh5.setBorder(0);
            cellh5.setFixedHeight(40);
            cellh5.setPaddingTop(10);

            cellh6.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh6.setVerticalAlignment(Element.ALIGN_TOP);
            cellh6.setBorder(0);
            cellh6.setPaddingTop(5);

            cellh7.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh7.setVerticalAlignment(Element.ALIGN_TOP);
            cellh7.setBorder(0);
            cellh7.setPaddingTop(5);

            cellh8.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh8.setVerticalAlignment(Element.ALIGN_TOP);
            cellh8.setBorder(0);

            cellh9.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh9.setVerticalAlignment(Element.ALIGN_TOP);
            cellh9.setBorder(0);


            cellh10.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh10.setVerticalAlignment(Element.ALIGN_TOP);
            cellh10.setBorder(0);
            cellh10.setPaddingTop(5);

            cellh11.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh11.setVerticalAlignment(Element.ALIGN_TOP);
            cellh11.setBorder(0);
            cellh11.setPaddingTop(5);

            cellh12.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh12.setVerticalAlignment(Element.ALIGN_TOP);
            cellh12.setBorder(0);

            cellh13.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh13.setVerticalAlignment(Element.ALIGN_TOP);
            cellh13.setBorder(0);

            cellh14.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh14.setVerticalAlignment(Element.ALIGN_TOP);
            cellh14.setBorder(0);
            cellh14.setPaddingTop(5);

            cellh15.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh15.setVerticalAlignment(Element.ALIGN_TOP);
            cellh15.setBorder(0);
            cellh15.setPaddingTop(5);

            cellh16.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh16.setVerticalAlignment(Element.ALIGN_TOP);
            cellh16.setBorder(0);

            cellh17.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh17.setVerticalAlignment(Element.ALIGN_TOP);
            cellh17.setBorder(0);
            cellh17.setPaddingTop(5);

            cellh18.setRowspan(8);
            cellh18.setBorder(0);

            cellh19.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh19.setVerticalAlignment(Element.ALIGN_TOP);
            cellh19.setBorder(0);
            cellh19.setPaddingTop(5);

            cellh20.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh20.setVerticalAlignment(Element.ALIGN_TOP);
            cellh20.setBorder(0);

            cellh21.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh21.setVerticalAlignment(Element.ALIGN_TOP);
            cellh21.setBorder(0);
            cellh21.setPaddingTop(5);

            cellh22.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh22.setVerticalAlignment(Element.ALIGN_TOP);
            cellh22.setBorder(0);

            cellh23.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh23.setVerticalAlignment(Element.ALIGN_TOP);
            cellh23.setBorder(0);
            cellh23.setPaddingTop(5);

            cellh24.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh24.setVerticalAlignment(Element.ALIGN_TOP);
            cellh24.setBorder(0);

            cellh25.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh25.setVerticalAlignment(Element.ALIGN_TOP);
            cellh25.setBorder(0);
            cellh25.setPaddingTop(5);

            cellh26.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellh26.setVerticalAlignment(Element.ALIGN_TOP);
            cellh26.setBorder(0);

            tableh.addCell(cellh1);
            tableh.addCell(cellh2);
            tableh.addCell(cellh3);
            tableh.addCell(cellh4);
            tableh.addCell(cellh5);
            tableh.addCell(cellh6);
            tableh.addCell(cellh7);
            tableh.addCell(cellh8);
            tableh.addCell(cellh9);
            tableh.addCell(cellh10);
            tableh.addCell(cellh11);
            tableh.addCell(cellh12);
            tableh.addCell(cellh13);
            tableh.addCell(cellh14);
            tableh.addCell(cellh15);
            tableh.addCell(cellh16);
            tableh.addCell(cellh17);
            tableh.addCell(cellh18);
            tableh.addCell(cellh19);
            tableh.addCell(cellh20);
            tableh.addCell(cellh21);
            tableh.addCell(cellh22);
            tableh.addCell(cellh23);
            tableh.addCell(cellh24);
            tableh.addCell(cellh25);
            tableh.addCell(cellh26);

            Paragraph paragraph = new Paragraph(10);
            paragraph.setFont(font1);
            paragraph.setIndentationLeft(20);
            paragraph.setIndentationRight(20);
            paragraph.setAlignment(Element.ALIGN_CENTER);

            tableh.setSpacingBefore(8);
            paragraph.add(tableh);
            document.add(paragraph);
            // 1-paragraf tugashi

            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return file;


    }
}
