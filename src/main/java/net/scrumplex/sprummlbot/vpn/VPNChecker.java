package net.scrumplex.sprummlbot.vpn;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.Startup;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class VPNChecker {

    private final String ip;
    private final String uid;

    private VPNChecker(String ip, String uid) {
        this.ip = ip;
        this.uid = uid;
    }

    public VPNChecker(Client c) {
        this(c.getIp(), c.getUniqueIdentifier());
    }

    public VPNChecker(ClientInfo c) {
        this(c.getIp(), c.getUniqueIdentifier());
    }

    public boolean isBlocked() {
        if (!Vars.VPNCHECKER_ENABLED)
            return false;

        if (uid != null)
            if (PermissionGroup.getPermissionGroupByName(Vars.PERMGROUPASSIGNMENTS.get("vpn")).isClientInGroup(uid))
                return false;

        if (Startup.vpnConfig.isBlocked(ip))
            return true;

        try {
            Socket sock = new Socket("api.sprum.ml", 44543);
            sock.setSoTimeout(3000);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out.writeBytes("<vpnchk>" + ip + "</vpnchk>\n");
            String response = in.readLine();
            sock.close();
            if (response == null)
                return false;
            if (response.equalsIgnoreCase("ip_is_vpn")) {
                Startup.vpnConfig.add(ip);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
