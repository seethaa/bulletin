package com.codepath.bulletin.models;

import org.parceler.Parcel;

/**
 * Created by seetha on 7/30/16.
 */
@Parcel
public class Filter{

    public String beginDate;
    public String sortBy;
    public boolean newsdeskArts;
    public boolean newsdeskFashionStyle;
    public boolean newsdeskSports;

    private static Filter instance = null;

    public static synchronized Filter getInstance() {
        if (instance == null ) {
            instance = new Filter();

        }
        return instance;
    }

    protected Filter(){
        //empty constructor
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getSortBy() {
        return sortBy;
    }

    public boolean isNewsdeskArts() {
        return newsdeskArts;
    }

    public boolean isNewsdeskFashionStyle() {
        return newsdeskFashionStyle;
    }

    public boolean isNewsdeskSports() {
        return newsdeskSports;
    }


    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }



    public void setNewsdeskArts(boolean newsdeskArts) {
        this.newsdeskArts = newsdeskArts;
    }

    public void setNewsdeskFashionStyle(boolean newsdeskFashionStyle) {
        this.newsdeskFashionStyle = newsdeskFashionStyle;
    }

    public void setNewsdeskSports(boolean newsdeskSports) {
        this.newsdeskSports = newsdeskSports;
    }



//    public Filter(String beginDate, String sortBy, String newsDeskValues, boolean newsdeskArts, boolean newsdeskFashionStyle, boolean newsdeskSports){
//        super();
//        this.beginDate = beginDate;
//        this.sortBy = sortBy;
//        this.newsdeskArts = newsdeskArts;
//        this.newsdeskFashionStyle = newsdeskFashionStyle;
//        this.newsdeskSports = newsdeskSports;
//    }


}
