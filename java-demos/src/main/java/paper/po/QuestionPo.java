package paper.po;

import java.util.Arrays;
import java.util.List;

/**
 * 问题PO
 *
 * @author Sheven
 */
public class QuestionPo {
    /**
     * 题目序号
     */
    private String questSeq;

    /**
     * 题目名
     */
    private String questName;

    /**
     * 题目选项数
     */
    private Integer optionNum;

    /**
     * 选项
     */
    private List<String> options;

    /**
     * 题目选项权重 总和为100
     */
    private Integer[] optionWeight;

    /**
     * 题目类型 0单选 1多选
     */
    private Integer questType;

    /**
     * 最大选择数
     */
    private Integer maxSelectNum;

    /**
     * 选择方式 0点击a标签 1点击div
     */
    private Integer selectionType;

    /**
     * 校验权重数量是否和题目数量相同
     *
     * @return 相等为true
     */
    public boolean judgeWeightSum() {
        if (this.optionNum.equals(this.optionWeight.length)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "QuestionPo{" +
                "序号：'" + questSeq + '\'' +
                ", 选项数：" + optionNum +
                ", 权重：" + Arrays.toString(optionWeight) +
                '}';
    }

    public QuestionPo(String questSeq, Integer optionNum, Integer[] optionWeight, Integer questType, Integer maxSelectNum, Integer selectionType) {
        this.questSeq = questSeq;
        this.optionNum = optionNum;
        this.optionWeight = optionWeight;
        this.questType = questType;
        this.maxSelectNum = maxSelectNum;
        this.selectionType = selectionType;
    }

    public String getQuestSeq() {
        return questSeq;
    }

    public void setQuestSeq(String questSeq) {
        this.questSeq = questSeq;
    }

    public String getQuestName() {
        return questName;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public Integer getOptionNum() {
        return optionNum;
    }

    public void setOptionNum(Integer optionNum) {
        this.optionNum = optionNum;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer[] getOptionWeight() {
        return optionWeight;
    }

    public void setOptionWeight(Integer[] optionWeight) {
        this.optionWeight = optionWeight;
    }

    public Integer getQuestType() {
        return questType;
    }

    public void setQuestType(Integer questType) {
        this.questType = questType;
    }

    public Integer getMaxSelectNum() {
        return maxSelectNum;
    }

    public void setMaxSelectNum(Integer maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
    }

    public Integer getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(Integer selectionType) {
        this.selectionType = selectionType;
    }
}
