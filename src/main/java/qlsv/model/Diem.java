package qlsv.model;

public class Diem {
    private String maSV;
    private String hoTen;       // chỉ để hiển thị
    private String maMH;
    private String tenMH;       // chỉ để hiển thị
    private int hocKy;
    private String namHoc;
    private Double diemQuaTrinh;
    private Double diemThi;
    private Double diemTongKet; // cột tính toán trong CSDL, chỉ đọc

    public Diem(String maSV, String hoTen, String maMH, String tenMH, int hocKy, String namHoc,
                Double diemQuaTrinh, Double diemThi, Double diemTongKet) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.diemQuaTrinh = diemQuaTrinh;
        this.diemThi = diemThi;
        this.diemTongKet = diemTongKet;
    }

    public String getMaSV() { return maSV; }
    public String getHoTen() { return hoTen; }
    public String getMaMH() { return maMH; }
    public String getTenMH() { return tenMH; }
    public int getHocKy() { return hocKy; }
    public String getNamHoc() { return namHoc; }
    public Double getDiemQuaTrinh() { return diemQuaTrinh; }
    public Double getDiemThi() { return diemThi; }
    public Double getDiemTongKet() { return diemTongKet; }
}
