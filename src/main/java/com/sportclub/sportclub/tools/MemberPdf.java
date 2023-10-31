package com.sportclub.sportclub.tools;

import com.lowagie.text.Font;
import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Member;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.sportclub.sportclub.service.GymService;
import jakarta.servlet.http.HttpServletResponse;
public class MemberPdf {
    private List<Member> listUsers;

    public MemberPdf(List<Member> listUsers) {
        this.listUsers = listUsers;

    }




    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.gray);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase(" ID", font));

        table.addCell(cell);

        cell.setPhrase(new Phrase("Prenom", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nom", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("tele", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Address", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Abonnement", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        for (Member user : listUsers) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getName());
            table.addCell(user.getLname());
            table.addCell(user.getEmail());
            table.addCell(String.valueOf(user.getTele()));
            table.addCell(user.getAdress());
            table.addCell(user.getAbonnement().getNameAb());


        }
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A3);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.gray);

        Paragraph p = new Paragraph("List des adhérents", font);

        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.0f, 3.0f, 3.0f, 3.5f,3.0f,3.0f, 3.0f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        document.close();

    }
}
