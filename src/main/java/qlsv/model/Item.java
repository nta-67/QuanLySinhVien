package qlsv.model;

/** Phần tử hiển thị trong ComboBox: lưu mã, hiển thị "mã - tên". */
public class Item {
    private final String ma;
    private final String ten;

    public Item(String ma, String ten) {
        this.ma = ma;
        this.ten = ten;
    }

    public String getMa() {
        return ma;
    }

    @Override
    public String toString() {
        return ten == null || ten.isEmpty() ? ma : ma + " - " + ten;
    }
}
