package pers.ktl.util.zxing;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.google.zxing.WriterException;

class ZxingFactoryTest {

	@Test
	void test() throws IOException, WriterException {
		ZxingFactory zxingFactory = new ZxingFactory("https://www.baidu.com");
		zxingFactory.writeToFile("C:\\Users\\KOUTIANLONG\\Desktop\\logo.png");
	}

}
