/*
 * Copyright (c) 2002-2012, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console;

public class WCWidth {

    public static int wcwidth(CharSequence cs) {
        return wcwidth(cs, 0, cs.length());
    }

    public static int wcwidth(CharSequence cs, int start, int end) {
        int nb = 0;
        for (int i = start; i < end; i++) {
            nb += WCWidth.wcwidth((int) cs.charAt(i));
        }
        return nb;
    }

    /* The following two functions define the column width of an ISO 10646
     * character as follows:
     *
     *    - The null character (U+0000) has a column width of 0.
     *
     *    - Other C0/C1 control characters and DEL will lead to a return
     *      value of -1.
     *
     *    - Non-spacing and enclosing combining characters (general
     *      category code Mn or Me in the Unicode database) have a
     *      column width of 0.
     *
     *    - SOFT HYPHEN (U+00AD) has a column width of 1.
     *
     *    - Other format characters (general category code Cf in the Unicode
     *      database) and ZERO WIDTH SPACE (U+200B) have a column width of 0.
     *
     *    - Hangul Jamo medial vowels and final consonants (U+1160-U+11FF)
     *      have a column width of 0.
     *
     *    - Spacing characters in the East Asian Wide (W) or East Asian
     *      Full-width (F) category as defined in Unicode Technical
     *      Report #11 have a column width of 2.
     *
     *    - All remaining characters (including all printable
     *      ISO 8859-1 and WGL4 characters, Unicode control characters,
     *      etc.) have a column width of 1.
     *
     * This implementation assumes that wchar_t characters are encoded
     * in ISO 10646.
     */
    public static int wcwidth(int ucs)
    {

        /* test for 8-bit control characters */
        if (ucs == 0)
            return 0;
        if (ucs < 32 || (ucs >= 0x7f && ucs < 0xa0))
            return -1;

        /* binary search in table of non-spacing characters */
        if (bisearch(ucs, combining, combining.length - 1))
            return 0;

        /* if we arrive here, ucs is not a combining or C0/C1 control character */
        return 1 +
                ((ucs >= 0x1100 &&
                        (ucs <= 0x115f ||                           /* Hangul Jamo init. consonants */
                                ucs == 0x2329 || ucs == 0x232a ||
                                (ucs >= 0x2e80 && ucs <= 0xa4cf &&
                                        ucs != 0x303f) ||           /* CJK ... Yi */
                                (ucs >= 0xac00 && ucs <= 0xd7a3) || /* Hangul Syllables */
                                (ucs >= 0xf900 && ucs <= 0xfaff) || /* CJK Compatibility Ideographs */
                                (ucs >= 0xfe10 && ucs <= 0xfe19) || /* Vertical forms */
                                (ucs >= 0xfe30 && ucs <= 0xfe6f) || /* CJK Compatibility Forms */
                                (ucs >= 0xff00 && ucs <= 0xff60) || /* Fullwidth Forms */
                                (ucs >= 0xffe0 && ucs <= 0xffe6) ||
                                (ucs >= 0x20000 && ucs <= 0x2fffd) ||
                                (ucs >= 0x30000 && ucs <= 0x3fffd))) ? 1 : 0);
    }

    /* sorted list of non-overlapping intervals of non-spacing characters */
    /* generated by "uniset +cat=Me +cat=Mn +cat=Cf -00AD +1160-11FF +200B c" */
    static Interval[] combining = {
            new Interval( 0x0300, 0x036F ), new Interval( 0x0483, 0x0486 ), new Interval( 0x0488, 0x0489 ),
            new Interval( 0x0591, 0x05BD ), new Interval( 0x05BF, 0x05BF ), new Interval( 0x05C1, 0x05C2 ),
            new Interval( 0x05C4, 0x05C5 ), new Interval( 0x05C7, 0x05C7 ), new Interval( 0x0600, 0x0603 ),
            new Interval( 0x0610, 0x0615 ), new Interval( 0x064B, 0x065E ), new Interval( 0x0670, 0x0670 ),
            new Interval( 0x06D6, 0x06E4 ), new Interval( 0x06E7, 0x06E8 ), new Interval( 0x06EA, 0x06ED ),
            new Interval( 0x070F, 0x070F ), new Interval( 0x0711, 0x0711 ), new Interval( 0x0730, 0x074A ),
            new Interval( 0x07A6, 0x07B0 ), new Interval( 0x07EB, 0x07F3 ), new Interval( 0x0901, 0x0902 ),
            new Interval( 0x093C, 0x093C ), new Interval( 0x0941, 0x0948 ), new Interval( 0x094D, 0x094D ),
            new Interval( 0x0951, 0x0954 ), new Interval( 0x0962, 0x0963 ), new Interval( 0x0981, 0x0981 ),
            new Interval( 0x09BC, 0x09BC ), new Interval( 0x09C1, 0x09C4 ), new Interval( 0x09CD, 0x09CD ),
            new Interval( 0x09E2, 0x09E3 ), new Interval( 0x0A01, 0x0A02 ), new Interval( 0x0A3C, 0x0A3C ),
            new Interval( 0x0A41, 0x0A42 ), new Interval( 0x0A47, 0x0A48 ), new Interval( 0x0A4B, 0x0A4D ),
            new Interval( 0x0A70, 0x0A71 ), new Interval( 0x0A81, 0x0A82 ), new Interval( 0x0ABC, 0x0ABC ),
            new Interval( 0x0AC1, 0x0AC5 ), new Interval( 0x0AC7, 0x0AC8 ), new Interval( 0x0ACD, 0x0ACD ),
            new Interval( 0x0AE2, 0x0AE3 ), new Interval( 0x0B01, 0x0B01 ), new Interval( 0x0B3C, 0x0B3C ),
            new Interval( 0x0B3F, 0x0B3F ), new Interval( 0x0B41, 0x0B43 ), new Interval( 0x0B4D, 0x0B4D ),
            new Interval( 0x0B56, 0x0B56 ), new Interval( 0x0B82, 0x0B82 ), new Interval( 0x0BC0, 0x0BC0 ),
            new Interval( 0x0BCD, 0x0BCD ), new Interval( 0x0C3E, 0x0C40 ), new Interval( 0x0C46, 0x0C48 ),
            new Interval( 0x0C4A, 0x0C4D ), new Interval( 0x0C55, 0x0C56 ), new Interval( 0x0CBC, 0x0CBC ),
            new Interval( 0x0CBF, 0x0CBF ), new Interval( 0x0CC6, 0x0CC6 ), new Interval( 0x0CCC, 0x0CCD ),
            new Interval( 0x0CE2, 0x0CE3 ), new Interval( 0x0D41, 0x0D43 ), new Interval( 0x0D4D, 0x0D4D ),
            new Interval( 0x0DCA, 0x0DCA ), new Interval( 0x0DD2, 0x0DD4 ), new Interval( 0x0DD6, 0x0DD6 ),
            new Interval( 0x0E31, 0x0E31 ), new Interval( 0x0E34, 0x0E3A ), new Interval( 0x0E47, 0x0E4E ),
            new Interval( 0x0EB1, 0x0EB1 ), new Interval( 0x0EB4, 0x0EB9 ), new Interval( 0x0EBB, 0x0EBC ),
            new Interval( 0x0EC8, 0x0ECD ), new Interval( 0x0F18, 0x0F19 ), new Interval( 0x0F35, 0x0F35 ),
            new Interval( 0x0F37, 0x0F37 ), new Interval( 0x0F39, 0x0F39 ), new Interval( 0x0F71, 0x0F7E ),
            new Interval( 0x0F80, 0x0F84 ), new Interval( 0x0F86, 0x0F87 ), new Interval( 0x0F90, 0x0F97 ),
            new Interval( 0x0F99, 0x0FBC ), new Interval( 0x0FC6, 0x0FC6 ), new Interval( 0x102D, 0x1030 ),
            new Interval( 0x1032, 0x1032 ), new Interval( 0x1036, 0x1037 ), new Interval( 0x1039, 0x1039 ),
            new Interval( 0x1058, 0x1059 ), new Interval( 0x1160, 0x11FF ), new Interval( 0x135F, 0x135F ),
            new Interval( 0x1712, 0x1714 ), new Interval( 0x1732, 0x1734 ), new Interval( 0x1752, 0x1753 ),
            new Interval( 0x1772, 0x1773 ), new Interval( 0x17B4, 0x17B5 ), new Interval( 0x17B7, 0x17BD ),
            new Interval( 0x17C6, 0x17C6 ), new Interval( 0x17C9, 0x17D3 ), new Interval( 0x17DD, 0x17DD ),
            new Interval( 0x180B, 0x180D ), new Interval( 0x18A9, 0x18A9 ), new Interval( 0x1920, 0x1922 ),
            new Interval( 0x1927, 0x1928 ), new Interval( 0x1932, 0x1932 ), new Interval( 0x1939, 0x193B ),
            new Interval( 0x1A17, 0x1A18 ), new Interval( 0x1B00, 0x1B03 ), new Interval( 0x1B34, 0x1B34 ),
            new Interval( 0x1B36, 0x1B3A ), new Interval( 0x1B3C, 0x1B3C ), new Interval( 0x1B42, 0x1B42 ),
            new Interval( 0x1B6B, 0x1B73 ), new Interval( 0x1DC0, 0x1DCA ), new Interval( 0x1DFE, 0x1DFF ),
            new Interval( 0x200B, 0x200F ), new Interval( 0x202A, 0x202E ), new Interval( 0x2060, 0x2063 ),
            new Interval( 0x206A, 0x206F ), new Interval( 0x20D0, 0x20EF ), new Interval( 0x302A, 0x302F ),
            new Interval( 0x3099, 0x309A ), new Interval( 0xA806, 0xA806 ), new Interval( 0xA80B, 0xA80B ),
            new Interval( 0xA825, 0xA826 ), new Interval( 0xFB1E, 0xFB1E ), new Interval( 0xFE00, 0xFE0F ),
            new Interval( 0xFE20, 0xFE23 ), new Interval( 0xFEFF, 0xFEFF ), new Interval( 0xFFF9, 0xFFFB ),
            new Interval( 0x10A01, 0x10A03 ), new Interval( 0x10A05, 0x10A06 ), new Interval( 0x10A0C, 0x10A0F ),
            new Interval( 0x10A38, 0x10A3A ), new Interval( 0x10A3F, 0x10A3F ), new Interval( 0x1D167, 0x1D169 ),
            new Interval( 0x1D173, 0x1D182 ), new Interval( 0x1D185, 0x1D18B ), new Interval( 0x1D1AA, 0x1D1AD ),
            new Interval( 0x1D242, 0x1D244 ), new Interval( 0xE0001, 0xE0001 ), new Interval( 0xE0020, 0xE007F ),
            new Interval( 0xE0100, 0xE01EF )
    };

    private static class Interval {
        public final int first;
        public final int last;

        public Interval(int first, int last) {
            this.first = first;
            this.last = last;
        }
    }

    /* auxiliary function for binary search in interval table */
    private static boolean bisearch(int ucs, Interval[] table, int max) {
        int min = 0;
        int mid;

        if (ucs < table[0].first || ucs > table[max].last)
            return false;
        while (max >= min) {
            mid = (min + max) / 2;
            if (ucs > table[mid].last)
                min = mid + 1;
            else if (ucs < table[mid].first)
                max = mid - 1;
            else
                return true;
        }

        return false;
    }


}
