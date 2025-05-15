package com.mehmetbirolgolge.notuygulamasi.model;

import java.util.Date;

public class Note {
    private int id;
    private String title;
    private String content;
    private String processedContent;
    private Date creationDate;
    private Date modificationDate;
    private boolean processedByAI;

    public Note() {
        this.creationDate = new Date();
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.creationDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProcessedContent() {
        return processedContent;
    }

    public void setProcessedContent(String processedContent) {
        this.processedContent = processedContent;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public boolean isProcessedByAI() {
        return processedByAI;
    }

    public void setProcessedByAI(boolean processedByAI) {
        this.processedByAI = processedByAI;
    }
}