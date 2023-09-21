package com.example.dictionary;

public class Card {

    private int id;//在整个布局里算第几个Card
    private String sign;//发音
    private String location;//发音文件在服务器上的地址

    //无参构造函数
    public Card() {

    }

    //有参构造函数
    public Card(int id, String sign, String location) {
        this.id = id;
        this.sign = sign;
        this.location = location;
    }

    //Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}


