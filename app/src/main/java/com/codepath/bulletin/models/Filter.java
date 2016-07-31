package com.codepath.bulletin.models;

import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.ParseException;

/**
 * Created by seetha on 7/30/16.
 */
public class Filter{

    public String beginDate;
    public String sortBy;
    public boolean newsdeskArts;
    public boolean newsdeskFashionStyle;
    public boolean newsdeskSports;
    public boolean isFirstCall = true;

    public boolean isFirstCall() {
        return isFirstCall;
    }


    private static Filter instance = null;

    public static synchronized Filter getInstance() {
        if (instance == null ) {
            instance = new Filter(false);

        }
        return instance;
    }

    protected Filter(boolean isFirstCall){
        isFirstCall = false;
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

    public String getFilteredQuery(String searchText, int page) {
        //begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=227c750bb7714fc39ef1559ef1bd8329
        String filteredQuery = "";

        if (searchText!=null) {
            filteredQuery = filteredQuery + "&q=" + searchText;
        }
        if (beginDate!=null){
            filteredQuery = filteredQuery + "&begin_date=" + getBeginDateFormatted();
        }
        if (sortBy!=null){
            filteredQuery = filteredQuery + "&sort=" + getSortByFormatted();
        }
        if (isNewsdeskArts() || isNewsdeskFashionStyle() || isNewsdeskSports()){
            filteredQuery = filteredQuery + "&fq=news_desk:" + getNewsDeskFormatted();
        }
        //add page num
        if (page!= 0) {
            filteredQuery = filteredQuery + "&page=" + page;
        }

        System.out.println("DEBUGGY FILTERED QUERY: " + filteredQuery);
        return filteredQuery;
    }

    public String getBeginDateFormatted() {
       String reformattedDate="yyyyMMdd";
        System.out.println("DEBUGGY date original: " + getBeginDate());

        if (getBeginDate()!=null) {
            try {
                SimpleDateFormat oldFormat = new SimpleDateFormat("MM-dd-yy");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

                reformattedDate = formatter.format(oldFormat.parse(getBeginDate()));

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            System.out.println("DEBUGGY date: " + reformattedDate);

        }
        return reformattedDate;
    }

    public String getSortByFormatted() {
        if (sortBy !=null){
            return sortBy.toLowerCase();
        }
        return "oldest";
    }

    public String getNewsDeskFormatted() {
        String formattedNewsDesk = "(";

        if (isNewsdeskArts()){
            formattedNewsDesk = formattedNewsDesk + "%22Arts%22";
        }
        if (isNewsdeskFashionStyle()){
            formattedNewsDesk = formattedNewsDesk + "%20%22Fashion%20&%20Style%22";
        }
        if (isNewsdeskSports()){
            formattedNewsDesk = formattedNewsDesk + "%20%22Sports%22";
        }
        return formattedNewsDesk + ")";
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
