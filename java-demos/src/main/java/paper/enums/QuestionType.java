package paper.enums;

public enum QuestionType {
    // 0单选 1多选
    SINGLE_SELECTION(0, "SINGLE_SELECTION", "单选"),
    MULTIPLE_SELECTION(1, "MULTIPLE_SELECTION", "多选");

    private int id;

    private String name;

    private String discription;

    QuestionType(int id, String name, String discription) {
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
