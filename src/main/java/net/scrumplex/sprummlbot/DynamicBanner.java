package net.scrumplex.sprummlbot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DynamicBanner {

    private final int[] usersPos;
    private final Color color;
    private final Font font;
    private int[] timePos;
    private int[] datePos;
    private BufferedImage bufferedImage;

    DynamicBanner(File image, Color color, Font font) throws IOException {
        this.bufferedImage = ImageIO.read(image);
        this.timePos = Vars.DYNBANNER_TIME_POS;
        this.datePos = Vars.DYNBANNER_DATE_POS;
        this.usersPos = Vars.DYNBANNER_USERS_POS;
        this.color = color;
        this.font = font;
        this.timePos[1] += this.font.getSize();
        this.datePos[1] += this.font.getSize();
        this.usersPos[1] += this.font.getSize();
    }

    public byte[] getNewImageAsBytes() throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(getImage(), "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    private BufferedImage getImage() throws InterruptedException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat date = new SimpleDateFormat("d.M.Y");
        BufferedImage copy = deepCopy(bufferedImage);
        Graphics2D g = copy.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(color);
        g.drawString(time.format(cal.getTime()), timePos[0], timePos[1]);
        g.drawString(date.format(cal.getTime()), datePos[0], datePos[1]);
        g.drawString(Vars.API.getServerInfo().get().getClientsOnline() + "/" + Vars.API.getServerInfo().get().getMaxClients() + " Users Online",
                usersPos[0], usersPos[1]);
        g.dispose();
        return copy;
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        return new BufferedImage(bi.getColorModel(), bi.copyData(null), bi.getColorModel().isAlphaPremultiplied(), null);
    }
}
