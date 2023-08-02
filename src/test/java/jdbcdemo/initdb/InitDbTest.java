package jdbcdemo.initdb;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer;


import static org.junit.jupiter.api.Assertions.*;

class InitDbTest {

    @Test
    void mergeTwoListTest() {
        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");

        List<String> list2 = new ArrayList<>();
        list1.add("4");
        list1.add("5");
        list1.add("6");

        List<String> list3 = new ArrayList<>();
        list3.add("1");
        list3.add("2");
        list3.add("3");
        list3.add("4");
        list3.add("5");
        list3.add("6");

        assertEquals(list3, InitDb.mergeTwoList(list2, list1));
    }

    @Test
    void dateOfBirthGeneratorTest() {
        System.out.println(InitDb.dateOfBirthGenerator());
    }

    @Test
    void emailCorrectorTest() {
        assertEquals("aeuo", InitDb.removeAccentsWithApacheCommons("αιόσ"));
    }

    @Test
    void passwordGeneratorTest() {
        System.out.println(InitDb.passwordGenerator());
    }
}