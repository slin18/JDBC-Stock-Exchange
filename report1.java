/*
 * Alex Nguyen, Sam Lin
 * */
import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.io.File;
import java.io.FileInputStream;

public class report1 {
	private static Connection conn;
	public static void main(String[] args){
		BufferedWriter bw = null;
	    FileWriter fw = null;
	    FileInputStream stream = null;
	    BufferedReader reader = null;
        
		try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (Exception ex)
        {
            System.out.println("Driver not found");
            System.out.println(ex);
        };
        String url = "jdbc:mysql://cslvm74.csc.calpoly.edu/nyse";
        conn = null;
        try { 
            stream = new FileInputStream("credentials.in");
            reader = new BufferedReader(new InputStreamReader(stream));
            String user = reader.readLine();
            String pass = reader.readLine();
            conn = DriverManager.getConnection(url, user, pass);
            stream.close();
            reader.close();
        }
        catch (Exception ex)
        {
                  System.out.println("Could not open connection");
                  System.out.println(ex);
        }
        Scanner sc = new Scanner(System.in); 
        System.out.println("Enter stock ticker: "); 
        String ticker = sc.next();
        try {
          fw = new FileWriter(ticker + ".html");
          bw = new BufferedWriter(fw);
          String htmlPage = "<html><body style=Õbackground-color:#cccÕ>"
              + "<b><h1><center><u>NYSE Analytics</u></center>"
              + "</h1></b>";

            bw.write(htmlPage);
            bw.write("<h3><b><center> Sam Lin & Alex Nguyen </center> </h3></b>");
            System.out.println("HTML MADE");
    		bw.write("<html><b><h2><center>General Stock Market Analytical Data<center></h2></b>");

          } catch(IOException e) {e.printStackTrace();} 

        //Q1 # of securities traded at start of 2016
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 1</b></h3>");
	    	Statement s1 = conn.createStatement(); 
	    	ResultSet result = s1.executeQuery("SELECT COUNT(*) "
	    			+ "FROM Prices "
	    			+ "WHERE Year(Day) = 2016 AND"
	    			+ " Day <= ALL(SELECT MIN(Day) as Day FROM Securities S , Prices P WHERE Year(Day) = 2016 AND Month(Day) = 1 AND S.Ticker = P.Ticker "
	    			+ "GROUP BY S.Ticker)"
	    			+ " GROUP BY Day"); 
	    	boolean f = result.next(); 
	    	while (f)
	        {
	          String s = result.getString(1);
	          bw.write("<html><p style=\"padding-left:220px\">" + s + "   securities traded at the start of 2016 </p>"); 
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
      //Q1 # of securities traded at end of 2016
        try {
	    	Statement s1 = conn.createStatement(); 
	    	ResultSet result = s1.executeQuery("SELECT COUNT(*) "
	    			+ "FROM Prices "
	    			+ "WHERE Year(Day) = 2016 AND"
	    			+ " Day >= ALL(SELECT MAX(Day) as Day FROM Securities S , Prices P WHERE Year(Day) = 2016 AND Month(Day) = 12 AND S.Ticker = P.Ticker "
	    			+ "GROUP BY S.Ticker)"
	    			+ " GROUP BY Day"); 
	    	boolean f = result.next(); 
	    	while (f)
	        {
	          String s = result.getString(1);
	          bw.write("<html><p style=\"padding-left:220px\">" + s + "   securities traded at the end of 2016 </p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
       //Q1 # of securities price increase between Dec 2015 - Dec 2016
        try {
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT COUNT(*) as SecIncr FROM (SELECT S.Ticker AS Ticker, Close FROM Securities S , AdjustedPrices P WHERE Year(Day) = 2016 AND Day >= ALL(SELECT MAX(Day) as Day FROM Securities S , Prices P WHERE Year(Day) = 2016 AND S.Ticker = P.Ticker GROUP BY S.Ticker) AND S.Ticker = P.Ticker GROUP BY S.Ticker) A, "
	    			+ "(SELECT S.Ticker AS Ticker, Close FROM Securities S , AdjustedPrices P WHERE Year(Day) = 2015 AND Day >= ALL(SELECT MAX(Day) as Day FROM Securities S , Prices P WHERE Year(Day) = 2015 AND S.Ticker = P.Ticker GROUP BY S.Ticker) AND S.Ticker = P.Ticker GROUP BY S.Ticker) B WHERE SIGN(A.Close-B.Close)=1 AND A.Ticker = B.Ticker");
	    	boolean f = result.next(); 
	    	while (f)
	        {
	          String s = result.getString(1);
	          bw.write("<html><p style=\"padding-left:220px\">" + s + "   security price increases between end the 2015 and the end 2016</p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        //Q1 # of securities price increase between Dec 2015 - Dec 2016
        try {
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT COUNT(*) as SecIncr FROM (SELECT S.Ticker AS Ticker, Close FROM Securities S , AdjustedPrices P WHERE Year(Day) = 2016 AND Day >= ALL(SELECT MAX(Day) as Day FROM Securities S , Prices P WHERE Year(Day) = 2016 AND S.Ticker = P.Ticker GROUP BY S.Ticker) AND S.Ticker = P.Ticker GROUP BY S.Ticker) A, "
	    			+ "(SELECT S.Ticker AS Ticker, Close FROM Securities S , AdjustedPrices P WHERE Year(Day) = 2015 AND Day >= ALL(SELECT MAX(Day) as Day FROM Securities S , Prices P WHERE Year(Day) = 2015 AND S.Ticker = P.Ticker GROUP BY S.Ticker) AND S.Ticker = P.Ticker GROUP BY S.Ticker) B WHERE SIGN(B.Close-A.Close)=1 AND A.Ticker = B.Ticker");
	    	boolean f = result.next(); 
	    	while (f)
	        {
	          String s = result.getString(1);
	          bw.write("<p style=\"padding-left:220px\">" + s + "   security price decreases between the end of 2015 and the end of 2016 </p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        // Q2 Top 10 most traded
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 2</b></h3>");
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT S.Ticker AS Ticker, SUM(Volume) as SUM FROM Securities S , Prices P WHERE Year(Day) = 2016 AND S.Ticker = P.Ticker GROUP BY S.Ticker ORDER BY SUM(Volume) DESC LIMIT 10");
	    	boolean f = result.next(); 
	    	while (f)
	        {
	          String s = result.getString(1);
	          String a = result.getString(2);
	          bw.write("<p style=\"padding-left:220px\">Stock ticker:   " + s + "     Volume:   " + a + "</p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        // Q3 
        try { 
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 3</b></h3>");
            Statement s4 = conn.createStatement();
            ResultSet result = s4.executeQuery("(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg),2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2010 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg),2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2011 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg),2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2012 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg),2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2013 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg),2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2014 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg),2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2015 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg),2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2016 ORDER BY Year, Absolute DESC LIMIT 5)");
            boolean f = result.next(); 
            while (f)
            {
              String s = result.getString(1);
              String a = result.getString(2);
              String c = result.getString(3);
              bw.write("<p style=\"padding-left:220px\">Year:   "+a+"     Ticker:   "+ s + "     Absolute Price Increase:   " + c + "</p>");
              f=result.next();
              }

                     
            }  catch (Exception ee) {System.out.println(ee);}
        // Q3 Relative
        try { 
            Statement s4 = conn.createStatement();
            ResultSet result = s4.executeQuery("(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg)/B.Avg*100,2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2010 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg)/B.Avg*100,2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2011 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg)/B.Avg*100,2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2012 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg)/B.Avg*100,2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2013 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg)/B.Avg*100,2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2014 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg)/B.Avg*100,2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2015 ORDER BY Year, Absolute DESC LIMIT 5) UNION "
            		+ "(SELECT A.Ticker as Ticker, A.Year AS Year, ROUND((A.Avg-B.Avg)/B.Avg*100,2) as Absolute FROM (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 12 GROUP BY S.Ticker, Year(Day), Month(Day)) A, (SELECT S.Ticker as Ticker, Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Month(Day) = 1 GROUP BY S.Ticker, Year(Day), Month(Day)) B WHERE A.Year = B.Year AND A.Ticker = B.Ticker AND A.Year = 2016 ORDER BY Year, Absolute DESC LIMIT 5)");
            boolean f = result.next(); 
            while (f)
            {
              String s = result.getString(1);
              String a = result.getString(2);
              String c = result.getString(3);
              bw.write("<p style=\"padding-left:220px\">Year: "+a+"     Ticker:   "+ s + "     Relative Price Increase:   " + c + "</p>");
              f=result.next();
              }
        }  catch (Exception ee) {System.out.println(ee);}
        
        //Q4
        try { 
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 4</b></h3>");
            bw.write("<p style=\"padding-left:220px\">Top 10 stocks to watch in 2017</p>");
            bw.write("<p style=\"padding-left:220px\">Ticker: </p>");
            Statement s4 = conn.createStatement();
            ResultSet result = s4.executeQuery("SELECT AvA.Ticker, ROUND(((AvA.AvgAbsolute*0.4 )+ (AvR.AvgRelative * 0.6 ))/2,2)as Avg FROM (SELECT Rel.Ticker as Ticker, ROUND(Avg(Rel.Relative),5)as AvgRelative FROM (SELECT A.Ticker as Ticker, A.Month, (A.Avg - B.Avg)/B.Avg * 100 as Relative FROM (SELECT Ticker, Month(Day) as Month, ROUND(Avg(Close),2) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 GROUP BY Ticker, Month(Day)) A, (SELECT Ticker, Month(Day) as Month, ROUND(Avg(Close),2) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 GROUP BY Ticker, Month(Day)) B WHERE A.Month = B.Month+1 AND A.Ticker = B.Ticker) Rel GROUP BY Rel.Ticker ORDER BY AvgRelative DESC ) AvR, (SELECT Abs.Ticker as Ticker, ROUND(Avg(Abs.Absolute),5)as AvgAbsolute FROM (SELECT A.Ticker as Ticker, A.Month, (A.Avg - B.Avg) as Absolute FROM (SELECT Ticker, Month(Day) as Month, ROUND(Avg(Close),2) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 GROUP BY Ticker, Month(Day)) A, (SELECT Ticker, Month(Day) as Month, ROUND(Avg(Close),2) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 GROUP BY Ticker, Month(Day)) B WHERE A.Month = B.Month+1 AND A.Ticker = B.Ticker) Abs GROUP BY Abs.Ticker ORDER BY AvgAbsolute DESC ) AvA WHERE AvA.Ticker = AvR.Ticker ORDER BY Avg DESC LIMIT 10 ");
            boolean f = result.next(); 
            while (f)
            {
              String s = result.getString(1);
              bw.write("<p style=\"padding-left:220px\">" + s + "</p>" );
              f=result.next();
              }
        }  catch (Exception ee) {System.out.println(ee);}
       
        //Q5
        try { 
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 5</b></h3>");
        	bw.write("<p style=\"padding-left:220px\">General Assessment</p>");
            Statement s4 = conn.createStatement();
            ResultSet result = s4.executeQuery("SELECT A.Sector, CASE WHEN Avg(ROUND((A.Avg-B.Avg)/B.Avg* 100,2)) > 3 THEN 'Significantly Outperforming' WHEN Avg(ROUND((A.Avg-B.Avg)/B.Avg* 100,2)) < -3 THEN 'Tanking' WHEN Avg(ROUND((A.Avg-B.Avg)/B.Avg* 100,2)) >= 0 AND Avg(ROUND((A.Avg-B.Avg)/B.Avg* 100,2)) < 3 THEN 'Growingish' ELSE 'losingish' END AS GeneralAssessment FROM (SELECT Sector, Month(Day) as Month, ROUND(Avg(Close),2) as Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Year(Day) = 2016 AND Sector != 'Telecommunications Services' GROUP BY Sector, Month(Day)) A, (SELECT Sector, Month(Day) as Month, ROUND(Avg(Close),2) AS Avg FROM Securities S, AdjustedPrices P WHERE S.Ticker = P.Ticker AND Year(Day) = 2016 AND Sector != 'Telecommunications Services' GROUP BY Sector, Month(Day)) B WHERE A.Month = B.Month + 1 AND A.Sector = B.Sector GROUP BY A.Sector"); 
            boolean f = result.next(); 
            while (f)
            {
              String s = result.getString(1);
              String a = result.getString(2);
              bw.write("<p style=\"padding-left:220px\">Sector:   " + s + ",  " + a + "</p>" );
              f=result.next();
              }
        }  catch (Exception ee) {System.out.println(ee);}
       
                // Q1 Range of dates
        try {
            bw.write("<html><b><h2><center>" + ticker + " Stock Data</center></h2></b>");
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 1</b></h3>");
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT Max(Day), Min(Day) FROM AdjustedPrices "
	    			+ "WHERE Ticker = " + "'"+ticker+"'");
	    	boolean f = result.next(); 
	    	while (f)
	        {
	          String s = result.getString(1);
	          String a = result.getString(2); 
	          bw.write("<p style=\"padding-left:220px\">Start Date:   " + a + "     Last Day Tracked:   " + s + "</p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 2</b></h3>");
        	Statement s1 = conn.createStatement();
        	ResultSet result = s1.executeQuery("SELECT T1.Year, T1.Volume, T1.VolPerDay, T1.AvgClose, T2.Decision FROM ( SELECT Year(Day) as Year, Volume, IF(Year(Day)%4=0, ROUND(Volume/366,0), ROUND(Volume/365,0)) as VolPerDay, ROUND(AVG(Close),2) as AvgClose FROM AdjustedPrices P WHERE Ticker = "+"'"+ticker+"'"+" GROUP BY Year(Day)) T1 INNER JOIN (SELECT A.Year, CASE WHEN SIGN(A.Avg - B.Avg) = 1 THEN 'increase' WHEN SIGN(A.Avg - B.Avg) = -1 THEN 'decrease' ELSE 'none' END as Decision FROM( SELECT Year(Day) as Year, Avg(Close) as Avg FROM AdjustedPrices P WHERE Ticker = "+"'"+ticker+"'"+" AND Month(Day) = 12 GROUP BY Year(Day) ) A, (SELECT Year(Day) as Year, Avg(Close) as Avg FROM AdjustedPrices P WHERE Ticker = "+"'"+ticker+"'"+" AND Month(Day) = 1 GROUP BY Year(Day)) B WHERE A.Year = B.Year) T2 ON T1.Year = T2.Year");
        	boolean f = result.next();
        	while (f)
            {
              String a = result.getString(1);
              String b = result.getString(2); 
              String c = result.getString(3);
              String d = result.getString(4); 
              String e = result.getString(5); 
              bw.write("<p style=\"padding-left:220px\">Year: " + a + " Volume: " + b + " Volume Per Day: " + c + " Avg Close: " + d + " Change throughout Year: " + e + "</p>");
              f=result.next();
            } 
       	}catch (Exception ee) {System.out.println(ee);}
        
        
        // Q3 show avg close, high, low, avg vol per month
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 3</b></h3>");
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT MonthName(Day) as Month, ROUND(Avg(Close),2) as AvgClose, Max(High) as Highest, Min(Low) as Lowest, ROUND(Avg(Volume),0) as AvgVol FROM AdjustedPrices "
	    			+ "WHERE Ticker = "+"'"+ticker+"'"+ "AND Year(Day) = 2016 GROUP BY Month(Day)");
	    	
	    	boolean f = result.next();
	    	bw.write("<p style=\"padding-left:220px\">For 2016 ");
	    	bw.write("Month:   AvgClose,   High,   Low,   AvgVolume   </p>");
	    	while (f)
	        {
	          String a = result.getString(1);
	          String b = result.getString(2); 
	          String c = result.getString(3);
	          String d = result.getString(4); 
	          String e = result.getString(5); 
	          bw.write("<p style=\"padding-left:220px\"p>" + a + ":   " + b + ",   " + c + ",   " + d + ",   " + e + "</p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        //Query 4
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 4</b></h3>");
        	Statement s1 = conn.createStatement();
        	ResultSet result = s1.executeQuery("SELECT IF(XD2.Avg > XD1.Avg, ROUND(XD2.Avg,2), ROUND(XD1.Avg,2)) as PriceChange, IF(XD2.Avg > XD1.Avg, XD2.MonthName, XD1.MonthName) as Month FROM (SELECT A.Ticker, A.MonthName, A.Avg - B.Avg as Avg FROM (SELECT Ticker, MonthName(Day) as MonthName, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = "+"'" + ticker +"'" + " GROUP BY Month(Day)) A, (SELECT Ticker, MonthName(Day) as MonthName, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2015 AND Ticker = "+"'" + ticker +"'" + " GROUP BY Month(Day)) B WHERE B.Month =12 AND A.Month = 1) XD1, (SELECT A.Ticker, A.MonthName, A.Avg-B.Avg as Avg FROM (SELECT Ticker, MonthName(Day) as MonthName, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = "+"'" + ticker +"'" + " GROUP BY MonthName(Day) ORDER BY Month(Day)) A, (SELECT Ticker, MonthName(Day) as MonthName, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = "+"'" + ticker +"'" + " GROUP BY MonthName(Day) ORDER BY Month(Day)) B WHERE A.Month = B.Month + 1 AND A.Avg-B.Avg >= ALL(SELECT A.Avg - B.Avg FROM (SELECT Ticker, MonthName(Day) as MonthName, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = "+"'" + ticker +"'" + " GROUP BY Month(Day) ORDER BY Month(Day)) A, (SELECT Ticker, MonthName(Day) as MonthName, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = "+"'" + ticker +"'" + " GROUP BY Month(Day) ORDER BY Month(Day)) B WHERE A.Month = B.Month + 1)) XD2 "); 
        	boolean f = result.next();
        	bw.write("<p style=\"padding-left:220px\">Best Performing Month for " + ticker + "</p>");
        	while (f)
            {
              String a = result.getString(1);
              String b = result.getString(2); 
              bw.write("<p style=\"padding-left:220px\"> is " + b + " with Price change: " + a + "</p>");
              f=result.next();
            } 
       	}catch (Exception ee) {System.out.println(ee);} 
          
        /* Method: Quick Flip, For each month of the last year, find the average closing
         * Find the relative percentage change between each month: (JanPrice-DecPrice)/DecPrice*100 to x%
         * Take the average relative percentage change (*) for the year 
         * If * went -5% or decrease 5%, then buy 
         * If * went +5% or increase 5%, then sell 
         * else if in between, hold 
         * */
        //Q5 1/1/15
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 5</b></h3>");
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT CASE WHEN ROUND(SUM(Avg)/COUNT(*),2) < -5 THEN 'Buy' WHEN ROUND(SUM(Avg)/COUNT(*),2) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, ROUND(SUM(Avg)/COUNT(*),2) FROM (SELECT A.Year, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices "
	    			+ "WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-01-01', Day) >= 1 AND DateDiff('2015-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices "
	    			+ "WHERE Ticker = "+"'"+ticker+"'"+"AND DateDiff('2015-01-01', Day) >= 1 AND DateDiff('2015-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1) OK");
	    	
	    	boolean f = result.next();
	    	bw.write("<p style=\"padding-left:220px\">For Jan 1 2015, </p>");
	    	while (f)
	        {
	          String a = result.getString(1);
	          String b = result.getString(2); 
	          bw.write("<p style=\"padding-left:220px\"><b>" + a + "</b>   given an average percent   " + b +"% per year </p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        //Q5 6/1/15
        try {
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT CASE WHEN ROUND(SUM(Avg)/COUNT(*),2) < -5 THEN 'Buy' WHEN ROUND(SUM(Avg)/COUNT(*),2) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, ROUND(SUM(Avg)/COUNT(*),2) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+ " AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A,  (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+ " AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+ " AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+ " AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK");
	    	boolean f = result.next();
	    	bw.write("<p style=\"padding-left:220px\">For June 1 2015, </p>");
	    	while (f)
	        {
	          String a = result.getString(1);
	          String b = result.getString(2); 
	          bw.write("<p style=\"padding-left:220px\"><b>" + a + "</b>   given an average percent   " + b +"% per year </p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        // Q5 10/1/15
        try {
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT CASE WHEN ROUND(SUM(Avg)/COUNT(*),2) < -5 THEN 'Buy' WHEN ROUND(SUM(Avg)/COUNT(*),2) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, ROUND(SUM(Avg)/COUNT(*),2) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker ="+"'"+ticker+"'"+"  AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 "
	    			+ "GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK");
	    	boolean f = result.next();
	    	bw.write("<p style=\"padding-left:220px\">For Oct 1 2015, </p>");
	    	while (f)
	        {
	          String a = result.getString(1);
	          String b = result.getString(2); 
	          bw.write("<p style=\"padding-left:220px\"><b>" + a + "</b>   given an average percent   " + b +"% per year</p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        //Q5 1/1/16
        try {
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT CASE WHEN ROUND(SUM(Avg)/COUNT(*),2) < -5 THEN 'Buy' WHEN ROUND(SUM(Avg)/COUNT(*),2) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, ROUND(SUM(Avg)/COUNT(*),2) FROM (SELECT A.Year, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices "
	    			+ "WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-01-01', Day) >= 1 AND DateDiff('2016-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices "
	    			+ "WHERE Ticker = "+"'"+ticker+"'"+"AND DateDiff('2016-01-01', Day) >= 1 AND DateDiff('2016-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1) OK");
	    	
	    	boolean f = result.next();
	    	bw.write("<p style=\"padding-left:220px\">For Jan 1 2016, </p>");
	    	while (f)
	        {
	          String a = result.getString(1);
	          String b = result.getString(2); 
	          bw.write("<p style=\"padding-left:220px\"><b>" + a + "</b>   given an average percent   " + b +"% per year </p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        // Q5 5/1/16
        try {
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT CASE WHEN ROUND(SUM(Avg)/COUNT(*),2) < -5 THEN 'Buy' WHEN ROUND(SUM(Avg)/COUNT(*),2) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, ROUND(SUM(Avg)/COUNT(*),2) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, SUM((A.Avg - B.Avg)/B.Avg * 100)/COUNT(*) as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK");
	    	boolean f = result.next();
	    	bw.write("<p style=\"padding-left:220px\">For May 1 2016, </p>");
	    	while (f)
	        {
	          String a = result.getString(1);
	          String b = result.getString(2); 
	          bw.write("<p style=\"padding-left:220px\"><b>" + a + "</b>   given an average percent   " + b +"% per year</p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        // Q5 10/1/16
        try {
	    	Statement s1 = conn.createStatement();
	    	ResultSet result = s1.executeQuery("SELECT CASE WHEN ROUND(SUM(Avg)/COUNT(*),2) < -5 THEN 'Buy' WHEN ROUND(SUM(Avg)/COUNT(*),2) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, ROUND(SUM(Avg)/COUNT(*),2) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker =  "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK");
	    	boolean f = result.next();
	    	bw.write("<p style=\"padding-left:220px\">For Oct 1 2016, </p>");
	    	while (f)
	        {
	          String a = result.getString(1);
	          String b = result.getString(2); 
	          bw.write("<p style=\"padding-left:220px\"><b>" + a + "</b>   given an average percent   " + b +"% per year</p>");
	          f=result.next();
	        } 
    	}catch (Exception ee) {System.out.println(ee);}
        
        // Q6 
        /* Method, look 6 months into the future
         * For each month, look at the average close 
         * Add the relative percentage change for each month divided by 12 months
         * Gives average relative percentage change 
         * Prediction said to buy stock when value drop more than 5%, by examining the future data if stock value increased more than 5%, you were correct that the stock was undervalued
         * Prediction said to sell stock when value raised more than 5%, by examining future data if stock value decreased more than 5%, you were correct in indicating the stock is overvalued
         * else you keep holding, because price is stable and you'll receive your dividends 
         * */
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 6</b></h3>");
         	Statement s1 = conn.createStatement();
         	ResultSet result = s1.executeQuery("SELECT IF(DecA.Decision = DecB.Decision, 'correct', 'wrong') as Prediction, ROUND(DecA.Avg,2) FROM (SELECT CASE WHEN SUM(ROUND((A.Avg-B.Avg)/B.Avg * 100,2))/COUNT(*) < -5 THEN 'Sell' WHEN SUM(ROUND((A.Avg-B.Avg)/B.Avg * 100,2))/COUNT(*) > 5 THEN 'Buy' ELSE 'Hold' END as Decision, SUM(ROUND((A.Avg-B.Avg)/B.Avg * 100,2))/COUNT(*) AS Avg FROM (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-01-01', INTERVAL 6 Month) AND Day >= '2015-01-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-01-01', INTERVAL 6 Month) AND Day >= '2015-01-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month + 1) DecA, (SELECT CASE WHEN SUM((A.Avg - B.Avg)/B.Avg * 100)/COUNT(*) < -5 THEN 'Buy' WHEN SUM((A.Avg - B.Avg)/B.Avg * 100)/COUNT(*) > 5 THEN 'Sell' ELSE 'Hold' END as Decision, SUM((A.Avg - B.Avg)/B.Avg)/COUNT(*) * 100 FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-01-01', Day) >= 1 AND DateDiff('2015-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A,  (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-01-01', Day) >= 1 AND DateDiff('2015-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1) DecB");
         	boolean f = result.next();
         	bw.write("<p style=\"padding-left:220px\">For Jan 1 2015, </p>");
         	while (f)
             {
               String a = result.getString(1);
               String b = result.getString(2); 
               bw.write("<p style=\"padding-left:220px\">My prediction is <b>" + a + "</b> with future average percent   " + b +"%</p>");
               f=result.next();
             } 
        	}catch (Exception ee) {System.out.println(ee);}
        
        try {
          	Statement s1 = conn.createStatement();
          	ResultSet result = s1.executeQuery("SELECT IF(DecA.Decision = DecB.Decision, 'correct', 'wrong') as Prediction, ROUND(DecA.Avg,2) FROM (SELECT CASE WHEN ROUND(SUM((A.Avg - B.Avg)/B.Avg*100),2)/COUNT(*) < -5 THEN 'Sell' WHEN ROUND(SUM((A.Avg - B.Avg)/B.Avg*100),2)/COUNT(*) > 5 THEN 'Buy' ELSE 'Hold' END AS Decision, SUM((A.Avg - B.Avg)/B.Avg)/COUNT(*) * 100 AS Avg FROM (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-06-01', INTERVAL 6 Month) AND Day >= '2015-06-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-06-01', INTERVAL 6 Month) AND Day >= '2015-06-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month + 1) DecA, (SELECT CASE WHEN SUM(Avg)/COUNT(*) < -5 THEN 'Buy' WHEN SUM(Avg)/COUNT(*) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, SUM(Avg)/COUNT(*) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-06-01', Day) >= 1 AND DateDiff('2015-06-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK) DecB");
          	boolean f = result.next();
          	bw.write("<p style=\"padding-left:220px\">For June 1 2015, </p>");
          	while (f)
              {
                String a = result.getString(1);
                String b = result.getString(2); 
                bw.write("<p style=\"padding-left:220px\">My prediction is <b>" + a + "</b> with future average percent   " + b +"%</p>");
                f=result.next();
              } 
         	}catch (Exception ee) {System.out.println(ee);}
        try {
           	Statement s1 = conn.createStatement();
           	ResultSet result = s1.executeQuery("SELECT IF(DecA.Decision = DecB.Decision, 'correct', 'wrong') as Prediction, DecA.Avg FROM (SELECT CASE WHEN SUM(Avg)/COUNT(*) < -5 THEN 'Sell' WHEN SUM(Avg)/COUNT(*) > 5 THEN 'Buy' ELSE 'Hold' END as Decision, ROUND(SUM(Avg)/COUNT(*),2) as Avg FROM( SELECT (A.Avg-B.Avg)/B.Avg*100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-10-01', INTERVAL 6 Month) AND Day >= '2015-10-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-10-01', INTERVAL 6 Month) AND Day >= '2015-10-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month + 1 UNION SELECT (A.Avg-B.Avg)/B.Avg*100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-10-01', INTERVAL 6 Month) AND Day >= '2015-10-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2015-10-01', INTERVAL 6 Month) AND Day >= '2015-10-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12) IDK) DecA, (SELECT CASE WHEN SUM(Avg)/COUNT(*) < -5 THEN 'Buy' WHEN SUM(Avg)/COUNT(*) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, SUM(Avg)/COUNT(*) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2015-10-01', Day) >= 1 AND DateDiff('2015-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK) DecB");
           	boolean f = result.next();
           	bw.write("<p style=\"padding-left:220px\">For Oct 1 2015, </p>");
           	while (f)
               {
                 String a = result.getString(1);
                 String b = result.getString(2); 
                 bw.write("<p style=\"padding-left:220px\">My prediction is <b>" + a + "</b> with future average percent   " + b +"%</p>");
                 f=result.next();
               } 
          	}catch (Exception ee) {System.out.println(ee);}
        
        try {
          	Statement s1 = conn.createStatement();
          	ResultSet result = s1.executeQuery("SELECT IF(DecA.Decision = DecB.Decision, 'correct', 'wrong') as Prediction, ROUND(DecA.Avg,2) FROM (SELECT CASE WHEN SUM(ROUND((A.Avg-B.Avg)/B.Avg * 100,2))/COUNT(*) < -5 THEN 'Sell' WHEN SUM(ROUND((A.Avg-B.Avg)/B.Avg * 100,2))/COUNT(*) > 5 THEN 'Buy' ELSE 'Hold' END as Decision, SUM(ROUND((A.Avg-B.Avg)/B.Avg * 100,2))/COUNT(*) AS Avg FROM (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-01-01', INTERVAL 6 Month) AND Day >= '2016-01-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-01-01', INTERVAL 6 Month) AND Day >= '2016-01-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month + 1) DecA, (SELECT CASE WHEN SUM((A.Avg - B.Avg)/B.Avg * 100)/COUNT(*) < -5 THEN 'Buy' WHEN SUM((A.Avg - B.Avg)/B.Avg * 100)/COUNT(*) > 5 THEN 'Sell' ELSE 'Hold' END as Decision, SUM((A.Avg - B.Avg)/B.Avg)/COUNT(*) * 100 FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-01-01', Day) >= 1 AND DateDiff('2016-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A,  (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-01-01', Day) >= 1 AND DateDiff('2016-01-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1) DecB");
          	boolean f = result.next();
          	bw.write("<p style=\"padding-left:220px\">For Jan 1 2016, </p>");
          	while (f)
              {
                String a = result.getString(1);
                String b = result.getString(2); 
                bw.write("<p style=\"padding-left:220px\">My prediction is <b>" + a + "</b> with future average percent   " + b +"%</p>");
                f=result.next();
              } 
         	}catch (Exception ee) {System.out.println(ee);}
        try {
           	Statement s1 = conn.createStatement();
           	ResultSet result = s1.executeQuery("SELECT IF(DecA.Decision = DecB.Decision, 'correct', 'wrong') as Prediction, ROUND(DecA.Avg,2) FROM (SELECT CASE WHEN ROUND(SUM((A.Avg - B.Avg)/B.Avg*100),2)/COUNT(*) < -5 THEN 'Sell' WHEN ROUND(SUM((A.Avg - B.Avg)/B.Avg*100),2)/COUNT(*) > 5 THEN 'Buy' ELSE 'Hold' END AS Decision, SUM((A.Avg - B.Avg)/B.Avg)/COUNT(*) * 100 AS Avg FROM (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-05-01', INTERVAL 6 Month) AND Day >= '2016-05-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day), Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-05-01', INTERVAL 6 Month) AND Day >= '2016-05-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month + 1) DecA, (SELECT CASE WHEN SUM(Avg)/COUNT(*) < -5 THEN 'Buy' WHEN SUM(Avg)/COUNT(*) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, SUM(Avg)/COUNT(*) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-05-01', Day) >= 1 AND DateDiff('2016-05-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK) DecB");
           	boolean f = result.next();
           	bw.write("<p style=\"padding-left:220px\">For May 1 2016, </p>");
           	while (f)
               {
                 String a = result.getString(1);
                 String b = result.getString(2); 
                 bw.write("<p style=\"padding-left:220px\">My prediction is <b>" + a + "</b> with future average percent   " + b +"%</p>");
                 f=result.next();
               } 
          	}catch (Exception ee) {System.out.println(ee);}
        
        try {
           	Statement s1 = conn.createStatement();
           	ResultSet result = s1.executeQuery("SELECT IF(DecA.Decision = DecB.Decision, 'correct', 'wrong') as Prediction, DecA.Avg FROM (SELECT CASE WHEN SUM(Avg)/COUNT(*) < -5 THEN 'Sell' WHEN SUM(Avg)/COUNT(*) > 5 THEN 'Buy' ELSE 'Hold' END as Decision, ROUND(SUM(Avg)/COUNT(*),2) as Avg FROM( SELECT (A.Avg-B.Avg)/B.Avg*100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-10-01', INTERVAL 6 Month) AND Day >= '2016-10-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-10-01', INTERVAL 6 Month) AND Day >= '2016-10-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month + 1 UNION SELECT (A.Avg-B.Avg)/B.Avg*100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-10-01', INTERVAL 6 Month) AND Day >= '2016-10-01' GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND Day <= ADDDATE('2016-10-01', INTERVAL 6 Month) AND Day >= '2016-10-01' GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12) IDK) DecA, (SELECT CASE WHEN SUM(Avg)/COUNT(*) < -5 THEN 'Buy' WHEN SUM(Avg)/COUNT(*) > 5 THEN 'Sell' ELSE 'Hold' END AS Decision, SUM(Avg)/COUNT(*) FROM (SELECT A.Year as YearA, B.Year as YearB, A.Month as MonthA, B.Month as MonthB, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = B.Month+1 AND A.Year = B.Year UNION SELECT A.Year as YearA, B.Year as YearB, A.Month, B.Month, (A.Avg - B.Avg)/B.Avg * 100 as Avg FROM (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) A, (SELECT Year(Day) as Year, Month(Day) as Month, ROUND(AVG(Close),2) as Avg FROM AdjustedPrices WHERE Ticker = "+"'"+ticker+"'"+" AND DateDiff('2016-10-01', Day) >= 1 AND DateDiff('2016-10-01', Day) <= 365 GROUP BY Year(Day), Month(Day)) B WHERE A.Month = 1 AND B.Month = 12 AND A.Year != B.Year ORDER BY YearA, YearB) OK) DecB");
           	boolean f = result.next();
           	bw.write("<p style=\"padding-left:220px\">For Oct 1 2016, </p>");
           	while (f)
               {
                 String a = result.getString(1);
                 String b = result.getString(2); 
                 bw.write("<p style=\"padding-left:220px\">My prediction is <b>" + a + "</b> with future average percent   " + b +"%</p>");
                 f=result.next();
               } 
          	}catch (Exception ee) {System.out.println(ee);}
        //Query 8
        
        try {
        	bw.write("<h3 style=\"padding-left:220px\"><b>Query 8</b></h3>");
            bw.write("<p style=\"padding-left:220px\">Compare between our tickers with the first being greatest growth</p>");
           	Statement s1 = conn.createStatement();
           	ResultSet result = s1.executeQuery("SELECT A.Ticker, ROUND(SUM((A.Avg - B.Avg)/B.Avg * 100),2)/COUNT(*)as Avg FROM (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = "+"'"+ticker+"'"+ "GROUP BY Month(Day)) A, (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = "+"'"+ticker+"'"+" GROUP BY Month(Day)) B WHERE A.Month = B.Month + 1 UNION SELECT A.Ticker, ROUND(SUM((A.Avg - B.Avg)/B.Avg * 100),2)/COUNT(*) as Avg FROM (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = 'ROK' GROUP BY Month(Day)) A, (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = 'ROK' GROUP BY Month(Day)) B WHERE A.Month = B.Month + 1 UNION SELECT A.Ticker, ROUND(SUM((A.Avg - B.Avg)/B.Avg * 100),2)/COUNT(*) as Avg FROM (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = 'XOM' GROUP BY Month(Day)) A, (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = 'XOM' GROUP BY Month(Day)) B WHERE A.Month = B.Month + 1 UNION SELECT A.Ticker, ROUND(SUM((A.Avg - B.Avg)/B.Avg * 100),2)/COUNT(*) as Avg FROM (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = 'SCHW' GROUP BY Month(Day)) A, (SELECT Ticker, Month(Day) as Month, Avg(Close) as Avg FROM AdjustedPrices WHERE Year(Day) = 2016 AND Ticker = 'SCHW' GROUP BY Month(Day) ) B WHERE A.Month = B.Month + 1 ORDER BY Avg DESC"); 
           	boolean f = result.next();
           	while (f)
               {
                 String a = result.getString(1);
                 String b = result.getString(2); 
                 bw.write("<p style=\"padding-left:220px\">Stock: " + a + " with average percent change " + b +"%</p>");
                 f=result.next();
               } 
          	}catch (Exception ee) {System.out.println(ee);}
        try {
            conn.close();
        }
        catch (Exception ex)
        {
            System.out.println("Unable to close connection");
        };
        
        try{
            bw.flush();
            fw.flush();
            bw.close();
            fw.close();
          }catch(IOException e) {e.printStackTrace();}  
	}
}
