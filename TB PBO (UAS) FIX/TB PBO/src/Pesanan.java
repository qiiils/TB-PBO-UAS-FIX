import java.time.LocalDateTime;

public class Pesanan implements Pembayaran {
    private int idPesanan;
    private Barang produk;
    private int jumlah;
    private LocalDateTime waktuTransaksi;

    public Pesanan(int idPesanan, Barang produk, int jumlah) {
        this.idPesanan = idPesanan;
        this.produk = produk;
        this.jumlah = jumlah;
        this.waktuTransaksi = LocalDateTime.now();
    }

    public int getIdPesanan() {
        return idPesanan;
    }

    public int getJumlah() {
        return jumlah;
    }

    public Barang getProduk() {
        return produk;
    }

    // Tambahkan implementasi metode tampilkanInfoPesanan
    public void tampilkanInfoPesanan() {
        System.out.println("ID Pesanan: " + idPesanan);
    
        if (produk instanceof Produk) {
            Produk produkPesanan = (Produk) produk;
            System.out.println("ID Produk: " + produkPesanan.getIdProduk() + ", Nama: " + produkPesanan.getNama() + ", Harga: Rp " + produkPesanan.getHarga());
        } else {
            // Handle the case where the Barang is not an instance of Produk
            System.out.println("ID Produk: N/A, Nama: N/A, Harga: N/A");
        }
    
        System.out.println("Jumlah: " + jumlah);
        System.out.println("Waktu Transaksi: " + waktuTransaksi);
    }
    

    // Tambahkan implementasi metode setJumlah
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    // Tambahkan implementasi metode getWaktuTransaksi
    public LocalDateTime getWaktuTransaksi() {
        return waktuTransaksi;
    }

    @Override
    public double hitungTotalPembayaran() {
        return jumlah * produk.getHarga();
    }

    @Override
    public void prosesPembayaran(double jumlahPembayaran) {
        double totalPembayaran = hitungTotalPembayaran();

        if (jumlahPembayaran >= totalPembayaran) {
            double kembalian = jumlahPembayaran - totalPembayaran;
            System.out.println("Pembayaran berhasil!");
            System.out.printf("Total bayar: Rp %.2f%n", totalPembayaran);
            System.out.printf("Kembalian: Rp %.2f%n", kembalian);
            System.out.println("Terima kasih telah berbelanja!");
        } else {
            System.out.println("Pembayaran tidak mencukupi. Silakan coba lagi.");
        }
    }
}
