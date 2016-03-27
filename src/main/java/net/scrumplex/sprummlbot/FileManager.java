package net.scrumplex.sprummlbot;

import net.scrumplex.sprummlbot.tools.EasyMethods;

import java.io.File;
import java.io.IOException;

class FileManager {

    static void createLicensesFile() throws IOException {
        File f = new File("licenses.txt");
        String sb = "Open Source Licenses:\n" +
                "Apache Commons Lang and Apache Commons Codec: http://www.apache.org/licenses/LICENSE-2.0\n" +
                "Teamspeak-3-Java-API: http://www.gnu.org/licenses/gpl-3.0.en.html\n" +
                "JSON: http://www.json.org/license.html\n";
        EasyMethods.writeToFile(f, sb);
    }

}
