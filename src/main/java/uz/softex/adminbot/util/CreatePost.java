package uz.softex.adminbot.util;

import uz.softex.adminbot.model.Job;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CreatePost {

    public static void generateJob(Job job){
        BufferedImage img = null;
        BufferedImage imgDraw = null;
        File f = null;
        File d = null;
        // Read image
        try
        {
            f = new File("post/post.jpg");
            //d = new File("pic/"+job.getPicUrl());
            img = ImageIO.read(f);
            //imgDraw = ImageIO.read(d);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-10));
        affineTransform.translate(80,300);


        BufferedImage temp = new BufferedImage(img.getWidth(),
                img.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = (Graphics2D) temp.getGraphics();
        graphics2D.drawImage(img,null,null);
        //graphics2D.drawImage(imgDraw.getScaledInstance(850,600,Image.SCALE_SMOOTH),affineTransform,null);
        graphics2D.setFont(new Font("Century Gothic", Font.BOLD, 70));
        graphics2D.setColor(Color.WHITE);

        String watermark = job.getPosition();
        System.out.println(watermark);

        graphics2D.drawString(watermark, 70,320);

        f = new File("pic/"+job.getPicUrl());

        try
        {
            ImageIO.write(temp, "jpg", f);
        }
        catch (IOException e)
        {
                System.out.println(e);
        }
    }


}
