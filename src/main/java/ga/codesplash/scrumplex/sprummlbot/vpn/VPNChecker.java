package ga.codesplash.scrumplex.sprummlbot.vpn;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import ga.codesplash.scrumplex.sprummlbot.Main;
import ga.codesplash.scrumplex.sprummlbot.Vars;

import javax.xml.bind.DatatypeConverter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class VPNChecker {

    private String ip;
    private String uid;
    private String type = "not detected";

    /**
     * This class will chack for vpns with several methods.
     *
     * @param ip  IP which will be checked
     * @param uid Unique ID of the Client which will be checked.
     */
    public VPNChecker(String ip, String uid) {
        this.ip = ip;
        this.uid = uid;
    }

    /**
     * This class will chack for vpns with several methods.
     *
     * @param c Client which will be checked
     */
    public VPNChecker(Client c) throws InterruptedException {
        this(Vars.API.getClientInfo(c.getId()).get());
    }

    /**
     * This class will chack for vpns with several methods.
     *
     * @param c Client's Info which will be checked
     */
    public VPNChecker(ClientInfo c) {
        this(c.getIp(), c.getUniqueIdentifier());
    }

    /**
     * @return Returns if an IP is a VPN or not.
     */
    public boolean isBlocked() {
        if (Vars.VPNCHECKER_WL.contains(uid))
            return false;

        if (Main.vpnConfig.get().contains(ip)) {
            type = "blacklisted";
            return true;
        }

        return isRDNSInvalid() || isCyberGhost() || isDefaultOpenVPN() || isFSecFreedome() || isPPTP();
    }

    /**
     * Checkis if Reverse DNS is the same as IP
     * @return
     */
    private boolean isRDNSInvalid() {
        if(getRDNS().equalsIgnoreCase(ip)) {
            type = "invalid RDNS";
        }
        return getRDNS().equalsIgnoreCase(ip);
    }
    /**
     * This will make an udp request on port 1194
     *
     * @return Returns if request was successful.
     */
    private boolean isDefaultOpenVPN() {
        boolean vpn = sendUDP(1194, DatatypeConverter.parseHexBinary("380100000000000000"));
        if (vpn)
            type = "defovpn";
        return vpn;
    }

    /**
     * This will make an udp request on port 2744 (Default F-Secure Freedome port)
     *
     * @return Returns if request was successful.
     */
    private boolean isFSecFreedome() {
        boolean vpn = sendUDP(2744, DatatypeConverter.parseHexBinary("380100000000000000"));
        if (vpn)
            type = "ovpn";
        return vpn;
    }

    /**
     * This will mlook if hostname of ip contains cg-dialup
     *
     * @return Returns if request was successful.
     */
    private boolean isCyberGhost() {
        boolean vpn = getRDNS().contains("cg-dialup");
        if (vpn) {
            type = "cyberghost";
            if (Vars.VPNCHECKER_SAVE)
                Main.vpnConfig.add(ip);
        }
        return vpn;
    }

    private boolean isPPTP() {
        boolean vpn = sendTCP(1723, "hi m8");
        if (vpn)
            type = "pptp";
        return vpn;
    }

    private String getRDNS() {
        try {
            return InetAddress.getByName(ip).getCanonicalHostName();
        } catch (UnknownHostException ignored) {
        }
        return "";
    }

    private boolean sendTCP(int port, String packet) {
        try {
            Socket sock = new Socket(ip, port);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            out.writeBytes(packet);
            if (Vars.VPNCHECKER_SAVE)
                Main.vpnConfig.add(ip);
            return true;
        } catch (IOException ignored) {
        }

        return false;
    }

    private boolean sendUDP(int port, byte[] packet) {
        try {
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket pack = new DatagramPacket(packet, packet.length, InetAddress.getByName(ip), port);
            ds.setSoTimeout(2000);
            ds.send(pack);
            byte[] by = new byte[ds.getReceiveBufferSize()];
            pack = new DatagramPacket(by, by.length);
            ds.receive(pack);
            ds.close();
            if (Vars.VPNCHECKER_SAVE)
                Main.vpnConfig.add(ip);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public String getType() {
        return type;
    }
}
