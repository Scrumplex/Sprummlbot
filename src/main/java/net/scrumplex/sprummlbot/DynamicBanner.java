package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.VirtualServerInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class DynamicBanner {

    private final int[] usersPos;
    private final Color color;
    private final Font font;
    private int[] timePos;
    private int[] datePos;
    private BufferedImage bufferedImage;
    private SimpleDateFormat time;
    private SimpleDateFormat date;

    DynamicBanner(File image, Color color, Font font) throws IOException {
        this.bufferedImage = ImageIO.read(image);
        this.timePos = Vars.DYNBANNER_TIME_POS;
        this.datePos = Vars.DYNBANNER_DATE_POS;
        this.usersPos = Vars.DYNBANNER_USERS_POS;
        this.time = new SimpleDateFormat(Vars.DYNBANNER_TIME_F);
        this.date = new SimpleDateFormat(Vars.DYNBANNER_DATE_F);
        this.color = color;
        this.font = font;
    }

    byte[] getNewImageAsBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(getImage(), "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    private BufferedImage getImage() {
        Calendar cal = Calendar.getInstance();
        BufferedImage copy = deepCopy(bufferedImage);
        Graphics2D g = copy.createGraphics();
        VirtualServerInfo info = Sprummlbot.getSprummlbot().getSyncAPI().getServerInfo();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(color);
        g.drawString(time.format(cal.getTime()), timePos[0], timePos[1]);
        g.drawString(date.format(cal.getTime()), datePos[0], datePos[1]);
        g.drawString(Vars.DYNBANNER_USERS_F.replace("%users%", String.valueOf(info.getClientsOnline())).replace("%max%", String.valueOf(info.getMaxClients())),
                usersPos[0], usersPos[1]);
        g.dispose();
        return copy;
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        return new BufferedImage(bi.getColorModel(), bi.copyData(null), bi.getColorModel().isAlphaPremultiplied(), null);
    }
}
