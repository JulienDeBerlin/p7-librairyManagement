package com.berthoud.p7.webserviceapp.business;

import com.berthoud.p7.webserviceapp.consumer.contract.BookDAO;
import com.berthoud.p7.webserviceapp.consumer.contract.CustomerDAO;
import com.berthoud.p7.webserviceapp.consumer.contract.LoanDAO;
import com.berthoud.p7.webserviceapp.model.entities.Book;
import com.berthoud.p7.webserviceapp.model.entities.Customer;
import com.berthoud.p7.webserviceapp.model.entities.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * This class is dedicated to the management of loans.
 */


@Service
@PropertySource("classpath:settings.properties")
public class LoanManager {


    @Value("${maxExtensions}")
    private String maxExtensions;

    @Value("${extensionLengthInDays}")
    private String extensionLengthInDays;

    @Value("${loanLengthInDays}")
    private String loanLengthInDays;


    @Autowired
    LoanDAO loanDAO;

    @Autowired
    BookDAO bookDAO;

    @Autowired
    CustomerDAO customerDAO;


    /**
     * The method is used to extend an active loan. The extension of a loan is only possible if all following conditions are met:
     * - membership is still valid
     * - max number of extension has not been reached (value to be set in separate settings.properties file)
     * - and of course, if loan id matches with an existing and still open loan.
     * <p>
     * The length of the extension is also to be set in the separate settings.properties file.
     *
     * @param loanId the id of the loan to be extended
     * @return :
     * 1 = success (loan has been extended),
     * 0 = failure (membership expired),
     * -1 = failure (max amount of extensions reached),
     * -2 = failure (loanId not correct)
     */
    public int extendLoan(int loanId) {

        Optional<Loan> l = loanDAO.findById(loanId);
        if (!l.isPresent() || !l.get().getBook().getStatus().equals(Book.Status.BORROWED)) {
            return -2;
        }

        Loan loan = l.get();

        if (loan.getCustomer().getDateExpirationMembership().isBefore(LocalDate.now())) {
            return 0;
        }


        if (loan.getNumberExtensions() < Integer.parseInt(maxExtensions)) {
            loan.setDateEnd(loan.getDateEnd().plusDays(Integer.parseInt(extensionLengthInDays)));
            loan.setNumberExtensions(loan.getNumberExtensions() + 1);
            loanDAO.save(loan);
            return 1;

        } else {
            return -1;
        }
    }


    /**
     * This method is used to register a new loan.
     * The registration of a new loan is only possible if all following conditions are met:
     * - membership is still valid
     * - the book is available
     * - id of customer and id of book are correct.
     *
     * @param CustomerId the id of the customer who want to borrow the book
     * @param bookId     the id of the book wished by the customer
     * @return :
     * 1 = success (loan is possible and registered),
     * 0 = failure (membership expired),
     * -1 = failure (book Id not correct),
     * -2 = failure (customer Id not correct),
     * -3 = failure (book not available)
     */
    public int registerNewLoan(int CustomerId, int bookId) {

        Optional<Book> b = bookDAO.findById(bookId);
        if (!b.isPresent()) {
            return -1;
//            throw new NoSuchElementException(bookId + " is not a valid book id");
        }

        if (!b.get().getStatus().equals(Book.Status.AVAILABLE)) {
            return -3;
        }


        Optional<Customer> c = customerDAO.findById(CustomerId);
        if (!c.isPresent()) {
            return -2;
//            throw new NoSuchElementException(CustomerId + " is not a valid customer id");
        }

        if (c.get().getDateExpirationMembership().isBefore(LocalDate.now())) {
            return 0;
        }

        // create new loan
        Loan newloan = new Loan();
        newloan.setCustomer(c.get());
        newloan.setBook(b.get());
        newloan.setNumberExtensions(0);
        newloan.setDateBegin(LocalDate.now());

        LocalDate DateEnd = LocalDate.now().plusDays(Integer.parseInt(loanLengthInDays));
        newloan.setDateEnd(DateEnd);


        newloan.setDateBack(LocalDate.of(1900, 1, 1));
        loanDAO.save(newloan);

        // change book status
        b.get().setStatus(Book.Status.BORROWED);
        bookDAO.save(b.get());
        return 1;
    }


    /**
     * This method is used to register a new loan.
     *
     * @param bookId the id of the book that is returned.
     * @return :
     * 1 = success (book return has been validated),
     * 0 = failure (no loan active with for this book id)
     * -1 = failure (bookId is not a valid book id)
     */

    public int bookBack(int bookId) {

        Optional<Book> b = bookDAO.findById(bookId);
        if (!b.isPresent()) {
            return -1;
        }

        Book book = b.get();
        if (!book.getStatus().equals(Book.Status.BORROWED)) {
            return 0;
        }

        for (Loan l : book.getLoans()) {

            if (l.getDateBack().equals(LocalDate.of(1900, 1, 1))) {
                l.setDateBack(LocalDate.now());
                loanDAO.save(l);

                book.setStatus(Book.Status.AVAILABLE);
                bookDAO.save(book);

                break;
            }
        }
        return 1;
    }


    /**
     * This method is used for the loan monitoring.
     *
     * @return a list of all still open loans.
     */
    public List<Loan> getAllOpenLoans() {
        List<Loan> allLoans = new ArrayList<>();
        allLoans.addAll(getOpenLoansInTime());
        allLoans.addAll(getOpenLoansLate());
        return allLoans;
    }

    /**
     * This method is used for the loan monitoring.
     *
     * @return a list of the open loans for which the return deadline has not been reached yet.
     */
    public List<Loan> getOpenLoansInTime() {
        // DateBack is 01/01/1900 for Loan still open
        LocalDate back = LocalDate.of(1900, 01, 01);
        return loanDAO.findByDateBackAndDateEndGreaterThan(back, LocalDate.now());
    }

    /**
     * This method is used for the loan monitoring.
     *
     * @return a list of the open loans for which the return deadline has been reached.
     */
    public List<Loan> getOpenLoansLate() {
        // DateBack is 01/01/1900 for Loan still open
        LocalDate back = LocalDate.of(1900, 01, 01);
        return loanDAO.findByDateBackAndDateEndLessThanEqual(back, LocalDate.now());
    }

}

