package com.sportclub.sportclub.tools;

import com.lowagie.text.Font;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;

public class CoachPdf {
    private List<Coach> listUsers;

    public CoachPdf(List<Coach> listUsers) {
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

        cell.setPhrase(new Phrase("Type de sport", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        for (Coach user : listUsers) {
            table.addCell(user.getId().toString());
            table.addCell(user.getName());
            table.addCell(user.getLname());
            table.addCell(user.getEmail());
            table.addCell(String.valueOf(user.getTele()));
            table.addCell(user.getAdress());
            table.addCell(user.getSportType());

        }
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = new Font(Font.HELVETICA, 16, Font.BOLD, Color.GRAY);
        Paragraph title = new Paragraph("Liste des entaineurs", font);
        title.setAlignment(Element.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.0f, 2.5f, 2.5f, 3.0f, 2.0f, 2.5f, 3.5f});
        table.setSpacingBefore(20);

        writeTableHeader(table);
        writeTableData(table);


        document.add(title);
        document.add(table);

        document.close();
    }
}
