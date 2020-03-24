package com.neawudroel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

public class Controller {

    //General stuff
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("Neawudroel");
    public Pane paneMainContent;
    public BorderPane bpBorderPane;
    private EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

    //This creates a query and takes down an entire ResultList into a normal local List
    //private List<person> personList = em.createQuery("SELECT a FROM person AS a").getResultList();

    //Init (run with Controller)
    public void initialize(){
    }

    /*
    public void addObjectToDB(){
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try{
            et = em.getTransaction();
            et.begin();
            em.persist( INSERT OBJECT YOU WANT TO PUSH TO DATABASE HERE );
            et.commit();
        }catch (Exception ex){
            if(et != null){
                et.rollback();
            }

            ex.printStackTrace();
        }finally {
            em.close();
        }
    }
    */


    public void miActor() throws IOException {
        Parent child = FXMLLoader.load(getClass().getResource("/sakila/Actor.fxml"));
        bpBorderPane.setCenter(child);
    }
}
