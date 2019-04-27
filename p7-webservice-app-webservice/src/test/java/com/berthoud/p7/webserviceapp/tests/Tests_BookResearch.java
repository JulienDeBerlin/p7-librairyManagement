package com.berthoud.p7.webserviceapp.tests;

import com.berthoud.p7.webserviceapp.business.BookResearchManager;
import com.berthoud.p7.webserviceapp.consumer.repositories.SpringDataJPA.BookReferenceRepository;
import com.berthoud.p7.webserviceapp.consumer.repositories.SpringDataJPA.BookRepository;
import com.berthoud.p7.webserviceapp.model.entities.Book;
import com.berthoud.p7.webserviceapp.model.entities.BookReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Tests_BookResearch {

    @Autowired
    BookReferenceRepository bookReferenceRepository;

    @Autowired
    BookResearchManager bookResearchManager;

    @Autowired
    BookRepository bookRepo;

    @Test
    @Transactional
    public void findByTags() {

        List<BookReference> bookReferenceList =
                bookReferenceRepository.findBookReferenceByTags(new HashSet<>(Arrays.asList("Sport", "Aventure", "Ecologie")), 3);

        for (BookReference bookReference : bookReferenceList) {
            System.out.println(bookReference.getTitle());
            for (Book book : bookReference.getBooks()) {
                System.out.println(book.getStatus());
            }
        }

        if (bookReferenceList.isEmpty()) {
            System.out.println("EMPTY!!!!");
        }

    }

    @Test
    @Transactional
    public void findByTagsAndAuthor() {

        List<BookReference> bookReferenceList =
                bookReferenceRepository.findBookReferenceByTagsAndAuthor(new HashSet<>(Arrays.asList("Sport", "Aventure", "Ecologie")), 3, "Surty");

        for (BookReference bookReference : bookReferenceList) {
            System.out.println(bookReference.getTitle());
            for (Book book : bookReference.getBooks()) {
                System.out.println(book.getStatus());
            }
        }

        if (bookReferenceList.isEmpty()) {
            System.out.println("EMPTY!!!!");
        }

    }


    @Test
    @Transactional
    public void findByTagsAndTitleElement() {

        List<BookReference> bookReferenceList =
                bookReferenceRepository.findBookReferenceByTagsAndTitleElement(new HashSet<>(Arrays.asList("Sport", "Aventure", "Ecologie")), 3, "les septs");

        for (BookReference bookReference : bookReferenceList) {
            System.out.println(bookReference.getTitle());
            for (Book book : bookReference.getBooks()) {
                System.out.println(book.getStatus());
            }
        }

        if (bookReferenceList.isEmpty()) {
            System.out.println("EMPTY!!!!");
        }

    }

    @Test
    @Transactional
    public void findByTagsAndTitleElementAndAuthor() {

        List<BookReference> bookReferenceList =
                bookReferenceRepository.findBookReferenceByTagsAndTitleElementAndAuthor
                        (new HashSet<>(Arrays.asList("Sport", "Aventure", "Ecologie")), 3, "%les septs %", "sur");

        for (BookReference bookReference : bookReferenceList) {
            System.out.println(bookReference.getTitle());
            for (Book book : bookReference.getBooks()) {
                System.out.println(book.getStatus());
            }
        }

        if (bookReferenceList.isEmpty()) {
            System.out.println("EMPTY!!!!");
        }

    }


    @Test
    @Transactional
    public void finderTestJPQL() {

        List<BookReference> bookReferenceList =
                bookReferenceRepository.findBookReferenceTestJPQL("Sur");

        for (BookReference bookReference : bookReferenceList) {
            System.out.println(bookReference.getTitle());
            for (Book book : bookReference.getBooks()) {
                System.out.println(book.getStatus());
            }
        }

        if (bookReferenceList.isEmpty()) {
            System.out.println("EMPTY!!!!");
        }

    }


    @Test
    @Transactional
    public void findMultipleParameters() {

        List<BookReference> bookReferenceList =
                bookResearchManager.findBookMultiParameters("Sur", "Italie", -1, Arrays.asList("Sport", "Aventure", "Ecologie"));
        assertEquals(bookReferenceList.size(), 1);

        List<BookReference> bookReferenceList2 =
                bookResearchManager.findBookMultiParameters("Sur", "Italie", 1, Arrays.asList("Sport", "Aventure", "Ecologie"));
        assertEquals(bookReferenceList2.size(), 1);

        List<BookReference> bookReferenceList3 =
                bookResearchManager.findBookMultiParameters("Sur", "Italie", 2, Arrays.asList("Sport", "Aventure", "Ecologie"));
        assertEquals(bookReferenceList3.size(), 0);

        List<BookReference> bookReferenceList4 =
                bookResearchManager.findBookMultiParameters("sur", "Italie", -1, Arrays.asList("Sport", "Aventure", "Ecologie"));
        assertEquals(bookReferenceList4.size(), 1);

        List<BookReference> bookReferenceList5 =
                bookResearchManager.findBookMultiParameters("Sur", "Ital", -1, Arrays.asList("Sport", "Aventure", "Ecologie"));
        assertEquals(bookReferenceList5.size(), 1);

        List<BookReference> bookReferenceList6 =
                bookResearchManager.findBookMultiParameters("Su", "Italie", -1, Arrays.asList("Sport", "Aventure", "Ecologie"));
        assertEquals(bookReferenceList6.size(), 0);

        List<BookReference> bookReferenceList7 =
                bookResearchManager.findBookMultiParameters("Sur", "Italie", -1, Arrays.asList("Sport", "Ecologie", "Ecologie"));
        assertEquals(bookReferenceList7.size(), 1);

        List<BookReference> bookReferenceList8 =
                bookResearchManager.findBookMultiParameters("", "Italie", -1, Arrays.asList());
        assertEquals(bookReferenceList8.size(), 3);

        List<BookReference> bookReferenceList9 =
                bookResearchManager.findBookMultiParameters("", "", -1, Arrays.asList("Architecture"));
        assertEquals(bookReferenceList9.size(), 2);

        List<BookReference> bookReferenceList10 =
                bookResearchManager.findBookMultiParameters("Sur", "", -1, Arrays.asList());
        assertEquals(bookReferenceList10.size(), 1);

    }


    @Test
    @Transactional
    public void findBookByTitle() {
        List<BookReference> bookReferenceList = bookResearchManager.findBookReferenceByTitle("itALie");
        for (BookReference bookReference : bookReferenceList) {
            System.out.println(bookReference.getTitle());
            for (Book book : bookReference.getBooks()) {
                System.out.println(book.getStatus());
            }
        }
    }


}
