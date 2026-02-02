package paper.enums;

public enum SelectionTypeEnum {

    // 0点击a标签 1点击div
    TYPE_A(0, "A", "点击a标签"),
    TYPE_B(1, "B", "点击div");

    private int id;

    private String name;

    private String discription;

    SelectionTypeEnum(int id, String name, String discription) {
        this.id = id;
        this.name = name;
        this.discription = discription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }
}
