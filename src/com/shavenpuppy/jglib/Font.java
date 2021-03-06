/*
 * Copyright (c) 2003-onwards Shaven Puppy Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'Shaven Puppy' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.shavenpuppy.jglib;

import java.io.*;

import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

/**
 * A Font. Has some global characteristics, and a number of Glyphs, and a mapping of characters to Glyphs.
 * A Font can be constructed from various sources.
 */
public final class Font implements Serializable {

	static final long serialVersionUID = 4L;

	/** Handy bounds */
	private static final Rectangle tempBounds = new Rectangle();
	private static final Point tempPoint = new Point();

	/** The font's name */
	private String name;

	/** The font's style */
	private boolean bold, italic;

	/** Font's ascent */
	private int ascent;

	/** Font's descent */
	private int descent;

	/** Font's leading */
	private int leading;

	/** Size */
	private int size;

	/** The font's glyphs */
	private Glyph[] glyph;

	/** Maps Unicode characters to glyphs */
	private int[] map;

	/** Lastly an Image of all the glyphs in the font */
	private Image image;

	/**
	 * Public constructor for Font for serialization.
	 */
	public Font() {
	}

	/**
	 * Construct a font
	 */
	public Font(String name, boolean bold, boolean italic, Image image, Glyph[] glyph, int size, int ascent, int descent, int leading, int[] map) {

		this.name = name;
		this.bold = bold;
		this.italic = italic;
		this.image = image;
		this.size = size;
		this.ascent = ascent;
		this.descent = descent;
		this.leading = leading;
		this.map = map;
		this.glyph = glyph;

	}

	public static Font importSerialised(String filename) throws Exception
	{
		InputStream fIn = Font.class.getClassLoader().getResourceAsStream(filename);
		BufferedInputStream bIn = new BufferedInputStream(fIn);
		ObjectInputStream oIn = new ObjectInputStream(bIn);

		return (Font)oIn.readObject();
	}

	/**
	 * Gets the ascent.
	 * @return Returns a int
	 */
	public int getAscent() {
		return ascent;
	}

	/**
	 * Gets the descent.
	 * @return Returns a int
	 */
	public int getDescent() {
		return descent;
	}

	/**
	 * Gets a glyph.
	 * @param i The glyph index
	 * @return a Glyph
	 */
	public Glyph getGlyph(int i) {
		return glyph[i];
	}

	/**
	 * @return the number of glyphs
	 */
	public int getNumGlyphs() {
		return glyph.length;
	}

	/**
	 * Gets the image.
	 * @return Returns a SpriteImage
	 */
	public Image getImage() {
		assert image != null : "Font image has been disposed.";
		return image;
	}

	/**
	 * Gets the leading.
	 * @return Returns a int
	 */
	public int getLeading() {
		return leading;
	}

	/**
	 * Gets the size.
	 * @return Returns a int
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Map a character to a glyph
	 */
	public Glyph map(char c) {
		if (c >= map.length) {
			c = 0;
		}
		if (c == '\t') {
			c = ' ';
		}
		int m = map[c];
		assert m != -1 : "char "+((int)c)+" is not valid";
		if (m >= glyph.length) {
			m = 0;
		}
		return glyph[m];
	}

	/**
	 * Calculate the bounding box of a string if it was drawn at (0,0)
	 */
	public Rectangle getStringBounds(String text, int start, int end, Rectangle dest) {
		if (dest == null) {
			dest = new Rectangle();
		} else {
			dest.setBounds(0,0,0,0);
		}

		Glyph last = null;
		int penX = 0;
		for (int i = start; i < end; i ++) {
			Glyph next = map(text.charAt(i));
			next.getBounds(tempBounds);
			next.getBearing(tempPoint);
			tempBounds.setLocation(tempPoint.getX() + penX - next.getKerningAfter(last), tempPoint.getY());
			tempBounds.setWidth(Math.max(tempBounds.getWidth(), next.getAdvance()));
			dest.add(tempBounds);
			penX += next.getAdvance() - next.getKerningAfter(last);
			last = next;
		}

		return dest;
	}

	/**
	 * @return the font's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return true if this is a bold font
	 */
	public boolean isBold() {
		return bold;
	}

	/**
	 * @return true if this is an italic font
	 */
	public boolean isItalic() {
		return italic;
	}

	/**
	 * @return true if this font is not bold or italic
	 */
	public boolean isPlain() {
		return !(bold || italic);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Font [name=");
		buffer.append(name);
		buffer.append(", bold=");
		buffer.append(bold);
		buffer.append(", italic=");
		buffer.append(italic);
		buffer.append(", ascent=");
		buffer.append(ascent);
		buffer.append(", descent=");
		buffer.append(descent);
		buffer.append(", leading=");
		buffer.append(leading);
		buffer.append(", size=");
		buffer.append(size);
		buffer.append(", image=");
		buffer.append(image);
		buffer.append("]");
		return buffer.toString();
	}

}
