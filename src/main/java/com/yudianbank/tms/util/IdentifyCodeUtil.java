package com.yudianbank.tms.util;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 生成随机验证码工具类
 *
 * @author Song Lea
 */
public class IdentifyCodeUtil {

    private static final String BASE_CODE_CHAR = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefhijklmnpqrstuvwxyz2345678";
    private int width = 160; // 图片的宽度
    private int height = 40; // 图片的高度
    private int codeCount = 4;  // 验证码字符个数
    private int lineCount = 20;  // 验证码干扰线数
    private String code = null;  // 验证码
    private BufferedImage buffImg = null; // Image对象
    private Random random = new Random();

    public IdentifyCodeUtil() {
        createImage();
    }

    public IdentifyCodeUtil(int width, int height) {
        this.width = width;
        this.height = height;
        createImage();
    }

    public IdentifyCodeUtil(int width, int height, int codeCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        createImage();
    }

    public IdentifyCodeUtil(int width, int height, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        createImage();
    }

    // 生成验证码图片的实现
    private void createImage() {
        // 字体的宽度与高度
        int fontWidth = width / codeCount;
        int fontHeight = height - 5;
        int codeY = height - 8;
        // 生成Graphics2D对象
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = buffImg.createGraphics();
        // 设置背景色
        graphics.setColor(getRandomColor(200, 250));
        graphics.fillRect(0, 0, width, height);
        // 设置字体
        Font font = getRandomFont(fontHeight);
        graphics.setFont(font);
        // 设置干扰线
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            graphics.setColor(getRandomColor(1, 255));
            graphics.drawLine(xs, ys, xe, ye);
        }
        // 添加噪点
        int area = (int) (0.01f * width * height);
        for (int i = 0; i < area; i++) {
            buffImg.setRGB(random.nextInt(width), random.nextInt(height), random.nextInt(255));
        }
        // 得到随机字符
        this.code = getRandomStr(codeCount);
        for (int i = 0; i < codeCount; i++) {
            // 为字符设置随机的颜色
            graphics.setColor(getRandomColor(1, 255));
            // strRand为要画出来的东西,x和y表示要画的东西最左侧字符的基线位于此图形上下文坐标系的(x, y)位置处
            graphics.drawString(this.code.substring(i, i + 1), i * fontWidth + 3, codeY);
            // 旋转指定角度
            graphics.rotate(Math.toRadians(random.nextInt(5)), width / 2, height / 2);
        }
        // 释放资源
        graphics.dispose();
    }

    // 得到随机字符
    private String getRandomStr(int n) {
        StringBuilder str = new StringBuilder();
        int len = BASE_CODE_CHAR.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            str.append(BASE_CODE_CHAR.charAt((int) r));
        }
        return str.toString();
    }

    // 得到随机颜色
    private Color getRandomColor(int fc, int bc) {
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    // 产生随机字体
    private Font getRandomFont(int size) {
        Random random = new Random();
        Font font[] = new Font[4];
        font[0] = new Font("Ravie", Font.PLAIN, size);
        font[1] = new Font("Consolas", Font.PLAIN, size);
        font[2] = new Font("Fixedsys", Font.PLAIN, size);
        font[3] = new Font("Wide Latin", Font.PLAIN, size);
        return font[random.nextInt(4)];
    }

    // 写验证码到输出流
    public void write(OutputStream outputStream) throws IOException {
        try {
            ImageIO.write(buffImg, "png", outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    // 返回验证码
    public String getCode() {
        return code.toLowerCase();
    }
}
