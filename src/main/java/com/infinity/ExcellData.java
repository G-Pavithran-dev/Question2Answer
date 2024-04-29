package com.infinity;

public class ExcellData {
    
    private int serialNo;
    private String question;
    private int marks;
    private String additionalInfo;
    public ExcellData(int serialNo,String question,int marks,String additionalInfo)
    {
        this.serialNo=serialNo;
        this.question=question;
        this.marks=marks;
        this.additionalInfo=additionalInfo;
    }
    public int getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public int getMarks() {
        return marks;
    }
    public void setMarks(int marks) {
        this.marks = marks;
    }
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
}
