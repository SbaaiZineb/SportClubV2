package com.sportclub.sportclub.tools;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sportclub.sportclub.entities.Member;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
public class MemberPdf {
    private List<Member> listUsers;

    public MemberPdf(List<Member> listUsers) {
        this.listUsers = listUsers;

    }

    private void writeTableHeader(PdfPTable table) {

        table.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100);
        table.getDefaultCell().setPadding(5);

        Font font = new Font(Font.HELVETICA, 12, Font.BOLD, Color.GRAY);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setBorderColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);


        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Prénom", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nom", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Téléphone", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Adresse", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Abonnement", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        for (Member user : listUsers) {
            table.addCell(user.getId().toString());
            table.addCell(user.getName());
            table.addCell(user.getLname());
            table.addCell(user.getEmail());
            table.addCell(String.valueOf(user.getTele()));
            table.addCell(user.getAdress());
          if (user.getCurrentAbonnement()!=null){
                table.addCell(user.getCurrentAbonnement().getNameAb());

            }else {
                table.addCell("");

            }
            }
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = new Font(Font.HELVETICA, 16, Font.BOLD, Color.GRAY);
        Paragraph title = new Paragraph("Liste des adhérents", font);
        title.setAlignment(Element.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1.0f, 2.5f, 2.5f, 3.0f, 2.0f, 2.5f, 3.5f });
        table.setSpacingBefore(20);

        writeTableHeader(table);
        writeTableData(table);



        document.add(title);
        document.add(table);

        document.close();
    }
}
