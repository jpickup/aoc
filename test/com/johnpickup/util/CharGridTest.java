package com.johnpickup.util;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CharGridTest {
    CharGrid twoByTwo = new CharGrid(Arrays.asList("12","34"));
    CharGrid threeByThree = new CharGrid(Arrays.asList("123","456","789"));
    CharGrid threeByFour = new CharGrid(Arrays.asList("abc","def","ghi","jkl"));
    CharGrid fourByFour = new CharGrid(Arrays.asList("abcd","efgh","ijkl","mnop"));
    CharGrid fiveByFive = new CharGrid(Arrays.asList("abcde","fghij","klmno","pqrst","uvwxy"));

    @Test
    public void flipHorizontal() {
        assertThat(twoByTwo.flipHorizontal(), is(new CharGrid(Arrays.asList("21","43"))));
        assertThat(threeByThree.flipHorizontal(), is(new CharGrid(Arrays.asList("321","654","987"))));
        assertThat(threeByThree.flipHorizontal().flipHorizontal(), is(threeByThree));
    }

    @Test
    public void flipVertical() {
        assertThat(twoByTwo.flipVertical(), is(new CharGrid(Arrays.asList("34","12"))));
        assertThat(threeByThree.flipVertical(), is(new CharGrid(Arrays.asList("789","456","123"))));
        assertThat(threeByThree.flipVertical().flipVertical(), is(threeByThree));
    }

    @Test
    public void rotateClockwise() {
        assertThat(twoByTwo.rotateClockwise(), is(new CharGrid(Arrays.asList("31","42"))));
        assertThat(threeByThree.rotateClockwise(), is(new CharGrid(Arrays.asList("741","852","963"))));
        assertThat(threeByFour.rotateClockwise(), is(new CharGrid(Arrays.asList("jgda","kheb","lifc"))));
        assertThat(fourByFour.rotateClockwise(), is(new CharGrid(Arrays.asList("miea","njfb","okgc","plhd"))));
        assertThat(fiveByFive.rotateClockwise(), is(new CharGrid(Arrays.asList("upkfa","vqlgb","wrmhc","xsnid","ytoje"))));
        assertThat(threeByThree.rotateClockwise().rotateClockwise().rotateClockwise().rotateClockwise(), is(threeByThree));
        assertThat(threeByThree.rotateClockwise().rotateClockwise(), is(threeByThree.flipHorizontal().flipVertical()));
    }
}