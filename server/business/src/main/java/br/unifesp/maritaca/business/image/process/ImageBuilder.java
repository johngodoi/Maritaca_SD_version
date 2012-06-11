package br.unifesp.maritaca.business.image.process;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import br.unifesp.maritaca.business.parser.Base64Parser;

public class ImageBuilder {
	private Base64Parser conversor;
	
	public String createImageFromBase64Code(String base64Code)
	{
		String path="../../resources/img/image.jpg";
		byte byteArray[]=conversor.decodeBase64CodeToByteArray(base64Code);
		try {
            final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));
            ImageIO.write(bufferedImage, "jpg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
		return path;
	}
}
