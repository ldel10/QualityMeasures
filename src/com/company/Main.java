package com.company;


import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String url = "https://2swdepm0wa.execute-api.us-east-1.amazonaws.com/prod/NavaInterview/measures";
        QualityMeasuresController qualityMeasuresController = new QualityMeasuresController(url);

        File schemaFolder = new File("schemas/");
        File dataFolder = new File("data/");

        File[] schemas = schemaFolder.listFiles();
        File[] dataFiles = dataFolder.listFiles();
        for(int i = 0; i < schemas.length; i++){

            //for each schema file, loop through data files and find matching file name
            String schemaName = schemas[i].getName();
            String schemaPrefix = schemaName.replaceFirst("[.][^.]+$", "");

            for(int j = 0; j < dataFiles.length; j++){
                String dataName = dataFiles[j].getName();
                String dataPrefix = dataName.replaceFirst("[.][^.]+$", "");
                if(schemaPrefix.equals(dataPrefix)){

                    //generate all request bodies
                    List<String> requestParams = qualityMeasuresController.ConstructRequestBody(schemas[i], dataFiles[j]);

                    //Make requests to external system with constructed parameters
                    for(String param : requestParams){
                        qualityMeasuresController.SendPostRequest(param);
                    }
                }
            }
        }

    }

}
