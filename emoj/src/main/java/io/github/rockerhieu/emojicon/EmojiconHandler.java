/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rockerhieu.emojicon;

import android.content.Context;
import android.text.Spannable;
import android.util.SparseIntArray;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public final class EmojiconHandler {
    private EmojiconHandler() {
    }

    private static final SparseIntArray sEmojisMap = new SparseIntArray(125);
    private static final SparseIntArray sSoftbanksMap = new SparseIntArray(0);
    private static final SparseIntArray sEmojiModifiersMap = new SparseIntArray(5);
    private static Map<String, Integer> sEmojisModifiedMap = new HashMap<>();

    static {
        sEmojiModifiersMap.put(0x1f3fb, 1);
        sEmojiModifiersMap.put(0x1f3fc, 1);
        sEmojiModifiersMap.put(0x1f3fd, 1);
        sEmojiModifiersMap.put(0x1f3fe, 1);
        sEmojiModifiersMap.put(0x1f3ff, 1);
    }

    static {
        // People

        sEmojisMap.put(0x1f60a, R.drawable.emoji_1f60a);
        sEmojisMap.put(0x1f61e, R.drawable.emoji_1f61e);
        sEmojisMap.put(0x1f603, R.drawable.emoji_1f603);
        sEmojisMap.put(0x1f609, R.drawable.emoji_1f609);
        sEmojisMap.put(0x1f61c, R.drawable.emoji_1f61c);
        sEmojisMap.put(0x1f618, R.drawable.emoji_1f618);
        sEmojisMap.put(0x1f621, R.drawable.emoji_1f621);
        sEmojisMap.put(0x1f625, R.drawable.emoji_1f625);
        sEmojisMap.put(0x1f629, R.drawable.emoji_1f629);
        sEmojisMap.put(0x1f62d, R.drawable.emoji_1f62d);
        sEmojisMap.put(0x1f631, R.drawable.emoji_1f631);
        sEmojisMap.put(0x1f601, R.drawable.emoji_1f601);
        sEmojisMap.put(0x1f602, R.drawable.emoji_1f602);
        sEmojisMap.put(0x1f612, R.drawable.emoji_1f612);
        sEmojisMap.put(0x1f614, R.drawable.emoji_1f614);
        sEmojisMap.put(0x1f60d, R.drawable.emoji_1f60d);

    }

    private static boolean isSoftBankEmoji(char c) {
        return ((c >> 12) == 0xe);
    }

    private static int getEmojiResource(Context context, int codePoint) {
        return sEmojisMap.get(codePoint);
    }

    private static int getSoftbankEmojiResource(char c) {
        return sSoftbanksMap.get(c);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param emojiAlignment
     * @param textSize
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize) {
        addEmojis(context, text, emojiSize, emojiAlignment, textSize, 0, -1, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param emojiAlignment
     * @param textSize
     * @param index
     * @param length
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize, int index, int length) {
        addEmojis(context, text, emojiSize, emojiAlignment, textSize, index, length, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param emojiAlignment
     * @param textSize
     * @param useSystemDefault
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize, boolean useSystemDefault) {
        addEmojis(context, text, emojiSize, emojiAlignment, textSize, 0, -1, useSystemDefault);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param emojiAlignment
     * @param textSize
     * @param index
     * @param length
     * @param useSystemDefault
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int emojiAlignment, int textSize, int index, int length, boolean useSystemDefault) {
        if (useSystemDefault || text == null) {
            return;
        }

        int textLength = text.length();
        int textLengthToProcessMax = textLength - index;
        int textLengthToProcess = length < 0 || length >= textLengthToProcessMax ? textLength : (length + index);

        // remove spans throughout all text
        EmojiconSpan[] oldSpans = text.getSpans(0, textLength, EmojiconSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            text.removeSpan(oldSpans[i]);
        }

        int skip;
        for (int i = index; i < textLengthToProcess; i += skip) {
            skip = 0;
            int icon = 0;
            char c = text.charAt(i);
            if (isSoftBankEmoji(c)) {
                icon = getSoftbankEmojiResource(c);
                skip = icon == 0 ? 0 : 1;
            }

            if (icon == 0) {
                int unicode = Character.codePointAt(text, i);
                skip = Character.charCount(unicode);

                if (unicode > 0xff) {
                    icon = getEmojiResource(context, unicode);
                }

                if (i + skip < textLengthToProcess) {
                    int followUnicode = Character.codePointAt(text, i + skip);
                    //Non-spacing mark (Combining mark)
                    if (followUnicode == 0xfe0f) {
                        int followSkip = Character.charCount(followUnicode);
                        if (i + skip + followSkip < textLengthToProcess) {

                            int nextFollowUnicode = Character.codePointAt(text, i + skip + followSkip);
                            if (nextFollowUnicode == 0x20e3) {
                                int nextFollowSkip = Character.charCount(nextFollowUnicode);
                                int tempIcon = getKeyCapEmoji(unicode);

                                if (tempIcon == 0) {
                                    followSkip = 0;
                                    nextFollowSkip = 0;
                                } else {
                                    icon = tempIcon;
                                }
                                skip += (followSkip + nextFollowSkip);
                            }
                        }
                    } else if (followUnicode == 0x20e3) {
                        //some older versions of iOS don't use a combining character, instead it just goes straight to the second part
                        int followSkip = Character.charCount(followUnicode);

                        int tempIcon = getKeyCapEmoji(unicode);
                        if (tempIcon == 0) {
                            followSkip = 0;
                        } else {
                            icon = tempIcon;
                        }
                        skip += followSkip;

                    } else if (sEmojiModifiersMap.get(followUnicode, 0) > 0) {
                        //handle other emoji modifiers
                        int followSkip = Character.charCount(followUnicode);


                        //TODO seems like we could do this for every emoji type rather than having that giant static map, maybe this is too slow?
                        String hexUnicode = Integer.toHexString(unicode);
                        String hexFollowUnicode = Integer.toHexString(followUnicode);

                        String resourceName = "emoji_" + hexUnicode + "_" + hexFollowUnicode;

                        int resourceId = 0;
                        if (sEmojisModifiedMap.containsKey(resourceName)) {
                            resourceId = sEmojisModifiedMap.get(resourceName);
                        } else {
                            resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getApplicationContext().getPackageName());
                            if (resourceId != 0) {
                                sEmojisModifiedMap.put(resourceName, resourceId);
                            }
                        }

                        if (resourceId == 0) {
                            followSkip = 0;
                        } else {
                            icon = resourceId;
                        }
                        skip += followSkip;
                    }
                }
            }

            if (icon > 0) {
                text.setSpan(new EmojiconSpan(context, icon, emojiSize, emojiAlignment, textSize), i, i + skip, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static int getKeyCapEmoji(int unicode) {
        int icon = 0;
        switch (unicode) {/*
            case 0x0023:
                icon = R.drawable.emoji_0023;
                break;
            case 0x002a:
                icon = R.drawable.emoji_002a_20e3;
                break;
            case 0x0030:
                icon = R.drawable.emoji_0030;
                break;
            case 0x0031:
                icon = R.drawable.emoji_0031;
                break;
            case 0x0032:
                icon = R.drawable.emoji_0032;
                break;
            case 0x0033:
                icon = R.drawable.emoji_0033;
                break;
            case 0x0034:
                icon = R.drawable.emoji_0034;
                break;
            case 0x0035:
                icon = R.drawable.emoji_0035;
                break;
            case 0x0036:
                icon = R.drawable.emoji_0036;
                break;
            case 0x0037:
                icon = R.drawable.emoji_0037;
                break;
            case 0x0038:
                icon = R.drawable.emoji_0038;
                break;
            case 0x0039:
                icon = R.drawable.emoji_0039;
                break;
            default:
                break;*/
        }
        return icon;
    }

}
