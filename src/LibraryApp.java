import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Класс Book содержит основные атрибуты книги и методы для работы с ними(старался максимально соблюсти принципы ООП, как и в остальных классах)
class Book {
    private int id;           //Уникальный идентификатор книги
    private String title;     //Название
    private String author;    //Автор
    private int year;         //Год издания
    private String genre;     //Жанр

    //Конструктор
    public Book(int id, String title, String author, int year, String genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
    }

    //Геттеры(для каждого атрибута)
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    //Сеттеры(для каждого атрибута)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setId(int id) {
        this.id = id;
    }

    //Переопределение метода toString() для красивого вывода информации о книге
    @Override
    public String toString() {
        return String.format("id: %d | Название: %s | Автор: %s | Год: %d | Жанр: %s",
                id, title, author, year, genre);
    }
}


//В рамках класса LibraryManager реализованы основные операции: добавление книги, редактирование книги, поиск книг(и) по атрибутам, сохранение и загрузка списка книг в файл(из файла)

class LibraryManager {
    private final List<Book> books;
    //Счетчик для генерации уникальных id книг
    private int nextId;

    //Конструктор инициализирует пустую библиотеку
    public LibraryManager() {
        books = new ArrayList<>();
        //Начинаем нумерацию id с 1
        nextId = 1;
    }

    //Метод addBook позволяет добавлять в библиотеку новую книгу с поочередным вводом параметром(начиная с названия и заканчивая жанром
    public void addBook(String title, String author, int year, String genre) {
        // Все проверки валидности выполняются в addBookMenu, здесь просто создаем и добавляем книгу
        Book newBook = new Book(nextId++, title, author, year, genre);
        books.add(newBook);
        System.out.println("Книга добавлена: " + newBook);
    }

    //Метод editBook позволяет редактировать любую книгу из библиотеки по id
    public void editBook(int id, String title, String author, Integer year, String genre) {
        for (Book book : books) {
            if (book.getId() == id) {
                //Идея: если пользователь в рамках какого-то атрибута ничего не ввел, то не меняем этот атрибут
                if (title != null && !title.isEmpty()) {
                    book.setTitle(title);
                }
                if (author != null && !author.isEmpty()) {
                    book.setAuthor(author);
                }
                if (year != null) {
                    book.setYear(year);
                }
                if (genre != null && !genre.isEmpty()) {
                    book.setGenre(genre);
                }
                System.out.println("Книга изменена: " + book);
                return;
            }
        }
        //Если книга с указанным id не найдена - выводим сообщение об ошибке
        System.out.println("Книга с id " + id + " не найдена");
    }

