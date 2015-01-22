package com.jcwhatever.nucleus.utils.text;

import com.jcwhatever.nucleus.utils.text.TextUtils.CaseSensitivity;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TextUtilsTest {

    @org.junit.Test
    public void testIsValidName() throws Exception {

        Assert.assertEquals(true, TextUtils.isValidName("validName"));

        Assert.assertEquals(true, TextUtils.isValidName("valid_Name"));

        Assert.assertEquals(true, TextUtils.isValidName("validName_"));

        // must start with a letter
        Assert.assertEquals(false, TextUtils.isValidName("_invalidName"));

        // must start with a letter
        Assert.assertEquals(false, TextUtils.isValidName("01234567890"));

        // must be less than or equal to 16 characters
        Assert.assertEquals(false, TextUtils.isValidName("a01234567890ABCDEFGH"));

        // can be 16 characters
        Assert.assertEquals(true, TextUtils.isValidName("a01234567890ABCD"));

        // must be more than 0 characters
        Assert.assertEquals(false, TextUtils.isValidName(""));

        // cannot contain illegal characters
        Assert.assertEquals(false, TextUtils.isValidName("a-"));


        // must be less than or equal to 17 characters
        Assert.assertEquals(false, TextUtils.isValidName("a01234567890ABCDEFGH", 17));

        // can be 17 characters
        Assert.assertEquals(true, TextUtils.isValidName("a01234567890ABCDE", 17));
    }

    @org.junit.Test
    public void testStartsWith() throws Exception {

        List<String> searchCandidates = new ArrayList<String>(10);
        searchCandidates.add("ABC_");
        searchCandidates.add("abc_");
        searchCandidates.add("_abc");
        searchCandidates.add("_ABC");

        List<String> searchResult;

        searchResult = TextUtils.startsWith("", searchCandidates);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(4, searchResult.size());
        Assert.assertEquals(true, searchResult.containsAll(searchCandidates));

        searchResult = TextUtils.startsWith("", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(4, searchResult.size());
        Assert.assertEquals(true, searchResult.containsAll(searchCandidates));

        searchResult = TextUtils.startsWith("abc", searchCandidates);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertEquals("abc_", searchResult.get(0));

        searchResult = TextUtils.startsWith("ABC", searchCandidates);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertEquals("ABC_", searchResult.get(0));

        searchResult = TextUtils.startsWith("ABC", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertEquals(true, searchResult.contains("ABC_"));
        Assert.assertEquals(true, searchResult.contains("abc_"));

        searchResult = TextUtils.startsWith("ABCD", searchCandidates);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.startsWith("ABCD", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.startsWith("ABC", new ArrayList<String>(0));
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.startsWith("ABC", new ArrayList<String>(0), CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());
    }

    @org.junit.Test
    public void testEndsWith() throws Exception {

        List<String> searchCandidates = new ArrayList<String>(10);
        searchCandidates.add("ABC_");
        searchCandidates.add("abc_");
        searchCandidates.add("_abc");
        searchCandidates.add("_ABC");

        List<String> searchResult;

        searchResult = TextUtils.endsWith("", searchCandidates);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(4, searchResult.size());
        Assert.assertEquals(true, searchResult.containsAll(searchCandidates));

        searchResult = TextUtils.endsWith("", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(4, searchResult.size());
        Assert.assertEquals(true, searchResult.containsAll(searchCandidates));

        searchResult = TextUtils.endsWith("abc", searchCandidates);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertEquals("_abc", searchResult.get(0));

        searchResult = TextUtils.endsWith("ABC", searchCandidates);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertEquals("_ABC", searchResult.get(0));

        searchResult = TextUtils.endsWith("ABC", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertEquals(true, searchResult.contains("_ABC"));
        Assert.assertEquals(true, searchResult.contains("_abc"));

        searchResult = TextUtils.endsWith("ABCD", searchCandidates);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.endsWith("ABCD", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.endsWith("ABC", new ArrayList<String>(0));
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.endsWith("ABC", new ArrayList<String>(0), CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());
    }

    @org.junit.Test
    public void testContains() throws Exception {

        List<String> searchCandidates = new ArrayList<String>(10);
        searchCandidates.add("ABC_");
        searchCandidates.add("abc_");
        searchCandidates.add("_abc");
        searchCandidates.add("_ABC");

        List<String> searchResult;

        searchResult = TextUtils.contains("", searchCandidates);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(4, searchResult.size());
        Assert.assertEquals(true, searchResult.containsAll(searchCandidates));

        searchResult = TextUtils.contains("", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(4, searchResult.size());
        Assert.assertEquals(true, searchResult.containsAll(searchCandidates));

        searchResult = TextUtils.contains("abc", searchCandidates);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertEquals(true, searchResult.contains("_abc"));
        Assert.assertEquals(true, searchResult.contains("abc_"));

        searchResult = TextUtils.contains("ABC", searchCandidates);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertEquals(true, searchResult.contains("_ABC"));
        Assert.assertEquals(true, searchResult.contains("ABC_"));

        searchResult = TextUtils.contains("ABC", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(4, searchResult.size());
        Assert.assertEquals(true, searchResult.contains("_ABC"));
        Assert.assertEquals(true, searchResult.contains("_abc"));
        Assert.assertEquals(true, searchResult.contains("ABC_"));
        Assert.assertEquals(true, searchResult.contains("abc_"));

        searchResult = TextUtils.contains("ABCD", searchCandidates);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.contains("ABCD", searchCandidates, CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.contains("ABC", new ArrayList<String>(0));
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

        searchResult = TextUtils.contains("ABC", new ArrayList<String>(0), CaseSensitivity.IGNORE_CASE);
        Assert.assertEquals(true, searchResult != null);
        Assert.assertEquals(0, searchResult.size());

    }

    @org.junit.Test
    public void testPadRight() throws Exception {

        String source = "source";

        Assert.assertEquals("source   ", TextUtils.padRight(source, 3));

        Assert.assertEquals("source|||", TextUtils.padRight(source, 3, '|'));

        Assert.assertEquals("source", TextUtils.padRight(source, 0, '|'));

        Assert.assertEquals("|||", TextUtils.padRight("", 3, '|'));
    }


    @org.junit.Test
    public void testPadLeft() throws Exception {
        String source = "source";

        Assert.assertEquals("   source", TextUtils.padLeft(source, 3));

        Assert.assertEquals("|||source", TextUtils.padLeft(source, 3, '|'));

        Assert.assertEquals("source", TextUtils.padLeft(source, 0, '|'));

        Assert.assertEquals("|||", TextUtils.padLeft("", 3, '|'));
    }

    @org.junit.Test
    public void testTruncate() throws Exception {
        String source = "1234567890ABCDEFG";

        Assert.assertEquals("1234567890ABCDEF", TextUtils.truncate(source));

        Assert.assertEquals("12345", TextUtils.truncate(source, 5));

    }

    @org.junit.Test
    public void testCamelCase() throws Exception {

        Assert.assertEquals("thisIsATest", TextUtils.camelCase("This is a test"));

        Assert.assertEquals("thisisatest", TextUtils.camelCase("thisIsATest"));

        Assert.assertEquals("thisisatest", TextUtils.camelCase("Thisisatest"));

        Assert.assertEquals("", TextUtils.camelCase(""));
    }

    @org.junit.Test
    public void testTitleCase() throws Exception {

        Assert.assertEquals("This is a Test", TextUtils.titleCase("This is a test"));

        Assert.assertEquals("ThisIsATest", TextUtils.titleCase("thisIsATest"));

        Assert.assertEquals("Thisisatest", TextUtils.titleCase("Thisisatest"));

        Assert.assertEquals("", TextUtils.titleCase(""));
    }

    @org.junit.Test
    public void testConcat() throws Exception {

        List<String> list = new ArrayList<>(10);
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        Assert.assertEquals("1,2,3,4", TextUtils.concat(list, ","));

        Assert.assertEquals("1,2,3,4", TextUtils.concat(list, ",", "empty"));

        Assert.assertEquals("empty", TextUtils.concat(new ArrayList<>(0), ",", "empty"));
    }

    @org.junit.Test
    public void testConcat1() throws Exception {

        String[] array = new String[]{
                "1",
                "2",
                "3",
                "4"
        };

        Assert.assertEquals("1,2,3,4", TextUtils.concat(array, ","));

        Assert.assertEquals("1,2,3,4", TextUtils.concat(array, ",", "empty"));

        Assert.assertEquals("empty", TextUtils.concat(new String[0], ",", "empty"));
    }

    @org.junit.Test
    public void testConcat2() throws Exception {
        String[] array = new String[]{
                "1",
                "2",
                "3",
                "4"
        };

        Assert.assertEquals("2,3,4", TextUtils.concat(1, array, ","));

        Assert.assertEquals("2,3,4", TextUtils.concat(1, array, ",", "empty"));

        Assert.assertEquals("empty", TextUtils.concat(1, new String[0], ",", "empty"));
    }

    @org.junit.Test
    public void testConcat3() throws Exception {
        String[] array = new String[]{
                "1",
                "2",
                "3",
                "4"
        };

        Assert.assertEquals("2,3", TextUtils.concat(1, 3, array, ","));

        Assert.assertEquals("2,3", TextUtils.concat(1, 3, array, ",", "empty"));

        Assert.assertEquals("empty", TextUtils.concat(1, 2, new String[0], ",", "empty"));

        Assert.assertEquals("empty", TextUtils.concat(1, 1, new String[0], ",", "empty"));
    }

    @org.junit.Test
    public void testPaginateString() throws Exception {

        String singleWord = "1234567890ABCDEF";
        String multiWord = "A B C D E F";
        String multiWordBig = "A B C D E F 1234567890ABCDEF";

        List<String> result;

        // should return entire source as a single word that cant be broken
        result = TextUtils.paginateString(singleWord, 2, false);
        Assert.assertEquals(true, result != null);
        Assert.assertEquals(1, result.size());

        result = TextUtils.paginateString(multiWord, 2, false);
        Assert.assertEquals(true, result != null);
        Assert.assertEquals(6, result.size());

        result = TextUtils.paginateString(multiWordBig, 2, false);
        Assert.assertEquals(true, result != null);
        Assert.assertEquals(7, result.size());

        /*
        for (int i=0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
        */
    }

    @org.junit.Test
    public void testPaginateString1() throws Exception {

        String singleWord = "1234567890ABCDEF";
        String multiWord = "§lA §lB §lC §lD §lE §lF";
        String multiWordBig = "§l§l§lA §lB §lC §lD §lE §lF §l1234567890ABCDEF";

        List<String> result;

        // should return entire source as a single word that cant be broken
        result = TextUtils.paginateString(singleWord, 2, true);
        Assert.assertEquals(true, result != null);
        Assert.assertEquals(1, result.size());

        result = TextUtils.paginateString(multiWord, 2, true);
        Assert.assertEquals(true, result != null);
        Assert.assertEquals(6, result.size());

        result = TextUtils.paginateString(multiWordBig, 2, true);
        Assert.assertEquals(true, result != null);
        Assert.assertEquals(7, result.size());
    }

    @org.junit.Test
    public void testParseBoolean() throws Exception {

        Assert.assertEquals(true, TextUtils.parseBoolean("true"));
        Assert.assertEquals(true, TextUtils.parseBoolean("True"));
        Assert.assertEquals(true, TextUtils.parseBoolean("TRUE"));
        Assert.assertEquals(true, TextUtils.parseBoolean("yes"));
        Assert.assertEquals(true, TextUtils.parseBoolean("Yes"));
        Assert.assertEquals(true, TextUtils.parseBoolean("YES"));
        Assert.assertEquals(true, TextUtils.parseBoolean("1"));

        Assert.assertEquals(false, TextUtils.parseBoolean(null));
        Assert.assertEquals(false, TextUtils.parseBoolean("false"));
        Assert.assertEquals(false, TextUtils.parseBoolean("123"));
    }

    @org.junit.Test
    public void testParseByte() throws Exception {
        Assert.assertEquals((byte) 10, TextUtils.parseByte("10", (byte) 5));

        Assert.assertEquals((byte) 5, TextUtils.parseByte("10000", (byte) 5));
        Assert.assertEquals((byte) 5, TextUtils.parseByte(null, (byte) 5));
        Assert.assertEquals((byte) 5, TextUtils.parseByte("false", (byte) 5));
    }

    @org.junit.Test
    public void testParseShort() throws Exception {
        Assert.assertEquals(10, TextUtils.parseShort("10", (short) 5));

        Assert.assertEquals(5, TextUtils.parseShort(String.valueOf(Integer.MAX_VALUE), (short) 5));
        Assert.assertEquals(5, TextUtils.parseShort(null, (short) 5));
        Assert.assertEquals(5, TextUtils.parseShort("false", (short) 5));
    }

    @org.junit.Test
    public void testParseInt() throws Exception {
        Assert.assertEquals(10, TextUtils.parseInt("10", 5));

        Assert.assertEquals(5, TextUtils.parseInt(String.valueOf(Long.MAX_VALUE), 5));
        Assert.assertEquals(5, TextUtils.parseInt(null, 5));
        Assert.assertEquals(5, TextUtils.parseInt("false", 5));
    }

    @org.junit.Test
    public void testParseLong() throws Exception {
        Assert.assertEquals(10, TextUtils.parseLong("10", 5));

        Assert.assertEquals(5, TextUtils.parseLong(null, 5));
        Assert.assertEquals(5, TextUtils.parseLong("false", 5));
    }

    @org.junit.Test
    public void testParseFloat() throws Exception {
        Assert.assertEquals(10F, TextUtils.parseFloat("10", 5F), 0F);
        Assert.assertEquals(10.5F, TextUtils.parseFloat("10.5", 5F), 0F);

        Assert.assertEquals(5F, TextUtils.parseFloat(null, 5F), 0F);
        Assert.assertEquals(5F, TextUtils.parseFloat("false", 5F), 0F);
    }

    @org.junit.Test
    public void testParseDouble() throws Exception {
        Assert.assertEquals(10D, TextUtils.parseDouble("10", 5.0D), 0D);
        Assert.assertEquals(10.5D, TextUtils.parseDouble("10.5", 5.0D), 0D);

        Assert.assertEquals(5.0D, TextUtils.parseDouble(null, 5.0D), 0D);
        Assert.assertEquals(5.0D, TextUtils.parseDouble("false", 5.0D), 0D);
    }

    @org.junit.Test
    public void testParseUUID() throws Exception {

        UUID testId = UUID.randomUUID();

        Assert.assertEquals(testId, TextUtils.parseUUID(testId.toString()));
        Assert.assertEquals(null, TextUtils.parseUUID((String) null));
        Assert.assertEquals(null, TextUtils.parseUUID("false"));
   }
}