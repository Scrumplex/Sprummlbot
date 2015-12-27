package ga.codesplash.scrumplex.sprummlbot;

import ga.codesplash.scrumplex.sprummlbot.tools.EasyMethods;

import java.io.File;
import java.io.IOException;

class FileManager {

    static void createLicensesFile() throws IOException {
        File f = new File("licenses.txt");
        StringBuilder sb = new StringBuilder();
        sb.append("Open Source Licenses:\n");
        sb.append("Apache Commons Lang and Apache Commons IO: http://www.apache.org/licenses/LICENSE-2.0\n");
        sb.append("Teamspeak-3-Java-API: http://www.gnu.org/licenses/gpl-3.0.en.html\n");
        sb.append("JSON: http://www.json.org/license.html\n");
        EasyMethods.writeToFile(f, sb.toString());
    }

}
