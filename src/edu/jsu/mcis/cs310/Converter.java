/*
 * Author: Malek 
 * CS 310
 * Project 1 
 */

package edu.jsu.mcis.cs310;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import com.github.cliftonlabs.json_simple.JsonObject;

import com.opencsv.CSVWriter;
public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
   
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
 // Create a CSV reader
 CSVReader reader = new CSVReader(new StringReader(csvString));
        
 // Read the header row
 String[] header = reader.readNext();
 
 // Create a JSON object to store the JSON data
 JsonObject json = new JsonObject();
 
 // Add the header row to the JSON object
 json.put("ColHeadings", header);
 
 // Create a list to store the production numbers (ProdNum)
 JsonArray prodNums = new JsonArray();
 
 // Create a list to store the data rows
 JsonArray data = new JsonArray();
 
 // Read the rest of the data rows
 String[] row;
 while ((row = reader.readNext()) != null) {
     // Add the production number to the list
     prodNums.add(row[0]);
     
     // Create a list to store the values of the current data row
     JsonArray values = new JsonArray();
     
     // Add the values of the current data row to the list
     for (int i = 1; i < header.length; i++) {
         try {
             int value = Integer.parseInt(row[i]);
             values.add(value);
         } catch (NumberFormatException e) {
             values.add(row[i]);
         }
     }
     
     // Add the list of values to the data list
     data.add(values);
 }
 
 // Add the ProdNums and Data lists to the JSON object
 json.put("ProdNums", prodNums);
 json.put("Data", data);
 
 // Convert the JSON object to a string
 result = json.toJson();            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
   // --------- JSON to CVS 
   public static String jsonToCsv(String jsonString) {
        
    String result = ""; // default return value; initialize to an empty string 
            // format for decimal values
          DecimalFormat decimalFormat = new DecimalFormat("00"); 
    try {
        
         JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());
         // deserialize the input JSON string into a JsonObject
        
        JsonArray colheadings = new JsonArray();
        colheadings=(JsonArray) (jsonObject.get("ColHeadings"));
        // get the column headings from the JSON object
        
        JsonArray ProdNumbers = new JsonArray();
        ProdNumbers=(JsonArray) (jsonObject.get("ProdNums"));
        // get the product numbers from the JSON object
        
        JsonArray bigdata = new JsonArray();
        bigdata=(JsonArray) (jsonObject.get("Data"));
        // get the data from the JSON object
        
        StringWriter stringWriter = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(stringWriter, ',', '"', '\\', "\n")) {
            String[] headings = new String[colheadings.size()];
            for (int i = 0; i < colheadings.size(); i++) {
                headings[i] = colheadings.get(i).toString();
            }
            csvWriter.writeNext(headings);
            // write the column headings to the CSV file
            
            for(int i=0;i<ProdNumbers.size();i++){
                String[] row= new  String[colheadings.size()];
                JsonArray data = new JsonArray(); 
                data=(JsonArray) bigdata.get(i);
                
                row[0]=ProdNumbers.get(i).toString();
                // add the product number to the first column of the row
                for (int j = 0; j < data.size(); j++) {
                    
                    if(data.get(j)==data.get(colheadings.indexOf("Episode")-1)){
                        // check if the current data value corresponds to the "Episode" column
                        
                        int number = Integer.parseInt(data.get(j).toString());
                        String formattedNumber = "";
                        
                        formattedNumber = decimalFormat.format(number);
                        // format the episode number to the specified format
                        
                        row[j+1]=formattedNumber;
                    }
                    else{
                        row[j+1] = data.get(j).toString();
                    }
                    
                }
                
                csvWriter.writeNext(row);
                // write the row to the CSV file
                
            }
        }

        result = stringWriter.toString();
        // convert the StringWriter to a string and store it in the `result` variable
        
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    
    return result.trim();
    // return the result with leading and trailing whitespaces trimmed
    
    
}


}