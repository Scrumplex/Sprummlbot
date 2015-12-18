package ga.codesplash.scrumplex.sprummlbot;

import ga.codesplash.scrumplex.sprummlbot.tools.EasyMethods;

import java.io.File;
import java.io.IOException;

/**
 * Created by Sefa on 18.12.2015.
 */
public class FileManager {

    public void createLicensesFile() throws IOException {
        File f = new File("licenses.txt");
        StringBuilder sb = new StringBuilder();
        sb.append("Open Source Licenses:\n");
        sb.append("Apache License V2: http://www.apache.org/licenses/LICENSE-2.0\n");
        sb.append("GPL V3: http://www.gnu.org/licenses/gpl-3.0.en.html\n");
        EasyMethods.writeToFile(f, sb.toString());
    }

}
