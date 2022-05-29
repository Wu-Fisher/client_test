package test;

import java.io.Serializable;
import java.util.Calendar;

public class RankListData implements Serializable, Comparable {

    private int id;
    private int score;
    private String name;
    private Calendar date;

    public RankListData(int id, int score, String name, Calendar date) {
        this.id = id;
        this.score = score;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    @Override
    public int compareTo(Object o) {
        return this.score - ((RankListData) o).getScore();
    }

    @Override
    public String toString() {
        return "id=" + id + ",score=" + score + ",name=" + name + ",date=" + date.toString();
    }
}
