package com.company;
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;

public class QualityMeasuresController {

    private String url;

    public QualityMeasuresController(String url){
        this.url = url;
    }

    public List<String> ConstructRequestBody(File schema, File data){

        HashMap<String, String[]> map = new HashMap<>();
        List<String> keys = new ArrayList<>();
        List<String> result = new ArrayList<>();

        try{

            //First, scan over schema file, storing each line as a map entry with Name as the key, and array of data as value
            //This will allow easy access to schema while iterating over data file
            Scanner schemaScanner = new Scanner(schema);
            while(schemaScanner.hasNextLine()){
                String column = schemaScanner.nextLine();
                String[] values = column.split(",");
                keys.add(values[0]);            //keep this list to ensure we are iterating over keys in proper order
                map.put(values[0], new String[]{values[1], values[2]});
            }

            //Scan over each line of data file, creating a new json object for each log record, representing the json body we are POSTin
            Scanner dataScanner = new Scanner(data);
            while(dataScanner.hasNextLine()){
                JSONObject body = new JSONObject();
                int index = 0;
                String row = dataScanner.nextLine();

                Iterator it = keys.iterator();
                while(it.hasNext()) {

                    String key = it.next().toString();      //get name
                    String[] keyData = map.get(key);        //Get values for width and type
                    int characters = Integer.parseInt(keyData[0]); //get number of characters to parse
                    String value = row.substring(index, index + characters); //leverage the width value so we know how many characters to take from log record
                    index += characters;

                    if(keyData[1].equals("BOOLEAN")){
                        if(value.equals("0")) value = "false";
                        if(value.equals("1")) value = "true";
                    }

                    //add data to json Object
                    body.put(key,value);
                }
                System.out.println(body.toString());
                result.add(body.toString());
            }


        }
        catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
        }


        return result;
    }

    public void SendPostRequest(String params){

        try{

            URL object = new URL(url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");

            //write json object to body of post request
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(params);

            //read response from url connection and print
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
            in.close();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

}
