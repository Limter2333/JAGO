package paper.po;

import java.util.*;

/**
 * 问卷PO
 * @author Sheven
 */
public class QuestionnaireInfo {

    /**
     * 问卷题目
     */
    private String title;

    /**
     * 题目分组数
     */
    private Integer groupNum;

    /**
     * 题目列表
     */
    private List<QuestionPo> questionList;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(Integer groupNum) {
        this.groupNum = groupNum;
    }

    public List<QuestionPo> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuestionPo> questionList) {
        this.questionList = questionList;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
