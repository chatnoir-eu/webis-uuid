/*
 * Webis UUID Generator.
 * Copyright (C) 2015-2017 Janek Bevendorff, Webis Group
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package de.webis;

import org.apache.commons.codec.binary.Hex;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Generator for version 5 (name-based SHA1) UUIDs to identify records in generated web corpus MapFiles.
 * UUIDs are generated within NameSpace_URL as defined by RFC 4122.
 * The name part consists of a given scheme prefix (e.g. clueweb09) followed by a colon and the internal
 * (globally non-unique) record ID (e.g. clueweb09-en0001-02-21241).
 *
 * @author Janek Bevendorff <janek.bevendorff@uni-weimar.de>
 */
public class WebisUUID
{
    // RFC 4122 defines 6ba7b811-9dad-11d1-80b4-00c04fd430c8
    private static final byte[] UUID_NAMESPACE_URL = { 107, -89, -72, 17, -99, -83, 17, -47, -128, -76, 0, -64, 79, -44, 48, -56 };

    /**
     * Fixed prefix for usage with non-static member methods.
     */
    private final String mPrefix;

    /**
     * If you are generating several UUIDs with the same prefix you may consider
     * creating a generator instance with that prefix for convenience reasons
     * instead of using the static generator method.
     *
     * @param prefix UUID prefix
     */
    public WebisUUID(final String prefix)
    {
        mPrefix = prefix;
    }

    /**
     * Generate a version 5 UUID.
     * The hashed name part is prefix:internalId where prefix has been
     * defined during object instantiation.
     *
     * @param internalId internal ID (scheme-specific part)
     * @return generated version 5 UUID
     */
    public UUID generateUUID(final String internalId)
    {
        return generateUUID(mPrefix, internalId);
    }

    /**
     * Generate a version 5 UUID.
     * The hashed name part is prefix:internalId.
     *
     * @param prefix the scheme prefix
     * @param internalId internal ID (scheme-specific part)
     * @return generated version 5 UUID
     */
    public static UUID generateUUID(final String prefix, final String internalId)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(UUID_NAMESPACE_URL);
            md.update((prefix + ":" + internalId).getBytes());
            final byte[] digest = md.digest();
            final byte[] shortened = new byte[16];
            System.arraycopy(digest, 0, shortened, 0, 16);

            // set version
            shortened[6] &= 0x0f;
            shortened[6] |= 0x50;

            // set variant
            shortened[8] &= 0x3f;
            shortened[8] |= 0x80;

            // format string
            final String encodedHex = Hex.encodeHexString(shortened);
            final StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < encodedHex.length(); ++i) {
                if (8 == i || 12 == i || 16 == i || 20 == i) {
                    strBuilder.append("-");
                }
                strBuilder.append(encodedHex.charAt(i));
            }

            return UUID.fromString(strBuilder.toString());
        } catch (NoSuchAlgorithmException ignored) {
            // should never happen
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
    }

    /**
     * Command line interface for generating UUIDs.
     *
     * @param args command line arguments.
     */
    public static void main(final String[] args)
    {
        if (2 != args.length) {
            System.err.println("ERROR: Missing arguments!");
            System.err.println("Usage: webis-uuid.jar PREFIX INTERNAL_ID");
            System.exit(1);
        }

        System.out.println(generateUUID(args[0], args[1]).toString());
    }
}
