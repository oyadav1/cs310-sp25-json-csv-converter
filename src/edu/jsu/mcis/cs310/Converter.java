package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

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
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        String result = "{}"; // Default return value

        try {
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();
            reader.close();

            if (full.isEmpty()) return result;

            Iterator<String[]> iterator = full.iterator();

            JsonArray colHeadings = new JsonArray();
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            if (iterator.hasNext()) {
                String[] headers = iterator.next();
                colHeadings.addAll(Arrays.asList(headers));

                while (iterator.hasNext()) {
                    String[] row = iterator.next();

                    prodNums.add(row[0]);

                    JsonArray rowData = new JsonArray();
                    for (int i = 1; i < row.length; i++) {
                        if (i == 2 || i == 3) { 
                          
                            rowData.add(Integer.parseInt(row[i]));
                        } else {
                            rowData.add(row[i]); // Treat everything else as string
                        }
                    }
                    data.add(rowData);
                }
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.put("ProdNums", prodNums);
            jsonObject.put("ColHeadings", colHeadings);
            jsonObject.put("Data", data);

            result = Jsoner.serialize(jsonObject);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();
    }

    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        String result = ""; // Default return value

        try {
            // Parse JSON
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonString);

            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray data = (JsonArray) jsonObject.get("Data");

            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            // Write headers
            String[] headers = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                headers[i] = colHeadings.get(i).toString();
            }
            csvWriter.writeNext(headers);

            // Write data rows
            for (int i = 0; i < data.size(); i++) {
                JsonArray row = (JsonArray) data.get(i);
                String[] csvRow = new String[row.size() + 1];

                csvRow[0] = prodNums.get(i).toString(); // First column (ProdNum as string)
                for (int j = 0; j < row.size(); j++) {
                    if (j == 1) {
                     
                        csvRow[j + 1] = row.get(j).toString();
                    } 
                    else if (j == 2) { 
                        
                        int episode = ((Number) row.get(j)).intValue();
                        csvRow[j + 1] = String.format("%02d", episode); 
                    } 
                    else {
                        csvRow[j + 1] = row.get(j).toString(); // Everything else as string
                    }
                }
                csvWriter.writeNext(csvRow);
            }

            csvWriter.close();
            result = writer.toString();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();
    }

}
    
