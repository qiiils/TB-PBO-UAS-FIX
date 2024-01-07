public class Produk extends Barang implements IProduk {
    public Produk(int idProduk, String nama, double harga) {
        super(idProduk, nama, harga);
    }

    public int getIdProduk() {
        return idBarang; // Assuming getIdProduk is equivalent to getIdBarang
    }

    @Override
    public double getHarga() {
        return super.getHarga();
    }

    @Override
    public void tampilkanInfo() {
        System.out.println("ID Produk: " + getIdBarang() + ", Nama: " + getNama() + ", Harga: Rp " + getHarga());
    }
}
