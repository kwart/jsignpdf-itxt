package com.lowagie.text.awt;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public class Color implements Paint {

	int value;
	transient private PaintContext theContext;

	public final static Color gray = new Color(128, 128, 128);
	public final static Color white = new Color(255, 255, 255);
	public final static Color black = new Color(0, 0, 0);

	private static final double FACTOR = 0.7;

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, int a) {
		value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		testColorValueRange(r, g, b, a);
	}

	private static void testColorValueRange(int r, int g, int b, int a) {
		boolean rangeError = false;
		String badComponentString = "";

		if (a < 0 || a > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Alpha";
		}
		if (r < 0 || r > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Red";
		}
		if (g < 0 || g > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Green";
		}
		if (b < 0 || b > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Blue";
		}
		if (rangeError == true) {
			throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponentString);
		}
	}

	public int getRed() {
		return (getRGB() >> 16) & 0xFF;
	}

	public int getGreen() {
		return (getRGB() >> 8) & 0xFF;
	}

	public int getBlue() {
		return (getRGB() >> 0) & 0xFF;
	}

	public int getRGB() {
		return value;
	}

	public Color(float r, float g, float b) {
		this((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5));
		testColorValueRange(r, g, b, 1.0f);
	}

	private static void testColorValueRange(float r, float g, float b, float a) {
		boolean rangeError = false;
		String badComponentString = "";
		if (a < 0.0 || a > 1.0) {
			rangeError = true;
			badComponentString = badComponentString + " Alpha";
		}
		if (r < 0.0 || r > 1.0) {
			rangeError = true;
			badComponentString = badComponentString + " Red";
		}
		if (g < 0.0 || g > 1.0) {
			rangeError = true;
			badComponentString = badComponentString + " Green";
		}
		if (b < 0.0 || b > 1.0) {
			rangeError = true;
			badComponentString = badComponentString + " Blue";
		}
		if (rangeError == true) {
			throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponentString);
		}
	}

	public Color darker() {
		return new Color(Math.max((int) (getRed() * FACTOR), 0), Math.max((int) (getGreen() * FACTOR), 0), Math.max(
				(int) (getBlue() * FACTOR), 0));
	}

	public int getAlpha() {
		return (getRGB() >> 24) & 0xff;
	}

	public int getTransparency() {
		int alpha = getAlpha();
		if (alpha == 0xff) {
			return Transparency.OPAQUE;
		} else if (alpha == 0) {
			return Transparency.BITMASK;
		} else {
			return Transparency.TRANSLUCENT;
		}
	}

	public synchronized PaintContext createContext(ColorModel cm, Rectangle r, Rectangle2D r2d, AffineTransform xform,
			RenderingHints hints) {
		PaintContext pc = theContext;
		if (pc == null || ((ColorPaintContext) pc).color != getRGB()) {
			pc = new ColorPaintContext(getRGB(), cm);
			theContext = pc;
		}
		return pc;
	}
}
