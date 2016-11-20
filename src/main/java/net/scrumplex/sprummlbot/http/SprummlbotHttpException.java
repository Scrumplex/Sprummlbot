package net.scrumplex.sprummlbot.http;

public class SprummlbotHttpException extends Exception {

    public SprummlbotHttpException(String message) {
        super(message);
    }

    public SprummlbotHttpException(Exception e) {
        super(e);
    }

}