    //Метод listBooks выводит на экран список всех книг из библиотеки
    public void listBooks() {
        if (books.isEmpty()) {
            System.out.println("В библиотеке нет книг");
            return;
        }
        System.out.println("\nСписок всех книг:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    //Метод searchByAttribute осуществляет поиск книг(и) по любому атрибуту
    public void searchByAttribute(int attributeChoice, String searchTerm) {
        List<Book> foundBooks = new ArrayList<>();

        for (Book book : books) {
            boolean match = false; //Флаг совпадения

            //Пользователь должен выбрать атрибут, по которому производится поиск
            switch (attributeChoice) {
                case 1: //Поиск по названию
                    //Здесь и далее при проверке на совпадение не учитываем регистр
                    match = book.getTitle().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
                case 2: //Поиск по автору
                    match = book.getAuthor().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
                case 3: //Поиск по жанру
                    match = book.getGenre().toLowerCase().contains(searchTerm.toLowerCase());
                    break;
                case 4: //Поиск по году
                    match = String.valueOf(book.getYear()).contains(searchTerm);
                    break;
                case 5: //Поиск по id
                    try {
                        //Преобразовываем поисковый запрос в число 
                        int searchId = Integer.parseInt(searchTerm);
                        match = book.getId() == searchId;
                    } catch (NumberFormatException e) {
                        //Если запрос не число - ничего не делаем (match останется false)
                    }
                    break;
            }

            //Если книга соответствует критериям поиска - добавляем в список найденных
            if (match) {
                foundBooks.add(book);
            }
        }

        //Выводим результаты
        if (foundBooks.isEmpty()) {
            System.out.println("Книги не найдены");
        } else {
            System.out.println("\nНайденные книги:");
            for (Book book : foundBooks) {
                System.out.println(book);
            }
        }
    }

    //Метод saveToFile сохраняет список всех текущих книг из библиотеки в файл с задаваемым названием
    public void saveToFile(String filename) {
        //Создаем объект File для проверки существования файла
        File file = new File(filename);
        //Может быть такое, что пользователь хочет сохранить библу в уже существующий файл, тогда даем ему выбор: перезаписать файл и потерять все данные из него или остановиться
        if (file.exists()) {
            System.out.print("Файл уже существует. Перезаписать? (y/n): ");
            Scanner tempScanner = new Scanner(System.in);
            String answer = tempScanner.nextLine();
            if (!answer.equalsIgnoreCase("y")) {
                System.out.println("Сохранение отменено");
                return;
            }
        }
        
        //Сохраняем список книг в файл
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Book book : books) {
                writer.println(book.getId() + ";" + book.getTitle() + ";" +
                        book.getAuthor() + ";" + book.getYear() + ";" + book.getGenre());
            }
            System.out.println("Данные сохранены в файл: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    //Метод loadFromFile позволяет загрузить в библиотеку список книг без создания дубликатов
    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<Book> loadedBooks = new ArrayList<>();
            int maxId = 0;
            int duplicateCount = 0; //Счетчик пропущенных дубликатов
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    try {
                        int id = Integer.parseInt(parts[0]);      
                        String title = parts[1];                  
                        String author = parts[2];                 
                        int year = Integer.parseInt(parts[3]);    
                        String genre = parts[4];                  

                        //Проверка на дубликаты: если в библиотеке уже есть загружаемая из файла книга, то не добавляем ее
                        boolean isBookExists = false;
                        for (Book existingBook : books) {
                            if (existingBook.getTitle().equalsIgnoreCase(title) &&
                                    existingBook.getAuthor().equalsIgnoreCase(author) &&
                                    existingBook.getYear() == year) {
                                isBookExists = true;  
                                duplicateCount++;   
                                break;              
                            }
                        }

                        if (!isBookExists) {
                            loadedBooks.add(new Book(id, title, author, year, genre));
                        }

                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {
                        //Обработка ошибок преобразования строк в числа
                        System.out.println("Ошибка формата данных в файле: " + e.getMessage());
                    }
                }
            }

            //Добавляем книги в библиотеку
            for (Book loadedBook : loadedBooks) {
                //Назначаем книгам новые уникальные id
                loadedBook.setId(nextId++);
                books.add(loadedBook);
            }

            //Вывод результатов
            System.out.println("Данные загружены из файла: " + filename);
            System.out.println("Добавлено новых книг: " + loadedBooks.size());
            //Если были дубликаты - сообщаем сколько пропущено
            if (duplicateCount > 0) {
                System.out.println("Пропущено дубликатов: " + duplicateCount);
            }

        } catch (FileNotFoundException e) {
            //Обработка случая когда файл не найден
            System.out.println("Файл не найден: " + filename);
        } catch (IOException e) {
            //Обработка других ошибок ввода-вывода
            System.out.println("Ошибка при загрузке из файла: " + e.getMessage());
        }
    }

    //Метод для проверки существования книги в библиотеке по id
    public boolean isBookExists(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return true; 
            }
        }
        return false; 
    }
}

//Главный класс приложения - содержит точку входа программы (main метод) и реализует пользовательский интерфейс
public class LibraryApp {
    private static final Scanner scanner = new Scanner(System.in);  //Для чтения ввода пользователя
    private static final LibraryManager library = new LibraryManager(); //Основной объект библиотеки

