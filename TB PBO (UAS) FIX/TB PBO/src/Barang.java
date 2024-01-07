public abstract class Barang {
    protected int idBarang;
    protected String nama;
    protected double harga;

    public Barang(int idBarang, String nama, double harga) {
        this.idBarang = idBarang;
        this.nama = nama;
        this.harga = harga;
    }

    public abstract void tampilkanInfo();

    public int getIdBarang() {
        return idBarang;
    }

    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }
}
