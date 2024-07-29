import java.io.*;
import java.util.*;

interface Manageable {
    void saveToFile(String filename) throws IOException;
    void loadFromFile(String filename) throws IOException, ClassNotFoundException;
}

class Book implements Serializable {
    private String title;
    private String author;
    private boolean isCheckedOut;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isCheckedOut = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isCheckedOut() {
        return isCheckedOut;
    }

    public void checkOut() {
        isCheckedOut = true;
    }

    public void returnBook() {
        isCheckedOut = false;
    }

    @Override
    public String toString() {
        return String.format("%-30s %-30s %s", title, author, isCheckedOut ? "Checked Out" : "Available");
    }
}

class Library<T extends Book> implements Manageable {
    private List<T> books;

    public Library() {
        books = new ArrayList<>();
    }

    public void addBook(T book) {
        books.add(book);
    }

    public void removeBook(String title) {
        books.removeIf(book -> book.getTitle().equalsIgnoreCase(title));
    }

    public T findBook(String title) {
        for (T book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public void checkOutBook(String title) {
        T book = findBook(title);
        if (book != null && !book.isCheckedOut()) {
            book.checkOut();
            System.out.println("You have checked out: " + book);
        } else if (book == null) {
            System.out.println("Book not found.");
        } else {
            System.out.println("Book is already checked out.");
        }
    }

    public void returnBook(String title) {
        T book = findBook(title);
        if (book != null && book.isCheckedOut()) {
            book.returnBook();
            System.out.println("You have returned: " + book);
        } else if (book == null) {
            System.out.println("Book not found.");
        } else {
            System.out.println("Book was not checked out.");
        }
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            System.out.println("\n" + "=" + 90);
            System.out.printf("| %-30s | %-30s | %-20s |\n", "Title", "Author", "Status");
            System.out.println("=" + 90);
            for (T book : books) {
                System.out.printf("| %-30s | %-30s | %-20s |\n", book.getTitle(), book.getAuthor(), book.isCheckedOut() ? "Checked Out" : "Available");
            }
            System.out.println("="  + 90);
        }
    }

    @Override
    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(books);
        }
    }

    @Override
    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            books = (List<T>) ois.readObject();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Library<Book> library = new Library<>();
        boolean running = true;

        try {
            library.loadFromFile("library_data.dat");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing library data found. Starting with an empty library.");
        }

        while (running) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String addTitle = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String addAuthor = scanner.nextLine();
                    library.addBook(new Book(addTitle, addAuthor));
                    System.out.println("Book added successfully.");
                    break;
                case 2:
                    System.out.print("Enter book title to remove: ");
                    String removeTitle = scanner.nextLine();
                    library.removeBook(removeTitle);
                    System.out.println("Book removed successfully.");
                    break;
                case 3:
                    System.out.print("Enter book title to check out: ");
                    String checkOutTitle = scanner.nextLine();
                    library.checkOutBook(checkOutTitle);
                    break;
                case 4:
                    System.out.print("Enter book title to return: ");
                    String returnTitle = scanner.nextLine();
                    library.returnBook(returnTitle);
                    break;
                case 5:
                    library.displayBooks();
                    break;
                case 6:
                    try {
                        library.saveToFile("library_data.dat");
                        System.out.println("Library data saved. Exiting.");
                    } catch (IOException e) {
                        System.out.println("Error saving library data.");
                    }
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("       Library Management System");
        System.out.println("-".repeat(50));
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. Check Out Book");
        System.out.println("4. Return Book");
        System.out.println("5. Display Books");
        System.out.println("6. Save and Exit");
        System.out.print("Choose an option: ");
    }
}
