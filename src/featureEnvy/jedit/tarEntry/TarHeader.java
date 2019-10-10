/*
** Authored by Timothy Gerard Endres
** <mailto:time@gjt.org>  <http://www.trustice.com>
** 
** This work has been placed into the public domain.
** You may use this work in any way and for any purpose you wish.
**
** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
** REDISTRIBUTION OF THIS SOFTWARE. 
** 
*/

package featureEnvy.jedit.tarEntry;

import java.util.Date;

/**
 * This class encapsulates the Tar Entry Header used in Tar Archives.
 * The class also holds a number of tar constants, used mostly in headers.
 */

public class
TarHeader extends Object
	{
	/**
	 * The length of the name field in a header buffer.
	 */
	public static final int		NAMELEN = 100;
	/**
	 * The length of the mode field in a header buffer.
	 */
	public static final int		MODELEN = 8;
	/**
	 * The length of the user id field in a header buffer.
	 */
	public static final int		UIDLEN = 8;
	/**
	 * The length of the group id field in a header buffer.
	 */
	public static final int		GIDLEN = 8;
	/**
	 * The length of the checksum field in a header buffer.
	 */
	public static final int		CHKSUMLEN = 8;
	/**
	 * The length of the size field in a header buffer.
	 */
	public static final int		SIZELEN = 12;
	/**
	 * The length of the magic field in a header buffer.
	 */
	public static final int		MAGICLEN = 8;
	/**
	 * The length of the modification time field in a header buffer.
	 */
	public static final int		MODTIMELEN = 12;
	/**
	 * The length of the user name field in a header buffer.
	 */
	public static final int		UNAMELEN = 32;
	/**
	 * The length of the group name field in a header buffer.
	 */
	public static final int		GNAMELEN = 32;
	/**
	 * The length of the devices field in a header buffer.
	 */
	public static final int		DEVLEN = 8;

	/**
	 * LF_ constants represent the "link flag" of an entry, or more commonly,
	 * the "entry type". This is the "old way" of indicating a normal file.
	 */
	public static final byte	LF_OLDNORM	= 0;
	/**
	 * Normal file type.
	 */
	public static final byte	LF_NORMAL	= (byte) '0';
	/**
	 * Link file type.
	 */
	public static final byte	LF_LINK		= (byte) '1';
	/**
	 * Symbolic link file type.
	 */
	public static final byte	LF_SYMLINK	= (byte) '2';
	/**
	 * Character device file type.
	 */
	public static final byte	LF_CHR		= (byte) '3';
	/**
	 * Block device file type.
	 */
	public static final byte	LF_BLK		= (byte) '4';
	/**
	 * Directory file type.
	 */
	public static final byte	LF_DIR		= (byte) '5';
	/**
	 * FIFO (pipe) file type.
	 */
	public static final byte	LF_FIFO		= (byte) '6';
	/**
	 * Contiguous file type.
	 */
	public static final byte	LF_CONTIG	= (byte) '7';

	/**
	 * The magic tag representing a POSIX tar archive.
	 */
	public static final String	TMAGIC		= "ustar";

	/**
	 * The magic tag representing a GNU tar archive.
	 */
	public static final String	GNU_TMAGIC	= "ustar  ";

	/**
	 * The entry's name.
	 */
	public StringBuffer		name;
	/**
	 * The entry's permission mode.
	 */
	public int				mode;
	/**
	 * The entry's user id.
	 */
	public int				userId;
	/**
	 * The entry's group id.
	 */
	public int				groupId;
	/**
	 * The entry's size.
	 */
	public long				size;
	/**
	 * The entry's modification time.
	 */
	public long				modTime;
	/**
	 * The entry's checksum.
	 */
	public int				checkSum;
	/**
	 * The entry's link flag.
	 */
	public byte				linkFlag;
	/**
	 * The entry's link name.
	 */
	public StringBuffer		linkName;
	/**
	 * The entry's magic tag.
	 */
	public StringBuffer		magic;
	/**
	 * The entry's user name.
	 */
	public StringBuffer		userName;
	/**
	 * The entry's group name.
	 */
	public StringBuffer		groupName;
	/**
	 * The entry's major device number.
	 */
	public int				devMajor;
	/**
	 * The entry's minor device number.
	 */
	public int				devMinor;


	public
	TarHeader()
		{
		this.magic = new StringBuffer( TarHeader.TMAGIC );

		this.name = new StringBuffer();
		this.linkName = new StringBuffer();

		String user =
			System.getProperty( "user.name", "" );

		if ( user.length() > 31 )
			user = user.substring( 0, 31 );

		this.userId = 0;
		this.groupId = 0;
		this.userName = new StringBuffer( user );
		this.groupName = new StringBuffer( "" );
		}

	/**
	 * TarHeaders can be cloned.
	 */
	public Object
	clone()
		{
		TarHeader hdr = null;

		try {
			hdr = (TarHeader) super.clone();

			hdr.name =
				(this.name == null ) ? null
					: new StringBuffer( this.name.toString() );
			hdr.mode = this.mode;
			hdr.userId = this.userId;
			hdr.groupId = this.groupId;
			hdr.size = this.size;
			hdr.modTime = this.modTime;
			hdr.checkSum = this.checkSum;
			hdr.linkFlag = this.linkFlag;
			hdr.linkName =
				(this.linkName == null ) ? null
					: new StringBuffer( this.linkName.toString() );
			hdr.magic =
				(this.magic == null ) ? null
					: new StringBuffer( this.magic.toString() );
			hdr.userName =
				(this.userName == null ) ? null
					: new StringBuffer( this.userName.toString() );
			hdr.groupName =
				(this.groupName == null ) ? null
					: new StringBuffer( this.groupName.toString() );
			hdr.devMajor = this.devMajor;
			hdr.devMinor = this.devMinor;
			}
		catch ( CloneNotSupportedException ex )
			{
			ex.printStackTrace();
			}

		return hdr;
		}

	/**
	 * Get the name of this entry.
	 *
	 * @return Teh entry's name.
	 */
	public String
	getName()
		{
		return this.name.toString();
		}

	/**
	 * Parse an octal string from a header buffer. This is used for the
	 * file permission mode value.
	 *
	 * @param header The header buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The number of header bytes to parse.
	 * @return The long value of the octal string.
	 */
	public static long
	parseOctal( byte[] header, int offset, int length )
		throws InvalidHeaderException
		{
		long result = 0;
		boolean stillPadding = true;

		int end = offset + length;
		for ( int i = offset ; i < end ; ++i )
			{
			if ( header[i] == 0 )
				break;

			if ( header[i] == (byte) ' ' || header[i] == '0' )
				{
				if ( stillPadding )
					continue;

				if ( header[i] == (byte) ' ' )
					break;
				}
			
			stillPadding = false;

			result =
				(result << 3)
					+ (header[i] - '0');
			}

		return result;
		}

	/**
	 * Parse an entry name from a header buffer.
	 *
	 * @param header The header buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The number of header bytes to parse.
	 * @return The header's entry name.
	 */
	public static StringBuffer
	parseName( byte[] header, int offset, int length )
		throws InvalidHeaderException
		{
		StringBuffer result = new StringBuffer( length );

		int end = offset + length;
		for ( int i = offset ; i < end ; ++i )
			{
			if ( header[i] == 0 )
				break;
			result.append( (char)header[i] );
			}

		return result;
		}

	/**
	 * Determine the number of bytes in an entry name.
	 *
	 * @param header The header buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The number of header bytes to parse.
	 * @return The number of bytes in a header's entry name.
	 */
	public static int
	getNameBytes( StringBuffer name, byte[] buf, int offset, int length )
		{
		int i;

		for ( i = 0 ; i < length && i < name.length() ; ++i )
			{
			buf[ offset + i ] = (byte) name.charAt( i );
			}

		for ( ; i < length ; ++i )
			{
			buf[ offset + i ] = 0;
			}

		return offset + length;
		}

	/**
	 * Parse an octal integer from a header buffer.
	 *
	 * @param header The header buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The number of header bytes to parse.
	 * @return The integer value of the octal bytes.
	 */
	public static int
	getOctalBytes( long value, byte[] buf, int offset, int length )
		{
		byte[] result = new byte[ length ];

		int idx = length - 1;

		buf[ offset + idx ] = 0;
		--idx;
		buf[ offset + idx ] = (byte) ' ';
		--idx;

		if ( value == 0 )
			{
			buf[ offset + idx ] = (byte) '0';
			--idx;
			}
		else
			{
			for ( long val = value ; idx >= 0 && val > 0 ; --idx )
				{
				buf[ offset + idx ] = (byte)
					( (byte) '0' + (byte) (val & 7) );
				val = val >> 3;
				}
			}

		for ( ; idx >= 0 ; --idx )
			{
			buf[ offset + idx ] = (byte) ' ';
			}

		return offset + length;
		}

	/**
	 * Parse an octal long integer from a header buffer.
	 *
	 * @param header The header buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The number of header bytes to parse.
	 * @return The long value of the octal bytes.
	 */
	public static int
	getLongOctalBytes( long value, byte[] buf, int offset, int length )
		{
		byte[] temp = new byte[ length + 1 ];
		TarHeader.getOctalBytes( value, temp, 0, length + 1 );
		System.arraycopy( temp, 0, buf, offset, length );
		return offset + length;
		}

	/**
	 * Parse the checksum octal integer from a header buffer.
	 *
	 * @param header The header buffer from which to parse.
	 * @param offset The offset into the buffer from which to parse.
	 * @param length The number of header bytes to parse.
	 * @return The integer value of the entry's checksum.
	 */
	public static int
	getCheckSumOctalBytes( long value, byte[] buf, int offset, int length )
		{
		TarHeader.getOctalBytes( value, buf, offset, length );
		buf[ offset + length - 1 ] = (byte) ' ';
		buf[ offset + length - 2 ] = 0;
		return offset + length;
		}

	/**
	 * Parse an entry's TarHeader information from a header buffer.
	 * @param header  The tar entry header buffer to get information from.
	 */
	public void parse(byte[] header) throws InvalidHeaderException {
		int offset = 0;
		this.name = TarHeader.parseName(header, offset, TarHeader.NAMELEN);
		offset += TarHeader.NAMELEN;
		this.mode = (int) TarHeader.parseOctal(header, offset, TarHeader.MODELEN);
		offset += TarHeader.MODELEN;
		this.userId = (int) TarHeader.parseOctal(header, offset, TarHeader.UIDLEN);
		offset += TarHeader.UIDLEN;
		this.groupId = (int) TarHeader.parseOctal(header, offset, TarHeader.GIDLEN);
		offset += TarHeader.GIDLEN;
		this.size = TarHeader.parseOctal(header, offset, TarHeader.SIZELEN);
		offset += TarHeader.SIZELEN;
		this.modTime = TarHeader.parseOctal(header, offset, TarHeader.MODTIMELEN);
		offset += TarHeader.MODTIMELEN;
		this.checkSum = (int) TarHeader.parseOctal(header, offset, TarHeader.CHKSUMLEN);
		offset += TarHeader.CHKSUMLEN;
		this.linkFlag = header[offset++];
		this.linkName = TarHeader.parseName(header, offset, TarHeader.NAMELEN);
		offset += TarHeader.NAMELEN;
		this.magic = TarHeader.parseName(header, offset, TarHeader.MAGICLEN);
		offset += TarHeader.MAGICLEN;
		this.userName = TarHeader.parseName(header, offset, TarHeader.UNAMELEN);
		offset += TarHeader.UNAMELEN;
		this.groupName = TarHeader.parseName(header, offset, TarHeader.GNAMELEN);
		offset += TarHeader.GNAMELEN;
		this.devMajor = (int) TarHeader.parseOctal(header, offset, TarHeader.DEVLEN);
		offset += TarHeader.DEVLEN;
		this.devMinor = (int) TarHeader.parseOctal(header, offset, TarHeader.DEVLEN);
	}

	/**
	 * Fill in a TarHeader given only the entry's name.
	 * @param name  The tar entry name.
	 */
	public void name(String name) {
		boolean isDir = name.endsWith("/");
		this.checkSum = 0;
		this.devMajor = 0;
		this.devMinor = 0;
		this.name = new StringBuffer(name);
		this.mode = isDir ? 040755 : 0100644;
		this.userId = 0;
		this.groupId = 0;
		this.size = 0;
		this.checkSum = 0;
		this.modTime = (new java.util.Date()).getTime() / 1000;
		this.linkFlag = isDir ? TarHeader.LF_DIR : TarHeader.LF_NORMAL;
		this.linkName = new StringBuffer("");
		this.userName = new StringBuffer("");
		this.groupName = new StringBuffer("");
		this.devMajor = 0;
		this.devMinor = 0;
	}

	/**
	 * Write an entry's header information to a header buffer.
	 * @param outbuf  The tar entry header buffer to fill in.
	 */
	public void writeEntry(byte[] outbuf) {
		int offset = 0;
		offset = TarHeader.getNameBytes(this.name, outbuf, offset, TarHeader.NAMELEN);
		offset = TarHeader.getOctalBytes(this.mode, outbuf, offset, TarHeader.MODELEN);
		offset = TarHeader.getOctalBytes(this.userId, outbuf, offset, TarHeader.UIDLEN);
		offset = TarHeader.getOctalBytes(this.groupId, outbuf, offset, TarHeader.GIDLEN);
		long size = this.size;
		offset = TarHeader.getLongOctalBytes(size, outbuf, offset, TarHeader.SIZELEN);
		offset = TarHeader.getLongOctalBytes(this.modTime, outbuf, offset, TarHeader.MODTIMELEN);
		int csOffset = offset;
		for (int c = 0; c < TarHeader.CHKSUMLEN; ++c)
			outbuf[offset++] = (byte) ' ';
		outbuf[offset++] = this.linkFlag;
		offset = TarHeader.getNameBytes(this.linkName, outbuf, offset, TarHeader.NAMELEN);
		offset = TarHeader.getNameBytes(this.magic, outbuf, offset, TarHeader.MAGICLEN);
		offset = TarHeader.getNameBytes(this.userName, outbuf, offset, TarHeader.UNAMELEN);
		offset = TarHeader.getNameBytes(this.groupName, outbuf, offset, TarHeader.GNAMELEN);
		offset = TarHeader.getOctalBytes(this.devMajor, outbuf, offset, TarHeader.DEVLEN);
		offset = TarHeader.getOctalBytes(this.devMinor, outbuf, offset, TarHeader.DEVLEN);
		for (; offset < outbuf.length;)
			outbuf[offset++] = 0;
		long checkSum = this.computeCheckSum(outbuf);
		TarHeader.getCheckSumOctalBytes(checkSum, outbuf, csOffset, TarHeader.CHKSUMLEN);
	}

	/**
	 * Compute the checksum of a tar entry header.
	 * @param buf  The tar entry's header buffer.
	 * @return  The computed checksum.
	 */
	public long computeCheckSum(byte[] buf) {
		long sum = 0;
		for (int i = 0; i < buf.length; ++i) {
			sum += 255 & buf[i];
		}
		return sum;
	}

	}
 
