import java.util.*;

class Product {
    String name;
    double price;
    int quantity;

    Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}

class Cart {
    Product product;
    int quantity;

    Cart(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}

public class OnlineShoppingSystem {
    private static List<Product> productCatalog = new ArrayList<>();
    private static List<Cart> cart = new ArrayList<>();

    public static void main(String[] args) {
        productCatalog.add(new Product("Laptop", 500.0, 10));
        productCatalog.add(new Product("Smartphone", 300.0, 15));
        productCatalog.add(new Product("Headphones", 50.0, 20));
        productCatalog.add(new Product("Smartwatch", 100.0, 8));
        productCatalog.add(new Product("Keyboard", 30.0, 25));

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            printBox("--- Online Shopping Menu ---");
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. Checkout");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewProducts();
                    break;
                case 2:
                    addToCart(scanner);
                    break;
                case 3:
                    checkout(scanner);
                    break;
                case 4:
                    exit = true;
                    printBox("Thank you for shopping with us!");
                    break;
                default:
                    printBox("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void viewProducts() {
        printBox("--- Product Catalog ---");
        System.out.printf("%-15s%-10s%-10s\n", "Name", "Price", "Quantity");
        System.out.println("----------------------------------");
        for (Product product : productCatalog) {
            System.out.printf("%-15s%-10.2f%-10d\n", product.name, product.price, product.quantity);
        }
    }

    private static void addToCart(Scanner scanner) {
        System.out.print("Enter the product name to add to cart: ");
        String productName = scanner.nextLine();
        boolean productFound = false;

        for (Product product : productCatalog) {
            if (product.name.equalsIgnoreCase(productName)) {
                productFound = true;
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (quantity <= product.quantity) {
                    cart.add(new Cart(product, quantity));
                    printBox(quantity + " " + product.name + "(s) added to cart.");
                } else {
                    printBox("Insufficient stock available.");
                }
                break;
            }
        }

        if (!productFound) {
            printBox("Product not found in catalog.");
        }
    }

    private static void checkout(Scanner scanner) {
        if (cart.isEmpty()) {
            printBox("Your cart is empty. Add items to checkout.");
            return;
        }

        double total = 0;
        printBox("--- Cart Summary ---");
        System.out.printf("%-15s%-10s%-10s\n", "Name", "Price", "Quantity");
        System.out.println("----------------------------------");
        for (Cart cartItem : cart) {
            System.out.printf("%-15s%-10.2f%-10d\n", cartItem.product.name, cartItem.product.price, cartItem.quantity);
            total += cartItem.product.price * cartItem.quantity;
        }
        System.out.printf("\nTotal Amount: %.2f\n", total);

        boolean paymentSuccessful = false;
        while (!paymentSuccessful) {
            System.out.print("Enter your 16-digit card number: ");
            String cardNumber = scanner.nextLine();

            try {
                validateCardNumber(cardNumber);
                printBox("Payment successful! Thank you for your purchase.");
                updateStock();
                cart.clear();
                paymentSuccessful = true;
            } catch (Exception e) {
                printBox(e.getMessage());
            }
        }
    }

    private static void validateCardNumber(String cardNumber) throws Exception {
        if (cardNumber.length() != 16 || !cardNumber.matches("\\d{16}")) {
            throw new Exception("Invalid card number. Please try again.");
        }
    }

    private static void updateStock() {
        for (Cart cartItem : cart) {
            cartItem.product.quantity -= cartItem.quantity;
        }
        printBox("Stock updated successfully.");
    }

    private static void printBox(String message) {
        int length = message.length();
        System.out.println("+" + "-".repeat(length + 2) + "+");
        System.out.println("| " + message + " |");
        System.out.println("+" + "-".repeat(length + 2) + "+");
    }
}
