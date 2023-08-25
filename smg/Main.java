package smg;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Main {
    
    private static UI mainframe;
    public static void main(String[] args) throws InvalidFormatException, IOException {
        UI.ui_main(file -> onProceed(file), Main::setFrame);
    }

    public static void setFrame(UI frame) {
        mainframe = frame;    
    }

    private static void onProceed(String filename) {
        try {
            SplitDocument sd = new SplitDocument(Paths.get(filename));
            sd.setLogger(s -> mainframe.log(s));
            sd.setProgressMarker(n -> mainframe.markProgress(n));
            sd.split();
        } catch (Exception e) {
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            
            mainframe.log("\n");
			mainframe.log("An error has occured.\n");
            mainframe.log("Please take note of the following details and report to the developers.\n");
            mainframe.log("Thank you for cooperating.\n");
            mainframe.log("\n");
			mainframe.log(String.format("Type: %s \n", e.getClass().getSimpleName()));
			mainframe.log(String.format("Message: %s \n", e.getMessage()));
			mainframe.log(String.format("Stack Trace: %s \n", sw.toString()));
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private static void emulateProgress(String name) {
        for (int i = 0; i < 100; i += 1 + (int) (Math.random() * 4)) {
            String msg = String.format("%d%% complete...\n", i);
            mainframe.log(msg);
            mainframe.markProgress(i);
            System.out.print(msg);
            sleep(1000);
        }
    }
}
