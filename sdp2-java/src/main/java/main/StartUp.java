package main;

import domain.PopulateDB;

public class StartUp {

    public static void main(String[] args) {
        // GebruikerController dc = new GebruikerController();
        //
        // System.out.println(dc.getGebruiker(1).toString());
        //
        // System.out.println(dc.getAll().toString());
        //
        // System.out.println(dc.getGebruiker("geralt@gmail.com"));

        PopulateDB populator = new PopulateDB();
        populator.run();

        StartUpGui.start(args);

    }

}
