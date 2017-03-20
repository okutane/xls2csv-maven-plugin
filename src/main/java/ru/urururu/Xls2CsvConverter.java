package ru.urururu;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
class Xls2CsvConverter implements HSSFListener {
    private static final String XLS = ".xls";
    private static final String CSV = ".csv";
    private POIFSFileSystem fs;
    private PrintStream output;
    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;

    private Xls2CsvConverter(String inputFilename, PrintStream output) throws IOException {
        this.fs = new POIFSFileSystem(new FileInputStream(inputFilename));
        this.output = output;
    }

    static void convert(Log log, File targetDir) throws IOException {
        File[] files = targetDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(XLS);
            }
        });

        for (File srcFile : files) {
            log.info(srcFile.getName());
            File targetFile = new File(srcFile.getAbsolutePath().replace(XLS, CSV));

            try (PrintStream out = new PrintStream(targetFile, "UTF-8")) {
                new Xls2CsvConverter(srcFile.getAbsolutePath(), out).process();
            }
        }
    }

    private void process() throws IOException {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        this.formatListener = new FormatTrackingHSSFListener(listener) {
            public String getFormatString(CellValueRecordInterface cell) {
                return "General";
            }
        };
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(this.formatListener);
        factory.processWorkbookEvents(request, this.fs);
    }

    public void processRecord(Record record) {
        int thisColumn = -1;
        String thisStr = null;
        switch (record.getSid()) {
            case SSTRecord.sid:
                this.sstRecord = (SSTRecord) record;
                break;
            case LabelSSTRecord.sid:
                LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
                thisColumn = labelSSTRecord.getColumn();
                thisStr = StringEscapeUtils.escapeCsv(this.sstRecord.getString(labelSSTRecord.getSSTIndex()).toString());
                break;
            case BlankRecord.sid:
                BlankRecord blankRecord = (BlankRecord) record;
                thisColumn = blankRecord.getColumn();
                thisStr = "";
                break;
            case NumberRecord.sid:
                NumberRecord numberRecord = (NumberRecord) record;
                thisColumn = numberRecord.getColumn();
                thisStr = this.formatListener.formatNumberDateCell(numberRecord);
        }

        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            thisColumn = mc.getColumn();
            thisStr = "";
        }

        if (thisStr != null) {
            if (thisColumn > 0) {
                this.output.print(',');
            }

            this.output.print(thisStr);
        }

        if (record instanceof LastCellOfRowDummyRecord) {
            this.output.println();
        }
    }
}
