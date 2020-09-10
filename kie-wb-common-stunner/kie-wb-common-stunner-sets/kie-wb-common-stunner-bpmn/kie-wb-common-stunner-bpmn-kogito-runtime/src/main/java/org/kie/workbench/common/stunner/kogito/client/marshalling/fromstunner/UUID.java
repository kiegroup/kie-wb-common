/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.kogito.client.marshalling.fromstunner;

import java.util.Random;

/**
 * forked from org.eclipse.emf.ecore.util.EcoreUtil.UUID
 */
final class UUID {
    private static final char[] BASE64_DIGITS = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};
    private static final long EPOCH_ADJUSTMENT = 0L;
    private static long lastTime = System.currentTimeMillis() + 0L;
    private static short clockSequence;
    private static short timeAdjustment;
    private static int sleepTime = 1;
    private static final byte[] uuid = new byte[16];
    private static final char[] buffer = new char[23];

    public static synchronized String generate() {
        updateCurrentTime();

        for(int i = 0; i < 5; ++i) {
            buffer[4 * i + 1] = BASE64_DIGITS[uuid[i * 3] >> 2 & 63];
            buffer[4 * i + 2] = BASE64_DIGITS[uuid[i * 3] << 4 & 48 | uuid[i * 3 + 1] >> 4 & 15];
            buffer[4 * i + 3] = BASE64_DIGITS[uuid[i * 3 + 1] << 2 & 60 | uuid[i * 3 + 2] >> 6 & 3];
            buffer[4 * i + 4] = BASE64_DIGITS[uuid[i * 3 + 2] & 63];
        }

        buffer[21] = BASE64_DIGITS[uuid[15] >> 2 & 63];
        buffer[22] = BASE64_DIGITS[uuid[15] << 4 & 48];
        return new String(buffer);
    }

    private UUID() {
    }

    private static void updateClockSequence() {
        uuid[8] = (byte)(clockSequence >> 8 & 63 | 128);
        uuid[9] = (byte)(clockSequence & 255);
    }

    private static void updateCurrentTime() {
        long currentTime = System.currentTimeMillis() + 0L;
        int i;
        if (lastTime > currentTime) {
            ++clockSequence;
            if (16384 == clockSequence) {
                clockSequence = 0;
            }

            updateClockSequence();
        } else if (lastTime == currentTime) {
            ++timeAdjustment;
            if (timeAdjustment > 9999) {
                for(i = 0; i < 10000 * sleepTime; ++i) {
                }

                timeAdjustment = 0;

                for(currentTime = System.currentTimeMillis() + 0L; lastTime == currentTime; currentTime = System.currentTimeMillis() + 0L) {
                    ++sleepTime;

                    for(i = 0; i < 10000; ++i) {
                    }
                }
            }
        } else {
            timeAdjustment = 0;
        }

        lastTime = currentTime;
        currentTime *= 10000L;
        currentTime += (long)timeAdjustment;
        currentTime |= 1152921504606846976L;

        for(i = 0; i < 4; ++i) {
            uuid[i] = (byte)((int)(currentTime >> 8 * (3 - i) & 255L));
        }

        for(i = 0; i < 2; ++i) {
            uuid[i + 4] = (byte)((int)(currentTime >> 8 * (1 - i) + 32 & 255L));
        }

        for(i = 0; i < 2; ++i) {
            uuid[i + 6] = (byte)((int)(currentTime >> 8 * (1 - i) + 48 & 255L));
        }

    }

    static {
        Random random = new Random();
        clockSequence = (short)random.nextInt(16384);
        updateClockSequence();
        byte[] nodeAddress = new byte[6];
        random.nextBytes(nodeAddress);
        nodeAddress[0] |= -128;

        for(int i = 0; i < 6; ++i) {
            uuid[i + 10] = nodeAddress[i];
        }

        buffer[0] = '_';
    }
}
