package pers.ktl.util.zxing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author KOUTIANLONG -日期：2020.02.09 -功能：二维码制作，解析 -参考：
 *         [1]https://www.jianshu.com/p/7ae3b7002530
 *         [2]https://blog.csdn.net/qq_32193151/article/details/73650491
 * 
 */

public class ZxingFactory {

	// 定义默认二维码边长
	private int qrCodeSide = 430;

	// 定义默认内容字符集编码
	private String contextEncode = "UTF-8";

	// 二维码内容
	private String content = "";

	// 二维码图片格式
	private String qrCodeFormat = "jpg";

	// LOGO路径
	private String logoUrl = null;

	public ZxingFactory() {

	}

	public ZxingFactory(String content) {

		this.content = content;
	}

	public ZxingFactory(String content, String qrCodeFormat) {

		this.content = content;
		this.qrCodeFormat = qrCodeFormat;
	}

	public ZxingFactory(String content, String qrCodeFormat, String logoUrl) {

		this.content = content;
		this.qrCodeFormat = qrCodeFormat;
		this.logoUrl = qrCodeFormat;
	}

	public ZxingFactory(String content, String qrCodeFormat, String logoUrl, int qrCodeSide) {

		this.content = content;
		this.qrCodeFormat = qrCodeFormat;
		this.logoUrl = qrCodeFormat;
		this.qrCodeSide = qrCodeSide;
	}

	public int getQrCodeSide() {
		return qrCodeSide;
	}

	public void setQrCodeSide(int qrCodeSide) {
		this.qrCodeSide = qrCodeSide;
	}

	public String getContextEncode() {
		return contextEncode;
	}

	public void setContextEncode(String contextEncode) {
		this.contextEncode = contextEncode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getQrCodeFormat() {
		return qrCodeFormat;
	}

	public void setQrCodeFormat(String qrCodeFormat) {
		this.qrCodeFormat = qrCodeFormat;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	// 创建二维码
	private BitMatrix creatQrCode() throws WriterException {

		// 设置生成二维码参数
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 设置内容字符集编码
		hints.put(EncodeHintType.CHARACTER_SET, this.contextEncode);
		hints.put(EncodeHintType.MARGIN, 0);// 设置二维码边的宽度(非负数)
		// 生成二维码

		return new MultiFormatWriter().encode(this.content, BarcodeFormat.QR_CODE, this.qrCodeSide, this.qrCodeSide,
				hints);
	}

	public BufferedImage getLogoQrCode() throws IOException, WriterException {

		// 读取LOGO文件
		BufferedImage logodImage = ImageIO.read(new File(this.logoUrl));

		// 将 matrixImage 转换为:TYPE_3BYTE_BGR
		BufferedImage newQrCode = new BufferedImage(this.qrCodeSide, this.qrCodeSide, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2 = newQrCode.createGraphics();
		BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(this.creatQrCode());
		g2.drawImage(qrCodeImage, 0, 0, this.qrCodeSide, this.qrCodeSide, null);
		qrCodeImage.flush();

		// 绘制图片
		int logoXY = this.qrCodeSide / 5 * 2;
		int logoSide = this.qrCodeSide / 5;
		g2.drawImage(logodImage, logoXY, logoXY, logoSide, logoSide, null);
		// 绘制边框
		BasicStroke stroke = new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		// 设置笔画对象
		g2.setStroke(stroke);
		// 指定弧度的圆角矩形
		RoundRectangle2D.Float round = new RoundRectangle2D.Float(logoXY, logoXY, logoSide, logoSide, 20, 20);
		g2.setColor(Color.white);
		// 绘制圆弧矩形
		g2.draw(round);

		// 设置LOGO(含灰色边框）
		BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		// 设置笔画对象
		g2.setStroke(stroke2);
		RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(logoXY + 2, logoXY + 2, this.qrCodeSide / 5 - 4,
				logoSide - 4, 20, 20);
		g2.setColor(new Color(128, 128, 128));
		g2.draw(round2);// 绘制圆弧矩形
		g2.dispose();
		newQrCode.flush();

		return newQrCode;
	}

	// 通过文件解析二维码
	public Result decodeQrcode(String filePath, String character) throws IOException, NotFoundException {
		BufferedImage image = ImageIO.read(new File(filePath));
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		Map<DecodeHintType, Object> hints = new LinkedHashMap<DecodeHintType, Object>();
		// 解码设置编码方式为：utf-8，
		// hints.put(DecodeHintType.CHARACTER_SET, character);
		// 优化精度
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		// 复杂模式，开启PURE_BARCODE模式
		hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
		return new MultiFormatReader().decode(bitmap, hints);
	}

	// 通过图片对象解析二维码
	public Result decodeQrcode(BufferedImage image, String character) throws NotFoundException {

		BufferedImageLuminanceSource bSource = new BufferedImageLuminanceSource(image);
		HybridBinarizer hBinarizer = new HybridBinarizer(bSource);
		BinaryBitmap binaryBitmap = new BinaryBitmap(hBinarizer);

		// 定义二维码的参数:
		Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, character);// 定义字符集
		hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

		return new MultiFormatReader().decode(binaryBitmap, hints);
	}

	// 通过输入流解析二维码
	public Result decodeQrcode(InputStream input, String character) throws IOException, NotFoundException {

		BufferedImage image = ImageIO.read(input);
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		Map<DecodeHintType, Object> hints = new LinkedHashMap<DecodeHintType, Object>();
		// 解码设置编码方式
		// hints.put(DecodeHintType.CHARACTER_SET, character);
		// 优化精度
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		// 复杂模式，开启PURE_BARCODE模式
		hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
		return new MultiFormatReader().decode(bitmap, hints);
	}

	// qrCode写入文件中
	public void writeToFile(String outFileUri) throws IOException, WriterException {

		if (this.logoUrl != null) {

			ImageIO.write(this.getLogoQrCode(), this.qrCodeFormat, new File(outFileUri));
		} else {
			MatrixToImageWriter.writeToPath(this.creatQrCode(), this.qrCodeFormat, new File(outFileUri).toPath());
		}
	}

	// qrCode写入流中
	public void writeToStream(OutputStream outStream) throws IOException, WriterException {

		if (this.logoUrl != null) {

			ImageIO.write(this.getLogoQrCode(), this.qrCodeFormat, outStream);
		} else {

			MatrixToImageWriter.writeToStream(this.creatQrCode(), this.qrCodeFormat, outStream);
		}
	}

}
