package net.scrumplex.sprummlbot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class InteractiveBanner {

    private final int[] usersPos;
    private final Color color;
    private final int fontSize;
    private int[] timePos;
    private int[] datePos;
    private BufferedImage bufferedImage;

    InteractiveBanner(File image, int[] timePos, int[] datePos, int[] usersPos, Color color, int fontSize) throws IOException {
        this.bufferedImage = ImageIO.read(image);
        this.timePos = timePos;
        this.datePos = datePos;
        this.usersPos = usersPos;
        this.color = color;
        this.fontSize = fontSize;
        this.timePos[1] += this.fontSize;
        this.datePos[1] += this.fontSize;
        this.usersPos[1] += this.fontSize;
    }

    public byte[] getNewImageAsBytes() throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(getImage(), "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    public File writeToFile(File file) throws IOException, InterruptedException {
        if (!file.exists())
            file.createNewFile();
        ImageIO.write(getImage(), "png", file);
        return file;
    }

    private BufferedImage getImage() throws InterruptedException, IOException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat date = new SimpleDateFormat("d.M.Y");
        BufferedImage copy = deepCopy(bufferedImage);
        Graphics g = copy.getGraphics();
        g.setFont(g.getFont().deriveFont(fontSize));
        g.setColor(color);
        g.drawString(time.format(cal.getTime()), timePos[0], timePos[1]);
        g.drawString(date.format(cal.getTime()), datePos[0], datePos[1]);
        g.drawString(Vars.API.getServerInfo().get().getClientsOnline() + "/" + Vars.API.getServerInfo().get().getMaxClients() + " Users Online",
                usersPos[0], usersPos[1]);
        g.dispose();
        return copy;
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
