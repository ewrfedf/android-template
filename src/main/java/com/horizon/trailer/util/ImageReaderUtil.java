package com.horizon.trailer.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageReaderUtil {
	public static byte[] getImgByte(String imgPath) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Options opts = new Options();
		// 调整图片尺寸到 1080*1080
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, opts);
		opts.inSampleSize = ImageReaderUtil.computeSampleSize(opts, -1,
				1080 * 1080);
		opts.inJustDecodeBounds = false;

		byte[] myByteArray = null;
		Bitmap saveBitmap = BitmapFactory.decodeFile(imgPath, opts);
		if (saveBitmap != null) {
			saveBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			myByteArray = baos.toByteArray();
		}
		return myByteArray;
	}

	public static Bitmap getImageFromAssetsFile1(Context context,
			String fileName) {
		Bitmap image = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];// 读取
		int len = 0;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			while ((len = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, len);// 写入
			}
			byte[] result = outputStream.toByteArray();// 声明字节数组
			image = BitmapFactory.decodeByteArray(result, 0, result.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static byte[] getImageByteFromAssetsFile1(Context context,
			String fileName) {
		byte[] result = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];// 读取
		int len = 0;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			while ((len = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, len);// 写入
			}
			result = outputStream.toByteArray();// 声明字节数组
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		// 保证是方形，并且从中心画
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int w;
		int deltaX = 0;
		int deltaY = 0;
		if (width <= height) {
			w = width;
			deltaY = height - w;
		} else {
			w = height;
			deltaX = width - w;
		}
		final Rect rect = new Rect(deltaX, deltaY, w, w);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		// 圆形，所有只用一个

		int radius = (int) (Math.sqrt(w * w * 2.0d) / 2);
		canvas.drawRoundRect(rectF, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w1 = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		if (w1 > h) {
			h = w1;
		} else {
			w1 = h;
		}

		// 取 drawable 的颜色格式
		Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
				: Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w1, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas1 = new Canvas(bitmap);
		drawable.setBounds(0, 0, w1, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas1);

		// bitmap.getWidth()
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		// 保证是方形，并且从中心画
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int w;
		int deltaX = 0;
		int deltaY = 0;
		if (width <= height) {
			w = width;
			deltaY = height - w;
		} else {
			w = height;
			deltaX = width - w;
		}
		System.out
				.println("rect  " + deltaX + " " + deltaY + " " + w + " " + w);
		final Rect rect = new Rect(deltaX, deltaY, w, w);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		// 圆形，所有只用一个

		int radius = (int) (Math.sqrt(w * w * 2.0d) / 2);
		canvas.drawRoundRect(rectF, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static int computeSampleSize(Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

}
