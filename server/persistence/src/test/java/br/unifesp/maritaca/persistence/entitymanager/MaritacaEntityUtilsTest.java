package br.unifesp.maritaca.persistence.entitymanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import br.unifesp.maritaca.persistence.util.MaritacaEntityUtils;

import com.google.common.io.Files;

public class MaritacaEntityUtilsTest {
	
	private String image = getImageXmlForTest();

	@Test
	public void testCompress() throws IOException {			
		String compressedImage = MaritacaEntityUtils.compress(image);		
		
		Assert.assertTrue(compressedImage.length() < image.length());
	}

	@Test
	public void testUncompress() throws IOException, InterruptedException {
		String compressedImage = MaritacaEntityUtils.compress(image);
		String decompressedImage = MaritacaEntityUtils.decompress(compressedImage);

		Assert.assertEquals(image, decompressedImage);
	}

	private static String getImageXmlForTest() {
		try {
			File source = new File("src/test/resources/imagem.xml");
			String string = Files.toString(source, Charset.defaultCharset());
			return string.trim();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
