import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AplikasiMerchandise {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/merchandise";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "";

    public static void main(String[] args) {
        try {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                List<Barang> daftarBarang = new ArrayList<>();
                daftarBarang.add(new Produk(1, "Gelang", 20000.0));
                daftarBarang.add(new Produk(2, "Ganci", 10000.0));
                daftarBarang.add(new Produk(3, "Stiker", 5000.0));

                List<Pesanan> daftarPesanan = new ArrayList<>();

                Pengguna pengguna = new Pengguna("aqila", "17");

                Scanner scanner = new Scanner(System.in);
                boolean loggedIn = false;

                while (!loggedIn) {
                    System.out.print("Masukkan username: ");
                    String inputUsername = scanner.nextLine();
                    System.out.print("Masukkan password: ");
                    String inputPassword = scanner.nextLine();

                    if (pengguna.autentikasi(inputUsername, inputPassword)) {
                        loggedIn = true;
                        System.out.println("Login berhasil!");
                    } else {
                        System.out.println("Autentikasi gagal. Coba lagi.");
                    }
                }

                int pilihan;
                do {
                    System.out.println("\nMenu:");
                    System.out.println("1. Tampilkan Barang");
                    System.out.println("2. Tambah Pesanan");
                    System.out.println("3. Tampilkan Pesanan");
                    System.out.println("4. Edit Pesanan");
                    System.out.println("5. Hapus Pesanan");
                    System.out.println("6. Keluar");
                    System.out.print("Masukkan pilihan Anda: ");
                    pilihan = scanner.nextInt();
                    scanner.nextLine();

                    switch (pilihan) {
                        case 1:
                            tampilkanBarang(daftarBarang);
                            break;
                        case 2:
                            if (loggedIn) {
                                tambahPesanan(daftarBarang, daftarPesanan, scanner, connection);
                            } else {
                                System.out.println("Silakan login terlebih dahulu.");
                            }
                            break;
                        case 3:
                            tampilkanPesanan(daftarPesanan);
                            break;
                        case 4:
                            if (loggedIn) {
                                editPesanan(daftarPesanan, scanner, connection);
                            } else {
                                System.out.println("Silakan login terlebih dahulu.");
                            }
                            break;
                        case 5:
                            if (loggedIn) {
                                hapusPesanan(daftarPesanan, scanner, connection);
                            } else {
                                System.out.println("Silakan login terlebih dahulu.");
                            }
                            break;
                    }
                } while (pilihan != 6);

            } catch (SQLException e) {
                System.err.println("Error JDBC: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void tampilkanBarang(List<Barang> daftarBarang) {
        System.out.println("Daftar Barang:");
        for (Barang barang : daftarBarang) {
            barang.tampilkanInfo();
        }
    }

    private static void tampilkanPesanan(List<Pesanan> daftarPesanan) {
        System.out.println("Daftar Pesanan:");

        for (Pesanan pesanan : daftarPesanan) {
            pesanan.tampilkanInfoPesanan();
            tampilkanGarisPemisah();
        }
    }

    private static void tampilkanGarisPemisah() {
        System.out.println("-------------------------------------");
    }

    private static void tambahPesanan(List<Barang> daftarBarang, List<Pesanan> daftarPesanan, Scanner scanner,
            Connection connection) {
        try {
            tampilkanBarang(daftarBarang);
            System.out.print("Masukkan ID Barang yang akan dipesan: ");
            int idBarang = scanner.nextInt();
            scanner.nextLine();

            Barang barangDipesan = temukanBarangBerdasarkanID(daftarBarang, idBarang);

            if (barangDipesan != null) {
                System.out.print("Masukkan jumlah pesanan: ");
                int jumlahPesanan = scanner.nextInt();
                scanner.nextLine();

                // Mendapatkan ID Pesanan terbaru dari database
                int idPesananTerbaru = getIdPesananTerbaru(connection);

                // Membuat pesanan baru dengan ID yang sesuai
                Pesanan pesananBaru = new Pesanan(idPesananTerbaru + 1, (Produk) barangDipesan, jumlahPesanan);
                daftarPesanan.add(pesananBaru);

                // Menyimpan pesanan ke dalam database
                simpanPesananKeDatabase(pesananBaru, connection);

                // Menampilkan struk transaksi dan proses pembayaran
                tampilkanStrukTransaksi(pesananBaru);
                prosesPembayaran(pesananBaru, scanner);

            } else {
                System.out.println("ID Barang tidak valid.");
            }

        } catch (SQLException e) {
            System.err.println("Error JDBC: " + e.getMessage());
        }
    }

    // Metode untuk mendapatkan ID Pesanan terbaru dari database
    private static int getIdPesananTerbaru(Connection connection) throws SQLException {
        int idPesananTerbaru = 0;

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT MAX(id_pesanan) FROM pesanan")) {
            if (resultSet.next()) {
                idPesananTerbaru = resultSet.getInt(1);
            }
        }

        return idPesananTerbaru;
    }

    private static void prosesPembayaran(Pesanan pesanan, Scanner scanner) {
        double totalBayar = pesanan.hitungTotalPembayaran();
        System.out.printf("Total bayar: Rp %.2f%n", totalBayar);

        System.out.print("Masukkan jumlah pembayaran: ");
        double jumlahPembayaran = scanner.nextDouble();
        scanner.nextLine(); // Konsumsi karakter newline

        pesanan.prosesPembayaran(jumlahPembayaran);
    }

    private static void editPesanan(List<Pesanan> daftarPesanan, Scanner scanner, Connection connection) {
        try {
            tampilkanPesanan(daftarPesanan);
            System.out.print("Masukkan ID Pesanan yang akan diedit: ");
            int idPesanan = scanner.nextInt();
            scanner.nextLine();

            Pesanan pesananYangAkanDiEdit = temukanPesananBerdasarkanID(daftarPesanan, idPesanan);

            if (pesananYangAkanDiEdit != null) {
                System.out.print("Masukkan jumlah baru: ");
                int jumlahBaru = scanner.nextInt();
                scanner.nextLine();

                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE pesanan SET jumlah = ? WHERE id_pesanan = ?")) {
                    statement.setInt(1, jumlahBaru);
                    statement.setInt(2, idPesanan);

                    int affectedRows = statement.executeUpdate();

                    if (affectedRows > 0) {
                        pesananYangAkanDiEdit.setJumlah(jumlahBaru);
                        System.out.println("Pesanan berhasil diedit!");
                    }
                }

            } else {
                System.out.println("ID Pesanan tidak valid.");
            }

        } catch (SQLException e) {
            System.err.println("Error JDBC: " + e.getMessage());
        }
    }

    private static void hapusPesanan(List<Pesanan> daftarPesanan, Scanner scanner, Connection connection) {
        try {
            tampilkanPesanan(daftarPesanan);
            System.out.print("Masukkan ID Pesanan yang akan dihapus: ");
            int idPesanan = scanner.nextInt();
            scanner.nextLine();

            Pesanan pesananYangAkanDihapus = temukanPesananBerdasarkanID(daftarPesanan, idPesanan);

            if (pesananYangAkanDihapus != null) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM pesanan WHERE id_pesanan = ?")) {
                    statement.setInt(1, idPesanan);

                    int affectedRows = statement.executeUpdate();

                    if (affectedRows > 0) {
                        daftarPesanan.remove(pesananYangAkanDihapus);
                        System.out.println("Pesanan berhasil dihapus!");
                    }
                }

            } else {
                System.out.println("ID Pesanan tidak valid.");
            }

        } catch (SQLException e) {
            System.err.println("Error JDBC: " + e.getMessage());
        }
    }

    private static void tampilkanStrukTransaksi(Pesanan pesanan) {
        System.out.println("\n===== Struk Transaksi =====");
        pesanan.tampilkanInfoPesanan();
        System.out.println("Waktu Transaksi: " + pesanan.getWaktuTransaksi());
        System.out.println("============================\n");
    }

    private static Barang temukanBarangBerdasarkanID(List<Barang> daftarBarang, int idBarang) {
        for (Barang barang : daftarBarang) {
            if (barang instanceof Produk && ((Produk) barang).getIdProduk() == idBarang) {
                return barang;
            }
        }
        return null;
    }

    private static Pesanan temukanPesananBerdasarkanID(List<Pesanan> daftarPesanan, int idPesanan) {
        for (Pesanan pesanan : daftarPesanan) {
            if (pesanan.getIdPesanan() == idPesanan) {
                return pesanan;
            }
        }
        return null;
    }

    private static void simpanPesananKeDatabase(Pesanan pesanan, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO pesanan (id_pesanan, id_produk, jumlah, waktu_transaksi) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, pesanan.getIdPesanan());
            statement.setInt(2, ((Produk) pesanan.getProduk()).getIdProduk());
            statement.setInt(3, pesanan.getJumlah());
            statement.setTimestamp(4, Timestamp.valueOf(pesanan.getWaktuTransaksi()));

            statement.executeUpdate();
        }
    }

}
