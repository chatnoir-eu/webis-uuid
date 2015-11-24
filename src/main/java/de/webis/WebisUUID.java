/*
 * Webis UUID generator.
 * Copyright (C) 2015 Janek Bevendorff <janek.bevendorff@uni-weimar.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.webis;

import org.apache.commons.codec.binary.Hex;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    public String generateUUID(final String internalId)
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
    public static String generateUUID(final String prefix, final String internalId)
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

            return strBuilder.toString();
        } catch (NoSuchAlgorithmException ignored) {
            return null;
        }
    }

    /**
     * Command line interface for generating UUIDs.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args)
    {
        if (2 != args.length) {
            System.err.println("ERROR: Missing arguments!");
            System.err.println("Usage: webis-uuid.jar PREFIX INTERNAL_ID");
            System.exit(1);
        }

        System.out.println(generateUUID(args[0], args[1]));
    }
}
