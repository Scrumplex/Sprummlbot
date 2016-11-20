package net.scrumplex.sprummlbot.tools;

public class Validate {

    public static class Network {
        public static boolean port(int port) {
            return port > 0 && port <= 65535;
        }
    }
}