    //Метод main является точкой входа в программу
    public static void main(String[] args) {
        System.out.println("=== Менеджер библиотеки ===");

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": //Добавление книги
                    addBookMenu();
                    break;
                case "2": //Редактирование книги
                    editBookMenu();
                    break;
                case "3": //Просмотр всех книг
                    library.listBooks();
                    break;
                case "4": //Поиск книг
                    searchBooksMenu();
                    break;
                case "5": //Сохранение в файл
                    saveToFileMenu();
                    break;
                case "6": //Загрузка из файла
                    loadFromFileMenu();
                    break;
                case "0": //Выход из программы
                    System.out.println("Выход из программы");
                    scanner.close();
                    return;
                default: //Неверный ввод(не в диапазоне 0-6)
                    System.out.println("Неверный выбор, попробуйте снова");
            }
        }
    }

    //Метод printMenu выводит пользователю главный интерфейс взаимодействия с программой
    private static void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Добавить книгу");
        System.out.println("2. Редактировать книгу");
        System.out.println("3. Показать все книги");
        System.out.println("4. Найти книгу");
        System.out.println("5. Сохранить в файл");
        System.out.println("6. Загрузить из файла");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    //Метод addBookMenu запрашивает, считывает и проверяет на валидность вводимые пользователем данные при добавлении новой книги
    private static void addBookMenu() {
        //Запрашиваем атрибуты новой книги у пользователя
        System.out.print("Введите название: ");
        String title = scanner.nextLine().trim();
        //Далее идут проверки на валидность для каждого вводимого атрибута, если какой-то атрибут оказался невалидным - возврат в главное меню
        if (title.isEmpty()) {
            System.out.println("Ошибка: название книги не может быть пустым");
            return;
        }
        System.out.print("Введите автора: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("Ошибка: автор книги не может быть пустым");
            return;
        }

        int year = readIntInput("Введите год издания: ");
        if (year == 0) {
            System.out.println("Ошибка: год издания не может быть пустым или равным 0");
            return;
        }

        System.out.print("Введите жанр: ");
        String genre = scanner.nextLine().trim();
        if (genre.isEmpty()) {
            System.out.println("Ошибка: жанр книги не может быть пустым");
            return;
        }

        //Если все проверки пройдены - передаем данные в метод добавления книги
        library.addBook(title, author, year, genre);
    }

    //Метод editBookMenu позволяет редактировать существующие в библиотеке книги по id
    private static void editBookMenu() {
        int id = readIntInput("Введите id книги для редактирования: ");

        if (!library.isBookExists(id)) {
            System.out.println("Книга с id " + id + " не найдена");
            return;
        }

        System.out.print("Введите новое название (оставьте пустым чтобы не менять): ");
        String title = scanner.nextLine().trim();

        System.out.print("Введите нового автора (оставьте пустым чтобы не менять): ");
        String author = scanner.nextLine().trim();

        //Используем вспомогательный метод для чтения числа с возможностью пропуска
        Integer year = readIntInputOrNull("Введите новый год издания (оставьте пустым чтобы не менять): ");

        System.out.print("Введите новый жанр (оставьте пустым чтобы не менять): ");
        String genre = scanner.nextLine().trim();
        
        //Передаем null для не меняемых полей
        library.editBook(id,
                title.isEmpty() ? null : title,    
                author.isEmpty() ? null : author, 
                year,                               
                genre.isEmpty() ? null : genre);    
    }

    //Метод searchBooksMenu реализует меню поиска книг в библиотеке
    private static void searchBooksMenu() {
        System.out.println("\n--- Поиск книги ---");
        System.out.println("1. По названию");
        System.out.println("2. По автору");
        System.out.println("3. По жанру");
        System.out.println("4. По году издания");
        System.out.println("5. По id");
        System.out.print("Выберите атрибут для поиска: ");
        
        int attributeChoice;
        try {
            attributeChoice = Integer.parseInt(scanner.nextLine());
            //Проверка на валидность выбора атрибута, по которому будем производить поиск
            if (attributeChoice < 1 || attributeChoice > 5) {
                System.out.println("Неверный выбор атрибута");
                return;
            }
        } catch (NumberFormatException e) {
            //Обработка случая когда введено не число
            System.out.println("Ошибка: введите число от 1 до 5");
            return;
        }

        //Определение названия атрибута для сообщения пользователю
        String attributeName = switch (attributeChoice) {
            case 1 -> "названию";
            case 2 -> "автору";
            case 3 -> "жанру";
            case 4 -> "году издания";
            case 5 -> "id";
            default -> "";
        };

        System.out.print("Введите значение для поиска по " + attributeName + ": ");
        String searchTerm = scanner.nextLine().trim();

        //Проверка на то, что поисковый запрос не пустой
        if (searchTerm.isEmpty()) {
            System.out.println("Поисковый запрос не может быть пустым");
            return;
        }

        library.searchByAttribute(attributeChoice, searchTerm);
    }

    //Метод saveToFileMenu предоставляет меню сохранения текущего списка книг из библиотеки в файл
    private static void saveToFileMenu() {
        System.out.print("Введите имя файла для сохранения: ");
        String filename = scanner.nextLine().trim();
        //Проверка имени файла на валидность
        if (filename.isEmpty()) {
            System.out.println("Имя файла не может быть пустым");
            return;
        }
        library.saveToFile(filename);
    }

    //Метод loadFromFileMenu предоставляет меню загрузки библиотеки из файла
    private static void loadFromFileMenu() {
        System.out.print("Введите имя файла для загрузки: ");
        String filename = scanner.nextLine().trim();
        //Проверка валидности имени файла
        if (filename.isEmpty()) {
            System.out.println("Имя файла не может быть пустым");
            return;
        }
        library.loadFromFile(filename);
    }

    //Метод readIntInput является вспомогательным и осуществляет чтение целого числа с обработкой ошибок
    private static int readIntInput(String zapros) {
        while (true) {
            try {
                System.out.print(zapros);
                //Пытаемся преобразовать ввод в число
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                //Если преобразование не удалось - сообщаем об ошибке и повторяем цикл
                System.out.println("Ошибка: введите целое число");
            }
        }
    }

    //Вспомогательный метод readIntInputOrNull для чтения целого числа с возможностью пропуска
    private static Integer readIntInputOrNull(String zapros) {
        while (true) {
            try {
                System.out.print(zapros);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    return null;
                }

                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                //Если преобразование не удалось - сообщаем об ошибке и повторяем цикл
                System.out.println("Ошибка: введите целое число или оставьте пустым чтобы не менять");
            }
        }
    }
}