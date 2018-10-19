package com.vailter.image.kit;

import com.vailter.image.type.ImageType;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

public class Base64Test {
    @Test
    public void testConvertImg() {
        try {
            File file = new File(Base64Test.class.getResource("/1.jpg").toURI());
            ImageKit kit = ImageKit.read(file);
            ImageType imageType = kit.getFormat();
            System.out.println(imageType);
            System.out.println(imageType.getB64Prefix());
            String imgBase64Url = kit.toBase64();
            System.out.println(imageType.getB64Prefix() + imgBase64Url);

            ImageKit kit1 = ImageKit.read(imgBase64Url);
            kit1.getFormat();
            kit1.transferTo("D:\\test.jpg");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
