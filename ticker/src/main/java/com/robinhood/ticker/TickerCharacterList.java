package com.robinhood.ticker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the primary class that Ticker uses to determine how to animate from one character
 * to another. The provided string dictates what characters will appear between
 * the start and end characters.
 *
 * <p>For example, given the string "abcde", if the view wants to animate from 'd' to 'b',
 * it will know that it has to go from 'd' to 'c' to 'b', and these are the characters
 * that show up during the animation scroll.
 */
public class TickerCharacterList {
    private final int numOriginalCharacters;
    // The saved character list will always be of the format: EMPTY, list, list
    private final char[] characterList;
    // A minor optimization so that we can cache the indices of each character.
    private final Map<Character, Integer> characterIndicesMap;

    public TickerCharacterList(String characterList) {
        if (characterList.contains(Character.toString(TickerUtils.EMPTY_CHAR))) {
            throw new IllegalArgumentException(
                    "You don't need to include TickerUtils.EMPTY_CHAR in the character list.");
        }

        final char[] charsArray = characterList.toCharArray();
        final int length = charsArray.length;
        this.numOriginalCharacters = length;

        characterIndicesMap = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            characterIndicesMap.put(charsArray[i], i);
        }

        this.characterList = new char[length * 2 + 1];
        this.characterList[0] = TickerUtils.EMPTY_CHAR;
        for (int i = 0; i < length; i++) {
            this.characterList[1 + i] = charsArray[i];
            this.characterList[1 + length + i] = charsArray[i];
        }
    }

    /**
     * @param start the character that we want to animate from
     * @param end the character that we want to animate to
     * @return a valid {@link AnimationCharacterIndices}, or null if the inputs are not supported.
     */
    AnimationCharacterIndices getCharacterIndices(char start, char end) {
        int startIndex = getIndexOfChar(start);
        int endIndex = getIndexOfChar(end);
        if (startIndex < 0 || endIndex < 0) {
            return null;
        }

        if (start != TickerUtils.EMPTY_CHAR && end != TickerUtils.EMPTY_CHAR &&
                endIndex < startIndex) {
            final int nonWrapDistance = startIndex - endIndex;
            final int wrapDistance = numOriginalCharacters - startIndex + endIndex;
            if (wrapDistance < nonWrapDistance) {
                endIndex = numOriginalCharacters + endIndex;
            }
        }
        return new AnimationCharacterIndices(startIndex, endIndex);
    }

    Set<Character> getSupportedCharacters() {
       return characterIndicesMap.keySet();
    }

    char[] getCharacterList() {
        return characterList;
    }

    private int getIndexOfChar(char c) {
        if (c == TickerUtils.EMPTY_CHAR) {
            return 0;
        } else if (characterIndicesMap.containsKey(c)) {
            return characterIndicesMap.get(c) + 1;
        } else {
            return -1;
        }
    }

    public class AnimationCharacterIndices {
        public final int startIndex;
        public final int endIndex;

        public AnimationCharacterIndices(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }
}