package org.broad.igv.blat;

import org.broad.igv.feature.PSLRecord;
import org.broad.igv.feature.Strand;

import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.util.List;

/**
 * Table model for Blat results (psl records)
 * <p/>
 * tChr tStart tEnd  strand score match misMatch repMatcch Ns qGapCount qGapBases tGapCount tGapBases
 *
 * @author jrobinso
 *         Date: 11/29/12
 *         Time: 6:46 PM
 */
public class BlatTableModel extends AbstractTableModel {


    String[] columnNames = {"chr", "start", "end", "strand", "score", "match", "mis-match", "rep. match", "N's",
            "Q gap count", "Q gap bases", "T gap count", "T gap bases"};

    List<PSLRecord> records;


    public BlatTableModel(List<PSLRecord> records) {
        this.records = records;
    }

    @Override
    public int getRowCount() {
        return records.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        PSLRecord record = records.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return record.getChr();
            case 1:
                return record.getStart();
            case 2:
                return record.getEnd();
            case 3:
                return (record.getStrand() == Strand.POSITIVE ? "+" : "-");
            case 4:
                return (int) record.getScore();
            case 5:
                return record.getMatch();
            case 6:
                return record.getMisMatch();
            case 7:
                return record.getRepMatch();
            case 8:
                return record.getNs();
            case 9:
                return record.getQGapCount();
            case 10:
                return record.getQGapBases();
            case 11:
                return record.getTGapCount();
            case 12:
                return record.getTGapBases();
            default:
                return "?";
        }

    }

    public String getChr(int rowIndex) {
        PSLRecord record = records.get(rowIndex);
        return record.getChr();
    }

    public int getStart(int rowIndex) {
        PSLRecord record = records.get(rowIndex);
        return record.getStart();
    }

    public int getEnd(int rowIndex) {
        PSLRecord record = records.get(rowIndex);
        return record.getEnd();
    }

    public void save(File f) throws IOException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            for (PSLRecord record : records) {
                pw.println(record.getText());
            }
        } finally {
            if (pw != null) pw.close();
        }

    }
}
